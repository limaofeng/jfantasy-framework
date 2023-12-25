package net.asany.jfantasy.graphql.publishers;

import java.util.function.Consumer;
import java.util.function.Predicate;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

/**
 * 基础发布者
 *
 * @param <D> 数据类型
 * @author limaofeng
 */
@Slf4j
public class BasePublisher<D> {

  /** 消费者 */
  private final FluxConsumer<D> consumer;

  /** 发布者 */
  private final Flux<D> publisher;

  public BasePublisher() {
    this.consumer = new FluxConsumer<>();
    Flux<D> flux = Flux.create(this.consumer, FluxSink.OverflowStrategy.BUFFER);
    ConnectableFlux<D> connectableFlux = flux.share().publish();
    connectableFlux.connect();
    publisher = Flux.from(connectableFlux);
  }

  /**
   * 发布数据
   *
   * @param d 数据
   */
  public void emit(D d) {
    consumer.next(d);
  }

  /**
   * 获取发布者
   *
   * @return Flux 发布者
   */
  public Flux<D> getPublisher() {
    return publisher;
  }

  /**
   * 获取发布者
   *
   * @param predicate 过滤器
   * @return Flux 发布者
   */
  public Flux<D> getPublisher(Predicate<D> predicate) {
    return publisher.filter(predicate);
  }

  private static class FluxConsumer<T> implements Consumer<FluxSink<T>> {

    private FluxSink<T> emitter;

    @Override
    public void accept(FluxSink emitter) {
      this.emitter = emitter;
    }

    public void next(T o) {
      this.emitter.next(o);
    }
  }
}
