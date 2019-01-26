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
package edu.purdue.cs.tornado.test;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;

import edu.purdue.cs.tornado.spouts.TwitterFireHoseSpout;

/**
 * This topology demonstrates Storm's stream groupings and multilang capabilities.
 */
public class TestTwitterFireHoseSpout {
  

  public static class PrintTweet extends BaseBasicBolt {
   

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {
      String text = tuple.getString(0);
      System.out.println(text);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
      
    }
  }

  public static void main(String[] args) throws Exception {

    TopologyBuilder builder = new TopologyBuilder();

    builder.setSpout("spout", new TwitterFireHoseSpout(), 1);

    builder.setBolt("split", new PrintTweet(), 1).shuffleGrouping("spout");


    Config conf = new Config();
    conf.setDebug(true);


    
      conf.setMaxTaskParallelism(3);

      LocalCluster cluster = new LocalCluster();
      cluster.submitTopology("word-count", conf, builder.createTopology());

     
    
  }
}