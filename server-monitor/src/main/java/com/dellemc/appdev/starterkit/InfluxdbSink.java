package com.dellemc.appdev.starterkit;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;

import java.util.concurrent.TimeUnit;

public class InfluxdbSink extends RichSinkFunction<ServerStatus> {
    private static final long serialVersionUID = 1L;
    
    private InfluxDB influxDB = null;
    private String uri;
    private String user;
    private String password;
    private String database;

    public InfluxdbSink(String uri, String user, String password, String database) {
        this.uri = uri;
        this.user = user;
        this.password = password;
        this.database = database;
    }
    @Override
    public void invoke(ServerStatus value) {
        try {
            // String dbName = "sharktank";
            influxDB.query(new Query("CREATE DATABASE " + database));
            influxDB.setDatabase(database);
            System.out.println("value: " + value);
            influxDB.write(Point.measurement(value.getKey())
                    .time(value.getTimestamp(), TimeUnit.MILLISECONDS)
                    .addField("VALUE", value.load)
                    .build());

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void open(Configuration config) {
        // influxDB = InfluxDBFactory.connect("http://monitoring-influxdb.default.svc.cluster.local:8086", "root", "root");
        influxDB = InfluxDBFactory.connect(uri, user, password);
    }

    @Override
    public void close() throws Exception {
        if (influxDB != null) {
            influxDB.close();
        }
    }
}
