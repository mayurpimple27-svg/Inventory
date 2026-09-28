# Product API

This is a Spring Boot application that implements a RESTful API for managing products.

## Endpoints

### Get all products
```
GET /products
```

### Get a product by ID
```
GET /products/{id}
```

### Create a new product
```
POST /products
```
Request body:
```json
{
  "name": "Product Name",
  "description": "Product Description",
  "price": 99.99
}
```

### Update a product
```
PUT /products/{id}
```
Request body:
```json
{
  "name": "Updated Product Name",
  "description": "Updated Product Description",
  "price": 199.99
}
```

### Delete a product
```
DELETE /products/{id}
```

## Technologies Used

- Spring Boot 3.1.0
- Spring Web
- Spring Data JPA
- H2 Database
- Java 17

## How to Run

1. Make sure you have Java 17 and Maven installed
2. Navigate to the project root directory
3. Run the following command:
   ```
   mvn spring-boot:run
   ```
4. The application will start on port 8080

## Database

This application uses an in-memory H2 database which is created fresh each time the application starts.
You can access the H2 console at http://localhost:8080/h2-console
- JDBC URL: jdbc:h2:mem:testdb
- Username: sa
- Password: password