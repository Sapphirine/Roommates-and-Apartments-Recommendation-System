/**
 * 
 */
package jsonconverter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Zhicheng
 *
 */
public class eventcrawl {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		BufferedReader br=new BufferedReader(new FileReader("data/id_group.csv"));
		FileWriter fw = new FileWriter("data/event_name.csv");
		FileWriter fw1 = new FileWriter("data/group_event.csv");
		FileWriter fw2 = new FileWriter("data/event_location.csv");
		FileWriter fw3 = new FileWriter("data/event_error.csv");

		
		String line = new String();
		ArrayList groupidlist =new ArrayList ();
		int count = 1;
		int offset,total_count;

		
		while((line=br.readLine())!= null){
			String[] values = line.split(",",-1);
			groupidlist.add(Integer.parseInt(values[0]));
		}
		
		

		for(int id=0;id<groupidlist.size();id++){
			System.out.println("group:"+groupidlist.get(id)+" start\n");
			try{
			Thread thread = Thread.currentThread();
			thread.sleep(500);
			String str = "https://api.meetup.com/2/events?key=74669656f72194c5e3c5976116e291&group_id="+groupidlist.get(id)+"&order=time&limited_events=False&desc=false&offset=0&format=json&only=id%2Cname%2Cvenue&page=1&fields=&sign=True";
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
					thread = Thread.currentThread();
				    thread.sleep(1000);
				str = "https://api.meetup.com/2/events?key=74669656f72194c5e3c5976116e291&group_id="+groupidlist.get(id)+"&order=time&limited_events=False&desc=false&offset="+i+"&format=json&only=id%2Cname%2Cvenue&page=200&fields=&sign=True";
				url= new URL(str);
				
				scan=new Scanner(url.openStream());
				data = new String ();
				
				while (scan.hasNext())
					data +=scan.nextLine();
				scan.close();
				
				obj = new JSONObject(data); 
				results = obj.getJSONArray("results");
				
				for(int j=0;j<results.length();j++){
					String group_event = groupidlist.get(id)+","+results.getJSONObject(j).getString("id")+","+count+"\n";
					String event_name = count+","+results.getJSONObject(j).getString("name")+"\n";
					fw1.write(group_event,0,group_event.length());
					fw1.flush();
					fw.write(event_name,0,event_name.length());
					fw.flush();
					try{
						String event_loc = count+","+results.getJSONObject(j).getJSONObject("venue").getDouble("lon")+","+results.getJSONObject(j).getJSONObject("venue").getDouble("lat")+"\n";
						fw2.write(event_loc,0,event_loc.length());
						fw2.flush();

					}
					catch(JSONException e){
						
					}
					count++;
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

}
