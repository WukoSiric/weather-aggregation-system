JCC = javac
JFLAGS = -g
JC = java
CP = -cp .:json.jar
PORT = 4567

default: ContentServer.class AggregationServer.class

ContentServer.class: ContentServer.java
	$(JCC) $(JFLAGS) $(CP) ContentServer.java

AggregationServer.class: AggregationServer.java
	$(JCC) $(JFLAGS) $(CP) AggregationServer.java

run-content: ContentServer.class
	$(JC) $(CP) ContentServer localhost:$(PORT) weather1.txt

run-aggregation: AggregationServer.class
	$(JC) $(CP) AggregationServer $(PORT)
