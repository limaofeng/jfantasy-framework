package org.jfantasy.framework.util.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.framework.util.common.dto.TreeNode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class ObjectUtilTest {

    @Test
    public void toString1() throws Exception {

    }

    @Test
    public void filter() throws Exception {

    }

    @Test
    public void filter1() throws Exception {

    }

    @Test
    public void filter2() throws Exception {

    }

    @Test
    public void filter3() throws Exception {

    }

    @Test
    public void toString2() throws Exception {

    }

    @Test
    public void toFieldList() throws Exception {

    }

    @Test
    public void toFieldArray() throws Exception {

    }

    @Test
    public void toFieldArray1() throws Exception {

    }

    @Test
    public void toFieldArray2() throws Exception {

    }

    @Test
    public void getMaxObject() throws Exception {

    }

    @Test
    public void getMinObject() throws Exception {

    }

    @Test
    public void indexOf() throws Exception {

    }

    @Test
    public void indexOf1() throws Exception {

    }

    @Test
    public void find() throws Exception {

    }

    @Test
    public void first() throws Exception {

    }

    @Test
    public void last() throws Exception {

    }

    @Test
    public void exists() throws Exception {

    }

    @Test
    public void find1() throws Exception {

    }

    @Test
    public void find2() throws Exception {

    }

    @Test
    public void indexOf2() throws Exception {

    }

    @Test
    public void indexOf3() throws Exception {

    }

    @Test
    public void indexOf4() throws Exception {

    }

    @Test
    public void indexOf5() throws Exception {

    }

    @Test
    public void indexOf6() throws Exception {

    }

    @Test
    public void setProperties() throws Exception {

    }

    @Test
    public void sort() throws Exception {

        List<HashMap<String, String>> list = new ArrayList<>();

        HashMap<String, String> d1 = new HashMap<>();
        d1.put("properties", "{\"name\":\"2\"}");
        list.add(d1);

        HashMap<String, String> d2 = new HashMap<>();
        d1.put("properties", "{\"name\":\"1\"}");
        list.add(d2);

        ObjectUtil.sort(list, "x.x.x1", "");

    }

    @Test
    public void sort1() throws Exception {

    }

    @Test
    public void sort2() throws Exception {

    }

    @Test
    public void sort3() throws Exception {

    }

    @Test
    public void sort4() throws Exception {

    }

    @Test
    public void isNull() throws Exception {

    }

    @Test
    public void isNotNull() throws Exception {

    }

    @Test
    public void defaultValue() throws Exception {

    }

    @Test
    public void toMap() throws Exception {

    }

    @Test
    public void join() throws Exception {

    }

    @Test
    public void join1() throws Exception {

    }

    @Test
    public void join2() throws Exception {

    }

    @Test
    public void join3() throws Exception {

    }

    @Test
    public void exists1() throws Exception {

    }

    @Test
    public void exists2() throws Exception {

    }

    @Test
    public void remove() throws Exception {

    }

    @Test
    public void guid() throws Exception {

    }

    @Test
    public void reverse() throws Exception {

    }

    @Test
    public void analyze() throws Exception {

    }

    private static final Log LOG = LogFactory.getLog(ObjectUtilTest.class);

    @BeforeEach
    public void setUp() throws Exception {

    }

    @AfterEach
    public void tearDown() throws Exception {

    }

    @Test
    public void testClone() throws Exception {

    }

    @Test
    public void testToString() throws Exception {

    }

    @Test
    public void testFilter() throws Exception {
        List<ObjectTestBean> list = new ArrayList<>();
        list.add(new ObjectTestBean("limaofeng", Boolean.TRUE, 32));
        list.add(new ObjectTestBean("wangmingliang", Boolean.TRUE, 27));
        list.add(new ObjectTestBean("duanxiangbing", Boolean.FALSE, 25));

        List<ObjectTestBean> listf = ObjectUtil.filter(list, "locked", Boolean.TRUE);

        Assert.isTrue(listf.size() == 2);

        ObjectTestBean[] arrs = ObjectUtil.filter(list.toArray(new ObjectTestBean[list.size()]), "locked", Boolean.TRUE, Boolean.FALSE);

        Assert.isTrue(3 == arrs.length);

        arrs = ObjectUtil.filter(list.toArray(new ObjectTestBean[list.size()]), "locked", new Boolean[]{Boolean.TRUE, Boolean.FALSE});

        Assert.isTrue(3 == arrs.length);
    }

    @Test
    public void testToFieldArray() throws Exception {

    }

    @Test
    public void testGetMinObject() throws Exception {

    }

    @Test
    public void testIndexOf() throws Exception {

    }

    @Test
    public void testSort() throws Exception {

    }

    @Test
    public void merge() throws Exception {
        String[] dest = {"中国", "美国"};
        String[] items = {"中国", "英国"};

        String[] array = ObjectUtil.merge(dest, items);

        Assert.isTrue(array == new String[]{"中国", "美国", "英国"});
    }

    @Test
    public void testAnalyze() throws Exception {
    }

    @Test
    public void map() {
    }

    @Test
    public void tree() {
        List<TreeNode> nodes = new ArrayList<>();

        nodes.add(TreeNode.builder().id("1").name("第一级").build());
        nodes.add(TreeNode.builder().id("1.1").name("第二级(1)").index(2).parent(TreeNode.builder().id("1").build()).build());
        nodes.add(TreeNode.builder().id("1.2").name("第二级(2)").index(1).parent(TreeNode.builder().id("1").build()).build());
        nodes.add(TreeNode.builder().id("1.1.1").name("第三级").parent(TreeNode.builder().id("1.1").build()).build());
        nodes.add(TreeNode.builder().id("1.1.1.1").name("第四级").parent(TreeNode.builder().id("1.1.1").build()).build());

        List<TreeNode> treeData = ObjectUtil.tree(nodes, "id", "parent.id", "children");

        Assert.isTrue(treeData.size() == 1, "转换失败！");

        treeData = ObjectUtil.tree(nodes, "id", "parent.id", "children", Comparator.comparingInt(TreeNode::getIndex));

        Assert.isTrue(treeData.get(0).getChildren().get(0).getName().equals("第二级(2)"), "排序失败！");

        List<TreeNode> flat = ObjectUtil.flat(treeData, "children");

        Assert.isTrue(flat.size() == 5, "平铺失败");

    }


    public static class ObjectTestBean {
        private String name;
        private Boolean locked;
        private int age;

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