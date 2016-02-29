/**FeatureExtractor.java
 * 5:26:07 PM @author Arindam
 */
package nlp.app.math.core.features;

import java.util.Map;

import nlp.app.math.core.MathSample;
import nlp.app.math.core.ProblemRepresentation;

/**
 * @author Arindam
 *
 */
public interface IFeatureExtractor {
	public void addFeatures(ProblemRepresentation rep, MathSample sample, int y, Map<String,Double> aggregatefeatureMap,
			Map<String, Double> featureMap);
}
