package com.wingli.agent.transform.config;

import java.util.List;

/**
 * 配置类
 */
public class DubboConfig {

    private boolean disableProvider;

    private List<ReplaceApi> apiGroupList;

    public boolean isDisableProvider() {
        return disableProvider;
    }

    public void setDisableProvider(boolean disableProvider) {
        this.disableProvider = disableProvider;
    }

    public List<ReplaceApi> getApiGroupList() {
        return apiGroupList;
    }

    public void setApiGroupList(List<ReplaceApi> apiGroupList) {
        this.apiGroupList = apiGroupList;
    }
}
