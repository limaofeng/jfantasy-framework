package org.jfantasy.framework.util.concurrent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.framework.util.json.bean.User;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

public class LinkedQueueTest {

    private static Log logger = LogFactory.getLog(LinkedQueueTest.class);

    @Test
    public void testRemove() throws Exception {
        LinkedQueue<User> queue = new LinkedQueue<User>();
        User user = new User();
        //添加数据
        queue.put(user);

        Assert.isTrue(queue.remove(user));

        Assert.isTrue(queue.size() == 0);
    }

    @Test
    public void testIterator() throws Exception {
        LinkedQueue<User> queue = new LinkedQueue<User>();
        User _u1 = new User(){
            {
                this.setNickName("test-1");
            }
        };
        User _u2 = new User(){
            {
                this.setNickName("test-2");
            }
        };
        User _u3 = new User(){
            {
                this.setNickName("test-3");
            }
        };
        //添加数据
        queue.add(_u1);
        queue.add(_u2);
        queue.add(_u3);

        queue.remove(_u3);
        queue.remove(_u1);

        logger.error("queue size : " + queue.size());

        for (User user : queue) {
            logger.error(user.getNickName());
            Assert.isTrue(user.getNickName() == "test-2");
        }
    }
}