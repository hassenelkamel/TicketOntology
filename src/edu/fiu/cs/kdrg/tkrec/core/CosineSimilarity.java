package edu.fiu.cs.kdrg.tkrec.core;

/**
 * 
 * @author Liang Tang
 * @date Apr 1, 2013 8:14:40 PM
 * @email tangl99@gmail.com
 * MIT license
 */
public class CosineSimilarity implements SimilarityFunction<SparseVector> {
	
	@Override
	public double sim(SparseVector o1, SparseVector o2) {
		// TODO Auto-generated method stub
		SparseVector v1 = o1;
		SparseVector v2 = o2;
		double dotProduct = v1.dotProduct(v2);
		if (dotProduct == 0) {
			return 0;
		}
		else {
			if (o1.isNormalized() && o2.isNormalized()) {
				return dotProduct;
			}
			else {
				return dotProduct/(v1.norm2()*v2.norm2());
			}
		}
	}

	@Override
	public double maxValue() {
		// TODO Auto-generated method stub
		return 1.0;
	}

	@Override
	public double minValue() {
		// TODO Auto-generated method stub
		return 0.0;
	}


}
