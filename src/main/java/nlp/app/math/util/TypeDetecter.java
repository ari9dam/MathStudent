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
	public ArrayList<CoreLabel> findType(CoreLabel token, AnnotatedSentence s){
		/**
		 * Simple deterministic algorithm	
		 */
		boolean stop = false;
		boolean ccFound = false;
		boolean hasNp = false;
		ArrayList<CoreLabel> type = new ArrayList<CoreLabel>();
		int tId = token.index()+1;
		while(!stop && s.hasToken(tId)){
			String pos = s.getPOS(tId);

			if(POSUtil.isNoun(pos)){
				if(!ccFound){
					if(!hasNp ||(hasNp&&pos.equalsIgnoreCase("nns")))
						type.add(s.getToken(tId));					
					hasNp = true;
				}else{
					boolean match = false;
					//found a plural noun
					if(pos.equalsIgnoreCase("nns")){
						// if not present in the type add and stop
						for(CoreLabel l : type){
							if(s.getLemma(l.index()).
									equalsIgnoreCase(s.getLemma(tId))){
								match = true;
								break;
							}
							stop = false;	
							type.add(s.getToken(tId));
						}
					}
				}
			}else if(POSUtil.isNonComparativeAdj(pos)){
				if(!ccFound)
					type.add(s.getToken(tId));
			}else if(POSUtil.isCC(pos)){
				ccFound = true;
			}else{
				stop = true;
			}
			tId++;
		}
		/** end while loop*/
		
		return type;
	}
}
