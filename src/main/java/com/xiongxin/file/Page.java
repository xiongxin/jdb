package com.xiongxin.file;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

// Page 映射 一个block
public class Page {
    private final ByteBuffer bb;
    public final static Charset CHARSET = StandardCharsets.US_ASCII;

    // For creating data buffers
    public Page(int blocksize) {
        bb = ByteBuffer.allocateDirect(blocksize);
    }

    // For creating log pages
    public Page(byte[] bytes) {
        bb = ByteBuffer.wrap(bytes);
    }

    public int getInt(int offset) {
        return bb.getInt(offset);
    }

    public void setInt(int offset, int n) {
        bb.putInt(offset, n);
    }

    public byte[] getBytes(int offset) {
        bb.position(offset);
        var length = bb.getInt();
        var b = new byte[length];
        bb.get(b);

        return b;
    }

    public void setBytes(int offset, byte[] bytes) {
        bb.position(offset);
        bb.putInt(bytes.length);
        bb.put(bytes);
    }

    public String getString(int offset) {
        byte[] b = getBytes(offset);

        return new String(b, CHARSET);
    }

    public void setString(int offset, String s) {
        setBytes(offset, s.getBytes(CHARSET));
    }

    public static int maxLength(int strlen) {
        var bytesPerChar = CHARSET.newEncoder().maxBytesPerChar();
        return Integer.BYTES + (strlen * (int) bytesPerChar);
    }

    // a package private, needed by FileMgr
    ByteBuffer contents() {
        bb.position(0);
        return bb;
    }
}
