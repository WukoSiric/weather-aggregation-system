JCC = javac
JFLAGS = -g
JC = java
CP = -cp .:json.jar

default: ContentServer.class 

ContentServer.class: ContentServer.java
	$(JCC) $(JFLAGS) $(CP) ContentServer.java

run: ContentServer.class
	$(JC) $(CP) ContentServer localhost:25565 weather1.txt