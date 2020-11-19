package com.xiongxin.file;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

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

    public synchronized void read(BlockId blk, Page page)
}
