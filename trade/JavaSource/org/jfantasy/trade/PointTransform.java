package org.jfantasy.trade;

import java.math.BigDecimal;

/**
 * 积分转换器,用于积分消费时的转换
 */
public interface PointTransform {

    BigDecimal convert(Long point);

}
