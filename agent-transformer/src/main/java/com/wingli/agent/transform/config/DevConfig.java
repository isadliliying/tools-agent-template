package com.wingli.agent.transform.config;

/**
 * 配置项
 */
public class DevConfig {

    /**
     * 配置名称
     */
    private String devConfigName;

    /**
     * 是否启用 mqConfig 配置
     */
    private boolean enableMqConfig;

    /**
     * mq配置
     */
    private MqConfig mqConfigObj;

    /**
     * 是否启用 xxlJob 配置
     */
    private boolean enableXxlConfig;

    /**
     * xxlJob配置
     */
    private XxlJobConfig xxlConfigObj;

    /**
     * 是否启用 dubbo 配置
     */
    private boolean enableDubboConfig;

    /**
     * dubbo 配置
     */
    private DubboConfig dubboConfig;

    /**
     * 是否启用 bean 配置
     */
    private boolean enableBeanConfig;

    /**
     * bean 配置
     */
    private BeanConfig beanConfig;

    /**
     * 是否启用 iot 配置
     */
    private boolean enableIotConfig;

    /**
     * 是否禁用 请求头增强
     */
    private boolean disableHeader = false;

    /**
     * 是否禁用 请求记录
     */
    private boolean disableRequestRecord = false;

    public boolean isDisableHeader() {
        return disableHeader;
    }

    public void setDisableHeader(boolean disableHeader) {
        this.disableHeader = disableHeader;
    }

    public boolean isDisableRequestRecord() {
        return disableRequestRecord;
    }

    public void setDisableRequestRecord(boolean disableRequestRecord) {
        this.disableRequestRecord = disableRequestRecord;
    }

    public String getDevConfigName() {
        return devConfigName;
    }

    public void setDevConfigName(String devConfigName) {
        this.devConfigName = devConfigName;
    }

    public boolean isEnableMqConfig() {
        return enableMqConfig;
    }

    public void setEnableMqConfig(boolean enableMqConfig) {
        this.enableMqConfig = enableMqConfig;
    }

    public MqConfig getMqConfigObj() {
        return mqConfigObj;
    }

    public void setMqConfigObj(MqConfig mqConfigObj) {
        this.mqConfigObj = mqConfigObj;
    }

    public boolean isEnableXxlConfig() {
        return enableXxlConfig;
    }

    public void setEnableXxlConfig(boolean enableXxlConfig) {
        this.enableXxlConfig = enableXxlConfig;
    }

    public XxlJobConfig getXxlConfigObj() {
        return xxlConfigObj;
    }

    public void setXxlConfigObj(XxlJobConfig xxlConfigObj) {
        this.xxlConfigObj = xxlConfigObj;
    }

    public boolean isEnableDubboConfig() {
        return enableDubboConfig;
    }

    public void setEnableDubboConfig(boolean enableDubboConfig) {
        this.enableDubboConfig = enableDubboConfig;
    }

    public DubboConfig getDubboConfig() {
        return dubboConfig;
    }

    public void setDubboConfig(DubboConfig dubboConfig) {
        this.dubboConfig = dubboConfig;
    }

    public boolean isEnableBeanConfig() {
        return enableBeanConfig;
    }

    public void setEnableBeanConfig(boolean enableBeanConfig) {
        this.enableBeanConfig = enableBeanConfig;
    }

    public BeanConfig getBeanConfig() {
        return beanConfig;
    }

    public void setBeanConfig(BeanConfig beanConfig) {
        this.beanConfig = beanConfig;
    }

    public boolean isEnableIotConfig() {
        return enableIotConfig;
    }

    public void setEnableIotConfig(boolean enableIotConfig) {
        this.enableIotConfig = enableIotConfig;
    }

}
