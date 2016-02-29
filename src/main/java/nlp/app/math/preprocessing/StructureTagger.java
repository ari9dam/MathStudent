/**StructureTagger.java
 * 12:00:57 PM @author Arindam
 */
package nlp.app.math.preprocessing;

import java.util.ArrayList;
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
