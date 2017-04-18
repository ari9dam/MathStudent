/**ThreeCrossValidation.java
 * 3:29:11 PM @author Arindam
 */
package nlp.app.math;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import nlp.app.conceptnet.ConceptNetCache;
import nlp.app.math.core.Problem;

/**
 * @author Arindam
 *
 */
public class ThreeCrossValidation {
	public static void main(String args[]) throws IOException, ClassNotFoundException{
		/**********************
		 * 3-Cross validation *
		 * ********************
		 * 
		 */

		/********************************
		 * Read all the raw data
		 * ******************************
		 */
		String prefix="src\\main\\resources\\data";
		String file = "src\\main\\resources\\data\\annotatedAddSub.json";
		String jsonString = FileUtils.readFileToString(new 
				File(file));//File(prefix+"_test.json"));//
		JSONArray testProblems = new JSONArray(jsonString);
		

		/**********************
		 * Read partition 1,2,3
		 * ********************
		 */
		String partition1File = prefix+"\\fold1.txt";
		String partition2File = prefix+"\\fold2.txt";
		String partition3File = prefix+"\\fold3.txt";

		List<String> part1 = FileUtils.readLines(new File(partition1File));
		List<String> part2 = FileUtils.readLines(new File(partition2File));
		List<String> part3 = FileUtils.readLines(new File(partition3File));


		JSONArray fold1 = new JSONArray();
		JSONArray fold2 = new JSONArray();
		JSONArray fold3 = new JSONArray();

		for(Object problem: testProblems){
			JSONObject p = (JSONObject) problem;
			if(part1.contains(p.get("iIndex").toString())){
				fold1.put(p);
			}else if(part2.contains(p.get("iIndex").toString())){
				fold2.put(p);
			}else if(part3.contains(p.get("iIndex").toString())){
				fold3.put(p);
			}else{
				System.out.println("This problem was not part of any fold.");
				System.out.println(p);
			}
		}

		System.out.println("Fold1 size = "+fold1.length());
		System.out.println("Fold2 size = "+fold2.length());
		System.out.println("Fold3 size = "+fold3.length());
		
		
		TrainingMain trainingMain = new TrainingMain();
		JSONArray td1 = new JSONArray(fold1.toString());
		ArrayList<String> features =null;
		/*	for(Object obj : fold2)
			td1.put(obj);
		trainingMain.train(td1, "cv3", testProblems);
		features = new ArrayList<String>(FileUtils.readLines(
				new File("cv3"+"_features.txt")));
		Solver solver1 = new Solver(false, "cv3"+"_model_smoothed_0_001.ser", features);
		test(fold3,solver1);*/
		
		/*td1 = new JSONArray(fold1.toString());
		for(Object obj : fold3)
			td1.put(obj);
		trainingMain.train(td1, "cv2", testProblems);
		features = new ArrayList<String>(FileUtils.readLines(
				new File("cv2"+"_features.txt")));
		Solver solver2 = new Solver(false, "cv2"+"_model_smoothed_0_001.ser", features);
		test(fold2,solver2);*/
		
		td1 = new JSONArray(fold2.toString());
		for(Object obj : fold3)
			td1.put(obj);
		trainingMain.train(td1, "cv1", testProblems);
		features = new ArrayList<String>(FileUtils.readLines(
				new File("cv1"+"_features.txt")));
		Solver solver3 = new Solver(false, "cv1"+"_model_smoothed_0_001.ser", features);
		test(fold1,solver3);
	}

	public static void test(JSONArray testProblems, Solver solver3) throws IOException{

		int total=0,correct2=0,exp=0;
		List<String> incorrect = new LinkedList<String>();
		List<String> excep = new LinkedList<String>();
		for(Object p : testProblems){
			JSONObject test = (JSONObject) p; 
			total++;


			try{
				Problem res3 = solver3.solve(test.getString("sQuestion"));
				ArrayList<String> answers = new ArrayList<String>(res3.getAnswers().values());
				if(answers.size()== 1){
					Double aans = Double.parseDouble((String) test.getJSONArray("lSolutions").get(0));
					Double cans = Double.parseDouble(answers.get(0));
					if(aans.equals(cans)){
						//System.out.println("correct");
						//System.out.println(res3);
						correct2++;
					}else{
						//System.out.println("incorrect");
						//System.out.println(res3);
						incorrect.add(res3.getText());
					}
				}

			}catch(Exception e){
				//e.printStackTrace();
				exp++;
				excep.add(test.getString("sQuestion"));
			}				
		}
		System.out.println("Total ="+total+", correct2 = "+correct2+",exception="+exp);
		FileUtils.writeLines(new File("incorrect_cv"+testProblems.length()+".txt"), incorrect);
		ConceptNetCache.getInstance().persist();
	}
}

