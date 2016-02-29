package nlp.app.math.app;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import edu.asu.loglinear.PredictCLM;
import edu.asu.type.Sample;
import nlp.app.math.core.IMathConcept;
import nlp.app.math.core.MathSample;
import nlp.app.math.core.ProblemRepresentation;
import nlp.app.math.core.features.FeatureExtraction;
import nlp.app.math.util.ConvertMathSamples;
import nlp.app.math.util.GenerateAllPossibleWorlds;

/**
 * @author Arindam
 * Finds relation between pair of entities
 * currently there is only one relation that can exist between a pair
 * named: PartWhole
 */
public class RelationFinder {
	private ArrayList<String> featureNames;
	private PredictCLM predicter;
	private Map<String,Integer> indexMap;
	private ConvertMathSamples converter;
	private GenerateAllPossibleWorlds gw;
	private final String vpFile="C:\\Users\\Arindam\\Dropbox\\Math Challenge\\sample_questions_verb_polarity.ser"; 
	private Map<String, Double> vpMap ;
	
	
	public RelationFinder(ArrayList<String> featureNames, String model) 
			throws ClassNotFoundException, IOException {
		this.featureNames = featureNames;
		this.predicter = new PredictCLM(model);
		this.indexMap = new HashMap<String,Integer>();
		for(int i=0;i<this.featureNames.size();i++){
			indexMap.put(this.featureNames.get(i), i);
		}
		converter = new ConvertMathSamples();
		gw = new GenerateAllPossibleWorlds();
		ObjectInputStream objectinputstream = null;
		objectinputstream = new ObjectInputStream(new FileInputStream(vpFile));
		vpMap = (Map<String,Double>) objectinputstream.readObject();
		objectinputstream.close();
	}




	public void findRelations(ProblemRepresentation irep, boolean ignoreUnknownVerbs, String similarityMathod) {

		
		/*****************************************************
		 * Creating Math sample
		 * ***************************************************/

		//###################################
		//###create an empty math sample#####
		//###################################
		MathSample sample = new MathSample(irep.getId());
		
		sample.setQuantities(irep.getQuantities());
		/*
		 * Generate all y's
		 */
		gw.generate(sample);
		
		
		/*##############################
		 * Get feature map for each y ##
		 * Add feature map to sample ###
		 * #############################
		 */
		FeatureExtraction extractor = new FeatureExtraction(this.vpMap);
		extractor.computeFeatureMaps(irep, sample, sample.numberOfPossibleWorlds());
		
		/**
		 * Create a test data Sample from the math sample
		 */
		
		Sample s =  converter.toSample(sample, this.indexMap);

		/**
		 * Call predict CLM
		 */
		int y = predicter.predict(s);
		IMathConcept bestWorld = sample.getWorld(y);
		
		/**
		 * Populate relation map
		 */
		irep.addMathConcept(bestWorld);
	}
}
