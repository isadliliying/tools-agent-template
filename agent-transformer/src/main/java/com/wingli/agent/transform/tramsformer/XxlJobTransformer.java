package com.wingli.agent.transform.tramsformer;

import com.wingli.agent.transform.helper.AnnoConditionConfigHolder;
import com.wingli.agent.transform.config.XxlJobConfig;
import com.wingli.agent.transform.helper.TransformerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

/**
 * xxl-job隔离相关的代码插桩
 *
 */
public class XxlJobTransformer implements ClassFileTransformer {

    private static final Logger logger = LoggerFactory.getLogger(XxlJobTransformer.class);

    private Instrumentation inst;

    private XxlJobConfig xxlJobConfig;

    public XxlJobTransformer(Instrumentation inst, XxlJobConfig xxlJobConfig) {
        this.inst = inst;
        this.xxlJobConfig = xxlJobConfig;
    }

    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (className == null) return classfileBuffer;
        className = className.replace("/", ".");

        try {

            //修改 xxl-job 注册时候的 appName
            if (className.equals("com.xxl.job.core.executor.XxlJobExecutor")) {
                String appName = xxlJobConfig.getXxlJobAppName();
                if (appName != null && !appName.equals("")) {
                    logger.warn("setting xxl-job appName={}", appName);
                    String src = "appName=\"" + appName + "\";";
                    classfileBuffer = TransformerHelper.insertMethodBefore("initExecutorServer", loader, classfileBuffer, src);
                }
            }

            //禁用 xxl-job
            if (className.equals("org.springframework.context.annotation.ConfigurationClassParser")) {
                if (xxlJobConfig.isDisableXxlJob()) {
                    AnnoConditionConfigHolder.addDisableReturnType("com.xxl.job.core.executor.XxlJobExecutor");
                }
            }

        } catch (Throwable t) {
            logger.error("transform err.", t);
        }
        return classfileBuffer;
    }

}
