## BC Courts Document Download API

## Overview  

The Courts Document Download API provides a set of operations for requesting the pre-download of Application Records from both Civil and Criminal SCV Court List Views to a user's OneDrive.  

## Technical Overview

| Layer              | Technology            |
| ------------------ | --------------------- |
| Service            | Java, Springboot      |
| Application Server | Tomcat (Embedded)     |
| Runtime            | OpenShift             |

### Dependencies

This API manages 'in transit' file status information with the Redis Client application found [here](https://github.com/bcgov/bc-courts-doc-downloader/tree/develop/src/backend/DocDownloaderRedisCache).

### Environmental Variables

The following environmental variable must available to this API at runtime:

| Name                            | Comment    | Ex. Value |
| ------------------------------- | ---------------- |---------|
| APPLICATION_DEBUG_LEVEL		  | debug level     |	info |
| APPLICATION_DEBUG_PASSWORD	  | basic auth password    | password |
| APPLICATION_USERNAME			| basic auth username | user |
| MSG_AUTHORITY					| MS Graph Authority | https://login.microsoftonline.com/uiy879dwlkoii |
| MSG_CLIENTID					| MS Graph Client Id | gT98~yuuudhk |
| MSG_ENDPOINT					| MS Graph endpoint | https://graph.microsoft.com/ |
| MSG_SECRETKEY					| MS Graph Secret Key | ku77y_ioppTf |
| ORDS_APPLICATION_ID			| Oracle REST Application Id | YYtH_3440 |
| ORDS_DATABASE_ID				| Oracle REST Database Id | pp22 |
| ORDS_PASSWORD					| Oracle REST password | password |
| ORDS_PUT_ID					| Oracle REST Put ID | 0999_99 |
| ORDS_SERVER 					| Oracle REST Server Name | Lemon |
| ORDS_SSG_BASE_URL				| SSG Base Url | https://myserver.com/v1 |
| ORDS_SSG_PASSWORD				| SSG Password | password |
| ORDS_SSG_USERNAME				| SSG Username | user |
| ORDS_TICKETLIFETIME			| Document Ticket lifetime | 20
| ORDS_USERNAME					| Oracle REST Username | OrdsUser | 
| REDIS_CLIENT_HOST				| Redis Client host | localhost |
| REDIS_CLIENT_PORT				| Redis Client port | 8082 |
| REDIS_CLIENT_USERNAME			| Redis Client username | RedisUser |
| REDIS_CLIENTPASSWORD			| Redis Client password | RedisPassword | 
| S3_ACCESS_ENDPOINT			| S3 Storage endpoint | https://mybucket |
| S3_ACCESS_BUCKET				| S3 Bucket name | dev_bucket |
| S3_ACCESS_KEYID				| S3 Storage Access Key | 7623e86823e |
| S3_ACCESS_SECRETKEY			| S3 Storgae Secret Key | 7gi7viuhUi | 
| S3_POLLER_TIMEOUT 			| S3 Storage poller time out (mins) | 10 |
| API_PROFILE					| Spring Profile | dev |


### Setting up and running the application locally

Once enviromental variables have been set in the IDE of your choice, AND the Redis Cache Client application is running (See [here](https://github.com/bcgov/bc-courts-doc-downloader/tree/develop/src/backend/DocDownloaderRedisCache)) launch the API with: 

> mvn clean install  
> mvn spring-boot:run
  
### Actuator

This API includes a number of actuator endpoints providing various metrics. Largely these may be used for determing JVM memory usage, memory tuning, or troubleshooting.

**All Metrics**  
https://server.name:[port]/actuator/metrics

**Memory Related**  
https://server.name:[port]/actuator/metrics/jvm.memory.max  
https://server.name:[port]/actuator/metrics/jvm.memory.used  


### API Operations 

For a complete list of the available API operations, see [here](https://github.com/bcgov/bc-courts-doc-downloader/blob/develop/src/backend/DocDownloaderAPI/DocDownloader-spec.yaml).

**Note:** The yaml spec file may be loaded to [here](https://editor.swagger.io/) for easier viewing. 

## DevOps Process

All changes to the API must be made as branches from the 'develop' branch. After a successful PR, a feature branch is merged back to 'develop' which kicks off the CiCD pipeline and automatically deploys the code to dc2d23-dev (docdownloader-api deployment). 


## TEST/PROD Promotion

To push the same image to TEST and/or PRODUCTION, (tbd).  

### License

Apache license 2.0
