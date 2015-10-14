package com.fantasy.mall.goods.interceptor;

import com.fantasy.mall.goods.service.GoodsService;
import com.fantasy.mall.goods.service.ProductService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * 修改货品的占用库存数时
 */
//@Component
//@Aspect
public class ProductFreezeStoreInterceptor {

    @Autowired
    private ProductService productService;
    @Autowired
    private GoodsService goodsService;

    /**
     * 货品占用数量发生变化时，更新商品的占用数量
     * @功能描述
     * @param point
     */
    @After("execution(public * com.fantasy.mall.goods.service.ProductService.freezeStore(..))")
    @Transactional
    public void freezeStore(JoinPoint point) {
        Long productId = (Long) point.getArgs()[0];
        Integer quantity = (Integer) point.getArgs()[1];
        goodsService.freezeStore(productService.get(productId).getGoods().getId(), quantity);
    }


}
