# Conference room reservation service


http://localhost:8080/swagger-ui/index.html#/room-controller/getRooms


### Table of Contents
- [Introduction](#introduction)
- [Prerequisites](#prerequisites)
- [Technical Specifications](#technical-specifications)
- [Local Run](#local-run)
- [Testing h2](#testing-rabbitmq)


## Introduction

 - Application to reserve room based on the availability

## Prerequisites
 - Docker installed
 - git to pull the code

## Technical Specifications
 - Springboot framework
 - Redis for caching
 - Docker for containerization
 - Spring scheduler for scheduling certain daily operations


## Local Run

 - Checkout the code from github master branch
 - Run docker compose up --build 
 - Run docker compose down to down the application


## Testing h2
 - http://localhost:8080/h2-console/login.do
 - Username: sa
 - Password: password


```mermaid
mindmap
  root((mindmap))
    Migration scripts and database creation
    Springboot Validation
    H2 database
    Logging
    Dockerization 
    Redis for caching
    Producer-Consumer in java.util.concurrent package to manage FCFS
    Data structure: Binary Search Tree for reservation system
    Domains
        Room
            Retrieve rooms
        Schedule
            Reserve schedule for maintenance
        Reservation
            Create booking
            Cancel booking
            Get all the bookings
            Get available slots
    
```


