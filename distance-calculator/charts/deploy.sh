#!/bin/bash
sed 's|$name|'"$1"'|' calculator/values.yaml.orig > calculator/values.yaml
sed 's|$name|'"$1"'|' mqttwriter/values.yaml.orig > mqttwriter/values.yaml

data_source_name=""
IFS='-'
read -ra ADDR <<<$1
for i in "${ADDR[@]}";
do
	data_source_name="${data_source_name}${i}_"
done

data_source_name="${data_source_name}flnk"

sed 's|$data_source|'"$data_source_name"'|' calculator/templates/grafana_dashboard.yaml.orig > calculator/templates/grafana_dashboard.yaml

helm install features features/
helm install calculator calculator/

fp_state=$(kubectl -n $1 get flinkapplication $1 --output="jsonpath={.status.state}")

while [[ "$fp_state" != 'started' ]]
do
	echo "Flink application not ready. Sleeping for 5seconds"
	sleep 5;
	fp_state=$(kubectl -n $1 get flinkapplication $1 --output="jsonpath={.status.state}")
done

helm install mqttwriter mqttwriter/
