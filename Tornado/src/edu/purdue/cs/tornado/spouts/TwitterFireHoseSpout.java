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
package edu.purdue.cs.tornado.spouts;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Spout which gets tweets from Twitter using OAuth Credentials.
 * Adopted from https://github.com/P7h/StormTweetsSentimentAnalysis
 * @author - Prashanth Babu
 */
public final class TwitterFireHoseSpout extends BaseRichSpout {
	//**********************************************************************************
	//**********************Twitter APIs constants
	public static final String OAUTH_ACCESS_TOKEN = "OAUTH_ACCESS_TOKEN";
	public static final String OAUTH_ACCESS_TOKEN_SECRET = "OAUTH_ACCESS_TOKEN_SECRET";
	public static final String OAUTH_CONSUMER_KEY = "OAUTH_CONSUMER_KEY";
	public static final String OAUTH_CONSUMER_SECRET = "OAUTH_CONSUMER_SECRET";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TwitterFireHoseSpout.class);
	private static final long serialVersionUID = -6815379407002113362L;

	private SpoutOutputCollector _outputCollector;
    private LinkedBlockingQueue<Status> _queue;
    private TwitterStream _twitterStream;

	@Override
	public final void open(final Map conf, final TopologyContext context,
	                 final SpoutOutputCollector collector) {
		this._queue = new LinkedBlockingQueue<>(1000);
		this._outputCollector = collector;

		final StatusListener statusListener = new StatusListener() {
			@Override
			public void onStatus(final Status status) {
				_queue.offer(status);
			}

			@Override
			public void onDeletionNotice(final StatusDeletionNotice sdn) {
			}

			@Override
			public void onTrackLimitationNotice(final int i) {
			}

			@Override
			public void onScrubGeo(final long l, final long l1) {
			}

			@Override
			public void onStallWarning(final StallWarning stallWarning) {
			}

			@Override
			public void onException(final Exception e) {
			}
		};
		//Twitter stream authentication setup
		final Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(SpatioTextualConstants.CONFIG_PROPERTIES_FILE));
		} catch (final IOException ioException) {
			//Should not occur. If it does, we cant continue. So exiting the program!
			LOGGER.error(ioException.getMessage(), ioException);
			System.exit(1);
		}

		final ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
		configurationBuilder.setIncludeEntitiesEnabled(true);

		configurationBuilder.setOAuthAccessToken(properties.getProperty(TwitterFireHoseSpout.OAUTH_ACCESS_TOKEN));
		configurationBuilder.setOAuthAccessTokenSecret(properties.getProperty(TwitterFireHoseSpout.OAUTH_ACCESS_TOKEN_SECRET));
		configurationBuilder.setOAuthConsumerKey(properties.getProperty(TwitterFireHoseSpout.OAUTH_CONSUMER_KEY));
		configurationBuilder.setOAuthConsumerSecret(properties.getProperty(TwitterFireHoseSpout.OAUTH_CONSUMER_SECRET));
		this._twitterStream = new TwitterStreamFactory(configurationBuilder.build()).getInstance();
		this._twitterStream.addListener(statusListener);

		//Our usecase computes the sentiments of States of USA based on tweets.
		//So we will apply filter with US bounding box so that we get tweets specific to US only.
		final FilterQuery filterQuery = new FilterQuery();

		//Bounding Box for United States.
		//For more info on how to get these coordinates, check:
		//http://www.quora.com/Geography/What-is-the-longitude-and-latitude-of-a-bounding-box-around-the-continental-United-States
		final double[][] boundingBoxOfUS = {{-124.848974, 24.396308},
				                                   {-66.885444, 49.384358}};
		filterQuery.locations(boundingBoxOfUS);
		this._twitterStream.filter(filterQuery);
	}

	@Override
	public final void nextTuple() {
		final Status status = _queue.poll();
		if (null == status) {
			//If _queue is empty sleep the spout thread so it doesn't consume resources.
			Utils.sleep(500);
        } else {
			//Emit the complete tweet to the Bolt.
			this._outputCollector.emit(new Values(status));
		}
	}

	@Override
	public final void close() {
		this._twitterStream.cleanUp();
		this._twitterStream.shutdown();
	}

	@Override
	public final void ack(final Object id) {
	}

	@Override
	public final void fail(final Object id) {
	}

	@Override
	public final void declareOutputFields(final OutputFieldsDeclarer outputFieldsDeclarer) {
		//For emitting the complete tweet to the Bolt.
		outputFieldsDeclarer.declare(new Fields("tweet"));
	}
}
