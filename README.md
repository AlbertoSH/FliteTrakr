# FliteTrakr

 
Solution for [Backend – FliteTrakr](https://bitbucket.org/adigsd/backend-flitetrakr) coding challenge

## How to build

From the root of the project just run

    $ gradle :flitetrakr_sync:build   # for sync version
    $ gradle :flitetrakr_reactive:build   # for reactive version
    $ gradle :flitetrakr_sync_kotlin:build   # for kotlin sync version
    
Or, if you just don't want to run the tests you can also run

    $ gradle :flitetrakr_sync:assemble   # for sync version
    $ gradle :flitetrakr_reactive:assemble   # for reactive version
    $ gradle :flitetrakr_sync_kotlin:assemble   # for sync version

The generated jars are at 
`flitetrakr_sync/build/libs/flitetrakr-1.0-SNAPSHOT.jar`
, `flitetrakr_reactive/build/libs/flitetrakr-1.0-SNAPSHOT.jar`
 and `flitetrakr_sync_kotlin/build/libs/flitetrakr-1.0-SNAPSHOT.jar`

---

## How to run

Once you've build the project you can just run with `java -jar`:

    $ java -jar flitetrakr_sync/build/libs/flitetrakr-1.0-SNAPSHOT.jar

This will trigger the "manual" mode so you can enter manually your queries. 
You can also specify a file where the queries are found with:

    $ java -jar flitetrakr_sync/build/libs/flitetrakr-1.0-SNAPSHOT.jar input.txt

or

    $ cat input.txt | java -jar flitetrakr_sync/build/libs/flitetrakr-1.0-SNAPSHOT.jar
    
The effect is the same. By default the sync version is executed (see below)

---

## How to use

Once the app has started it receives a list of connections with the 
following format:

    Connection: .*-.*-[0-9]+(, .*-.*-[0-9]+)*
    
Once it loads them it waits until a query is performed. It has a quite 
simple and flexible text analyzer so you don't have to worry if you
write something wrong. Some example inputs are:

    What is the price of the connection NUE-FRA-LHR?
    What is the cheapest connection from NUE to AMS?
    What is cheapest connection foorm AMS to FRA?
    How different connections with maximum 3 stops are between NUE and FRA?
    How mand different connections with exactly 1 stop exists between LHR and AMS?
    Find all conns f NUE t LHR below 170 euros

---

## Frameworks used

* [**Guava**](https://github.com/google/guava): Google Core Libraries for Java 6+. `Preconditions` methods are used in constructors to ensure valid values
* [**RxJava 1**](https://github.com/ReactiveX/RxJava): [Reactive Extensions](http://reactivex.io/) for Java. Used in the `reactive` version of the solution
* [**Junit**](http://junit.org/junit4/): test framework
* [**Hamcrest**](http://hamcrest.org/): matchers for tests
* [**Mockito**](http://mockito.org/): easy mocks for tests

---

## Multiple versions

As you can see there are two different versions. One of them uses a classic
and simple synchronous behaviour that makes it suitable for console applications
(like this) but will have a poor response in any event-driven environment
(desktop or mobile app). This impact is even greater if the `Connection`
objects supplied by the 'IConnectionService' are stored in a DB or retrieved
from some API.

As the main thread shouldn't be occupied in task like that I developed
an RxJava version that performs the work asynchronously and makes it suitable
to be integrated in some app without UX detriment.

Although this reactive version allow us to use it in other environments
this improvement isn't free. We pay the cost of setting up the `Observable`
structures. So depending of the environment we should choose wisely

---

## Kotlin

Kotlin is a statically-typed programming language that runs on the Java 
Virtual Machine and also can be compiled to JavaScript source code. 
Its primary development is from a team of JetBrains programmers.
Kotlin was named Language of the Month in the January 2012 issue of 
Dr. Dobb's Journal. While not syntax compatible with Java, Kotlin is
designed to interoperate with Java code and is reliant on Java code 
from the existing Java Class Library, such as the collections framework.

You can find more info about it in [its website](https://kotlinlang.org/)