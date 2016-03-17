/**ChangeConcept.java
 * 8:07:26 PM @author Arindam
 */
package nlp.app.math.core;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Arindam
 *
 */
public class ChangeConcept implements IMathConcept {
	private Quantity start;
	private Quantity end;
	private List<Quantity> gains;
	private List<Quantity> losses;


	public ChangeConcept(Quantity start, Quantity end, List<Quantity> gains, 
			List<Quantity> losses) {
		this.start = start;
		this.end = end;
		this.gains = gains;
		this.losses = losses;
	}

	public Quantity getStart() {
		return start;
	}


	public Quantity getEnd() {
		return end;
	}


	public List<Quantity> getGains() {
		return gains;
	}


	public List<Quantity> getLosses() {
		return losses;
	}

	@Override
	public Equation toEquation() {
		String leq= this.start.getValueForEquation();
		String req = this.end.getValueForEquation();
		
		for(Quantity gain:this.getGains()){
			leq+=" + " + gain.getValueForEquation();
		}
		
		for(Quantity loss:this.getLosses()){
			req+=" + " + loss.getValueForEquation();
		}
		
		Equation equation = new Equation(leq+" = "+req);
		return equation;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		result = prime * result + ((gains == null) ? 0 : gains.hashCode());
		result = prime * result + ((losses == null) ? 0 : losses.hashCode());
		result = prime * result + ((start == null) ? 0 : start.hashCode());
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
		ChangeConcept other = (ChangeConcept) obj;
		if (end == null) {
			if (other.end != null)
				return false;
		} else if (!end.equals(other.end))
			return false;
		if (gains == null) {
			if (other.gains != null)
				return false;
		} else if (!setEquals(gains,other.gains))
			return false;
		if (losses == null) {
			if (other.losses != null)
				return false;
		} else if (!setEquals(losses,other.losses))
			return false;
		if (start == null) {
			if (other.start != null)
				return false;
		} else if (!start.equals(other.start))
			return false;
		return true;
	}

	
	/**
	 * @param losses2
	 * @param losses3
	 * @return
	 */
	private boolean setEquals(List<Quantity> a, List<Quantity> b) {
		Set<Quantity> l = new HashSet<Quantity>(a);
		Set<Quantity> r = new HashSet<Quantity>(b);
		return l.equals(r);
	}

	@Override
	public String toString() {
		String gain="";
		for(Quantity g:gains){
			gain+=g.getValueForEquation()+",";
		}
		
		String loss="";
		for(Quantity g:losses){
			loss+=g.getValueForEquation()+",";
		}
		
		return "ChangeConcept [\nstart=" + start.getValueForEquation() + ", \nend=" + end.getValueForEquation() + ", gains=" + gain + ", \nlosses=" + loss + "]";
	}
	
	public JSONObject toJSON(){
		
		JSONObject ret = new JSONObject();
		JSONObject arg1 = new JSONObject();
		JSONObject arg2 = new JSONObject();
		JSONObject arg3 = new JSONObject();
		JSONObject arg4 = new JSONObject();
		
		JSONArray  loss = new JSONArray();
		JSONArray  gain = new JSONArray();
		
		arg1.put("name", "start");
		if(this.start.isUnknown())
			arg1.put("value", "X");
		else if(this.start.isDefault())
			arg1.put("value", "DEFAULT");
		else arg1.put("value", this.start.getValue());
		
		arg4.put("name", "end");
		if(this.end.isUnknown())
			arg4.put("value", "X");
		else arg4.put("value", this.end.getValue());
		
		arg2.put("name", "loss");
		for (Quantity q : this.losses){
			if(q.isUnknown())
				loss.put("X");
			else loss.put(q.getValue());
		}
		arg2.put("value", loss);
		
		arg3.put("name", "gain");
		for (Quantity q : this.gains){
			if(q.isUnknown())
				gain.put("X");
			else gain.put(q.getValue());
		}
		arg3.put("value", gain);
		
		ret.put("arg1", arg1);
		ret.put("arg2", arg2);
		ret.put("arg3", arg3);
		ret.put("arg4", arg4);
		
		ret.put("type", "CH");
		
		return ret;
	}

}
