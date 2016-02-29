package nlp.app.math.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.asu.nlu.common.ds.AnnotatedSentence;
import edu.stanford.nlp.ling.CoreLabel;

/**
 * @author Arindam
 *
 */
public class Quantity {
	private String value;
	private ArrayList<CoreLabel> type;
	private Integer SentenceId;
	private Integer tokenId;
	private boolean isUnknown;
	private String unknownId;
	private Map<String, List<CoreLabel>> context;
	private boolean isDefault = false;
	
	public Quantity(String value, Integer sentenceId, Integer tokenId) {
		this.value = value;
		SentenceId = sentenceId;
		this.tokenId = tokenId;
		this.isUnknown =  value==null;
		this.context = new HashMap<String, List<CoreLabel>>();
		this.isDefault = false;
	}
	/**
	 * @return the type
	 */
	public ArrayList<CoreLabel> getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(ArrayList<CoreLabel> type) {
		this.type = type;
	}
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @return the sentenceId
	 */
	public Integer getSentenceId() {
		return SentenceId;
	}
	/**
	 * @return the tokenId
	 */
	public Integer getTokenId() {
		return tokenId;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((SentenceId == null) ? 0 : SentenceId.hashCode());
		result = prime * result + ((tokenId == null) ? 0 : tokenId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Quantity other = (Quantity) obj;
		if (SentenceId == null) {
			if (other.SentenceId != null)
				return false;
		} else if (!SentenceId.equals(other.SentenceId))
			return false;
		if (tokenId == null) {
			if (other.tokenId != null)
				return false;
		} else if (!tokenId.equals(other.tokenId))
			return false;
		return true;
	}
	/**
	 * @return the isUnknown
	 */
	public boolean isUnknown() {
		return isUnknown;
	}
	/**
	 * @param isUnknown the isUnknown to set
	 */
	public void setUnknown(boolean isUnknown) {
		this.isUnknown = isUnknown;
	}
	/**
	 * @return
	 */
	public String getUnknownId() {
		
		return this.unknownId;
	}
	
	public void setUnknownId(String id){
		this.unknownId = id;
	}
	
	public void setContext(String rel, Set<Integer> words, AnnotatedSentence s) {
		List<CoreLabel> labels = new ArrayList<CoreLabel>();
		for(Integer i: words){
			labels.add(s.getToken(i));
		}
		this.context.put(rel, labels);
	}
	
	@Override
	public String toString() {
		return "\nQuantity [value=" + value + ",\n type=" + type + ",\n SentenceId=" + SentenceId + ",\n tokenId=" + tokenId
				+ ",\n isUnknown=" + isUnknown + ",\n unknownId=" + unknownId + ",\n context=" + context + "]";
	}

	public List<CoreLabel> getAssociatedEntity(String rel) {
		
		return this.context.get(rel);
	}
	
	public String getValueForEquation() {
		//if it's a quantity return the value from the map
		if(!this.isUnknown())
			return this.getValue();
			
		//else return the key itself. e.g. "X"
		return this.getUnknownId();
	}
	/**
	 * @return the isDefault
	 */
	public boolean isDefault() {
		return isDefault;
	}
	/**
	 * @param isDefault the isDefault to set
	 */
	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}
	
	public String getUniqueId(){
		return "@"+ this.SentenceId + "@"+ this.tokenId;
	}
	
	public Double getDoubleValue(){
		if(this.isUnknown)
			return null;
		return Double.parseDouble(this.value);
	}
}
