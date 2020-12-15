package com.xiongxin.file;

import java.io.File;

public class FileTest {

    public static void main(String[] args) {
        var fileMgr = new FileMgr(new File("testdb"), 400);
        var blk = new BlockId("testfile", 2);
        var pos1 = 88;

        var p1 = new Page(fileMgr.blockSize());
        p1.setString(pos1, "abcdefghijklm");
        int size = Page.maxLength("abcdefghijklm".length());
        int pos2 = pos1 + size;
        p1.setInt(pos2, 345);
        fileMgr.write(blk, p1);

        Page p2 = new Page(fileMgr.blockSize());
        fileMgr.read(blk, p2);
        System.out.println("offset " + pos2 + " contains " + p2.getInt(pos2));
        System.out.println("offset " + pos1 + " contains " + p2.getString(pos1));
    }
}
