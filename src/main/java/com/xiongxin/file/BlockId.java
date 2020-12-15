package com.xiongxin.file;


public record BlockId(String filename, int blknum) {
    @Override
    public boolean equals(Object obj) {
        var blk = (BlockId) obj;
        return filename.equals(blk.filename) && blknum == blk.blknum;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString() {
        return "[file " + filename + ", blockId " + blknum + "]";
    }
}