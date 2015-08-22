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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.lucene.index.CorruptIndexException;

import de.linguatools.disco.CorruptConfigFileException;
import de.linguatools.disco.DISCO;
import de.linguatools.disco.ReturnDataBN;
import de.linguatools.disco.ReturnDataCol;
import de.linguatools.disco.TextSimilarity;
import de.linguatools.disco.WrongWordspaceTypeException;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

public class SemanticHelper {
	/**
	 * Adopted from http://rahular.com/twitter-sentiment-analysis/
	 */
	static StanfordCoreNLP pipeline;
	public static void initSentimentAnalysis() {
		Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
        pipeline = new StanfordCoreNLP(props);
    }
	/**
	 * Adopted from http://rahular.com/twitter-sentiment-analysis/
	 */
    public static int findSentiment(String text) {

        int mainSentiment = 0;
        if (text != null && text.length() > 0) {
            int longest = 0;
            Annotation annotation = pipeline.process(text);
            for (CoreMap sentence : annotation
                    .get(CoreAnnotations.SentencesAnnotation.class)) {
                Tree tree = sentence
                        .get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
                int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
                String partText = sentence.toString();
                if (partText.length() > longest) {
                    mainSentiment = sentiment;
                    longest = partText.length();
                }

            }
        }
        return mainSentiment;
    }
    
  
    public static ArrayList<String> findSemanticRelatedKeywords(String keyword){
    	return null;
    }
    public static DISCO getDiskBasedDiscoInstance(String discoDir){
    	
    	DISCO disco=null;
        try {
           
				disco = new DISCO(discoDir, false);
			
        } catch (IOException|CorruptConfigFileException ex) {
            System.out.println("Error creating DISCO instance: "+ex);
            return null;
        }
        return disco;
    }
    public static DISCO getMemoryBasedDiscoInstance(String discoDir){
    	
    	DISCO discoRAM=null;
        try {
            discoRAM = new DISCO(discoDir, true);
        } catch (FileNotFoundException | CorruptConfigFileException ex) {
            System.out.println("Error creating DISCO instance: "+ex);
            return null;
        } catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return discoRAM;
    }
    public static ArrayList<String> getSematicallySimilarKeyWords(DISCO disco, String keyword){
    	ArrayList<String> similariKeywords = new ArrayList<String>();
    	similariKeywords.add(keyword);
    	ReturnDataBN simResult=null;
    	if(disco!=null&&keyword!=null)
			try {
				simResult = disco.similarWords(keyword);
			} catch (IOException | WrongWordspaceTypeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	if(simResult!=null)
    	for(int i=0;i<simResult.words.length&&i<SpatioTextualConstants.maxSimilarKeywords;i++){
    		similariKeywords.add(simResult.words[i]);
		}
    	return similariKeywords;
    }
    public static ArrayList<String> getSematicallySimilarKeyWords(DISCO disco, ArrayList<String> keywords){
    	ArrayList<String> similariKeywords = new ArrayList<String>();
    	for(String keyword: keywords){
    		similariKeywords.addAll(getSematicallySimilarKeyWords(disco, keyword));
    	}
    	return similariKeywords;
    }
    public static Float getSimiarityScore(DISCO disco, String text1,String text2){
    	Float result =null;
    	 try {
         
            TextSimilarity textSimialirty = new TextSimilarity();
            result= textSimialirty.textSimilarity(text1, text2, disco);
             
         } catch (Exception ex) {
             System.out.println("Error retrieving semantic similairty: "+ex);
             return null;
         }
    	 return result;
    }
    public static Double findSemanticRelatednessScore(ArrayList<String> list1, ArrayList<String> list2){
    	
    	try{
    		// first command line argument is path to the DISCO word space directory
            String discoDir = "/home/ahmed/Downloads/enwiki-20130403-word2vec-lm-mwl-lc-sim/";
            // second argument is the input word
            String word = "fly";

    	/****************************************
    	 * create instance of class DISCO.      *
    	 * Do NOT load the word space into RAM. *
    	 ****************************************/
            DISCO disco;
            try {
                disco = new DISCO(discoDir, false);
            } catch (FileNotFoundException | CorruptConfigFileException ex) {
                System.out.println("Error creating DISCO instance: "+ex);
                return null;
            }

            // is the word space of type "sim"?
            if( disco.wordspaceType != DISCO.WordspaceType.SIM ){
               System.out.println("The word space "+discoDir+" is not of type SIM!");
               return null;
            }
            
            // retrieve the frequency of the input word
            int freq = disco.frequency(word);
            // and print it to stdout
            System.out.println("Frequency of "+word+" is "+freq);

            // end if the word wasn't found in the index
            if(freq == 0) return null;

            // retrieve the collocations for the input word
            long startTime = System.currentTimeMillis();
            ReturnDataCol[] collocationResult = disco.collocations(word);
            long endTime = System.currentTimeMillis();
        	long elapsedTime = endTime - startTime;
        	System.out.println("OK. getting coolocations words  took "+elapsedTime+" ms.");
            // and print the first 20 to stdout
            System.out.println("Collocations:");
            for(int i = 1; i < collocationResult.length; i++){
                System.out.println("\t"+collocationResult[i].word+"\t"+
                        collocationResult[i].value);
    	    if( i >= 20 ) break;
            }

            // retrieve the most similar words for the input word
            ReturnDataBN simResult;
            try {
            	startTime = System.currentTimeMillis();
                simResult = disco.similarWords(word);
                 endTime = System.currentTimeMillis();
            	 elapsedTime = endTime - startTime;
            	System.out.println("OK. getting similar words  took "+elapsedTime+" ms.");
            } catch (WrongWordspaceTypeException ex) {
                System.out.println("Error retrieving most similar words: "+ex);
                return null;
            }
            
            try {
            	startTime = System.currentTimeMillis();
               TextSimilarity textSimialirty = new TextSimilarity();
              float similairty= textSimialirty.textSimilarity("i love coffee and tea", "i love coffee", disco);
                 endTime = System.currentTimeMillis();
            	 elapsedTime = endTime - startTime;
            	 System.out.println("Semantic similairty "+similairty+" .");
            	System.out.println("OK. getting semantic similairty  took "+elapsedTime+" ms.");
            } catch (Exception ex) {
                System.out.println("Error retrieving semantic similairty: "+ex);
                return null;
            }
            
            // and print the first 20 of them to stdout
            System.out.println("Most similar words:");
            for(int i = 1; i < simResult.words.length; i++){
                System.out.println("\t"+simResult.words[i]+"\t"+simResult.values[i]);
    	    if( i >= 20 ) break;
            }

            // compute second order similarity between the input word and its most
            // similar words
    	System.out.println("Computing second order similarity between "+word+
    			   " and all of its similar words...");
    	 startTime = System.currentTimeMillis();
            for(int i = 1; i < simResult.words.length; i++){
                try {
                    float s2 = disco.secondOrderSimilarity(word, simResult.words[i]);
                } catch (WrongWordspaceTypeException ex) {
                    System.out.println("Error computing second order similarity: "+ex);
                    return null;
                }
    	}
    	 endTime = System.currentTimeMillis();
    	 elapsedTime = endTime - startTime;
    	System.out.println("OK. Computation took "+elapsedTime+" ms.");

    	/**********************************************
    	 * Create another DISCO instance,             *
    	 * this time loading the word space into RAM. *
    	 **********************************************/
    	System.out.println("Trying to load word space into RAM...\n"+
    			 "(in case of OutOfMemoryError: increase JVM "+
    			 "heap space to size of word space directory!)");
    	startTime = System.currentTimeMillis();
            DISCO discoRAM;
            try {
                discoRAM = new DISCO(discoDir, true);
            } catch (FileNotFoundException | CorruptConfigFileException ex) {
                System.out.println("Error creating DISCO instance: "+ex);
                return null;
            }
    	endTime = System.currentTimeMillis();
    	long elapsedTimeLoad = endTime - startTime;
    	System.out.println("OK (loading to RAM took "+elapsedTimeLoad+" ms)");

            // compute second order similarity between the input word and its most
            // similar words in RAM
    	System.out.println("Computing second order similarity between "+word+
    			   " and all of its similar words in RAM...");
    	startTime = System.currentTimeMillis();
            for(int i = 1; i < simResult.words.length; i++){
                try {
                    float s2 = discoRAM.secondOrderSimilarity(word, simResult.words[i]);
                } catch (WrongWordspaceTypeException ex) {
                    System.out.println("Error computing second order similarity: "+ex);
                    return null;
                }
    	}
    	endTime = System.currentTimeMillis();
    	long elapsedTimeRAM = endTime - startTime;
    	System.out.println("OK. Computation took "+elapsedTimeRAM+" ms in RAM.");
    	if( elapsedTimeRAM >= elapsedTime ){
    	    System.out.println("Maybe your system had to swap to disk?");
            }
    	}catch (Exception e ){
    		e.printStackTrace();
    		
    	}
    	return null;
    }

}
