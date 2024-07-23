### Document Download Redis Client

### Overview 

This application stores and maintains document download status for the Document Download API which is found [here](https://github.com/bcgov/bc-courts-doc-downloader/tree/develop/src/backend/DocDownloaderAPI). 

## Technical Overview

| Layer              | Technology            |
| ------------------ | --------------------- |
| Service            | Java, Springboot      |
| Application Server | Tomcat (Embedded)     |
| Runtime            | OpenShift             |

### Dependencies

This API depends on a existance of a Redis Cache instance. See below for configuration instructions.  

### Environmental Variables

The following environmental variable must available to this API at runtime:

| Name                            | Comment    | Ex. Value |
| ------------------------------- | ---------------- |---------|
| REDIS_CACHE_AUTH_CLIENT		  | API Basic Auth User |	user |
| REDIS_CACHE_AUTH_SECRET	      | API Basic Auth password | password |
| REDIS_HOST					| Redis Cache server host | http://myHost |
| REDIS_PORT					| Redis Cache server port | 8080 |
| REDIS_PROFILE					| Spring Profile | dev |
| SPLUNK_URL					| Splunk HEC endpoint | http://splunk |
| SPLUNK_TOKEN					| Splunk HEC token | abcdef |

### Setting up and running this application locally

**Redis Docker Container Setup, launch and configure**  

> docker pull redis:latest

Run the new container in Docker Desktop using ports 6379:6379.  
In a terminal window for the running container execute the following commands:  

> redis-cli
> config set requirepass admin
> quit

Now install and launch this application from the IDE of your choice. 

> mvn clean install  
> mvn spring-boot:run -P splunk

Assuming Redis Server host and port has been set properly, the application should launch and connect to the Redis Server.  

### Actuator

This API includes a number of actuator endpoints providing various metrics. Largely these may be used for health of the API.

**All Metrics**  
https://server.name:[port]/actuator/metrics

**Health Status**  
https://server.name:[port]/actuator/health

### API Operations 

For a complete list of the available API operations, see [here](https://github.com/bcgov/bc-courts-doc-downloader/blob/develop/src/backend/DocDownloaderRedisCache/RedisCacheClient-spec.yaml).

**Note:** The yaml spec file may be loaded to [here](https://editor.swagger.io/) for easier viewing. 

## DevOps Process

All changes to this API must be made as branches from the 'develop' branch. After a successful PR, a feature branch is merged back to 'develop' which kicks off the CiCD pipeline and automatically deploys the code to dc2d23-dev (docdownloader-redis-cache-client deployment). 

## TEST/PROD Promotion

To push the same image to TEST and/or PRODUCTION, (tbd).  

### License

Apache license 2.0
