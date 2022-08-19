package com.wingli.agent.transform;

import com.wingli.agent.transform.config.DevConfig;
import com.wingli.agent.transform.helper.ApolloConfigHelper;
import com.wingli.agent.transform.tramsformer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.Instrumentation;

public class Agent {

    private static final Logger logger = LoggerFactory.getLogger(Agent.class);

    /**
     * 入口
     */
    public static void premain(String agentArgs, Instrumentation inst) {
        //兼容处理多种运行环境，涉及到spring的bean加载，需要根据运行环境加以处理
        PreTransformer preTransformer = new PreTransformer(inst);
        inst.addTransformer(preTransformer);
        //仅在测试环境使用
        if (!isFat()) {
            logger.warn("current env is not fat! tools-agent-template will not working!");
            return;
        }
        //从apollo获取隔离配置
        DevConfig devConfig = ApolloConfigHelper.initDevConfig();
        if (devConfig == null) {
            logger.warn("devConfig is null! tools-agent-template will not working!");
            return;
        }

        logger.warn("using devConfigName {}!", devConfig.getDevConfigName());

        //理论上多个隔离处理使用同一个transformer也是ok滴，也推荐这样做

        //mq隔离配置
        if (devConfig.isEnableMqConfig()) {
            logger.info("tools-agent-template enable mq config!");
            MqTransformer mqTransformer = new MqTransformer(inst, devConfig.getMqConfigObj());
            inst.addTransformer(mqTransformer);
        }
        //xxl-job隔离配置
        if (devConfig.isEnableXxlConfig()) {
            logger.info("tools-agent-template enable xxl-job config!");
            XxlJobTransformer xxlJobTransformer = new XxlJobTransformer(inst, devConfig.getXxlConfigObj());
            inst.addTransformer(xxlJobTransformer);
        }
        //dubbo隔离配置
        if (devConfig.isEnableDubboConfig()) {
            logger.info("tools-agent-template enable dubbo config!");
            DubboTransformer dubboTransformer = new DubboTransformer(inst, devConfig.getDubboConfig());
            inst.addTransformer(dubboTransformer);
        }
        //bean配置
        if (devConfig.isEnableBeanConfig()) {
            logger.info("tools-agent-template enable bean config!");
            BeanTransformer beanTransformer = new BeanTransformer(inst, devConfig.getBeanConfig());
            inst.addTransformer(beanTransformer);
        }
    }

    /**
     * 根据属性及环境变量判断是否为测试运行在测试环境
     * @return true 表示当前在测试环境
     */
    private static boolean isFat() {
        String env = System.getProperty("env");
        if (env != null && !"".equals(env)) {
            return env.trim().equalsIgnoreCase("fat");
        }
        env = System.getenv("ENV");
        if (env != null && !"".equals(env)) {
            return env.trim().equalsIgnoreCase("fat");
        }
        return false;
    }

}