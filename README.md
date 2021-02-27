# employee-inventory
spring boot application that maintains an Inventory of Employee data

<b>How to run ? </b>

1. clone this repository
2. mvn clean install
3. mvn package
4. java -jar employee-inventory-0.0.1-SNAPSHOT.jar

(Java version used: JDK-11)


<b>High Level Design: </b>


![flow](https://user-images.githubusercontent.com/6492557/109376192-f13d3c00-78e8-11eb-9e37-2da8881aa3a3.png)


Based on the above diagram, 

1. POST - http://localhost:8085/api/employee?action=upload

   Upload API will accept a csv file contains employee data and return 202-Created response with generated taskId.
   Asynchronously, the async-file-processor will read the file and insert into the h2 database.

<  b> (Implemented Bulk Insertion in batch to redure db hit and to gain performance some logic to reduce hibernate cache size) </b>

2. GET http://localhost:8085/api/employee/progress/f359d9a059e74464909ee99a50fc7e3a

    TaskId will get generated which can be used to track the progress.

3. GET http://localhost:8085/api/employee?page=0&size=50&sort=name

   API to fetch all employee data from database. <b>Pagination supported as data are huge. </b>

4. PUT http://localhost:8085/api/employee
5. DEL http://localhost:8085/api/employee/6ad1b221-2b25-4de1-8656-69410aca2508
6. DEL http://localhost:8085/api/employee/all

<b> (POSTMAN COLLECTION and Sample Data file in root directory) </b>

<a href="EmployeeInventory.postman_collection.json" download="EmployeeInventory.postman_collection.json">Postman Collection</a>
