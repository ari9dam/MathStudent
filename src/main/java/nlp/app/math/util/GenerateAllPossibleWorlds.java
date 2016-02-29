package nlp.app.math.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import nlp.app.math.core.ChangeConcept;
import nlp.app.math.core.MathSample;
import nlp.app.math.core.PartWholeConcept;
import nlp.app.math.core.Quantity;

/**
 * @author Arindam
 *
 */
public class GenerateAllPossibleWorlds {
	private Quantity defaultQ = null;

	public GenerateAllPossibleWorlds(){
		this.defaultQ = new Quantity("0",-1,-1);
		this.defaultQ.setDefault(true);
	}

	public void generate(MathSample sample){
		generatePartWholes(sample);
		generateChangeWorlds(sample);

	}
	public void generatePartWholes(MathSample sample){
		List<Quantity> quantites = sample.getQuantities();
		List<Quantity> parts = new ArrayList<Quantity>();
		for(Quantity whole: quantites){
			for(Quantity q: quantites){
				if(!q.equals(whole))
					parts.add(q);
			}
			this.addPartWholeWorlds(sample, whole, parts);
			parts.clear();
		}
	}

	private void addPartWholeWorlds(MathSample sample, Quantity whole, List<Quantity> parts){
		SubsetIterator<Quantity> it = new SubsetIterator<Quantity>(parts);
		while(it.hasNext()){
			List<Quantity> subset = it.next();
			if(subset.isEmpty())
				continue;

			PartWholeConcept cb = new PartWholeConcept(whole, subset);
			sample.addWorld(cb);
		}
	}

	public void generateChangeWorlds(MathSample sample){
		List<Quantity> quantites = sample.getQuantities();
		List<Quantity> change = new ArrayList<Quantity>();
		
		// possible n(n-1)*(3^(n-2) -1)/2
		for(Quantity start: quantites){
			for(Quantity end: quantites){
				if(!start.equals(end)){
					for(Quantity q: quantites){
						if(q!=start & q!=end){
							change.add(q);
						}
					}

					this.addChangeWorlds(sample, start, end, change);
					change.clear();
				}
			}
		}

		// start is default : possible n*(3^(n-1) -1)
		for(Quantity end: quantites){
			for(Quantity q: quantites){
				if(q!=end){
					change.add(q);
				}
			}
			this.addChangeWorlds(sample, this.defaultQ, end, change);
			change.clear();
		}
	}

	private void addChangeWorlds(MathSample sample, Quantity start, Quantity end, 
			List<Quantity> change){
		SubsetIterator<Quantity> it = new SubsetIterator<Quantity>(change);
		while(it.hasNext()){
			List<Quantity> gain = it.next();
			List<Quantity> newChange = new LinkedList<Quantity>(change);
			newChange.removeAll(gain);
			SubsetIterator<Quantity> it_loss = new SubsetIterator<Quantity>(newChange);
			while(it_loss.hasNext()){
				List<Quantity> loss = it_loss.next();
				if(!gain.isEmpty() || !loss.isEmpty()){
					ChangeConcept c = new ChangeConcept(start,end, gain, loss);
					sample.addWorld(c);
				}
			}
		}
	}
}
