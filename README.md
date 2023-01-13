# Kafka example

`docker-compose up -d`

`curl -X POST http://localhost:8080/send`

`curl -X POST http://localhost:8080/send/nack`

`curl -X POST http://localhost:8080/send/long-processing`

`curl -X POST http://localhost:8080/send/error`

`curl -X POST http://localhost:8080/send/error-retry`

`curl -X POST http://localhost:8080/send/count/10`

`curl -X POST http://localhost:8080/reactive/send`

Running a Spring Boot application (development/testing)
`mvn spring-boot:run`

**Example - handle error and "repeat()"**
```java
public class Example {
    private Disposable createConsumer() {
        return consumerTemplate.receive()
                .doOnNext(receiverRecord -> { 
                    // some processing with ACK/COMMIT (+ error handling for processing)
                })
                .onErrorResume(throwable -> { // This will "resume" on error for "repeat()"
                    log.error("onErrorResume", throwable);
                    return Mono.empty();
                })
                .repeat() // This will "restart" a consumer on error (e.g. CommitFailedException)
                .subscribe(); //Tested with `reactor-kafka:jar:1.3.8`
    }
}
```