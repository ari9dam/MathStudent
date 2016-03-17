package nlp.app.math.core.features;

import java.util.Map;

import nlp.app.math.core.IMathConcept;
import nlp.app.math.core.MathSample;
import nlp.app.math.core.PartWholeConcept;
import nlp.app.math.core.ProblemRepresentation;
import nlp.app.math.core.Quantity;

/**
 * @author Arindam
 *
 */
public class PartWholeSubjectCue implements IFeatureExtractor{

	/**
	 * Subject of a quantity e.g. "they" acts as the cue word
	 * If subj of whole consumes the subjects of the parts
	 * && type is same
	 * Then value = 1.0
	 */
	private final String fName = "f_partwhole_subjcue";
	@Override
	public void addFeatures(ProblemRepresentation rep, MathSample sample, int y,
			Map<String, Double> aggregatefeatureMap, Map<String, Double> featureMap) {
		
		aggregatefeatureMap.put(fName, 0.0);
		IMathConcept world = sample.getWorld(y);
		if(world instanceof PartWholeConcept){
			PartWholeConcept ppw = (PartWholeConcept) world;
			Quantity whole = ppw.getWhole();
			if(ppw.getParts().size()<2)
				return;
			double partcue = featureMap.get("f_PartIsWithWhole");
			if(partcue>0.5)
				return; 
			
			double value =ppw.getParts().size();
			for(Quantity part : ppw.getParts()){
				String id = whole.getUniqueId() + part.getUniqueId();
				double typeMatch = featureMap.get("f_sameType"+id);
				double subjConsume = featureMap.get("f_subjConsume"+id);
				if(typeMatch<0.5 || subjConsume <0.5 ){
					value = 0.0;
					break;
				}
			}
			
			aggregatefeatureMap.put(fName, value);
		}
	}
}
