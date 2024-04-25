### Redis Client Prototype Application 

Provides the necessary starting application for storing document transfer state in a Redis cache (External).

It is assumed that the Redis cache has been stood up and is running at the server and port defined in the application.properties file.  

srcs used in creating this application: 
- https://www.baeldung.com/spring-data-redis-tutorial
- https://github.com/eugenp/tutorials/tree/master/persistence-modules/spring-data-redis
- https://www.docker.com/blog/how-to-use-the-redis-docker-official-image/#how-to-run-redis-in-docker 
 
Operation List

| HTTP Verb  | Path 	| Comment |
| ---------- | ----------- | ---------- |
| GET  	     | /jobs       | Report all jobs |
| GET 	     | /job/{id}   | Get a jobs status |
| POST       | /job        | Add a job status |
| PUT        | /job/{id}   | Update a job status |
| DEL        | /job/{id}   | Remove a Job status |



