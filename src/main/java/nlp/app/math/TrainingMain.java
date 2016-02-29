/**TrainingMain.java
 * 12:37:39 AM @author Arindam
 */
package nlp.app.math;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;

/**
 * @author Arindam
 *
 */
public class TrainingMain {
	public static void main(String args[]) throws IOException, ClassNotFoundException{
		/********************************
		 * Read the raw data
		 * ******************************
		 */
		String prefix="C:\\Users\\Arindam\\Dropbox\\Math Challenge\\sample_questions";
		
		String jsonString = FileUtils.readFileToString(new 
				File(prefix+".json"));
		JSONArray problems = new JSONArray(jsonString);
		jsonString = FileUtils.readFileToString(new 
				File(prefix+"_test.json"));
		JSONArray testProblems = new JSONArray(jsonString);
		
		TrainingMain trainingMain = new TrainingMain();
		trainingMain.train(problems, prefix, testProblems);
	}
	
	public void train(JSONArray problems, String prefix, JSONArray testProblems) 
			throws FileNotFoundException, IOException, ClassNotFoundException{
		/************************************
		 * Prepare the raw data for training
		 * **********************************
		 */
		TrainSolver tsolver = new TrainSolver(false);
		tsolver.prepareTrainingData(problems,
				prefix);
		
		/************************************
		 * estimate the parameters
		 * 1. without smoothing
		 * 2. with smoothing, 0.01
		 * 3. with smoothing, 0.001
		 ************************************
		 */
	/*	tsolver.train(false,0.1,null,
				prefix+"_training_data.ser",
				prefix+"_model_unsmoothed.ser");*/
		
		tsolver.train(true,0.1,0.01,
				prefix+"_training_data.ser",
				prefix+"_model_smoothed_0_01.ser");
		
		System.out.println("Done!!");
		
		tsolver.train(true,0.1,0.001,
				prefix+"_training_data.ser",
				prefix+"_model_smoothed_0_001.ser");
		
		System.out.println("Done!!");
		
		/************************************
		 * testing model
		 * 1. without smoothing
		 * 2. with smoothing, 0.01
		 * 3. with smoothing, 0.001
		 ************************************
		 *//*
		ArrayList<String> features = new ArrayList<String>(FileUtils.readLines(
				new File(prefix+"_features.txt")));
		int count1=0,count2=0,count3=0;
		//Solver solver1 = new Solver(false, prefix+"_model_unsmoothed.ser", features);
		Solver solver2 = new Solver(false, prefix+"_model_smoothed_0_01.ser", features);
		Solver solver3 = new Solver(false, prefix+"_model_smoothed_0_001.ser", features);
		for(Object p : testProblems){
			JSONObject test = (JSONObject) p; 
			//Problem res1 = solver1.solve(test.getString("sQuestion"));
			Problem res2 = solver2.solve(test.getString("sQuestion"));
			Problem res3 = solver3.solve(test.getString("sQuestion"));
			
			//System.out.println(res1);
			System.out.println(res2);
			System.out.println(res3);
		}*/
	}
}
