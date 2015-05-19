/**
 * 
 */
package jsonconverter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Zhicheng
 *
 */
public class eventlocation {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		BufferedReader br=new BufferedReader(new FileReader("data/group_event.csv"));
		FileWriter fw = new FileWriter("data/event_location.csv");
		FileWriter fw3 = new FileWriter("data/event_error.csv");

		
		String line = new String();
		ArrayList groupidlist =new ArrayList ();
		int count = 1;
		int offset,total_count;

		
		while((line=br.readLine())!= null){
			String[] values = line.split(",",-1);
			if(!groupidlist.contains(Integer.parseInt(values[0])))
			groupidlist.add(Integer.parseInt(values[0]));
		}
		
		

		for(int id=0;id<groupidlist.size();id++){
			System.out.println("group:"+groupidlist.get(id)+" start\n");
			try{
				Thread thread = Thread.currentThread();
			    thread.sleep(500);
			String str = "https://api.meetup.com/2/venues?key=74669656f72194c5e3c5976116e291&group_id="+groupidlist.get(id)+"&desc=False&offset=0&photo-host=public&format=json&page=1&fields=&sign=True";
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
			    str = "https://api.meetup.com/2/venues?key=74669656f72194c5e3c5976116e291&group_id="+groupidlist.get(id)+"&desc=False&offset="+i+"&photo-host=public&format=json&page=200&fields=&sign=True";
				url= new URL(str);
				
				scan=new Scanner(url.openStream());
				data = new String ();
				
				while (scan.hasNext())
					data +=scan.nextLine();
				scan.close();
				
				obj = new JSONObject(data); 
				results = obj.getJSONArray("results");
				
				for(int j=0;j<results.length();j++){
					String event_loc = count+","+results.getJSONObject(j).getDouble("lon")+","+results.getJSONObject(j).getDouble("lat")+"\n";
					fw.write(event_loc,0,event_loc.length());
					fw.flush();
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
				System.out.println("unknown exception:\n"+e.toString());			}


			
			

		}
	}

}
