This project contains 4 files:

1.	hw3createdb.sql creates the database schema.
2.	hw3dropdb.sql drops all indexes and all tables. 

Steps to run the project:
1.	Make sure the project has odbc (ojdbc6.jar) and json (java-json.jar) jars added in the Java build path.
2.	Run hw3createdb.sql on your database.
3.	Open populate.java, check for database connection details to make sure it has your database information.
4.	Open populate.java to populate all the tables with data from json files.
5.	Run hw3.java to run the app.


Assumptions made:
1. Bottom level drop-down fields will only populate if a user selects attributes. 
