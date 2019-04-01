/**
 * Copyright Jul 7, 2015
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

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig; //import kafka.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

//import kafka.consumer.ConsumerIterator;
//import kafka.consumer.KafkaStream;
//import kafka.javaapi.consumer.ConsumerConnector;

public class TestKafkaConsumer {
	static KafkaConsumer<String, String> consumer; //static ConsumerConnector consumer;  
	static String zookeeper;
	static String group;
	static String topic;

	public static void main(String[] args) throws InterruptedException {
		zookeeper = "localhost:2181";
		group="queryprocExample";
		topic="queries";
		consumer = new KafkaConsumer<>(createConsumerConfigProps(zookeeper, topic));
		ArrayList<String> topics = new ArrayList<String>();
		topics.add("queries");
		topics.add("output");
		consumer.subscribe(topics);	// consume from topics: queries, output, etc
		
		// Print the records that are consumed from the topics denoted above
	     while (true) {
	         ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
	         for (ConsumerRecord<String, String> record : records)
	             System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
	     }

	}
	
	/**
	 * 3/30/2019 - Slightly modified function similar to createConsumerConfig(String, String)
	 * 
	 * @param a_zookeeper
	 * @param a_groupId
	 * @return Properties object 
	 */
	private static Properties createConsumerConfigProps(String a_zookeeper, String a_groupId) {
		Properties props = new Properties();
		props.put("bootstrap.servers", "localhost:9092");
		props.put("group.id", "queries");
        props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        return props;
	}
}
