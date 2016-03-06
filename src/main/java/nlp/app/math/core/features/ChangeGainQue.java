/**ChangeGainQue.java
 * 3:30:12 PM @author Arindam
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
public class ChangeGainQue implements IFeatureExtractor {

	private String fName = "f_change_gaincue";
	private String fName1 = "f_mistyped_gain";
	@Override
	public void addFeatures(ProblemRepresentation rep, MathSample sample, int y,
			Map<String, Double> aggregatefeatureMap, Map<String, Double> featureMap) {
		featureMap.put(fName, 0.0);
		featureMap.put(fName1, 0.0);
		IMathConcept world = sample.getWorld(y);
		VerbPolarityHelper vhelper = VerbPolarityHelper.getInstance();
		if(y==268)
			System.out.print("");
		
		if(world instanceof ChangeConcept){
			boolean gainCue = false;
			ChangeConcept chc = (ChangeConcept) world;
			Quantity end = chc.getEnd();
			for(Quantity q: chc.getGains()){
				gainCue = true;
				//all should have a non possessive verb 
				List<CoreLabel> verbs = q.getAssociatedEntity("verb");
				if(!q.hasNonBeVerb())
					return;
				
				double polarity = 0;
				for(CoreLabel label: verbs){
					polarity += vhelper.getPolarity(label.lemma());
				}
				
				if(q.isUnknown()){
					if(sample.getQuantities().size()==3 && !chc.getStart().isDefault()){
						if(chc.getStart().getDoubleValue() < chc.getEnd().getDoubleValue()){
							featureMap.put(fName, 1.0); 
							return;
						}else{
							featureMap.put(fName, 0.0); 
							return;
						}
					}
				}
				
				String id = end.getUniqueId() + q.getUniqueId();
				double subjMatch = featureMap.get("f_subjmatch"+id);
				double typeMatch = featureMap.get("f_sameType"+id);
				if(typeMatch<0.5){
					featureMap.put(fName1, 1.0);
				}
				if(typeMatch < 0.5 || (polarity <-0.5 && subjMatch > 0.5)||(polarity > 0.5 && subjMatch < 0.5)||(polarity<0.001 && polarity>-0.0002))
					return;
				
			}

			if(gainCue){
				featureMap.put(fName, 1.0);
			}
		}
	}

}
