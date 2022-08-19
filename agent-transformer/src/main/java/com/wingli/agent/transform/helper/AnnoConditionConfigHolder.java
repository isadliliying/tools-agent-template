package com.wingli.agent.transform.helper;

import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 注解条件的配置持有者
 * 使用静态的原因是，方便传递到 agent-helper 模块，可以直接调用
 */
public class AnnoConditionConfigHolder {

    private static CopyOnWriteArraySet<String> disableClasses = new CopyOnWriteArraySet<>();

    private static CopyOnWriteArraySet<String> disableMethod = new CopyOnWriteArraySet<>();

    private static CopyOnWriteArraySet<String> disableReturnType = new CopyOnWriteArraySet<>();

    public static void addDisableClass(String className) {
        disableClasses.add(className);
    }

    public static void addDisableMethod(String className, String method) {
        disableMethod.add(buildDisableMethodId(className, method));
    }

    public static void addDisableReturnType(String returnType){
        disableReturnType.add(returnType);
    }

    public static String buildDisableMethodId(String className, String method) {
        return className + "#" + method;
    }

    public static CopyOnWriteArraySet<String> getDisableClasses() {
        return disableClasses;
    }

    public static CopyOnWriteArraySet<String> getDisableMethod() {
        return disableMethod;
    }

    public static CopyOnWriteArraySet<String> getDisableReturnType() {
        return disableReturnType;
    }
}
