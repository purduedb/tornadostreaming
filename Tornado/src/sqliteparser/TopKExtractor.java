package sqliteparser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class TopKExtractor {

	public static ArrayList <String> extractedKwords = new ArrayList<String>();
	public static void extractKeywords(String sql) {

		SQLiteLexer lexer = new SQLiteLexer(new ANTLRInputStream(sql));
		SQLiteParser parser = new SQLiteParser(new CommonTokenStream(lexer));


		ParseTree treetry  = parser.select_stmt();

		ParseTreeWalker.DEFAULT.walk(new SQLiteBaseListener(){ 

			@Override
			public void enterExpr(@NotNull SQLiteParser.ExprContext ctx)  {
				if(ctx.getText().startsWith("contains")) 
				{
					String afterParen = ctx.getText().substring(9, ctx.getText().length()-1);
					//System.out.println(afterParen);
					String seper[] = afterParen.split("\""); // split on "
					System.out.println(seper[1]);

					String newseper = seper[1].substring(0, seper[1].length());
					String kwords[] = newseper.split(",");

					for(String kword : kwords)
					{
						extractedKwords.add(kword);
					}
				}
			}

		},treetry);



	}

	public static ArrayList<Integer> limitColl = new ArrayList<Integer>();
	
	static int limit;
	int limflag=0;

	public static void extractLimit(String sql){


		SQLiteLexer lexer = new SQLiteLexer(new ANTLRInputStream(sql));
		SQLiteParser parser = new SQLiteParser(new CommonTokenStream(lexer));


		ParseTree treetry  = parser.select_stmt();

		ParseTreeWalker.DEFAULT.walk(new SQLiteBaseListener(){ 
			@Override
			public void enterExpr(@NotNull SQLiteParser.ExprContext ctx)  { 
				//System.out.println("the text from parser is: " + ctx.getText());
				if(ctx.getText().startsWith("limit")) 
				{
					String furtherTerm = ctx.getText().substring(6,ctx.getText().length()-1);
					limit = Integer.parseInt(furtherTerm);
					limitColl.add(limit);
					//System.out.println(furtherTerm);
					//limflag = 1;

					//System.exit(1);
				}
			}


		},treetry);

		//	       if(limflag == 1)
		//    	   {
		//    		   return;
		//    	   }

	}

	static ArrayList<ArrayList<String>> mykey = new ArrayList<ArrayList<String>>();

	public static void extractExprKey(String sql){

		SQLiteLexer lexer = new SQLiteLexer(new ANTLRInputStream(sql));
		SQLiteParser parser = new SQLiteParser(new CommonTokenStream(lexer));


		ParseTree treetry  = parser.select_stmt();


		ParseTreeWalker.DEFAULT.walk(new SQLiteBaseListener(){ 

			@Override
			public void enterExpr(@NotNull SQLiteParser.ExprContext ctx)  { 
				ArrayList<String> mykeys = new ArrayList<String>();
				if(ctx.getText().startsWith("contains"))
				{
					String toextract = ctx.getText();

					//System.out.println(toextract);

					String extracted = toextract.substring(9, toextract.length()-1);

					String theKeys[] = extracted.split(",");

					//	 mykey.add(TextHelpers.transformIntoSortedArrayListOfString(toextract));

					for(String theKey: theKeys)
					{
						if(!theKey.endsWith(".txt"))
						{
							if(theKey.startsWith("\""))
							{
								theKey=theKey.substring(1);
							}
							if(theKey.endsWith("\""))
							{
								theKey=theKey.substring(0, theKey.length()-1);
							}

							//System.out.println(theKey);

							mykeys.add(theKey);
						}
					}
					mykey.add(mykeys);

					//System.out.println(extracted);
				}

			}	

		},treetry);


	}

	static ArrayList<Double> x = new ArrayList<Double>();
	static ArrayList<ArrayList<Double>> z = new ArrayList<ArrayList<Double>>();

	public static void addDoubleToArray(Double addvar)
	{
		x.add(addvar);
	}

	public static void addArrayListtoArrayList(ArrayList<Double> y)
	{
		z.add(y);
	}

	public ArrayList<Double> retx ()
	{
		return x;
	}

	public static ArrayList<ArrayList<Double>> retz ()
	{
		return z;
	}
	static int sign = 0;

	public static void extractExprInt(String sql)
	{
		SQLiteLexer lexer = new SQLiteLexer(new ANTLRInputStream(sql));
		SQLiteParser parser = new SQLiteParser(new CommonTokenStream(lexer));


		ParseTree treetry  = parser.select_stmt();


		ParseTreeWalker.DEFAULT.walk(new SQLiteBaseListener(){ 

			@Override
			public void enterExpr(@NotNull SQLiteParser.ExprContext ctx)  {
				if(ctx.getText().contains("dist") && ctx.getText().contains("+"))
				{
					//System.out.println(ctx.getText());
					//System.out.println(ctx.getText().substring(5,ctx.getText().indexOf(')')));
					String mytext = ctx.getText().substring(5,ctx.getText().indexOf(')'));
					String[] locs = mytext.split(",");
					
					//System.out.println(locs[0]);
					
					//					
					Double lat = Double.parseDouble(locs[0]);
					Double lon = Double.parseDouble(locs[1]);
					x.add(lat);
					x.add(lon);
					z.add(x);
					x = new ArrayList<Double>();
					//System.out.println(lat);
					//System.out.println(lon);
				}	
			}
		},treetry);


	}

	
	public ArrayList<Integer> returnlimit()
	{
		return limitColl;
	}

	public ArrayList<ArrayList<Double>> returnExprInt()
	{
		return z;
	}
	
	public ArrayList<ArrayList<String>> returnKeywords()
	{
		return mykey;
	}


	public void readfromFile() throws Exception
	{
		BufferedReader br = new BufferedReader(new FileReader("/Users/jaideepjuneja/Downloads/ar8ahmed-tornado-adca970d5b12-2/Tornado/src/sqliteparser/test.txt"));
		String line;
		//System.out.println();
		while((line = br.readLine()) !=null) {
			extractLimit(line);
			extractKeywords(line);
			System.out.println(limit);
			
			//extractExprKey(line);
			extractExprInt(line);
			
			
		}
		int c = 0;
		System.out.println("The arrayList z starts from here:");

		for(ArrayList<Double> e : z)
		{
			System.out.println(c++);
			for(Double t:e)
			{
				System.out.println(t);
			}
		}
		br.close();



	}

	
	public static ArrayList<Integer> readLimFromFile() throws Exception {
		BufferedReader br = new BufferedReader(new FileReader("/Users/jaideepjuneja/Downloads/ar8ahmed-tornado-adca970d5b12-2/Tornado/src/sqliteparser/test.txt"));
		String line;
		//System.out.println();
		while((line = br.readLine()) !=null) {
			extractLimit(line);
			//System.out.println(limit);
		}
		br.close();
		return limitColl;
	}
	
	
	public static ArrayList<ArrayList<String>> readKeyFromFile() throws Exception
	{
		BufferedReader br = new BufferedReader(new FileReader("/Users/jaideepjuneja/Downloads/ar8ahmed-tornado-adca970d5b12-2/Tornado/src/sqliteparser/test.txt"));
		String line;
		//System.out.println();
		while((line = br.readLine()) !=null) {
			extractExprKey(line);
		}
		br.close();		
		
		return mykey;
	}

	public static ArrayList<ArrayList<Double>> readIntFromFile() throws Exception
	{
		BufferedReader br = new BufferedReader(new FileReader("/Users/jaideepjuneja/Downloads/ar8ahmed-tornado-adca970d5b12-2/Tornado/src/sqliteparser/test.txt"));
		String line;
		//System.out.println();
		while((line = br.readLine()) !=null) {
			extractExprInt(line);
		}
		br.close();		
		
		return z;
	}
	
	public static void main(String args[]) throws Exception {
		//readfromFile();
		//String sql = "select * from hello S where contains(S.txt,\"cafe,restaurant,hotel\") order by 1/DIST(S.loc,q1.loc) limit(3)";
		TopKExtractor topk = new TopKExtractor();
		//topk.readfromFile();
		int c = 0;
		ArrayList<Integer> limColl = topk.readLimFromFile();
		System.out.println("The Limits are: ");
		for(Integer e: limColl) {
			System.out.println(c++);
			System.out.println(e);
		}
		
		ArrayList<ArrayList<Double>> Coor = topk.readIntFromFile();
		System.out.println("The Coors are: ");
		c = 0;
		for(ArrayList<Double> e : Coor)
		{
			System.out.println(c++);
			for(Double t : e)
			{
				System.out.println(t);
			}
		}
		
		c = 0;
		
//		ArrayList<ArrayList<String>> KeyWords = topk.readKeyFromFile();
//		
//		for(ArrayList<String> KeyWord : KeyWords)
//		{
//			System.out.println(c++);
//			for(String t : KeyWord)
//			{
//				System.out.println(t);
//			}
//		}
//		
		
		//		topk.extractKeywords(sql);
		//		topk.extractLimit(sql);
		//		System.out.println("The limit found is : "+topk.limit);
		//		for(String x : topk.extractedKwords)
		//		{
		//			System.out.println(x);
		//		}
	}
}
