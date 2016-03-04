/**ThreeCrossValidation.java
 * 3:29:11 PM @author Arindam
 */
package nlp.app.math;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Arindam
 *
 */
public class ThreeCrossValidation {
	public static void main(String args[]) throws IOException{
		/**********************
		 * 3-Cross validation *
		 * ********************
		 */
		
		/********************************
		 * Read all the raw data
		 * ******************************
		 */
		String prefix="C:\\Users\\Arindam\\Dropbox\\Math Challenge\\Math_Word_DS_Kushman\\ALL\\all";
		String jsonString = FileUtils.readFileToString(new 
				File(prefix+".json"));
		JSONArray problems = new JSONArray(jsonString);
		
		/**********************
		 * Read partition 1,2,3
		 * ********************
		 */
		String partition1File = "C:\\Users\\Arindam\\Dropbox\\Math Challenge\\Math_Word_DS_Kushman\\fold1.txt";
		String partition2File = "C:\\Users\\Arindam\\Dropbox\\Math Challenge\\Math_Word_DS_Kushman\\fold2.txt";
		String partition3File = "C:\\Users\\Arindam\\Dropbox\\Math Challenge\\Math_Word_DS_Kushman\\fold3.txt";
		
		List<String> part1 = FileUtils.readLines(new File(partition1File));
		List<String> part2 = FileUtils.readLines(new File(partition2File));
		List<String> part3 = FileUtils.readLines(new File(partition3File));
		
		
		JSONArray fold1 = new JSONArray();
		JSONArray fold2 = new JSONArray();
		JSONArray fold3 = new JSONArray();
		
		for(Object problem: problems){
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
		
		
	}
}
