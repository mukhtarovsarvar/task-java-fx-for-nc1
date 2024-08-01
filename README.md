# JavaFX Application Setup and Running Guide

## Overview

This project is a JavaFX application that requires Java 17 or newer. Follow these steps to set up and run the
application.

## Prerequisites

1. **Java Development Kit (JDK)**  
   Ensure JDK 17 or newer is
   installed. [Download JDK](https://www.oracle.com/java/technologies/downloads/#java17)

2. **Apache Maven**  
   Maven is used for managing dependencies and building the
   project. [Install Maven](https://maven.apache.org/install.html)

3. **IDE (Optional)**  
   Use an IDE such as IntelliJ IDEA, Eclipse, or Visual Studio Code for easier project management.

## Run Instructions

### 1. Clone the Repository

```bash
git clone https://github.com/mukhtarovsarvar/task-java-fx-for-nc1.git
cd task-java-fx-for-nc1
```

### 2. MySQL

Change your mysql server information

```properties
spring.datasource.url=jdbc:mysql://host:port/database
spring.datasource.username=username
spring.datasource.password=password
```

Also I have free mysql server if you want to use.

```properties
spring.datasource.url=jdbc:mysql://sql.freedb.tech:3306/freedb_newsdb
spring.datasource.username=freedb_news_user
spring.datasource.password=&*6vWjPV@*sj8s8
```

### 3. Run Application

```bash
mvn clean javafx:run 
```

**Contact**:  ``mukhtarovsarvarbek@gmail.com``