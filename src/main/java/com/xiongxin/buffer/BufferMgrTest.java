package com.xiongxin.buffer;

import com.xiongxin.file.BlockId;
import com.xiongxin.file.FileMgr;
import com.xiongxin.log.LogMgr;

import java.io.File;

public class BufferMgrTest {

    public static void main(String[] args) {


        var fm = new FileMgr(new File("buffermgrtest"), 400);
        var bm = new BufferMgr(fm,new LogMgr(fm, "logtest"), 3);

        var buff = new Buffer[6];
        buff[0] = bm.pin(new BlockId("testfile", 0));
        buff[1] = bm.pin(new BlockId("testfile", 1));
        buff[2] = bm.pin(new BlockId("testfile", 2));
        bm.unpin(buff[1]); buff[1] = null;
        buff[3] = bm.pin(new BlockId("testfile", 0));
        buff[4] = bm.pin(new BlockId("testfile", 1));

        System.out.println("Available buffers: " + bm.available());

        try {
            System.out.println("Attempping to pin block 3...");
            buff[5] = bm.pin(new BlockId("testfile", 3));
        } catch (BufferAbortException e) {
            System.out.println("Exception: No available buffers");
        }

        bm.unpin(buff[2]); buff[2] = null;
        buff[5] = bm.pin(new BlockId("testfile", 3));

        System.out.println("Final Buffer Allocation:");
        for (var i = 0; i < buff.length; i++) {
            var b = buff[i];
            if (b != null) {
                System.out.println("buff["+i+"] pinned to block " + b.block());
            }
        }
    }
}
