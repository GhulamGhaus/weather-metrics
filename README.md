# Weather Metrics Application

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/technologies/javase/17-relnote-issues.html)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.0-green.svg)](https://spring.io/projects/spring-boot)


## Overview

The Weather Metrics is a Spring Boot-based application to manage and analyze weather metrics collected by sensors.
It provides APIs for adding weather data, validating inputs, and querying statistical metrics over specified date ranges.

## Table of Contents

- [Introduction](#introduction)
- [Scope](#scope)
- [Feature](#feature)
- [Exposed API](#exposed-api)
- [Quickstart](#quickstart)
- [Prerequisites](#prerequisites)
- [Test with Postman](#testing-with-postman)
- [Configuration](#configuration)

### Introduction
Building a service that receives weather data from various sensors that
report metrics such as temperature, humidity, wind speed and query the application to fetch the metric information.
### Scope

Using REST API,
Add Weather metric data for a sensor
- As a User, I can add weather metric data for a sensor with following metric: Temperature, Humidity, WindSpeed
  - Each sensor data should include 
    metricName: The type of metric (e.g., "Temp").
    metricValue: The numeric value of the metric.
    unit: The unit of the metric (e.g., "Celsius", "g/kg", "km/h")

Retrieve Statistics for sensors over the Last 7 days
- As a user, I can retrieve statistical data (avg, min, max, or sum) for Temperature and Humidity metrics for one or more sensors (e.g., sensor-1 and sensor-2) over the last 7 days.
- As a user, Give me the statistics(avg or min or max or sum) of temperature and humidity for sensor 1 from start date to end date.
- As a user, Give me the statistics(avg or min or max or sum) of one or more metric from start date to end date, if date range not given, return latest of 7 days data.


### Feature
- Add new weather metrics.
- Query weather metrics based on sensor id.
- Query weather metrics statistics.
- Query the statistics by date rage.

### Technology and Framework Used

- Java 17 - programing language
- Spring-boot 3.4.0 - Framework 
- Embedded H2 Database - Database
- Lombok - annotations to simplify generation of getters, setters, builder, Slf4j etc.
- jakarta.validation-api 3.1.0 - API input validation
- Embedded Apache Tomcat with Spring-boot 3.4.0
- Spring-data-jpa - JPA to manage Data base repository

### Exposed API

Open API specification ;[ weather-metric-api.yaml](weather-metric-api.yaml)


Create a new weather metrics
- POST `/api/v1/weather/metric`

request body:


      `[
         {
            "sensorId": "string",
            "timestamp": "2024-12-15T18:52:05.436Z",
            "metrics": [
                  {
                  "metricName": "string",
                  "metricValue": 0,
                  "unit": "string"
                  }
             ]
         }
      ]`


- GET `/api/v1/weather/metric`
  
  - Query parameter `array[string] sensorId`

Response body: 

      `[
         {
            "sensorId": "string",
            "timestamp": "2024-12-15T18:52:05.436Z",
            "metrics": [
                  {
                  "metricName": "string",
                  "metricValue": 0,
                  "unit": "string"
                  }
             ]
         }
      ]`

Retrieve weather metric statistics 
- GET `/api/v1/weather/metric/statistic`

  - Query parameter 

    `array[string] sensorId`, 
    `string statistic, array[string]  metricName, string($date-time) startDate,  string($date-time) endDate
`

Response body:

      `[
         {
            "sensorId": "string",
            "timestamp": "2024-12-15T18:52:05.436Z",
            "metrics": [
                  {
                  "metricName": "string",
                  "metricValue": 0,
                  "statistic": "string"
                  }
             ]
         }
      ]`


### Quickstart

1. **Clone the repository:**

   ```bash
   git clone https://github.com/your-username/weather-metrics.git

2. **Navigate to the project directory:**

   ```bash
   cd weather-metrics

3. **Build the project:**

   ```bash
   mvn clean package

4. **Run the Application**
   ```bash
   java -jar target/weather-metrics-0.0.1-SNAPSHOT.jar
### Prerequisites
- Java 17
- Maven
- Docker (Optional)
- Postman (Optional) - API client to run post collection


### Testing with Postman

1. Download and install [Postman](https://www.postman.com/).

2. Import the provided Postman
   collection: [weather-metric-collection](weather-metric.postman_collection.json)
3. Open the imported collection in Postman.

4. Execute the included requests to test various API endpoints.

### Test with Docker
You can build a docker image with Dockerfile provided and run in environment where Docker demon is running

- Build the Docker Image
   Navigate to folder where [Dockerfile.yaml](Dockerfile.yaml)  and weather-metrics-0.0.1-SNAPSHOT.jar available. (/weather-metrics)
  
   run bellow command

      docker build -t weather-metrics .
  - Run the Docker container with image weather-metrics with expose port 8080

         docker run -p 8080:8080 weather-metrics

-  Verify and Test with postman or Curl command 
      access Get API http://localhost:8080/api/v1/weather/metric 

### Configuration
- Configure the database data source in src/main/resources/application.yaml
- Adjust application properties such as server port, database connection, etc., based on system requirements.
- Application is configured with in memory h2database with default configuration, refer application.yaml for more configuration details
- You can configure any SQL database by providing input to the application.yaml




