/**IMathConcept.java
 * 8:05:03 PM @author Arindam
 */
package nlp.app.math.core;

import org.json.JSONObject;

/**
 * @author Arindam
 *
 */
public interface IMathConcept {
	public Equation toEquation();
	public boolean equals(Object obj);
	public JSONObject toJSON();
}
