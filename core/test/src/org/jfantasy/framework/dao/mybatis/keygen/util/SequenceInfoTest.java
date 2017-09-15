package org.jfantasy.framework.dao.mybatis.keygen.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.ApplicationTest;
import org.jfantasy.framework.dao.mybatis.keygen.service.SequenceService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApplicationTest.class)
public class SequenceInfoTest {

    private static final Log LOG = LogFactory.getLog(SequenceInfoTest.class);

    @Autowired
    private SequenceService sequenceService;

    @Test
    public void testExists() {
        sequenceService.exists("contacts_book:id");
    }

    @Test
    public void testNextValue() throws Exception {
        LOG.debug("next：" + SequenceInfo.nextValue("test") + "次");
    }

    @Test
    public void testNextValues() throws Exception {
        List<Thread> threads = new ArrayList<Thread>();
        final Set<Long> set = new HashSet<Long>();
        for (int i = 0; i < 1200; i++) {
            threads.add(new Thread(() -> {
                set.add(SequenceInfo.nextValue("test"));
                LOG.debug("next：" + set.size() + "次");
            }));
        }
        for (Thread thread : threads) {
            thread.start();
        }
        Thread.sleep(TimeUnit.MINUTES.toMillis(2));
    }


}