package io.pravega.flinkapp;


import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class InfluxdbSink extends RichSinkFunction<OutSenorData> {
    InfluxDB influxDB = null;
    String influxdbUrl = "";
    String influxdbUsername = "";
    String influxdbPassword = "";
    String influxdbDbName = "";


    public InfluxdbSink() {
        this.influxdbUrl = "http://project-metrics:8086";
        this.influxdbUsername = "default";
        this.influxdbPassword = "default";
        this.influxdbDbName = "test_flnk";
    }

    @Override
    public void invoke(OutSenorData value) {
        try {
            System.out.println("value: " + value);
            influxDB.write(Point.measurement(value.getSensorid())
                    .time(value.getTimestamp(), TimeUnit.MILLISECONDS)
                    .addField("DIFFERENCE", value.getDifference())
                    .addField("TREND", value.getTrend())
                    .addField("AVERAGE", value.getAverage())
                    .build());
        } catch(Exception e) {
            System.out.println("Failed!");
            e.printStackTrace();
        }
    }

    @Override
    public void open(Configuration config) throws IOException {
        String flinkfilepath = getEnvVar("FLINK_CONF_DIR", "/etc/flink");
        flinkfilepath = flinkfilepath+"/"+"flink-conf.yaml";
        System.out.println("flink conf dir " + flinkfilepath);
            ParameterTool params = ParameterTool.fromPropertiesFile(flinkfilepath);
            influxdbDbName = params.get("metrics.reporter.influxdb.db","test_flnk");
            influxdbUsername = params.get("metrics.reporter.influxdb.username","default");
            influxdbPassword = params.get("metrics.reporter.influxdb.password","default");
            String influxdbdest = params.get("metrics.reporter.influxdb.host","project-metrics");
            String influxdbport = params.get("metrics.reporter.influxdb.port","8086");
            influxdbUrl = "http://"+influxdbdest+":"+influxdbport;

            System.out.println("influxdbDbName:" + influxdbDbName );
            System.out.println("influxdbUsername:" + influxdbUsername );
            System.out.println("influxdbPassword:" + influxdbPassword );
            System.out.println("influxdbUrl:" + influxdbUrl );

        if (influxdbUsername == null || influxdbUsername.isEmpty()) {
            influxDB = InfluxDBFactory.connect(influxdbUrl);
        }
        else {
            influxDB = InfluxDBFactory.connect(influxdbUrl, influxdbUsername, influxdbPassword);
        }
        //influxDB = InfluxDBFactory.connect("http://
        //String influxdbDbName = "demo";
        //influxDB.query(new Query("CREATE DATABASE " + influxdbDbName));
        influxDB.setDatabase(influxdbDbName);
        //influxDB.query(new Query("DROP SERIES FROM /.*/"));
    }

    @Override
    public void close() throws Exception {
        if (influxDB != null) {
            influxDB.close();
        }
    }

    private static String getEnvVar(String name, String defaultValue) {
        String value = System.getenv(name);
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }
        return value;
    }
}
