/**ChangeCueDerived.java
 * 1:16:15 AM @author Arindam
 */
package nlp.app.math.core.features;

import java.util.Map;

import nlp.app.math.core.ChangeConcept;
import nlp.app.math.core.IMathConcept;
import nlp.app.math.core.MathSample;
import nlp.app.math.core.ProblemRepresentation;

/**
 * @author Arindam
 *
 */
public class ChangeCueDerived implements IFeatureExtractor {

	private String fName1 = "f_def_gain_change";
	private String fName2 = "f_def_only_loss_change";
	private String fName3 = "f_derived_change";
	private String fName6 = "f_derived_change_type_mismatch";
	private String fName4 = "f_derived_change_mayend";
	private String fName5 = "f_derived_change_priorstart";
	private String fName7 = "f_derived_change_explicitstart";
	private String fName8 = "f_derived_change_explicitstart_mayend";
	@Override
	public void addFeatures(ProblemRepresentation rep, MathSample sample, int y,
			Map<String, Double> aggregatefeatureMap, Map<String, Double> featureMap) {

		aggregatefeatureMap.put(fName1, 0.0);
		aggregatefeatureMap.put(fName2, 0.0);
		aggregatefeatureMap.put(fName3, 0.0);
		aggregatefeatureMap.put(fName4, 0.0);
		aggregatefeatureMap.put(fName5, 0.0);
		aggregatefeatureMap.put(fName6, 0.0);
		aggregatefeatureMap.put(fName7, 0.0);
		IMathConcept world = sample.getWorld(y);

		if(world instanceof ChangeConcept){

			ChangeConcept chc = (ChangeConcept) world;
			
			int weight = chc.getGains().size() + chc.getLosses().size(); 
			boolean notEmptyLoss = chc.getLosses().size()>0;
			boolean notEmptyGain = chc.getGains().size()>0;
			
			boolean loss = featureMap.get("f_change_losscue") > 0.5;
			boolean gain = featureMap.get("f_change_gaincue")>0.5;
			
			boolean bad = (notEmptyLoss && ! loss) ||(notEmptyGain && !gain);
			boolean im_start = featureMap.get("f_change_start_implicitcue") > 0.5;
			boolean exp_start = featureMap.get("f_change_start_explicitcue") > 0.5;
			boolean def_start = featureMap.get("f_change_default_startcue") > 0.5;
			boolean prior_start = featureMap.get("f_change_prior_startcue") > 0.5;

			boolean end = featureMap.get("f_change_endcue") > 0.5;
			boolean mayend = featureMap.get("f_change_maybeendcue") > 0.5;

			boolean loss_type_mismatch = featureMap.get("f_mistyped_loss") > 0.5;
			boolean gain_type_mismatch = featureMap.get("f_mistyped_gain") > 0.5;

			boolean def_gain_change = !bad && gain && def_start && end;
			boolean def_only_loss_change = !bad && !gain && loss && def_start && end;
			boolean derived_change = !bad && (gain || loss) && (im_start) && ! def_start && end;
			boolean derived_change_exp_start = !bad && (gain || loss) && (exp_start) && ! def_start && end;
			boolean derived_prior_start = !bad && (gain || loss) && prior_start && (end||mayend);
			boolean derived_change_mayend = !bad && (gain || loss) && (prior_start||im_start) && !def_start && mayend;
			boolean derived_change_expst_mayend = !bad && (gain || loss) && (exp_start) && !def_start && mayend;

			boolean change_type_mismatch = !bad && (gain || loss) && (def_start||exp_start||im_start||prior_start)  
					&& (end||mayend) && (loss_type_mismatch||gain_type_mismatch);


			aggregatefeatureMap.put(fName1, def_gain_change? weight:0.0);
			aggregatefeatureMap.put(fName2, def_only_loss_change? weight:0.0);
			aggregatefeatureMap.put(fName3, derived_change? weight:0.0);
			aggregatefeatureMap.put(fName4, derived_change_mayend? weight:0.0);
			aggregatefeatureMap.put(fName5, derived_prior_start? weight:0.0);
			aggregatefeatureMap.put(fName6, change_type_mismatch? weight:0.0);
			aggregatefeatureMap.put(fName7, derived_change_exp_start? weight:0.0);
			aggregatefeatureMap.put(fName8, derived_change_expst_mayend? weight:0.0);
			if(change_type_mismatch){
				System.out.println("mismatch");
			}

		}
	}

}
