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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import edu.purdue.cs.tornado.helper.SemanticHelper;

public class TestTweetSentiment {

	public static void main(String[] args) throws IOException {
		FileInputStream fstream = new FileInputStream("datasources/twitterdata.csv");
		BufferedReader br = new BufferedReader(new InputStreamReader(
				fstream));
		SemanticHelper.initSentimentAnalysis();
		int sentiment = SemanticHelper.findSentiment("worst bad ugly damn very bad  shit angry");
		System.out.println("negative sentiment: "+sentiment );
		sentiment = SemanticHelper.findSentiment("best amazing wonderfull super this bleased happy joy");
		System.out.println("positive sentiment: "+sentiment );
		String strLine;

		// Read File Line By Line
		while ((strLine = br.readLine()) != null) {
			StringTokenizer stringTokenizer = new StringTokenizer(strLine,",") ;
			String id = stringTokenizer.nextToken();
			
			String dateString = stringTokenizer.nextToken();
			Double lat = Double.parseDouble(stringTokenizer.nextToken()) ;
			Double lon = Double.parseDouble(stringTokenizer.nextToken()) ;
			
			
			
			String dummy = stringTokenizer.nextToken();
			String textContent = "";
			while (stringTokenizer .hasMoreTokens())
				textContent = textContent+stringTokenizer.nextToken()+" ";
			sentiment = SemanticHelper.findSentiment(textContent);
			System.out.println("Sentiment: "+sentiment+" of Tweet: " +textContent );
			
		}
		br.close();
	}

}
