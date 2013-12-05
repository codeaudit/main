/*
             Licensed to the DARPA XDATA project.
       DARPA XDATA licenses this file to you under the 
         Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
           You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
                 either express or implied.                    
   See the License for the specific language governing
     permissions and limitations under the License.
*/
package smile.wide.algorithms.fang;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;

/**
 * Mapper
 * @author m.a.dejongh@gmail.com
 *
 */
public class FangCounterMapper extends Mapper<LongWritable, Text, Text, VIntWritable> {
	String record = new String();
	int x = 0;
	int nvar = 0;
	VIntWritable one = new VIntWritable(1);
	String par = "";
	Set<Integer> parents = new HashSet<Integer>();
	String assignment = "";
	/** Initializes class parameters*/
	@Override
	protected void setup(Context context) {
		Configuration conf = context.getConfiguration();
		//set some constants here
		//set total nr variables
		nvar = conf.getInt("nvar",0);
		//set variable
		x = conf.getInt("VarX",0);
		//set current parent set
		par = conf.get("parents","");
	}
	
	/**Mapper
	 */
	@Override
	protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException  {
		record = value.toString();
		String[] values = record.split(",|\t| ");
		
		//create joint assignment of x and parents
		assignment = "v"+x+"="+values[x];
		for(Integer y : parents) {
			assignment += "+v"+y+"="+values[y];
		}
		context.write(new Text(assignment),one);
		//we need to iterate over all non-parents
		for(int y=0;y<nvar;++y) {
			if( y!=x && !parents.contains(y) ) {
				//create joint assignment of x,parents,y
				context.write(new Text(assignment + "+v"+y+"="+values[y]),one);
			}
		}
	}
}
