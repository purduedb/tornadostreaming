package sqliteparser;

//import storm.starter.

//import org.apache.storm.starter.bolt.


//package org.apache.storm.starter.bolt;

import java.util.ArrayList;
import java.util.Map;
import org.apache.storm.Config;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.TupleUtils;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.log4j.Logger;
//import org.apache.storm.starter.tools.NthLastModifiedTimeTracker;
//import org.apache.storm.starter.tools.SlidingWindowCounter;



public class StringClassifierBolt extends BaseRichBolt {
    
	private static int flag = 0;
    
    public static int getflag(){
     return flag;
    }
    
    public static void setflag(int data){
        flag = data;
    }

    //added
    
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
                    //System.out.println("Within Distance query");
                    return;
                   
                }
//                else
//                {
//                    System.out.println("not encountered wdist");
//                    return;
//                }
                //return;
                //System.out.println(ctx.getText());
                //System.out.println("Helloss");
                //}
                
                return;
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
                //System.out.println("Conditional limit query");
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
               // System.out.println("Spatial keyword topk query");
                return;
               // System.out.println(ctx.getText());
                //System.out.println("Helloss");
                }
                return;
            }
        }, treetry);
        
        
        //return flag;
    }
    
    
    //public static ArrayList <String> container = new ArrayList<String>();
    public static ArrayList<ArrayList<String>> extract(String sql)
    {
    	ArrayList<ArrayList<String>> extractor = new ArrayList<ArrayList<String>>();
    	ArrayList <String> container = new ArrayList<String>();
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
//               // System.out.println(ctx.getText());
//                //System.out.println("Helloss");
//                }
 
                if(ctx.getText().toLowerCase().contains("and") || ctx.getText().toLowerCase().contains("or"))
                {
                  
                   //String [] t = new String[5];
                   //String n[];
                   //System.out.println(ctx.getText());
                   String[] x = ctx.getText().split("and");
                   //System.out.println(x.toString();
                   for(String i:x)
                   {
                	   ArrayList<String> nexus  = new ArrayList<String>();
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
                               extractor.add(nexus);
                           }
                           
                          //n = t;
                       }
                       else{
                           if(!container.contains(i))
                                   {
                                       container.add(i);
                                       nexus.add(i);
                                       //System.out.println("added "+i);
                                   }//System.out.println(n);
                      // System.out.println("i is " + i);
                           if(!nexus.isEmpty())
                               extractor.add(nexus);
                       }
                   }
                 
                
                }
               
                    
                return;
            }
            
            
            
        }, treetry);
        
        if(container.size() <= 0)
        	{
        	return null;
        	}
        return extractor;
        
        
//        if(extractor.size() == 0) {
//        return null;
//        }
//        else
//        {
//            return extractor;
//        }
    }
    
    
    
    
    //added
	
     private OutputCollector ocollecter;
    public void prepare(Map conf, TopologyContext context, OutputCollector collector)
    {
        this.ocollecter = collector;
    }
    
    public void declareOutputFields(OutputFieldsDeclarer declarer)
    {
        declarer.declare(new Fields("line","classified"));
    }
    
    public void execute(Tuple tuple)
    {
        String toClassify = tuple.getString(0);
        //String to = tuple.getString(1);
        //int dur = tuple.getInteger(2);
        //int classified = classify(toClassify);
        
        check(toClassify);
        
        
        
        
        if(getflag() == 1)
        {
        	System.out.println("The line obtained is : " + toClassify);
            System.out.println("QueryType: Within Distance query");
            setflag(0);
        }
        else if(getflag() == 2)
        {
        	System.out.println("The line obtained is : " + toClassify);
            System.out.println("QueryType: Conditional limit query");
            setflag(0);
        }
        else if(getflag() == 3)
        {
        	System.out.println("The line obtained is : " + toClassify);
            System.out.println("QueryType: Spatial keyword topk query");
            setflag(0);
        }
        else
        {
        	System.out.println("The line obtained is : " + toClassify);
            System.out.println("QueryType: Spatial Keyword Filter Query");
            setflag(0);
            ArrayList<ArrayList<String>> newerClassified = extract(toClassify);
           // extract(toClassify);
            
            System.out.println("newerClassified contains ");
            for(ArrayList<String> e : newerClassified)
            {
            	for(String r : e)
            	
                System.out.println(r);
            }
            
            
        }
        
        
        //ocollecter.emit(new Values(toClassify));
        //ocollecter.emit(new Values(fr + "-" + to,dur));
    }
    
    public void cleanup()
    {
        System.out.println("cleaned up");
        
    }
}