### Redis Client Application

Provides the API for storing and retrieving document transfer states in a Redis cache (External).

### Running Client application in IntelliJ

* Run the main class `ca.bc.gov.ag.courts.RedisCacheApplication`
* Add to its configuration the JVM options: `-Dspring.profiles.active=dev`
* Same will apply if running the jar directly.

srcs used in creating this application:

- https://www.baeldung.com/spring-data-redis-tutorial
- https://github.com/eugenp/tutorials/tree/master/persistence-modules/spring-data-redis
- https://www.docker.com/blog/how-to-use-the-redis-docker-official-image/#how-to-run-redis-in-docker

Operation API endpoints:

| HTTP Verb | Path 	    | Comment             |
|-----------|-----------|---------------------|
| GET  	    | /jobs     | Report all jobs     |
| GET 	     | /job/{id} | Get a jobs status   |
| POST      | /job      | Add a job status    |
| PUT       | /job/{id} | Update a job status |
| DEL       | /job/{id} | Remove a Job status |

### Authentication:

API is now behind a basic authentication layer.
The username and password are set in the application.properties file.

```properties
REDIS_CACHE_AUTH_CLIENT for local default to client
REDIS_CACHE_AUTH_SECRET for local default to secret
```

### Redis Application:

As said, a Redis application should be running in your docker desktop
in order for the local Client Application to connect and start. To start
a Redis container (a temp instance that does not save its cache in restarts)
run the following commands in your terminal:

```shell
docker pull redis:latest
docker run --name loc-redis -p 6379:6379 -d redis redis-server --save ''
```

To ensure your application connects to Redis docker instance  
ensure the application.properties point to the same host and port.

```properties
spring.redis.host=${REDIS_HOST:127.0.0.1}
spring.redis.port=${REDIS_PORT:6379}
```

