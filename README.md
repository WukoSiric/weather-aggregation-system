# How to run
To compile all java files, run the following command:
```
make 
```

When running the programs, the class path must be specified to include the external libraries. 

**Aggregation Server:** 
``` 
java -cp ".:./Libraries/*" AggregationServer <port> 
```
If ```<port>``` is not specified, the server will run on port 4567.

**Content Server:** 
``` 
java -cp ".:./Libraries/*" ContentServer localhost:port <file_path>
```

**GETClient:**
```
java -cp ".:./Libraries/*" GETClient localhost:port <weather_station>
```
If ```<weather_station>``` is not specified, the GETClient will return all data in the weather.json file.


# How to run with Makefile
To avoid having to specify the class path, the Makefile also allows you to run the programs. These all start the server on the port 4567.

To compile and run the aggregation server, run the following commands:
```
make run-server
```

I have provided three utilies for running the ContentServer, each of should attempt to PUT the contents of weather1.txt, weather2.txt, and weather3.txt (all located in ./Stations). 
To run each content server, run the following commands:
```
make run-content1 
make run-content2
make run-content3
```

Finally, to run the GETClient, run the following command:
```
make run-client
```
* This won't specify which weather station to get data from, so it will return all data in the weather.json file

You can also run the GETClient with a specific weather station with any of the following commands, which insert the weather station as an argument for weather1.txt, weather2.txt, and weather3.txt respectively:
```
make run-client1
make run-client2
make run-client3
```
## Extra Utilities 
To clean the project, run the following command:
```
make clean
```
* This will remove all .class files along with the weather.json file 

# Architecture Design Decisions

# Testing Strategy