/**
 * Copyright Jul 5, 2015
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
package edu.purdue.cs.tornado.sentiment;

import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import edu.purdue.cs.tornado.helper.SemanticHelper;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.messages.DataObject;
import edu.purdue.cs.tornado.messages.CombinedTuple;

public class EvalSentimentBolt extends BaseRichBolt {
	private Map stormConf; // configuration
	private TopologyContext context; // storm context
	private OutputCollector collector;
	private String dataSource;
	public EvalSentimentBolt(String dataSource){
		this.dataSource=dataSource;
	}
	@Override
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		// TODO Auto-generated method stub
		this.stormConf = stormConf;
		this.context = context;
		this.collector = collector;
		SemanticHelper.initSentimentAnalysis();

	}

	@Override
	public void execute(Tuple input) {
		// load outputTuple from Tuple
		
		
		updateOutput( input);
	}
	private  synchronized void updateOutput(Tuple input){
		CombinedTuple outputTuple = (CombinedTuple) input
				.getValueByField(SpatioTextualConstants.output);
		// get Original Text from outputTuple
				if(outputTuple!=null&&outputTuple.getDataObject()!=null&&dataSource.equals(outputTuple.getDataObject().getSrcId())){
					String text = outputTuple.getDataObject().getOriginalText();
					int sentiment = SemanticHelper.findSentiment(text);
					String sentimentString = mapIntToSentimentValue(sentiment);
					DataObject newDataObject = new DataObject(outputTuple.getDataObject());
					newDataObject.setOriginalText(sentimentString+"\n"+text);
					outputTuple.setDataObject(newDataObject);
				//	outputTuple.getDataObject().setOriginalText(sentimentString+"\n"+outputTuple.getDataObject().getOriginalText());
				}
				if(outputTuple!=null&&outputTuple.getDataObject2()!=null&&dataSource.equals(outputTuple.getDataObject2().getSrcId())){
					String text = outputTuple.getDataObject2().getOriginalText();
					int sentiment = SemanticHelper.findSentiment(text);
					String sentimentString = mapIntToSentimentValue(sentiment);
					DataObject newDataObject = new DataObject(outputTuple.getDataObject2());
					newDataObject.setOriginalText(sentimentString+"\n"+text);
					outputTuple.setDataObject2(newDataObject);
				//	outputTuple.getDataObject2().setOriginalText(sentimentString+"\n"+outputTuple.getDataObject2().getOriginalText());
				}
				
				collector.emit(new Values(outputTuple));
	}
	
	private String mapIntToSentimentValue(Integer sentment){
		if(sentment==0)
			return "[Very negative] : ";
		if(sentment==1)
			return "[Negative] : ";
		if(sentment==2)
			return "[Neutral] : ";
		if(sentment==3)
			return "[Positive] : ";
		else
			return "[Very positive] : ";
	}
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// create a new output tuple type
		declarer.declare(new Fields(SpatioTextualConstants.output));
	}
}
