# README #

Recruitment task review.

### Assumptions ###

The code in this repository is created with keeping these goals in mind:

* test first approach TDD (in the domain area)
* isolation and encapsulation (limit the dependencies between software components)
* maintainability
* ease of future extensions
* separation of state mutators and lookups
* testing at the interface level (not testing individual classes)

### How to run the HttpServer ###

In the main project directory:

```sbt
sbt run
```

### How to send HTTP requests ###

Configuration for [Postman](https://www.postman.com/company/about-postman/) can be found
here: [Postman Collection](Payment.postman_collection.json)

### Running tests ###

In the main project directory:

```sbt
sbt clean test
```