package edu.purdue.cs.tornado.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import edu.purdue.cs.tornado.loadbalance.Partition;

public class PartitionSerializer  extends  com.esotericsoftware.kryo.Serializer<Partition> {

	@Override
	public void write(Kryo kryo, Output output, Partition partition) {
		kryo.writeObjectOrNull(output,partition.getCoords(),double[].class);
		kryo.writeObjectOrNull(output,partition.getDimensions(),double[].class);
		kryo.writeObjectOrNull(output,partition.getIndex(),int.class);
		kryo.writeObjectOrNull(output,partition.getCost(),double.class);
		
		
	}

	@Override
	public Partition read(Kryo kryo, Input input, Class<Partition> type) {
		
		Partition partition = new Partition();
		partition.setCoords(kryo.readObjectOrNull(input,double[].class));
		partition.setDimensions(kryo.readObjectOrNull(input, double[].class));
		partition.setIndex(kryo.readObjectOrNull(input, int.class));
		partition.setCost(kryo.readObjectOrNull(input, double.class));
		return partition;
	}

}
