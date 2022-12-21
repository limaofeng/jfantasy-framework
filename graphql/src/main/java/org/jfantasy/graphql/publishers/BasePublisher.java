package org.jfantasy.graphql.publishers;

import java.util.function.Consumer;
import java.util.function.Predicate;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

@Slf4j
public class BasePublisher<D> {

  private final FluxConsumer consumer;
  private final Flux<D> publisher;

  public BasePublisher() {
    this.consumer = new FluxConsumer();
    Flux<D> flux = Flux.create(this.consumer, FluxSink.OverflowStrategy.BUFFER);
    ConnectableFlux<D> connectableFlux = flux.share().publish();
    connectableFlux.connect();
    publisher = Flux.from(connectableFlux);
  }

  public void emit(D d) {
    consumer.next(d);
  }

  public Flux<D> getPublisher() {
    return publisher;
  }

  public Flux<D> getPublisher(Predicate<D> predicate) {
    return publisher.filter(predicate);
  }

  private static class FluxConsumer implements Consumer<FluxSink> {

    private FluxSink emitter;

    @Override
    public void accept(FluxSink emitter) {
      this.emitter = emitter;
    }

    public void next(Object o) {
      this.emitter.next(o);
    }
  }
}
