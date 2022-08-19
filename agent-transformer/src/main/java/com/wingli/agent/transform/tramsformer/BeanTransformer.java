package com.wingli.agent.transform.tramsformer;

import com.wingli.agent.transform.helper.AnnoConditionConfigHolder;
import com.wingli.agent.transform.config.BeanConfig;
import com.wingli.agent.transform.config.ClassMethodPair;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

/**
 * bean相关的代码插桩
 * 包括配置按 被注解的类型、被注解方法的返回值、被注解类及方法 来进行禁用
 */
public class BeanTransformer implements ClassFileTransformer {

    private Instrumentation inst;

    private BeanConfig beanConfig;

    public BeanTransformer(Instrumentation inst, BeanConfig beanConfig) {
        this.inst = inst;
        this.beanConfig = beanConfig;
        //初始化相关的数据
        initConfig(beanConfig);
    }


    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        //目前是空实现，统一在PreTransformer进行处理
        return classfileBuffer;
    }

    private static void initConfig(BeanConfig beanConfig) {
        for (String skipAnnotatedClass : beanConfig.getSkipAnnotatedClasses()) {
            AnnoConditionConfigHolder.addDisableClass(skipAnnotatedClass);
        }
        for (String skipAnnoReturnType : beanConfig.getSkipAnnotatedReturnType()) {
            AnnoConditionConfigHolder.addDisableReturnType(skipAnnoReturnType);
        }
        for (ClassMethodPair classMethodPair : beanConfig.getSkipAnnotatedMethod()) {
            AnnoConditionConfigHolder.addDisableMethod(classMethodPair.getClassName(), classMethodPair.getMethodName());
        }
    }

}
