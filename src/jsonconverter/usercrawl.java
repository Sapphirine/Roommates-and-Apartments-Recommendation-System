package jsonconverter;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class usercrawl {

	public static void main(String[] args) throws Exception, IOException, JSONException {
		BufferedReader br=new BufferedReader(new FileReader("data/id_group.csv"));
		FileWriter fw = new FileWriter("data/user_group.csv");
		//FileWriter fw1 = new FileWriter("data/userid_topicid.csv");
		//FileWriter fw2 = new FileWriter("data/id_topic_user.csv");
		FileWriter fw3 = new FileWriter("data/group_error.csv");

		
		String line = new String();
		ArrayList groupidlist =new ArrayList ();
		int total_count,offset = 0;
		Map<Integer,Integer> user_topic = new HashMap<Integer,Integer>();
		Map<Integer,String> topic_id = new HashMap<Integer,String>();

		
		while((line=br.readLine())!= null){
			String[] values = line.split(",",-1);
			groupidlist.add(Integer.parseInt(values[0]));
		}
						
		for(int id=0;id<groupidlist.size();id++){
			System.out.println("group:"+groupidlist.get(id)+" start\n");
			try{
			String str = "https://api.meetup.com/2/members?key=74669656f72194c5e3c5976116e291&group_id="+groupidlist.get(id)+"&order=name&offset=0&format=json&only=id%2Cname%2Ctopics&page=1";
			URL url= new URL(str);
			Scanner scan= new Scanner(url.openStream());
			String data = new String ();
			
			while (scan.hasNext())
				data += scan.nextLine();
			scan.close();
			
			JSONObject obj = new JSONObject(data); 
			JSONObject meta = obj.getJSONObject("meta");
			JSONArray results = obj.getJSONArray("results");
			total_count = meta.getInt("total_count");
			
			for(int i=0;i<=(int)(total_count/200);i++){
				try{
					Thread thread = Thread.currentThread();
				    thread.sleep(1500);
				str = "https://api.meetup.com/2/members?key=74669656f72194c5e3c5976116e291&group_id="+groupidlist.get(id)+"&order=name&offset="+i+"&format=json&only=id%2Cname%2Ctopics&page=200";
				url= new URL(str);
				
				scan=new Scanner(url.openStream());
				data = new String ();
				
				while (scan.hasNext())
					data +=scan.nextLine();
				scan.close();
				
				obj = new JSONObject(data); 
				results = obj.getJSONArray("results");
				
				for(int j=0;j<results.length();j++){
					String user_group = results.getJSONObject(j).getInt("id")+","+groupidlist.get(id)+"\n";
					fw.write(user_group,0,user_group.length());
					fw.flush();
					
					/*try{
					if(!user_topic.containsKey(results.getJSONObject(j).getInt("id"))){
						JSONArray topics = results.getJSONObject(j).getJSONArray("topics");
						for(int l=0;l<topics.length();l++){
							user_topic.put(results.getJSONObject(j).getInt("id"),topics.getJSONObject(l).getInt("id"));
							if (!topic_id.containsKey(topics.getJSONObject(l).getInt("id")))
								topic_id.put(topics.getJSONObject(l).getInt("id"),topics.getJSONObject(l).getString("name"));
						}

					}
					}
					catch(JSONException e)
					{	
						System.out.println("json exception:\n"+e.toString());
						
					}*/
				}
					

			}
				catch(Exception e)
				{
					String group_error = "group: "+groupidlist.get(id)+",offset: "+i+" error\n";
					fw3.write(group_error,0,group_error.length());
					fw3.flush();
					System.out.println("internet exception:\n"+e.toString());
				}
			}
			System.out.println("group:"+groupidlist.get(id)+" finished\n");
			}
			
			catch(Exception e)
			{
				String group_error = "group: "+groupidlist.get(id)+"error\n";
				fw3.write(group_error,0,group_error.length());
				fw3.flush();
				System.out.println("unknown exception:\n"+e.toString());
			}

		}
}
	
	
	public static void outputusertopic(Map<Integer,Integer> user_topic,FileWriter fw1) throws IOException
	{
		Iterator it1 = user_topic.entrySet().iterator();
		while(it1.hasNext()){
			Map.Entry<Integer, Integer> entry = (Entry<Integer, Integer>) it1.next();
			int key = entry.getKey();
			int value = entry.getValue();
			String temp = key+","+value+"\n";
			fw1.write(temp,0,temp.length());
			fw1.flush();
		}
	}
	
	public static void outputtopicid(Map<Integer,String> topic_id,FileWriter fw2) throws IOException
	{
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
}
