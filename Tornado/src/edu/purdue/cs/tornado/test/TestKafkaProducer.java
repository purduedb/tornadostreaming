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
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
public class TestKafkaProducer {
	static String topic;
	static String zookeeper;

	public static void main(String[] args) throws InterruptedException {
		
		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.propsapache.kafka.common.serialization.StringSerializer");
		KafkaProducer producer = new KafkaProducer(props);
		
		boolean sync = false;
		String topic="output";
		String key = "q1";
		String value = "{\"qname\":\"q11\",\"point\":{\"lat\":41.8864494854113,\"lng\":-87.61987492165883},\"text\":\"Ahmed refaat ahmed2 \"}";
		ProducerRecord producerRecord = new ProducerRecord(topic, key.getBytes(), value.getBytes());
		if (sync) {
			try {
				producer.send(producerRecord).get();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			producer.send(producerRecord);
		}
		producer.close();
		/*
		zookeeper = "localhost:2181";
		topic="output";
		Properties props = new Properties();
        props.put("metadata.broker.list", "localhost:9092");

        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
       // props.put("request.required.acks", "1");
 
        ProducerConfig config = new ProducerConfig(props);
 
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(props);
        String msg = "{\"qname\":\"q1\",\"point\":{\"lat\":41.8864494854113,\"lng\":-87.61987492165883},\"text\":\"Ahmed refaat ahmed \"}";
        ProducerRecord<String, String> data = new ProducerRecord<String, String>(topic, "q1", msg);
        producer.send(data);*/
	}
}
