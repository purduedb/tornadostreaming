package edu.purdue.cs.tornado.atlas.SGB.global;

public class LinfinityDistance implements Distance  {

	public LinfinityDistance() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public double getDistance(int metric, Tuple t1, Tuple t2, int[] Fileds) {
		// TODO Auto-generated method stub
		if(metric==AttrType.Linfinity)
		{
			double results=0;
			if(Fileds.length<=t1.schema.getLength()||
					Fileds.length<=t2.schema.getLength()	)
			{
				for(int i:Fileds)
				{
					float f1=Float.valueOf(t1.getFiled(i).toString());
					float f2=Float.valueOf(t2.getFiled(i).toString());
					
					if(results<Math.abs(f1-f2))
					{
						results=Math.abs(f1-f2);
					}
					
				}
				
				return results;
				
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
