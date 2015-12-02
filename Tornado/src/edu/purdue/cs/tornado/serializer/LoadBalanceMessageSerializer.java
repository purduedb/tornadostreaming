package edu.purdue.cs.tornado.serializer;

import java.util.ArrayList;
import java.util.List;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import edu.purdue.cs.tornado.loadbalance.LoadBalanceMessage;
import edu.purdue.cs.tornado.loadbalance.Partition;

public class LoadBalanceMessageSerializer extends  com.esotericsoftware.kryo.Serializer<LoadBalanceMessage> {

	@Override
	public void write(Kryo kryo, Output output, LoadBalanceMessage laadBalanceMessgae) {
		
		kryo.writeObjectOrNull(output,laadBalanceMessgae.getAuxCell(),Partition.class);
		kryo.writeObjectOrNull(output,laadBalanceMessgae.getLoadBalanceMessageType(),String.class);
		kryo.writeObjectOrNull(output,laadBalanceMessgae.getNewCell(),Partition.class);
		kryo.writeObjectOrNull(output,laadBalanceMessgae.getNewExecuterTasks(),List.class);
		kryo.writeObjectOrNull(output,laadBalanceMessgae.getNewPartitions(),ArrayList.class);
		kryo.writeObjectOrNull(output,laadBalanceMessgae.getParition(),Partition.class);
		kryo.writeObjectOrNull(output,laadBalanceMessgae.getSendTo(),Integer.class);
		kryo.writeObjectOrNull(output,laadBalanceMessgae.getType(),String.class);
	}

	@Override
	public LoadBalanceMessage read(Kryo kryo, Input input, Class<LoadBalanceMessage> type) {
		LoadBalanceMessage loadBalanceMessage = new LoadBalanceMessage();
		
		loadBalanceMessage.setAuxCell(kryo.readObjectOrNull(input,Partition.class));
		loadBalanceMessage.setLoadBalanceMessageType(kryo.readObjectOrNull(input,String.class));
		loadBalanceMessage.setNewCell(kryo.readObjectOrNull(input,Partition.class));
		loadBalanceMessage.setNewExecuterTasks(kryo.readObjectOrNull(input,List.class));
		loadBalanceMessage.setNewPartitions(kryo.readObjectOrNull(input,ArrayList.class));
		loadBalanceMessage.setParition(kryo.readObjectOrNull(input,Partition.class));
		loadBalanceMessage.setSendTo(kryo.readObjectOrNull(input,Integer.class));
		loadBalanceMessage.setType(kryo.readObjectOrNull(input,String.class));
		return loadBalanceMessage;
	}

}
