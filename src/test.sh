
javac simpledb/buffer/*.java
javac simpledb/server/*.java

rm cs4431_basic.log
rm cs4431_lru.log
rm cs4431_clock.log
rm sql.log

./kill_1099.sh

rm -rf ~/testdb

java simpledb.server.Startup testdb &

javac sqlclient/main/ExecuteSimpleDBSQL.java
echo "Basic buffer manager" >> sql.log
java sqlclient.main.ExecuteSimpleDBSQL sqlclient/sqlqueries/examples.sql >> sql.log

./kill_1099.sh

rm -rf ~/testdb

java simpledb.server.Startup testdb -lru &

javac sqlclient/main/ExecuteSimpleDBSQL.java
echo "LRU buffer manager" >> sql.log
java sqlclient.main.ExecuteSimpleDBSQL sqlclient/sqlqueries/examples.sql >> sql.log

./kill_1099.sh

rm -rf ~/testdb

java simpledb.server.Startup testdb -clock &

javac sqlclient/main/ExecuteSimpleDBSQL.java
echo "Clock buffer manager" >> sql.log
java sqlclient.main.ExecuteSimpleDBSQL sqlclient/sqlqueries/examples.sql >> sql.log

./kill_1099.sh
