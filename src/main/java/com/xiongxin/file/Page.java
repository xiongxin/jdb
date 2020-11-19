package com.xiongxin.file;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

// Page 映射 一个block
public class Page {
    private ByteBuffer bb;
    public static Charset CHARSET = StandardCharsets.US_ASCII;

    // For creating data buffers
    public Page(int blocksize) {
        bb = ByteBuffer.allocateDirect(blocksize);
    }
}
