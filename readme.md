# SOAP-library
This API is just a conversion of REST-library API by Roman Shmandrovskyi: https://github.com/RomanShmandrovskyi/REST-library-API.

There are 3 tables: `Author`, `Genre` and `Book`.

One Author has many Books (for Genre too) and one Book must has one Author and one Genre.

## Getting started
1. Clone project:
```
git clone https://github.com/Pastornak/SOAP-library-API.git
```

2. Open `src/main/resources/application.properties` file and enter instead `<data_base_name>` name you want for your data base:
```
spring.datasource.dbname=<data_base_name>
```
2.1. The API will up on built-in data base H2 will be injected with data;
Queries for injected data can be found at `/src/main/resources/data.sql`;

2.2. It is also possible to up API using SQL data base. To do this, just change the last config properties to:
```
spring.datasource.url=jdbc:mysql://localhost:3306/${spring.datasource.dbname}?serverTimezone=UTC
spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL57Dialect
```

3. Load root project folder in console and run command:
```
mvn spring-boot:run
```
Or import project as Maven project and just run main method in `Application` class from `src/main/java/ua/com/epam` package;

4. WSDL will be available on:
```
http://localhost:8080/ws/library.wsdl
```

5. If you use built in H2 data base, you can reach something like SQL Workbench. To up it change `spring.h2.console.enabled` property to `true`:
```
spring.h2.console.enabled=true
```
Rebuild project. H2 console will be available on:
```
http://localhost:8080/h2-workbench
```
In opened window leave all inputs as default expect `JDBC URL`. Here you must paste `jdbc:h2:mem:<data_base_name>`. Look step `2.`
