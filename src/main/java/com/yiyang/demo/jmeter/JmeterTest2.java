package com.yiyang.demo.jmeter;

import org.apache.jmeter.util.JMeterUtils;

import java.io.IOException;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class JmeterTest2 {


    /***
     * linux上打成tar包   https://www.cnblogs.com/charlypage/p/10140565.html
     *  tar -cvf apache-jmeter-5.3.tar  apache-jmeter-5.3/
     * @param args
     */
    public static void main(String[] args) {
        extracted();
        System.out.println(isOSLinux());
    }

    private static void extracted() {
        String jemterHome = "src/main/resources/apache-jmeter-5.3";
        JMeterUtils.setJMeterHome(jemterHome);
        JMeterUtils.loadJMeterProperties(JMeterUtils.getJMeterBinDir() + "/jmeter.properties");
        String jtl = jemterHome + "/result.jtl";
        String csvPath = jemterHome + "/result.csv";
        String command = JMeterUtils.getJMeterBinDir() + "/JMeterPluginsCMD.bat --generate-csv " + csvPath + " --input-jtl " + jtl + "  --plugin-type AggregateReport ";
        try {
            Process exec = Runtime.getRuntime().exec(command);
            exec.waitFor(10, TimeUnit.SECONDS);
            //Runtime.getRuntime().exec("bash " + command);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
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
