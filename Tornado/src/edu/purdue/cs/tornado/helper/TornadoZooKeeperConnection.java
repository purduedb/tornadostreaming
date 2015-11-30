package edu.purdue.cs.tornado.helper;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.storm.curator.framework.CuratorFramework;
import org.apache.storm.curator.framework.CuratorFrameworkFactory;
import org.apache.storm.curator.retry.ExponentialBackoffRetry;
import org.apache.storm.zookeeper.CreateMode;
import org.apache.storm.zookeeper.KeeperException;
import org.apache.storm.zookeeper.WatchedEvent;
import org.apache.storm.zookeeper.Watcher;

import backtype.storm.Config;

/**
 * 
 * Class to communicate with the ZooKeeper, write and read statistical informations, and barrier
 * 
 * @author Anas Daghistani <anas@purdue.edu>
 *
 */
public class TornadoZooKeeperConnection {
	private final CuratorFramework client;
	String myId;
	private final String barrierPath;
	private final Watcher watcher = new Watcher()
	{
		@Override
		public void process(WatchedEvent event)
		{
			notifyFromWatcher();
		}
	};

	public TornadoZooKeeperConnection(Map conf, String myId){
		this.myId = myId;
		this.barrierPath = "/barrier";

		List<String> servers = (List<String>) conf.get(Config.TRANSACTIONAL_ZOOKEEPER_SERVERS);
		Long port = (Long) conf.get(Config.TRANSACTIONAL_ZOOKEEPER_PORT);
		if (servers == null || port == null) {
			servers = (List<String>) conf.get(Config.STORM_ZOOKEEPER_SERVERS);
			port = (Long) conf.get(Config.STORM_ZOOKEEPER_PORT);
		}
		String connectionString = servers.get(0) + ":" + port.toString();
		client = CuratorFrameworkFactory.builder()
				.connectString(connectionString)
				.namespace("Tornado")
				.retryPolicy(new ExponentialBackoffRetry(1000, 3))
				.build();
		client.start();
		if(myId.startsWith("ExecutorBolt")){
			//Create a Znode for the ExecutorBolt
			initializeZnode();
		}
	}

	public synchronized void initializeZnode(){
		try {
			client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath("/ExecutorBoltStat/"+myId);
		} catch (KeeperException e) {
			System.out
			.println("Keeper exception when instantiating queue: "
					+ e.toString());
		} catch (InterruptedException e) {
			System.out.println("Interrupted exception");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public short readData(){
		try {
			byte[] data = client.getData().forPath("/ExecutorBoltStat/"+myId);
			ByteBuffer wrapped = ByteBuffer.wrap(data); // big-endian by default
			return wrapped.getShort();
		} catch (Exception e) {
			System.out.println("Error reading data from: "+"/ExecutorBoltStat/"+myId);
			e.printStackTrace();
		}
		return 0;
	}

	public int[] readDataFrom(String ID){
		int[] stat = new int[4];
		try {
			byte[] data = client.getData().forPath("/ExecutorBoltStat/"+ID);
			ByteBuffer wrapped = ByteBuffer.wrap(data); // big-endian by default
			stat[0] = wrapped.getInt();
			stat[1] = wrapped.getInt();
			stat[2] = wrapped.getInt();
			stat[3] = wrapped.getInt();
			return stat;
		} catch (Exception e) {
			System.out.println("Error reading data from: "+"/ExecutorBoltStat/"+ID);
			e.printStackTrace();
		}
		return null;
	}

	public void writeData(int statData, int statQuery, int coord, int axis){
		ByteBuffer dbuf = ByteBuffer.allocate(16);
		dbuf.putInt(statData);
		dbuf.putInt(statQuery);
		dbuf.putInt(coord);
		dbuf.putInt(axis);
		byte[] bytes = dbuf.array();
		try {
			client.setData().forPath("/ExecutorBoltStat/"+myId,bytes);
		} catch (Exception e) {
			System.out.println("Error writing data to: "+"/ExecutorBoltStat/"+myId);
			e.printStackTrace();
		}
	}

	public List<String> getExecutersChildern(){
		try {
			List<String> Children = client.getChildren().forPath("/ExecutorBoltStat/");
			return Children;
		} catch (Exception e) {
			System.out.println("Error getting children of: "+"/ExecutorBoltStat/");
			e.printStackTrace();
		}
		return null;
	}

	/* --------------------------------------------
	 *   FROM THIS POINT
	 * -------------------------------------------
	 * Copyright 2012 Netflix, Inc.
	 *
	 *    Licensed under the Apache License, Version 2.0 (the "License");
	 *    you may not use this file except in compliance with the License.
	 *    You may obtain a copy of the License at
	 *
	 *        http://www.apache.org/licenses/LICENSE-2.0
	 *
	 *    Unless required by applicable law or agreed to in writing, software
	 *    distributed under the License is distributed on an "AS IS" BASIS,
	 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	 *    See the License for the specific language governing permissions and
	 *    limitations under the License.
	 *    
	 *    
	 * Edited by: Anas Daghistani   
	 *    
	 */

	/**
	 * Utility to set the barrier node
	 *
	 * @throws Exception errors
	 */
	public synchronized void setBarrier() throws Exception{
		boolean result;
		try{
			for(;;){
				client.create().creatingParentsIfNeeded().forPath(barrierPath);
				result = (client.checkExists().forPath(barrierPath) == null);
				if ( result ){
					break;
				}
			}
		}
		catch ( KeeperException.NodeExistsException ignore ){
			// ignore
		} 
		//	catch (Exception e) {
		//	setBarrier();
		//}
	}

	/**
	 * Utility to remove the barrier node
	 *
	 * @throws Exception errors
	 */
	public synchronized void removeBarrier() throws Exception{
		try{
			client.delete().forPath(barrierPath);
		}
		catch ( KeeperException.NoNodeException ignore ){
			// ignore
		}
	}

	/**
	 * Blocks until the barrier node comes into existence
	 *
	 * @throws Exception errors
	 */
	public synchronized void waitOnBarrier() throws Exception{
		waitOnBarrier(-1, null);
	}

	/**
	 * Blocks until the barrier no longer exists or the timeout elapses
	 *
	 * @param maxWait max time to block
	 * @param unit time unit
	 * @return true if the wait was successful, false if the timeout elapsed first
	 * @throws Exception errors
	 */
	public synchronized boolean waitOnBarrier(long maxWait, TimeUnit unit) throws Exception{
		long startMs = System.currentTimeMillis();
		boolean hasMaxWait = (unit != null);
		long maxWaitMs = hasMaxWait ? TimeUnit.MILLISECONDS.convert(maxWait, unit) : Long.MAX_VALUE;

		boolean result;
		for(;;){
			result = (client.checkExists().usingWatcher(watcher).forPath(barrierPath) == null);
			if ( result ){
				break;
			}

			if ( hasMaxWait ){
				long elapsed = System.currentTimeMillis() - startMs;
				long thisWaitMs = maxWaitMs - elapsed;
				if ( thisWaitMs <= 0 ){
					break;
				}
				wait(thisWaitMs);
			}
			else{
				wait();
			}
		}
		return result;
	}

	private synchronized void notifyFromWatcher(){
		notifyAll();
	}

}
