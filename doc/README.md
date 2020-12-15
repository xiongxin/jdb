## 13 Memory Management

## 13.1 Two Principles of Database Memory Management

内存管理的两个基本的准则：
- 最小磁盘访问
    - 阻止重复获取磁盘block (使用缓存解决，仅仅在必须的情况下才写入磁盘数据)
    - 执行计划也会选择最小磁盘获取的方法执行
- 不以来虚拟内存
    - 数据库系统有自己的buffer pool


## 13.2 Managing Log Information

日志管理在数据库系统中代表写入日志记录到日志文件。(主要用于恢复管理器)

## 13.4 Managing User Data

### 13.4.1 The Buffer manager

Buffer管理器是数据库系统代表page存放用户数据

### 13.4.2 Buffers

### 13.4.3 Buffer Replacement Strategies

#### The Naive strategy

按顺序依次获取，没有就下一个

#### 