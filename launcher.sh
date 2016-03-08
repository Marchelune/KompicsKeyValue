cd ./temp/
nodesNumber=$(ls -1 | wc -l)
echo "Launching $nodesNumber nodes ..."

for (( i=1; i<=$nodesNumber ; i++ ))
do
cd node$i
java -Dconfig.file=./reference.conf -jar KeyValueStore-0.0.1-SNAPSHOT-fat.jar &
process_ids[$i]=$!
cd ..
done

echo "########## OK ###########"
echo "###press a key to quit###"
read -n1 -s key

for ((  i = 1 ;  i <= $nodesNumber;  i++  ))
do
    echo "Kill PID:${process_ids[$i]}"
    kill -9 ${process_ids[$i]}
    
done
