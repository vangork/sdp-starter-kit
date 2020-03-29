# Calculator
Process the sensor data with flink, calculate the average distance in every 3 seconds and sink to influxdb. 

### arguments:

_pravega_scope_: default is "distance-calculator"
_pravega_stream_: default is "distance-calculator-stream"
_pravega_controller_uri_: default is "tcp://127.0.0.1:9090"

_influxdb_url_: default is "http://127.0.0.1:8086"
_influxdb_username_: default is ""
_influxdb_password_: default is ""

_influxdb_DbName_: default is "distance_calculator"