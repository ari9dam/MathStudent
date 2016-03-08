/**TypeRelatedFeatures.java
 * 4:20:11 PM @author Arindam
 */
package nlp.app.math.core.features;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.stanford.nlp.ling.CoreLabel;
import nlp.app.math.core.MathSample;
import nlp.app.math.core.ProblemRepresentation;
import nlp.app.math.core.Quantity;

/**
 * @author Arindam
 *
 */
public class TypeRelatedFeatures{

	private final String fName1 = "f_subType";
	private final String fName2 = "f_sameType";
	private final String fName3 = "f_notdisjoint";// not subType or Same Type and not disjoint
	private final String fName4 = "f_disjoint";//purely disjoint
	private final String fName5 = "f_isAType"; // computed from WordNet


	public void addFeatures(ProblemRepresentation rep, MathSample sample, 
			Map<String, Double> featureMap){

		for(Quantity q1: rep.getQuantities()){
			for(Quantity q2: rep.getQuantities()){

				String id = q1.getUniqueId() + q2.getUniqueId();
				ArrayList<CoreLabel> typeQ1 = q1.getType();
				ArrayList<CoreLabel> typeQ2 = q2.getType();

				Set<String> tQ1 = new HashSet<String>();
				Set<String> tQ2 = new HashSet<String>();

				for(CoreLabel l: typeQ1){
					tQ1.add(l.lemma());
				}

				for(CoreLabel l: typeQ2){
					tQ2.add(l.lemma());
				}


				featureMap.put(this.fName1+id, 0.0);
				featureMap.put(this.fName2+id, 0.0);
				featureMap.put(this.fName3+id, 0.0);
				featureMap.put(this.fName4+id, 0.0);
				featureMap.put(this.fName5+id, 0.0);// compute with concept net
				
				if(tQ1.size()==1&& tQ2.size()==1 ){
					boolean dollar = false;
					for(String l: tQ1){
						if(l.equalsIgnoreCase("$")||l.equalsIgnoreCase("money")|| l.equalsIgnoreCase("dollar")){
							dollar = true;
						}
					}
					if(dollar){
						for(String l: tQ2){
							if(l.equalsIgnoreCase("$")||l.equalsIgnoreCase("money")||l.equalsIgnoreCase("dollar")){
								dollar = true;
							}
						}
					}
					
					if(dollar)
						featureMap.put(this.fName2+id, 1.0);
				}
				
				if(q1.isUnknown()&& tQ1.isEmpty() && !tQ2.isEmpty()){
					featureMap.put(this.fName2+id, 1.0);
				}
				
				if(tQ1.containsAll(tQ2)){
					if(!tQ2.isEmpty() && tQ1.size()!=tQ2.size()
							){
						featureMap.put(this.fName1+id, 1.0);
					}else{
						featureMap.put(this.fName2+id, 1.0);
					}
				}else{
					tQ1.retainAll(tQ2);
					if(!tQ1.isEmpty()){
						featureMap.put(this.fName3+id, 1.0);
					}else{			
						featureMap.put(this.fName4+id, 1.0);
					}
				}
			}
		}

	}
}
