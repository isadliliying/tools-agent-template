package com.wingli.agent.helper;

import com.wingli.agent.transform.config.MqConfig;
import com.wingli.agent.transform.config.ReplaceTopic;
import com.wingli.agent.transform.helper.ApolloConfigHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * mq隔离helper
 */
public class MqHelper {

    private static final Logger logger = LoggerFactory.getLogger(MqHelper.class);

    /**
     * 配置mq的listener
     * 已屏蔽敏感信息
     */
    public static void configMqListener(List<Object> listenerList) {
        MqConfig mqConfig = ApolloConfigHelper.getDevConfig().getMqConfigObj();
        if (mqConfig == null) {
            return;
        }

        //先把topic的映射转换成map
        Map<String, String> topicReplaceMap = new HashMap<>();
        if (mqConfig.getReplaceTopicList() != null) {
            for (ReplaceTopic replaceTopic : mqConfig.getReplaceTopicList()) {
                topicReplaceMap.put(replaceTopic.getOldTopic(), replaceTopic.getNewTopic());
            }
        }
        //遍历listener （已屏蔽敏感信息）
//        Iterator<DefaultMessageListener> it = listenerList.iterator();
//        while (it.hasNext()) {
//            DefaultMessageListener message = it.next();
//            String topic = message.getTopic();
//
//            //处理 enable
//            if (mqConfig.getOnlyEnableTopicList() == null || mqConfig.getOnlyEnableTopicList().isEmpty()) {
//                //处理disable
//                if (mqConfig.getDisableTopicList().contains(topic)) {
//                    logger.warn("disable mq listener topic={}", topic);
//                    it.remove();
//                    continue;
//                }
//            } else {
//                //如果不包含，则直接禁用
//                if (!mqConfig.getOnlyEnableTopicList().contains(topic)) {
//                    logger.warn("disable mq listener topic={}", topic);
//                    it.remove();
//                    continue;
//                }
//            }
//
//            //处理topic替换
//            if (topicReplaceMap.containsKey(topic)) {
//                String newTopic = topicReplaceMap.get(topic);
//                message.setTopic(newTopic);
//                logger.warn("replace mq listener oldTopic={} newTopic={}", topic, newTopic);
//            }
//        }

    }

}
