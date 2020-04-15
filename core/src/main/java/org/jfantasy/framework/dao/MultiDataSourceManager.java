package org.jfantasy.framework.dao;

import org.jfantasy.framework.dao.annotations.DataSource;
import org.jfantasy.framework.util.Stack;
import org.jfantasy.framework.util.common.ObjectUtil;

/**
 * 多数据源管理类
 *
 * @author limaofeng
 */
public class MultiDataSourceManager {

    private static final ThreadLocal<MultiDataSourceManager> DELEGATE = new ThreadLocal<>();

    private Stack<DataSource> stack = new Stack<>();

    public static MultiDataSourceManager getManager() {
        MultiDataSourceManager localMessage = DELEGATE.get();
        if (ObjectUtil.isNull(localMessage)) {
            DELEGATE.set(new MultiDataSourceManager());
        }
        return DELEGATE.get();
    }

    public void push(DataSource dataSource) {
        this.stack.push(dataSource);
    }

    public DataSource peek() {
        return this.stack.peek();
    }

    public DataSource pop() {
        return this.stack.pop();
    }

    public void destroy() {
        DELEGATE.remove();
    }
}
