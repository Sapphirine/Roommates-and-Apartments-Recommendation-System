/**
 * 
 */
package jsonconverter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Zhicheng
 *
 */
public class calculating {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		BufferedReader br=new BufferedReader(new FileReader("data/id_group.csv"));
		BufferedReader br1=new BufferedReader(new FileReader("data/group_event.csv"));

		FileWriter fw = new FileWriter("data/group_eventnumber.csv");
		
		
		String line = new String();
		int count,offset = 0;
		Map<Integer,Integer> group_event = new HashMap<Integer,Integer>();

		
		while((line=br.readLine())!= null){
			String[] values = line.split(",",-1);
			group_event.put(Integer.parseInt(values[0]),0);
		}
		
		while((line=br1.readLine())!=null){
			String[] values = line.split(",", -1);
			count = group_event.get(Integer.parseInt(values[0]));
			//System.out.println(Integer.parseInt(values[0])+","+count);
			group_event.put(Integer.parseInt(values[0]),count+1);
		}
		
		
		Iterator it = group_event.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<Integer, Integer> entry = (Entry<Integer, Integer>) it.next();
			int key = entry.getKey();
			int value = entry.getValue();
			if(value!=0){
			String temp = key+","+value+"\n";
			fw.write(temp,0,temp.length());
			fw.flush();}
		}

	}

}
