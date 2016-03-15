package nlp.app.math.core.features;

import java.util.Map;

import edu.asu.nlu.common.ds.AnnotatedSentence;
import nlp.app.math.core.IMathConcept;
import nlp.app.math.core.MathSample;
import nlp.app.math.core.PartWholeConcept;
import nlp.app.math.core.ProblemRepresentation;
import nlp.app.math.core.Quantity;

/**
 * @author Arindam
 *
 */
public class PartWholeGenericQue implements IFeatureExtractor{

	private final String fName = "f_partwhole_genericcue";

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
			
			if(whole.isUnknown()&& !wholeMarkedWithCue){
				AnnotatedSentence sen = rep.getAnnotatedSentences().
						get(rep.getAnnotatedSentences().size()-1);
				String s = sen.getRawSentence().toLowerCase();
				if(s.contains("what")&&(s.contains(" either")||s.contains("total")||s.contains(" all ")))
					wholeMarkedWithCue = true;
			}

			for(Quantity part : ppw.getParts()){
				boolean partMarkedWithCue = part.isMarkedWithAll(rep);
		
				if(!wholeMarkedWithCue || partMarkedWithCue ){
					value = 0.0;
					break;
				}
			}
			aggregatefeatureMap.put(fName, value);

		}
	}
}