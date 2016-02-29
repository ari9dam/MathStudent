package nlp.app.math.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

/**
 * @author Arindam
 * Stores the math problem
 */
public class Problem {
	private String text;
	private Map<String,String> answers;
	private List<Equation> equations;
	private final ObjectMapper mapper = new ObjectMapper();
	
	public Problem(JSONObject obj){
		String qs = obj.getString("sQuestion");
		if(qs!=null){
			this.text = qs;
		}
		try {
			if(obj.has("lSolutions"))
				this.answers = mapper.readValue(obj.getString("lSolutions"),
					TypeFactory.defaultInstance().constructCollectionType(ArrayList.class,  
					   String.class));
			else
				this.answers = new HashMap<String,String>();
		} catch (IOException e) {
			this.answers = new HashMap<String,String>();
		}
		
		try {
			if(obj.has("lEquations"))
				this.equations = mapper.readValue(obj.getString("lEquations"),
					TypeFactory.defaultInstance().constructCollectionType(ArrayList.class,  
					   Equation.class));
			else
				this.equations = new ArrayList<Equation>();
		} catch (IOException e) {
			this.equations = new ArrayList<Equation>();
		}
	}
	
	public Problem(String problem){
		this(new JSONObject().put("sQuestion", problem));
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @return the answers
	 */
	public Map<String, String> getAnswers() {
		return answers;
	}

	/**
	 * @param answers the answers to set
	 */
	public void setAnswers(Map<String, String> answers) {
		this.answers = answers;
	}

	/**
	 * @return the equations
	 */
	public List<Equation> getEquations() {
		return equations;
	}

	/**
	 * @param equations the equations to set
	 */
	public void setEquations(List<Equation> equations) {
		this.equations = equations;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Problem [text=" + text + ", answers=" + answers + ", equations=" + equations + "]";
	}
	
	
}
