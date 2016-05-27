package edu.purdue.cs.tornado.atlas.SGB.global;


/**
 * 
 * @author tang49
 * the database for the SGB-All operation 
 */
public class InMemorySGB {

	
	public static String groupindex;
	
	//public static GroupAll groupOptoer;
	
	public InMemorySGB() {
		// TODO Auto-generated constructor stub
	}

	public void init(String metric, String duplicate, String groupindex)
	{
		this.groupindex=groupindex;
		
	}

}
