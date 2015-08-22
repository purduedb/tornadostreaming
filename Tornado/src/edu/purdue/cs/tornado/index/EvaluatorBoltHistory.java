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
package edu.purdue.cs.tornado.index;
/**
 * This class keeps history of a moving object in the range of evaluator bolts
 * @author ahmed
 *
 */
public class EvaluatorBoltHistory {
	Integer taskId;
	Long timeStamp;	
	
	public EvaluatorBoltHistory(Integer taskId, Long timeStamp) {
		super();
		this.taskId = taskId;
		this.timeStamp = timeStamp;
	}
	public Integer getTaskId() {
		return taskId;
	}
	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}
	public Long getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(Long timeStamp) {
		this.timeStamp = timeStamp;
	}
	@Override
	public boolean equals(Object o) {
        if (o == this) 
            return true;
        if (!(o instanceof EvaluatorBoltHistory)) 
            return false;
        EvaluatorBoltHistory c = (EvaluatorBoltHistory) o;
        return (Integer.compare(taskId,c.getTaskId()) == 0);
    }
}
