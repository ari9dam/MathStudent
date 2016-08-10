/**ComputeVerbPolarity.java
 * 2:13:28 AM @author Arindam
 */
package nlp.app.math.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import edu.asu.nlu.common.ds.AnnotatedSentence;
import edu.stanford.nlp.ling.CoreLabel;
import nlp.app.math.app.UnknownFinder;
import nlp.app.math.core.ChangeConcept;
import nlp.app.math.core.IMathConcept;
import nlp.app.math.core.MathSample;
import nlp.app.math.core.ProblemRepresentation;
import nlp.app.math.core.Quantity;
import nlp.app.math.core.features.FeatureExtractorCommon;
import nlp.app.math.core.features.TypeRelatedFeatures;
import nlp.app.math.preprocessing.StructureTagger;

/**
 * @author Arindam
 *
 */
public class ComputeVerbPolarity {
	private UnknownFinder unknownFinder;
	private StructureTagger structureTagger;



	public ComputeVerbPolarity(boolean debug){
		this.unknownFinder = new UnknownFinder(debug);
		this.structureTagger = new StructureTagger(true);
	}
	
	public void compute() throws IOException{
		String prefix="C:\\Users\\Arindam\\Dropbox\\Math Challenge\\sample_questions";
		String file = "C:\\Users\\Arindam\\Dropbox\\Math Challenge\\Math_Word_DS_Kushman\\ALL\\arith_me.json";
		String jsonString = FileUtils.readFileToString(new 
				File(file));//File(prefix+"_test.json"));//
		JSONArray problems = new JSONArray(jsonString);
		ArrayList<String> features = new ArrayList<String>(FileUtils.readLines(
				new File(prefix+"_features.txt")));
		GenerateAllPossibleWorlds gw = new GenerateAllPossibleWorlds();
		AnnotateCorrectWorld fy = new AnnotateCorrectWorld();
		Map<String,Double> verbPolarityMap = new HashMap<String,Double>();
		List<MathSample> mathSamples = new LinkedList<MathSample>();
		
		Set<String> startEndVerbs = new HashSet<String>();
		Set<String> lossVerbs = new HashSet<String>();
		Set<String> gainVerbs = new HashSet<String>();
		Map<String, Integer> polarityMap = new HashMap<String, Integer>();
		
		for(Object obj: problems){
			JSONObject object = (JSONObject) obj;	

			/**
			 * Sentence processing starts here 
			 */
			
			ProblemRepresentation irep = new ProblemRepresentation(object.getString("sQuestion"));

			this.structureTagger.process(irep);

			this.unknownFinder.findUnknowns(irep);
			/***ends**/

			/*****************************************************
			 * Creating Math sample
			 * ***************************************************/

			//###################################
			//###create an empty math sample#####
			//###################################
			MathSample sample = new MathSample(object.get("iIndex").toString());

			/*
			 * Determine the fluents
			 */

			sample.setQuantities(irep.getQuantities());
			/*
			 * Generate all y's
			 */
			gw.generate(sample);

			/*
			 * Extract Correct Y
			 */
			fy.annotate(sample,object.getJSONArray("semantics"),irep);

			IMathConcept world = sample.getWorld(sample.getCorrectY());
			
			if(world instanceof ChangeConcept){
				
				//get verbs for start
				//get verbs for end
				//get polarity of loss/gain verbs
				
				Quantity start = ((ChangeConcept) world).getStart();
				Quantity end = ((ChangeConcept) world).getEnd();
				List<Quantity> gains = ((ChangeConcept) world).getGains();
				List<Quantity> losses = ((ChangeConcept) world).getLosses();
				
				
				List<CoreLabel> verbs = end.getAssociatedEntity("verb");
				if(!start.isDefault())
					verbs.addAll(start.getAssociatedEntity("verb"));
				
				for(CoreLabel l: verbs){
					startEndVerbs.add(l.lemma());
				}
				
				Map<String,Double> cMap = new HashMap<String,Double>();
				FeatureExtractorCommon common = new FeatureExtractorCommon();
				common.addFeatures(irep, sample, cMap);
				TypeRelatedFeatures typeFeatures = new TypeRelatedFeatures();
				typeFeatures.addFeatures(irep, sample, cMap);
				
				Quantity ref = end;
				if(end.getAssociatedEntity("nsubj").isEmpty()&& !start.isDefault())
					ref = start;
				
				for(Quantity l: losses){
					String id = l.getUniqueId() + ref.getUniqueId();
					double subjMatch = cMap.get("f_subjmatch"+id);
					List<CoreLabel> ve = l.getAssociatedEntity("verb");
					AnnotatedSentence sen = 
							irep.getAnnotatedSentences().get(l.getSentenceId()-1);
					
					for(CoreLabel lo: ve){
						String lemma = sen.getFullLemma(lo);
						if(!polarityMap.containsKey(lemma))
							polarityMap.put(lemma, 0);
						
						if(subjMatch < 0.5){
							polarityMap.put(lemma, polarityMap.get(lemma) + 1);
						}else
							polarityMap.put(lemma, polarityMap.get(lemma) - 1);
					}
				}
				
				for(Quantity l: gains){
					String id = l.getUniqueId() + ref.getUniqueId();
					double subjMatch = cMap.get("f_subjmatch"+id);
					List<CoreLabel> ve = l.getAssociatedEntity("verb");
					AnnotatedSentence sen = 
							irep.getAnnotatedSentences().get(l.getSentenceId()-1);
					
					for(CoreLabel lo: ve){
						String lemma = sen.getFullLemma(lo);
						if(!polarityMap.containsKey(lemma))
							polarityMap.put(lemma, 0);
						
						if(subjMatch < 0.5){
							polarityMap.put(lemma, polarityMap.get(lemma) - 1);
						}else
							polarityMap.put(lemma, polarityMap.get(lemma) + 1);
					}
				}
			}
			
			/*
			 * Store the sample
			 */
			mathSamples.add(sample);
			/*******************************************
			 * Ends
			 * *****************************************
			 */
		}
		
		for(Entry<String, Integer> entry: polarityMap.entrySet()){
			if(entry.getValue()>0.5)
				gainVerbs.add(entry.getKey());
			else
				lossVerbs.add(entry.getKey());
		}
		
		FileUtils.writeLines(new File("startendverbs.txt"), startEndVerbs);
		FileUtils.writeLines(new File("gainVerbs.txt"), gainVerbs);
		FileUtils.writeLines(new File("lossVerbs.txt"), lossVerbs);
		
		
	}
	
	public static void main(String args[]) throws IOException{
		ComputeVerbPolarity cvp = new ComputeVerbPolarity(false);
		cvp.compute();
	}
}
