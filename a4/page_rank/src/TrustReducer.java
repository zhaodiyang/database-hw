import java.io.IOException;
import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.util.*;

/* MapReduce algorithm for PageRank
 * From: http://lintool.github.io/MapReduceAlgorithms/ed1n/MapReduceI algorithms.pdf#
 * 
 * class Reducer
 *   method Reduce(nid m, [p1, p2, . . .])
 *     M←∅
 *     for all p ∈ counts [p1,p2,...] do
 *       if IsNode(p) then 
 *         M ← p				◃ Recover graph structure
 *       else
 *         s ← s + p 			◃ Sum incoming PageRank contributions
 *     M.PageRank ← s
 *     Emit(nid m, node M)
 */
public class TrustReducer extends Reducer<IntWritable, NodeOrDouble, IntWritable, Node> {
	public void reduce(IntWritable key, Iterable<NodeOrDouble> values, Context context) 
			throws IOException, InterruptedException {
		
		// First: M←∅, initialize sum s
		Node   m = null;
		double s = 0;
		
		// Second: everything in for loop of the pseudo-code
		for(NodeOrDouble nod : values) {
			
			// check if nod is a Node
			if (nod.isNode()) {
				m = nod.getNode();
				
				// check if m has any out-link
				// if it doesn't, record lost page rank to Counter
				if(m.outgoingSize() == 0) {
					
					// API requires a long-type value for the increment function
					// so scale up by 10^5 here to add accuracy by preserving 
					// the first 5 digits of the page rank value
					// will scale down back in LeftoverReducer
					long scaledPR = (long)(m.getPageRank() * 1e5);
					context.getCounter(Counter.LOST).increment(scaledPR);
				}
			}
			// nod is a double value, accumulate to s
			else { 
				s += nod.getDouble(); 
			}
		}
		// Third: M.PageRank ← s
		m.setPageRank(s);
		
		// Fourth: Emit(nid m, node M)
		context.write(key, m);
	}
}
