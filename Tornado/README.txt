*******************************************************************************************************
Tornado Simple tutorial for developers
-------------------
Running tornado on your local machine using a simple example.
1) Clone the source code of tornado. 
https://github.com/purduedb/tornadostreaming

2)import the project in your favorite IDE, e.g., eclipse  as a Maven project
For more information on using Maven please following the following links
Maven in Eclipse: http://www.vogella.com/tutorials/EclipseMaven/article.html
Maven in IntelliJ:   https://www.jetbrains.com/help/idea/maven.html

3) Run the src/edu/purdue/cs/tornado/examples/TornadoTweetCountExample.Java

*******************************************************************************************************
To configure Tornado: Edit the values of the configuration file: 

resources/config.properties

*******************************************************************************************************
To support textual semantic similarity you will need 

(1) Download the corpus for disco that is located at 
http://www.linguatools.de/disco/disco-wordspaces.html#enwiki13w2vslm
and set the proper path in the configuration file
discoDir=/home/ahmed/Downloads/enwiki-20130403-word2vec-lm-mwl-lc-sim/

(2) Then install the Disco depenecey and jar in your local 
maven repository
using the following statement
the jar file exists in the lib folder of this project

mvn install:install-file -Dfile=disco-2.0.jar -DgroupId=com.disco -DartifactId=disco -Dversion=10.2.0 -Dpackaging=jar

*******************************************************************************************************
Tornado  UI instructions  
--------------------------

this can run from your machine, i will access the server from kafka 
https://github.com/qadahtm/webui/tree/tornado-ui 

you will need to pull the tornado branch 
all deployment instructions are located in the Github repository file

Tornado will need to have a zookeeper and kafka to communicate with the UI (even in your local machine)
*******************************************************************************************************
To install kafka 

https://kafka.apache.org/quickstart

*******************************************************************************************************
To install zookeeper 

https://zookeeper.apache.org/doc/r3.1.2/zookeeperStarted.html


*******************************************************************************************************
To run Tornado on a cluster you need to properly set the following configurations in your resources\config.properties file according
to the locations of relevant files in the cluster machine and the URLs of zookeeper and kafka 

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


******************************************************************************************************
To generate the generate Tornado jar file to be submitted to a cluster 
----------------------------------------------------------
Maven install the project
