package nlp.app.math.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Arindam
 *
 */
public class MathSample {
	private Integer correctY;
	private ArrayList<IMathConcept> worlds;
	private ArrayList<Map<String,Double>> featureMaps;
	private String id;
	private List<Quantity> quantities;
	
	public MathSample() {
		this(null);
	}
	
	public MathSample(String id) {
		this.id = id;
		this.worlds = new ArrayList<IMathConcept>();
		this.featureMaps = new ArrayList<Map<String,Double>>();
	}
	
	public void setCorrectY(int y){
		this.correctY = y;
	}
	
	public Integer getCorrectY(){
		return this.correctY;
	}

	/**
	 * @return the worlds
	 */
	public ArrayList<IMathConcept> getWorlds() {
		return worlds;
	}

	/**
	 * @param worlds the world to add
	 */
	public void addWorld(IMathConcept world) {
		this.worlds.add(world);
	}

	/**
	 * @return the featureMaps
	 */
	public ArrayList<Map<String, Double>> getFeatureMaps() {
		return featureMaps;
	}

	/**
	 * @param featureMap the featureMap to add
	 */
	public void addFeatureMap(Map<String, Double> featureMap) {
		this.featureMaps.add(featureMap);
	}
	

	/**
	 * @return
	 */
	public int numberOfPossibleWorlds() {
		return this.worlds.size();
	}

	/**
	 * @param y
	 * @return
	 */
	public IMathConcept getWorld(int y) {
		return this.worlds.get(y);
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the quantities
	 */
	public List<Quantity> getQuantities() {
		return quantities;
	}

	/**
	 * @param quantities the quantities to set
	 */
	public void setQuantities(List<Quantity> quantities) {
		this.quantities = quantities;
	}
	
}
