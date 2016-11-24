package org.jfantasy.order;


import java.util.Map;

public interface OrderServiceBuilder<T extends OrderService> {

    T build(Map props);

}
