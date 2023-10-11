JCC = javac
JFLAGS = -g
JC = java
CP = -cp .:./Libraries/json.jar:./Libraries/junit.jar
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
	$(JC) $(CP) GETClient localhost:$(PORT) randomID

clean: 
	$(RM) *.class
	$(RM) weather.json