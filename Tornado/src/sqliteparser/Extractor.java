package sqliteparser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class Extractor {
	private static int flag = 0;

	public static int getflag(){
		return flag;
	}

	public static void setflag(int data){
		flag = data;
	}

	static int sign = 0;
	public void check(String sql)
	{
		SQLiteLexer lexer = new SQLiteLexer(new ANTLRInputStream(sql));
		SQLiteParser parser = new SQLiteParser(new CommonTokenStream(lexer));


		ParseTree treetry  = parser.select_stmt();


		ParseTreeWalker.DEFAULT.walk(new SQLiteBaseListener(){

			@Override
			public void enterPartition_term(@NotNull SQLiteParser.Partition_termContext ctx)  {//enterOrder(@NotNull SQLiteParser.ExprContext ctx) {
				// Check if the expression is a function call.
				//if (ctx.function_name() != null) {
				// Yes, it was a function call: add the name of the function
				// to out list.
				//functionNames.add(ctx.function_name().getText());
				//}
				//if(ctx.getText() != null)
				//{
				//System.out.println(ctx.getText());
				if(ctx.getText().contains("withindistance"))
				{
					setflag(1);
					System.out.println("Within Distance query");
					return;

				}
				else
				{
					//System.out.println("not encountered wdist");
					return;
				}
				//return;
				//System.out.println(ctx.getText());
				//System.out.println("Helloss");
				//}

				//  return;
			}


			@Override
			public void enterLimit(@NotNull SQLiteParser.LimitContext ctx)  {//enterOrder(@NotNull SQLiteParser.ExprContext ctx) {
				// Check if the expression is a function call.
				//if (ctx.function_name() != null) {
				// Yes, it was a function call: add the name of the function
				// to out list.
				//functionNames.add(ctx.function_name().getText());
				//}

				if(ctx.getText() != null && getflag() == 0)
				{
					setflag(2);
					// System.out.println("Conditional limit query");
					return;
					// System.out.println(ctx.getText());
					//System.out.println("Helloss");
				}

				return;
			}


			@Override
			public void enterExpr(@NotNull SQLiteParser.ExprContext ctx)  {//enterOrder(@NotNull SQLiteParser.ExprContext ctx) {
				// Check if the expression is a function call.
				//if (ctx.function_name() != null) {
				// Yes, it was a function call: add the name of the function
				// to out list.
				//functionNames.add(ctx.function_name().getText());
				//}
				if(ctx.getText().contains("limit") && getflag() == 0)
				{
					setflag(3);
					System.out.println("Spatial keyword topk query");
					return;
					// System.out.println(ctx.getText());
					//System.out.println("Helloss");
				}
				return;
			}
		}, treetry);


		//return flag;
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

	public static void extractExprInt(String sql)
	{
		SQLiteLexer lexer = new SQLiteLexer(new ANTLRInputStream(sql));
		SQLiteParser parser = new SQLiteParser(new CommonTokenStream(lexer));


		ParseTree treetry  = parser.select_stmt();

		ParseTreeWalker.DEFAULT.walk(new SQLiteBaseListener(){
			@Override
			public void enterExpr(@NotNull SQLiteParser.ExprContext ctx)  {
				try {
					//        		   System.out.println(ctx.getText());

					double addition = Double.parseDouble(ctx.getText());
					if(sign == 0)
					{
						addDoubleToArray(addition);
					}
					sign = 0;
					if(addition < 0)
					{
						sign = 1;
					}

					//System.out.println("x is "+x);
				} catch (NumberFormatException e)
				{
					//System.out.println("problem");
					//z.add(x);
					if(x.size() == 0) {
						//System.out.println("problem 0"); //do something
					}
					else {
						addArrayListtoArrayList(x);
					}	

					//               		System.out.println("z has the following: ");
					//                  int ctw = 0;
					//                  for(ArrayList <Double>temp : retz())
					//                  {
					//               	   System.out.println(ctw++);
					//               	   for(Double n : temp)
					//               	   {
					//               		   System.out.println(n);
					//               	   }
					//                  }



					//               		System.out.println("x has this: ");
					//              		for(Double a:x) {
					//              			System.out.println(a);
					//              		}
					//x.clear();
					x = new ArrayList<Double>();

					//               		ArrayList <Double> abc = intdoub;
					//               		holdes.add(abc);
					//               		System.out.println("this is not a double");
					//               		intdoub.clear();
				}
				//    		   finally {
				//          			addArrayListtoArrayList(x);
				//          		}

			} 
		}
		,treetry);
	}




	public static ArrayList<ArrayList<Double>> read() throws Exception
	{
		//String filepath=(String) spoutConf.get(FILE_PATH);
		///Users/jaideepjuneja/Downloads/ar8ahmed-tornado-1ee21b3c389c/Tornado/src/sqliteparser/test.txt
		BufferedReader br = new BufferedReader(new FileReader("resources/test.txt"));
		//BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(FilePath)));//new FileReader(FilePath));
		String line;
		//System.out.println();
		while((line = br.readLine()) !=null) {
			extractExprInt(line);
		}
		//z.add(x);
		addArrayListtoArrayList(x);
		br.close();
		return retz();
	}

	static ArrayList<ArrayList<String>> mykey = new ArrayList<ArrayList<String>>();

	public static void extractExprKey(String sql)
	{
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
						//System.out.println(theKey);
						mykeys.add(theKey);
					}
					mykey.add(mykeys);

					//System.out.println(extracted);
				}
			}	 
		}, treetry);



	}



	public static ArrayList<ArrayList<String>> readKeys() throws Exception {
		BufferedReader br = new BufferedReader(new FileReader("resources/test.txt"));
     //	BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(FilePath)));//new FileReader(FilePath));
		String line;
		//System.out.println();
		while((line = br.readLine()) !=null) {
			extractExprKey(line);
		}
		//z.add(x);


		br.close();


		return mykey;
	}




	public static void main(String[] args) throws Exception {
		//URL path = ClassLoader.getSystemResource("test.txt");test.txt
		//path.toURI();
		Extractor extr = new Extractor();
		//       BufferedReader br = new BufferedReader(new FileReader("/Users/jaideepjuneja/Downloads/ar8ahmed-tornado-adca970d5b12-2/Tornado/src/sqliteparser/hello.txt"));
		//       
		     //BufferedReader br = new BufferedReader(new FileReader(new File(path.toURI())));	
		//       String line;
		//       //System.out.println();
		//       while((line = br.readLine()) !=null)
		//       {
		//    	   extractExprInt(line);
		//    	   
		//       }
		//       extr.z.add(extr.x);
		//z.add(x);
		ArrayList<ArrayList<Double>> newarray = read();
		System.out.println("newarray has the following: ");
		int ctw = 0;
		for(ArrayList <Double> temp : newarray)
		{
			System.out.println(ctw++);
			for(Double n : temp)
			{
				System.out.println(n);
			}
		}

	}



}
