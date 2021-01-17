package com.yiyang.demo.hbase;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class Partition implements Serializable {

    byte[] startKey;
    byte[] endKey;

    @Override
    public String toString() {
        return "Partition{" +
                "startKey=" + Bytes.toString(startKey) +
                ", endKey=" + Bytes.toString(endKey) +
                '}';
    }
}
