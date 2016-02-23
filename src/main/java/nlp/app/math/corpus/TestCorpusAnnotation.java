package nlp.app.math.corpus;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import nlp.app.math.app.EquationSolver;
import nlp.app.math.app.FormEquations;
import nlp.app.math.core.ChangeConcept;
import nlp.app.math.core.IMathConcept;
import nlp.app.math.core.PartWholeConcept;
import nlp.app.math.core.ProblemRepresentation;
import nlp.app.math.core.Quantity;

/**
 * @author Arindam
 *
 */
public class TestCorpusAnnotation {
	public void test() throws IOException{
		FormEquations equationGenerator = new FormEquations();
		EquationSolver equationSolver = new EquationSolver();
		String jsonString = FileUtils.readFileToString(new 
				File("C:\\Users\\Arindam\\Dropbox\\Math Challenge\\test_math.json"));
		JSONArray problems = new JSONArray(jsonString);

		for(Object obj: problems){
			JSONObject prob = (JSONObject)obj;
			String t = prob.getString("sQuestion");
			JSONArray s = prob.getJSONArray("lSolutions");
			JSONArray concepts = prob.getJSONArray("semantics");
			if(concepts.length()>1)
				continue;
			ProblemRepresentation irep = new ProblemRepresentation(t);
			for(IMathConcept c: createConceptFromJSON(concepts,irep)){
				irep.addMathConcept(c);
			}
			try{
				Map<String, String> result = equationSolver.solve(equationGenerator.formEquations(irep), 
						irep.getUnknowns());
				if(result.size()==1){
					for(Entry<String, String> entry: result.entrySet()){
						if(Double.parseDouble(entry.getValue()) == Double.parseDouble(s.getString(0))){
							//correct
						}else{
							System.out.println("Solution mismatch");
							System.out.println(obj);
						}
					}
				}else{
					System.out.println("More than one Solution");
					System.out.println(obj);
				}
			}catch(Exception e){
				System.out.println(e);
				System.out.println(prob);
			}
		}
	}

	private Set<IMathConcept> createConceptFromJSON(JSONArray concepts, 
			ProblemRepresentation irep){
		Set<IMathConcept> out = new HashSet<IMathConcept>();
		for(Object obj : concepts){
			JSONObject object = (JSONObject)obj;
			String type = object .getString("type");
			IMathConcept c = null;
			if(type.equalsIgnoreCase("CB")){
				c = this.createPartWhole(object,irep);
			}else if(type.equalsIgnoreCase("CH")){
				c = this.createChange(object, irep);
			}else{
				System.out.println("wrong type\n"+obj);
			}
			out.add(c);
		}
		return out;
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
		Set<Quantity> loss = new HashSet<Quantity>();
		Set<Quantity> gain = new HashSet<Quantity>();

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

	public static void main(String args[]) throws IOException{
		TestCorpusAnnotation test  = new TestCorpusAnnotation();
		test.test();
	}
}
