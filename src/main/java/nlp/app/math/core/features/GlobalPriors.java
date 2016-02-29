package nlp.app.math.core.features;

import java.util.Map;
import nlp.app.math.core.IMathConcept;
import nlp.app.math.core.MathSample;
import nlp.app.math.core.PartWholeConcept;
import nlp.app.math.core.ProblemRepresentation;

/**
 * @author Arindam
 *
 */
public class GlobalPriors implements IFeatureExtractor{
	private final String fName1 = "f_moreThanOnePart"; 

	@Override
	public void addFeatures(ProblemRepresentation rep, MathSample sample, int y,
			Map<String, Double> aggregatefeatureMap, Map<String, Double> featureMap) {
		IMathConcept world = sample.getWorld(y);
		aggregatefeatureMap.put(fName1, 0.0);
		featureMap.put(fName1, 0.0);
		
		if(world instanceof PartWholeConcept){
			PartWholeConcept ppw = (PartWholeConcept) world;
			if(ppw.getParts().size()>1){
				aggregatefeatureMap.put(fName1, 1.0);
				featureMap.put(fName1, 1.0);
			}
		}
	}
}
