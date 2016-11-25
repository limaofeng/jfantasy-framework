package org.jfantasy.order;


import java.util.Map;

public interface OrderServiceBuilder<T extends OrderDetailService> {

    T build(Map props);

}
