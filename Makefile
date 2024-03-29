JCC = javac
JFLAGS = -g
JC = java
CP = -cp .:./Libraries/*
PORT = 4567

default: ContentServer.class AggregationServer.class GETClient.class

ContentServer.class: ContentServer.java
	$(JCC) $(JFLAGS) $(CP) ContentServer.java

AggregationServer.class: AggregationServer.java
	$(JCC) $(JFLAGS) $(CP) AggregationServer.java

GETClient.class: GETClient.java
	$(JCC) $(JFLAGS) $(CP) GETClient.java

run-content1: ContentServer.class
	$(JC) $(CP) ContentServer localhost:$(PORT) ./Stations/weather1.txt

run-content2: ContentServer.class
	$(JC) $(CP) ContentServer localhost:$(PORT) ./Stations/weather2.txt

run-content3: ContentServer.class
	$(JC) $(CP) ContentServer localhost:$(PORT) ./Stations/weather3.txt

run-server: AggregationServer.class
	$(JC) $(CP) AggregationServer

run-client: GETClient.class
	$(JC) $(CP) GETClient localhost:$(PORT)

run-client1: GETClient.class
	$(JC) $(CP) GETClient localhost:$(PORT) IDS60901

run-client2: GETClient.class
	$(JC) $(CP) GETClient localhost:$(PORT) BNE405

run-client3: GETClient.class
	$(JC) $(CP) GETClient localhost:$(PORT) SYD001

clean: 
	$(RM) *.class
	$(RM) weather.json
	$(RM) ./Tests/*.txt

# Testing
unit: GETClient.class GETClientTest.class ContentServer.class ContentServerTest.class AggregationServer.class AggregationServerTest.class
	$(JC) $(CP) GETClientTest
	$(JC) $(CP) ContentServerTest
	$(JC) $(CP) AggregationServerTest

GETClientTest.class: GETClientTest.java
	$(JCC) $(JFLAGS) $(CP) GETClientTest.java

ContentServerTest.class: ContentServerTest.java
	$(JCC) $(JFLAGS) $(CP) ContentServerTest.java

AggregationServerTest.class: AggregationServerTest.java
	$(JCC) $(JFLAGS) $(CP) AggregationServerTest.java

integration: 
	./IT-1.sh
	./IT-2.sh
	./IT-3.sh
	./IT-4.sh
	./IT-Clean.sh
	./IT-Compare.sh
