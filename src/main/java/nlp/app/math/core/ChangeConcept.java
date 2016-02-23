/**ChangeConcept.java
 * 8:07:26 PM @author Arindam
 */
package nlp.app.math.core;

import java.util.Set;

/**
 * @author Arindam
 *
 */
public class ChangeConcept implements IMathConcept {
	private Quantity start;
	private Quantity end;
	private Set<Quantity> gains;
	private Set<Quantity> losses;


	public ChangeConcept(Quantity start, Quantity end, Set<Quantity> gains, 
			Set<Quantity> losses) {
		this.start = start;
		this.end = end;
		this.gains = gains;
		this.losses = losses;
	}



	public Quantity getStart() {
		return start;
	}


	public Quantity getEnd() {
		return end;
	}


	public Set<Quantity> getGains() {
		return gains;
	}


	public Set<Quantity> getLosses() {
		return losses;
	}

	@Override
	public Equation toEquation() {
		String leq= this.start.getValueForEquation();
		String req = this.end.getValueForEquation();
		
		for(Quantity gain:this.getGains()){
			leq+=" + " + gain.getValueForEquation();
		}
		
		for(Quantity loss:this.getLosses()){
			req+=" + " + loss.getValueForEquation();
		}
		
		Equation equation = new Equation(leq+" = "+req);
		return equation;
	}

}
