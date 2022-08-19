package com.wingli.agent.transform.helper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wingli.agent.transform.config.DevConfig;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 隔离配置的持有者
 * 使用静态的原因是，方便传递到 agent-helper 模块，可以直接调用
 */
public class ApolloConfigHelper {

    private static final Logger logger = LoggerFactory.getLogger(ApolloConfigHelper.class);

    private static DevConfig devConfig;

    /**
     * 读取apollo配置
     */
    private static String getApolloJsonConfig() {
        String devToolsConfigUrl = System.getProperty("devToolsConfigUrl");
        if (devToolsConfigUrl == null) {
            devToolsConfigUrl = "http://xxx.seewo.com/xxx/default/study.wingli.dev.config.json";
        }
        logger.warn("using default config url {}", devToolsConfigUrl);
        try {
            Connection.Response response = Jsoup.connect(devToolsConfigUrl)
                    .timeout(5_000)
                    .method(Connection.Method.GET)
                    .ignoreHttpErrors(true)
                    .ignoreHttpErrors(true)
                    .execute();
            return response.body();
        } catch (Exception e) {
            logger.error("http request apollo config fail! url=" + devToolsConfigUrl, e);
            return null;
        }
    }

    public static DevConfig initDevConfig() {
        String devConfigName = System.getProperty("devConfigName");
        if (devConfigName == null) {
            logger.warn("could not get config cause devConfigName property not set.");
            return null;
        }

        String apolloConfigString = getApolloJsonConfig();
        if (apolloConfigString == null) {
            logger.warn("could not get config cause request apollo fail.");
            return null;
        }
        JSONObject configObj = JSON.parseObject(apolloConfigString);
        if (configObj == null) {
            logger.warn("could not get config cause parsing config to json err.apolloConfigString={}", apolloConfigString);
            return null;
        }
        JSONObject userConfigObj = configObj.getJSONObject(devConfigName);
        if (userConfigObj == null) {
            logger.warn("could not get config cause devConfigName={} not exists and configObj={}", devConfigName, configObj);
            return null;
        }
        DevConfig devConfig = userConfigObj.toJavaObject(DevConfig.class);
        if (devConfig == null) {
            logger.warn("could not get config cause parse config to java class err. userConfigObj={}", userConfigObj);
            return null;
        }
        devConfig.setDevConfigName(devConfigName);
        ApolloConfigHelper.devConfig = devConfig;
        return devConfig;
    }

    public static DevConfig getDevConfig() {
        return devConfig;
    }

}

