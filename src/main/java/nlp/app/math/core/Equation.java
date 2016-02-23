package nlp.app.math.core;

/**
 * @author Arindam
 *
 */
public class Equation {
	private String equation;

	/**
	 * @return the equation
	 */
	public String getEquation() {
		//equation with space doesn't work with the solver
		return equation.replaceAll(" ", "");
	}

	/**
	 * @param equation the equation to set
	 */
	public void setEquation(String equation) {
		this.equation = equation;
	}
	
	public Equation(String equation){
		this.equation = equation;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return equation;
	}
	
}
