// Soheil Nazar Shahsavani 

import java.util.StringTokenizer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;




public class InvertedIndex {

 
  public static class TokenizerMapper
       extends Mapper<LongWritable, Text, Text, IntWritable> {

    //private final static IntWritable one = new IntWritable(1);
    private Text word = new Text();

    public void map(LongWritable key, Text value, Context context
                    ) throws IOException, InterruptedException {
						
	  StringTokenizer itr = new StringTokenizer(value.toString());	  
	  String nV = itr.nextToken();
	  Integer nVInt = Integer.parseInt(nV);
	  
      IntWritable documentId = 
			new IntWritable(nVInt);	  
	  
      for ( ; itr.hasMoreTokens(); ) {
        word.set(itr.nextToken());
        context.write(word, documentId);
      }
    }
  }
  public static class IntSumReducer
       extends Reducer<Text,IntWritable,Text,Text> {
    private IntWritable result = new IntWritable();
    

    public void reduce(Text key, Iterable<IntWritable> values,
                       Context context
                       ) throws IOException, InterruptedException {
      
  /*       int sum = 0;
      for (IntWritable val : values) {
        sum += val.get();
      }
      result.set(sum);
      context.write(key, result);
 */    
    HashMap<Integer, Integer> docOccurence = new HashMap<Integer, Integer>();
								
 
	//initializing the HashMap
	
	for (IntWritable cur: values){
		Integer curKey = new Integer (cur.get());
		if ( docOccurence.containsKey(curKey) ){
			Integer curValue = docOccurence.get(curKey);
			docOccurence.put (curKey, curValue + 1);
		} else {
			docOccurence.put (curKey, 1);			
		}
	}
	
	StringBuilder outputText = new StringBuilder();
	Text word = new Text();
	
	Set<Integer> keys = docOccurence.keySet();
	
	for (Integer curKey: keys){
		
		String outStr = curKey.toString() + ':' + docOccurence.get(curKey).toString() + '\t' ;
		outputText.append ( outStr );
		
	}
	
      word.set(outputText.toString());
      context.write(key, word);
	
	}
  }

  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "InvertedIndex");
    job.setJarByClass(InvertedIndex.class);
    job.setMapperClass(TokenizerMapper.class);
    //job.setCombinerClass(IntSumReducer.class);
    job.setReducerClass(IntSumReducer.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(IntWritable.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);			
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
  
  
}
