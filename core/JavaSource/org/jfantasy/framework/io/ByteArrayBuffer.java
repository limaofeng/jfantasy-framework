package org.jfantasy.framework.io;

import org.jfantasy.framework.util.common.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class ByteArrayBuffer extends AbstractBuffer {

    private static final String  READONLY_EXCEPTION_MESSAGE = "READONLY";

    protected byte[] cacheBytes;

    protected ByteArrayBuffer(int access, boolean isVolatile) {
        super(access, isVolatile);
    }

    public ByteArrayBuffer(byte[] bytes) {
        this(bytes, 0, bytes.length, 2);
    }

    public ByteArrayBuffer(byte[] bytes, int index, int length) {
        this(bytes, index, length, 2);
    }

    public ByteArrayBuffer(byte[] bytes, int index, int length, int access) {
        super(2, false);
        this.cacheBytes = bytes;
        setPutIndex(index + length);
        setGetIndex(index);
        this._access = access;
    }

    public ByteArrayBuffer(byte[] bytes, int index, int length, int access, boolean isVolatile) {
        super(2, isVolatile);
        this.cacheBytes = bytes;
        setPutIndex(index + length);
        setGetIndex(index);
        this._access = access;
    }

    public ByteArrayBuffer(int size) {
        this(new byte[size], 0, size, 2);
        setPutIndex(0);
    }

    public ByteArrayBuffer(String value) {
        super(2, false);
        this.cacheBytes = StringUtil.getBytes(value);
        setGetIndex(0);
        setPutIndex(this.cacheBytes.length);
        this._access = 0;
        this._string = value;
    }

    public ByteArrayBuffer(String value, String encoding) throws UnsupportedEncodingException {
        super(2, false);
        this.cacheBytes = value.getBytes(encoding);
        setGetIndex(0);
        setPutIndex(this.cacheBytes.length);
        this._access = 0;
        this._string = value;
    }

    @Override
    public byte[] array() {
        return this.cacheBytes;
    }

    @Override
    public int capacity() {
        return this.cacheBytes.length;
    }

    @Override
    public void compact() {
        if (isReadOnly()){
            throw new IllegalStateException(READONLY_EXCEPTION_MESSAGE);
        }
        int s = markIndex() >= 0 ? markIndex() : getIndex();
        if (s > 0) {
            int length = putIndex() - s;
            if (length > 0) {
                System.arraycopy(this.cacheBytes, s, this.cacheBytes, 0, length);
            }
            if (markIndex() > 0){
                setMarkIndex(markIndex() - s);
            }
            setGetIndex(getIndex() - s);
            setPutIndex(putIndex() - s);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if ((obj == null) || (!(obj instanceof Buffer))) {
            return false;
        }
        if (obj instanceof Buffer.CaseInsensitve) {
            return equalsIgnoreCase((Buffer) obj);
        }

        Buffer b = (Buffer) obj;

        if (b.length() != length()) {
            return false;
        }

        if ((this._hash != 0) && (obj instanceof AbstractBuffer)) {
            AbstractBuffer ab = (AbstractBuffer) obj;
            if ((ab._hash != 0) && (this._hash != ab._hash)) {
                return false;
            }
        }

        int get = getIndex();
        int bi = b.putIndex();
        for (int i = putIndex(); i-- > get; ) {
            byte b1 = this.cacheBytes[i];
            bi--;
            byte b2 = b.peek(bi);
            if (b1 != b2){
                return false;
            }

        }
        return true;
    }

    @Override
    public boolean equalsIgnoreCase(Buffer b) {
        if (b == this) {
            return true;
        }

        if ((b == null) || (b.length() != length())) {
            return false;
        }

        if ((this._hash != 0) && (b instanceof AbstractBuffer)) {
            AbstractBuffer ab = (AbstractBuffer) b;
            if ((ab._hash != 0) && (this._hash != ab._hash)){
                return false;
            }
        }

        int get = getIndex();
        int bi = b.putIndex();
        byte[] barray = b.array();
        int i;
        if (barray == null) {
            for (i = putIndex(); i-- > get; ) {
                byte b1 = this.cacheBytes[i];
                bi--;
                byte b2 = b.peek(bi);
                if (b1 != b2) {
                    if ((97 <= b1) && (b1 <= 122)){
                        b1 = (byte) (b1 - 97 + 65);
                    }
                    if ((97 <= b2) && (b2 <= 122)){
                        b2 = (byte) (b2 - 97 + 65);
                    }
                    if (b1 != b2){
                        return false;
                    }
                }
            }
        } else {
            for (i = putIndex(); i-- > get; ) {
                byte b1 = this.cacheBytes[i];
                bi--;
                byte b2 = barray[bi];
                if (b1 != b2) {
                    if ((97 <= b1) && (b1 <= 122)){
                        b1 = (byte) (b1 - 97 + 65);
                    }
                    if ((97 <= b2) && (b2 <= 122)){
                        b2 = (byte) (b2 - 97 + 65);
                    }

                    if (b1 != b2){
                        return false;
                    }

                }
            }
        }
        return true;
    }

    @Override
    public byte get() {
        return this.cacheBytes[this._get++];
    }

    @Override
    public int hashCode() {
        if ((this._hash == 0) || (this._hashGet != this._get) || (this._hashPut != this._put)) {
            int get = getIndex();
            for (int i = putIndex(); i-- > get; ) {
                byte b = this.cacheBytes[i];
                if ((97 <= b) && (b <= 122)){
                    b = (byte) (b - 97 + 65);
                }
                this._hash = 31 * this._hash + b;
            }
            if (this._hash == 0){
                this._hash = -1;
            }
            this._hashGet = this._get;
            this._hashPut = this._put;
        }
        return this._hash;
    }

    @Override
    public byte peek(int index) {
        return this.cacheBytes[index];
    }

    @Override
    public int peek(int index, byte[] b, int offset, int length) {
        int l = length;
        if (index + l > capacity()) {
            l = capacity() - index;
            if (l == 0) {
                return -1;
            }
        }
        if (l < 0) {
            return -1;
        }
        System.arraycopy(this.cacheBytes, index, b, offset, l);
        return l;
    }

    @Override
    public void poke(int index, byte b) {
        this.cacheBytes[index] = b;
    }

    @Override
    public int poke(int index, Buffer src) {
        this._hash = 0;

        int length = src.length();
        if (index + length > capacity()) {
            length = capacity() - index;
        }

        byte[] srcArray = src.array();
        if (srcArray != null) {
            System.arraycopy(srcArray, src.getIndex(), this.cacheBytes, index, length);
        } else {
            int s = src.getIndex();
            for (int i = 0; i < length; i++) {
                this.cacheBytes[index++] = src.peek(s++);
            }
        }
        return length;
    }

    @Override
    public int poke(int index, byte[] b, int offset, int length) {
        this._hash = 0;

        if (index + length > capacity()) {
            length = capacity() - index;
        }

        System.arraycopy(b, offset, this.cacheBytes, index, length);

        return length;
    }

    public void wrap(byte[] b, int off, int len) {
        if (b == null){
            throw new IllegalArgumentException();
        }
        if (isReadOnly()){
            throw new IllegalStateException(READONLY_EXCEPTION_MESSAGE);
        }
        if (isImmutable()){
            throw new IllegalStateException("IMMUTABLE");
        }
        this.cacheBytes = b;
        clear();
        setGetIndex(off);
        setPutIndex(off + len);
    }

    public void wrap(byte[] b) {
        if (isReadOnly()){
            throw new IllegalStateException(READONLY_EXCEPTION_MESSAGE);
        }
        if (isImmutable()){
            throw new IllegalStateException("IMMUTABLE");
        }
        this.cacheBytes = b;
        setGetIndex(0);
        setPutIndex(b.length);
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        out.write(this.cacheBytes, getIndex(), length());
        clear();
    }

    @Override
    public int readFrom(InputStream in, int max) throws IOException {
        if ((max < 0) || (max > space())){
            max = space();
        }
        int p = putIndex();

        int len = 0;
        int total = 0;
        int available = max;
        while (total < max) {
            len = in.read(this.cacheBytes, p, available);
            if (len < 0){
                break;
            }
            if (len > 0) {
                p += len;
                total += len;
                available -= len;
                setPutIndex(p);
            }
            if (in.available() <= 0){
                break;
            }
        }
        if ((len < 0) && (total == 0)){
            return -1;
        }
        return total;
    }

    @Override
    public int space() {
        return this.cacheBytes.length - this._put;
    }

    public static class CaseInsensitive extends ByteArrayBuffer implements Buffer.CaseInsensitve {

        public CaseInsensitive(String s) {
            super(s);
        }

        public CaseInsensitive(byte[] b, int o, int l, int rw) {
            super(b, o, l, rw);
        }

    }
}
