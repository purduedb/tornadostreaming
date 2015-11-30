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
package edu.purdue.cs.tornado;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import backtype.storm.generated.StormTopology;
import backtype.storm.topology.BoltDeclarer;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import edu.purdue.cs.tornado.evaluator.DynamicEvalautorBolt;
import edu.purdue.cs.tornado.evaluator.SpatioTextualEvaluatorBolt;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.index.DynamicGlobalIndexBolt;
import edu.purdue.cs.tornado.index.GlobalIndexBolt;
import edu.purdue.cs.tornado.loadbalance.Partition;
/**
 * This class is an extension to the storm topology builder and it allows adding spatio- textual query processing abilities 
 * @author Ahmed Mahmood
 *
 */
public class SpatioTextualToplogyBuilder extends TopologyBuilder {
	
	private Map<String, IRichBolt> _spatioTexualIndexes = new HashMap<String, IRichBolt>();
	private Map<String, IRichBolt> _spatioTexualEvaluators = new HashMap<String, IRichBolt>();
	private Map<String, SpatioTextualIndexGetter> _IndexGetter = new HashMap<String, SpatioTextualIndexGetter>();
	private Map<String, BoltDeclarer> _evaluatorIndexGetter = new HashMap<String, BoltDeclarer>();
	
	/**
	 * This method adds a static spatio-textual index processor to the tornado
	 */
	public StormTopology createTopology() {
		for(String indexBoltId: _IndexGetter.keySet()) {
            SpatioTextualIndexGetter indexBoltGetter = _IndexGetter.get(indexBoltId);
            
            for(String configKey: indexBoltGetter.spatioTextualConfig.keySet())
            	_evaluatorIndexGetter.get(indexBoltId).addConfiguration(configKey, indexBoltGetter.spatioTextualConfig.get(configKey));
            
        }
		StormTopology stormTopology =  super.createTopology();
		
		return stormTopology;
	}
	/**
	 * specificy all details of the index, either static /dynamic, gird, kd tree, and the partitions 
	 * @param id
	 * @param routing_parallelism_hint
	 * @param evaluator_parallelism_hint
	 * @param partitions
	 * @param indexType Must be from the GlobalIndex types
	 * @return
	 */
	public SpatioTextualBoltDeclarer addSpatioTextualProcessor(String id,
			Number routing_parallelism_hint,Number evaluator_parallelism_hint,ArrayList<Partition> partitions,String indexType ) {
	
		//TODO add validation on the inputs 
		//TODO remove extra streams 
		//TODO consider the difference between many streams or a single stream
		String indexId = id + SpatioTextualConstants.IndexIDExtension;
		String indexToBoltStreamId_Query = id
				+ SpatioTextualConstants.Index_Bolt_STreamIDExtension_Query;
		String indexToBoltStreamId_Data = SpatioTextualConstants.getIndexBoltDataStreamId(id);
		String indexToBoltStreamId_Control = id
				+ SpatioTextualConstants.Index_Bolt_STreamIDExtension_Control;
		
		String indexToIndexStreamId_Query = id
				+ SpatioTextualConstants.Index_Index_STreamIDExtension_Query;
		String indexToIndexStreamId_Data = id
				+ SpatioTextualConstants.Index_Index_STreamIDExtension_Data;
		String indexToIndexStreamId_Control = id
				+ SpatioTextualConstants.Index_Index_STreamIDExtension_Control;
		
		
		String boltToIndexStreamId_Query = id
				+ SpatioTextualConstants.Bolt_Index_STreamIDExtension_Query;
		String boltToIndexStreamId_Data = id
				+ SpatioTextualConstants.Bolt_Index_STreamIDExtension_Data;
		String boltToIndexStreamId_Control = id
				+ SpatioTextualConstants.Bolt_Index_STreamIDExtension_Control;
		
		String boltToBoltStreamId_Query = id
				+ SpatioTextualConstants.Bolt_Bolt_STreamIDExtension_Query;
		String boltToBoltStreamId_Data = id
				+ SpatioTextualConstants.Bolt_Bolt_STreamIDExtension_Data;
		String boltToBoltStreamId_Control =  SpatioTextualConstants.getBoltBoltControlStreamId( id);
		
		
		
		GlobalIndexBolt indexBolt = new GlobalIndexBolt(id,partitions,indexType);
		SpatioTextualEvaluatorBolt spatioTextualBolt = new SpatioTextualEvaluatorBolt(id);
		_spatioTexualIndexes.put(id, indexBolt);
		_spatioTexualEvaluators.put(id, spatioTextualBolt);
		
		
		BoltDeclarer indexDeclarer = this.setBolt(indexId,
				indexBolt, routing_parallelism_hint).directGrouping(id, boltToIndexStreamId_Query)
				.directGrouping(id, boltToIndexStreamId_Data).directGrouping(id, boltToIndexStreamId_Control).allGrouping(indexId,indexToIndexStreamId_Control);
				
				
				
				
				
				
		
		SpatioTextualIndexGetter spatioTextualIndexGetter = new SpatioTextualIndexGetter(indexDeclarer);
		
		BoltDeclarer evaluatorBoltDeclarer = this.setBolt(id, spatioTextualBolt, evaluator_parallelism_hint).directGrouping(
				indexId, indexToBoltStreamId_Data).directGrouping(indexId, indexToBoltStreamId_Query).directGrouping(indexId, indexToBoltStreamId_Control)
				.directGrouping(id, boltToBoltStreamId_Query).directGrouping(id, boltToBoltStreamId_Data).directGrouping(id, boltToBoltStreamId_Control);
				
		this._evaluatorIndexGetter.put(id, evaluatorBoltDeclarer);
		this._IndexGetter.put(id, spatioTextualIndexGetter);
		return spatioTextualIndexGetter;
	}
	
	/**
	 * specificy all details of the index, either static /dynamic, gird, kd tree, and the partitions 
	 * @param id
	 * @param routing_parallelism_hint
	 * @param evaluator_parallelism_hint
	 * @param partitions
	 * @param indexType Must be from the GlobalIndex types
	 * @return
	 */
	public SpatioTextualBoltDeclarer addDynamicSpatioTextualProcessor(String id,
			Number routing_parallelism_hint,Number evaluator_parallelism_hint,ArrayList<Partition> partitions,String indexType ) {
	
		//TODO add validation on the inputs 
		//TODO remove extra streams 
		//TODO consider the difference between many streams or a single stream
		String indexId = id + SpatioTextualConstants.IndexIDExtension;
		String indexToBoltStreamId_Query = id
				+ SpatioTextualConstants.Index_Bolt_STreamIDExtension_Query;
		String indexToBoltStreamId_Data = SpatioTextualConstants.getIndexBoltDataStreamId(id);
		String indexToBoltStreamId_Control = id
				+ SpatioTextualConstants.Index_Bolt_STreamIDExtension_Control;
		
		String indexToIndexStreamId_Query = id
				+ SpatioTextualConstants.Index_Index_STreamIDExtension_Query;
		String indexToIndexStreamId_Data = id
				+ SpatioTextualConstants.Index_Index_STreamIDExtension_Data;
		String indexToIndexStreamId_Control = id
				+ SpatioTextualConstants.Index_Index_STreamIDExtension_Control;
		
		
		String boltToIndexStreamId_Query = id
				+ SpatioTextualConstants.Bolt_Index_STreamIDExtension_Query;
		String boltToIndexStreamId_Data = id
				+ SpatioTextualConstants.Bolt_Index_STreamIDExtension_Data;
		String boltToIndexStreamId_Control = id
				+ SpatioTextualConstants.Bolt_Index_STreamIDExtension_Control;
		
		String boltToBoltStreamId_Query = id
				+ SpatioTextualConstants.Bolt_Bolt_STreamIDExtension_Query;
		String boltToBoltStreamId_Data = id
				+ SpatioTextualConstants.Bolt_Bolt_STreamIDExtension_Data;
		String boltToBoltStreamId_Control =  SpatioTextualConstants.getBoltBoltControlStreamId( id);
		
		
		
		DynamicGlobalIndexBolt indexBolt = new DynamicGlobalIndexBolt(id);
		DynamicEvalautorBolt spatioTextualBolt = new DynamicEvalautorBolt(id);
		_spatioTexualIndexes.put(id, indexBolt);
		_spatioTexualEvaluators.put(id, spatioTextualBolt);
		
		
		BoltDeclarer indexDeclarer = this.setBolt(indexId,
				indexBolt, routing_parallelism_hint).directGrouping(id, boltToIndexStreamId_Query)
				.directGrouping(id, boltToIndexStreamId_Data).directGrouping(id, boltToIndexStreamId_Control).allGrouping(indexId,indexToIndexStreamId_Control);
				
				
				
				
				
				
		
		SpatioTextualIndexGetter spatioTextualIndexGetter = new SpatioTextualIndexGetter(indexDeclarer);
		
		BoltDeclarer evaluatorBoltDeclarer = this.setBolt(id, spatioTextualBolt, evaluator_parallelism_hint).directGrouping(
				indexId, indexToBoltStreamId_Data).directGrouping(indexId, indexToBoltStreamId_Query).directGrouping(indexId, indexToBoltStreamId_Control)
				.directGrouping(id, boltToBoltStreamId_Query).directGrouping(id, boltToBoltStreamId_Data).directGrouping(id, boltToBoltStreamId_Control);
				
		this._evaluatorIndexGetter.put(id, evaluatorBoltDeclarer);
		this._IndexGetter.put(id, spatioTextualIndexGetter);
		return spatioTextualIndexGetter;
	}
	/**
	 * Static index with partitions
	 * @param id
	 * @param routing_parallelism_hint
	 * @param evaluator_parallelism_hint
	 * @param partitions
	 * @return
	 * @throws java.lang.Exception 
	 */
	public SpatioTextualBoltDeclarer addSpatioTextualProcessor(String id,
			Number routing_parallelism_hint,Number evaluator_parallelism_hint,ArrayList<Partition> partitions) throws Exception{
		if(partitions==null||(Integer)evaluator_parallelism_hint!=partitions.size()){
			Exception  e = new Exception("Partitions list sizes and number of evaluators do not match  ");
			throw e;
		}
			
		return  addSpatioTextualProcessor(id,routing_parallelism_hint, evaluator_parallelism_hint,partitions,null);
	}
	/**
	 * Static grid index
	 * @param id
	 * @param routing_parallelism_hint
	 * @param evaluator_parallelism_hint
	 * @return
	 */
	public SpatioTextualBoltDeclarer addSpatioTextualProcessor(String id,
			Number routing_parallelism_hint,Number evaluator_parallelism_hint) {
		
		return  addSpatioTextualProcessor(id,routing_parallelism_hint, evaluator_parallelism_hint,null,null);
	}

	protected class SpatioTextualIndexGetter implements SpatioTextualBoltDeclarer {
		private BoltDeclarer _boltDeclarer;
		private Map <String, Object> spatioTextualConfig = new HashMap<String, Object>();
		public SpatioTextualIndexGetter(BoltDeclarer boltDeclarer){
			this._boltDeclarer =boltDeclarer;
			
		}
		/**
		 * Adding a persistent data input
		 * this adds to the configuration of the bolt that this source is persistent
		 */
		public SpatioTextualBoltDeclarer addPersistentSpatioTextualInput(String componentId,String streamId){
			this._boltDeclarer=this._boltDeclarer.fieldsGrouping(componentId, streamId,new Fields(SpatioTextualConstants.objectIdField));
			this._boltDeclarer.addConfiguration(SpatioTextualConstants.Data_Source+"_"+componentId+"_"+streamId, SpatioTextualConstants.Persistent);//this configurat
			spatioTextualConfig.put(SpatioTextualConstants.Data_Source+"_"+componentId+"_"+streamId, SpatioTextualConstants.Persistent);//this configurat
			return this;
		}
		public SpatioTextualBoltDeclarer addPersistentSpatioTextualInput(String componentId){
			this._boltDeclarer=this._boltDeclarer.fieldsGrouping(componentId,new Fields(SpatioTextualConstants.objectIdField));
			this._boltDeclarer.addConfiguration(SpatioTextualConstants.Data_Source+"_"+componentId, SpatioTextualConstants.Persistent);
			spatioTextualConfig.put(SpatioTextualConstants.Data_Source+"_"+componentId, SpatioTextualConstants.Persistent);
			return this;		
		}
		public SpatioTextualBoltDeclarer addVolatileSpatioTextualInput(String componentId,String streamId){
			this._boltDeclarer=this._boltDeclarer.shuffleGrouping(componentId, streamId);
			this._boltDeclarer.addConfiguration(SpatioTextualConstants.Data_Source+"_"+componentId+"_"+streamId, SpatioTextualConstants.Volatile);
			spatioTextualConfig.put(SpatioTextualConstants.Data_Source+"_"+componentId+"_"+streamId, SpatioTextualConstants.Volatile);
			this._boltDeclarer.addConfiguration(SpatioTextualConstants.getVolatilePropertyKey(componentId,streamId), SpatioTextualConstants.NOTCLEAN);
			spatioTextualConfig.put(SpatioTextualConstants.getVolatilePropertyKey(componentId,streamId), SpatioTextualConstants.NOTCLEAN);
			return this;
			
		}
		public SpatioTextualBoltDeclarer addVolatileSpatioTextualInput(String componentId){
			this._boltDeclarer=this._boltDeclarer.shuffleGrouping(componentId);
			this._boltDeclarer.addConfiguration(SpatioTextualConstants.Data_Source+"_"+componentId, SpatioTextualConstants.Volatile);
			spatioTextualConfig.put(SpatioTextualConstants.Data_Source+"_"+componentId, SpatioTextualConstants.Volatile);
			this._boltDeclarer.addConfiguration(SpatioTextualConstants.getVolatilePropertyKey(componentId), SpatioTextualConstants.NOTCLEAN);
			spatioTextualConfig.put(SpatioTextualConstants.getVolatilePropertyKey(componentId), SpatioTextualConstants.NOTCLEAN);
			return this;
		}
		public SpatioTextualBoltDeclarer addCleanVolatileSpatioTextualInput(String componentId,String streamId){
			this._boltDeclarer=this._boltDeclarer.shuffleGrouping(componentId, streamId);
			this._boltDeclarer.addConfiguration(SpatioTextualConstants.Data_Source+"_"+componentId+"_"+streamId, SpatioTextualConstants.Volatile);
			spatioTextualConfig.put(SpatioTextualConstants.Data_Source+"_"+componentId+"_"+streamId, SpatioTextualConstants.Volatile);
			this._boltDeclarer.addConfiguration(SpatioTextualConstants.getVolatilePropertyKey(componentId,streamId), SpatioTextualConstants.CLEAN);
			spatioTextualConfig.put(SpatioTextualConstants.getVolatilePropertyKey(componentId, streamId), SpatioTextualConstants.CLEAN);
			return this;
			
		}
		public SpatioTextualBoltDeclarer addCleanVolatileSpatioTextualInput(String componentId){
			this._boltDeclarer=this._boltDeclarer.shuffleGrouping(componentId);
			this._boltDeclarer.addConfiguration(SpatioTextualConstants.Data_Source+"_"+componentId, SpatioTextualConstants.Volatile);
			spatioTextualConfig.put(SpatioTextualConstants.Data_Source+"_"+componentId, SpatioTextualConstants.Volatile);
			this._boltDeclarer.addConfiguration(SpatioTextualConstants.getVolatilePropertyKey(componentId), SpatioTextualConstants.CLEAN);
			spatioTextualConfig.put(SpatioTextualConstants.getVolatilePropertyKey(componentId), SpatioTextualConstants.CLEAN);
			return this;
		}
		public SpatioTextualBoltDeclarer addContinuousQuerySource(String componentId,String streamId){
			this._boltDeclarer=this._boltDeclarer.fieldsGrouping(componentId, streamId,new Fields(SpatioTextualConstants.queryIdField));
			this._boltDeclarer.addConfiguration(SpatioTextualConstants.Query_Source+"_"+componentId+"_"+streamId, SpatioTextualConstants.Continuous);
			spatioTextualConfig.put(SpatioTextualConstants.Query_Source+"_"+componentId+"_"+streamId, SpatioTextualConstants.Continuous);
			return this;
		}
		public SpatioTextualBoltDeclarer addContinuousQuerySource(String componentId){
			this._boltDeclarer=this._boltDeclarer.fieldsGrouping(componentId,new Fields(SpatioTextualConstants.queryIdField));
			this._boltDeclarer.addConfiguration(SpatioTextualConstants.Query_Source+"_"+componentId, SpatioTextualConstants.Continuous);
			spatioTextualConfig.put(SpatioTextualConstants.Query_Source+"_"+componentId, SpatioTextualConstants.Continuous);
			return this;
		}
		public SpatioTextualBoltDeclarer addSnapShotQuerySource(String componentId,String streamId){
			this._boltDeclarer=this._boltDeclarer.shuffleGrouping(componentId, streamId);
			this._boltDeclarer.addConfiguration(SpatioTextualConstants.Query_Source+"_"+componentId+"_"+streamId, SpatioTextualConstants.Volatile);
			spatioTextualConfig.put(SpatioTextualConstants.Query_Source+"_"+componentId+"_"+streamId, SpatioTextualConstants.Volatile);
			return this;
		}
		public SpatioTextualBoltDeclarer addSnapShotQuerySource(String componentId){
			this._boltDeclarer=this._boltDeclarer.shuffleGrouping(componentId);
			this._boltDeclarer.addConfiguration(SpatioTextualConstants.Query_Source+"_"+componentId, SpatioTextualConstants.Volatile);
			spatioTextualConfig.put(SpatioTextualConstants.Query_Source+"_"+componentId, SpatioTextualConstants.Volatile);
			return this;
		}
		public SpatioTextualBoltDeclarer addStaticDataSource(String componentId, String sourceClassName,HashMap<String, String> conf){
			this._boltDeclarer.addConfiguration(SpatioTextualConstants.Data_Source+"_"+componentId, SpatioTextualConstants.Static);
			spatioTextualConfig.put(SpatioTextualConstants.Data_Source+"_"+componentId, SpatioTextualConstants.Static);
			spatioTextualConfig.put(SpatioTextualConstants.Static_Source_Class_Name+"_"+componentId, sourceClassName);
			//TODO check in the cluster mode if the configuration map is properly serialized, it should be as the hashmap is serializable in java
			spatioTextualConfig.put(SpatioTextualConstants.Static_Source_Class_Config+"_"+componentId, conf);
			return this;
		}
		@Override
		public SpatioTextualBoltDeclarer addCurrentSpatioTextualInput(String componentId) {
			this._boltDeclarer=this._boltDeclarer.fieldsGrouping(componentId,new Fields(SpatioTextualConstants.objectIdField));
			this._boltDeclarer.addConfiguration(SpatioTextualConstants.Data_Source+"_"+componentId, SpatioTextualConstants.Current);
			spatioTextualConfig.put(SpatioTextualConstants.Data_Source+"_"+componentId, SpatioTextualConstants.Current);
			return this;
		}
		@Override
		public SpatioTextualBoltDeclarer addCurrentSpatioTextualInput(String componentId, String streamId) {
			this._boltDeclarer=this._boltDeclarer.fieldsGrouping(componentId, streamId,new Fields(SpatioTextualConstants.objectIdField));
			this._boltDeclarer.addConfiguration(SpatioTextualConstants.Data_Source+"_"+componentId+"_"+streamId, SpatioTextualConstants.Current);
			spatioTextualConfig.put(SpatioTextualConstants.Data_Source+"_"+componentId+"_"+streamId, SpatioTextualConstants.Current);
			return this;
		}
	}

}
