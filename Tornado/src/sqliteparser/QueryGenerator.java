package sqliteparser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URL;

import edu.purdue.cs.tornado.helper.LatLong;

public class QueryGenerator {
static String file_path="/Users/jaideepjuneja/Downloads/temp.csv";
static String base = "select * from Tweets t where t.loc inside ";
static String appender = " and contains (";

static String newBase = "select * from Tweets T where contains (T.txt,\"";
static String newAppender = "\") ";
static String orderTerm = "order by dist(";
static String closingTerm = ",T.loc) + dist(\"";

//static String SecTerm = "+ dist(\"";

static String limitTerm = "\",T.txt) limit(3)";


static LatLong latlong;

	public static void main(String args[]){
		try {
		BufferedReader br = new BufferedReader(new FileReader(file_path));
		
		//URL path = ClassLoader.getSystemResource("hello.txt");
		
		int i = 0;
		String line;
		while((line = br.readLine())!=null)
		{
			
			if(i>10)
			{
				break;
			}
			i++;
			
			BufferedWriter bw = new BufferedWriter(new FileWriter("/Users/jaideepjuneja/Downloads/ar8ahmed-tornado-1ee21b3c389c/Tornado/src/sqliteparser/test.txt",true));
			
			System.out.println(line);
			String[] breakd = line.split(",");
			String[] newbreak = breakd[5].split(" ");
			//ArrayList
			
			if(newbreak.length < 5){
				i--;
				continue;
			}
			
			String key1 = newbreak[0];
			String key2 = newbreak[1];
			String key3 = newbreak[2];
			String key4 = newbreak[3];
			String key5 = newbreak[4];
			
			
			String thequery = base + "(" + breakd[2] + "," + breakd[3] + ")" + appender + key1 + "," + key2 + "," + key3 + "," + key4 + "," + key5 + ")" + "\n";
			
			//String thequery = newBase + key1 + "," + key2 + "," + key3 + "," + key4 + "," + key5 +newAppender+orderTerm+ breakd[2] + "," + breakd[3] + closingTerm + key1 + "," + key2 + "," + key3 + "," + key4 + "," + key5+limitTerm +"\n";
			System.out.println(thequery);

			bw.write(thequery);
			bw.close();
		}
		
		br.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Caught it!!!!!!");
			
		}
		
		
		
			
	}
}
