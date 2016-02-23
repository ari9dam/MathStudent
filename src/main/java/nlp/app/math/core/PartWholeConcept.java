/**Concept.java
 * 7:49:25 PM @author Arindam
 */
package nlp.app.math.core;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Arindam
 *
 */
public class PartWholeConcept implements IMathConcept{
	Quantity whole;
	Set<Quantity> parts;
	

	public PartWholeConcept(){
		this.whole = null;
		this.parts = new HashSet<Quantity>();
	}

	public PartWholeConcept(Quantity whole, Set<Quantity> parts) {
		this.whole = whole;
		this.parts = parts;
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
	public Set<Quantity> getParts() {
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
	
	
}

 