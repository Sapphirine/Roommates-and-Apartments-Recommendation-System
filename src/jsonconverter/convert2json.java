/**
 * 
 */
package jsonconverter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Zhicheng
 *
 */
public class convert2json {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws JSONException 
	 */
	public static void main(String[] args) throws IOException, JSONException {
		
		BufferedReader br=new BufferedReader(new FileReader("data/data3/id_group.csv"));
		BufferedReader br1=new BufferedReader(new FileReader("data//data3/cluster_result.csv"));
		BufferedReader br2=new BufferedReader(new FileReader("data/data3/source_target_weight.csv"));
		FileWriter fw = new FileWriter("data/data3/data3.json");
        Map<Integer,String> groupidmap = new HashMap<Integer, String>();
        Map<String,Object> nodemap = new HashMap<String, Object>();
        Map<String,Object> links = new HashMap<String, Object>();

        String line = new String ();
        int i=0;
        
        while((line=br.readLine())!= null){
			String[] values = line.split(",",-1);
			groupidmap.put(Integer.parseInt(values[0]),values[1]);
        }
        
        JSONArray ja1 = new JSONArray();
        while((line=br1.readLine())!= null){
			String[] values = line.split(",",-1);
			nodemap.put("name",groupidmap.get(Integer.parseInt(values[0])));
			nodemap.put("group",Integer.parseInt(values[1]));
	        ja1.put(nodemap);
	        nodemap.clear();
        }
        
        JSONArray ja2 = new JSONArray();
        while((line=br2.readLine())!= null){
			String[] values = line.split(",",-1);
			links.put("source",Integer.parseInt(values[0]));
			links.put("target",Integer.parseInt(values[1]));
			links.put("value",Double.parseDouble(values[2]));
	        ja2.put(links);
	        links.clear();
        }

        JSONObject jo = new JSONObject();
        jo.put("nodes",ja1);
        jo.put("links", ja2);
        
        fw.write(jo.toString(),0,jo.toString().length());
		fw.flush();
	}

}
