/**ChangeLossQueue.java
 * 3:30:25 PM @author Arindam
 */
package nlp.app.math.core.features;

import java.util.List;
import java.util.Map;

import edu.asu.nlu.common.ds.AnnotatedSentence;
import edu.stanford.nlp.ling.CoreLabel;
import nlp.app.math.core.ChangeConcept;
import nlp.app.math.core.IMathConcept;
import nlp.app.math.core.MathSample;
import nlp.app.math.core.ProblemRepresentation;
import nlp.app.math.core.Quantity;
import nlp.app.math.util.VerbPolarityHelper;

/**
 * @author Arindam
 *
 */
public class ChangeLossQueue  implements IFeatureExtractor{
	private String fName = "f_change_losscue";
	private String fName1 = "f_mistyped_loss";
	
	@Override
	public void addFeatures(ProblemRepresentation rep, MathSample sample, int y,
			Map<String, Double> aggregatefeatureMap, Map<String, Double> featureMap) {
		featureMap.put(fName, 0.0);
		featureMap.put(fName1, 0.0);
		IMathConcept world = sample.getWorld(y);
		VerbPolarityHelper vhelper = VerbPolarityHelper.getInstance();
		
		if(y==30)
			System.out.print("");
		if(world instanceof ChangeConcept){
			boolean lossCue = false;
			ChangeConcept chc = (ChangeConcept) world;
			Quantity end = chc.getEnd();
			for(Quantity q: chc.getLosses()){
				lossCue = true;
				//all should have a non possessive verb 
				List<CoreLabel> verbs = q.getAssociatedEntity("verb");
				double polarity = 0;
				for(CoreLabel label: verbs){
					if(label.lemma().equalsIgnoreCase("be")||
							label.lemma().equalsIgnoreCase("has")||
							label.lemma().equalsIgnoreCase("have")){
						return;
					}					
					
					AnnotatedSentence sen = rep.getAnnotatedSentences().get(q.getSentenceId()-1);
					polarity += vhelper.getPolarity(sen.getFullLemma(label));

				}
				
				if(q.isUnknown()){
					if(sample.getQuantities().size()==3 && !chc.getStart().isDefault()){
						if(chc.getStart().getDoubleValue() < chc.getEnd().getDoubleValue()){
							featureMap.put(fName, 0.0); 
							return;
						}else{
							featureMap.put(fName, 1.0); 
							return;
						}
					}
				}
				
				String id = end.getUniqueId() + q.getUniqueId();
				double subjMatch = featureMap.get("f_subjmatch"+id);
				if(subjMatch<0.1&& !chc.getStart().isDefault()){
					String id2 = chc.getStart().getUniqueId()+ q.getUniqueId();
					subjMatch+= featureMap.get("f_subjmatch"+id2);
				}
				double typeMatch = featureMap.get("f_sameType"+id)+ featureMap.get("f_subType"+id) + 
						featureMap.get("f_subType"+q.getUniqueId()+end.getUniqueId()) ;
				
				if(typeMatch<0.5){
					featureMap.put(fName1, 1.0);
				}
				//double subjConsume = featureMap.get("f_subjConsume"+id);
				if(typeMatch<0.5 || (polarity <-0.5 && subjMatch < 0.5)||(polarity > 0.5 && subjMatch > 0.5)
						||(polarity<0.001 && polarity>-0.0002))
					return;
			}
			
			if(lossCue){
				featureMap.put(fName, 1.0);
			}
			
		}
		
	}
}
