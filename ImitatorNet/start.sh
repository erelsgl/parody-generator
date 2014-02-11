MEMORY=800M
HOST=localhost
PORT=9001
export MAVEN_OPTS="-Xmx$MEMORY"
nohup mvn exec:java -Dexec.mainClass=imitatornet.ImitatorServer -Dexec.args="$HOST $PORT client" &
