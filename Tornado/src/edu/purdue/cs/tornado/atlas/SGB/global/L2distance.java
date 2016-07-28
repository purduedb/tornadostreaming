package edu.purdue.cs.tornado.atlas.SGB.global;

public class L2distance implements Distance {

	public static String metric;

	@Override
	public double getDistance(int metric, Tuple t1, Tuple t2, int[] Fileds) {
		// TODO Auto-generated method stub
		
		if(metric==AttrType.L2)
		{
			double results=0;
			//System.out.println(t1.schema.getLength());
			//System.out.println(t2.schema.getLength());
			//System.out.println(Fileds.length);
			
			if(Fileds.length<t1.schema.getLength()||
					Fileds.length<t2.schema.getLength()	)
			{
				for(int i:Fileds)
				{
					float f1=(Float)(t1.getFiled(i));
					float f2=(Float)(t2.getFiled(i));
					results=results+(f1-f2)*(f1-f2);
				}
				
				return Math.sqrt(results);
				
			}else
			{
				 throw new IllegalStateException("invalid attribute index");
				
			}
		
		}else
		{
			
			return 0 ;
		}
		
		
	} 
	

	
	

}
