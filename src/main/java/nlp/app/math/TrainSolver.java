/**TrainSolver.java
 * 1:22:56 PM @author Arindam
 */
package nlp.app.math;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import edu.asu.loglinear.TrainConditionalLogLinearModel;
import edu.asu.type.Sample;
import nlp.app.math.app.UnknownFinder;
import nlp.app.math.core.MathSample;
import nlp.app.math.core.ProblemRepresentation;
import nlp.app.math.core.features.FeatureExtraction;
import nlp.app.math.preprocessing.StructureTagger;
import nlp.app.math.util.AnnotateCorrectWorld;
import nlp.app.math.util.ConvertMathSamples;
import nlp.app.math.util.GenerateAllPossibleWorlds;

/**
 * @author Arindam
 *
 */
public class TrainSolver {
	private UnknownFinder unknownFinder;
	private StructureTagger structureTagger;
	private boolean debug;
	 

	public TrainSolver(boolean debug){
		this.debug = debug;
		this.unknownFinder = new UnknownFinder(debug);
		this.structureTagger = new StructureTagger(true);
		
	}

	public TrainSolver(){
		this(false);
	}
	/**
	 * 
	 * @param problems a json array of problems
	 * @param prefix to create the data file (_training_data.ser) and 
	 * 			the feature file (_features.ser, _features.txt)
	 * @throws IOException
	 */
	public void prepareTrainingData(JSONArray problems, String prefix) throws IOException{
		GenerateAllPossibleWorlds gw = new GenerateAllPossibleWorlds();
		AnnotateCorrectWorld fy = new AnnotateCorrectWorld();
		Map<String,Double> verbPolarityMap = new HashMap<String,Double>();
		List<MathSample> mathSamples = new LinkedList<MathSample>();
		for(Object obj: problems){
			JSONObject object = (JSONObject) obj;	
			
			/**
			 * Sentence processing starts here 
			 */
			//empty intermediate representation of the problem
			ProblemRepresentation irep = new ProblemRepresentation(object.getString("sQuestion"));

			/*
			 * Run pre-processing tasks including
			 * tokenization, lemmatization, sentence splitting, CFG parsing, 
			 * dependency parsing, co-reference resolution
			 */
			this.structureTagger.process(irep);
			

			/*
			 * detect the unknonwn(s) in the problem
			 * create a variable for it
			 * extracts and saves the feature that represents the meaning of the variable
			 * 
			 */
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
			
			/*##############################
			 * Get feature map for each y ##
			 * Add feature map to sample ###
			 * #############################
			 */
			FeatureExtraction extractor = new FeatureExtraction();
			Map<String, Double> vpMap = extractor.computeFeatureMaps(irep, sample, sample.numberOfPossibleWorlds());
			for(Entry<String, Double> entry: vpMap.entrySet()){
				if(verbPolarityMap.containsKey(entry.getKey())){
					verbPolarityMap.put(entry.getKey(), verbPolarityMap.get(entry.getKey())
							+ entry.getValue());
				}else{
					verbPolarityMap.put(entry.getKey(), entry.getValue());
				}
			}
			
			if(this.debug)
				System.out.println(sample);
			/*
			 * Store the sample
			 */
			mathSamples.add(sample);
			/*******************************************
			 * Ends
			 * *****************************************
			 */
		}
		//System.exit(0);
		/**************************************
		 * Convert Math Samples to Data Samples
		 * and save to disk for parameter est
		 * ************************************
		 */
		List<Sample> trainingData = new ArrayList<Sample>();
		ArrayList<String> features = new ArrayList<String>();
		ConvertMathSamples converter = new ConvertMathSamples();
		converter.toSamples(mathSamples, trainingData, features);
		
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(prefix+"_training_data.ser"));
		oos.writeObject(trainingData);
		oos.close();
		
		ObjectOutputStream oof = new ObjectOutputStream(new FileOutputStream(prefix+"_features.ser"));
		oof.writeObject(features);
		oof.close();
		
		ObjectOutputStream oov = new ObjectOutputStream(new FileOutputStream(prefix+"_verb_polarity.ser"));
		oov.writeObject(verbPolarityMap);
		oov.close();
		
		FileUtils.writeLines(new File(prefix+"_features.txt"), features);
		FileUtils.writeLines(new File(prefix+"_erb_polarity.txt"), verbPolarityMap.entrySet());
	}

	/**
	 * @param smoothing
	 * @param step size
	 * @param lambda for smoothing
	 * @param prefix of the data file
	 * @throws IOException 
	 * @throws FileNotFoundException
	 */
	public void train(boolean smoothing, double stepSize, Double lambda, String inputFile, 
			String outputFile) throws FileNotFoundException, IOException {
		TrainConditionalLogLinearModel model = new TrainConditionalLogLinearModel(smoothing,
				stepSize, lambda);
		model.train(inputFile, outputFile);
	}
}
