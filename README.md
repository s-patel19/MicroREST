# microREST
A Spring Boot based microservice application.
3 microservices i.e. order-service , inventory-service, notification-service.
Tools used : Spring Boot Framework, Maven Build Tool, MySQL as Database, WebClient and Resilience for Sync Commn(order-service and inventory-service), Apache Kafka for Async Comm(order-service and notification-service)


## How to run the application without Docker

1. After that run `mvn spring-boot:run` by going inside each folder to start the applications.
2. Run zookeeper and broker in docker-compose.yml file for local kafka use.
3. Visit localhost:8761 to view status.
4. Use Postman or any other API client to sent requests.For eg.
   ```json
   {
    "orderLineItemsDtoList":[
        {
            "skuCode":"iphone",
            "price":2323,
            "quantity":0
        }
    ]
    }
    ```

## How to run the application using Docker
1. Run `docker-compose up -d` to start the applications.
