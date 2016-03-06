/**StructureTagger.java
 * 12:00:57 PM @author Arindam
 */
package nlp.app.math.preprocessing;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import edu.asu.nlu.common.ds.AnnotatedSentence;
import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefChain.CorefMention;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import nlp.app.math.core.ProblemRepresentation;
import nlp.app.math.util.NlpPipeline;

/**
 * Run Various Syntactic Taggers and Parsers on the sentences
 * @author Arindam
 *
 */
public class StructureTagger {
	private StanfordCoreNLP pipeline;

	public StructureTagger(boolean isStanfordOnly){
		this.pipeline = NlpPipeline.getPipeline(isStanfordOnly);
	}

	/**
	 * Does
	 *  1. Tokenization
	 *  2. Sentence Boundary Detection
	 *  3. Lemmatization
	 *  4. Part-Of-Speech tagging
	 *  5. PCFG parsing
	 *  6. Dependency parsing
	 *  7. Co-reference resolution
	 */
	public void process(ProblemRepresentation irep){

		String text = irep.getText();

		// create an empty Annotation just with the given text
		Annotation document = new Annotation(text);

		// run all Annotators on this text
		pipeline.annotate(document);

		// these are all the sentences in this document
		// a CoreMap is essentially a Map that uses class objects as keys 
		//and has values with custom types
		List<CoreMap> sens = document.get(SentencesAnnotation.class);

		ArrayList<AnnotatedSentence> sentences = new ArrayList<AnnotatedSentence>();
		for(CoreMap sentence: sens){
			sentences.add(new AnnotatedSentence(sentence.get(TextAnnotation.class),sentence));
		}

		/**
		 * Add co-reference
		 */
		Map<Integer, CorefChain> corefChain = document.get(CorefChainAnnotation.class);
		if(corefChain!=null){
			List<Integer> source = new LinkedList<Integer>();
			List<Integer> target = new LinkedList<Integer>();
			List<CorefMention> except = new LinkedList<CorefMention>();
			
			//manual rule to correct: "His [a-z]*? [a-z]*? him"
			for(Entry<Integer, CorefChain> chain:corefChain.entrySet() ){
				boolean matched = false;
				int corefChainId = -1;
				for(CorefMention elem: chain.getValue().getMentionsInTextualOrder()){
					if(elem.mentionSpan.matches("[hH]is [a-z]*?")){
						int sentence = elem.position.get(0);
						int token = elem.endIndex-1;

						AnnotatedSentence sen = sentences.get(sentence-1);

						if(token+3< sen.getTokenSequence().size()&& 
								token>0){
							String sub = "";
							for(int i = token-1;i<token+3;i++){
								sub += sen.getWord(i)+" ";
							}
							if(sub.matches("[hH]is [a-z]*? [a-z]*? him ")){
								matched = true;

								// find cluster ID
								for(Entry<Integer, CorefChain> chain1:corefChain.entrySet() ){
									for(CorefMention elem1: 
										chain1.getValue().getMentionsInTextualOrder()){
										if(elem1.position.get(0)==sentence &&
												(elem1.endIndex)==token){
											corefChainId = elem1.corefClusterID;
											break;
										}
									}
									if(corefChainId!=-1)
										break;
								}
							}
							
							if(matched && corefChainId != -1){
								source.add(elem.corefClusterID);
								target.add(corefChainId);
								except.add(elem);
							}
						}
					}
				}
			}
			
			for(int i=0;i<source.size();i++){
				List<CorefMention> remove = new LinkedList<CorefMention>();
				List<CorefMention> s = corefChain.get(source.get(i)).getMentionsInTextualOrder();
				List<CorefMention> t = corefChain.get(target.get(i)).getMentionsInTextualOrder();
				for(CorefMention elem: s){
					if(!elem.equals(except.get(i))){
						t.add(elem);
						remove.add(elem);
					}
				}
				s.removeAll(remove);
				remove.clear();		
			}
			
			
			for(Entry<Integer, CorefChain> chain:corefChain.entrySet() ){
				for(CorefMention elem1: chain.getValue().getMentionsInTextualOrder()){
					for(CorefMention elem2: chain.getValue().getMentionsInTextualOrder()){
						irep.addCoref(elem1.position.get(0),elem1.endIndex-1,elem2.position.get(0),elem2.endIndex-1);
					}
				}
			}
		}

		irep.setAnnotatedSentences(sentences);
	}
}
