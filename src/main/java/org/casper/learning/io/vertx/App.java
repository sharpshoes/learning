package org.casper.learning.io.vertx;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;

public class App {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        HttpServer httpServer = vertx.createHttpServer();
        httpServer.requestHandler(request -> {
            HttpServerResponse response = request.response();
            response.putHeader("content-type", "text/plain");
            response.putHeader("Content-Length", "" + "Hello World.".length());
            response.write("Hello World.");
            response.end();

        });
        httpServer.listen(8080);

//        Vertx.vertx().createHttpServer().requestHandler(req -> req.response().
//        end("Hello World!")).listen(8080);
    }
} 