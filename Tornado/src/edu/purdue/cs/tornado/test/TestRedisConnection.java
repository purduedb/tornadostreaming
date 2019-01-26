package edu.purdue.cs.tornado.test;

import redis.clients.jedis.Jedis;

public class TestRedisConnection {
	 public static void main(String[] args) {
	      //Connecting to Redis server on localhost
	      Jedis jedis = new Jedis("172.18.11.144");
	      System.out.println("Connection to server sucessfully");
	      //set the data in redis string
	      jedis.set("tutorial-name", "Redis tutorial");
	     // Get the stored data and print it
	     System.out.println("Stored string in redis:: "+ jedis.get("tutorial-name"));
	     jedis.close();
	 }
}
