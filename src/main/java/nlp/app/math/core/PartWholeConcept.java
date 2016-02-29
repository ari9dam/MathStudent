/**Concept.java
 * 7:49:25 PM @author Arindam
 */
package nlp.app.math.core;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Arindam
 *
 */
public class PartWholeConcept implements IMathConcept{
	Quantity whole;
	List<Quantity> parts;
	

	public PartWholeConcept(){
		this.whole = null;
		this.parts = new ArrayList<Quantity>();
	}

	public PartWholeConcept(Quantity whole, List<Quantity> parts) {
		this.whole = whole;
		this.parts = new ArrayList<Quantity>(parts);
	}
	
	
	public void setWhole(Quantity q){
		this.whole = q;
	}
	
	public void addPart(Quantity q){
		this.parts.add(q);
	}
	
	/**
	 * @return the whole
	 */
	public Quantity getWhole() {
		return whole;
	}

	/**
	 * @return the parts
	 */
	public List<Quantity> getParts() {
		return parts;
	}

	@Override
	public String toString() {
		return "PartWholeConcept ["+"\nwhole=" + whole + ", \nparts=" + parts + "]";
	}

	
	@Override
	public Equation toEquation() {
		String eq= this.whole.getValueForEquation() +" = ";
		boolean isFirst = true;
		for(Quantity part:this.getParts()){
			if(isFirst)
				eq+=part.getValueForEquation();
			else
				eq+=" + " + part.getValueForEquation();
			isFirst = false;
		}
		Equation equation = new Equation(eq);
		return equation;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((parts == null) ? 0 : parts.hashCode());
		result = prime * result + ((whole == null) ? 0 : whole.hashCode());
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
		PartWholeConcept other = (PartWholeConcept) obj;
		if (parts == null) {
			if (other.parts != null)
				return false;
		} else if (!parts.equals(other.parts))
			return false;
		if (whole == null) {
			if (other.whole != null)
				return false;
		} else if (!whole.equals(other.whole))
			return false;
		return true;
	}
	
	
}

 