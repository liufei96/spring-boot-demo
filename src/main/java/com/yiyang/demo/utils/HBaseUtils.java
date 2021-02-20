package com.yiyang.demo.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.client.coprocessor.AggregationClient;
import org.apache.hadoop.hbase.client.coprocessor.LongColumnInterpreter;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class HBaseUtils {

    Logger logger = LoggerFactory.getLogger(HBaseUtils.class);

    private Connection connection;
    private static ExecutorService pool = Executors.newScheduledThreadPool(20);    //设置连接池
    private static HBaseUtils instance = null;
    private static Admin admin = null;
    private AggregationClient aggregationClient;

    /**
     * 建立连接
     *
     * @param config
     */
    public HBaseUtils(Configuration config) {
        try {
            connection = ConnectionFactory.createConnection(config, pool);
            aggregationClient = new AggregationClient(config);
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
        try (Table table = connection.getTable(TableName.valueOf(tableName))) {
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

    /***
     * 批量添加
     * @param tableName 表名
     * @param datas 数据
     */
    public void batchSave(String tableName, List<Map<String, Map<String, String>>> datas) {
        try (Table table = connection.getTable(TableName.valueOf(tableName))) {
            List<Put> puts = new ArrayList<>();
            datas.stream().forEach(item -> {
                item.entrySet().forEach(e -> e.getValue().entrySet().forEach(ee -> {
                    Map<String, String> value = e.getValue();
                    Put put = new Put(Bytes.toBytes(value.get("id")));
                    put.addColumn(Bytes.toBytes(e.getKey()), Bytes.toBytes(ee.getKey()), Bytes.toBytes(ee.getValue()));
                    puts.add(put);
                }));
            });
            table.put(puts);
        } catch (Exception e) {
            logger.error("HBase数据库向表put数据失败", e);
        }
    }

    /**
     * 插入记录（单行单列族-多列多值）
     *
     * @param tableName    表名
     * @param row          行名
     * @param columnFamily 列族名
     * @param columns      列名（数组）
     * @param values       值（数组）（且需要和列一一对应）
     */
    public void insertRecords(String tableName, String row, String columnFamily, String[] columns, String[] values) throws IOException {
        try (Table table = connection.getTable(TableName.valueOf(tableName))) {
            List<Put> puts = new ArrayList<>();
            for (int i = 0; i < columns.length; i++) {
                Put put = new Put(Bytes.toBytes(row));
                put.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(columns[i]), Bytes.toBytes(values[i]));
                puts.add(put);
            }
            table.put(puts);
        } catch (IOException e) {
            logger.error("HBase数据库向表put数据失败", e);
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
    public List scanAllRecord(String tablename, int size) throws IOException {
        List list = new ArrayList();
        TableName name = TableName.valueOf(tablename);
        Table table = connection.getTable(name);
        Scan scan = new Scan();
        ResultScanner scanner = table.getScanner(scan);
        try {
            for (Result result : scanner) {
                Map<String, String> map = new HashMap<>();
                for (Cell cell : result.rawCells()) {
                    if ("small_field".equals(Bytes.toString(CellUtil.cloneFamily(cell)))) {
                        map.put(Bytes.toString(CellUtil.cloneQualifier(cell)), Bytes.toString(CellUtil.cloneValue(cell)));
                    }
                }
                if (list.size() >= size) {
                    break;
                }
                list.add(map);
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


    /**
     * 数据查询代码
     *
     * @param tableName   表名
     * @param startRow    起点key
     * @param stopRow     结束key
     * @param objKey      筛选id
     * @param currentPage 当前页
     * @param pageSize    每页数量
     * @return
     * @throws IOException
     */
    public JSONObject findByConditionPage(String tableName, String startRow, String stopRow,
                                          String objKey, Integer currentPage, Integer pageSize) throws IOException {

        ResultScanner scanner = null;
        // 为分页创建的封装类对象，下面有给出具体属性
        try {
            // 计算起始页和结束页
            Integer page = (currentPage - 1) * pageSize;
            //Integer endPage = firstPage + pageSize;

            // 从表池中取出HBASE表对象
            Table table = connection.getTable(TableName.valueOf(tableName));
            // 获取筛选对象
            Scan scan = getScan(startRow, stopRow);

            // 给筛选对象放入过滤器(true标识分页,具体方法在下面)
            // scan.setFilter(packageFilters(true));
            // ---------------添加过滤查询
            // if (!StringUtils.isBlank(objKey)) {
            // FilterList filterList = new FilterList();
            // List<String> arr = new ArrayList<String>();
            // arr.add("info,tag, " + objKey);
            // for (String v : arr) { //
            // String[] s = v.split(",");
            // filterList.addFilter(new SingleColumnValueFilter(Bytes.toBytes(s[0]), Bytes.toBytes(s[1]), CompareOp.EQUAL, Bytes.toBytes(s[2])));
            // scan.setFilter(filterList);
            // }
            // }
            FilterList filterList = new FilterList();
            if (!StringUtils.isBlank(objKey)) {// key最后的值=objKey
                Filter filter = new RowFilter(CompareFilter.CompareOp.EQUAL, new RegexStringComparator(objKey));
                filterList.addFilter(filter);
            }
            // ----------------添加过滤查询
            // 缓存1000条数据
            scan.setCaching(10000);
            scan.setCacheBlocks(false);
            filterList.addFilter(new PageFilter(pageSize));
            // scan.setFilter(new PageFilter(pageSize));
            // scan.setLimit(pageSize);
//            scan.setMaxResultsPerColumnFamily(pageSize);
//            scan.setRowOffsetPerColumnFamily(page);
            scan.setFilter(filterList);
            scan.setReversed(true);
            Result r = null;
            JSONObject json = new JSONObject();
            JSONArray array = new JSONArray();
            scanner = table.getScanner(scan);
            byte[] lastRow = null;
            while ((r = scanner.next()) != null) {
                lastRow = r.getRow();
                System.out.println(Bytes.toString(lastRow));
                List<Cell> cells = r.listCells();
                JSONObject record = new JSONObject();
                for (int i = 0; i < cells.size(); i++) {
                    if ("small_field".equals(Bytes.toString(CellUtil.cloneFamily(cells.get(i))))) {
                        String key = Bytes.toString(CellUtil.cloneQualifier(cells.get(i)));
                        String value = Bytes.toString(CellUtil.cloneValue(cells.get(i)));
                        record.put(key, value);
                    }
                }
                array.add(record);
            }
            json.put("last_row", Bytes.toString(lastRow));
            json.put("data", array);
            System.out.println(array.size());
            return json;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
        return new JSONObject();
    }

    // 获取扫描器对象
    private static Scan getScan(String startRow, String stopRow) {
        Scan scan = new Scan();
        scan.withStartRow(getBytes(startRow));
        scan.withStopRow(getBytes(stopRow));
        return scan;
    }

    /* 转换byte数组 */
    public static byte[] getBytes(String str) {
        if (str == null)
            str = "";
        return Bytes.toBytes(str);
    }


    /**
     * 本地测试 3万多数据，耗时10几秒，太慢了
     * @param tableName
     * @return
     */
    public long getTotal(String tableName) {
        try {
            //提前创建connection和conf
            Admin admin = connection.getAdmin();
            TableName name = TableName.valueOf(tableName);
            //先disable表，添加协处理器后再enable表
            admin.disableTable(name);
            TableDescriptor descriptor = admin.getDescriptor(name);
            // HTableDescriptor descriptor = admin.getTableDescriptor(name);
            String coprocessorClass = "org.apache.hadoop.hbase.coprocessor.AggregateImplementation";
            if (!descriptor.hasCoprocessor(coprocessorClass)) {
                descriptor.getCoprocessorDescriptors();
            }
            admin.modifyTable(descriptor);
            admin.enableTable(name);
            //计时
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            Scan scan = new Scan();
            scan.setFilter(new RowFilter(CompareOperator.EQUAL, new RegexStringComparator("10057722_1610879039140")));
            long count = aggregationClient.rowCount(name, new LongColumnInterpreter(), scan);
            System.out.println("RowCount: " + count);
            stopWatch.stop();
            System.out.println("统计耗时：" + stopWatch.getTotalTimeMillis());
            return count;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return 0;
    }


}
