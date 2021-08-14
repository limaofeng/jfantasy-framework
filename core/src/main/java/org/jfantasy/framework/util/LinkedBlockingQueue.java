package org.jfantasy.framework.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 线程安全的队列实现
 *
 * @param <E>
 * @author 李茂峰
 * @version 1.0
 * @since 2012-12-4 下午02:36:30
 */
public class LinkedBlockingQueue<E> extends AbstractQueue<E> implements BlockingQueue<E>, Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -6642633435807467698L;
    /**
     * 容量
     */
    private final int capacity;
    /**
     * 计数器
     */
    private final AtomicInteger count = new AtomicInteger(0);
    /**
     * 链表头元素
     */
    private transient Node<E> head;
    /**
     * 链表尾元素
     */
    private transient Node<E> last;
    /**
     * 输出锁
     */
    private final ReentrantLock takeLock = new ReentrantLock();
    private final Condition notEmpty = this.takeLock.newCondition();
    /**
     * 输入锁
     */
    private final ReentrantLock putLock = new ReentrantLock();
    private final Condition notFull = this.putLock.newCondition();


    /**
     * 提供List接口的访问方式
     */
    private List<E> list = new InternalList<>(this);

    public LinkedBlockingQueue() {
        this(Integer.MAX_VALUE);
    }

    public LinkedBlockingQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException();
        }
        this.capacity = capacity;
        this.last = this.head = new Node<>();// 初始化链表
    }

    public LinkedBlockingQueue(Collection<? extends E> c) {
        this();
        for (E e : c) {
            add(e);
        }

    }

    /**
     * 唤醒等待的线程(输出)
     * <p/>
     * 输出线程可以开始取值了.
     */
    private void signalNotEmpty() {
        this.takeLock.lock();
        try {
            this.notEmpty.signal();
        } finally {
            this.takeLock.unlock();
        }
    }

    /**
     * 唤醒等待的线程(输入),输入线程可以开始存值了.
     */
    private void signalNotFull() {
        ReentrantLock tputLock = this.putLock;
        tputLock.lock();
        try {
            this.notFull.signal();
        } finally {
            tputLock.unlock();
        }
    }

    /**
     * 向链表的末尾追加元素
     *
     * @param x E
     */
    private void insert(E x) {
        this.last = this.last.next = new Node<>(x, null, this.last);
        this.head.previous = this.last;// 让链表首尾相连 --> 链表头会链接链表尾 但链表尾并没有链接到链表头
    }

    /**
     * 从链表的头取出元素
     *
     * @return E
     */
    private E extract() {
        Node<E> first = this.head.next;
        this.head = first;
        this.head.previous = this.last;
        E x = first.item;
        first.item = null;
        return x;
    }

    /**
     * 同时获取输入及输出锁
     *
     * 
     */
    public void fullyLock() {
        this.putLock.lock();
        this.takeLock.lock();
    }

    /**
     * 同时释放输入及输出锁
     */
    public void fullyUnlock() {
        this.takeLock.unlock();
        this.putLock.unlock();
    }

    /**
     * 获取链表长度
     */
    @Override
    public int size() {
        return this.count.get();
    }

    /**
     * 获取链表的可用长度
     */
    @Override
    public int remainingCapacity() {
        return this.capacity - this.count.get();
    }

    /**
     * 向链表添加元素
     * <p/>
     * 如果链表容量已满，会持续等待
     *
     * @param o E
     * @throws InterruptedException
     */
    @Override
    public void put(E o) throws InterruptedException {
        if (o == null) {
            throw new NullPointerException();
        }
        int c = -1;
        ReentrantLock tputLock = this.putLock;
        AtomicInteger tcount = this.count;
        tputLock.lockInterruptibly();
        try {
            try {
                // 如果链表已满,等待释放
                while (tcount.get() == this.capacity) {
                    this.notFull.await();
                }
            } catch (InterruptedException ie) {
                this.notFull.signal();
                throw ie;
            }
            insert(o);// 添加
            c = tcount.getAndIncrement();// 获取当前链表数量
            if (c + 1 < this.capacity) {
                this.notFull.signal();
                // 判断是否可以继续输入
            }

        } finally {
            tputLock.unlock();
            if (c == 0) {
                signalNotEmpty();// 通知输出锁，如果有元素正在等待输出。立即激活该线程
            }

        }
    }

    /**
     * 添加元素,可设置操作的超时时间
     *
     * @param o       要添加的元素
     * @param timeout 超时时间
     * @param unit    时间单位
     * @return boolean
     * @throws InterruptedException
     */
    @Override
    public boolean offer(E o, long timeout, TimeUnit unit) throws InterruptedException {
        if (o == null) {
            throw new NullPointerException();
        }
        int c = -1;
        ReentrantLock tputLock = this.putLock;
        AtomicInteger tcount = this.count;
        tputLock.lockInterruptibly();
        try {
            // 将超时时间转为 毫微秒
            if (waiting(unit.toNanos(timeout), tcount, this.capacity)) { // 如果容量已满。
                return false;  // 等待时间耗尽，返回false
            }
            insert(o);
            c = tcount.getAndIncrement();
            if (c + 1 < this.capacity) {
                this.notFull.signal();
            }
            return c >= 0;
        } finally {
            tputLock.unlock();
            if (c == 0) {
                signalNotEmpty();
            }
        }
    }

    /**
     * 添加元素,如果容量超出，则直接返回false
     *
     * @param o E
     * @return bookean
     */
    @Override
    public boolean offer(E o) {
        if (o == null) {
            throw new NullPointerException();
        }
        AtomicInteger tcount = this.count;
        if (tcount.get() == this.capacity) {
            return false;
        }
        int c = -1;
        ReentrantLock tputLock = this.putLock;
        tputLock.lock();
        try {
            if (tcount.get() < this.capacity) {
                insert(o);
                c = tcount.getAndIncrement();
                if (c + 1 < this.capacity) {
                    this.notFull.signal();
                }
            }
        } finally {
            tputLock.unlock();
            if (c > 0) {
                signalNotEmpty();
            }
        }
        return c >= 0;
    }

    /**
     * 获取队列的元素
     *
     * @return E
     * @throws InterruptedException
     */
    @Override
    public E take() throws InterruptedException {
        int c = -1;
        AtomicInteger tcount = this.count;
        ReentrantLock ttakeLock = this.takeLock;
        ttakeLock.lockInterruptibly();
        E x;
        try {
            try {
                // 如果容量为0,持续等待获取元素
                while (tcount.get() == 0) {
                    this.notEmpty.await();
                }
            } catch (InterruptedException ie) {
                this.notEmpty.signal();
                throw ie;
            }
            x = extract();// 获取元素
            c = tcount.getAndDecrement();// 计数器减1
            if (c > 1) {
                this.notEmpty.signal();// 判断是否可以继续输出
            }
        } finally {
            ttakeLock.unlock();
            if (c == this.capacity) {
                signalNotFull(); // 如果有线程在等待输入的话，激活线程。因为容量满额时，可能有线程正在等待输入
            }
        }
        return x;
    }

    /**
     * 获取队列元素，取出
     *
     * @param timeout 操作超时时间
     * @param unit    时间单位
     * @return 队列为空返回 null
     * @throws InterruptedException
     */
    @Override
    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        int c = -1;
        long nanos = unit.toNanos(timeout);
        AtomicInteger tcount = this.count;
        ReentrantLock ttakeLock = this.takeLock;
        ttakeLock.lockInterruptibly();
        E x;
        try {
            if(waiting(nanos,tcount,0)){
                return null;
            }
            x = extract();// 获取元素
            c = tcount.getAndDecrement();// 计数器减1
            if (c > 1) {
                this.notEmpty.signal();     // 判断是否可以继续输出
            }
        } finally {
            ttakeLock.unlock();
            if (c == this.capacity) {
                signalNotFull();
            }
        }
        return x;
    }

    private boolean waiting(long timeout, AtomicInteger tcount, int loop) throws InterruptedException {
        long nanos = timeout;
        try {
            while (tcount.get() == loop) {
                if (nanos <= 0) {
                    return true;
                }
                nanos = this.notFull.awaitNanos(nanos);// 挂起线程
            }
        } catch (InterruptedException ie) {
            this.notFull.signal();
            throw ie;
        }
        return false;
    }

    /**
     * 获取队列元素，取出
     *
     * @return 队列为空返回 null
     */
    @Override
    public E poll() {
        AtomicInteger tcount = this.count;
        if (tcount.get() == 0) {
            return null;
        }
        E x = null;
        int c = -1;
        ReentrantLock ttakeLock = this.takeLock;
        ttakeLock.lock();
        try {
            if (tcount.get() > 0) {
                x = extract();
                c = tcount.getAndDecrement();
                if (c <= 1) {
                    this.notEmpty.signal();
                }
            }
        } finally {
            ttakeLock.unlock();
        }
        if (c == this.capacity) {
            signalNotFull();
        }
        return x;
    }

    /**
     * 获取队列元素，不取出。
     * <p/>
     * 队列为空返回 null
     *
     * @return E
     */
    @Override
    public E peek() {
        if (this.count.get() == 0) {
            return null;
        }
        ReentrantLock ttakeLock = this.takeLock;
        ttakeLock.lock();
        try {
            Node<E> first = this.head.next;
            if (first == null) {
                return null;
            }
            return first.item;
        } finally {
            ttakeLock.unlock();
        }
    }

    /**
     * 转换为数组
     *
     * @return Object[]
     */
    @Override
    public Object[] toArray() {
        fullyLock();
        try {
            int size = this.count.get();
            Object[] a = new Object[size];
            int k = 0;
            for (Node<E> p = this.head.next; p != null; p = p.next) {
                a[k++] = p.item;
            }
            return a;
        } finally {
            fullyUnlock();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        T[] array = a;
        fullyLock();
        try {
            int size = this.count.get();
            if (array.length < size) {
                array = (T[]) Array.newInstance(a.getClass().getComponentType(), size);
            }
            int k = 0;
            for (Node<E> p = this.head.next; p != null; p = p.next) {
                array[k++] = (T) p.item;
            }
            return array;
        } finally {
            fullyUnlock();
        }
    }

    @Override
    public String toString() {
        fullyLock();
        try {
            return super.toString();
        } finally {
            fullyUnlock();
        }
    }

    /**
     * 清空队列
     */
    @Override
    public void clear() {
        fullyLock();
        try {
            this.head.next = null;
            assert this.head.item == null;
            this.last = this.head;
            if (this.count.getAndSet(0) == this.capacity) {
                this.notFull.signalAll();
            }
        } finally {
            fullyUnlock();
        }
    }

    @Override
    public int drainTo(Collection<? super E> c) {
        if (c == null) {
            throw new NullPointerException();
        }
        if (c == this) {
            throw new IllegalArgumentException();
        }
        fullyLock();
        Node<E> first;
        try {
            first = this.head.next;
            this.head.next = null;
            assert this.head.item == null;
            this.last = this.head;
            if (this.count.getAndSet(0) == this.capacity) {
                this.notFull.signalAll();
            }

        } finally {
            fullyUnlock();
        }
        int n = 0;
        for (Node<E> p = first; p != null; p = p.next) {
            c.add(p.item);
            p.item = null;
            n++;
        }
        return n;
    }

    @Override
    public int drainTo(Collection<? super E> c, int maxElements) {
        if (c == null) {
            throw new NullPointerException();
        }
        if (c == this) {
            throw new IllegalArgumentException();
        }
        fullyLock();
        try {
            int n = 0;
            Node<E> p = this.head.next;
            while ((p != null) && (n < maxElements)) {
                c.add(p.item);
                p.item = null;
                p = p.next;
                n++;
            }
            if (n != 0) {
                this.head.next = p;
                assert this.head.item == null;
                if (p == null) {
                    this.last = this.head;
                }
                if (this.count.getAndAdd(-n) == this.capacity) {
                    this.notFull.signalAll();
                }
            }
            return n;
        } finally {
            fullyUnlock();
        }
    }

    @Override
    public Iterator<E> iterator() {
        return new Itr();
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        fullyLock();
        try {
            s.defaultWriteObject();
            for (Node<E> p = this.head.next; p != null; p = p.next) {
                s.writeObject(p.item);
            }
            s.writeObject(null);
        } finally {
            fullyUnlock();
        }
    }

    /**
     * readObject
     *
     * @param s ObjectInputStream
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.count.set(0);
        this.last = this.head = new Node<>();
        while (true) {
            E item = (E) s.readObject();
            if (item == null) {
                break;
            }
            add(item);
        }
    }

    /**
     * 为List接口提供的方法
     * <p/>
     * {@link #list 的 get(int) 方法}
     *
     * @param index 下标
     * @return E
     */
    protected E get(int index) {
        return entry(index).item;
    }

    /**
     * 获取{index}对应的元素
     * <p/>
     * 不移除元素
     *
     * @param index 下标
     * @return Node<E>
     */
    private Node<E> entry(int index) {
        int size = this.count.get();
        if ((index < 0) || (index >= size)) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        Node<E> e = this.head;
        if (index < size >> 1) {
            for (int i = 0; i <= index; i++) {
                e = e.next;
            }
        } else {
            for (int i = size; i > index; i--) {
                e = e.previous;
            }
        }
        return e;
    }

    @Override
    public boolean add(E e) {
        return offer(e);
    }

    /**
     * 添加元素
     *
     * @param index 下标
     * @param o     E
     * @return boolean
     */
    public boolean add(int index, E o) {
        if (o == null) {
            throw new NullPointerException();
        }
        AtomicInteger tcount = this.count;
        if (tcount.get() == this.capacity) {
            return false;
        }
        int c = -1;
        ReentrantLock tputLock = this.putLock;
        tputLock.lock();
        try {
            if (tcount.get() < this.capacity) {
                if (index >= size()) {
                    insert(o);
                } else {
                    Node<E> p = entry(index);
                    p.previous.next = p.previous = new Node<>(o, p, p.previous);
                }
                c = tcount.getAndIncrement();
                if (c + 1 < this.capacity) {
                    this.notFull.signal();
                }
            }
        } finally {
            tputLock.unlock();
        }
        if (c == 0) {
            signalNotEmpty();
        }
        return c >= 0;
    }

    protected int indexOf(E o) {
        fullyLock();
        try {
            int index = 0;
            for (Node<E> e = this.head.next; e != this.head; e = e.next) {
                if (e.item.equals(o)) {
                    return index;
                }
                index++;
            }
            return -1;
        } finally {
            fullyUnlock();
        }
    }

    protected E set(int index, E element) {
        fullyLock();
        try {
            Node<E> e = entry(index);
            E oldVal = e.item;
            e.item = element;
            return oldVal;
        } finally {
            fullyUnlock();
        }
    }

    /**
     * 删除元素
     *
     * @param index 下标
     * @return E
     */
    protected E remove(int index) {
        fullyLock();
        try {
            Node<E> e = entry(index);
            Node<E> p = e.previous;
            E item = e.item;
            e.item = null;
            p.next = e.next;// 上个节点的下一个指向当前节点的下一个节点
            e.next.previous = p;// 当前节点的下一节点的上节点指向当前节点的上一个节点
            if (this.count.getAndDecrement() != this.capacity) {
                this.notFull.signalAll();
            }
            return item;
        } finally {
            fullyUnlock();
        }
    }

    /**
     * 移除元素
     *
     * @param o 对象
     * @return boolean
     */
    @Override
    public boolean remove(Object o) {
        if (o == null) {
            return false;
        }
        boolean removed = false;
        fullyLock();
        try {
            Node<E> trail = this.head;
            Node<E> p = this.head.next;
            while (p != null) {
                if (o.equals(p.item)) {
                    removed = true;
                    break;
                }
                trail = p;
                p = p.next;
            }
            if (removed) {
                p.item = null;
                trail.next = p.next;
                p.next.previous = trail;
                if (this.count.getAndDecrement() != this.capacity) {
                    this.notFull.signalAll();
                }
            }
        } finally {
            fullyUnlock();
        }
        return removed;
    }

    public List<E> list() {
        return this.list;
    }

    private class Itr implements Iterator<E> {
        private LinkedBlockingQueue.Node<E> current;
        private LinkedBlockingQueue.Node<E> lastRet;
        private E currentElement;

        Itr() {
            ReentrantLock tputLock = LinkedBlockingQueue.this.putLock;
            ReentrantLock ttakeLock = LinkedBlockingQueue.this.takeLock;
            tputLock.lock();
            ttakeLock.lock();
            try {
                this.current = LinkedBlockingQueue.this.head.next;
                if (this.current != null) {
                    this.currentElement = this.current.item;
                }
            } finally {
                ttakeLock.unlock();
                tputLock.unlock();
            }
        }

        @Override
        public boolean hasNext() {
            return this.current != null;
        }

        @Override
        public E next() {
            ReentrantLock tputLock = LinkedBlockingQueue.this.putLock;
            ReentrantLock ttakeLock = LinkedBlockingQueue.this.takeLock;
            tputLock.lock();
            ttakeLock.lock();
            try {
                if (this.current == null) {
                    throw new NoSuchElementException();
                }
                E x = this.currentElement;
                this.lastRet = this.current;
                this.current = this.current.next;
                if (this.current != null) {
                    this.currentElement = this.current.item;
                }
                return x;
            } finally {
                ttakeLock.unlock();
                tputLock.unlock();
            }
        }

        @Override
        public void remove() {
            if (this.lastRet == null) {
                throw new IllegalStateException();
            }
            ReentrantLock tputLock = LinkedBlockingQueue.this.putLock;
            ReentrantLock ttakeLock = LinkedBlockingQueue.this.takeLock;
            tputLock.lock();
            ttakeLock.lock();
            try {
                LinkedBlockingQueue.Node<E> node = this.lastRet;
                this.lastRet = null;
                LinkedBlockingQueue.Node<E> trail = LinkedBlockingQueue.this.head;
                LinkedBlockingQueue.Node<E> p = LinkedBlockingQueue.this.head.next;
                while ((p != null) && (p != node)) {
                    trail = p;
                    p = p.next;
                }
                if (p == null) {
                    return;
                }
                if (p == node) {
                    p.item = null;
                    trail.next = p.next;
                    int c = LinkedBlockingQueue.this.count.getAndDecrement();
                    if (c == LinkedBlockingQueue.this.capacity) {
                        LinkedBlockingQueue.this.notFull.signalAll();
                    }
                }
            } finally {
                ttakeLock.unlock();
                tputLock.unlock();
            }
        }
    }

    /**
     * 链表节点类
     *
     * @param <E>
     * @author 李茂峰
     * @version 1.0
     * @since 2012-12-4 下午02:14:43
     */
    static class Node<E> {
        /**
         * 当前节点对于的元素
         */
        volatile E item;
        /**
         * 下一个节点
         */
        Node<E> next;
        /**
         * 上一个节点
         */
        Node<E> previous;

        Node() {
        }

        Node(E item, Node<E> next, Node<E> previous) {
            this.item = item;
            this.next = next;
            this.previous = previous;
        }

    }

    static class InternalList<E> implements List<E> {

        private LinkedBlockingQueue<E> queue;

        InternalList(LinkedBlockingQueue<E> queue) {
            this.queue = queue;
        }

        @Override
        public boolean add(E o) {
            return this.queue.add(o);
        }

        @Override
        public void add(int index, E element) {
            this.queue.add(index, element);
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            return this.queue.addAll(c);
        }

        @Override
        public boolean addAll(int index, Collection<? extends E> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            this.queue.clear();
        }

        @Override
        public boolean contains(Object o) {
            return this.queue.contains(o);
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return this.queue.containsAll(c);
        }

        @Override
        public E get(int index) {
            return this.queue.get(index);
        }

        @Override
        public boolean isEmpty() {
            return this.queue.isEmpty();
        }

        @Override
        public Iterator<E> iterator() {
            return this.queue.iterator();
        }

        @Override
        public int lastIndexOf(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ListIterator<E> listIterator() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ListIterator<E> listIterator(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object o) {
            return this.queue.remove(o);
        }

        @Override
        public E remove(int index) {
            return this.queue.remove(index);
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return this.queue.removeAll(c);
        }

        @Override
        @SuppressWarnings("unchecked")
        public int indexOf(Object o) {
            return this.queue.indexOf((E) o);
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return this.queue.retainAll(c);
        }

        @Override
        public E set(int index, E element) {
            return this.queue.set(index, element);
        }

        @Override
        public int size() {
            return this.queue.size();
        }

        @Override
        public List<E> subList(int fromIndex, int toIndex) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object[] toArray() {
            return this.queue.toArray();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return this.queue.toArray(a);
        }
    }

}