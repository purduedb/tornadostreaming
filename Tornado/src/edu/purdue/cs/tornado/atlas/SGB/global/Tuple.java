package edu.purdue.cs.tornado.atlas.SGB.global;

import edu.purdue.cs.tornado.atlas.SGB.index.Rectangle;




public class Tuple {

	Object [] items;
	protected Schema schema;
	 
	public Schema getSchema() {
		return schema;
	}

	public void setSchema(Schema schema) {
		this.schema = schema;
	}

	public Tuple(Schema schema) {
		// TODO Auto-generated constructor stub
		this.items=new Object[schema.getLength()];
		this.schema=schema;
	}
	
	/**
	 * 
	 * @param i
	 * @param o
	 */
	public void setFiled(int i, Object o)
	{
		this.items[i]=o;
	}
	
	/**
	 * 
	 * @param i
	 * @return
	 */
	public Object getFiled(int i)
	{
		if(i>items.length)
			 throw new IllegalStateException("invalid attribute index");
		
		return items[i];
	}
	
	/**
	 * return the rectangle represent by one point
	 * @return
	 */
	public Rectangle getRectangleForPoint()
	{
		Rectangle rct=new Rectangle();
		
		Object[] values=this.getAllFields();
		 
	
		rct.set(Float.valueOf(values[1].toString()), 
				Float.valueOf(values[2].toString()),
				Float.valueOf(values[1].toString()),
				Float.valueOf(values[2].toString())
				); 
		
		return rct;
	}
	
	/**
	 * return the rectangle represent by one point
	 * @return
	 */
	public Rectangle getRectangleForRange(float eps)
	{
		Rectangle rct=new Rectangle();
		
		Object[] values=this.getAllFields();
		
		float x1=Float.valueOf(values[1].toString());
		float y1=Float.valueOf(values[2].toString());
		
		rct.set(x1-eps, 
				y1-eps,
				x1+eps,
				y1+eps
				); 
		
		return rct;
	}
	
	
	  /**
	   * Returns generic values for all fields.
	   */
	  public Object[] getAllFields() {

	    Object[] values = new Object[schema.getCount()];
	    for (int i = 0; i < values.length; i++) {
	      values[i] = getFiled(i);
	    }
	    return values;

	  } // public Object[] getAllFields()

	  /**
	   * Sets all fields, given generic values.
	   */
	  public void setAllFields(Object... values) {

	    for (int i = 0; i < values.length; i++) {
	    	setFiled(i, values[i]);
	    }

	  } // public void setAllFields(Object... values)

	 public String toString()
	 {
		 StringBuffer s= new StringBuffer();
		 
		 Object[] os=this.getAllFields();
		 
		for(Object o:os)
		{
			s.append(o.toString()+" ");
		}
		 
		 return s.toString();
		 
		 
	 }

	  /**
	   * Pads the current output to make columns line up.
	   * 
	   * @param fieldLen minimum length of the current field
	   * @param outputLen length of the current output so far
	   */
	  protected void padOutput(int fieldLen, int outputLen) {

	    fieldLen = Math.max(Schema.MIN_WIDTH, fieldLen);
	    for (int j = 0; j < fieldLen - outputLen; j++) {
	      System.out.print(' ');
	    }

	  } // protected void padOutput(int fieldLen, int outputLen)
	  
	  @Override
	  public boolean equals(Object obj) {
	  	// TODO Auto-generated method stub
	  	if(!(obj instanceof Tuple))
	  		return false;
	  	
	  	Tuple t=(Tuple)obj;
	  	
	  	//possible way
	  	return t.toString().equals(this.toString());
	  }

	  @Override
	  public int hashCode() {
	  	// TODO Auto-generated method stub
		  Object[] os=this.getAllFields();
		  
		  final int prime = 31;
		  int result=1;
		  for(Object o:os)
			{
			  result=result*prime+o.hashCode();
			}
		  
	  	return result;
	  }

}
