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
public class PartWholeNmodCue implements IFeatureExtractor{

	private final String fName = "f_partwhole_verbnmodmatchcue";

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
			double value = ppw.getParts().size()*1.0;
			boolean wholeMarkedWithCue = whole.isMarkedWithAll(rep);

			
			for(Quantity part : ppw.getParts()){
				boolean partMarkedWithCue = part.isMarkedWithAll(rep);
							
				String id = whole.getUniqueId() + part.getUniqueId();
				double typeMatch = featureMap.get("f_sameType"+id);
				double exactVerbMatch = featureMap.get("f_exactVerbMatched"+id);

				
				if(typeMatch<0.5 || exactVerbMatch < 0.5|| !wholeMarkedWithCue || partMarkedWithCue ){
					value = 0.0;
					break;
				}
			}
			aggregatefeatureMap.put(fName, value);
			
		}
	}
}
