JCC = javac
JFLAGS = -g
JC = java
CP = -cp .:json.jar

default: ContentServer.class 

ContentServer.class: ContentServer.java
	$(JCC) $(JFLAGS) $(CP) ContentServer.java