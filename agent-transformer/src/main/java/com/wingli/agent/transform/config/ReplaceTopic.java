package com.wingli.agent.transform.config;

/**
 * 配置类
 */
public class ReplaceTopic {
    /**
     * 旧的topic名称
     */
    private String oldTopic;

    /**
     * 新的topic名称
     */
    private String newTopic;

    public String getOldTopic() {
        return oldTopic;
    }

    public void setOldTopic(String oldTopic) {
        this.oldTopic = oldTopic;
    }

    public String getNewTopic() {
        return newTopic;
    }

    public void setNewTopic(String newTopic) {
        this.newTopic = newTopic;
    }
}
