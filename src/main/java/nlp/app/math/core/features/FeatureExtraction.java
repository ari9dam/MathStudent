/**FeatureExtraction.java
 * 5:58:36 PM @author Arindam
 */
package nlp.app.math.core.features;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nlp.app.math.core.MathSample;
import nlp.app.math.core.ProblemRepresentation;

/**
 * @author Arindam
 *
 */
public class FeatureExtraction {
	List<IFeatureExtractor> featureExtractors;
	private Map<String, Double> verbPolarityMap;

	public FeatureExtraction() {
		this(new HashMap<String, Double>());
	}

	public FeatureExtraction(Map<String, Double> verbPolarityMap) {
		this.featureExtractors = new LinkedList<IFeatureExtractor>();

		this.featureExtractors.add(new GlobalPriors());
		this.featureExtractors.add(new LocalPriors());
		this.featureExtractors.add(new PartWholePartQue());
		this.featureExtractors.add(new PartWholeSubjectCue());
		this.featureExtractors.add(new PartWholeVerbMatchCue());
		this.featureExtractors.add(new PartWholeLooseVerbMatchQue());
		this.featureExtractors.add(new PartWholeTypeCue());
		this.featureExtractors.add(new PartWholePropertyCue());
		this.featureExtractors.add(new PartWholeTmodQue());
		this.featureExtractors.add(new PartWholeNmodCue());
		this.featureExtractors.add(new PartWholeGenericQue());
		this.featureExtractors.add(new ChangeStratCue());
		this.featureExtractors.add(new ChangeEndQue());
		this.featureExtractors.add(new ChangeGainQue());
		this.featureExtractors.add(new ChangeLossQueue());
		this.featureExtractors.add(new ChangeCueDerived());
		this.featureExtractors.add(new ComparisionConceptDiffQue());
		this.featureExtractors.add(new ComparisionNonDiffCue());
		
		this.verbPolarityMap = new HashMap<String, Double>();
		this.verbPolarityMap.putAll(verbPolarityMap);
		
		
	}

	public Map<String, Double> getFeatureMap(ProblemRepresentation rep, MathSample sample, int y) {
		Map<String, Double> featureMap = new HashMap<String, Double>();
		Map<String, Double> aggfeatureMap = new HashMap<String, Double>();

		return this.getFeatureMap(rep, sample, y, featureMap, aggfeatureMap);
	}

	public Map<String, Double> getFeatureMap(ProblemRepresentation rep, MathSample sample, int y,
			Map<String, Double> featureMap, Map<String, Double> aggfeatureMap) {

		/**
		 * call a series of feature extractor
		 */
		for (IFeatureExtractor fe : this.featureExtractors) {
			fe.addFeatures(rep, sample, y, aggfeatureMap, featureMap);
		}
		return aggfeatureMap;
	}

	public Map<String, Double> computeFeatureMaps(ProblemRepresentation rep, MathSample sample, int max){
		Map<String,Double> vpMap = new HashMap<String,Double>();
		Map<String,Double> cMap = new HashMap<String,Double>(this.verbPolarityMap);
		FeatureExtractorCommon common = new FeatureExtractorCommon();
		common.addFeatures(rep, sample, cMap);
		TypeRelatedFeatures typeFeatures = new TypeRelatedFeatures();
		typeFeatures.addFeatures(rep, sample, cMap);
		
		for(int i=0;i<sample.numberOfPossibleWorlds();i++){
			Map<String,Double> fMap = new HashMap<String,Double>(cMap);
			Map<String,Double> aggfeatureMap = new HashMap<String,Double>();
			Map<String,Double> featureMap = this.getFeatureMap(rep, sample, i, 
					fMap, aggfeatureMap);
			
			sample.addFeatureMap(featureMap);
			/**
			 * Extract verb polarity
			 */
			for(Entry<String, Double> entry:fMap.entrySet() ){
				if(entry.getKey().startsWith("verb")){
					vpMap.put(entry.getKey(), entry.getValue());
				}
			}
		}
		return vpMap;
	}
}
