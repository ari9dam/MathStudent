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
public class TestMain {
	public static void main(String args[]) throws IOException, ClassNotFoundException{
		String prefix="C:\\Users\\Arindam\\Dropbox\\Math Challenge\\sample_questions";
		String file = "C:\\Users\\Arindam\\Dropbox\\Math Challenge\\Math_Word_DS_Kushman\\ALL\\arith_me.json";
		String jsonString = FileUtils.readFileToString(new 
				File(prefix+"_test.json"));//File(file));//
		JSONArray testProblems = new JSONArray(jsonString);
		ArrayList<String> features = new ArrayList<String>(FileUtils.readLines(
				new File(prefix+"_features.txt")));
		//Solver solver1 = new Solver(false, prefix+"_model_unsmoothed.ser", features);
		Solver solver2 = new Solver(false, prefix+"_model_smoothed_0_01.ser", features);
		Solver solver3 = new Solver(false, prefix+"_model_smoothed_0_001.ser", features);
		int correct1=0,total=0,correct2=0,exp=0;
		List<String> incorrect = new LinkedList<String>();
		List<String> excep = new LinkedList<String>();
		for(Object p : testProblems){
			JSONObject test = (JSONObject) p; 
			total++;
/*			try{
				Problem res2 = solver2.solve(test.getString("sQuestion"));
				ArrayList<String> answers = new ArrayList<String>(res2.getAnswers().values());
				if(answers.size()== 1){
					Double aans = Double.parseDouble((String) test.getJSONArray("lSolutions").get(0));
					Double cans = Double.parseDouble(answers.get(0));
					if(aans.equals(cans)){
						correct1++;
					}else{
						System.out.println(res2);
					}
				}else{
					System.out.println(res2);
				}
				
			}catch(Exception e){
				System.out.println(e);
			}*/
			
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
						System.out.println(res3);
						incorrect.add(res3.getText());
					}
				}
				
			}catch(Exception e){
				e.printStackTrace();
				exp++;
				excep.add(test.getString("sQuestion"));
			}				
		}
		System.out.println("Total ="+total+", correct1 = "+correct1+", correct2 = "+correct2+",exception="+exp);
		//FileUtils.writeLines(new File("incorrect.txt"), incorrect);
		//FileUtils.writeLines(new File("exception.txt"), excep);
		ConceptNetCache.getInstance().persist();
	}
}
