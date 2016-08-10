/**PartWholePartQue.java
 * 9:15:53 PM @author Arindam
 */
package nlp.app.math.core.features;

import java.util.List;
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
public class PartWholePartQue implements IFeatureExtractor{
	
	private String fName1 = "f_PartIsWithWhole";

	@Override
	public void addFeatures(ProblemRepresentation rep, MathSample sample, int y,
			Map<String, Double> aggregatefeatureMap, Map<String, Double> featureMap) {
		IMathConcept world = sample.getWorld(y);

		featureMap.put(fName1, 0.0);
		
		if(world instanceof PartWholeConcept){
			PartWholeConcept ppw = (PartWholeConcept) world;
			
			for(Quantity q: ppw.getParts()){
				if(q.isPart()){
					if(ppw.getParts().contains(q.getPartOf())){
						featureMap.put(fName1, 1.0);
						return;
					}
				}
			}
		}
	}
}
