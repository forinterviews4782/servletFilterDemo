# Interview exercise.
[![Build Status](https://travis-ci.com/forinterviews4782/servletFilterDemo.svg?branch=master)](https://travis-ci.com/forinterviews4782/servletFilterDemo)
This repository is an interview exercise demonstrating using servlet filters to add a tracking header to all requests as well as keeping track of response size and time.
## Instructions to run
1. Insure you have Java 8 or above installed.
2. Clone the repository
3. Change to the repository directory.
4. Execute

```gradlew :webapp:bootRun```

To run the project. Note by default the application uses port 8080. To change this edit webapp/src/resources/application.properties.
Once the project is running to run two requests whose response time should average out to about 2 seconds and whose response size should average to 10 execute the following curl commands.

```
curl -v http://localhost:8080/demo?milliseconds=1000\&stringchars=5
curl -v http://localhost:8080/demo?milliseconds=3000\&stringchars=15
```

To get summary information on these requests execute the following command.
```
curl http://localhost:8080/metricSummary
```
You should see output similar to the following.
```
{"responseAverage":10.0,"responseMax":15,"responseMin":5,"timingAverage":2095.0,"timingMax":3003,"timingMin":1187}
```
Note the timingMin and timingAverage numbers will be slightly off. This is due to the filter initialization code slowing down the first request. This would not be an issue if a lot of traffic was run through the application.
To get metrics on a specific request execute the following command

```curl http://localhost:8080/metric/237d22c0-c546-4c61-8730-52bb62bff056```

Replacing 237d22c0-c546-4c61-8730-52bb62bff056 with the value of the request-id header from a call to /demo.
## General design
This repository contains two projects. metrics is a servlet filter that meets the servlet 3.1 spec. This filter calculates the response size and time and sends that information to a rest endpoint. The webapp project is a simple spring boot application configured to use the filter. It has two endpoints, one to execute requests of arbitrary size and duration, and another endpoint to provide summary statistics based on data sent by the filter. Details of each project follow.
### metrics project
Following are details about the most important files in this project.
#### MetricsFilter.java
MetricsFilter.java is the file that implements the servlet filter interface. The init method calls internalInit to do the filter initialization. This is because in our unit tests we need to mock out the thread that sends data. To try and keep the code as portable as possible I avoided any use of Java EE annotations such as inject which is why I added an extra parameter that we can use to inject mocks. For an example of this see method testInitializingQueueWithNonDefaultValue in MetricsFilterTest.java
The internalInit method initializes a queue to temporarily store metrics created in the doFilter method. It also creates a thread that reads from the queue and sends the metrics to an endpoint.
The doFilter method is where metrics are generated. This method adds a custom header to the request by generating a UUID. It also logs the time it takes the filter chain to complete it’s work as well as using a custom response wrapper to get the total number of bytes written to the response. This data is then added to a queue for processing by another thread. The processing is done asynchronously because sending the information synchronously would slow down all requests.
#### MetricsResponseWrapper.java
MetricsResponseWrapper.java is a class that extends HttpResponseWrapper. This class is used in the filter so that all data is written to a byte array. The length of this array is then used to calculate the response size before the data is written to the client.
#### MetricsSendingThread.java 
MetricsSendingThread.java reads from the queue set up in MetricsFilter.java. When ever a new item is available it uses the MetricsSender class to send the data to an endpoint using REST. The MetricsSender class is purposely written with out third party libraries for producing JSON or making post requests to avoid possible class conflicts when using the filter in servlet containers that may have who knows what loaded.
### webapp project
#### WebConfiguration.java
WebConfiguration.java is used to load the metrics filter into the springboot application. Since this is a demonstration program we only run the filter on the /demo endpoint. This makes testing easier since calling the /metric endpoint to get information on requests made to /demo will not throw off the expected numbers by adding unexpected metrics.
#### DemoControler.java
DemoControler.java is an endpoint that is called to manually test the filter. It takes two query parameters. The milliseconds query parameter causes the endpoint to sleep for the specified number of milliseconds and the stringchars parameter insures that a response of the requested size gets returned.
#### MetricsControler.java
MetricsControler.java is used to look up an individual metric, store metrics sent by post requests from the filter, and provide a summary of all stored metrics. The metrics are stored in a hash map and statistics are calculated from scratch when the metricsSummary endpoint is called. I realize that calculating metrics statistics from scratch is not something you would want to do in production but since the webapp is not the focus of this exercise I figured the simplicity was worth the performance tradeoff
