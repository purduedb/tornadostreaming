package edu.purdue.cs.tornado.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import edu.purdue.cs.tornado.helper.IndexCellCoordinates;

public class IndexCellCoordinatesSerializer  extends  com.esotericsoftware.kryo.Serializer<IndexCellCoordinates>{
	@Override
	public IndexCellCoordinates read(Kryo kryo, Input input, Class<IndexCellCoordinates> type) {
		IndexCellCoordinates indexCellCoordinates
		= new IndexCellCoordinates(kryo.readObjectOrNull(input,Integer.class), kryo.readObjectOrNull(input,Integer.class));
		return indexCellCoordinates;
	}

	@Override
	public void write(Kryo kryo, Output output, IndexCellCoordinates indexCellCoordinates) {
		kryo.writeObjectOrNull(output,indexCellCoordinates.getX(),Integer.class);
		kryo.writeObjectOrNull(output,indexCellCoordinates.getY(),Integer.class);
		
	}

	
}
