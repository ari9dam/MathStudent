/**ComparisionConceptQue.java
 * 7:29:39 PM @author Arindam
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
public class ComparisionConceptDiffQue implements IFeatureExtractor {

	private String fName = "f_compare_diffcue";
	@Override
	public void addFeatures(ProblemRepresentation rep, MathSample sample, int y,
			Map<String, Double> aggregatefeatureMap, Map<String, Double> featureMap) {
		
		aggregatefeatureMap.put(fName, 0.0);
		
		
		IMathConcept world = sample.getWorld(y);
		
		boolean marker = false;
		if(world instanceof ComparisionConcept){

			ComparisionConcept cmc = (ComparisionConcept) world;
			
			Quantity large = cmc.getLargerQuantity();
			Quantity small = cmc.getSmallerQuantity();
			Quantity diff = cmc.getDifference();
			
			
			if(diff.isUnknown() 
					&& (large.getDoubleValue()> small.getDoubleValue())){
				
				String id = large.getUniqueId() + small.getUniqueId();
				
				double typeMatch = featureMap.get("f_sameType"+id);
				if(typeMatch < 0.5)
					return;
						
				AnnotatedSentence sen = rep.getAnnotatedSentences()
						 .get(diff.getSentenceId()-1);
				 List<CoreLabel> tokens = sen.getTokenSequence();
				 
				 boolean start = false;
				 for(CoreLabel token: tokens){
					 if(!start && token.lemma().equalsIgnoreCase("how")){
						 start = true;
					 }
					 
					 if(start){
						 if(sen.getPOS(token).equalsIgnoreCase("jjr")){
							 marker = true;
						 }else if(sen.getPOS(token).equalsIgnoreCase("rbr")){
							 marker = true;
						 }else if(sen.getLemma(token).equalsIgnoreCase("extra")){
							 marker = true;
						 }else if(sen.getLemma(token).equalsIgnoreCase("than")){
							 marker = true;
						 }
						 
						 if(marker)
							 break;
					 }
				 }
			}
			
		}
		
		if(marker)
			aggregatefeatureMap.put(fName, 1.0);
		
	}

}
