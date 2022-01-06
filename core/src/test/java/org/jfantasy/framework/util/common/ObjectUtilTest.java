package org.jfantasy.framework.util.common;

import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.framework.jackson.models.Article;
import org.jfantasy.framework.jackson.models.User;
import org.jfantasy.framework.util.common.dto.TreeNode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

@Slf4j
public class ObjectUtilTest {

  @Test
  public void toString1() {}

  @Test
  public void filter() {}

  @Test
  public void filter1() {}

  @Test
  public void filter2() {}

  @Test
  public void filter3() {}

  @Test
  public void toString2() {}

  @Test
  public void toFieldList() {}

  @Test
  public void toFieldArray() {}

  @Test
  public void toFieldArray1() {}

  @Test
  public void toFieldArray2() {}

  @Test
  public void getMaxObject() {}

  @Test
  public void getMinObject() {}

  @Test
  public void indexOf() {}

  @Test
  public void indexOf1() {}

  @Test
  public void find() {}

  @Test
  public void first() {}

  @Test
  public void last() {}

  @Test
  public void exists() {}

  @Test
  public void recursive() {
    TreeNode node1 = TreeNode.builder().name("1").build();
    node1.setChildren(Collections.singletonList(TreeNode.builder().name("1-1").build()));
    node1.setChildren(Collections.singletonList(TreeNode.builder().name("1-2").build()));
    TreeNode node2 = TreeNode.builder().name("2").build();
    List<TreeNode> list = Arrays.asList(node1, node2);
    List<TreeNode> x =
        ObjectUtil.recursive(
            list,
            (item, context) -> {
              item.setIndex(context.getIndex());
              item.setLevel(context.getLevel());
              item.setParent(context.getParent());
              return item;
            });
    log.debug("Layer:" + x);
  }

  @Test
  public void find2() {}

  @Test
  public void indexOf2() {}

  @Test
  public void indexOf3() {}

  @Test
  public void indexOf4() {}

  @Test
  public void indexOf5() {}

  @Test
  public void indexOf6() {}

  @Test
  public void setProperties() {}

  @Test
  public void sort() {

    List<HashMap<String, String>> list = new ArrayList<>();

    HashMap<String, String> d1 = new HashMap<>();
    d1.put("properties", "{\"name\":\"2\"}");
    list.add(d1);

    HashMap<String, String> d2 = new HashMap<>();
    d1.put("properties", "{\"name\":\"1\"}");
    list.add(d2);

    ObjectUtil.sort(list, "x.x.x1", "");

    List<ObjectTestBean> beans = new ArrayList<>();
    beans.add(new ObjectTestBean("limaofeng", Boolean.TRUE, 32));
    beans.add(new ObjectTestBean("wangmingliang", Boolean.TRUE, 27));
    beans.add(new ObjectTestBean("duanxiangbing", Boolean.FALSE, 25));

    beans = ObjectUtil.sort(beans, (l, r) -> r.age - l.age);

    log.debug(beans.toString());
  }

  @Test
  public void isNull() {}

  @Test
  public void isNotNull() {}

  @Test
  public void defaultValue() {}

  @Test
  public void toMap() {}

  @Test
  public void join() {}

  @Test
  public void join1() {}

  @Test
  public void join2() {}

  @Test
  public void join3() {}

  @Test
  public void exists1() {}

  @Test
  public void exists2() {}

  @Test
  public void remove() {}

  @Test
  public void guid() {}

  @Test
  public void reverse() {}

  @Test
  public void analyze() {}

  private static final Log LOG = LogFactory.getLog(ObjectUtilTest.class);

  @BeforeEach
  public void setUp() {}

  @AfterEach
  public void tearDown() {}

  @Test
  public void testCopy() {
    User user = new User();
    user.setName("limaofeng");
    Article article = ObjectUtil.copy(user, new Article());
    log.debug(article.getName());
    assert article.getName().equals(user.getName());
  }

  @Test
  public void testClone() {}

  @Test
  public void testToString() {}

  @Test
  public void testFilter() {
    List<ObjectTestBean> list = new ArrayList<>();
    list.add(new ObjectTestBean("limaofeng", Boolean.TRUE, 32));
    list.add(new ObjectTestBean("wangmingliang", Boolean.TRUE, 27));
    list.add(new ObjectTestBean("duanxiangbing", Boolean.FALSE, 25));

    List<ObjectTestBean> listf = ObjectUtil.filter(list, "locked", Boolean.TRUE);

    Assert.isTrue(listf.size() == 2, "不一致");

    ObjectTestBean[] arrs =
        ObjectUtil.filter(
            list.toArray(new ObjectTestBean[0]), "locked", Boolean.TRUE, Boolean.FALSE);

    Assert.isTrue(3 == arrs.length, "长度不一致");

    arrs =
        ObjectUtil.filter(
            list.toArray(new ObjectTestBean[0]), "locked", Boolean.TRUE, Boolean.FALSE);

    Assert.isTrue(3 == arrs.length, "长度不一致");
  }

  @Test
  public void testToFieldArray() {}

  @Test
  public void testGetMinObject() {}

  @Test
  public void testIndexOf() {}

  @Test
  public void testSort() {}

  @Test
  public void merge() {
    String[] dest = {"中国", "美国"};
    String[] items = {"中国", "英国"};

    String[] array = ObjectUtil.merge(dest, items);

    Assert.isTrue(Arrays.equals(array, new String[] {"中国", "美国", "英国"}), "长度不一致");
  }

  @Test
  public void testAnalyze() {}

  @Test
  public void map() {}

  @Test
  public void tree() {
    List<TreeNode> nodes = new ArrayList<>();

    nodes.add(TreeNode.builder().id("1").name("第一级").build());
    nodes.add(
        TreeNode.builder()
            .id("1.1")
            .name("第二级(1)")
            .index(2)
            .parent(TreeNode.builder().id("1").build())
            .build());
    nodes.add(
        TreeNode.builder()
            .id("1.2")
            .name("第二级(2)")
            .index(1)
            .parent(TreeNode.builder().id("1").build())
            .build());
    nodes.add(
        TreeNode.builder()
            .id("1.1.1")
            .name("第三级")
            .parent(TreeNode.builder().id("1.1").build())
            .build());
    nodes.add(
        TreeNode.builder()
            .id("1.1.1.1")
            .name("第四级")
            .parent(TreeNode.builder().id("1.1.1").build())
            .build());

    List<TreeNode> treeData = ObjectUtil.tree(nodes, "id", "parent.id", "children");

    Assert.isTrue(treeData.size() == 1, "转换失败！");

    treeData =
        ObjectUtil.tree(
            nodes, "id", "parent.id", "children", Comparator.comparingInt(TreeNode::getIndex));

    Assert.isTrue(treeData.get(0).getChildren().get(0).getName().equals("第二级(2)"), "排序失败！");

    List<TreeNode> flat = ObjectUtil.flat(treeData, "children");

    Assert.isTrue(flat.size() == 5, "平铺失败");

    nodes.forEach(item -> item.setParent(null));

    flat = ObjectUtil.flat(treeData, "children", "parent");

    Assert.isTrue(flat.get(1).getParent() != null, "平铺设置 parent 失败");
  }

  public static class ObjectTestBean {
    private final String name;
    private final Boolean locked;
    private final int age;

    public ObjectTestBean(String name, Boolean locked, int age) {
      this.name = name;
      this.locked = locked;
      this.age = age;
    }

    public String getName() {
      return name;
    }

    public Boolean getLocked() {
      return locked;
    }

    public int getAge() {
      return age;
    }
  }
}
