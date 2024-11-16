# Task Management System

## Overview

The **Task Management System** is a robust backend application designed to manage tasks efficiently. It provides a RESTful API for performing CRUD operations on tasks, user authentication and authorization, and additional features such as validation, error handling, search and filtering, and email notifications.

## Features

- **Task Management APIs**: 
  - CRUD operations for managing tasks.
  - Each task includes a title, description, status (e.g., todo, in progress, done), priority, and due date.

- **User  Authentication and Authorization**: 
  - JWT-based authentication with Spring Security.
  - Role-based access control (e.g., admin, regular user) to enforce authorization rules.

- **Validation and Error Handling**: 
  - Input validation to ensure data integrity and consistency.
  - Meaningful error messages for invalid requests or server errors.

- **Search and Filtering**: 
  - Endpoints for searching tasks based on criteria such as title, description, status, and due date.
  - Filtering options to narrow down task lists.

- **Email Notifications**: 
  - Send email notifications for upcoming task deadlines or important updates.
  - Configurable SMTP settings for email integration.

- **Pagination Support**: 
  - Efficient management of large datasets by supporting pagination in API responses.

- **Unit Testing**: 
  - Comprehensive unit tests to ensure the reliability and correctness of the application.
  - Uses JUnit and Mockito for testing service and controller layers.

- **Exception Handling**: 
  - Custom exceptions to handle various error cases gracefully.
  - Global exception handler to provide meaningful error responses.

## Technology Stack

- **Java 17**
- **Spring Boot 3.3.5**
- **Spring Data JPA**
- **Hibernate**
- **Spring Security**
- **MySQL**
- **Lombok**
- **MapStruct**
- **JUnit and Mockito for Testing**

## Project Structure

```
task-management-system/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── banquemisr/
│   │           └── challenge05/
│   │               └── taskmanagementsystem/
│   │                   ├── config
│   │                   ├── controller
│   │                   ├── domain
│   │                   │   ├── dto
│   │                   │   ├── enums
│   │                   │   ├── mapper
│   │                   │   └── entity
│   │                   ├── exception
│   │                   ├── repository
│   │                   ├── security
│   │                   ├── service
│   │                   └── util
│   │                   └── ServletInitializer.java
│   │                   └── TaskManagementSystemApplication.java
│   ├── test/
│   │   └── java/
│   │       └── banquemisr/
│   │           └── challenge05/
│   │               └── taskmanagementsystem
│   │                   ├── controller/     (Test classes for controllers)
│   │                   ├── service/        (Test classes for services)
│   │                   └── TaskManagementSystemApplicationTests.java (Main test class)
│   └── resources/      (Configuration files and other resources)
│       └── application.properties
└── pom.xml               (Project configuration file with dependencies)
```

## Database Setup

1. **Create the Database**: 
   - Create a new MySQL database named `task_management_system_db`.

2. **Schema Creation**: 
   - The application uses Spring Data JPA and Hibernate to automatically create the necessary tables based on the defined entity classes. Upon the first run, the schema will be generated.

## Getting Started

### Prerequisites

- Java 17 or higher
- MySQL Server
- Maven
- An IDE (e.g., IntelliJ IDEA, Eclipse)

### Installation Steps

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/KhaledAshrafH/task-management-system.git
   cd task-management-system
   ```

2. **Configure Database Connection:**
   Open `src/main/resources/application.properties` and configure the database settings:
   ```properties
      spring.application.name=Task Management System

      server.port=8083
      
      spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
      spring.datasource.url=jdbc:mysql://localhost:3306/task_management_system_db
      spring.datasource.username=root
      spring.datasource.password=
      
      spring.jpa.hibernate.ddl-auto=update
      spring.jpa.show-sql=true
      spring.jpa.database=mysql
      spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
      
      security.jwt.secret-key=3cfa76ef14937c1c0ea519f8fc057a80fcd04a7420 f8e8bcd0a7567c272e007b
      security.jwt.expiration-time=86400000 # a day
      
      spring.mail.host=smtp.gmail.com
      spring.mail.port=587
      spring.mail.username=hotelhuborg@gmail.com
      spring.mail.password=sols uwdt trzn frhs
      spring.mail.properties.mail.smtp.auth=true
      spring.mail.properties.mail.smtp.starttls.enable=true
   ```
3. **Build the Project:**
   ```bash
   mvn clean install
   ```

4. Run the Application:
    ```bash
    mvn spring-boot:run\
    ```

## Admin Setup

After running the application, you can manually create an admin user by using a database client or through the application’s registration endpoint. For testing purposes, you can register any user using the `/api/v1/auth/register` endpoint.

## Seed Data

The application includes seed data to cover various use cases. You can populate the database with initial tasks and users by calling the appropriate endpoints after starting the application.

## API Documentation

The API documentation is available through Swagger UI. Once the application is running, navigate to:
    ```
    http://localhost:8083/swagger-ui.html
    ```

## Endpoints Overview

### Authentication API

- **User  Registration**:
  - `POST /api/v1/auth/register`
    - **Request Body**: `RegistrationRequestDTO`
    - **Description**: Create a new user account.
    - **Response**: `AuthenticationResponseDTO` (201 Created)

- **User  Authentication**:
  - `POST /api/v1/auth/login`
    - **Request Body**: `LoginRequestDTO`
    - **Description**: Authenticate a user and return a JWT.
    - **Response**: `AuthenticationResponseDTO` (200 OK)

- **User  Logout**:
  - `POST /api/v1/auth/logout`
    - **Description**: Logout the user.
    - **Response**: No content (204 No Content)

### User API

- **Get All Users**:
  - `GET /api/v1/admin/users`
    - **Description**: Get all users (admin only).
    - **Response**: List of `User  ResponseDTO` (200 OK)

- **Get User Task History**:
  - `GET /api/v1/users/me/history`
    - **Description**: Get task history for the current user.
    - **Response**: List of `TaskHistoryResponseDTO` (200 OK)

- **Get All Notifications for Current User**:
  - `GET /api/v1/users/me/notifications`
    - **Description**: Get all notifications for the current user.
    - **Response**: List of `NotificationResponseDTO` (200 OK)

- **Get All Notifications for Specific User**:
  - `GET /api/v1/users/{userId}/notifications`
    - **Path Variable**: `userId`
    - **Description**: Get all notifications for a specific user.
    - **Response**: List of `NotificationResponseDTO` (200 OK)

- **Delete Notification**:
  - `DELETE /api/v1/users/me/notifications/{notificationId}`
    - **Path Variable**: `notificationId`
    - **Description**: Delete a notification by ID.
    - **Response**: No content (204 No Content)

- **Mark Notification as Read**:
  - `PUT /api/v1/users/me/notifications/{notificationId}`
    - **Path Variable**: `notificationId`
    - **Description**: Mark a notification as read.
    - **Response**: Message indicating success (200 OK)

### Task API

- **Create Task**:
  - `POST /api/v1/tasks`
    - **Request Body**: `TaskCreationDTO`
    - **Description**: Create a new task.
    - **Response**: `TaskResponseDTO` (201 Created)

- **Assign Task**:
  - `POST /api/v1/tasks/assign`
    - **Request Body**: `TaskCreationDTO`
    - **Description**: Assign a new task (admin only).
    - **Response**: `TaskResponseDTO` (201 Created)

- **Update Task**:
  - `PUT /api/v1/tasks/{id}`
    - **Path Variable**: `id`
    - **Request Body**: `TaskUpdateDTO`
    - **Description**: Update an existing task.
    - **Response**: `TaskResponseDTO` (200 OK)

- **Get Task by ID**:
  - `GET /api/v1/tasks/{id}`
    - **Path Variable**: `id`
    - **Description**: Retrieve a specific task by ID.
    - **Response**: `TaskResponseDTO` (200 OK)

- **Get All Tasks**:
  - `GET /api/v1/tasks`
    - **Query Parameters**: `page`, `size`
    - **Description**: Retrieve all tasks (admin only, supports pagination).
    - **Response**: List of `TaskResponseDTO` (200 OK)

- **Get All Created Tasks**:
  - `GET /api/v1/tasks/created`
    - **Query Parameters**: `page`, `size`
    - **Description**: Retrieve all tasks created by the current user (supports pagination).
    - **Response**: List of `TaskResponseDTO` (200 OK )

- **Get All Assigned Tasks**:
  - `GET /api/v1/tasks/assigned`
    - **Query Parameters**: `page`, `size`
    - **Description**: Retrieve all tasks assigned to the current user (supports pagination).
    - **Response**: List of `TaskResponseDTO` (200 OK)

- **Get All Assigned Tasks for User**:
  - `GET /api/v1/tasks/assigned/{userId}`
    - **Path Variable**: `userId`
    - **Query Parameters**: `page`, `size`
    - **Description**: Retrieve all tasks assigned to a specific user (supports pagination).
    - **Response**: List of `TaskResponseDTO` (200 OK)

- **Delete Task by ID**:
  - `DELETE /api/v1/tasks/{id}`
    - **Path Variable**: `id`
    - **Description**: Delete a task by ID.
    - **Response**: No content (204 No Content)

- **Search and Filter Tasks**:
  - `GET /api/v1/tasks/search`
    - **Query Parameters**: `title`, `desc`, `status`, `priority`, `from`, `to`
    - **Description**: Search and filter tasks based on various criteria.
    - **Response**: List of `TaskResponseDTO` (200 OK)

- **Get Task History**:
  - `GET /api/v1/tasks/{taskId}/history`
    - **Path Variable**: `taskId`
    - **Description**: Get the history of a specific task.
    - **Response**: List of `TaskHistoryResponseDTO` (200 OK)

## Conclusion

The Task Management System is a comprehensive solution for managing tasks with a focus on security, usability, and maintainability. By following the instructions above, you can set up and interact with the API effectively. The inclusion of unit tests and exception handling ensures a robust application design. For any issues or contributions, feel free to open an issue or pull request on the GitHub repository.
