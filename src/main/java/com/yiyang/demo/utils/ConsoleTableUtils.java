package com.yiyang.demo.utils;

import github.clyoudu.consoletable.ConsoleTable;
import github.clyoudu.consoletable.table.Cell;

import java.util.ArrayList;
import java.util.List;

public class ConsoleTableUtils {

    public static void main(String[] args) {
        String headers = "Label,# 样本,平均值,中位数,90% 百分位,95% 百分位,99% 百分位,最小值,最大值,异常 %,吞吐量,接收 KB/sec,标准偏差";
        String bodies = "HTTP Request,1,62,62,62,62,62,62,62,0.00%,16.1,2.6,0.00";
        String totalBodies = "总体,1,62,62,62,62,62,62,62,0.00%,16.1,2.6,0.00";

        List<Cell> headerList = new ArrayList<>();
        for (String header : headers.split(",")) {
            headerList.add(new Cell(header));
        }

        List<Cell> bodyList = new ArrayList<>();
        for (String body : bodies.split(",")) {
            headerList.add(new Cell(body));
        }

        List<Cell> totalBodyList = new ArrayList<>();
        for (String body : totalBodies.split(",")) {
            totalBodyList.add(new Cell(body));
        }

        List<List<Cell>> content = new ArrayList<>();
        content.add(bodyList);
        content.add(totalBodyList);


        ConsoleTable build = new ConsoleTable.ConsoleTableBuilder().addHeaders(headerList).addRows(content).build();
        System.out.println(build.toString());

    }

    public static int getMaxLength(String[] cols) {
        int max = cols[0].length();
        for (int i = 1; i < cols.length; i++) {
            max = Math.max(max, cols[i].length());
        }
        return max;
    }
}
