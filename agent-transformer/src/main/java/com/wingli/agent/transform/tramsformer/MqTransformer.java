package com.wingli.agent.transform.tramsformer;

import com.wingli.agent.transform.helper.AnnoConditionConfigHolder;
import com.wingli.agent.transform.config.MqConfig;
import com.wingli.agent.transform.helper.TransformerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

/**
 * mq隔离相关的代码插桩
 * 包括 配置consumer的group、更改consumer监听的topic名称、禁用consumer 等
 */
public class MqTransformer implements ClassFileTransformer {

    private static final Logger logger = LoggerFactory.getLogger(MqTransformer.class);

    private Instrumentation inst;

    private MqConfig mqConfig;

    public MqTransformer(Instrumentation inst, MqConfig mqConfig) {
        this.inst = inst;
        this.mqConfig = mqConfig;
    }


    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (className == null) return classfileBuffer;
        className = className.replace("/", ".");

        try {

            //处理 consumer 的 groupId
            if (className.equals("com.seewo.xxx.ConsumerManager")) {
                String groupId = mqConfig.getConsumerGroupId();
                if (!"".equals(groupId)) {
                    logger.warn("setting mq groupId={}", groupId);
                    String codeSrc = "this.groupId = \"" + groupId + "\";";
                    classfileBuffer = TransformerHelper.insertMethodAsFinally("setGroupId", loader, classfileBuffer, codeSrc);
                }
            }

            //处理 consumer topic的映射及禁用
            if (className.equals("com.seewo.xxx.RocketMQConfiguration")) {
                String codeSrc = TransformerHelper.unShadeIfNecessary("shaded.com.wingli.agent.helper.MqHelper.configMqListener($_);");
                classfileBuffer = TransformerHelper.insertMethodAsFinally("buildListeners", loader, classfileBuffer, codeSrc);
            }

            //处理 consumer bean 的禁用
            if (className.equals("org.springframework.context.annotation.ConfigurationClassParser")) {
                if (mqConfig.isDisableMqListener()) {
                    //已屏蔽敏感信息
                    AnnoConditionConfigHolder.addDisableMethod("com.seewo.xxxx.RocketMQConfiguration", "consumerManager");
                }
            }

        } catch (Throwable t) {
            logger.error("transform err.", t);
        }
        return classfileBuffer;
    }

}
