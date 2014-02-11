MEMORY=800M
HOST=`hostname -I`
PORT=9002
export MAVEN_OPTS="-Xmx$MEMORY"
cd `dirname $0`
mvn exec:java -Dexec.mainClass=imitatornet.ImitatorServer -Dexec.args="$HOST $PORT client" 
