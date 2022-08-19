package com.wingli.agent.transform.tramsformer;

import com.wingli.agent.transform.config.DubboConfig;
import com.wingli.agent.transform.helper.TransformerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

/**
 * dubbo隔离相关的代码插桩
 * 包括 provider、reference 的分组配置，provider的禁用
 */
public class DubboTransformer implements ClassFileTransformer {

    private static final Logger logger = LoggerFactory.getLogger(DubboTransformer.class);

    private Instrumentation inst;

    private DubboConfig dubboConfig;

    public DubboTransformer(Instrumentation inst, DubboConfig dubboConfig) {
        this.inst = inst;
        this.dubboConfig = dubboConfig;
    }


    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (className == null) return classfileBuffer;
        className = className.replace("/", ".");

        try {
            //配置 reference 的分组
            if (className.equals("org.springframework.core.annotation.AnnotationUtils")) {
                String codeSrc = TransformerHelper.unShadeIfNecessary("shaded.com.wingli.agent.helper.DubboHelper.configReferenceAnno($_,annotatedElement);");
                String methodDesc = "(Ljava/lang/reflect/AnnotatedElement;Ljava/lang/Class;)Ljava/lang/annotation/Annotation;";
                classfileBuffer = TransformerHelper.insertMethodAsFinally("getAnnotation", methodDesc, loader, classfileBuffer, codeSrc);
            }

            //配置 service 的分组
            if (className.equals("com.alibaba.dubbo.config.ServiceConfig")) {
                String codeSrc = TransformerHelper.unShadeIfNecessary("shaded.com.wingli.agent.helper.DubboHelper.configServiceAnno(this);");
                classfileBuffer = TransformerHelper.insertMethodAsFinally("setRef", loader, classfileBuffer, codeSrc);
            }

            //处理 provider 的暴露
            if (className.equals("com.alibaba.dubbo.config.ServiceConfig")) {
                if (dubboConfig.isDisableProvider()) {
                    logger.warn("disable dubbo provider!");
                    String codeSrc = "this.export = Boolean.FALSE;";
                    classfileBuffer = TransformerHelper.insertMethodBefore("export", loader, classfileBuffer, codeSrc);
                }
            }

        } catch (Throwable t) {
            logger.error("transform err.", t);
        }
        return classfileBuffer;
    }

}
