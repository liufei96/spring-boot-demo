package com.yiyang.demo.jmeter;

import org.apache.jmeter.util.JMeterUtils;

import java.io.IOException;
import java.util.Properties;
import java.util.UUID;

public class JmeterTest2 {


    /***
     * linux上打成tar包   https://www.cnblogs.com/charlypage/p/10140565.html
     *  tar -cvf apache-jmeter-5.3.tar  apache-jmeter-5.3/
     * @param args
     */
    public static void main(String[] args) {
        //extracted();
        System.out.println(isOSLinux());
    }

    private static void extracted() {
        String jemterHome = "src/main/resources/apache-jmeter-5.3";
        JMeterUtils.setJMeterHome(jemterHome);
        JMeterUtils.loadJMeterProperties(JMeterUtils.getJMeterBinDir() + "/jmeter.properties");
        String jtl = jemterHome + "/result.jtl";
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                String csvPath = jemterHome + "/result-"+ UUID.randomUUID().toString() + ".csv";
                String command = JMeterUtils.getJMeterBinDir() + "/JMeterPluginsCMD.bat --generate-csv " + csvPath + " --input-jtl " + jtl + "  --plugin-type AggregateReport ";
                try {
                    Runtime.getRuntime().exec(command);
                    //Runtime.getRuntime().exec("bash " + command);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }


    public static boolean isOSLinux() {
        Properties prop = System.getProperties();
        String os = prop.getProperty("os.name");
        if (os != null && os.toLowerCase().indexOf("linux") > -1) {
            return true;
        } else {
            return false;
        }
    }

}
