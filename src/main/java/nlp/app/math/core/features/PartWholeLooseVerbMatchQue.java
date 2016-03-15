/**PartWholeLooseVerbMatchQue.java
 * 3:14:06 PM @author Arindam
 */
package nlp.app.math.core.features;

import java.util.Map;

import edu.stanford.nlp.ling.CoreLabel;
import nlp.app.math.core.IMathConcept;
import nlp.app.math.core.MathSample;
import nlp.app.math.core.PartWholeConcept;
import nlp.app.math.core.ProblemRepresentation;
import nlp.app.math.core.Quantity;

/**
 * @author Arindam
 *
 */
public class PartWholeLooseVerbMatchQue implements IFeatureExtractor{

	private final String fName = "f_partwhole_loose_verbmatchcue";

	// think of adding three more
	// 1. synnonym
	// 2. verb and subj match
	// 3. verb match and subj consume
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

			for(CoreLabel l: whole.getAssociatedEntity("verb")){
				if(l.lemma().equalsIgnoreCase("do"))
					return;
			}

			boolean existsAllMarker = false;

			for(Quantity q: sample.getQuantities()){
				if(q==whole)
					continue;
				existsAllMarker = existsAllMarker || q.isMarkedWithAll(rep);
			}

			if(wholeMarkedWithCue || (existsAllMarker) ||!whole.isUnknown())
				return;

			boolean matched = true;
			for(Quantity part : ppw.getParts()){

				for(Quantity q: ppw.getParts()){

					if(q==part)
						continue;

					String id = q.getUniqueId() + part.getUniqueId();
					double typeMatch = featureMap.get("f_sameType"+id);
					double exactVerbMatch = featureMap.get("f_exactVerbMatched"+id);
					if(!q.hasNonBeVerb() && !part.hasNonBeVerb())
						exactVerbMatch +=1;

					if(typeMatch<0.5){
						typeMatch = featureMap.get("f_subType"+id) + 
								featureMap.get("f_subType"+part.getUniqueId()+whole.getUniqueId());
					}

					if(typeMatch<0.5 || exactVerbMatch < 0.5){
						matched = false;
						break;
					}
				}
			}
			
			if(!matched) 
				return;
			
			for(Quantity q: ppw.getParts()){

				String id = q.getUniqueId() + whole.getUniqueId();
				double typeMatch = featureMap.get("f_sameType"+id);
				double exactVerbMatch = featureMap.get("f_exactVerbMatched"+id);
				if(!q.hasNonBeVerb() && !whole.hasNonBeVerb())
					exactVerbMatch +=1;
				
				
				if(typeMatch<0.5){
					typeMatch = featureMap.get("f_subType"+id) + 
							featureMap.get("f_subType"+whole.getUniqueId()+whole.getUniqueId());
				}

				if(typeMatch<0.5 || exactVerbMatch > 0.5){
					matched = false;
					break;
				}
			}
			
			if(matched)
				aggregatefeatureMap.put(fName, value);
		}
	}
}
