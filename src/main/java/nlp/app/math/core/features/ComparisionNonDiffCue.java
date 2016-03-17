/**ComparisionNonDiffCue.java
 * 10:18:01 PM @author Arindam
 */
package nlp.app.math.core.features;

import java.util.List;
import java.util.Map;

import edu.asu.nlu.common.ds.AnnotatedSentence;
import edu.stanford.nlp.ling.CoreLabel;
import nlp.app.math.core.ComparisionConcept;
import nlp.app.math.core.IMathConcept;
import nlp.app.math.core.MathSample;
import nlp.app.math.core.ProblemRepresentation;
import nlp.app.math.core.Quantity;

/**
 * @author Arindam
 *
 */
public class ComparisionNonDiffCue  implements IFeatureExtractor {

	private String fName = "f_compare_nondiffcue";
	@Override
	public void addFeatures(ProblemRepresentation rep, MathSample sample, int y,
			Map<String, Double> aggregatefeatureMap, Map<String, Double> featureMap) {
		
		aggregatefeatureMap.put(fName, 0.0);
		
		
		IMathConcept world = sample.getWorld(y);
		
		boolean marker = false;
		boolean positive = true;
		boolean before = false;
		if(world instanceof ComparisionConcept){

			ComparisionConcept cmc = (ComparisionConcept) world;
			
			Quantity large = cmc.getLargerQuantity();
			Quantity small = cmc.getSmallerQuantity();
			Quantity diff = cmc.getDifference();
			
			
			if(!diff.isUnknown()){
				
				String id = large.getUniqueId() + diff.getUniqueId();
				String id1 = small.getUniqueId() + diff.getUniqueId();
				
				double typeMatch = featureMap.get("f_sameType"+id) +
						featureMap.get("f_sameType"+id1);
				if(typeMatch < 0.5)
					return;
						
				AnnotatedSentence sen = rep.getAnnotatedSentences()
						 .get(diff.getSentenceId()-1);
				 List<CoreLabel> tokens = sen.getTokenSequence();
				 
				 int start = diff.getTokenId();
				 
				 for(int i=start;i<Math.min(start+5, tokens.size());i++){
					 if(sen.getWord(i).equalsIgnoreCase("less")){
						 positive = false;
					 }else if(sen.getWord(i).equalsIgnoreCase("shorter")){
						 positive = false;
					 }else if(sen.getWord(i).equalsIgnoreCase("increased")){
						 marker = true;
					 }else if(sen.getLemma(i).equalsIgnoreCase("than")){
						 marker = true;
					 }
				 }
			}
			
			if(!positive){
				before = (large.getSentenceId() < small.getSentenceId())|| 
						((large.getSentenceId() == small.getSentenceId())
								&& large.getTokenId() < small.getTokenId());
			}else{
				before = (large.getSentenceId() > small.getSentenceId())|| 
						((large.getSentenceId() == small.getSentenceId())
								&& large.getTokenId() > small.getTokenId());
			}
			
		}
		
		
		if(marker&&before)
			aggregatefeatureMap.put(fName, 3.0);		
	}

}
