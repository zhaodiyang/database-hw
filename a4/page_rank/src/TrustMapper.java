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
 * class Mapper
 *   method Map(nid n,node N)
 *     p ← N.PageRank/|N.AdjacencyList|
 *     Emit(nid n, N ) 		◃ Pass along graph structure 
 *     for all nodeid m ∈ N.AdjacencyList do
 *       Emit(nid m, p) 	◃ Pass PageRank mass to neighbors
*/
public class TrustMapper extends Mapper<IntWritable, Node, IntWritable, NodeOrDouble> {
	public void map(IntWritable key, Node value, Context context) 
			throws IOException, InterruptedException {

		// increment the graph size by 1 
		context.getCounter(Counter.SIZE).increment(1);

		// First: p ← N.PageRank/|N.AdjacencyList|
		double pr = value.getPageRank()/value.outgoingSize();

		NodeOrDouble nodeNOD = new NodeOrDouble(value);
		NodeOrDouble prNOD = new NodeOrDouble(pr);

		// Second: Emit(nid n, N )
		context.write(key, nodeNOD);
		
		// Thrid: for all nodeid m ∈ N.AdjacencyList: Emit(nid m, p)
		for(int i : value.outgoing) {
			IntWritable m = new IntWritable(i);
			context.write(m, prNOD);
		}
	}
}
