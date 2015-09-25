./kill_1099.sh
rm -rf ~/testdb
javac simpledb/buffer/*.java
javac simpledb/server/*.java
java simpledb.server.Startup testdb $1
