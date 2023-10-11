# How to Run
To compile and run the aggregation server, run the following commands:
```
make run-server
```

I have provided three utilies for running the ContentServer, each of which correlating to weather1.txt, weather2.txt, and weather3.txt. 
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

## Extra Utilities 
To clean the project, run the following command:
```
make clean
```
* This will remove all .class files along with the weather.json file 

# Testing Strategy