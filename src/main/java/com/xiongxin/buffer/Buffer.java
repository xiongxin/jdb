package com.xiongxin.buffer;

import com.xiongxin.file.BlockId;
import com.xiongxin.file.FileMgr;
import com.xiongxin.file.Page;
import com.xiongxin.log.LogMgr;

public class Buffer {
    // fm lm buffer包括是日志，数据
    private FileMgr fm;
    private LogMgr lm;
    private Page contents;
    private BlockId blk = null;
    private int pins = 0;
    private int txnum = -1;
    private int lsn = -1;

    public Buffer(FileMgr fm, LogMgr lm) {
        this.fm = fm;
        this.lm = lm;
        contents = new Page(fm.blockSize());
    }

    public Page contents() {
        return contents;
    }

    public BlockId block() {
        return blk;
    }

    public void setModified(int txnum, int lsn) {
        this.txnum = txnum;
        if (lsn >= 0) {
            this.lsn = lsn;
        }
    }

    public boolean isPinned() {
        return pins > 0;
    }

    public int modifyingTx() {
        return txnum;
    }

    void assignToBlock(BlockId blockId) {
        flush();
        blk = blockId;
        fm.read(blk, contents);
        pins = 0;
    }

    /**
     * Write the buffer to its disk block if it is dirty.
     */
    void flush() {
        if (txnum >= 0) {
            lm.flush(lsn);
            fm.write(blk, contents);
            txnum = -1;
        }
    }

    void pin() {
        pins++;
    }

    void unpin() {
        pins--;
    }
}
