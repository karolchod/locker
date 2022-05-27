# locker
Interactive locker to store/transfer parcels.

## General info
Goal of this project is to implement a system of interactive boxes for storing and transfer parcels. The center of the system is a databaseand a server application, which provides API. Client application is an mobile application, which allows users to create user accounts, send parcels and receive them. Access to the boxes is controlled by a microcontroller that wirelessly communicates with the server via the Internet.

Project was started as part of my BSc thesis at Warsaw University of Technology (graduated in February 2022).

## In progress
Plan for further development is client and administrator web app.

## Technologies
Database:
* PostgreSQL 14

Server
* Java 11
* Spring Web, Security, JDBC, JWT
* Lombok

Mobile app
* Kotlin

Web app
* Angular

Microcontroller
* ESP8266
