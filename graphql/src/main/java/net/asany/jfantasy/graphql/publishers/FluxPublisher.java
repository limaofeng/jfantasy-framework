/*
 * Copyright (c) 2024 Asany
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.asany.net/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.asany.jfantasy.graphql.publishers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.SignalType;

/**
 * 基础发布者
 *
 * @param <D> 数据类型
 * @author limaofeng
 */
@Slf4j
public class FluxPublisher<D> implements Publisher<D> {

  /** 消费者 */
  protected final FluxConsumer<D> consumer;

  /** 发布者 -- GETTER -- 获取发布者 */
  @Getter protected final Flux<D> publisher;

  private final Map<SignalType, List<Runnable>> finalizers = new HashMap<>();

  public FluxPublisher() {
    this.consumer = new FluxConsumer<>();
    Flux<D> flux = Flux.create(this.consumer, FluxSink.OverflowStrategy.BUFFER);
    ConnectableFlux<D> connectableFlux = flux.share().publish();
    connectableFlux.connect();
    publisher =
        Flux.from(connectableFlux)
            .doFinally(
                type -> {
                  if (type == SignalType.CANCEL) {
                    if (finalizers.containsKey(SignalType.CANCEL)) {
                      finalizers.get(SignalType.CANCEL).forEach(Runnable::run);
                    }
                  }
                });
  }

  private FluxPublisher(FluxConsumer<D> consumer, Flux<D> publisher) {
    this.consumer = consumer;
    this.publisher = publisher;
  }

  /**
   * 发布数据
   *
   * @param d 数据
   */
  public void emit(D d) {
    log.info("Emitting data: {}", d);
    consumer.next(d);
  }

  public void error(Exception ex) {
    log.error("Error in flux stream", ex);
    consumer.error(ex);
  }

  public void onDispose(Runnable finalizer) {
    finalizers.computeIfAbsent(SignalType.CANCEL, k -> new ArrayList<>()).add(finalizer);
  }

  /**
   * 获取发布者
   *
   * @param predicate 过滤器
   * @return Flux 发布者
   */
  public FluxPublisher<D> filter(Predicate<D> predicate) {
    return new FluxPublisher<>(this.consumer, publisher.filter(predicate));
  }

  @Override
  public void subscribe(Subscriber<? super D> subscriber) {
    publisher.subscribe(subscriber);
  }

  private static class FluxConsumer<T> implements Consumer<FluxSink<T>> {

    private FluxSink<T> emitter;

    @Override
    public void accept(FluxSink<T> emitter) {
      this.emitter = emitter;
    }

    public void error(Exception ex) {
      this.emitter.error(ex);
    }

    public void next(T o) {
      this.emitter.next(o);
    }
  }
}
