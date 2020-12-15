package com.xiongxin.buffer;

import com.xiongxin.file.BlockId;
import com.xiongxin.file.FileMgr;
import com.xiongxin.log.LogMgr;

import java.io.File;

public class BufferTest {

    public static void main(String[] args) {
        var fm = new FileMgr(new File("bufferfiletest"), 400);
        var bm = new BufferMgr(fm,new LogMgr(fm, "logtest"), 8);

        var buff1 = bm.pin(new BlockId("testfile", 2));
        var page = buff1.contents();

    }
}
