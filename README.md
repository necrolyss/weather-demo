# weather-demo

N.B! Unfortunatley, github does not allow to upload files > 25MB ; so I can send it privately by request, or can be built manually by running command mvn clean install

Functional requirements: implemeted fully

Non functional requirements:
1. Coverage 94%
2. Remote unavailability handled with resilience4j - performs multiple tries before fail. If fail, exception goes through ApplicationExceptionHandler, where is "decorated"
3. Caching is performed with Spring @Cacheable and Caffeine caches. Caches has configurable expiration time and size (application.properties)
4. Geolocation data stored in a database as a cache
5. LocationConditions entity extends DateAudit, therefore it's possible to query weather conditions for specific location in given time range and / or within specified coordinates
6. Implemeted by liquibase

To run application:

 1) java -jar weather-demo-0.0.1-SNAPSHOT.jar (required Java 16 to run)
 2) Send GET request on http://localhost:8080/api/weather/current with 2 headers:
    2.1) Content-Type : application/json
    2.2) Debug-IP : some public IP address (e.g. 185.80.236.209). Will not work correctly (ofc., handled and delicately informed to user) 
          for localhost or private IP addresses and IPv6 addresses

DB Structure:

![mintos-db-diagram](https://user-images.githubusercontent.com/16892576/115747793-42742400-a39e-11eb-826c-06c2473322b3.png)

Success example:         

![Mintos-demo-lookup-success](https://user-images.githubusercontent.com/16892576/115747810-456f1480-a39e-11eb-988e-06ac0c1e3b8c.png)


