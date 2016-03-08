/**PartWholePropertyCue.java
 * 1:02:07 PM @author Arindam
 */
package nlp.app.math.core.features;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.ling.CoreLabel;
import nlp.app.math.core.IMathConcept;
import nlp.app.math.core.MathSample;
import nlp.app.math.core.PartWholeConcept;
import nlp.app.math.core.ProblemRepresentation;
import nlp.app.math.core.Quantity;
import nlp.app.math.util.WordNetHelper;

/**
 * @author Arindam
 *
 */
public class PartWholePropertyCue implements IFeatureExtractor{
	private WordNetHelper wnh = WordNetHelper.getInstance();
	private final String fName = "f_partwhole_properycue";
	
	/**
	 * B did X on Y type. N out of Y has P. How many don't have P?
	 */
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
			double typeMatch = featureMap.get("f_sameType"+id);
			if(isAType<0.5 && subType<0.5 && typeMatch<0.5){
				value = 0.0;
				break;
			}
		}
		
		if(value <0.5)
			return; 
		boolean isAntonym = false;
		for(Quantity part1 : ppw.getParts()){
			for(Quantity part2 : ppw.getParts()){
				if(part1==part2)
					continue;
				String id = part1.getUniqueId() + part2.getUniqueId();
				double isDisjointType = featureMap.get("f_disjoint"+id);
				if(isDisjointType>0.5){
					value = 0.0;
					break;
				}
				
				List<String> comm = new LinkedList<String>();
				List<String> diff1 = new LinkedList<String>();
				List<String> diff2 = new LinkedList<String>();
				
				for(CoreLabel c : part1.getType()){
					diff1.add(c.lemma());
				}
				
				for(CoreLabel c : part2.getType()){
					diff2.add(c.lemma());
				}
				
				
				for(CoreLabel c : part2.getAssociatedEntity("verb")){
					diff2.add(c.originalText());
				}
				
				comm.addAll(diff2);
				comm.retainAll(diff1);
				
				diff1.removeAll(comm);
				diff2.removeAll(comm);

				for(String s: diff1){
					for(String s1: diff2){
						if(wnh.isAntonym(s, s1)){
							isAntonym = true;
							break;
						}
					}
				}

			}
		}
		
		if(!isAntonym)
			value = 0.0;
		aggregatefeatureMap.put(fName, value);
	}
}
