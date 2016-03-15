/**Main.java
 * 7:57:28 PM @author Arindam
 */
package nlp.app.math;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;

/**
 * @author Arindam
 *
 */
public class Main {
	public static void main(String[] args) throws IOException, ClassNotFoundException{
		String prefix="C:\\Users\\Arindam\\Dropbox\\Math Challenge\\sample_questions";
		ArrayList<String> features = new ArrayList<String>(FileUtils.readLines(
				new File(prefix+"_features.txt")));
		
		Solver mathSolver = new Solver(true, prefix+"_model_smoothed_0_001.ser", features);
		String problem =  "	Faye had 34 coloring books. If she gave away 3 of them, but then bought 48 more, how many would she have in total? ";
		
		System.out.println(mathSolver.solve(problem));
	}
}
