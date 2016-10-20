package org.jfantasy.framework.util.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
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
    public void remove1() throws Exception {

    }

    @Test
    public void remove2() throws Exception {

    }

    @Test
    public void first1() throws Exception {

    }

    @Test
    public void first2() throws Exception {

    }

    @Test
    public void last1() throws Exception {

    }

    @Test
    public void last2() throws Exception {

    }

    @Test
    public void guid() throws Exception {

    }

    @Test
    public void reverse() throws Exception {

    }

    @Test
    public void reverse1() throws Exception {

    }

    @Test
    public void analyze() throws Exception {

    }

    private final static Log LOG = LogFactory.getLog(ObjectUtilTest.class);

    @Before
    public void setUp() throws Exception {

    }

    @After
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

        Assert.assertEquals(listf.size(), 2);

        ObjectTestBean[] arrs = ObjectUtil.filter(list.toArray(new ObjectTestBean[list.size()]), "locked", Boolean.TRUE, Boolean.FALSE);

        Assert.assertEquals(3, arrs.length);

        arrs = ObjectUtil.filter(list.toArray(new ObjectTestBean[list.size()]), "locked", new Boolean[]{Boolean.TRUE, Boolean.FALSE});

        Assert.assertEquals(3, arrs.length);
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

        Assert.assertArrayEquals(array, new String[]{"中国", "美国", "英国"});
    }

    @Test
    public void testAnalyze() throws Exception {
        LOG.debug(ObjectUtil.analyze("上海昊略公司，提供应用软件和服务"));
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