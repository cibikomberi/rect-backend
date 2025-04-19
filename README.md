# Rect - IoT Device Management Backend

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Technologies Used](#technologies-used)
- [Implementation Details](#implementation-details)
- [Frontend Repository](#frontend-repository)
- [Contact](#contact)

## Overview
Rect is the backend service for an IoT device management platform that enables users to control and monitor ESP32 and ESP8266 devices via MQTT. The backend is built with **Java Spring Boot** and provides secure communication and real-time updates using **WebSocket** and **MQTT**.

## Features
- **Device Management**: Register, control, and monitor IoT devices.
- **Real-time Communication**: Utilizes MQTT for device interaction and WebSocket for instant updates.
- **Customizable Dashboard**: Supports secure device sharing and collaborative monitoring.
- **Authentication & Security**: Implements **Spring Security** with **JWT-based authentication**.
- **Database Management**: Uses **MongoDB** for efficient storage and retrieval of user and device data.

## Technologies Used
- **Java Spring Boot** – REST API development
- **Spring Security** – Authentication & Authorization
- **JWT (JSON Web Tokens)** – Secure user authentication
- **MongoDB** – NoSQL database for storing user and device data
- **Spring Data JPA** – ORM for database interaction
- **MQTT** – Device communication protocol
- **WebSocket** – Real-time updates for the frontend

## Implementation Details

### **Spring Boot & REST APIs**
- Built with **Spring Boot**, following a layered architecture (**Controller-Service-Repository**) for maintainability.
- Uses **Spring Data JPA** to interact with **MongoDB**, ensuring smooth data persistence.

### **Authentication & Authorization**
- Implements **Spring Security** with **JWT authentication**, securing user logins.

### **Device Communication & Real-Time Updates**
1. **MQTT Communication**: ESP32 and ESP8266 devices communicate with the backend via an MQTT broker.
2. **Backend Processing**: The backend processes MQTT messages and updates the database accordingly.
3. **WebSocket Integration**: The backend pushes real-time updates to the frontend via WebSockets.

### **Database Management (MongoDB)**
- Stores **users, devices, and device data** in a structured manner.
- Uses **Spring Data JPA** for querying and persistence.

## Frontend Repository
The frontend for this project is built using **React.js** with **IBM’s Carbon Design System** for a modern and responsive UI.

🔗 **Frontend Repository:** [https://github.com/cibikomberi/rect-frontend](https://github.com/cibikomberi/rect-frontend)

## PlatformIO Library
A dedicated **PlatformIO Library** is available for ESP32/ESP8266 devices to communicate with the Rect backend.

🔗 **PlatformIO Library:** [https://github.com/cibikomberi/Rect](https://github.com/cibikomberi/Rect)
## Contact
For any queries or support, please contact [cibikomberi@gmail.com](mailto:cibikomberi@gmail.com).

---
*Thank you for using Rect Backend!* 🚀
