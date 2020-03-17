package com.dellemc.appdev.starterkit;

import java.io.Serializable;
import java.sql.Timestamp;

public class ServerStatus implements Serializable {

    private static final long serialVersionUID = 1L;
    public String server;
    public String timestamp;
    public String load;

    public ServerStatus(){}

    public ServerStatus(String server, String timestamp, String load) {
        this.server = server;
        this.timestamp = timestamp;
        this.load = load;
    }

    public ServerStatus(String server, Long timestamp, double load) {
        this.server = server;
        this.timestamp = Long.toString(timestamp / 1000);
        this.load = Double.toString(load);
    }

    @Override
    public String toString() {
        return String.format("%s %s load(%s)", new Timestamp(getTimestamp()), server, load);
    } 

    long getTimestamp() {
        // Minisecond
        return Long.parseLong(timestamp) * 1000;
    }

    public String getKey() {
        return server;
    }
}
