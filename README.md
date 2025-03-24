# kibit
kibit home assignment - Instant Payment API

## Steps to run the application in docker

1. clone the repository
2. run gradle build
3. run docker-compose up -d

## Testing the application

There is some init data in the database. With it we can create a transaction in the following way:

Call localhost:8080/api/v0/transaction with the following json body:

```
{
    "clientTransactionId": "ab1656a2-6535-4aa9-a639-9cf7ba54c6ef",
    "sender": "5f8bae39-4d39-40cb-9c2f-f5aacd434d07",
    "recipient": "e46eaa3c-ae54-4914-87a2-cdd860c51594",
    "amount": 100.0
}
```

## Further possible improvements

On application level:
1. In order to ensure the high availability on application level I'd implement circuit breaker 
functionality for example with hystrix.
2. Implementing caching would also help in making the application more resilient.
3. proper timeout management and retry strategy

On infrastructure level I would go with the following setup:
1. load balancer in front of the application
2. multiple instances of the application with autoscaling by the actual traffic
3. database replication

