package com.dellemc.appdev.starterkit;

import java.net.URI;
import java.sql.Timestamp;   
import io.pravega.connectors.flink.FlinkPravegaReader;
import io.pravega.connectors.flink.PravegaConfig;
import io.pravega.connectors.flink.serialization.PravegaSerialization;
import io.pravega.client.stream.Stream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.tuple.Tuple2;

public class Flink {
    public static void main( String[] args )
    {
        // turn off logger
        org.apache.log4j.BasicConfigurator.configure();
        org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.OFF);
        
        // Pravega Param
        URI pravegaControllerUri = Parameters.getPravegaUri();
        String pravegaScope = Parameters.getPravegaScope();
        String pravegaStream = Parameters.getPravegaStream();
        //String pravegaReaderGroup = Parameters.getPravegaReaderGroup();
        //String pravegaReaderId = Parameters.getPravegaReaderId();

        //InfluxDb Param
        String influxDbUri = Parameters.getInfluxDbUri();
        String influxDbUser =  Parameters.getInfluxDbUser();
        String influxDbPassword = Parameters.getInfluxDbPassword();
        String influxDbDatebase = Parameters.getInfluxDbDatabase();
        
        try {
            //PravegaClient pravegaClient = new PravegaClient(pravegaControllerUri, pravegaScope, pravegaStream);
            //pravegaClient.createReaderGroup(pravegaReaderGroup);
            //pravegaClient.read(pravegaReaderGroup, pravegaReaderId);

            PravegaConfig pravegaConfig = PravegaConfig.fromDefaults()
                .withControllerURI(pravegaControllerUri)
                .withDefaultScope(pravegaScope)
                //.withCredentials(credentials)
                .withHostnameValidation(false);

            Stream stream = pravegaConfig.resolve(pravegaStream);
            FlinkPravegaReader<ServerStatus> source = FlinkPravegaReader.<ServerStatus>builder()
                .withPravegaConfig(pravegaConfig)
                .forStream(stream)
                .withDeserializationSchema(PravegaSerialization.deserializationFor(ServerStatus.class))
                .build();

            // initialize the Flink execution environment
            final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
            env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime); 
            
            DataStream<ServerStatus> dataStream = env.addSource(source)
                .name(pravegaStream)
                .assignTimestampsAndWatermarks(new AssignTimestampAndWatermarks())
                // .keyBy(
                //     new KeySelector<ServerStatus, String>() {
                //         static final long serialVersionUID = 0;
                //         @Override
                //         public String getKey(ServerStatus ele) throws Exception {
                //             return ele.server;
                //         }
                //     }
                // )
                .keyBy((ServerStatus x) -> x.getKey())
                .timeWindow(Time.milliseconds(10000)) // 10 seconds
                .aggregate(new AverageAggregate());

            // create an output sink to print to stdout for verification
            dataStream.addSink(new InfluxdbSink(influxDbUri, influxDbUser, influxDbPassword, influxDbDatebase));
            dataStream.print();

            // execute within the Flink environment
            env.execute("Flink");
        }
        catch(Exception e) {
			// Display full details of any exception that occurs
			System.out.println("msg " + e.getMessage());
			// System.out.println("loc " + e.getLocalizedMessage());
			// System.out.println("cause " + e.getCause());
			// System.out.println("excep " + e);
			e.printStackTrace();
		}
    }

    private static class AssignTimestampAndWatermarks implements AssignerWithPeriodicWatermarks<ServerStatus> {
        static final long serialVersionUID = 0;
        private final long maxOutOfOrderness = 5000; // 5 seconds

        private long currentMaxTimestamp = 0L;

        @Override
        public long extractTimestamp(ServerStatus ele, long previousElementTimestamp) {
            long timestamp = ele.getTimestamp();
            currentMaxTimestamp = Math.max(timestamp, currentMaxTimestamp);
            //System.out.format("[Info] [flink] %s handled event '%s'\n", new Timestamp(System.currentTimeMillis()), ele);
            return timestamp;
        }

        @Override
        public Watermark getCurrentWatermark() {
            // return the watermark as current highest timestamp minus the out-of-orderness bound
            return new Watermark(currentMaxTimestamp - maxOutOfOrderness);
        }
    }

    // Tuple3<Double, Long, Tuple2<String, Long>>
    // f0: sum
    // f1: count
    // f2.f0 server
    // f2.f1 timestamp
    private static class AverageAggregate implements AggregateFunction<ServerStatus, Tuple3<Double, Long, Tuple2<String, Long>>, ServerStatus> {
        static final long serialVersionUID = 0;

        @Override
        public Tuple3<Double, Long, Tuple2<String, Long>> createAccumulator() {
            return new Tuple3<>(0.0, 0L, new Tuple2<>("", 0L));
        }

        @Override
        public Tuple3<Double, Long, Tuple2<String, Long>> add(ServerStatus ele, Tuple3<Double, Long, Tuple2<String, Long>> acc) {
            double load1Min = Double.parseDouble(ele.load.split(" ")[0]);
            return new Tuple3<>(acc.f0 + load1Min, acc.f1 + 1L, new Tuple2<>(ele.server, ele.getTimestamp() > acc.f2.f1 ? ele.getTimestamp() : acc.f2.f1 ));
        }

        @Override
        public ServerStatus getResult(Tuple3<Double, Long, Tuple2<String, Long>> acc) { 
            ServerStatus ss = new ServerStatus(acc.f2.f0, acc.f2.f1, acc.f0 / acc.f1);
            System.out.format("[Info] [flink] %s generate event '%s'\n", new Timestamp(System.currentTimeMillis()), ss);
            return ss;
        }

        @Override
        public Tuple3<Double, Long, Tuple2<String, Long>> merge(Tuple3<Double, Long, Tuple2<String, Long>> a, Tuple3<Double, Long, Tuple2<String, Long>> b) {
            return new Tuple3<>(a.f0 + b.f0, a.f1 + b.f1, new Tuple2<>(a.f2.f0, a.f2.f1 > b.f2.f1 ? a.f2.f1 : b.f2.f1));
        }
    }
}