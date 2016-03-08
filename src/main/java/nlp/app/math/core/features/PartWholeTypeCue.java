/**PartWholeTypeCue.java
 * 11:49:39 AM @author Arindam
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
public class PartWholeTypeCue implements IFeatureExtractor {
	private final String fName = "f_partwhole_typecue";
	
	/*
	 * case 1:
		whole A and Parts B,C
		type A sub type B,C
		type B , type C disjoint 

	   case 2: 
		type B is  type A
		type C is  type A
		type B not eq type C

	   case 3:
		type B is sup of type A
		type C is A type A
		type B not eq type C
	 **/
	@Override
	public void addFeatures(ProblemRepresentation rep, MathSample sample, int y,
			Map<String, Double> aggregatefeatureMap, Map<String, Double> featureMap) {
		
		aggregatefeatureMap.put(fName, 0.0);
		
		IMathConcept world = sample.getWorld(y);
		
		if(world instanceof PartWholeConcept){
			
			PartWholeConcept ppw = (PartWholeConcept) world;
			if(ppw.getParts().size()<2)
				return;			
			add(rep, sample, y, aggregatefeatureMap, featureMap);
		}
	}
	
	public void add(ProblemRepresentation rep, MathSample sample, int y,
			Map<String, Double> aggregatefeatureMap, Map<String, Double> featureMap){
		IMathConcept world = sample.getWorld(y);
		PartWholeConcept ppw = (PartWholeConcept) world;
		
		Quantity whole = ppw.getWhole();
		double value = 1.0;
		for(Quantity part : ppw.getParts()){
			String id = part.getUniqueId() + whole.getUniqueId() ;
			double subType = featureMap.get("f_subType"+id);
			double isAType = featureMap.get("f_isAType"+id);
			if(isAType<0.5 && subType<0.5){
				value = 0.0;
				break;
			}
		}
		
		if(value <0.5)
			return; // not sub type or isA
		
		for(Quantity part1 : ppw.getParts()){
			for(Quantity part2 : ppw.getParts()){
				if(part1==part2)
					continue;
				String id = part1.getUniqueId() + part2.getUniqueId();
				double isDisjointType = featureMap.get("f_disjoint"+id);
				
				if(isDisjointType<0.5){
					value = 0.0;
					break;
				}
			}
		}
		
		aggregatefeatureMap.put(fName, value);
	}

}
