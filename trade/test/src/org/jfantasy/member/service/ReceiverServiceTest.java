package org.jfantasy.member.service;

import org.jfantasy.member.bean.Receiver;
import org.jfantasy.pay.PayServerApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 收货地址
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PayServerApplication.class)
@ActiveProfiles("dev")
public class ReceiverServiceTest {

    @Autowired
    private ReceiverService receiverService;

    @Test
    public void update() throws Exception {
        Receiver receiver = new Receiver();
        receiver.setId(204L);
        receiver.setIsDefault(true);
        receiverService.update(receiver,true);
    }

}