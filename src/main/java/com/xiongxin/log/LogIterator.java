package com.xiongxin.log;

import com.xiongxin.file.BlockId;
import com.xiongxin.file.FileMgr;
import com.xiongxin.file.Page;

import java.util.Iterator;

/**
 * 一个类提供能力可以相反的顺序遍历日志记录
 */
public class LogIterator implements Iterator<byte[]> {
    private FileMgr fm;
    private BlockId blk;
    private Page page;
    private int currentpos;
    private int boundary;

    public LogIterator(FileMgr fm, BlockId blk) {
        this.fm = fm;
        this.blk = blk;
        var b = new byte[fm.blockSize()];
        page = new Page(b);
        moveToBlock(blk);
    }


    @Override
    public boolean hasNext() {
        return currentpos < fm.blockSize() || blk.blknum() > 0;
    }

    @Override
    public byte[] next() {
        if (currentpos == fm.blockSize()) {
            blk = new BlockId(blk.filename(), blk.blknum() - 1);
            moveToBlock(blk);
        }
        var rec = page.getBytes(currentpos);
        currentpos += Integer.BYTES + rec.length;

        return rec;
    }

    private void moveToBlock(BlockId blk) {
        fm.read(blk, page);
        boundary = page.getInt(0);
        currentpos = boundary;
    }
}
