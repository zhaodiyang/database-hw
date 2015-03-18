import java.util.*;

public class FDChecker {

	/**
	 * Checks whether a decomposition of a table is dependency
	 * preserving under the set of functional dependencies fds
	 * 
	 * @param t1 one of the two tables of the decomposition
	 * @param t2 the second table of the decomposition
	 * @param fds a complete set of functional dependencies that apply to the data
	 * 
	 * @return true if the decomposition is dependency preserving, false otherwise
	 **/
	public static boolean checkDepPres(AttributeSet t1, AttributeSet t2, Set<FunctionalDependency> fds) {
		//a decomposition is dependency preserving, if local functional dependencies are
		//sufficient to enforce the global properties
		//To check a particular functional dependency a -> b is preserved, 
		//you can run the following algorithm
		//result = a
		//while result has not stabilized
		//	for each table in the decomposition
		//		t = result intersect table 
		//		t = closure(t) intersect table
		//		result = result union t
		//if b is contained in result, the dependency is preserved
		Iterator<FunctionalDependency> fdIter = fds.iterator();
		while(fdIter.hasNext()){
			FunctionalDependency fd = fdIter.next();
			AttributeSet left = fd.left;
			Attribute right = fd.right;
			AttributeSet result = left;
			do{
				/**
				 * t1
				 * */
				// attrs = result intersect t1 
				left = result;
				AttributeSet attrs = new AttributeSet();
				Iterator<Attribute> attrIter = result.iterator();
				while(attrIter.hasNext()){
					Attribute tmp = attrIter.next();
					if(t1.contains(tmp))
						attrs.add(tmp);
				}
				
				// attrs = closure(attrs)
				Set<FunctionalDependency> fdSet =  new HashSet<FunctionalDependency>();
				fdSet.add(fd);
				attrs = closure(attrs, fdSet);
				
				// attrsTmp = attrs intersect t1
				AttributeSet attrsTmp = new AttributeSet();
				attrIter = attrs.iterator();
				while(attrIter.hasNext()){
					Attribute tmp = attrIter.next();
					if(t1.contains(tmp))
						attrsTmp.add(tmp);
				}
				
				// result = result union attrsTmp
				attrIter = attrsTmp.iterator();
				while(attrIter.hasNext())
					result.add(attrIter.next());
				
				/**
				 * t2
				 * */
				// attrs = result intersect t2
				attrs = new AttributeSet();
				attrIter = result.iterator();
				while(attrIter.hasNext()){
					Attribute tmp = attrIter.next();
					if(t2.contains(tmp))
						attrs.add(tmp);
				}
				
				// attrs = closure(attrs)
				fdSet =  new HashSet<FunctionalDependency>();
				fdSet.add(fd);
				attrs = closure(attrs, fdSet);
				
				// attrsTmp = attrs intersect t2
				attrsTmp = new AttributeSet();
				attrIter = attrs.iterator();
				while(attrIter.hasNext()){
					Attribute tmp = attrIter.next();
					if(t2.contains(tmp))
						attrsTmp.add(tmp);
				}
				
				// result = result union attrsTmp
				attrIter = attrsTmp.iterator();
				while(attrIter.hasNext())
					result.add(attrIter.next());
				
			}while(!left.equals(result));
			
			if(!left.contains(right))
				return false;
		}
		return true;
	}

	/**
	 * Checks whether a decomposition of a table is lossless
	 * under the set of functional dependencies fds
	 * 
	 * @param t1 one of the two tables of the decomposition
	 * @param t2 the second table of the decomposition
	 * @param fds a complete set of functional dependencies that apply to the data
	 * 
	 * @return true if the decomposition is lossless, false otherwise
	 **/
	public static boolean checkLossless(AttributeSet t1, AttributeSet t2, Set<FunctionalDependency> fds) {
		//Lossless decompositions do not lose information, the natural join is equal to the 
		//original table.
		//a decomposition is lossless if the common attributes for a superkey for one of the
		//tables.
		AttributeSet sharedAttrs = new AttributeSet();
		Iterator<Attribute> attrIter = t1.iterator();
		while(attrIter.hasNext()){
			Attribute tmp = attrIter.next();
			if(t2.contains(tmp))
				sharedAttrs.add(tmp);
		}
		AttributeSet closureAttrs = closure(sharedAttrs, fds);
		if(closureAttrs.equals(t1) || closureAttrs.equals(t2))
			return true;
		
		return false;
	}

	//recommended helper method
	//finds the total set of attributes implied by attrs
	public static AttributeSet closure(AttributeSet attrs, Set<FunctionalDependency> fds) {
		AttributeSet result = attrs;
		AttributeSet tmp = result;
		
		do{
			result = tmp;
			Iterator<FunctionalDependency> iterfd = fds.iterator();
			while(iterfd.hasNext()){
				FunctionalDependency fd = iterfd.next();
				AttributeSet left = fd.left;
				Attribute right = fd.right;
				
				// redundancy check
				/*
				if(left.contains(right))
					left.remove(right);*/
				
				Iterator<Attribute> iter = left.iterator();
				boolean leftInAttrs = true;
				while(iter.hasNext()){
					if(!tmp.contains(iter.next())){
						leftInAttrs = false;
						break;
					}
				}
				if(leftInAttrs)
					tmp.add(fd.right);
			}
		}while(!result.equals(tmp));
		
		return result;
	}
}
