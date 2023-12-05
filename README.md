# Conference room reservation service


http://localhost:8080/swagger-ui/index.html#/room-controller/getRooms


### Table of Contents
- [Introduction](#introduction)
- [Prerequisites](#prerequisites)
- [Technical Specifications](#technical-specifications)
- [Local Run](#local-run)
- [Testing h2](#testing-h2)
- [Mind map](#mind-map)
- [Sequence Diagram](#sequence-diagram)


## Introduction

 - Application to reserve room based on the availability

## Prerequisites
 - Docker installed
 - git to pull the code

## Technical Specifications
 - Springboot framework version 3.2.0
 - Java 17
 - Redis for caching
 - H2 for database
 - Docker for containerization
 - Spring scheduler for scheduling daily operations


## Local Run

 - Checkout the code from github master branch
 - Run docker compose up --build 
 - Run docker compose down to down the application


## Testing h2
 - http://localhost:8080/h2-console/login.do
 - Username: sa
 - Password: password


## Mind map



## Sequence Diagram
```mermaid
sequenceDiagram
    participant User
    participant BookingService
    participant ConcurrentQueue as LinkedTransferQueue
    participant ReservationRequestConsumer
    participant ReservationService
    participant RoomService
    participant SchedulerService
    participant RoomReservationBinarySearchTree

    
    
    User->>+BookingService: POST /v1/bookings
        BookingService->>ConcurrentQueue: Push the CreateBookingDTO to the queue
    BookingService->>-User: Acknowledgement with id 200 OK status
    ConcurrentQueue-->>+ReservationRequestConsumer: The consumer is a runnable task which reads the BookingDTO from the LinkedTransferqueue
    ReservationRequestConsumer-->>+ReservationService: Calls the reserve function
        ReservationService-->>SchedulerService: Check if there are overlapping maintenance schedule
        ReservationService-->>RoomService: Check the tentative rooms based on the no of persons and the seating capactity of the room
        ReservationService-->>RoomReservationBinarySearchTree: check if reservation can be done for the given time and the duration
        ReservationService-->>ReservationService: Persist reservation in database
    ReservationService-->>-ReservationRequestConsumer: success
    ReservationRequestConsumer-->>-ConcurrentQueue: The consumer thread runs indefinitely consuming the requests
    
```


