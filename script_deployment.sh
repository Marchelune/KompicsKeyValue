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
echo "keyvaluestore.self.ranks = 1;$ip:45671!2;$ip:45672!3;$ip:45673!4;$ip:45674!5;$ip:45675!6;$ip:45676" >> reference.conf
echo "keyvaluestore.self.ranges = 0:999;$ip:45671,$ip:45672,$ip:45673!1000:2000;$ip:45674,$ip:45675,$ip:45676" >> reference.conf
echo "keyvaluestore.epfd.initDelay = 3000" >> reference.conf
echo "keyvaluestore.epfd.deltaDelay = 800" >> reference.conf
cd ../../../../../
done



