import java.io.IOException;
import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.util.*;


public class LeftoverReducer extends Reducer<IntWritable, Node, IntWritable, Node> {
	public static double alpha = 0.85;
	public void reduce(IntWritable nid, Iterable<Node> Ns, Context context) 
			throws IOException, InterruptedException {

		// fetch lost and size value from configuration
		// scale down the lost value by 10^3
		double lost = (double)(Long.parseLong(context.getConfiguration().get("leftover")))/1e3;
		double size = (double)(Long.parseLong(context.getConfiguration().get("size")));

		// Ns here contains every node in the graph
		for(Node n : Ns) {
			
			// apply page rank formular here to re-compute page rank for every node
			// lost/size: we evenly re-distribte lost page rank to all nodes
			double pr = alpha * (1/size) + (1 - alpha) * (lost/size + n.getPageRank());
			n.setPageRank(pr);
			
			// Emit n with a re-computed page rank
			context.write(nid, n);
		}		
	}
}
