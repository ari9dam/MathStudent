/**NlpPipeline.java
 * 10:03:14 AM @author Arindam
 */
package nlp.app.math.util;

import edu.asu.nlu.common.parsing.StanfordBerkeleyNlpPipeline;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

/**
 * @author Arindam
 *
 */
public class NlpPipeline {
	private static final StanfordBerkeleyNlpPipeline pipeline = new StanfordBerkeleyNlpPipeline();
	private static final StanfordBerkeleyNlpPipeline onlyStanfordPipeline = new StanfordBerkeleyNlpPipeline(true);
	public static StanfordCoreNLP getPipeline(){
		return pipeline.getPipeline();
	}
	
	public static StanfordCoreNLP getPipeline(boolean isStanfordOnly){
		if(isStanfordOnly)
			return onlyStanfordPipeline.getPipeline();
		return pipeline.getPipeline();
	}
}
