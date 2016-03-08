echo "Creating Node configuration"
rm -r temp
mkdir temp

echo "Building project"
cd KeyValueStore 
mvn clean install
cd ../

echo "Give me the maximum key"
read maxKey
echo "How many partitions do you want ? -- a positive devider of the maximum key number will be perfect."
read partitions
echo "Replication degree ?"
read repDegree
echo "Give me your Ip"
read ip

sizePartition=$(($maxKey / $partitions))
nodesNumber=$(($partitions*repDegree))

###########################
#Preparing ranks and ranges
ranges=""
declare -a ranks
k=1
for (( i=1; i<=$partitions ; i++ ))
do
tempRanges=""
tempRanks=""
for (( j=1; j<=$repDegree ; j++ ))
do
rank=$((k))
port=$(($rank+45670))

tempRanks+="$rank;$ip:$port!"
tempRanges+="$ip:$port,"

k=$((k+1))
done
range=$(((i-1)*$sizePartition+1))":"$((i*$sizePartition))
tempRanges=${tempRanges%?}
if [ $k -lt $nodesNumber ]
then
tempRanges+="!"
fi
ranges+="$range;$tempRanges"

tempRanks=${tempRanks%?}
ranks[$i]=$tempRanks
done

echo "Creating $nodesNumber nodes in temp"

##############################################
#Creating each node and writing each conf files
k=1
for (( i=1; i<=$partitions ; i++ ))
do
for (( j=1; j<=$repDegree ; j++ ))
do
rank=$((k))
port=$(($rank + 45670))

mkdir temp/node$rank
cp KeyValueStore/target/KeyValueStore-0.0.1-SNAPSHOT-fat.jar temp/node$rank
cp individualLauncher.sh temp/node$rank
cd temp/node$rank

echo "keyvaluestore.self.addr=\"$ip:$port\"" >> reference.conf
echo "keyvaluestore.self.rank=$rank" >> reference.conf
echo "keyvaluestore.self.ranks=\"${ranks[$i]}\"" >> reference.conf
echo "keyvaluestore.self.ranges=\"$ranges\"" >> reference.conf
echo "keyvaluestore.epfd.initDelay=3000" >> reference.conf
echo "keyvaluestore.epfd.deltaDelay=800" >> reference.conf

cd ../../
k=$((k+1))
done
done

##############################################
#Configuring client
cd client/
rm reference.conf
echo "keyvaluestore.self.addr=\"$ip:44444\"" >> reference.conf
echo "keyvaluestore.kvstore.addr=\"$ip:$port\"" >> reference.conf

echo "#########Done !###########"


