
package org.jfantasy.framework.io;

public class View extends AbstractBuffer {
    private static final int ACCESS_1 = 1;
    private static final int ACCESS_2 = 2;
    Buffer buffer;

    public View(Buffer buffer, int mark, int get, int put, int access) {
        super(access, !buffer.isImmutable());
        this.buffer = buffer.buffer();
        setPutIndex(put);
        setGetIndex(get);
        setMarkIndex(mark);
        this._access = access;
    }

    public View(Buffer buffer) {
        super(2, !buffer.isImmutable());
        this.buffer = buffer.buffer();
        setPutIndex(buffer.putIndex());
        setGetIndex(buffer.getIndex());
        setMarkIndex(buffer.markIndex());
        this._access = buffer.isReadOnly() ? ACCESS_1 : ACCESS_2;
    }

    public View() {
        super(ACCESS_2, true);
    }

    public void update(Buffer buffer) {
        this._access = ACCESS_2;
        this.buffer = buffer.buffer();
        setGetIndex(0);
        setPutIndex(buffer.putIndex());
        setGetIndex(buffer.getIndex());
        setMarkIndex(buffer.markIndex());
        this._access = buffer.isReadOnly() ? ACCESS_1 : ACCESS_2;
    }

    public void update(int get, int put) {
        int a = this._access;
        this._access = ACCESS_2;
        setGetIndex(0);
        setPutIndex(put);
        setGetIndex(get);
        setMarkIndex(-1);
        this._access = a;
    }

    @Override
    public byte[] array() {
        return this.buffer.array();
    }

    @Override
    public Buffer buffer() {
        return this.buffer.buffer();
    }

    @Override
    public int capacity() {
        return this.buffer.capacity();
    }

    @Override
    public void clear() {
        setMarkIndex(-1);
        setGetIndex(0);
        setPutIndex(this.buffer.getIndex());
        setGetIndex(this.buffer.getIndex());
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj) || (obj instanceof View) && (obj.equals(this)) || (super.equals(obj));
    }

    @Override
    public int hashCode() {
        return buffer.hashCode();
    }

    @Override
    public boolean isReadOnly() {
        return this.buffer.isReadOnly();
    }

    @Override
    public boolean isVolatile() {
        return true;
    }

    @Override
    public byte peek(int index) {
        return this.buffer.peek(index);
    }

    @Override
    public int peek(int index, byte[] b, int offset, int length) {
        return this.buffer.peek(index, b, offset, length);
    }

    @Override
    public Buffer peek(int index, int length) {
        return this.buffer.peek(index, length);
    }

    @Override
    public int poke(int index, Buffer src) {
        return this.buffer.poke(index, src);
    }

    @Override
    public void poke(int index, byte b) {
        this.buffer.poke(index, b);
    }

    @Override
    public int poke(int index, byte[] b, int offset, int length) {
        return this.buffer.poke(index, b, offset, length);
    }

    @Override
    public String toString() {
        if (this.buffer == null) {
            return "INVALID";
        }
        return super.toString();
    }

    public static class CaseInsensitive extends View implements Buffer.CaseInsensitve {

        public CaseInsensitive() {
            // Do nothing
        }

        public CaseInsensitive(Buffer buffer, int mark, int get, int put, int access) {
            super(buffer, mark, get, put, access);
        }

        public CaseInsensitive(Buffer buffer) {
            super(buffer);
        }

    }
}
