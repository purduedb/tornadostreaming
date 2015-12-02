package edu.purdue.cs.tornado.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import edu.purdue.cs.tornado.loadbalance.Cell;
import edu.purdue.cs.tornado.loadbalance.Partition;

public class CellSerializer extends  com.esotericsoftware.kryo.Serializer<Cell> {

	@Override
	public void write(Kryo kryo, Output output, Cell cell) {
		kryo.writeObjectOrNull(output,cell.getCoords(),double[].class);
		kryo.writeObjectOrNull(output,cell.getDimensions(),double[].class);
		kryo.writeObjectOrNull(output,cell.getIndex(),int.class);
		kryo.writeObjectOrNull(output,cell.getCost(),double.class);
		
		
	}

	@Override
	public Cell read(Kryo kryo, Input input, Class<Cell> type) {
		
		Cell cell = new Cell();
		cell.setCoords(kryo.readObjectOrNull(input,double[].class));
		cell.setDimensions(kryo.readObjectOrNull(input, double[].class));
		cell.setIndex(kryo.readObjectOrNull(input, int.class));
		cell.setCost(kryo.readObjectOrNull(input, double.class));
		return cell;
	}

}
