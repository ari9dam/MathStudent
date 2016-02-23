package nlp.app.math.app;

import java.util.LinkedList;
import java.util.List;
import nlp.app.math.core.Equation;
import nlp.app.math.core.IMathConcept;
import nlp.app.math.core.ProblemRepresentation;

/**
 * @author Arindam
 *
 */
public class FormEquations {
	public List<Equation> formEquations(ProblemRepresentation irep){
		List<Equation> equations = new LinkedList<Equation>();
		
		for(IMathConcept c: irep.getMathConcepts())
			equations.add(c.toEquation());
		return equations;
	}
}
