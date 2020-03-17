package com.dellemc.appdev.starterkit;

import java.net.URI;

import io.pravega.client.admin.StreamManager;
import io.pravega.client.stream.StreamConfiguration;
//import io.pravega.client.stream.ScalingPolicy;
import io.pravega.client.ClientConfig;
import io.pravega.client.EventStreamClientFactory;
import io.pravega.client.stream.impl.JavaSerializer;

import io.pravega.client.stream.EventStreamWriter;
import io.pravega.client.stream.EventWriterConfig;
//import java.util.concurrent.CompletableFuture;

import io.pravega.client.stream.ReaderGroupConfig;
import io.pravega.client.stream.Stream;
import io.pravega.client.admin.ReaderGroupManager;
import io.pravega.client.stream.EventStreamReader;
import io.pravega.client.stream.ReaderConfig;
import io.pravega.client.stream.EventRead;
import io.pravega.client.stream.ReinitializationRequiredException;
import io.pravega.client.stream.TruncatedDataException;

public class PravegaClient
{
    private URI controllerUri;
    private String scope;
    private String stream;

    public PravegaClient(URI controllerUri, String scope, String stream) {
        this.controllerUri = controllerUri;
        this.scope = scope;
        this.stream = stream;
    }

    public void init() {
        try(StreamManager streamManager = StreamManager.create(controllerUri)) {
            System.out.printf("[Info] [pravega] Connected to '%s'\n", controllerUri);
            final boolean scopeCreation = streamManager.createScope(scope);
            if (scopeCreation) {
                System.out.printf("[Info] [pravega] Scope '%s' is created\n", scope);
            }
            else {
                System.out.printf("[Info] [pravega] Scope '%s' exists already\n", scope);
            }
            StreamConfiguration streamConfig = StreamConfiguration.builder()
                                                                //.scalingPolicy(ScalingPolicy.fixed(1))
                                                                  .build();
            final boolean streamCreation = streamManager.createStream(scope, stream, streamConfig);
            if (streamCreation) {
                System.out.printf("[Info] [pravega] Stream '%s' is created\n", stream);
            }
            else {
                System.out.printf("[Info] [pravega] Stream '%s' exists already\n", stream);
            }
        }
    }

    public void write(String routingKey, ServerStatus event) {
        ClientConfig clientConfig = ClientConfig.builder()
                                                .controllerURI(controllerUri)
                                                .build();
        try (EventStreamClientFactory clientFactory = EventStreamClientFactory.withScope(scope, clientConfig)) {
            EventStreamWriter<ServerStatus> writer = clientFactory.createEventWriter(stream, 
                    new JavaSerializer<ServerStatus>(),
                    EventWriterConfig.builder().build());
            System.out.format("[Info] [pravega] Writing event '%s' with routing key '%s'\n", 
                    event, routingKey);
            
            writer.writeEvent(routingKey, event);
            //final CompletableFuture<Void> writeFuture = writer.writeEvent(routingKey, event);
            //writeFuture.get();
        }
        catch (Exception e) {
            System.out.println("[Error] [pravega] Failed to write event. Error: " + e.toString());
            throw e;
		}
    }

    public void createReaderGroup(String readerGroup) {
        final ReaderGroupConfig readerGroupConfig = ReaderGroupConfig.builder()
                                                                     .stream(Stream.of(scope, stream))
                                                                     .build();
        try (ReaderGroupManager readerGroupManager = ReaderGroupManager.withScope(scope, controllerUri))
        {
            readerGroupManager.createReaderGroup(readerGroup, readerGroupConfig);
        }
    }

    public void read(String readerGroup, String readerId) {
        ClientConfig clientConfig = ClientConfig.builder()
                                                .controllerURI(controllerUri)
                                                .build();
        try (EventStreamClientFactory clientFactory = EventStreamClientFactory.withScope(scope, clientConfig);
            EventStreamReader<ServerStatus> reader = clientFactory.createReader(readerId,
                                                                            readerGroup,
                                                                            new JavaSerializer<ServerStatus>(),
                                                                            ReaderConfig.builder().build())) {
            System.out.printf("[Info] [pravega] Reading events from controller '%s' scope '%s' stream '%s'\n", 
                    controllerUri, scope, stream);
            EventRead<ServerStatus> event = null;
            do {
                try {
                    // timeout of readNextEvent(long timeout) is in ms
                    final long READER_TIMEOUT_MS = 2000;
                    event = reader.readNextEvent(READER_TIMEOUT_MS);
                    if (event.getEvent() != null) {
                        System.out.printf("[Info] [pravega] Read event '%s'\n", event.getEvent());
                    }
                } catch (ReinitializationRequiredException | TruncatedDataException e) {
                    //There are certain circumstances where the reader needs to be reinitialized
                    System.out.println("[Error] [pravega] Failed to read event. Error: " + e.toString());
                    e.printStackTrace();
                }
            } while (event.getEvent() != null);
            System.out.printf("[Info] [pravega] No more events from topic '%s' steam '%s'\n", scope, stream);
        }
    }
}