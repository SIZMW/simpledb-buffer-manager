Project 1: Better Buffer Manager For SimpleDB
====================================

Authors:
* Aditya Nivarthi (anivarthi)
* Lambert Wang (lwang5)

Purpose:
* This program is used to demonstrate more efficient replacement policies for a buffer manager.
* This program is used to demonstrate more efficient data structures within a buffer for a database.

Notes:
* All modifications or creations have the "CS 4432 Project 1" tag in the comments by the changes.
* Newly created class files have the tag at the top of the class only.

Source Files Created:
* simpledb/buffer/AbstractBufferManager.java
* simpledb/buffer/LRUBufferManager.java
* simpledb/buffer/ClockBufferManager.java
* simpledb/buffer/LRUBuffer.java
* simpledb/buffer/ClockBuffer.java
* sqlclient/main/ExecuteSimpleDBSQL.java

Source Files Modified:
* simpledb/buffer/BasicBufferMgr.java
* simpledb/buffer/Buffer.java
* simpledb/server/SimpleDB.java
* simpledb/server/Startup.java

Usage (Eclipse Project):
* Extract the zip file to get the project folder.
* Import this project folder into Eclipse.

To start the server, run the class `simpleDB.server.Startup` class with a run configuration, giving these arguments:
* To run clock replacement policy:
```
cs4432db -clock
```
* To run LRU replacement policy:
```
cs4432db -lru
```
* To run the test queries file, run the class `sqlclient.main.ExecuteSimpleDBSQL.java`
  * Run this class with a run configuration, giving these arguments:
```
"<FILE_PATH_TO_PROJECT>/src/sqlclient/sqlqueries/test.sql"
```
where `<FILE_PATH_TO_PROJECT>` is the full path to where the zip file project was extracted.

Usage (Command Line):
* Extract the zip file to get the project folder.
* Compile the simplebd/server/Startup.java
  * For example, call the following commands:
  ```
  $ cd simpledb/server
  $ javac Startup.java
  ```
* To run 'StartUp.class' with the proper arguments
  * For example, call the following commands from the outside directory containing /simpledb:
  ```
  $ java simpledb.server.Startup {-lru|-clock}  
  ```  
  
Running examples.sql on the database:
* Compile sqlclient/main/ExecuteSimpleDBSQL.java
  * For example, call the following commands:
  ```
  $ cd sqlclient/main
  $ javac ExecuteSimpleDBSQL.java
  ```
* Run ExecuteSimpleDBSQL with examples.sql
  * From the same directory, call the following command:
  ```
  $ java ExecuteSimpleDBSQL {path to examples.sql}
  ```


Directory:
* readme.md  
The file you are reading. Contains information about the project.
* bugs.txt  
A list of known bugs and issues with our implementation.
* testing.txt  
Documentation of the testing we did on our database
* design.txt  
A file containing the design description of out buffer manager.
* examples.sql  
A sql that we ran on our databse for testing creation and querying our database.
* ExtendedSimpleDB.zip  
Standalone source code for our extension of simpleDB
  * simpledb\  
  Contains the packages for simpledb
    * buffer\  
    Location of our extended code
    * server\  
    Location of our extended code
  * sqlclient\  
  A client program we used to query our database
    * main\  
    Location containing the sql client.
