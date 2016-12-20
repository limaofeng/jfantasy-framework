package org.jfantasy.member.service;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.member.bean.Receiver;
import org.jfantasy.member.dao.ReceiverDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ReceiverService {

    private final ReceiverDao receiverDao;

    @Autowired
    public ReceiverService(ReceiverDao receiverDao) {
        this.receiverDao = receiverDao;
    }

    private void changeDefault(Receiver receiver) {
        Long memberId = receiver.getId() != null ? this.receiverDao.get(receiver.getId()).getMemberId() : receiver.getMemberId();
        int count = this.receiverDao.count(Restrictions.eq("memberId", memberId));
        if (count == 0) {
            receiver.setIsDefault(true);
        } else {
            if (ObjectUtil.defaultValue(receiver.getIsDefault(), false)) {
                List<Receiver> receivers = this.receiverDao.find(Restrictions.eq("memberId", memberId), Restrictions.eq("isDefault", true));
                for (Receiver ver : receivers) {
                    ver.setIsDefault(false);
                    receiverDao.save(ver);
                }
            }
        }
    }

    public Receiver save(Receiver receiver) {
        changeDefault(receiver);
        return receiverDao.save(receiver);
    }

    public Receiver update(Receiver receiver,boolean patch) {
        changeDefault(receiver);
        return receiverDao.update(receiver,patch);
    }

    public List<Receiver> find(Criterion[] criterions, String orderBy, String order) {
        return this.receiverDao.find(criterions, orderBy, order);
    }

    public List<Receiver> find(List<PropertyFilter> filters, String orderBy, String order) {
        return this.receiverDao.find(filters, orderBy, order);
    }

    public Receiver get(Long id) {
        return this.receiverDao.get(id);
    }

    public void deltele(Long id) {
        Receiver receiver = this.receiverDao.get(id);
        if (Boolean.TRUE.equals(receiver.getIsDefault())) {
            List<Receiver> receivers = this.receiverDao.find(new Criterion[]{Restrictions.eq("memberId", receiver.getMemberId()), Restrictions.eq("isDefault", Boolean.FALSE)}, "isDefault", "desc", 0, 1);
            if (!receivers.isEmpty()) {
                receivers.get(0).setIsDefault(true);
                this.receiverDao.save(receivers.get(0));
            }
        }
        this.receiverDao.delete(receiver);
    }

}
