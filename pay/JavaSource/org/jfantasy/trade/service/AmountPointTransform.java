package org.jfantasy.trade.service;

import org.jfantasy.trade.PointTransform;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class AmountPointTransform implements PointTransform {

    @Override
    public BigDecimal convert(Long point) {
        return BigDecimal.valueOf(point / 100);
    }

}
