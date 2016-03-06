/**VerbPolarityHelper.java
 * 9:15:40 PM @author Arindam
 */
package nlp.app.math.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import edu.stanford.nlp.ling.CoreLabel;
import nlp.app.math.core.ChangeConcept;
import nlp.app.math.core.IMathConcept;
import nlp.app.math.core.MathSample;
import nlp.app.math.core.ProblemRepresentation;
import nlp.app.math.core.Quantity;

/**
 * @author Arindam
 *
 */
public class VerbPolarityHelper {
	private Map<String, Double> verbPolarityMap;
	private static final String filename = "src\\main\\resources\\verb_polarity.txt";
	private static VerbPolarityHelper helper = null;
	private VerbPolarityHelper(String filename) throws IOException{
		this.verbPolarityMap = new HashMap<String,Double>();
		File f = new File(filename);
		for(String line: FileUtils.readLines(f)){
			String[] keyval = line.split("\t");
			this.verbPolarityMap.put(keyval[0], Double.parseDouble(keyval[1]));
		}
		
	}
	
	public double getPolarity(String verb){
		if(this.verbPolarityMap.containsKey(verb))
			return this.verbPolarityMap.get(verb);
		return 0;
	}
	
	public void persist() throws IOException{
		List<String> list = new LinkedList<String>();
		for(Entry<String, Double> entry: this.verbPolarityMap.entrySet()){
			list.add(entry.getKey()+"\t"+entry.getValue());
		}
		
		FileUtils.writeLines(new File(filename), list);
	}
	
	public static VerbPolarityHelper getInstance() throws RuntimeException{
		if(helper==null){
			try{
				helper = new VerbPolarityHelper(filename);
			}catch(IOException e){
				throw new RuntimeException(e);
			}
		}
		return helper;
	}
	
	public void add(ProblemRepresentation irep, MathSample sample){
		IMathConcept concept = sample.getWorld(sample.getCorrectY());
		
		if(concept instanceof ChangeConcept ){
			ChangeConcept chc = (ChangeConcept) concept;
			Quantity end  = chc.getEnd();
			List<CoreLabel> endsubj = end.getAssociatedEntity("nsubj");
			Set<String> ensj = new HashSet<String>();
			for(CoreLabel l: endsubj){
				ensj.add(l.lemma());
			}
				
			/*
			 * if the subj of a question equals the subj of the verb accept else reverse 
			 */
			for(Quantity q: chc.getGains()){
				List<CoreLabel> nsubj = q.getAssociatedEntity("nsubj");
				Set<String> qsj = new HashSet<String>();
				for(CoreLabel l: nsubj){
					qsj.add(l.lemma());
				}
				//not finished
				qsj.retainAll(ensj);
				if(qsj.size()>0){
					// get the verb
					List<CoreLabel> verb = q.getAssociatedEntity("verb");
					this.verbPolarityMap.put("", 1.0);
				}else{
					this.verbPolarityMap.put("", -1.0);
				}
			}
			
			for(Quantity q: chc.getLosses()){
				
			}
		}
	}
	
	public static void main(String[] args) throws IOException{
		VerbPolarityHelper v = VerbPolarityHelper.getInstance();
		v.persist();
	}
}
