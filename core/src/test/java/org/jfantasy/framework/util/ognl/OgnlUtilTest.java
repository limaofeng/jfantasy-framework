package org.jfantasy.framework.util.ognl;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import ognl.Ognl;
import ognl.OgnlContext;
import org.jfantasy.framework.jackson.models.User;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

@Slf4j
public class OgnlUtilTest {

  @Test
  public void testSetValue() {
    User user = new User();
    OgnlContext ognlContext = (OgnlContext) Ognl.createDefaultContext(user);
    OgnlUtil.getInstance().setValue("id", user, "1");
    OgnlUtil.getInstance().setValue("username", user, "limaofeng");
    OgnlUtil.getInstance().setValue("details.name", user, "limaofeng");
    System.out.println(user);
  }

  @Test
  public void testSetValueByBeanArray() {
    OgnlBean ognlTest = new OgnlBean();

    OgnlUtil.getInstance().setValue("number", ognlTest, "100");
    OgnlUtil.getInstance().setValue("name", ognlTest, "test");

    log.debug(ognlTest.toString());

    OgnlUtil.getInstance().setValue("names[0]", ognlTest, "test1");
    OgnlUtil.getInstance().setValue("names[1]", ognlTest, "test2");

    log.debug(ognlTest.toString());

    Assert.isTrue(ognlTest.getNames().length == 2);

    OgnlUtil.getInstance().setValue("listNames[0]", ognlTest, "test1");
    OgnlUtil.getInstance().setValue("listNames[1]", ognlTest, "test2");

    log.debug(ognlTest.toString());

    Assert.isTrue(ognlTest.getListNames().size() == 2);

    OgnlUtil.getInstance().setValue("list[0].number", ognlTest, "100");
    OgnlUtil.getInstance().setValue("list[0].name", ognlTest, "test");

    log.debug(ognlTest.toString());

    Assert.isTrue(ognlTest.getList().size() == 1);

    OgnlUtil.getInstance().setValue("bean.array[0].number", ognlTest, "100");
    OgnlUtil.getInstance().setValue("bean.array[0].name", ognlTest, "test");

    OgnlUtil.getInstance().setValue("bean.array[1].number", ognlTest, "100");
    OgnlUtil.getInstance().setValue("bean.array[1].name", ognlTest, "test");

    log.debug(ognlTest.toString());

    Assert.isTrue(ognlTest.getBean().getArray().length == 2);
  }

  @Test
  public void arrayTest() throws Exception {
    OgnlBean ognlTest = new OgnlBean();
    OgnlUtil.getInstance().setValue("array[0].number", ognlTest, "100");
    OgnlUtil.getInstance().setValue("array[0].name", ognlTest, "test0");
    OgnlUtil.getInstance().setValue("array[1].number", ognlTest, "200");
    OgnlUtil.getInstance().setValue("array[1].name", ognlTest, "test1");
    log.debug(ognlTest.toString());
  }

  @Test
  public void getListItem() throws Exception {
    List<OgnlBean> list = new ArrayList<>();
    list.add(OgnlBean.builder().name("limaofeng").build());
    list.add(OgnlBean.builder().name("huangli").build());
    OgnlBean bean = OgnlUtil.getInstance().getValue("[0]", list);
    log.debug(bean.getName());
  }
}
