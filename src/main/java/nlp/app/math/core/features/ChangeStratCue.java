/**ChangeVerbCue.java
 * 2:39:19 PM @author Arindam
 */
package nlp.app.math.core.features;

import java.util.List;
import java.util.Map;

import edu.asu.nlu.common.ds.AnnotatedSentence;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import nlp.app.math.core.ChangeConcept;
import nlp.app.math.core.IMathConcept;
import nlp.app.math.core.MathSample;
import nlp.app.math.core.ProblemRepresentation;
import nlp.app.math.core.Quantity;

/**
 * @author Arindam
 *
 */
public class ChangeStratCue implements IFeatureExtractor{

	private String fName1 = "f_change_start_implicitcue";
	private String fName2 = "f_change_start_explicitcue";
	private String fName3 = "f_change_default_startcue";
	@Override
	public void addFeatures(ProblemRepresentation rep, MathSample sample, int y,
			Map<String, Double> aggregatefeatureMap, Map<String, Double> featureMap) {
		aggregatefeatureMap.put(fName1, 0.0);
		aggregatefeatureMap.put(fName2, 0.0);
		aggregatefeatureMap.put(fName3, 0.0);
		
		IMathConcept world = sample.getWorld(y);
		
		if(world instanceof ChangeConcept){
			
			ChangeConcept chc = (ChangeConcept) world;
			
			Quantity start = chc.getStart();
			
			/**
			 * Start is default
			 */
			if(start.isDefault()){
				//there should not be any quantity with same type as final and 
				//has "be verb" with past tense for same subj and type
				boolean defaultStartcue = false;
				for(Quantity q: rep.getQuantities()){
					defaultStartcue = true;
					String id = start.getUniqueId()+ q.getUniqueId();
					if(q==start)
						continue;
					
					double typeMatch = featureMap.get("f_sameType"+id);
					double subjMatch = featureMap.get("f_subjmatch"+id);
					
					if(typeMatch >0.5 && subjMatch>0.5){
						List<CoreLabel> verbs = q.getAssociatedEntity("verb");
						for(CoreLabel label: verbs){
							if(label.lemma().equalsIgnoreCase("be")||
									label.lemma().equalsIgnoreCase("has")||
									label.lemma().equalsIgnoreCase("have")){
								defaultStartcue = false;
								break;
							}
						}
					}
				}
				
				if(defaultStartcue)
					aggregatefeatureMap.put(fName3, 1.0);
				
			}else{
				
				//start implicit cue : past possessive verb
				boolean pastPossesiveVerb = false;
				List<CoreLabel> verbs = start.getAssociatedEntity("verb");
				for(CoreLabel label: verbs){
					if(label.lemma().equalsIgnoreCase("be")||
							label.lemma().equalsIgnoreCase("has")||
							label.lemma().equalsIgnoreCase("have")){
						if("vbn".equalsIgnoreCase(label.get(PartOfSpeechAnnotation.class))||
								"vbd".equalsIgnoreCase(label.get(PartOfSpeechAnnotation.class))){
							pastPossesiveVerb = true;
						}
					}
				}
				
				if(pastPossesiveVerb){
					aggregatefeatureMap.put(fName1, 1.0);
				}
				
				//start explicit cue : "initially", "started with" etc.
				boolean explicitcue = false;
				List<CoreLabel> xcomp = start.getAssociatedEntity("xcomp");
				for(CoreLabel label: xcomp){
					if(label.lemma().equalsIgnoreCase("start")){
						AnnotatedSentence sentence = rep.getAnnotatedSentences().get(
								start.getSentenceId());
						
						String nextWord = sentence.getLemma(label.index()+1);
						if(nextWord!=null && "with".equalsIgnoreCase(nextWord)){
							explicitcue = true;
						}
					}else if(label.lemma().equalsIgnoreCase("initially")){
						explicitcue = true;
					}
				}
				
				if(explicitcue){
					aggregatefeatureMap.put(fName2, 1.0);
				}
			}
		}
	}
	
}
