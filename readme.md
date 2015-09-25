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
* AbstractBufferManager.java
* LRUBufferManager.java
* ClockBufferManager.java
* LRUBuffer.java
* ClockBuffer.java

Source Files Modified:
* BasicBufferMgr.java
* Buffer.java
* SimpleDB.java
* Startup.java

Usage:
* Extract the zip file to get the project folder.
* Import this project folder into Eclipse.

To start the server, run the class simpleDB.server.Startup class with a run configuration, giving these arguments:
* To run clock replacement policy:
```
cs4432db -clock
```
* To run LRU replacement policy:
```
cs4432db -lru
```
* To run the test queries file, run the class sql_client.src.main.test_simpledb_sql.java
  * Run this class with a run configuration, giving these arguments:
```
"<FILE_PATH_TO_PROJECT>/src/sql_client/src/test/sql_queries/test.sql"
```
where `<FILE_PATH_TO_PROJECT>` is the full path to where the zip file project was extracted.
