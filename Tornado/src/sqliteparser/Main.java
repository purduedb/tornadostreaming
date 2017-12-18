package sqliteparser;

//package nl.bigo.sqliteparser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;



public class Main {
    
    
    private static int flag = 0;
    
    public static int getflag(){
     return flag;
    }
    
    public static void setflag(int data){
        flag = data;
    }
    int sign = 0;
    
    
    public static void check(String sql)
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
    
    //public static ArrayList<String> extractor = new ArrayList<String>();
    public static ArrayList <String> container = new ArrayList<String>();
    public static ArrayList<ArrayList<String>> cont = new ArrayList<ArrayList<String>>();
    public static ArrayList <Double> intdoub = new ArrayList<Double>();
    public static ArrayList<ArrayList<Double>> holdes = new ArrayList<ArrayList<Double>>();
    
    
    public static ArrayList<String> extract(String sql)
    {
         SQLiteLexer lexer = new SQLiteLexer(new ANTLRInputStream(sql));
        SQLiteParser parser = new SQLiteParser(new CommonTokenStream(lexer));

        
        ParseTree treetry  = parser.select_stmt();
        
        ParseTreeWalker.DEFAULT.walk(new SQLiteBaseListener(){
        
          @Override
            public void enterExpr(@NotNull SQLiteParser.ExprContext ctx)  {//enterOrder(@NotNull SQLiteParser.ExprContext ctx) {
                // Check if the expression is a function call.
                //if (ctx.function_name() != null) {
                    // Yes, it was a function call: add the name of the function
                    // to out list.
                    //functionNames.add(ctx.function_name().getText());
                //}
//                if(ctx.getText().contains("(") && ctx.getText().contains(")"))
//                {
//                    
//                    
//                    
//                //setflag(3);
//               // System.out.println("Spatial keyword topk query");
//                //return;
//                
//                //System.out.println("Helloss");
//                }
        	  //System.out.println(ctx.getText());
                if(ctx.getText().toLowerCase().contains("and") || ctx.getText().toLowerCase().contains("or"))
                {
                  
                   //String [] t = new String[5];
                   //String n[];
                   //System.out.println(ctx.getText());
                   String[] x = ctx.getText().split("and");
                   //System.out.println(x.toString();
                   for(String i:x)
                   {
                	   ArrayList<String> nexus = new ArrayList<String>();
                       if(i.contains("or"))
                       {
                           String[] t = i.split("or");
                           
                           if(t.length == 0)
                           {
                               System.out.println("Here i is "+i);
                               continue;
                           }
                           else{
                               for(String n : t)
                               {
                                   if(!container.contains(n))
                                   {
                                       container.add(n);
                                       nexus.add(n);
                                       //System.out.println("added "+n);
                                   }//System.out.println(n);
                               }
                              
                               cont.add(nexus);
                           }
                           
                          //n = t;
                       }
                       else{
                           if(!container.contains(i))
                                   {
                        	   			//cont.add(i);
                                       container.add(i);
                                       nexus.add(i);
                                       //System.out.println("added "+i);
                                   }//System.out.println(n);
                      // System.out.println("i is " + i);
                           if(!nexus.isEmpty())
                           cont.add(nexus);
                       }
                       
                       //cont.add(nexus); //added
                       
                   }
                 
                
                }
                else {
                	//System.out.println(ctx.getText());
                	try {
                	double x = Double.parseDouble(ctx.getText());
                	intdoub.add(x);
                	//System.out.println("x is "+x);
                	} catch (NumberFormatException e)
                	{
                		System.out.println("intdoub has this: ");
                		for(Double a:intdoub) {
                			System.out.println(a);
                		}
                		ArrayList <Double> abc = intdoub;
                		holdes.add(abc);
                		System.out.println("this is not a double");
                		//intdoub.clear();
                	}
                	
                }
               
                    
                return;
            }
            
            
            
        }, treetry);
        
        return null;
        
//        if(extractor.size() == 0) {
//        return null;
//        }
//        else
//        {
//            return extractor;
//        }
    }
    
    
    static ArrayList<ArrayList<String>> mykey = new ArrayList<ArrayList<String>>();
    
    public static void extractKeywords(String sql)
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
        		 
        		System.out.println(toextract);
        		 
        		 String extracted = toextract.substring(9, toextract.length()-1);
        		 
        		 String theKeys[] = extracted.split(",");
        		 
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
    
    
    
    
    public static void main(String[] args) throws Exception {
    	//URL path = ClassLoader.getSystemResource("test.txt");test.txt
        //path.toURI();
        
        BufferedReader br = new BufferedReader(new FileReader("/Users/jaideepjuneja/Downloads/ar8ahmed-tornado-adca970d5b12-2/Tornado/src/sqliteparser/hello.txt"));
        
        //BufferedReader br = new BufferedReader(new FileReader(new File(path.toURI())));	
        String line;
        //System.out.println();
        while((line = br.readLine()) !=null)
        {
           // System.out.println(line);
            check(line);
            //System.out.println(getflag());
            
            if(getflag() == 1)
            {
             //   System.out.println("Within Distance query");
            }
            else if(getflag() == 2)
            {
             //   System.out.println("Conditional limit query");
            }
            else if(getflag() == 3)
            {
               // System.out.println("Spatial keyword topk query");
            }
            else
            {
                //System.out.println("Spatial Keyword Filter Query");
                extractKeywords(line); //extract(line)
                						// extracting the keywords
            
//            System.out.println("container contains ");
//                for(String e : container)
//                {
//                    System.out.println(e);
//                }
//                
//                int nt = 0;
//                System.out.println("cont contains ");
//                for(ArrayList<String> e : cont)
//                {
//                	System.out.println(nt++);
//                	for(String hu : e)
//                	{
//                    System.out.println(hu);
//                	}
//                }
                
                
                
                
            }
            
            
            
            setflag(0);
            
            
           
        }
        br.close();
        
//        System.out.println("intdoub contains:");
//        for(Double d : intdoub)
//        {
//        	System.out.println(d);
//        }
//        int nt = 0;
//        System.out.println("mykey contains:");
//        for(ArrayList<String> e : mykey)
//          {
//          	System.out.println(nt++);
//          	for(String hu : e)
//          	{
//              System.out.println(hu);
//          	}
//          }
//        

        // The list that will hold our function names.
       // final List<String> functionNames = new ArrayList<String>();
        //String sql = "select count(*), rating from Source S Where OVERLAP (S.txt, \"cafe\") > 0 Group By rating, COLLECTIVE( Order by 1/DIST (S.loc, q1.loc) + OVERLAP(S.txt,\"cafe\") skip contains(collect(S.txt),S.txt) limit contains(collect(S.txt),\"cafe\"))";
       // String sql = "select *,over(partition by within distance(S.loc,3), Collective( skip contains(collect(S.txt),S.txt) Limit Contains(Collect(S.txt),\"cafe\")) Having Contains(Collect(G.txt),\"cafe\") Order by DIST (CENTROID(G),q1.loc) + DIAMETER(G)) From source S Where OVERLAP(S.txt, \"cafe\") > 0";
//        String sql = "select * from hello order by 1/DIST(S.loc,q1.loc) limit (2)";
//        check(sql);
//        System.out.println("this is my sqk");
//        System.out.println(getflag());
        /*       // The select-statement to be parsed.
        String sql = "SELECT log AS x FROM t1 \n" +
                "GROUP BY x\n" +
                "HAVING count(*) >= 4 \n" +
                "ORDER BY max(n) + 0";
*/
        // Create a lexer and parser for the input.
        
        

        
    
    }
}
