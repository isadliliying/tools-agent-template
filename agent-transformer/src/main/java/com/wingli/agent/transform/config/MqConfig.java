package com.wingli.agent.transform.config;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * 配置类
 */
public class MqConfig implements Serializable {

    /**
     * 是否启用cbb的mq组件的消费者
     */
    private boolean disableMqListener = false;

    /**
     * 重写 消费者 的 groupId
     */
    private String consumerGroupId = "";

    /**
     * 禁用的topic列表
     */
    private List<String> disableTopicList = new LinkedList<>();

    /**
     * 仅启用的topic
     */
    private List<String> onlyEnableTopicList = new LinkedList<>();


    /**
     * 映射的topic列表
     */
    private List<ReplaceTopic> replaceTopicList = new LinkedList<>();


    public boolean isDisableMqListener() {
        return disableMqListener;
    }

    public void setDisableMqListener(boolean disableMqListener) {
        this.disableMqListener = disableMqListener;
    }

    public String getConsumerGroupId() {
        return consumerGroupId;
    }

    public void setConsumerGroupId(String consumerGroupId) {
        this.consumerGroupId = consumerGroupId;
    }

    public List<String> getDisableTopicList() {
        return disableTopicList;
    }

    public void setDisableTopicList(List<String> disableTopicList) {
        this.disableTopicList = disableTopicList;
    }

    public List<String> getOnlyEnableTopicList() {
        return onlyEnableTopicList;
    }

    public void setOnlyEnableTopicList(List<String> onlyEnableTopicList) {
        this.onlyEnableTopicList = onlyEnableTopicList;
    }

    public List<ReplaceTopic> getReplaceTopicList() {
        return replaceTopicList;
    }

    public void setReplaceTopicList(List<ReplaceTopic> replaceTopicList) {
        this.replaceTopicList = replaceTopicList;
    }
}
