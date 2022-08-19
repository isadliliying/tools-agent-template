package com.wingli.agent.transform.config;

/**
 * 配置类
 */
public class XxlJobConfig {

    private boolean disableXxlJob;

    private String xxlJobAppName;

    public boolean isDisableXxlJob() {
        return disableXxlJob;
    }

    public void setDisableXxlJob(boolean disableXxlJob) {
        this.disableXxlJob = disableXxlJob;
    }

    public String getXxlJobAppName() {
        return xxlJobAppName;
    }

    public void setXxlJobAppName(String xxlJobAppName) {
        this.xxlJobAppName = xxlJobAppName;
    }
}
