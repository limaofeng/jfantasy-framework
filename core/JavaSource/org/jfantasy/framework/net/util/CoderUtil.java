package org.jfantasy.framework.net.util;

import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * 类型名称：
 * 说明：
 * 创建人：CshBBrain;技术博客：http://cshbbrain.iteye.com/
 * 创建日期：2011-9-21
 * 修改人：
 * 修改日期：
 */
public class CoderUtil {
    private CoderUtil() {
    }

    private static final Logger LOG = Logger.getLogger(CoderUtil.class);

    public static final char BEGIN_CHAR = 0x00;// 开始字符
    public static final char END_CHAR = 0xFF;// 结束字符
    public static final String BEGIN_MSG = String.valueOf(BEGIN_CHAR);// 消息开始
    public static final String END_MSG = String.valueOf(END_CHAR); // 消息结束
    public static final String BLANK_SPACE = " ";// 空白字符串
    public static final String BLANK_LINE = "\r\n\r\n";// 空白行
    public static final String UTF8 = "utf-8";//utf-8编码
    private static Charset charset = Charset.forName("utf-8");

    /**
     * 方法名：encode
     * @param str
     * @return
     * 返回类型：ByteBuffer
     * 说明：
     * 创建人：CshBBrain;技术博客：http://cshbbrain.iteye.com/
     * 创建日期：2011-5-3
     * 修改人：
     * 修改日期：
     */
    public static ByteBuffer encode(String str) {
        return charset.encode(str);
    }

    /**
     * 方法名：toByte
     * @param str
     * @return
     * 返回类型：byte[]
     * 说明：获取字符串的utf-8编码
     * 创建人：CshBBrain;技术博客：http://cshbbrain.iteye.com/
     * 创建日期：2011-11-12
     * 修改人：
     * 修改日期：
     */
    public static byte[] toByte(String str) {
        if (str != null) {
            try {
                return str.getBytes(UTF8);
            } catch (UnsupportedEncodingException e) {
                LOG.error(e.getMessage(), e);
                return null;
            }
        }
        return null;
    }

    /**
     * 方法名：toNormalByte
     * @param str
     * @return
     * 返回类型：byte[]
     * 说明：获取平台相关的字节编码
     * 创建人：CshBBrain;技术博客：http://cshbbrain.iteye.com/
     * 创建日期：2012-9-19
     * 修改人：
     * 修改日期：
     */
    public static byte[] toNormalByte(String str) {
        if (str != null) {
            return str.getBytes();
        }
        return null;
    }

    /**
     * 方法名：decode
     * @param bb
     * @return
     * 返回类型：String
     * 说明：
     * 创建人：CshBBrain;技术博客：http://cshbbrain.iteye.com/
     * 创建日期：2011-5-3
     * 修改人：
     * 修改日期：
     */
    public static String decode(ByteBuffer bb) {
        return charset.decode(bb).toString();
    }

    public static String decode(byte[] bb) {
        try {
            return new String(bb, charset.displayName());
        } catch (UnsupportedEncodingException e) {
            LOG.error(e.getMessage(), e);
            return "";
        }
    }

    /**
     * 方法名：toLong
     * @param b
     * @return
     * 返回类型：long
     * 说明：将字节数组转换为Long型数据，高字节在前，该方法来自网络
     * 创建人：CshBBrain;技术博客：http://cshbbrain.iteye.com/
     * 创建日期：2012-9-18
     * 修改人：
     * 修改日期：
     */
    public static long toLong(byte... b) {
        int mask = 0xff;
        int temp = 0;
        long res = 0;
        int byteslen = b.length;
        if (byteslen > 8) {
            return 0L;
        }
        for (byte aB : b) {
            res <<= 8;
            temp = aB & mask;
            res |= temp;
        }
        return res;
    }

    /**
     * 方法名：toInt
     * @param b
     * @return
     * 返回类型：int
     * 说明：将字节数组转换为整数，高字节在前，该方法来自网络
     * 创建人：CshBBrain;技术博客：http://cshbbrain.iteye.com/
     * 创建日期：2012-9-18
     * 修改人：
     * 修改日期：
     */
    public static int toInt(byte... b) {
        return (int) toLong(b);
    }

    /**
     * 方法名：toShort
     * @param b
     * @return
     * 返回类型：short
     * 说明：将字节转换为短整数，高字节在前，该方法来自网络
     * 创建人：CshBBrain;技术博客：http://cshbbrain.iteye.com/
     * 创建日期：2012-9-18
     * 修改人：
     * 修改日期：
     */
    public static short toShort(byte... b) {
        return (short) toLong(b);
    }

    /**
     * 方法名：numberToByte
     * @param l
     * @param length
     * @return
     * 返回类型：byte[]
     * 说明：将数字转换为字节数组；从高位向低位取值，高位在前，该方法来自网络
     * 创建人：CshBBrain;技术博客：http://cshbbrain.iteye.com/
     * 创建日期：2012-9-19
     * 修改人：
     * 修改日期：
     */
    private static byte[] numberToByte(long l, int length) {
        byte[] bts = new byte[length];
        for (int i = 0; i < length; i++) {
            bts[i] = (byte) (l >> ((length - i - 1) * 8));
        }
        return bts;
    }

    /**
     * 方法名：shortToByte
     * @param i
     * @return
     * 返回类型：byte[]
     * 说明：短整形转换为字节数组，该方法来自网络
     * 创建人：CshBBrain;技术博客：http://cshbbrain.iteye.com/
     * 创建日期：2012-9-19
     * 修改人：
     * 修改日期：
     */
    public static byte[] shortToByte(int i) {
        return numberToByte(i, 2);
    }

    /**
     * 方法名：intToByte
     * @param i
     * @return
     * 返回类型：byte[]
     * 说明：将整数转换为字节数组，该方法来自网络
     * 创建人：CshBBrain;技术博客：http://cshbbrain.iteye.com/
     * 创建日期：2012-9-19
     * 修改人：
     * 修改日期：
     */
    public static byte[] intToByte(int i) {
        return numberToByte(i, 4);
    }

    /**
     * 方法名：longToByte
     * @param i
     * @return
     * 返回类型：byte[]
     * 说明：将长整形转换为字节数组，该方法来自网络
     * 创建人：CshBBrain;技术博客：http://cshbbrain.iteye.com/
     * 创建日期：2012-9-19
     * 修改人：
     * 修改日期：
     */
    public static byte[] longToByte(long i) {
        return numberToByte(i, 8);
    }

    /**
     * 方法名：formatBytes
     * @param bytes
     * @return
     * 返回类型：String
     * 说明：将字节数组转换为字符串，主要用于日志的输出，该方法来自网络
     * 创建人：CshBBrain;技术博客：http://cshbbrain.iteye.com/
     * 创建日期：2012-9-19
     * 修改人：
     * 修改日期：
     */
    public static String formatBytes(byte... bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 3);
        for (byte byt : bytes) {
            sb.append(String.format("%02X ", byt));
        }
        return sb.toString();
    }

    public static byte[] join(byte[] dest, byte[] orig) {
        if (orig == null || orig.length == 0) {
            return dest;
        }
        Object array = Array.newInstance(byte.class, dest.length + orig.length);
        for (int i = 0; i < dest.length; i++) {
            Array.set(array, i, dest[i]);
        }
        for (int i = 0; i < orig.length; i++) {
            Array.set(array, dest.length + i, orig[i]);
        }
        return (byte[]) array;
    }
}
