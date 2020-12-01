package com.xiongxin.log;

import com.xiongxin.file.FileMgr;
import com.xiongxin.file.Page;

import java.io.File;

public class LogTest {

    private static LogMgr lm;

    public static void main(String[] args) {
        var fm = new FileMgr(new File("testdb"), 400);
        lm = new LogMgr(fm, "logtest");

        printLogRecords("The initial empty log file:");  //print an empty log file
        System.out.println("done");
        createRecords(1, 35);
        printLogRecords("The log file now has these records:");
        createRecords(36, 70);
        lm.flush(65);
        printLogRecords("The log file now has these records:");

    }

    private static void printLogRecords(String msg) {
        var iter = lm.iterator();
        while (iter.hasNext()) {
            var rec = iter.next();
            var p = new Page(rec);
            var s = p.getString(0);
            int npos = Page.maxLength(s.length());
            var val = p.getInt(npos);

            System.out.print("[" + s + ", " + val + "]");
        }

        System.out.println();
    }

    private static void createRecords(int start, int end) {
        System.out.println("Creating records: ");
        for (var i = start; i <= end; i ++) {
            var rec = createLogRecord("record" + i, i + 100);
            int lsn = lm.append(rec);
            System.out.print(lsn + " ");
        }
        System.out.println();
    }

    private static byte[] createLogRecord(String s, int n) {
        var spos = 0;
        var npos = spos + Page.maxLength(s.length());
        byte[] b = new byte[npos + Integer.BYTES];
        var page = new Page(b);
        page.setString(spos, s);
        page.setInt(npos, n);
        return b;
    }
}
