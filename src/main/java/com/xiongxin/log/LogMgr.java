package com.xiongxin.log;

import com.xiongxin.file.BlockId;
import com.xiongxin.file.FileMgr;
import com.xiongxin.file.Page;

public class LogMgr {

    private FileMgr fm;
    private String logfile;
    private Page logpage;
    private BlockId currentblk;
    private int latestLSN = 0;
    private int lastSavedLSN = 0;

    /**
     * Creates the manager for the specified log file
     * If the log file does not yet exist, it is created
     * with an empty first block
     * @param fm the file manager
     * @param logfile the name of the log file
     */
    public LogMgr(FileMgr fm, String logfile) {
        this.fm = fm;
        this.logfile = logfile;
        var b = new byte[fm.blockSize()];
        logpage = new Page(b);
        var logsize = fm.length(logfile);
        if (logsize == 0)
            currentblk = appendNewBlock();
        else {
            currentblk = new BlockId(logfile, logsize - 1);
            fm.read(currentblk, logpage);
        }
    }

    public void flush(int lsn) {
        if (lsn >= lastSavedLSN)
            flush();
    }

    /**
     * 添加一个日志记录到log buffer
     * 记录包含任意的bytes数组。日志记录从由右至左依次写入。
     * 记录的size是bytes，。buffer的起始位置记录最后写入日志的记录位置
     * 这样做的目的是为了方法后面恢复时从前往后执行。
     *
     * @param logrec
     * @return
     */
    public synchronized int append(byte[] logrec) {
        var boundary = logpage.getInt(0);
        var recsize = logrec.length;
        var bytesneed = recsize + Integer.BYTES;
        if (boundary - bytesneed < Integer.BYTES) { // 日志记录需要的空间大了
            flush(); // so move to the next block
            currentblk = appendNewBlock();
            boundary = logpage.getInt(0);
        }

        var recpos = boundary - bytesneed;
        logpage.setBytes(recpos, logrec);
        logpage.setInt(0, recpos);
        latestLSN += 1;

        return latestLSN;
    }

    private BlockId appendNewBlock() {
        var blk = fm.append(logfile);
        logpage.setInt(0, fm.blockSize());
        fm.write(blk, logpage);
        return blk;
    }

    /**
     * Write the buffer to the log file.
     */
    private void flush() {
        fm.write(currentblk, logpage);
        lastSavedLSN = latestLSN;
    }
}
