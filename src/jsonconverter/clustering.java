/**
 * 
 */
package jsonconverter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Zhicheng
 *
 */
public class clustering {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		ArrayList grouplist = new ArrayList ();
		BufferedReader br=new BufferedReader(new FileReader("data/id_group.csv"));
		FileWriter fw = new FileWriter("data/source_target_weight.csv");
		String line = new String();

		
		while((line=br.readLine())!= null){
			String[] values = line.split(",",-1);
			grouplist.add(Integer.parseInt(values[0]));
		}
		int group_num = grouplist.size();
		System.out.println(group_num);

		//obtain a group list
		
		
		br=new BufferedReader(new FileReader("data/user_group.csv"));

		ArrayList<ArrayList> user_group =new ArrayList<ArrayList> (group_num);
		int i,j,k,index;
		for(i=0;i<group_num;i++)
			{user_group.add(new ArrayList());}
		while((line=br.readLine())!= null){
			String[] values = line.split(",",-1);
			index=grouplist.indexOf(Integer.parseInt(values[1]));
			if(index !=-1){
				user_group.get(index).add(Integer.parseInt(values[0]));
			}
		}			
		
		int weight = 0;
		
		
		
		for(i=0;i<group_num;i++){
			for(j=i+1;j<group_num;j++){
				for(k=0;k<user_group.get(j).size();k++){
					if(user_group.get(i).contains(user_group.get(j).get(k)))
						weight++;		
				}
				if (weight!=0){
					line = grouplist.get(i)+","+grouplist.get(j)+","+weight+"\n";
					//line = i+","+j+","+weight+"\n";
					System.out.println(line);
					fw.write(line,0,line.length());
					fw.flush();
				}
				weight=0;
			}
		}
	}

}
