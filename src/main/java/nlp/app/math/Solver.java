package nlp.app.math;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nlp.app.math.app.EquationSolver;
import nlp.app.math.app.FormEquations;
import nlp.app.math.app.RelationFinder;
import nlp.app.math.app.UnknownFinder;
import nlp.app.math.core.Equation;
import nlp.app.math.core.Problem;
import nlp.app.math.core.ProblemRepresentation;
import nlp.app.math.preprocessing.StructureTagger;

/**
 * @author Arindam
 * This is the main class
 * It takes a problem as an input
 * forms a set of equations
 * pass those equation to a automated solver
 * return the solution
 */
public class Solver {
	private UnknownFinder unknownFinder;
	private RelationFinder relationFinder;
	private FormEquations equationGenerator;
	private EquationSolver equationSolver;
	private StructureTagger structureTagger;
	private boolean debug;


	public Solver(boolean debug, String model, ArrayList<String> features) throws ClassNotFoundException, IOException{
		this.debug = debug;
		this.unknownFinder = new UnknownFinder(debug);
		this.relationFinder = new RelationFinder(features,model);
		this.equationGenerator = new FormEquations();
		this.equationSolver = new EquationSolver();
		this.structureTagger = new StructureTagger(true);
	}
	
	public ProblemRepresentation prepareProblemRepresentation(String text){
		return this.prepareProblemRepresentation(text, null);
	}
	
	public ProblemRepresentation prepareProblemRepresentation(String text,String id){

		//empty intermediate representation of the problem
		ProblemRepresentation irep = new ProblemRepresentation(text);

		/**
		 * Run pre-processing tasks including
		 * tokenization, lemmatization, sentence splitting, CFG parsing, 
		 * dependency parsing, co-reference resolution
		 */
		this.structureTagger.process(irep);
		//System.out.println("tagging done");
		
		/**
		 * detect the unknonwn(s) in the problem
		 * create a variable for it
		 * extracts and saves the feature that represents the meaning of the variable
		 * 
		 */
		this.unknownFinder.findUnknowns(irep);
		
		irep.setId(id);
		return irep;
	}
	
	public Problem solve(ProblemRepresentation irep, boolean handleUnseenVerbs, 
			String verbSimilarityMethod ) throws IOException{
		
		//the output object; contains the text, equation(s) and the answer(s)
		Problem out = new Problem(irep.getText());
		/**
		 * Finds the best possible world 
		 * based on its experience in the training set
		 * Entity can be an unknown or a constant quantity in the text
		 */
		this.relationFinder.findRelations(irep ,handleUnseenVerbs,verbSimilarityMethod);
		
		/**
		 * generates the complete set of equations 
		 * from the intermediate representation
		 */
		List<Equation> equations = equationGenerator.formEquations(irep);

		/**
		 * call a system of equation solver to find the solution
		 * the solver requires the unknown to be specified in a separate list
		 */

		Map<String,String> solution = this.equationSolver.solve(equations, 
				irep.getUnknowns());

		/**
		 * prepare the return object
		 */
		out.setEquations(equations);
		out.setAnswers(solution);
		
		if(debug){
			System.out.println(irep);
		}
		return out;
	}

	/**
	 * @param problem text
	 * @return solution
	 * @throws IOException 
	 */
	public  Problem solve(String text) throws IOException {
		return this.solve(this.prepareProblemRepresentation(text), false, null);
	}
}
