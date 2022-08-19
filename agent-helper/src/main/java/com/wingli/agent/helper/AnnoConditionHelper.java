package com.wingli.agent.helper;

import com.wingli.agent.transform.helper.AnnoConditionConfigHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.MethodMetadata;

/**
 * 注解条件helper
 */
public class AnnoConditionHelper {

    private static final Logger logger = LoggerFactory.getLogger(AnnoConditionHelper.class);

    /**
     * 判断是否应该disable掉annotation
     * bean的禁用就是通过这里来实现
     */
    public static boolean isShouldBeDisable(AnnotatedTypeMetadata metadata) {
        //class
        if (metadata instanceof ClassMetadata) {
            ClassMetadata classMetadata = (ClassMetadata) metadata;
            String className = classMetadata.getClassName();
            if (className != null && AnnoConditionConfigHolder.getDisableClasses().contains(className)) {
                logger.warn("disable annotated class={}", className);
                return true;
            }
        }

        //method
        if (metadata instanceof MethodMetadata) {
            MethodMetadata methodMetadata = (MethodMetadata) metadata;
            String methodName = methodMetadata.getMethodName();
            String className = methodMetadata.getDeclaringClassName();
            if (methodName != null && className != null) {
                if (AnnoConditionConfigHolder.getDisableMethod().contains(AnnoConditionConfigHolder.buildDisableMethodId(className, methodName))) {
                    logger.warn("disable annotated class={} method={}", className, methodName);
                    return true;
                }
            }
            String returnTypeName = methodMetadata.getReturnTypeName();
            if (returnTypeName != null && AnnoConditionConfigHolder.getDisableReturnType().contains(returnTypeName)) {
                logger.warn("disable annotated returnType={}", returnTypeName);
                return true;
            }
        }

        return false;
    }

}
