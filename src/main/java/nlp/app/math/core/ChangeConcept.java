/**ChangeConcept.java
 * 8:07:26 PM @author Arindam
 */
package nlp.app.math.core;

import java.util.List;

/**
 * @author Arindam
 *
 */
public class ChangeConcept implements IMathConcept {
	private Quantity start;
	private Quantity end;
	private List<Quantity> gains;
	private List<Quantity> losses;


	public ChangeConcept(Quantity start, Quantity end, List<Quantity> gains, 
			List<Quantity> losses) {
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


	public List<Quantity> getGains() {
		return gains;
	}


	public List<Quantity> getLosses() {
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



	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		result = prime * result + ((gains == null) ? 0 : gains.hashCode());
		result = prime * result + ((losses == null) ? 0 : losses.hashCode());
		result = prime * result + ((start == null) ? 0 : start.hashCode());
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChangeConcept other = (ChangeConcept) obj;
		if (end == null) {
			if (other.end != null)
				return false;
		} else if (!end.equals(other.end))
			return false;
		if (gains == null) {
			if (other.gains != null)
				return false;
		} else if (!gains.equals(other.gains))
			return false;
		if (losses == null) {
			if (other.losses != null)
				return false;
		} else if (!losses.equals(other.losses))
			return false;
		if (start == null) {
			if (other.start != null)
				return false;
		} else if (!start.equals(other.start))
			return false;
		return true;
	}
	
	

}
