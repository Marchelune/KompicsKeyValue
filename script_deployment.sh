echo "Creating Node configuration"
rm -r temp
mkdir temp
echo "How many nodes ?"
read nodesNumber
echo "Give me your Ip"
read ip

for (( i=1; i<=$nodesNumber; i++ ))
do
echo $i
cp -r KeyValueStore/ temp/
cd temp
mv KeyValueStore node$i
pwd
cd node$i/src/main/resources
rm reference.conf
port=$(($i + 45670))
echo "keyvaluestore.self.addr = $ip:$port" >> reference.conf
echo "keyvaluestore.self.rank = $i" >> reference.conf
echo "keyvaluestore.self.ranks = 1;$ip:45671!2;$ip:45672" >> reference.conf
echo "keyvaluestore.self.ranges = 20:30;$ip:45671,$ip:45672!50:60;$ip:45671,$ip:45672" >> reference.conf
echo "initDelay = 3000" >> reference.conf
echo "deltaDelay = 800" >> reference.conf
cd ../../../../../
done



