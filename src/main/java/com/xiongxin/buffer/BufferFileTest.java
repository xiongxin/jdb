package com.xiongxin.buffer;

import com.xiongxin.file.BlockId;
import com.xiongxin.file.FileMgr;
import com.xiongxin.file.Page;
import com.xiongxin.log.LogMgr;

import java.io.File;

public class BufferFileTest {

    public static void main(String[] args) {
        var fm = new FileMgr(new File("testfile"), 400);
        var bm = new BufferMgr(fm,new LogMgr(fm, "logtest"), 3);

        var str = "abcdefghijklm";

        var blk = new BlockId("testfile", 2);
        var pos1 = 88;

        var b1 = bm.pin(blk);
        var p1 = b1.contents();
        p1.setString(pos1, str);

        var size = Page.maxLength(str.length());
        var pos2 = pos1 + size;
        p1.setInt(pos2, 345);
        b1.setModified(1, 0);

        bm.unpin(b1);


        var b2 = bm.pin(blk);
        var p2 = b2.contents();


        System.out.println("offset " + pos2 + " contains " + p2.getInt(pos2));
        System.out.println("offset " + pos1 + " contains " + p2.getString(pos1));
        bm.unpin(b2);
    }

}
