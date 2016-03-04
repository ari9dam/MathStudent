/**ChangeLossQueue.java
 * 3:30:25 PM @author Arindam
 */
package nlp.app.math.core.features;

import java.util.List;
import java.util.Map;

import edu.stanford.nlp.ling.CoreLabel;
import nlp.app.math.core.ChangeConcept;
import nlp.app.math.core.IMathConcept;
import nlp.app.math.core.MathSample;
import nlp.app.math.core.ProblemRepresentation;
import nlp.app.math.core.Quantity;
import nlp.app.math.util.VerbPolarityHelper;

/**
 * @author Arindam
 *
 */
public class ChangeLossQueue  implements IFeatureExtractor{
	private String fName = "f_change_losscue";


	@Override
	public void addFeatures(ProblemRepresentation rep, MathSample sample, int y,
			Map<String, Double> aggregatefeatureMap, Map<String, Double> featureMap) {
		aggregatefeatureMap.put(fName, 0.0);

		IMathConcept world = sample.getWorld(y);
		VerbPolarityHelper vhelper = VerbPolarityHelper.getInstance();
		
		if(world instanceof ChangeConcept){
			boolean lossCue = false;
			ChangeConcept chc = (ChangeConcept) world;
			Quantity end = chc.getEnd();
			for(Quantity q: chc.getLosses()){
				lossCue = true;
				//all should have a non possessive verb 
				List<CoreLabel> verbs = q.getAssociatedEntity("verb");
				double polarity = 0;
				for(CoreLabel label: verbs){
					if(label.lemma().equalsIgnoreCase("be")||
							label.lemma().equalsIgnoreCase("has")||
							label.lemma().equalsIgnoreCase("have")){
						return;
					}					
					
					polarity += vhelper.getPolarity(label.lemma());
				}
				
				if(q.isUnknown()){
					if(sample.getQuantities().size()==3){
						if(chc.getStart().getDoubleValue() < chc.getEnd().getDoubleValue()){
							aggregatefeatureMap.put(fName, 0.0); 
							return;
						}else{
							aggregatefeatureMap.put(fName, 1.0); 
							return;
						}
					}
				}
				
				String id = end.getUniqueId() + q.getUniqueId();
				double subjMatch = featureMap.get("f_subjmatch"+id);
				
				if((polarity <-0.5 && subjMatch < 0.5)||(polarity > 0.5 && subjMatch > 0.5))
					return;
			}
			
			if(lossCue){
				aggregatefeatureMap.put(fName, 1.0);
			}
			
		}
		
	}
}
