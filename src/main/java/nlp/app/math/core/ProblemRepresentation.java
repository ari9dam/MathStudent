package nlp.app.math.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import edu.asu.nlu.common.ds.AnnotatedSentence;

/**
 * @author Arindam
 *
 */
public class ProblemRepresentation {
	private String text;
	private ArrayList<AnnotatedSentence> annotatedSentences; 
	private Integer numberOfunknowns;
	private Integer nuberOfQuantities;
	private Map<Integer,Quantity> quantityMap;
	private Map<Integer,Quantity> unknownMap;
	private List<IMathConcept> mathConcepts;
	private Set<Equation> equations;
	private String id;
	private Map<String,Boolean> corefMap;
	
	/**
	 * @param text
	 */
	public ProblemRepresentation(String text) {
		this.text = text;
		this.nuberOfQuantities  = 0;
		this.numberOfunknowns = 0;
		this.quantityMap = new HashMap<Integer,Quantity>();
		this.unknownMap = new HashMap<Integer,Quantity>();
		this.equations = new HashSet<Equation>();
		this.mathConcepts = new ArrayList<IMathConcept>();
		this.corefMap = new HashMap<String,Boolean>();
	}
	
	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @return the numberOfunknowns
	 */
	public int getNumberOfunknowns() {
		return numberOfunknowns;
	}

	/**
	 * @return the nuberOfQuantities
	 */
	public int getNuberOfQuantities() {
		return nuberOfQuantities;
	}


	/**
	 * @return the equations
	 */
	public Set<Equation> getEquations() {
		return equations;
	}
	/**
	 * @param equations the equations to set
	 */
	public void setEquations(Set<Equation> equations) {
		this.equations = equations;
	}
	
	public List<IMathConcept> getMathConcepts(){
		return this.mathConcepts;
	}
	
	public void addMathConcept(IMathConcept concept){
		this.mathConcepts.add(concept);
	}
	
	
	/**
	 * @return
	 */
	public List<String> getUnknowns() {
		List<String> unknowns = new LinkedList<String>();
		for(Entry<Integer, Quantity> entry: unknownMap.entrySet()){
			unknowns.add(entry.getValue().getUnknownId());
		}
		return unknowns;
	}
	
	/**
	 * @return the annotatedSentences
	 */
	public ArrayList<AnnotatedSentence> getAnnotatedSentences() {
		return annotatedSentences;
	}
	
	/**
	 * @param annotatedSentences the annotatedSentences to set
	 */
	public void setAnnotatedSentences(ArrayList<AnnotatedSentence> annotatedSentences) {
		this.annotatedSentences = annotatedSentences;
	}
	
	/**
	 * @param word
	 * @return the id for that constant quantity
	 */
	public Quantity addConstantQuantity(String value, int sId, int tokenId) {
		this.nuberOfQuantities++;
		Quantity quantity = new Quantity(value, sId, tokenId);
		this.quantityMap.put(nuberOfQuantities, 
				quantity);
		return quantity;
	}

	/**
	 * @param word
	 * @return the id for that constant quantity
	 */
	public Quantity addUnknown(int sId, int tokenId) {
		this.numberOfunknowns++;
		Quantity q = new Quantity(null, sId, tokenId);
		q.setUnknownId("x"+numberOfunknowns);
		this.unknownMap.put(numberOfunknowns,q);
		return q;
	}


	
	public List<Quantity> getQuantities(){
		List<Quantity> q = new LinkedList<Quantity>();
		q.addAll(this.quantityMap.values());
		q.addAll(this.unknownMap.values());
		return q;
	}
	
	public List<Quantity> getUnknownQuantities(){
		return new ArrayList<Quantity>(this.unknownMap.values());
	}
	
	@Override
	public String toString() {
		return "ProblemRepresentation [text=" + text + ",\n numberOfunknowns=" + numberOfunknowns + ",\n nuberOfQuantities="
				+ nuberOfQuantities + ",\n quantityMap=" + quantityMap + ",\n unknownMap=" + unknownMap + "]";
	}


	public String getId() {
		
		return id;
	}
	

	public void setId(String id) {
		this.id = id;
	}


	public void addCoref(int sId1, int tId1, int s, int t) {
		this.corefMap.put(sId1+":"+tId1+":"+s+":"+t,true);
	}
	
	public Boolean isCoref(int sId1, int tId1, int s, int t){
		return this.corefMap.containsKey(sId1+":"+tId1+":"+s+":"+t);
	}
	
}
