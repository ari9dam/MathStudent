package nlp.app.math.corpus;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import nlp.app.math.app.EquationSolver;
import nlp.app.math.app.FormEquations;
import nlp.app.math.core.ChangeConcept;
import nlp.app.math.core.ComparisionConcept;
import nlp.app.math.core.IMathConcept;
import nlp.app.math.core.PartWholeConcept;
import nlp.app.math.core.ProblemRepresentation;
import nlp.app.math.core.Quantity;

/**
 * @author Arindam
 *
 */
public class TestCorpusAnnotation {
	FormEquations equationGenerator = new FormEquations();
	EquationSolver equationSolver = new EquationSolver();

	public void test() throws Exception{

		String jsonString = FileUtils.readFileToString(new File("arith_addsub_annotated.json"));
		JSONArray problems = new JSONArray(jsonString);

		for(Object obj: problems){
			JSONObject prob = (JSONObject)obj;
			boolean result = check(prob);
			if(!result)
				System.out.println(obj);
		}
	}

	public boolean check(JSONObject prob) throws Exception{

		String t = prob.getString("sQuestion");
		JSONArray s = prob.getJSONArray("lSolutions");
		JSONArray concepts = prob.getJSONArray("semantics");
		if(concepts.length()>1)
			return false;

		ProblemRepresentation irep = new ProblemRepresentation(t);
		try{
			for(IMathConcept c: createConceptsFromJSON(concepts,irep)){
				irep.addMathConcept(c);
			}

			Map<String, String> result = equationSolver.solve(equationGenerator.formEquations(irep), 
					irep.getUnknowns());
			if(result.size()==1){
				for(Entry<String, String> entry: result.entrySet()){
					if(Double.parseDouble(entry.getValue()) == Double.parseDouble(s.getString(0))){
						return true;
					}else{
						return false;
					}
				}
			}else{
				return false;
			}
		}catch(Exception e){
			System.out.println(prob);
			throw e;
		}

		return false;
	}
	private Set<IMathConcept> createConceptsFromJSON(JSONArray concepts, 
			ProblemRepresentation irep){
		Set<IMathConcept> out = new HashSet<IMathConcept>();
		for(Object obj : concepts){
			JSONObject object = (JSONObject)obj;

			IMathConcept c = createConceptFromJSON(object,irep);
			if(c!=null)
				out.add(c);
		}
		return out;
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
			c = this.createComparision(object, irep);
		}else{
			System.out.println("wrong type\n"+object);
		}
		return c;
	}

	private IMathConcept createPartWhole(JSONObject object, ProblemRepresentation irep ){
		int i=0;
		Quantity whole;
		Quantity part;
		PartWholeConcept  ppw = new PartWholeConcept();
		for(String key: object.keySet()){
			if(key.equalsIgnoreCase("type"))
				continue;

			JSONObject entry = object.getJSONObject(key);
			String name = entry.getString("name");
			if(name.equalsIgnoreCase("whole")){
				String value = entry.getString("value");
				if(value.equalsIgnoreCase("X")){
					whole = irep.addUnknown(1, ++i);
				}else{
					whole = irep.addConstantQuantity(value, 1, ++i);
				}
				ppw.setWhole(whole);
			}else if(name.equalsIgnoreCase("part")){
				JSONArray values = entry.getJSONArray("value");
				for(Object v: values){
					String value = (String)v;
					if(value.equalsIgnoreCase("X")){
						part = irep.addUnknown(1, ++i);
					}else{
						part = irep.addConstantQuantity(value, 1, ++i);
					}
					ppw.addPart(part);
				}
			}
		}
		return ppw;
	}

	private IMathConcept createChange(JSONObject object, ProblemRepresentation irep){
		int i=0;
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
					start = irep.addUnknown(1, ++i);
				}else if(value.equalsIgnoreCase("default")){
					start = irep.addConstantQuantity("0", 1, ++i);
				}else{
					start = irep.addConstantQuantity(value, 1, ++i);
				}
			}if(name.equalsIgnoreCase("end")){
				String value = entry.getString("value");
				if(value.equalsIgnoreCase("X")){
					end = irep.addUnknown(1, ++i);
				}else{
					end = irep.addConstantQuantity(value, 1, ++i);
				}
			}else if(name.equalsIgnoreCase("loss")){
				JSONArray values = entry.getJSONArray("value");
				for(Object v: values){
					String value = (String)v;
					if(value.equalsIgnoreCase("X")){
						loss.add(irep.addUnknown(1, ++i));
					}else{
						loss.add(irep.addConstantQuantity(value, 1, ++i));
					}
				}
			}else if(name.equalsIgnoreCase("gain")){
				JSONArray values = entry.getJSONArray("value");
				for(Object v: values){
					String value = (String)v;
					if(value.equalsIgnoreCase("X")){
						gain.add(irep.addUnknown(1, ++i));
					}else{
						gain.add(irep.addConstantQuantity(value, 1, ++i));
					}
				}
			}
		}
		ChangeConcept  ch = new ChangeConcept(start, end, gain, loss);
		return ch;
	}
	
	private IMathConcept createComparision(JSONObject object, ProblemRepresentation irep){
		int i=0;
		Quantity large=null;
		Quantity small=null;
		Quantity diff = null;

		for(String key: object.keySet()){
			if(key.equalsIgnoreCase("type"))
				continue;

			JSONObject entry = object.getJSONObject(key);
			String name = entry.getString("name");
			if(name.equalsIgnoreCase("large")){
				String value = entry.getString("value");
				if(value.equalsIgnoreCase("X")){
					large = irep.addUnknown(1, ++i);
				}else{
					large = irep.addConstantQuantity(value, 1, ++i);
				}
			}if(name.equalsIgnoreCase("small")){
				String value = entry.getString("value");
				if(value.equalsIgnoreCase("X")){
					small = irep.addUnknown(1, ++i);
				}else{
					small = irep.addConstantQuantity(value, 1, ++i);
				}
			}else if(name.equalsIgnoreCase("diff")){
				String value = entry.getString("value");
				if(value.equalsIgnoreCase("X")){
					diff = irep.addUnknown(1, ++i);
				}else{
					diff = irep.addConstantQuantity(value, 1, ++i);
				}
			}
		}
		ComparisionConcept  ch = new ComparisionConcept(large, small, diff);
		return ch;
	}


	public static void main(String args[]) throws Exception{
		TestCorpusAnnotation test  = new TestCorpusAnnotation();
		test.test();
	}
}
