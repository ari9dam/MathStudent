/**ConceptNetSearch.java
 * 12:58:31 PM @author Arindam
 */
package nlp.app.conceptnet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONObject;

/**
 * @author Arindam
 *
 */
public class ConceptNetSearch {
	private final String searchURL = "http://conceptnet5.media.mit.edu/data/5.4/search";
	public boolean search(JSONObject param) throws IOException{
		
		String urlStr = this.searchURL+"?";
		 
		  for (String key: param.keySet()) {
			  urlStr+=key;
			  urlStr += "=";
			  urlStr+=URLEncoder.encode(param.getString(key),"UTF-8");
		      urlStr+="&";
		  }
		  
		  URL url = new URL(urlStr);
		  HttpURLConnection conn =
		      (HttpURLConnection) url.openConnection();
		  
		  if (conn.getResponseCode() != 200) {
		    throw new IOException(conn.getResponseMessage());
		  }

		  // Buffer the result into a string
		  BufferedReader rd = new BufferedReader(
		      new InputStreamReader(conn.getInputStream()));
		  StringBuilder sb = new StringBuilder();
		  String line;
		  while ((line = rd.readLine()) != null) {
		    sb.append(line);
		  }
		  rd.close();
		  conn.disconnect();
		  
		  JSONObject response = new JSONObject(sb.toString());
		  if(response.has("numFound")&& response.getInt("numFound")>0)
			  return true;
		 
		  System.out.println(response);
		return false;
	}
	public static void main(String[] args) throws IOException {
		JSONObject object = new JSONObject();
		object.put("start", "/c/en/buy");
		object.put("end", "/c/en/pay");
		object.put("limit", "1");
		
		ConceptNetSearch cn = new ConceptNetSearch();
		cn.search(object);

	}

}
