/**PartWholeTmodQue.java
 * 10:15:44 PM @author Arindam
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
public class PartWholeTmodQue implements IFeatureExtractor{
	private final String fName = "f_partwhole_verbtmodmatchcue";

	@Override
	public void addFeatures(ProblemRepresentation rep, MathSample sample, int y,
			Map<String, Double> aggregatefeatureMap, Map<String, Double> featureMap) {
		aggregatefeatureMap.put(fName, 0.0);
		IMathConcept world = sample.getWorld(y);
		if(world instanceof PartWholeConcept){

			PartWholeConcept ppw = (PartWholeConcept) world;

			if(ppw.getParts().size()<2)
				return;

			Quantity whole = ppw.getWhole();
			double value = ppw.getParts().size();

			
			for(Quantity part : ppw.getParts()){


				String id = whole.getUniqueId() + part.getUniqueId();
				double typeMatch = featureMap.get("f_sameType"+id);
				double exactVerbMatch = featureMap.get("f_exactVerbMatched"+id);
				double tmodmatch = featureMap.get("f_tmodmatch"+id);


				if(typeMatch<0.5 || exactVerbMatch < 0.5|| !whole.isUnknown() ||
						tmodmatch < 0.5){
					value = 0.0;
					break;
				}
			}
			
			aggregatefeatureMap.put(fName, value);

		}
	}
}
