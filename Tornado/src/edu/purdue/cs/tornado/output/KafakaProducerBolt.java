/**
 * Copyright Jul 12, 2015
 * Author : Ahmed Mahmood
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.purdue.cs.tornado.output;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;

import edu.purdue.cs.tornado.helper.Command;
import edu.purdue.cs.tornado.helper.JsonHelper;
import edu.purdue.cs.tornado.helper.LatLong;
import edu.purdue.cs.tornado.helper.SpatialHelper;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.messages.CombinedTuple;

public class KafakaProducerBolt extends BaseRichBolt {
	private Map stormConf; // configuration
	private TopologyContext context; // storm context
	private OutputCollector collector;
	private Properties props;
	private KafkaProducer<String, String> producer;
	private String topic;
	FileWriter fw;

	@Override
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		this.context = context;
		this.collector = collector;
		this.stormConf = stormConf;
		topic = (String) stormConf.get(SpatioTextualConstants.kafkaProducerTopic);
		props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, (String) stormConf.get(SpatioTextualConstants.kafkaBootstrapServerConfig));
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		producer = new KafkaProducer<String, String>(props);
		try {
			fw = new FileWriter("datasources/jsonTrace.txt");
		} catch (IOException e) {

			e.printStackTrace(System.err);
		}
	}

	@Override
	public synchronized void  execute(Tuple input) {
		try {
			CombinedTuple outputTuple = (CombinedTuple) input.getValueByField(SpatioTextualConstants.output);
			//		System.out.println(outputTuple.toString());
			LatLong latLong = SpatialHelper.convertFromXYToLatLonTo(outputTuple.getDataObject().getLocation());
			Double lat = latLong.getLatitude();
			Double lon = latLong.getLongitude();
			//String jsonOutput = convertOutputToJson(outputTuple.getQuery().getQueryId(), lat, lon, outputTuple.getDataObject().getOriginalText());
		//	fw.write(jsonOutput.toString()+"\n");
//			ProducerRecord<String, String> producerRecord = new ProducerRecord<String, String>(topic, outputTuple.getQuery().getQueryId(), jsonOutput);
//			producer.send(producerRecord);
			if (SpatioTextualConstants.queryTextualSpatialJoin.equals(outputTuple.getQuery().getQueryType())) {
				latLong = SpatialHelper.convertFromXYToLatLonTo(outputTuple.getDataObject2().getLocation());
				lat = latLong.getLatitude();
				lon = latLong.getLongitude();
				
				//jsonOutput = convertOutputToJson(outputTuple.getQuery().getQueryId(), lat, lon, outputTuple.getDataObject2().getOriginalText());
				String json = convertOutputToJsonForJoin(outputTuple);
			//	fw.write(jsonOutput.toString()+"\n");
				ProducerRecord<String, String> producerRecord = new ProducerRecord<String, String>(topic, outputTuple.getQuery().getQueryId()+"", json);
				producer.send(producerRecord);
				
				
				
				
				
			}
			else{

				String json2 = convertOutputToJsonForSingleQuery(outputTuple);
				ProducerRecord<String, String> producerRecord = new ProducerRecord<String, String>(topic, outputTuple.getQuery().getQueryId()+"", json2);
				producer.send(producerRecord);
				fw.write(json2.toString()+"\n");
			}
		} catch (Exception e) {

			e.printStackTrace(System.err);
		}
	}

	private String convertOutputToJson(String queriId, Double lat, Double lon, String text) {
		String json = "";
		Map outputMap = new HashMap<Object, Object>();
		Map locationMap = new HashMap<Object, Object>();
		locationMap.put("lat", lat);
		locationMap.put("lng", lon);
		outputMap.put("text", text);
		outputMap.put("name", queriId);
		outputMap.put("point", locationMap);
		json = JsonHelper.convertMapToJsonString(outputMap);
		return json;
	}
	private String convertOutputToJsonForSingleQuery(CombinedTuple outputTuple) {
		String json = "";
		Map outputMap = new HashMap<Object, Object>();
		
		outputMap.put("name", outputTuple.getQuery().getQueryId());
		String tag = "+";
		if(outputTuple.getDataObjectCommand()!=null){
			if(Command.addCommand.equals(outputTuple.getDataObjectCommand()))
				tag="+";
			else if(Command.updateCommand.equals(outputTuple.getDataObjectCommand()))
				tag="u";
			else if(Command.dropCommand.equals(outputTuple.getDataObjectCommand()))
				tag="-";
		}
		outputMap.put("tag", tag);
		outputMap.put("oid", outputTuple.getDataObject().getObjectId());
		Map location1Map = new HashMap<Object, Object>();
		location1Map.put("lat", SpatialHelper.convertFromXYToLatLonTo(outputTuple.getDataObject().getLocation()).getLatitude());
		location1Map.put("lng",  SpatialHelper.convertFromXYToLatLonTo(outputTuple.getDataObject().getLocation()).getLongitude());
		outputMap.put("text", outputTuple.getDataObject().getOriginalText());
		outputMap.put("point", location1Map);
		json = JsonHelper.convertMapToJsonString(outputMap);
		return json;
	}
	private String convertOutputToJsonForJoin(CombinedTuple outputTuple) {
		String json = "";
		Map outputMap = new HashMap<Object, Object>();
		
		outputMap.put("name", outputTuple.getQuery().getQueryId());
		String tag = "+";
		if(outputTuple.getDataObjectCommand()!=null){
			if(Command.addCommand.equals(outputTuple.getDataObjectCommand()))
				tag="+";
			else if(Command.updateCommand.equals(outputTuple.getDataObjectCommand()))
				tag="u";
			else if(Command.dropCommand.equals(outputTuple.getDataObjectCommand()))
				tag="-";
		}
		outputMap.put("tag", tag);
		outputMap.put("oid1", outputTuple.getDataObject().getObjectId());
		Map location1Map = new HashMap<Object, Object>();
		location1Map.put("lat", SpatialHelper.convertFromXYToLatLonTo(outputTuple.getDataObject().getLocation()).getLatitude());
		location1Map.put("lng",  SpatialHelper.convertFromXYToLatLonTo(outputTuple.getDataObject().getLocation()).getLongitude());
		outputMap.put("text1", outputTuple.getDataObject().getOriginalText());
		outputMap.put("point1", location1Map);
		
		outputMap.put("oid2", outputTuple.getDataObject2().getObjectId());
		Map location2Map = new HashMap<Object, Object>();
		location2Map.put("lat", SpatialHelper.convertFromXYToLatLonTo(outputTuple.getDataObject2().getLocation()).getLatitude());
		location2Map.put("lng",  SpatialHelper.convertFromXYToLatLonTo(outputTuple.getDataObject2().getLocation()).getLongitude());
		outputMap.put("text2", outputTuple.getDataObject2().getOriginalText());
		outputMap.put("point2", location2Map);
		json = JsonHelper.convertMapToJsonString(outputMap);
		return json;
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub

	}
}
