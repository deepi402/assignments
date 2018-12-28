Follow below steps to download, compile and run serviceplan-price-service application.

1) git clone https://github.com/deepi402/assignments.git
2) cd serviceplan-price-service
3) mvn clean package spring-boot:run -> to launch application
4) http://localhost:8080/h2-console -> to connect to and browse in-memory H2 database
5) http://localhost:8080/v1/price -> to see all preloaded price information in system
6) http://localhost:8080/swagger-ui.html -> to use price REST APIs
7) Run com.serviceplan.price.service.impl.PriceServiceImplTest.java for Junit test for various service methods.
