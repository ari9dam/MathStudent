/**ConvertMathSamples.java
 * 12:25:04 PM @author Arindam
 */
package nlp.app.math.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.asu.type.Sample;
import nlp.app.math.core.MathSample;

/**
 * @author Arindam
 *
 */
public class ConvertMathSamples {
	public void toSamples(List<MathSample> mathSamples, List<Sample> samples, ArrayList<String> features ){
		Set<String> f = new HashSet<String>();
		//combine all features
		for(MathSample ms: mathSamples){
			ArrayList<Map<String, Double>> map = ms.getFeatureMaps();
			for(Map<String, Double> entry: map){
				f.addAll(entry.keySet());
			}
		}

		//assign each feature a dimension
		features.clear();
		int index = 0;
		Map<String, Integer> indexMap = new HashMap<String, Integer>(); 
		for(String s: f){
			features.add(s);
			indexMap.put(s, index);
			index++;
		}
		// create feature vectors from feature maps
		for(MathSample ms: mathSamples){
			samples.add(this.toSample(ms, indexMap));
		}
	}

	public static void main(String args[]){
		MathSample s1 = new MathSample();
		s1.setCorrectY(0);
		Map<String,Double> f1 = new HashMap<String,Double>();
		f1.put("f1", 2.0);
		f1.put("f3", 0.0);
		f1.put("f4", 1.0);

		Map<String,Double> f3 = new HashMap<String,Double>();
		f3.put("f4", 2.0);
		f3.put("f3", 0.0);

		s1.addFeatureMap(f1);
		s1.addFeatureMap(f3);

		MathSample s2 = new MathSample();
		s2.setCorrectY(0);
		Map<String,Double> f2 = new HashMap<String,Double>();
		f2.put("f1", 2.0);
		f2.put("f2", 0.0);
		s2.addFeatureMap(f2);

		List<MathSample> lms = new ArrayList<MathSample>();
		lms.add(s1);
		lms.add(s2);

		List<Sample> ls = new ArrayList<Sample>();
		ArrayList<String> lf = new ArrayList<String>();

		ConvertMathSamples cn = new ConvertMathSamples();
		cn.toSamples(lms, ls, lf);
		System.out.println(lf);
	}

	/**
	 * @param math sample
	 * @param indexmap
	 * @return sample
	 */
	public Sample toSample(MathSample ms, Map<String,Integer> indexMap) {

		ArrayList<ArrayList<Double>> featureVectors = new ArrayList<ArrayList<Double>>();
		ArrayList<Map<String, Double>> arraymap = ms.getFeatureMaps();
		for(Map<String, Double> map: arraymap){
			Double[] fv = new Double[indexMap.size()];
			Arrays.fill(fv, 0.0);
			for( Entry<String, Double> entry: map.entrySet()){
				fv[indexMap.get(entry.getKey())] = entry.getValue();
			}
			featureVectors.add(new ArrayList<Double>(Arrays.asList(fv)));
		}
		Sample s = new Sample(ms.getId(), ms.getCorrectY(), featureVectors);
		return s;
	}
}
