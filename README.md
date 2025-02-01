# Transactional OutBox With AsyncAPI, SpringModulith and ZenWaveSDK

Implementing a Transactional OutBox With AsyncAPI, SpringModulith and ZenWaveSDK.

<picture>
    <source
        srcset="https://www.zenwave360.io/posts/TransactionalOutBoxWithAsyncAPIAndSpringModulith/TransactionalOutBoxWithAsyncAPIAndSpringModulith-light.png"
        media="(prefers-color-scheme: light)">
    <source
        srcset="https://www.zenwave360.io/posts/TransactionalOutBoxWithAsyncAPIAndSpringModulith/TransactionalOutBoxWithAsyncAPIAndSpringModulith-dark.png"
        media="(prefers-color-scheme: dark)">
    <img
        src="https://www.zenwave360.io/posts/TransactionalOutBoxWithAsyncAPIAndSpringModulith/TransactionalOutBoxWithAsyncAPIAndSpringModulith-light.png"
        alt="Transactional OutBox"
        style="max-width: 100%;">
</picture>

We’ll explore how we can implement a Transactional Outbox Pattern to:

- Persist data to a supported transactional database (e.g., SQL or MongoDB).
- Send events to an external message broker like Kafka or RabbitMQ using Spring Cloud Stream.
- Leverage Spring Modulith Events transactional features.
- Use ZenWaveSDK Code Generator for AsyncAPI so you don’t need to write a single line of boilerplate code for the transactional outbox and event publishing.

Follow detailed instructions at https://www.zenwave360.io/posts/TransactionalOutBoxWithAsyncAPIAndSpringModulith

## Requirements

* JDK 21+
* Maven 3.8.+
* Docker Compose: in case you don't have Docker-Compose installed in your machine, install [Rancher Desktop](https://rancherdesktop.io/) and configure `dockerd` as engine (instead of `containerd`), this will include `docker` and `docker-compose` commands in your PATH.
* Your favorite IDE

## Getting Started

Use the following commands to run the application or tests:

* Start docker dependencies:

```bash
docker-compose up -d
```

* Run the application:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

* Open [Swagger UI](http://localhost:8080/swagger-ui/index.html) in your browser.
Use "Basic Authentication" with username `user` and password `password` to authenticate.

* Testing Spring Modulith Events Externalizer for Spring Cloud Stream:

```bash
curl -X 'POST' \
  'http://localhost:8080/api/customers' \
  -H 'accept: application/json' \
  -H 'Authorization: Basic dXNlcjpwYXNzd29yZA==' \
  -H 'Content-Type: application/json' \
  -d '{
  "name": "John Doe",
  "email": "me@email.com",
  "addresses": [
    {
      "street": "Rue del Percebe 13",
      "city": "Aqui"
    }
  ],
  "paymentMethods": [
    {
      "type": "VISA",
      "cardNumber": "string"
    }
  ]
}'
```

* Reading Avro messages from Kafka:

```bash
docker-compose exec -T schema-registry bash -c "kafka-avro-console-consumer --bootstrap-server kafka:19093 --topic customer.events.avro --from-beginning --property schema.registry.url=http://schema-registry:8081"
```



