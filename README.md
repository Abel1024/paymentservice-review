# README #

Recruitment task review.

### Assumptions ###

The code in this repository is created with keeping these goals in mind:

* Test first approach **TDD** (in the domain area).
* **Isolation** and **encapsulation** (limit the dependencies between software components)
* **maintainability**
* Ease of adding new features in the future.
* Separation of state mutators and lookups.
* Testing at the interface level (not testing individual classes).
* This is a start of a potentially new project, so there must be a lot of space for the developers to add new code.

### Where to start the review? ###

It is recommended to have a look first at the [Protocol](ps-domain/src/main/scala/payment/Protocol.scala)

* `Commands` - they produce new state or a `DomainError`
* `Queries` - they produce a view of the current state represented by `Responses` or a `DomainErrors`

It is possible to interact with the domain using the [protocol](ps-domain/src/main/scala/payment/Protocol.scala) via the
[Payments](ps-domain/src/main/scala/payment/Payments.scala) trait.   
Currently, there is only one implementation which stores all data in a Vector in
memory [VectorPayments](ps-domain/src/main/scala/payment/VectorPayments.scala)

### How to run the Http Server? ###

In the main project directory:

```sbt
sbt run
```

### How to send HTTP requests? ###

Configuration for [Postman](https://www.postman.com/company/about-postman/) can be found
here: [Postman Collection](Payment.postman_collection.json)

### How to run tests? ###

In the main project directory:

```sbt
sbt clean test
```

### Additional assumptions: ###

* All the data that are kept in the domain can easily fit in the server memory.
* It is good enough to protect the state from corruption with a
  single [ReentrantReadWriteLock](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/locks/ReentrantReadWriteLock.html)
  as done here [Service](ps-service/src/main/scala/ps/service/PaymentsService.scala).

### What is not implemented? ###

* Statistics
* Automated tests at the HTTP API level
* Proper handling of BigDecimals as money
* Request validation for **/payments?currency=X**

### What is in each module? ###

* [ps-domain](ps-domain) - a fully immutable domain structure that handles all the logic of payments
* [ps-service](ps-service) - an isolation component, it forwards the requests from outside the world to the domain, it
  is a good place to add additional orchestration.
* [ps-time](ps-time) - time related utilities.
* [ps-transfer](ps-transfer) - a place for data transfer classes