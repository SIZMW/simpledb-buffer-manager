 kill -9 $(lsof -n -i:1099 | grep java | awk '{ print $2 }')
