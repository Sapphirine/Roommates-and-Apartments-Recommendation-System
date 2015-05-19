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
import org.json.JSONObject;

/**
 * @author Zhicheng
 *
 */
public class usereventcrawl {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		BufferedReader br=new BufferedReader(new FileReader("data/allusers.csv"));
		String line = new String();
		ArrayList userlist =new ArrayList ();
		
		while((line=br.readLine())!= null){
			String[] values = line.split(",",-1);
			userlist.add((String) values[0]);
		}



	    

		
		
		
		
		br=new BufferedReader(new FileReader("data/group_event.csv"));
		FileWriter fw = new FileWriter("data/user_event.csv");
		FileWriter fw3 = new FileWriter("data/event_error.csv");

		
		ArrayList eventidlist =new ArrayList ();
		int total_count,offset,eventid = 0;
		

		
		while((line=br.readLine())!= null){
			String[] values = line.split(",",-1);
			eventidlist.add((String) values[1]);
		}
						
		for(int id=0;id<eventidlist.size();id++){
			System.out.println("event:"+eventidlist.get(id)+" start\n");
			try{
				Thread thread = Thread.currentThread();
			    thread.sleep(500);
			String str = "https://api.meetup.com/2/rsvps?key=74669656f72194c5e3c5976116e291&event_id="+eventidlist.get(id)+"&order=event&desc=false&offset=0&photo-host=public&format=json&only=member&page=1&fields=&sign=True";
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
					str = "https://api.meetup.com/2/rsvps?key=74669656f72194c5e3c5976116e291&event_id="+eventidlist.get(id)+"&order=event&desc=false&offset="+i+"&photo-host=public&format=json&only=member&page=200&fields=&sign=True";
				url= new URL(str);
				
				scan=new Scanner(url.openStream());
				data = new String ();
				
				while (scan.hasNext())
					data +=scan.nextLine();
				scan.close();
				
				obj = new JSONObject(data); 
				results = obj.getJSONArray("results");
				
				for(int j=0;j<results.length();j++){
					eventid = id+1;
					if (userlist.contains(results.getJSONObject(j).getJSONObject("member").getInt("member_id"))){
					String user_event = results.getJSONObject(j).getJSONObject("member").getInt("member_id")+","+eventid+"\n";
					fw.write(user_event,0,user_event.length());
					fw.flush();
					}
					
				}
					

			}
				catch(Exception e)
				{
					String event_error = "event: "+eventidlist.get(id)+",offset: "+i+" error\n";
					fw3.write(event_error,0,event_error.length());
					fw3.flush();
					System.out.println("internet exception:\n"+e.toString());
				}
			}
			System.out.println("event:"+eventidlist.get(id)+" "+id+" finished\n");
			}
			
			catch(Exception e)
			{
				String event_error = "event: "+eventidlist.get(id)+"error\n";
				fw3.write(event_error,0,event_error.length());
				fw3.flush();
				System.out.println("unknown exception:\n"+e.toString());
			}
		}
}
}
