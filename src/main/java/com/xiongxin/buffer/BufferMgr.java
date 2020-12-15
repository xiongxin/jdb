package com.xiongxin.buffer;

import com.xiongxin.file.BlockId;
import com.xiongxin.file.FileMgr;
import com.xiongxin.log.LogMgr;

public class BufferMgr {

    private Buffer[] bufferpool;
    private int numAvailable;
    private static final long MAX_TIME = 10000;

    public BufferMgr(FileMgr fm, LogMgr lm, int numbuffers) {
        bufferpool = new Buffer[numbuffers];
        numAvailable = numbuffers;
        for (var i = 0; i < numbuffers; i++) {
            bufferpool[i] = new Buffer(fm, lm);
        }
    }

    public synchronized int available() {
        return numAvailable;
    }

    /**
     * Flushes the dirty buffers modified by the specified transaction
     * @param txnum
     */
    public synchronized void flushAll(int txnum) {
        for (var buff : bufferpool) {
            if (buff.modifyingTx() == txnum) {
                buff.flush();
            }
        }
    }

    public synchronized void unpin(Buffer buffer) {
        buffer.unpin();
        if (!buffer.isPinned()) {
            numAvailable++;
            notifyAll();
        }
    }

    public synchronized Buffer pin(BlockId blockId) {
        try {
            var timestamp = System.currentTimeMillis();
            var buff = tryToPin(blockId);
            while (buff == null && !waitingTooLong(timestamp)) {
                wait(MAX_TIME);
                buff = tryToPin(blockId);
            }
            if (buff == null) {
                throw new BufferAbortException();
            }

            return buff;
        } catch (InterruptedException e) {
            throw new BufferAbortException();
        }
    }

    private Buffer tryToPin(BlockId blockId) {
        var buf = findExistingBuffer(blockId);
        if (buf == null) {
            buf = chooseUnpinnedBuffer();
            if (buf == null) {
                return null;
            }
            buf.assignToBlock(blockId);
        }

        if (!buf.isPinned()) { // assignToBlock buf.pins = 0, must unpinned.
            numAvailable--;
        }
        buf.pin();
        return buf;
    }

    private boolean waitingTooLong(long starttime) {
        return System.currentTimeMillis() - starttime > MAX_TIME;
    }

    private Buffer findExistingBuffer(BlockId blockId) {
        for (var buff: bufferpool) {
            var b = buff.block();
            if (b != null && b.equals(blockId)) {
                return buff;
            }
        }

        return null;
    }

    private Buffer chooseUnpinnedBuffer() {
        for (var buff : bufferpool) {
            if (!buff.isPinned()) {
                return buff;
            }
        }

        return null;
    }
}
