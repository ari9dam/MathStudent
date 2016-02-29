/**AssociatedWordFinder.java
 * 7:16:21 PM @author Arindam
 */
package nlp.app.math.app;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.asu.nlu.common.ds.AnnotatedSentence;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.trees.GrammaticalRelation;

/**
 * @author Arindam
 *
 */
public class AssociatedWordFinder {

	private boolean debug;

	public AssociatedWordFinder(boolean debug){
		this.debug = debug;
	}

	Set<Integer> findAssociatedWord(List<Integer> labels, AnnotatedSentence sen, String pos){
		Set<Integer> ret = new HashSet<Integer>();
		SemanticGraph g = sen.getDependencyGraph();

		int minIndex = 10000;
		for(Integer i : labels){
			ArrayList<IndexedWord> nbrs = new ArrayList<IndexedWord>();
			try{
				nbrs.addAll(g.getChildList(g.getNodeByIndex(i)));
				nbrs.addAll(g.getParentList(g.getNodeByIndex(i)));
				
				for(IndexedWord word: nbrs){
					if(sen.getPOS(word.index()).toLowerCase().startsWith(pos)){
						if(!pos.startsWith("vb") || !g.getChildrenWithReln(g.getNodeByIndex(i), 
								GrammaticalRelation.valueOf("advcl")).contains(word))
							ret.add(word.index());
					}
				}
			}catch(Exception e){

			}
			
			if(i<minIndex)
				minIndex = i;

		}
		if(!pos.startsWith("vb")||ret.size()==0)
			for(int  i=minIndex;i>0;i--){
				if(sen.getPOS(i).toLowerCase().startsWith(pos)){
					ret.add(i);
					break;
				}
			}

		return ret;
	}

	Set<Integer> findAssociatedWordForQuestion(List<Integer> labels, AnnotatedSentence sen, String pos){
		Set<Integer> ret = new HashSet<Integer>();
		SemanticGraph g = sen.getDependencyGraph();

		int minIndex = 10000;
		for(Integer i : labels){
			ArrayList<IndexedWord> nbrs = new ArrayList<IndexedWord>();
			try{
				nbrs.addAll(g.getChildList(g.getNodeByIndex(i)));
				nbrs.addAll(g.getParentList(g.getNodeByIndex(i)));
			}catch(Exception e){

			}
			if(i<minIndex)
				minIndex = i;
			for(IndexedWord word: nbrs){
				if(sen.getPOS(word.index()).toLowerCase().startsWith(pos)){
					ret.add(word.index());
				}
			}

		}
		if(!pos.startsWith("vb")||ret.size()==0)
		for(int  i=minIndex;i<sen.getTokenSequence().size();i++){
			if(sen.getPOS(i).toLowerCase().startsWith(pos)){
				ret.add(i);
				break;
			}
		}

		return ret;
	}

	/**
	 * @param verb
	 * @param typeIds
	 * @param s
	 * @param string
	 * @return
	 */
	public Set<Integer> findAssociatedWordWithRel(Set<Integer> verb, ArrayList<Integer> typeIds, AnnotatedSentence s,
			String rel) {
		Set<Integer> ret = new HashSet<Integer>();
		Set<IndexedWord> nn = new HashSet<IndexedWord>();

		for(Integer v : verb){
			nn = s.getDependencyGraph().getChildrenWithReln(s.getDependencyGraph().getNodeByIndex(v), 
					GrammaticalRelation.valueOf(rel));
		}

		if(nn.size()==0){
			for(Integer v : typeIds){
				try{
					nn.addAll(s.getDependencyGraph().getChildrenWithReln(s.getDependencyGraph().getNodeByIndex(v), 
							GrammaticalRelation.valueOf(rel)));
				}catch(Exception e){

				}
			}
		}

		for(IndexedWord index: nn){
			ret.add(index.index());
		}
		if(rel.equalsIgnoreCase("nsubj")){
			//to hadnle the case where "is" "are" is the verb
			ret.removeAll(typeIds);
			ret.removeAll(verb);
		}
		return ret;
	}
}
