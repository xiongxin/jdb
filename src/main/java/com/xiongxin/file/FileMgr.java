package com.xiongxin.file;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class FileMgr {

    private File dbDirectory;
    private int blocksize;
    private boolean isNew;
    private Map<String, RandomAccessFile> openFiles = new HashMap<>();

    public FileMgr(File dbDirectory, int blocksize) {
        this.dbDirectory = dbDirectory;
        this.blocksize = blocksize;
        isNew = !dbDirectory.exists();

        // create the directory if the database is new
        if (isNew) {
            if (!dbDirectory.mkdirs()) {
                System.out.println("数据库文件夹创建失败");
                System.exit(64);
            }
        }

        // remove any leftover temporary tables
        for (var filename : dbDirectory.list()) {
            if (filename.startsWith("temp")) {
                new File(dbDirectory, filename).delete();
            }
        }
    }

    // 读取一个block到page内存中
    public synchronized void read(BlockId blk, Page page) {
        try {
            var f = getFile(blk.filename());
            f.seek(blk.blknum() * blocksize); //指定到任意block的位置
            f.getChannel().read(page.contents());
        } catch (IOException e) {
            throw new RuntimeException("cannot read block " + blk);
        }
    }

    public synchronized void write(BlockId blockId, Page page) {
        try {
            var f = getFile(blockId.filename());
            f.seek(blocksize * blockId.blknum());
            f.getChannel().write(page.contents());
        } catch (IOException e) {
            throw new RuntimeException("cannot write block" + blockId);
        }
    }

    public synchronized BlockId append(String filename) {
        var newblknum = length(filename);
        var blk = new BlockId(filename, newblknum);
        var b = new byte[blocksize];

        try {
            var f = getFile(blk.filename());
            f.seek(blk.blknum() * blocksize);
            f.write(b);
        } catch (IOException e) {
            throw new RuntimeException("cannot append block" + blk);
        }

        return blk;
    }

    public int length(String filename) {
        try {
            var f = getFile(filename);
            return (int) (f.length() / blocksize);
        } catch (IOException e) {
            throw new RuntimeException("cannot access " + filename);
        }
    }

    public boolean isNew() {
        return isNew;
    }

    public int blockSize() {
        return blocksize;
    }

    private RandomAccessFile getFile(String filename) throws IOException {
        var f = openFiles.get(filename);
        if (f == null) {
            var dbTable = new File(dbDirectory, filename);
            f = new RandomAccessFile(dbTable, "rws");
            openFiles.put(filename, f);
        }

        return f;
    }


}
