package io.pravega.example.writers;

import io.pravega.client.admin.StreamManager;
import io.pravega.client.stream.StreamConfiguration;

import java.net.URI;

public class Utils {
    /**
     * Creates a Pravega stream with a given configuration.
     *
     * @param scope the Pravega configuration.
     * @param streamName the stream name (qualified or unqualified).
     * @param controllerURI the stream configuration (scaling policy, retention policy).
     */
    public static void createStream(String scope, String streamName, URI controllerURI) {
        boolean result = false;

	    try(StreamManager streamManager = StreamManager.create(controllerURI)) {
	        //System.out.println("CREATE STREAM MANAGER");
            result = streamManager.createScope(scope);
            System.out.println("DONE: " + scope + " has been created");
            result = streamManager.createStream(scope, streamName, StreamConfiguration.builder().build());
            System.out.println("DONE: " + scope + "/" + streamName + " has been created");
	    }
	    catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException(e);
        }
    }
}
