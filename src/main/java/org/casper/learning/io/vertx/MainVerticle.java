package org.casper.learning.io.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    startFuture.complete();
  }

  private Future<Void> prepareDatabase() {
    Future<Void> future = Future.future();
    // (...)
    return future;
  }

  private Future<Void> startHttpServer() {
    Future<Void> future = Future.future();
    // (...)
    return future;
  }
}