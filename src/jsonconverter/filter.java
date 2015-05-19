/**
 * 
 */
package jsonconverter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Zhicheng
 *
 */
public class filter {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		BufferedReader br=new BufferedReader(new FileReader("data/id_group.csv"));
		FileWriter fw = new FileWriter("data/data1/id_group.csv");

		
		String line = new String();
		int count =0,offset = 0;
		String regexp = "\"";
        Map<Integer,String> groupidmap = new HashMap<Integer, String>();
        Map<Integer,Integer> idtrans = new HashMap<Integer, Integer>();

        
        while((line=br.readLine())!= null){
			String[] values = line.split(",",-1);
			groupidmap.put(Integer.parseInt(values[0]),values[1]);
        }


		 br=new BufferedReader(new FileReader("data/data1/groupidset.csv"));

		
		
		while((line=br.readLine())!=null){
			String[] values = line.split(",", -1);
			idtrans.put(Integer.parseInt(values[0]), count);
			String temp = values[0]+","+groupidmap.get(Integer.parseInt(values[0])).replaceAll("\"","")+"\n";
			fw.write(temp,0,temp.length());
			fw.flush();
			count++;
			//topic_id.put(Integer.parseInt(values[1].replaceAll(regexp,"")), values[3].replaceAll(regexp, ""));
		}
	}

}
