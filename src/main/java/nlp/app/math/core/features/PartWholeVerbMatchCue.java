/**PartWholeVerbMatchCue.java
 * 10:39:30 AM @author Arindam
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
public class PartWholeVerbMatchCue implements IFeatureExtractor{

	private final String fName = "f_partwhole_verbmatchcue";
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
			Quantity whole = ppw.getWhole();
			double value = 0.0;
			boolean wholeMarkedWithCue = false;
			
			
			for(CoreLabel c: whole.getAssociatedEntity("prep_in")){
				if(c.lemma().equalsIgnoreCase("total")){
					wholeMarkedWithCue = true;
				}else if(c.lemma().equalsIgnoreCase("all")){
					wholeMarkedWithCue = true;
				}
			}
			for(CoreLabel l: whole.getAssociatedEntity("advmod")){
				if(l.lemma().equalsIgnoreCase("together"))
					wholeMarkedWithCue = true;
			}
			
			if(ppw.getParts().size()<2)
				return;
			/**
			 * 1 evening , a restaurant served a total of 0.2 loaf of wheat bread and 0.4 
			 * loaf of white bread .
			 *  How many loaves were served in all ?
			 */
			value = 1.0;
			for(Quantity part : ppw.getParts()){
				
				
				String id = whole.getUniqueId() + part.getUniqueId();
				double typeMatch = featureMap.get("f_sameType"+id);
				double exactVerbMatch = featureMap.get("f_exactVerbMatched"+id);

				for(CoreLabel l: whole.getAssociatedEntity("dobj")){
					if(l.lemma().equalsIgnoreCase("total")){
						if(part.getAssociatedEntity("prep_in").isEmpty()){
							wholeMarkedWithCue = true;
						}else{
							for(CoreLabel c: part.getAssociatedEntity("prep_in")){
								if(!c.lemma().equalsIgnoreCase("total")&&!c.lemma().equalsIgnoreCase("all"))
									wholeMarkedWithCue = true;
							}
						}
					}
				} 
				
				if(typeMatch<0.5 || exactVerbMatch < 0.5|| !wholeMarkedWithCue ){
					value = 0.0;
					break;
				}
			}
			aggregatefeatureMap.put(fName, value);
			
		}
	}
}
