/**
 * 
 */
package jsonconverter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Zhicheng
 *
 */
public class getuser {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		BufferedReader br=new BufferedReader(new FileReader("data/user_group.csv"));
		FileWriter fw = new FileWriter("data/allusers.csv");
		ArrayList userlist = new ArrayList ();
		String line = new String();
		
		while((line=br.readLine())!= null){
			String[] values = line.split(",",-1);
			if(!userlist.contains(Integer.parseInt(values[0]))){
				userlist.add(Integer.parseInt(values[0]));
				line = Integer.parseInt(values[0])+"\n";
				fw.write(line,0,line.length());
				fw.flush();
			}
        }

	}

}
