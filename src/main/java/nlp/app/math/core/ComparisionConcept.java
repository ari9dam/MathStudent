/**ComparisionConcept.java
 * 6:48:14 PM @author Arindam
 */
package nlp.app.math.core;

import org.json.JSONObject;

/**
 * @author Arindam
 *
 */
public class ComparisionConcept implements IMathConcept{
	Quantity largerQuantity;
	Quantity smallerQuantity;
	Quantity difference;

	public ComparisionConcept(Quantity largerQuantity, Quantity smallerQuantity, Quantity difference) {
		this.largerQuantity = largerQuantity;
		this.smallerQuantity = smallerQuantity;
		this.difference = difference;
	}

	public ComparisionConcept() {
	}

	@Override
	public Equation toEquation() {
		String eq= this.largerQuantity.getValueForEquation() +" = ";
		eq+= this.getSmallerQuantity().getValueForEquation() +" + "+ 
		this.getDifference().getValueForEquation();
		Equation equation = new Equation(eq);
		return equation;
	}

	/**
	 * @return the largerQuantity
	 */
	public Quantity getLargerQuantity() {
		return largerQuantity;
	}


	/**
	 * @param largerQuantity the largerQuantity to set
	 */
	public void setLargerQuantity(Quantity largerQuantity) {
		this.largerQuantity = largerQuantity;
	}


	/**
	 * @return the smallerQuantity
	 */
	public Quantity getSmallerQuantity() {
		return smallerQuantity;
	}


	/**
	 * @param smallerQuantity the smallerQuantity to set
	 */
	public void setSmallerQuantity(Quantity smallerQuantity) {
		this.smallerQuantity = smallerQuantity;
	}


	/**
	 * @return the difference
	 */
	public Quantity getDifference() {
		return difference;
	}


	/**
	 * @param difference the difference to set
	 */
	public void setDifference(Quantity difference) {
		this.difference = difference;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((difference == null) ? 0 : difference.hashCode());
		result = prime * result + ((largerQuantity == null) ? 0 : largerQuantity.hashCode());
		result = prime * result + ((smallerQuantity == null) ? 0 : smallerQuantity.hashCode());
		return result;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ComparisionConcept other = (ComparisionConcept) obj;
		if (difference == null) {
			if (other.difference != null)
				return false;
		} else if (!difference.equals(other.difference))
			return false;
		if (largerQuantity == null) {
			if (other.largerQuantity != null)
				return false;
		} else if (!largerQuantity.equals(other.largerQuantity))
			return false;
		if (smallerQuantity == null) {
			if (other.smallerQuantity != null)
				return false;
		} else if (!smallerQuantity.equals(other.smallerQuantity))
			return false;
		return true;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ComparisionConcept [largerQuantity=" + largerQuantity + ", smallerQuantity=" + smallerQuantity
				+ ", difference=" + difference + "]";
	}
	
	public JSONObject toJSON(){
		JSONObject ret = new JSONObject();
		JSONObject arg1 = new JSONObject();
		JSONObject arg2 = new JSONObject();
		JSONObject arg3 = new JSONObject();
		
		arg1.put("name", "large");
		if(this.largerQuantity.isUnknown())
			arg1.put("value", "X");
		else arg1.put("value", this.largerQuantity.getValue());
		
		arg2.put("name", "diff");
		if(this.difference.isUnknown())
			arg2.put("value", "X");
		else arg2.put("value", this.difference.getValue());
		
		arg3.put("name", "small");
		if(this.smallerQuantity.isUnknown())
			arg3.put("value", "X");
		else arg3.put("value", this.smallerQuantity.getValue());
		
		
		ret.put("arg1", arg1);
		ret.put("arg2", arg2);
		ret.put("arg3", arg3);
		ret.put("type", "CP");
		
		return ret;
	}
}