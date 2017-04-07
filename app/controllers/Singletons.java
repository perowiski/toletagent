package controllers;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import play.Environment;
import play.libs.ws.WSClient;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.InetAddress;

/**
 * Created by seyi on 7/25/16.
 */

@Singleton
public class Singletons {
    public static Environment environment;
    public static WSClient ws;

    @Inject
    public Singletons(
            Environment playEnvironment,
            WSClient wsClient
    ) {
        environment = playEnvironment;
        ws = wsClient;
    }
}
