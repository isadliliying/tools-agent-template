package com.wingli.agent.transform.config;

import java.util.Collections;
import java.util.List;

/**
 * 配置类
 */
public class BeanConfig {

    private List<String> skipAnnotatedClasses = Collections.emptyList();

    private List<ClassMethodPair> skipAnnotatedMethod = Collections.emptyList();

    private List<String> skipAnnotatedReturnType = Collections.emptyList();

    public List<String> getSkipAnnotatedClasses() {
        return skipAnnotatedClasses;
    }

    public void setSkipAnnotatedClasses(List<String> skipAnnotatedClasses) {
        this.skipAnnotatedClasses = skipAnnotatedClasses;
    }

    public List<ClassMethodPair> getSkipAnnotatedMethod() {
        return skipAnnotatedMethod;
    }

    public void setSkipAnnotatedMethod(List<ClassMethodPair> skipAnnotatedMethod) {
        this.skipAnnotatedMethod = skipAnnotatedMethod;
    }

    public List<String> getSkipAnnotatedReturnType() {
        return skipAnnotatedReturnType;
    }

    public void setSkipAnnotatedReturnType(List<String> skipAnnotatedReturnType) {
        this.skipAnnotatedReturnType = skipAnnotatedReturnType;
    }
}
