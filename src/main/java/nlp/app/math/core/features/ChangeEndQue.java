/**ChangeEndQue.java
 * 3:29:55 PM @author Arindam
 */
package nlp.app.math.core.features;

import java.util.List;
import java.util.Map;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import nlp.app.math.core.ChangeConcept;
import nlp.app.math.core.IMathConcept;
import nlp.app.math.core.MathSample;
import nlp.app.math.core.ProblemRepresentation;
import nlp.app.math.core.Quantity;

/**
 * @author Arindam
 *
 */
public class ChangeEndQue implements IFeatureExtractor {

	private String fName = "f_change_endcue";
	private String fName1 = "f_change_maybeendcue";
	@Override
	public void addFeatures(ProblemRepresentation rep, MathSample sample, int y,
			Map<String, Double> aggregatefeatureMap, Map<String, Double> featureMap) {
		featureMap.put(fName, 0.0);
		featureMap.put(fName1, 0.0);
		if(y==28||y==48)
			System.out.print("");
		IMathConcept world = sample.getWorld(y);

		if(world instanceof ChangeConcept){

			ChangeConcept chc = (ChangeConcept) world;

			Quantity end = chc.getEnd();
			
			/**
			 * verb is be
			 * the changes are of the same type
			 * subj match
			 */
			List<CoreLabel> verbs = end.getAssociatedEntity("verb");
			boolean presentPossesiveVerb = false;
			boolean onlybe = false;
			for(CoreLabel label: verbs){
				if(label.lemma().equalsIgnoreCase("be")||
						label.lemma().equalsIgnoreCase("has")||
						label.lemma().equalsIgnoreCase("have")){
					if(!"vbn".equalsIgnoreCase(label.get(PartOfSpeechAnnotation.class)) && 
							!"vbd".equalsIgnoreCase(label.get(PartOfSpeechAnnotation.class))){
						presentPossesiveVerb = true;
						if(!end.hasNonBeVerb())
							onlybe = true;
					}
				}
			}
			if(!presentPossesiveVerb)
				return;
			
			if(!chc.getStart().isDefault()){
				if(!chc.getStart().isUnknown() && !end.isUnknown()){
					if(chc.getStart().getDoubleValue() < end.getDoubleValue()
							&& chc.getGains().isEmpty())
						return;
					
				}
				String id = end.getUniqueId() + chc.getStart().getUniqueId();
				double typeMatch = featureMap.get("f_sameType"+id);
				double subjMatch = featureMap.get("f_subjmatch"+id);
				double  prepInMatch = 0.0;
				if(chc.getStart().getAssociatedEntity("nsubj").isEmpty()){
					prepInMatch = featureMap.get("f_prep_inmatch"+id);
				}
				
				  
				if(typeMatch <0.5 || (subjMatch <0.5 & prepInMatch <0.5)){
			
					if(typeMatch>0.5
							&& onlybe 
							&& (chc.getStart().getAssociatedEntity("nsubj").isEmpty()||
									end.getAssociatedEntity("nsubj").isEmpty())
							&& end.getSentenceId()>=(rep.getAnnotatedSentences().size()-1)){
						featureMap.put(fName1, 1.0);
					}
					
					if(end.getAssociatedEntity("prep_in").isEmpty() 
							&& typeMatch>0.5 
							&& presentPossesiveVerb && end.isUnknown()){
						featureMap.put(fName1, 1.0);
					}
					
					presentPossesiveVerb = false;
				}
			}
			
			if(presentPossesiveVerb && 
					(chc.getStart().getSentenceId()!= end.getSentenceId()))
				featureMap.put(fName, 1.0);
			
		}

	}

}
