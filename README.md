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
To avoid having to specify the class path, the Makefile also allows you to run the programs. These all start the server on the port 4567 and assume its running on local host.

To compile and run the aggregation server, run the following commands:
```
make run-server
```

I have provided three utilies for running the ContentServer, each of should attempt to PUT the contents of weather1.txt, weather2.txt, and weather3.txt (all located in ./Stations) respectively. 
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

You can also run the GETClient with parameters to specify which weather station to get data from, corresponding again with the data created after weather1.txt, weather2.txt and weather3.txt have been PUT into weather.json. To do this, run the following commands:
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
## HTTP Headers 
### **GET HEADERS** 

The request line can either be ```GET / HTTP/1.1``` or ```GET /weather.json HTTP/1.1```. There is no functional difference between these two. If these are sent alone, the server will return all data in the weather.json file.

To specify a specific weather station, the ```Station-ID``` header must be specified as part of the request. This can look like the following: 

```
GET /weather.json HTTP/1.1
Station-ID: IDS60901
```
```
GET / HTTP/1.1
Station-ID: IDS60901
```
If you have specified a weather station but it is not found in the weather.json file, the server will simply return the entire weather.json file, even if it only contains ```{}```. 

* Sending a GET request before weather.json is created simply returns a 404 error.

# Testing Strategy