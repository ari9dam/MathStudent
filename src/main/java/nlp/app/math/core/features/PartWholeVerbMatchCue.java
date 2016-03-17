package nlp.app.math.core.features;

import java.util.List;
import java.util.Map;

import edu.stanford.nlp.ling.CoreLabel;
import nlp.app.conceptnet.ConceptNetCache;
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
	private final String fName1 = "f_partwhole_verbrelatedcue";
	// think of adding three more
	// 1. synnonym
	// 2. verb and subj match
	// 3. verb match and subj consume
	@Override
	public void addFeatures(ProblemRepresentation rep, MathSample sample, int y,
			Map<String, Double> aggregatefeatureMap, Map<String, Double> featureMap) {
		aggregatefeatureMap.put(fName, 0.0);
		aggregatefeatureMap.put(fName1, 0.0);
		IMathConcept world = sample.getWorld(y);
		if(world instanceof PartWholeConcept){

			PartWholeConcept ppw = (PartWholeConcept) world;

			if(ppw.getParts().size()<2)
				return;
			double partcue = featureMap.get("f_PartIsWithWhole");
			if(partcue>0.5)
				return; 
			Quantity whole = ppw.getWhole();
			double value = ppw.getParts().size()*1.0;
			boolean usedRelatedVerb = false;
			double value1 = ppw.getParts().size();
			boolean wholeMarkedWithCue = whole.isMarkedWithAll(rep);



			boolean existsAllMarker = false;

			for(Quantity q: sample.getQuantities()){
				if(q==whole)
					continue;
				existsAllMarker = existsAllMarker || q.isMarkedWithAll(rep);
			}

			if(!wholeMarkedWithCue && (existsAllMarker|| !whole.isUnknown()))
				return;
			
			if(whole.isMarkedWithTotalOf(rep)&& existsAllMarker )
				return;

			for(Quantity part : ppw.getParts()){

				String id = whole.getUniqueId() + part.getUniqueId();
				double typeMatch = featureMap.get("f_sameType"+id);
				double exactVerbMatch = featureMap.get("f_exactVerbMatched"+id);


				if(typeMatch<0.5){
					typeMatch = featureMap.get("f_subType"+id) + 
							featureMap.get("f_subType"+part.getUniqueId()+whole.getUniqueId());
				}

				if(typeMatch<0.5 || exactVerbMatch < 0.5){

					if(typeMatch >0.5){
						List<CoreLabel> verb1 = whole.getAssociatedEntity("verb");
						List<CoreLabel> verb2 = part.getAssociatedEntity("verb");
						if(verb1.size()==1&&verb2.size()==1  && exactVerbMatch<0.5 
								&& part.hasNonBeVerb() && whole.hasNonBeVerb()){
							String v1 = verb1.get(0).lemma();
							String v2 = verb2.get(0).lemma();

							boolean relatedTo = ConceptNetCache.getInstance().
									isRelated(v1, v2, "RelatedTo");

							relatedTo  =  relatedTo || ConceptNetCache.getInstance().
									isRelated(v2, v1, "RelatedTo");
							if(!relatedTo
									||v1.equalsIgnoreCase("do")||v2.equalsIgnoreCase("do")){
								value1 = 0.0;
							}else{
								usedRelatedVerb = true;
								System.out.println(v1+"-"+v2);
							}
						}else{
							value1 = 0.0;
						}
					}else{
						value1 = 0.0;
					}

					value = 0.0;
					break;
				}
			}
			
			aggregatefeatureMap.put(fName, value);
			if(usedRelatedVerb)
				aggregatefeatureMap.put(fName1, value1);
		}
	}
}
