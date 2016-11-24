package org.jfantasy.order.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Criterion;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.jackson.JSON;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.order.OrderServiceFactory;
import org.jfantasy.order.bean.OrderServer;
import org.jfantasy.order.dao.OrderServerDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class OrderServerService {

    private final static Log LOG = LogFactory.getLog(OrderServerService.class);

    @Autowired
    private OrderServerDao orderServerDao;
    @Autowired
    private OrderServiceFactory orderServiceFactory;

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public Pager<OrderServer> findPager(Pager<OrderServer> pager, List<PropertyFilter> filters) {
        return this.orderServerDao.findPager(pager, filters);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public OrderServer save(String type, String description, Map<String,Object> props) {
        OrderServer orderServer = ObjectUtil.defaultValue(this.orderServerDao.get(type),new OrderServer());
        orderServer.setType(type);
        orderServer.setDescription(description);
        orderServer.setEnabled(true);
        orderServer.setProperties(props);
        LOG.debug(" save OrderServer : " + JSON.serialize(orderServer));
        return this.orderServerDao.save(orderServer);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<OrderServer> find(Criterion... criterions) {
        return this.orderServerDao.find(criterions);
    }

//    public void register(OrderServer entity) {
//        if(entity.getCallType() == CallType.rpc) {
//            String[] ars = entity.get(OrderServer.PROPS_DOMAIN).split(":");
//            RpcProxyFactory rpcProxyFactory = new RpcProxyFactory(new NettyClientFactory(ars[0], Integer.valueOf(ars[1])));
//            orderServiceFactory.register(entity.getType(), rpcProxyFactory.proxyBean(org.jfantasy.pay.order.OrderService.class, timeoutInMillis));
//        }else if(entity.getCallType() == CallType.restful) {
//        }
//    }
//    @Transactional(propagation = Propagation.NOT_SUPPORTED)
//    public void register() {
//        for (OrderServer entity : this.find(Restrictions.eq("enabled", true))) {
//            try {
//                register(entity);
//            } catch (Exception e) {
//                LOG.error(e.getMessage(), e);
//            }
//        }
//    }

    public void unregister(OrderServer entity) {
        orderServiceFactory.unregister(entity.getType());
    }

}
