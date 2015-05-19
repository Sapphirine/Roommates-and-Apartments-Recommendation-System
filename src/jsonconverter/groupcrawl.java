/**
 * 
 */
package jsonconverter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Zhicheng
 *
 */
public class groupcrawl {


	/**
	 * @param args
	 * @throws JSONException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws JSONException, IOException {
		
		Date date = new Date();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		new File("data/"+df.format(date)).mkdir();
		
		FileWriter fw = new FileWriter("data/id_group.csv");
		FileWriter fw1 = new FileWriter("data/groupid_topicid.csv");
		FileWriter fw2 = new FileWriter("data/id_topic_group.csv");
		FileWriter fw3 = new FileWriter("data/"+df.format(date)+"/group_membernum.csv");



		
		int total_count=0;
		
		Map<Integer,String> topic_id = new HashMap<Integer,String>();

		
		
		//get the total count
		String str = "https://api.meetup.com/2/groups?key=649d2169296f44404f12707723167&radius=100.0&order=id&format=json&lat=40.7599983215&page=1&desc=false&offset=0&only=id%2Cname%2Ctopics%2Cmembers&fields=&lon=-73.9899978638";
		URL url= new URL(str);

		Scanner scan=new Scanner(url.openStream());
		String data = new String ();
		
		while (scan.hasNext())
			data +=scan.nextLine();
		scan.close();
		
		JSONObject obj = new JSONObject(data); 
		JSONObject meta = obj.getJSONObject("meta");
		JSONArray results = obj.getJSONArray("results");
		total_count = meta.getInt("total_count");
		
		
		
		
		
		
		//
		for(int i=0;i<=(int)(total_count/200);i++){
			str = "https://api.meetup.com/2/groups?key=649d2169296f44404f12707723167&radius=100.0&order=id&format=json&lat=40.7599983215&page=200&desc=false&offset="+i+"&only=id%2Cname%2Ctopics%2Cmembers&fields=&lon=-73.9899978638";
			url= new URL(str);
			
			scan=new Scanner(url.openStream());
			data = new String ();
			
			while (scan.hasNext())
				data +=scan.nextLine();
			scan.close();
			
			obj = new JSONObject(data); 
			results = obj.getJSONArray("results");
			
			for(int j=0;j<results.length();j++){
				String group_id = results.getJSONObject(j).getInt("id")+","+results.getJSONObject(j).getString("name")+"\n";
				String group_num = results.getJSONObject(j).getInt("id")+","+results.getJSONObject(j).getInt("members")+"\n";
				fw.write(group_id,0,group_id.length());
				fw.flush();
				fw3.write(group_num,0,group_num.length());
				fw3.flush();
				
				try{JSONArray topics = results.getJSONObject(j).getJSONArray("topics");
				for(int l=0;l<topics.length();l++){
					String group_topic = results.getJSONObject(j).getInt("id")+","+topics.getJSONObject(l).getInt("id")+"\n";
					fw1.write(group_topic,0,group_topic.length());
					fw1.flush();
					if (!topic_id.containsKey(topics.getJSONObject(l).getInt("id")))
						topic_id.put(topics.getJSONObject(l).getInt("id"),topics.getJSONObject(l).getString("name"));
				}}
				catch(JSONException e)
				{	
				}
			
				
		}
			
			
		Iterator it = topic_id.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<Integer, String> entry = (Entry<Integer, String>) it.next();
			int key = entry.getKey();
			String value = entry.getValue();
			String temp = key+","+value+"\n";
			fw2.write(temp,0,temp.length());
			fw2.flush();
		}

	}
		fw.close();
		fw1.close();
		fw2.close();
		fw3.close();

}
}
