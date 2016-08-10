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
		String p1 = "C:\\Users\\Arindam\\git\\MathStudent\\cv2";
		String prefix="C:\\Users\\Arindam\\Dropbox\\Math Challenge\\sample_questions";
		ArrayList<String> features = new ArrayList<String>(FileUtils.readLines(
				new File(p1+"_features.txt")));
		
		Solver mathSolver = new Solver(true, p1+"_model_smoothed_0_001.ser", features);
		String problem =  "	Joan found 70 seashells on the beach . "
				+ "she gave Sam some of her seashells . "
				+ "She has 27 seashell . "
				+ "How many seashells did she give to Sam ?";
		
		System.out.println(mathSolver.solve(problem));
	}
}
