/**AnnotateCorrectWorld.java
 * 4:14:53 PM @author Arindam
 */
package nlp.app.math.util;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import nlp.app.math.core.ChangeConcept;
import nlp.app.math.core.ComparisionConcept;
import nlp.app.math.core.IMathConcept;
import nlp.app.math.core.MathSample;
import nlp.app.math.core.PartWholeConcept;
import nlp.app.math.core.ProblemRepresentation;
import nlp.app.math.core.Quantity;

/**
 * @author Arindam
 *
 */
public class AnnotateCorrectWorld {


	public AnnotateCorrectWorld(){

	}

	public void annotate(MathSample sample, JSONArray annotations, 
			ProblemRepresentation irep){

		for(Object a : annotations){
			JSONObject annotation = (JSONObject) a;
			IMathConcept c = this.createConceptFromJSON(annotation, irep);
			int i=0;
			for(IMathConcept concept: sample.getWorlds()){
				if(concept.equals(c)){
					sample.setCorrectY(i);
					break;
				}			
				i++;
			}
			
			if(i==sample.getWorlds().size()){
				System.out.println(irep);
			}
		}

	}

	public IMathConcept createConceptFromJSON(JSONObject object, 
			ProblemRepresentation irep){

		String type = object .getString("type");
		IMathConcept c = null;
		if(type.equalsIgnoreCase("CB")){
			c = this.createPartWhole(object,irep);
		}else if(type.equalsIgnoreCase("CH")){
			c = this.createChange(object, irep);
		}else if(type.equalsIgnoreCase("CP")){
			c = this.createCompare(object, irep);
		}else{
			System.out.println("wrong type\n"+object);
		}
		return c;
	}

	/**
	 * @param object
	 * @param irep
	 * @return
	 */
	private IMathConcept createCompare(JSONObject object, ProblemRepresentation irep) {
		Quantity large=null;
		Quantity small=null;
		Quantity diff=null;
		
		for(String key: object.keySet()){
			if(key.equalsIgnoreCase("type"))
				continue;

			JSONObject entry = object.getJSONObject(key);
			String name = entry.getString("name");
			if(name.equalsIgnoreCase("large")){
				String value = entry.getString("value");
				if(value.equalsIgnoreCase("X")){
					large = irep.getUnknownQuantities().get(0);
				}else{
					for(Quantity q: irep.getQuantities()){
						if(q.isUnknown())
							continue;
						if(q.getValue().equalsIgnoreCase(value)){
							large  = q;
							break;
						}

					}
				}
			}else if(name.equalsIgnoreCase("small")){
				String value = entry.getString("value");
				if(value.equalsIgnoreCase("X")){
					small = irep.getUnknownQuantities().get(0);
				}else{
					for(Quantity q: irep.getQuantities()){
						if(q.isUnknown())
							continue;
						if(q.getValue().equalsIgnoreCase(value)){
							small  = q;
							break;
						}

					}
				}
			}else if(name.equalsIgnoreCase("diff")){
				String value = entry.getString("value");
				if(value.equalsIgnoreCase("X")){
					diff = irep.getUnknownQuantities().get(0);
				}else{
					for(Quantity q: irep.getQuantities()){
						if(q.isUnknown())
							continue;
						if(q.getValue().equalsIgnoreCase(value)){
							diff  = q;
							break;
						}

					}
				}
			}
		}
		
		ComparisionConcept c = new ComparisionConcept(large, small, diff); 
		return c;
	}

	private IMathConcept createPartWhole(JSONObject object, ProblemRepresentation irep ){
		Quantity whole = null;
		Quantity part = null;
		PartWholeConcept  ppw = new PartWholeConcept();
		for(String key: object.keySet()){
			if(key.equalsIgnoreCase("type"))
				continue;

			JSONObject entry = object.getJSONObject(key);
			String name = entry.getString("name");
			if(name.equalsIgnoreCase("whole")){
				String value = entry.getString("value");
				if(value.equalsIgnoreCase("X")){
					whole = irep.getUnknownQuantities().get(0);
				}else{
					for(Quantity q: irep.getQuantities()){
						if(q.isUnknown())
							continue;
						if(q.getValue().equalsIgnoreCase(value)){
							whole  = q;
							break;
						}

					}
				}
				ppw.setWhole(whole);
			}else if(name.equalsIgnoreCase("part")){
				JSONArray values = entry.getJSONArray("value");
				for(Object v: values){
					String value = (String)v;
					if(value.equalsIgnoreCase("X")){
						part = irep.getUnknownQuantities().get(0);
					}else{
						for(Quantity q: irep.getQuantities()){
							if(q.isUnknown())
								continue;
							if(q.getValue().equalsIgnoreCase(value)){
								part  = q;
								break;
							}

						}
					}
					ppw.addPart(part);
				}
			}
		}
		return ppw;
	}

	private IMathConcept createChange(JSONObject object, ProblemRepresentation irep){

		Quantity start=null;
		Quantity end=null;
		List<Quantity> loss = new LinkedList<Quantity>();
		List<Quantity> gain = new LinkedList<Quantity>();

		for(String key: object.keySet()){
			if(key.equalsIgnoreCase("type"))
				continue;

			JSONObject entry = object.getJSONObject(key);
			String name = entry.getString("name");
			if(name.equalsIgnoreCase("start")){
				String value = entry.getString("value");
				if(value.equalsIgnoreCase("X")){
					start = irep.getUnknownQuantities().get(0);
				}else if(value.equalsIgnoreCase("default")){
					start = new Quantity("0",-1,-1);
					start.setDefault(true);
				}else{
					for(Quantity q: irep.getQuantities()){
						if(q.getValue().equalsIgnoreCase(value)){
							start  = q;
							break;
						}
					}
				}
			}if(name.equalsIgnoreCase("end")){
				String value = entry.getString("value");
				if(value.equalsIgnoreCase("X")){
					end = irep.getUnknownQuantities().get(0);
				}else{
					for(Quantity q: irep.getQuantities()){
						if(q.getValue().equalsIgnoreCase(value)){
							end  = q;
							break;
						}
					}
				}
			}else if(name.equalsIgnoreCase("loss")){
				JSONArray values = entry.getJSONArray("value");
				for(Object v: values){
					String value = (String)v;
					if(value.equalsIgnoreCase("X")){
						loss.add(irep.getUnknownQuantities().get(0));
					}else{
						for(Quantity q: irep.getQuantities()){
							if(q.getValue().equalsIgnoreCase(value)){
								loss.add(q);
								break;
							}
						}
					}
				}
			}else if(name.equalsIgnoreCase("gain")){
				JSONArray values = entry.getJSONArray("value");
				for(Object v: values){
					String value = (String)v;
					if(value.equalsIgnoreCase("X")){
						gain.add(irep.getUnknownQuantities().get(0));
					}else{
						for(Quantity q: irep.getQuantities()){
							if(q.getValue().equalsIgnoreCase(value)){
								gain.add(q);
								break;
							}
						}
					}
				}
			}
		}
		ChangeConcept  ch = new ChangeConcept(start, end, gain, loss);
		return ch;
	}
}
