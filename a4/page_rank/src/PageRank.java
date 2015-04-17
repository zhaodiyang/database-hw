import java.io.IOException;
import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.*;
import org.apache.hadoop.mapreduce.lib.output.*;
import org.apache.hadoop.util.*;


public class PageRank {

	public static void main(String[] args) throws IOException {
		int numRepititions = 5;
		long leftover = 0;
		long size = 0;
		for(int i = 0; i < 2*numRepititions; i++) {
			Job job;
			if(i%2 == 0) {
				job = getTrustJob();
			}
			else {
				job = getLeftoverJob(leftover, size);
			}

			String inputPath = i == 0 ? "input" : "stage" + (i-1);
			String outputPath = "stage" + i;

			FileInputFormat.addInputPath(job, new Path(inputPath));
			FileOutputFormat.setOutputPath(job, new Path(outputPath));

			try { 
				job.waitForCompletion(true);
			} catch(Exception e) {
				System.err.println("ERROR IN JOB: " + e);
				return;
			}
			// TrustJob Turn - set leftover and size, get ready for LeftoverJob
			if(i%2 == 0) {
				leftover = job.getCounters().findCounter(Counter.LOST).getValue();
				size = job.getCounters().findCounter(Counter.SIZE).getValue();
			}
			// LeftoverJob Turn - reset Counter values to zero
			else {
				job.getCounters().findCounter(Counter.LOST).setValue(0);
				job.getCounters().findCounter(Counter.SIZE).setValue(0);
			}
		}
	}
	public static Job getStandardJob(String l, String s) throws IOException {
		Configuration conf = new Configuration();
		if(!l.equals("") && !s.equals("")) {
			conf.set("leftover", l);
			conf.set("size", s);
		}
		Job job = new Job(conf);

		job.setOutputKeyClass(IntWritable.class);
		job.setOutputValueClass(Node.class);

		job.setInputFormatClass(NodeInputFormat.class);
		job.setOutputFormatClass(NodeOutputFormat.class);

		job.setJarByClass(PageRank.class);

		return job;
	}

	public static Job getTrustJob() throws IOException{

		Job job = getStandardJob("", "");

		job.setMapOutputKeyClass(IntWritable.class);
		job.setMapOutputValueClass(NodeOrDouble.class);

		job.setMapperClass(TrustMapper.class);
		job.setReducerClass(TrustReducer.class);

		return job;
	}

	public static Job getLeftoverJob(long l, long s) throws IOException{
		Job job = getStandardJob("" + l, "" + s);

		job.setMapperClass(LeftoverMapper.class);
		job.setReducerClass(LeftoverReducer.class);

		return job;
	}
}





