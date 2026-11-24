# Survey Management System 📊

A distributed application for survey management, developed as part of the Computer Engineering degree at the University of A Coruña (UDC).

The project focuses on distributed systems integration through REST web services and Apache Thrift.

## 🎓 Academic Context

- **University:** University of A Coruña (UDC)
- **Degree:** Computer Engineering
- **Specialization:** Software Engineering
- **Course:** Distributed Systems Integration (Integración de Sistemas Distribuidos)
- **Academic year:** 2025/2026
- **Project type:** Collaborative university project

## 🛠️ Tech Stack

| **Category** | **Technologies** |
|---|---|
| Programming Language | Java |
| Web Services | REST, Apache Thrift |
| Web Servers | Eclipse Jetty, Apache Tomcat |
| Database | SQL |
| Build Tools | Maven |
| Version Control | Git, GitHub |

## 🏗️ Project Architecture

The application follows a client-server architecture and is organized into separate modules.

- **Service (`ws-app-service`):** Server-side application that exposes remote services.
- **Client (`ws-app-client`):** Client application with configurable REST and Apache Thrift implementations.
- **Database:** Persistent application data storage.

The client can be configured to use different communication implementations and server endpoints.

## 🚀 Getting Started

Follow the instructions below to configure and run the application in your local development environment.

### Development Environment 📋

Follow the official development environment installation instructions provided by UDC:

- [Instructions for Unix-like operating systems (Spanish)](https://github.com/udc-fic-isd/isd-entorno/blob/main/LEEME_UNIX.md).
- [Instructions for Windows (Spanish)](https://github.com/udc-fic-isd/isd-entorno/blob/main/LEEME_WINDOWS.md).

### Installation 🔧

**1. Clone the repository**

```bash
git clone <repository-url>
cd <repository-name>
```

**2. Initialize the database and build the project**

```bash
mvn sql:execute install
```

### Running the Service

The database server must be running before starting the application.

**Option 1: Running the service with Maven and Jetty**

```bash
cd ws-app-service
mvn jetty:run
```

**Option 2: Running the service with Apache Tomcat**

Copy the generated WAR file (`ws-app-service/target/ws-app-service.war`) to Tomcat's `webapps` directory.

Start Tomcat:

```bash
cd <TOMCAT_HOME>/bin
startup.sh
```

Stop Tomcat:

```bash
shutdown.sh
```

On Windows, use the corresponding `startup.bat` and `shutdown.bat` scripts.

### Running the Client Application

Configure the following file:

```text
ws-app-client/src/main/resources/ConfigurationParameters.properties
```

Specify the client implementation (REST or Thrift) and configure the `endpointAddress` property according to the selected web server.

| **Server** | **Port** |
|---|---|
| Jetty | 9090 |
| Tomcat | 8080 |

Navigate to the client module:

```bash
cd ws-app-client
```

Execute the client application:

```bash
mvn exec:java -Dexec.mainClass="..." -Dexec.args="..."
```

Replace the placeholders with the corresponding main class and operation arguments.

## 👥 Contributors

This project was developed collaboratively as part of the Distributed Systems Integration course at UDC.

## 🙏 Acknowledgements

This project is based on the development framework and teaching materials provided by the University of A Coruña (UDC).

## 📚 Disclaimer

This repository is intended for educational and portfolio purposes. It was developed as part of a university assignment and is not intended for production use.
