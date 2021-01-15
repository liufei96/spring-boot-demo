package com.yiyang.demo.utils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class HBaseUtils {

    Logger logger = LoggerFactory.getLogger(HBaseUtils.class);

    private Connection connection;
    private static ExecutorService pool = Executors.newScheduledThreadPool(20);    //设置连接池
    private static HBaseUtils instance = null;
    private static Admin admin = null;

    /**
     * 建立连接
     *
     * @param config
     */
    public HBaseUtils(Configuration config) {
        try {
            connection = ConnectionFactory.createConnection(config, pool);
            admin = connection.getAdmin();
        } catch (IOException e) {
            logger.error("建立连接HBase数据库失败", e);
        }
    }

    /**
     * 创建表空间
     *
     * @param namespace
     */
    public void createNamespace(String namespace) {
        try (Admin admin = connection.getAdmin()) {
            NamespaceDescriptor desc = NamespaceDescriptor.create(namespace).build();
            admin.createNamespace(desc);
        } catch (IOException e) {
            logger.error("HBase数据库创建表空间失败", e);
        }
    }

    /**
     * 创建表名
     *
     * @param tableName
     * @param columnFamily
     */
    public void createTable(String tableName, List<String> columnFamily) {
        try (Admin admin = connection.getAdmin()) {
            List<ColumnFamilyDescriptor> cfDescriptor = columnFamily.stream()
                    .map(e -> ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes(e)).build())
                    .collect(Collectors.toList());
            TableDescriptor tableDescriptor = TableDescriptorBuilder.newBuilder(TableName.valueOf(tableName)).setColumnFamilies(cfDescriptor).build();
            //判断表名是否存在
            if (!admin.tableExists(TableName.valueOf(tableName))) {
                admin.createTable(tableDescriptor);
            } else {
                logger.info("hbase数据库表已经存在，{}", tableName);
            }
        } catch (IOException e) {
            logger.error("HBase数据库创建表名失败", e);
        }
    }

    /**
     * 向表put数据,单行单列族-多列多值
     *
     * @param tableName 表名
     * @param rowKey    列簇
     * @param data      Map ：data.put("cf1", dataMap);
     */
    public void save(String tableName, String rowKey, Map<String, Map<String, String>> data) {
        try {
            Table table = connection.getTable(TableName.valueOf(tableName));
            List<Put> puts = new ArrayList<>();
            data.entrySet().forEach(e -> e.getValue().entrySet().forEach(ee -> {
                Put put = new Put(Bytes.toBytes(rowKey));
                put.addColumn(Bytes.toBytes(e.getKey()), Bytes.toBytes(ee.getKey()), Bytes.toBytes(ee.getValue()));
                puts.add(put);
            }));
            table.put(puts);
        } catch (Exception e) {
            logger.error("HBase数据库向表put数据失败", e);
        }
    }

    /**
     * 插入记录（单行单列族-多列多值）
     *
     * @param tableName     表名
     * @param row           行名
     * @param columnFamily 列族名
     * @param columns       列名（数组）
     * @param values        值（数组）（且需要和列一一对应）
     */
    public void insertRecords(String tableName, String row, String columnFamily, String[] columns, String[] values) throws IOException {
        TableName name = TableName.valueOf(tableName);
        Table table = connection.getTable(name);
        Put put = new Put(Bytes.toBytes(row));
        for (int i = 0; i < columns.length; i++) {
            put.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(columns[i]), Bytes.toBytes(values[i]));
            table.put(put);
        }
    }

    /**
     * 插入记录（单行单列族-单列单值）
     *
     * @param tableName    表名
     * @param row          行名
     * @param columnFamily 列族名
     * @param column       列名
     * @param value        值
     */
    public void insertOneRecord(String tableName, String row, String columnFamily, String column, String value) throws IOException {
        TableName name = TableName.valueOf(tableName);
        Table table = connection.getTable(name);
        Put put = new Put(Bytes.toBytes(row));
        put.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(column), Bytes.toBytes(value));
        table.put(put);
    }

    /**
     * 查询一行数组
     *
     * @param tableName 表名
     * @param rowKey    行
     * @return
     */
    public Map<String, String> getByRowKey(String tableName, String rowKey) {
        Map<String, String> map = new HashMap<>();
        try {
            Table table = connection.getTable(TableName.valueOf(tableName));
            Get get = new Get(Bytes.toBytes(rowKey));
            Result result = table.get(get);
            for (Cell c : result.rawCells()) {
                map.put(Bytes.toString(CellUtil.cloneQualifier(c)), Bytes.toString(CellUtil.cloneValue(c)));
            }

        } catch (Exception e) {
            logger.error("HBase数据库根据表名和列簇名查询数据失败", e);
        }
        return map;
    }

    /**
     * 查找单行单列族单列记录
     *
     * @param tablename    表名
     * @param rowKey       行名
     * @param columnFamily 列族名
     * @param column       列名
     * @return
     */
    public String selectValue(String tablename, String rowKey, String columnFamily, String column) throws IOException {
        TableName name = TableName.valueOf(tablename);
        Table table = connection.getTable(name);
        Get g = new Get(rowKey.getBytes());
        g.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(column));
        Result rs = table.get(g);
        return Bytes.toString(rs.value());
    }

    /**
     * 查询表中所有行（Scan方式）
     *
     * @param tablename
     * @return
     */
    public List scanAllRecord(String tablename) throws IOException {
        List list = new ArrayList();
        TableName name = TableName.valueOf(tablename);
        Table table = connection.getTable(name);
        Scan scan = new Scan();
        ResultScanner scanner = table.getScanner(scan);
        try {
            for (Result result : scanner) {
                for (Cell cell : result.rawCells()) {
                    String rowKey = Bytes.toString(CellUtil.cloneRow(cell));
                    String family = Bytes.toString(CellUtil.cloneFamily(cell));
                    String qualifier = Bytes.toString(CellUtil.cloneQualifier(cell));
                    String value = Bytes.toString(CellUtil.cloneValue(cell));
                    list.add(rowKey + "\t" + family + ":" + qualifier + "\t" + value);
                }
            }
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }

        return list;
    }

    /**
     * 删除一行记录
     *
     * @param rowKey 行
     * @throws IOException
     */
    public void deleteRow(String tableName, String rowKey) throws IOException {
        Table table = connection.getTable(TableName.valueOf(tableName));
        Delete delete = new Delete(rowKey.getBytes());
        table.delete(delete);
    }

    /**
     * 删除单行单列族记录
     *
     * @param tablename    表名
     * @param rowkey       行名
     * @param columnFamily 列族名
     */
    public void deleteColumnFamily(String tablename, String rowkey, String columnFamily) throws IOException {
        TableName name = TableName.valueOf(tablename);
        Table table = connection.getTable(name);
        Delete d = new Delete(rowkey.getBytes()).addFamily(Bytes.toBytes(columnFamily));
        table.delete(d);
    }

    /**
     * 删除单行单列族单列记录
     *
     * @param tablename    表名
     * @param rowkey       行
     * @param columnFamily 列族名
     * @param column       列名
     */
    public void deleteColumn(String tablename, String rowkey, String columnFamily, String column) throws IOException {
        TableName name = TableName.valueOf(tablename);
        Table table = connection.getTable(name);
        Delete d = new Delete(rowkey.getBytes()).addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(column));
        table.delete(d);
    }

    /**
     * 删除单行单列族多列记录
     *
     * @param tablename    表名
     * @param rowkey       行
     * @param columnFamily 列簇名
     * @param columns      列名数组
     * @throws IOException
     */
    public void deleteColumns(String tablename, String rowkey, String columnFamily, String[] columns) throws IOException {
        TableName tableName = TableName.valueOf(tablename);
        Table table = connection.getTable(tableName);
        for (int i = 0; i < columns.length; i++) {
            new Delete(rowkey.getBytes()).addColumns(Bytes.toBytes(columnFamily), Bytes.toBytes(columns[i]));
        }
    }
}
