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
package edu.purdue.cs.tornado.helper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.purdue.cs.tornado.messages.DataObject;
import edu.purdue.cs.tornado.messages.Query;

public abstract class IndexCell {

	public enum IndexCellType {
		Grid, Multi_Level_Grid, Pyramid
	}

	public IndexCell() {
	}

	/**
	 * This function indexes the data object the textual content is assumed to
	 * be sorted increases the count of objects for every distinct keyword
	 * 
	 * @param dataObject
	 */

	public abstract void addQuery(Query query);

	public abstract boolean cellOverlapsSpatiall(Rectangle rectangle);

	public abstract boolean cellOverlapsTextually(ArrayList<String> textList);

	public abstract void dropQuery(Query query);

	public abstract boolean equals(Object o);

	/**
	 * 
	 * Estimate how many data objects contain Any of the input keywords this is
	 * estimated as the sum of objects per keyword.
	 * 
	 * @param keywords
	 */
	public abstract Integer estimateDataObjectCountAny(ArrayList<String> keywords);

	public abstract ArrayList<Query> findandRemoveExpriedQueries();

	public abstract ArrayList<List<Query>> geSpatiotTextualOverlappingQueries(Point p, ArrayList<String> keywords);

	public abstract Rectangle getBounds();

	public abstract HashMap<String, List<Query>> getTextualOverlappingQueries(ArrayList<String> keywords);

	public abstract void removeQueryFromInvertedList(Query query);

	public abstract void setBounds(Rectangle bounds);

	public abstract void setStoredQueries(ArrayList<Query> storedQueries);

	public abstract List<Query> getQueries();

	public abstract HashMap<String, HashMap<String, ArrayList<Query>>> getQueriesInvertedList();

	public abstract void setQueriesInvertedList(HashMap<String, HashMap<String, ArrayList<Query>>> queriesInvertedList);

	public abstract IndexCellCoordinates getGlobalCoordinates();

	public abstract void setGlobalCoordinates(IndexCellCoordinates globalCoordinates);

	public abstract Integer getIndexCellCost() ;
	public abstract void setIndexCellCost(Integer indexCellCost);

	public abstract boolean isTransmitted() ;
	public abstract void setTransmitted(boolean transmitted) ;

	public abstract Long getMinExpireTime() ;

	public abstract void setMinExpireTime(Long minExpireTime);

	public abstract ArrayList<Query> getStoredQueries();

	public abstract List<DataObject> getStoredObjects();

	public abstract void setStoredObjects(com.sun.tools.javac.util.List<DataObject> objects) ;
	public abstract Integer getDataObjectCount();

	public abstract DataObject dropDataObject(Integer id);
	public abstract HashMap<String, Integer> getAllDataTextInCell() ;

	public abstract List<DataObject> getStoredObjects(Rectangle range, ArrayList<String> keywords, TextualPredicate predicate) ;

	public abstract DataObject getDataObject(Integer id) ;

	public abstract void addDataObject(DataObject id) ;

	public abstract int estimateDataObjectCountAll(ArrayList<String> keywords);

}
