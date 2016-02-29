/**LocalPriors.java
 * 2:51:37 AM @author Arindam
 */
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
public class LocalPriors implements IFeatureExtractor {

	private final String fName = "f_partIsSmallerThanWhole";
	@Override
	public void addFeatures(ProblemRepresentation rep, MathSample sample, int y,
			Map<String, Double> aggregatefeatureMap, Map<String, Double> featureMap) {
		double value = 0.0;
	
		IMathConcept world = sample.getWorld(y);
		if(world instanceof PartWholeConcept){
			PartWholeConcept ppw = (PartWholeConcept) world;

			for(Quantity part : ppw.getParts()){
				if(!part.isUnknown() && !ppw.getWhole().isUnknown() && 
					part.getDoubleValue() > ppw.getWhole().getDoubleValue()){
					value++;
				}
			}
		}
		
		aggregatefeatureMap.put(fName, value);
		featureMap.put(fName, value);
	}
}
