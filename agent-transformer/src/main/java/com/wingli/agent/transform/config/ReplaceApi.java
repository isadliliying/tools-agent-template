package com.wingli.agent.transform.config;

/**
 * 配置类
 */
public class ReplaceApi {
    /**
     * 正则
     */
    private String matchRegex;

    /**
     * 分组名
     */
    private String groupName;

    public String getMatchRegex() {
        return matchRegex;
    }

    public void setMatchRegex(String matchRegex) {
        this.matchRegex = matchRegex;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
