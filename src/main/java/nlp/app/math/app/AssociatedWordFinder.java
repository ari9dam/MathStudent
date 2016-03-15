/**AssociatedWordFinder.java
 * 7:16:21 PM @author Arindam
 */
package nlp.app.math.app;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import edu.asu.nlu.common.ds.AnnotatedSentence;
import edu.asu.nlu.common.util.POSUtil;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.trees.GrammaticalRelation;

/**
 * @author Arindam
 *
 */
public class AssociatedWordFinder {

	@SuppressWarnings("unused")
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
							if(pos.startsWith("vb")){
								Set<IndexedWord> x = g.getParentsWithReln(word, 
										GrammaticalRelation.valueOf("xcomp"));
								ArrayList<IndexedWord> xcomp = new ArrayList<IndexedWord>(x);
								if(xcomp.size()==1&& 
										POSUtil.isVerb(sen.getPOS(xcomp.get(0).index()))
										&& !POSUtil.isAux(sen.getLemma(xcomp.get(0).index()))){
									ret.add(xcomp.get(0).index());
								}else{
									ret.add(word.index());
								}
							}else{
								ret.add(word.index());
							}
					}
				}
			}catch(Exception e){
				e.printStackTrace();
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
		
		if(pos.equalsIgnoreCase("vb")&&ret.size()==1){
			int rem = -1;
			for(Integer id: ret){
				if(sen.getLemma(id).equalsIgnoreCase("do")){
					boolean changed = false;
					Set<IndexedWord> p = g.getParentsWithReln(g.getNodeByIndex(id), 
							GrammaticalRelation.valueOf("aux"));
					for(IndexedWord ind: p){
						if(POSUtil.isVerb(sen.getPOS(ind.index()))){
							ret.add(ind.index());
							changed = true;
							rem = id;
						}
					}
					
					if(!changed){
						p = g.getChildrenWithReln(g.getNodeByIndex(id), 
								GrammaticalRelation.valueOf("ccomp"));
						for(IndexedWord ind: p){
							if(POSUtil.isVerb(sen.getPOS(ind.index()))){
								ret.add(ind.index());
								rem = id;
							}
						}
					}
				}
			}
			
			ret.remove(rem);
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
		Set<IndexedWord> ccomp = new HashSet<IndexedWord>();
		for(Integer v : verb){
			nn = s.getDependencyGraph().getChildrenWithReln(s.getDependencyGraph().getNodeByIndex(v), 
					GrammaticalRelation.valueOf(rel));
		}
		boolean hasVbrel = !nn.isEmpty();
		for(Integer v : typeIds){
			try{
				Set<IndexedWord> temp = s.getDependencyGraph().getChildrenWithReln(s.getDependencyGraph().getNodeByIndex(v), 
						GrammaticalRelation.valueOf(rel));
				if(hasVbrel){
					for(IndexedWord word:temp){
						if(s.getLemma(word.index()).equalsIgnoreCase("all")||
								s.getLemma(word.index()).equalsIgnoreCase("total"))
							nn.add(word);
					}
				}else{
					nn.addAll(temp);
				}
					
			}catch(Exception e){

			}
		}

		for(IndexedWord index: nn){
			ret.add(index.index());
		}
		if(rel.equalsIgnoreCase("nsubj")){
			//to hadnle the case where "is" "are" is the verb
			ret.removeAll(typeIds);
			ret.removeAll(verb);

			if(ret.isEmpty()){
				for(Integer v : verb){
					ccomp = s.getDependencyGraph().getChildrenWithReln(s.getDependencyGraph().getNodeByIndex(v), 
							GrammaticalRelation.valueOf("ccomp"));
				}
				for(IndexedWord c: ccomp){
					nn.addAll(s.getDependencyGraph().getChildrenWithReln(c, 
							GrammaticalRelation.valueOf("nsubj")));
				}
				

				for(IndexedWord index: nn){
					if(POSUtil.isNoun(s.getPOS(index.index())))
						ret.add(index.index());
				}
				
				ret.removeAll(typeIds);
				ret.removeAll(verb);

			}
			
			Iterator<Integer> it = ret.iterator();
			while(it.hasNext()){
				String pos = s.getPOS(it.next()).toLowerCase();
				if(pos.startsWith("rb")||pos.startsWith("jj"))
					it.remove();
			}
		}
		return ret;
	}
}
