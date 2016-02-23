package nlp.app.math.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nlp.app.math.core.Equation;

/**
 * @author Arindam
 *
 */
public class EquationSolver {
	final String path = ""; 
	
	public Map<String,String> solve(List<Equation> equations, List<String> unknowns) throws IOException{
		Map<String,String> ret = new HashMap<String,String>();
		List<String> eqns = new LinkedList<String>();
		
		if(unknowns.isEmpty()||equations.isEmpty())
			return ret;
		
		for(Equation e: equations){
			eqns.add(e.getEquation());
		}
		
		String line;
		Runtime r = Runtime.getRuntime();
		Process p = r.exec(String.format("cmd /c maxima -r \'0;solve([%s],[%s]);\n'", String.join(",", eqns), String.join(",", unknowns)),null,
				new File("C:/Program Files (x86)/Maxima-sbcl-5.37.2/bin/"));
		//System.out.println("Calling equation solver with: "+
			//	equations.toString() +"and" + unknowns.toString() );
		BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
		Double numerator = null, denom = null;  
		boolean ratio = false;
		String var = "";
		while ((line = input.readLine()) != null) {
			
		    line  = line.replaceAll("\\[\\[", "\\[").replaceAll("\\]\\]", "\\]");
		    if(line.contains("[") && line.contains("]")){
		    	String values = line.substring(line.indexOf('[')+1, line.lastIndexOf(']'));
		    	String[] vals = values.split(",");
		    	for(String keyVal : vals){
		    		String[] kv = keyVal.split("=");
		    		if(!kv[1].trim().matches("[- ]+"))
		    			ret.put(kv[0].trim(), kv[1].replaceAll(" ",""));
		    		else if(numerator!=null) {
		    			ratio =true;
		    			var = kv[0].trim();
		    			if(kv[1].trim().contains(" "))
		    				numerator = -1* numerator;
		    		}
		    	}
		    }else if(line.trim().matches("\\d+")){
		    	if(numerator==null)
		    		numerator = Double.parseDouble(line.trim());
		    	else if(denom==null && ratio){
		    		denom = Double.parseDouble(line.trim());
		    		if(denom!=null && denom!=0 ){
		    		Double val = numerator/denom;
	    				ret.put(var, val.toString());
		    		}
		    	}
		    }
		  }
		input.close();
		//System.out.println(ret);
		return ret;
	}
	
	public static void main(String args[]) throws IOException{
		EquationSolver solver = new EquationSolver();
		List<Equation> eqns = new LinkedList<Equation>();
		eqns.add(new Equation("2.2=1.7+x1"));

		
		List<String> unknowns = new LinkedList<String>();
		unknowns.add("x1");
		//unknowns.add("x2");
		solver.solve(eqns, unknowns);
	}
}
