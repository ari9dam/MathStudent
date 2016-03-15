/**TypeDetecter.java
 * 5:29:54 PM @author Arindam
 */
package nlp.app.math.util;

import java.util.ArrayList;

import edu.asu.nlu.common.ds.AnnotatedSentence;
import edu.asu.nlu.common.util.POSUtil;
import edu.stanford.nlp.ling.CoreLabel;

/**
 * @author Arindam
 *
 */
public class TypeDetecter {
	private WordNetHelper wnh = WordNetHelper.getInstance();
	public ArrayList<CoreLabel> findType(CoreLabel token, AnnotatedSentence s, 
			ArrayList<CoreLabel> prevType ) throws RuntimeException{
		/**
		 * Simple deterministic algorithm	
		 */
		boolean stop = false;
		boolean ccFound = false;
		boolean hasNp = false;
		boolean lookForAdjective = false;
		boolean changedAdj = false;
		ArrayList<CoreLabel> type = new ArrayList<CoreLabel>();
		try{
			String prev = "";
			int tId = token.index()+1;
			while(!stop && s.hasToken(tId)){
				String pos = s.getPOS(tId).toLowerCase();

				if(POSUtil.isNoun(pos)){
					if(!ccFound){
						if(!hasNp ||(hasNp&&pos.equalsIgnoreCase("nns")))
							prev = s.getLemma(tId);
							type.add(s.getToken(tId));					
						hasNp = true;
					}else{
						if(ccFound && lookForAdjective){
							stop = true;// case like 2 turnip and 3 banana
							continue;
						}
						//found a plural noun
						if(pos.startsWith("nns")||(pos.startsWith("nn")&&changedAdj)){
							// if not present in the type add and stop
							boolean match = false;
							for(CoreLabel l : type){
								if(s.getLemma(l.index()).
										equalsIgnoreCase(s.getLemma(tId))){
									match  = true;
									break;
								}
							}
							
							if(!match){
								stop = false;
								prev = s.getLemma(tId);
								type.add(s.getToken(tId));
							}
						}
					}
				}else if(POSUtil.isNonComparativeAdj(pos)){
					if(hasNp&&!wnh.isAntonym(s.getLemma(tId), prev)){
						stop = true; // adj after noun "'games last' year"
					}else if(!ccFound){
						type.add(s.getToken(tId));
					}else if(ccFound){
						lookForAdjective = false;// not situations like 20 turnip and 30 watermelon
						changedAdj = true;
					}
				}else if(POSUtil.isCC(pos)){
					ccFound = true;
				}else if(pos.toLowerCase().equalsIgnoreCase("cd") && ccFound){
					lookForAdjective = true;
				}else{
					stop = true;
				}
				tId++;
			}
			
			
			/*
			tId--;
			String pos = s.getPOS(tId);
			if(s.getLemma(tId).equalsIgnoreCase("be")&& !lookForAdjective){
				tId++;
				if(s.hasToken(tId)){
					pos = s.getPOS(tId);
					if(POSUtil.isVerb(pos)){
						if(wnh.isAdjective(s.getLemma(tId)))
							type.add(s.getToken(tId)); // were broken 
					}
				}
			}
			*/
			if(type.isEmpty()){
				// look for $
				int start = token.index()-1;
				if(s.hasToken(start)&&s.getLemma(start).equalsIgnoreCase("$")){
					type.add(s.getToken(start));
				}else if(s.hasToken(start-1)&&s.getLemma(start-1).equalsIgnoreCase("$")){
					type.add(s.getToken(start-1));
				}
			}
			
		}catch(RuntimeException e){
			throw e;
		}
		/** end while loop*/
		
		return type;
	}
	
	public ArrayList<CoreLabel> findObj(AnnotatedSentence s, 
			int right) throws RuntimeException{
		
		int i=0;
		boolean nextNoun = false;
		boolean found = false;
		ArrayList<CoreLabel> obj = new ArrayList<CoreLabel>();
		
		for(CoreLabel token: s.getTokenSequence()){
			if(right!=-1 && i>=right)
				break;
			
			if(POSUtil.isVerb(s.getPOS(token))){
				nextNoun = true;
			}
			
			if(nextNoun && POSUtil.isNoun(s.getPOS(token))){
				obj.add(token);
				found = true;
			}
			
			if(found && !POSUtil.isNoun(s.getPOS(token)))
				break;
			i++;
		}
		return obj;
	}
}
