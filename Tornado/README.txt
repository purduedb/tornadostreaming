*******************************************************************************************************
This is the release version
Tornado Instuctions
-------------------
Localcluster depolyment instructions
1) install local zookeeper  http://zookeeper.apache.org/doc/r3.3.3/zookeeperStarted.html
2) install local kakfka http://kafka.apache.org/07/quickstart.html
3) Get tornado code 
	https://bitbucket.org/ar8ahmed/tornado 
please send me a request to with your bitbucket account so i can grant you access 

4) you will need to use a tweets file 
you can get the one from ibalyatham tweets there is a file named small.csv or any larger file
ibnalhaytham.cs.purdue.edu
tweet files are located in /scratch2/aaly/brian/

5)import the project in eclipse  as a maven project

6) you will need to download the corpus for disco that is located at 
http://www.linguatools.de/disco/disco-wordspaces.html#enwiki13w2vslm
and set the proper path in the configuration 

7) set the following configurations in your resources\config.properties file according to the locations of relevant files in your machine and 
URL of zookeeper and kafka 
kafkaZookeeper= localhost:2181
kafkaConsumerGroup=queryprocExample
kafkaConsumerTopic=queries
kafkaProducerTopic=output
kafkaBootstrapServerConfig=localhost:9092
discoDir=/home/ahmed/Downloads/enwiki-20130403-word2vec-lm-mwl-lc-sim/
stormSubmitType=localCluster
NIMBUS_HOST=localhost
NIMBUS_THRIFT_PORT=6627
STORM_ZOOKEEPER_PORT=2181
STORM_ZOOKEEPER_SERVERS=localhost
STORM_JAR_PATH=target/tornado-0.0.1-SNAPSHOT-jar-with-dependencies.jar
STORM_NUMBER_OF_WORKERS=20
TWEETS_FILE_PATH=/home/ahmed/Downloads/sample_usa_tweets.csv
POIS_PATH=datasources/pois.csv






*******************************************************************************************************
Tornado  UI instructions  
--------------------------

this can run from your machine, i will access the server from kafka 
https://github.com/qadahtm/webui/tree/tornado-ui 

you will need to pull the tornado branch 
all depolyment instrucations are located in the git repository file


******************************************************************************************************
Important notes for comilation and generate a jar file 
----------------------------------------------------------
To compile this code properly using Maven you need to install the Disco depenecey and jar in your local 
maven repository
using the following statement
the jar file exists in the lib folder of this project

mvn install:install-file -Dfile=disco-2.0.jar -DgroupId=com.disco -DartifactId=disco -Dversion=10.2.0 -Dpackaging=jar
