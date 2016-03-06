package nlp.app.math.core.features;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.stanford.nlp.ling.CoreLabel;
import nlp.app.math.core.MathSample;
import nlp.app.math.core.ProblemRepresentation;
import nlp.app.math.core.Quantity;
import nlp.app.math.util.WordNetHelper;

/**
 * @author Arindam
 *
 */
public class FeatureExtractorCommon {
	public WordNetHelper wnh;
	private HashSet<String> beVerbs;

	public FeatureExtractorCommon() {
		this.beVerbs = new HashSet<String>();
		beVerbs.add("be");
		beVerbs.add("has");
		beVerbs.add("have");
		this.wnh = WordNetHelper.getInstance();
	}

	public void addFeatures(ProblemRepresentation rep, MathSample sample, 
			Map<String, Double> featureMap) {
		/**
		 * Populate 
		 * 	1. subj match
		 *  2. default subj match, subj consume
		 *  3. verb match
		 *  4. exact verb match
		 *  5. subj to iobj match
		 *  6. iobj to subj match
		 *  7. prep_in match
		 *  for each pair of quantity 
		 */
		int verpRepeat = 0;
		String id="";
		for(Quantity q1: rep.getQuantities()){
			for(Quantity q2: rep.getQuantities()){

				if(q1==q2)
					continue;

				id = q1.getUniqueId() + q2.getUniqueId();

				/**
				 * Algo: Subj & Default Subj Match
				 */
				boolean subjmatch = false;
				boolean defaultSubjMatch = false;
				boolean subjConsume  = false;
				List<CoreLabel> nsubj1 = q1.getAssociatedEntity("nsubj");
				List<CoreLabel> nsubj2 = q2.getAssociatedEntity("nsubj");
				if(nsubj1.size()>0&&nsubj2.size()>0){
					CoreLabel ns1  = nsubj1.get(0);
					CoreLabel ns2  = nsubj2.get(0);
					if(rep.isCoref(q1.getSentenceId(), ns1.index(), q2.getSentenceId(), ns2.index())){
						subjmatch = true;
					}else if(ns1.lemma().equalsIgnoreCase("they")){
						subjConsume = true;
					}else if(ns1.lemma().equals(ns2.lemma())){
						subjmatch = true;
					}else if((ns1.ner().equalsIgnoreCase("Person") && 
							!ns2.ner().equalsIgnoreCase("Person") && 
							!ns2.lemma().equalsIgnoreCase("they")) 
							|| (ns2.ner().equalsIgnoreCase("Person") 
									&& !ns1.ner().equalsIgnoreCase("Person")
									&& !ns1.lemma().equalsIgnoreCase("they"))){
						defaultSubjMatch = true;
					}else if((wnh.isAHyponym(ns1.lemma(), "person", 1) && 
							!ns2.ner().equalsIgnoreCase("Person")) || (wnh.isAHyponym(ns2.lemma(), "person", 1) && 
									!ns1.ner().equalsIgnoreCase("Person"))){
						// Case: The recipe wants X. She put in y. ...
						defaultSubjMatch = true;
					}
				}else if((nsubj1.size()==0||nsubj2.size()==0)){
					defaultSubjMatch = true;
				}

				featureMap.put("f_subjmatch"+id, subjmatch?1.0:0.0);
				featureMap.put("f_subjConsume"+id, subjConsume?1.0:0.0);
				featureMap.put("f_defaultsubjmatch"+id, defaultSubjMatch?1.0:0.0);

				/**
				 * Algo Verb Match
				 */
				boolean verbMatched  = false;
				boolean exactVerbMatched = false;
				boolean v1Entailsv2 = false;
				boolean v2Entailsv1 = false;
				HashSet<String> verb1 = new HashSet<String>();
				HashSet<CoreLabel> verb2 = new HashSet<CoreLabel>();
				for(CoreLabel l: q1.getAssociatedEntity("verb"))
					verb1.add(l.lemma());
				if(!verb1.isEmpty() && verb1.contains("do")){
					for(CoreLabel l: q1.getAssociatedEntity("ccomp")){
						verb1.add(l.lemma());
					}
				}

				for(CoreLabel l: q2.getAssociatedEntity("verb"))
					verb2.add(l);
				for(CoreLabel label: verb2){
					if(label.lemma().equals("do")){
						for(CoreLabel l: q2.getAssociatedEntity("ccomp")){
							verb2.add(l);
						}
						break;
					}
				}

				for(CoreLabel l: verb2){
					if(beVerbs.contains(l.lemma()))
						continue;

					if(verb1.contains(l.lemma())){
						exactVerbMatched = true;
						verbMatched = true;
						verpRepeat++;		
					}else {
						// check related
						for(String verb: verb1){
							if(beVerbs.contains(verb))
								continue;
							if(wnh.hasCommonAncestor(
									verb, l.lemma(), 0)){
								verbMatched = true;
								verpRepeat++;
							}
							if(wnh.entails(verb, l.lemma())){
								v1Entailsv2 = true;
							}else if(wnh.entails(l.lemma(),verb)){
								v2Entailsv1 = true;
							}
						}
					}
				}

				featureMap.put("f_verbMatched"+id, verbMatched?1.0:0.0);
				featureMap.put("f_exactVerbMatched"+id, exactVerbMatched?1.0:0.0);
				featureMap.put("f_v2Entailsv1"+id, v2Entailsv1?1.0:0.0);
				featureMap.put("f_v1Entailsv2"+id, v1Entailsv2?1.0:0.0);

				/**
				 * nsubj to iobj 
				 * iobj to nsubj
				 */
				boolean subjIobjmatch = false;
				boolean iobjsubjmatch = false;
				List<CoreLabel> iobj1  = new ArrayList<CoreLabel>();
				List<CoreLabel> iobj2  = new ArrayList<CoreLabel>();
				iobj1.addAll(q1.getAssociatedEntity("iobj"));
				iobj1.addAll(q1.getAssociatedEntity("prep_of"));

				iobj2.addAll(q2.getAssociatedEntity("iobj"));
				iobj2.addAll(q2.getAssociatedEntity("prep_of"));

				/**
				 * exact or coref
				 */
				if(nsubj1.size()>0&&iobj2.size()>0){
					CoreLabel ns1  = nsubj1.get(0);
					CoreLabel io2  = iobj2.get(0);
					if(rep.isCoref(q1.getSentenceId(), ns1.index(), q2.getSentenceId(), io2.index())){
						subjIobjmatch = true;
					}else if(ns1.lemma().equals(io2.lemma())){
						subjIobjmatch = true;
					}else if(io2.originalText().equals("him")){
						subjIobjmatch = true;
					}
				}
				if(nsubj2.size()>0&&iobj1.size()>0){
					CoreLabel ns2  = nsubj2.get(0);
					CoreLabel io1  = iobj1.get(0);
					if(rep.isCoref(q2.getSentenceId(), ns2.index(), q1.getSentenceId(), io1.index())){
						iobjsubjmatch = true;
					}else if(ns2.lemma().equals(io1.lemma())){
						iobjsubjmatch = true;
					}else if(ns2.originalText().equals("him")){
						iobjsubjmatch = true;
					}
				}
				featureMap.put("f_iobsubjmatch"+id, iobjsubjmatch?1.0:0.0);
				featureMap.put("f_subjIobjmatch"+id, subjIobjmatch?1.0:0.0);

				boolean tmodMatch = false;

				List<CoreLabel> tmod1 = q1.getAssociatedEntity("tmod");
				List<CoreLabel> tmod2 = q2.getAssociatedEntity("tmod");
				if(tmod1.size()>0&&tmod2.size()>0){
					CoreLabel ns1  = tmod1.get(0);
					CoreLabel ns2  = tmod2.get(0);
					if(ns1.lemma().equalsIgnoreCase(ns2.lemma()))
						tmodMatch = true;

				}else if((tmod1.size()==0||tmod2.size()==0)){
					tmodMatch = true;
				}
				featureMap.put("f_tmodmatch"+id, tmodMatch?1.0:0.0);

				List<CoreLabel> prep_in1 = q1.getAssociatedEntity("prep_in");
				List<CoreLabel> prep_in2 = q2.getAssociatedEntity("prep_in");
				boolean prep_in_match = false;
				if(!prep_in1.isEmpty() && !prep_in2.isEmpty()){
					Set<String> p1 = new HashSet<String>();
					Set<String> p2 = new HashSet<String>();

					for(CoreLabel l:prep_in1 ){
						p1.add(l.lemma());
					}

					for(CoreLabel l:prep_in2 ){
						p2.add(l.lemma());
					}
					prep_in_match = p1.equals(p2);
				}
				featureMap.put("f_prep_inmatch"+id, prep_in_match?1.0:0.0);
			}
			
			featureMap.put("f_verbRepeated"+id, verpRepeat>0?1.0:0.0);
		}
	}
}
