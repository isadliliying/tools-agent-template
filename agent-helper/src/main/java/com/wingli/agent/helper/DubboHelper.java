package com.wingli.agent.helper;

import com.alibaba.dubbo.config.ServiceConfig;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.spring.ServiceBean;
import com.wingli.agent.transform.helper.ApolloConfigHelper;
import com.wingli.agent.transform.config.ReplaceApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Pattern;

/**
 * dubbo隔离helper
 */
public class DubboHelper {

    private static final Logger logger = LoggerFactory.getLogger(DubboHelper.class);

    /**
     * 根据配置文件对api的group进行复写
     */
    public static String determinedGroup(String apiName) {
        List<ReplaceApi> list = ApolloConfigHelper.getDevConfig().getDubboConfig().getApiGroupList();
        if (list == null) list = Collections.emptyList();
        for (ReplaceApi replaceApi : list) {
            String regex = replaceApi.getMatchRegex();
            if (Pattern.compile(regex).matcher(apiName).matches()) {
                return replaceApi.getGroupName();
            }
        }
        return null;
    }

    /**
     * 对reference注解的值进行配置与覆盖
     */
    public static <A extends Annotation> void configReferenceAnno(A anno, AnnotatedElement annotatedElement) {
        try {
            if (anno == null) {
                return;
            }
            if (anno instanceof Reference) {
                if (anno instanceof Proxy) {
                    try {
                        Field fieldH = Proxy.class.getDeclaredField("h");
                        fieldH.setAccessible(true);
                        Object h = fieldH.get(anno);
                        Class clz = Class.forName("sun.reflect.annotation.AnnotationInvocationHandler");
                        Field fieldMemberValues = clz.getDeclaredField("memberValues");
                        fieldMemberValues.setAccessible(true);
                        Object memberValues = fieldMemberValues.get(h);
                        if (memberValues instanceof LinkedHashMap) {
                            String apiName = ((Field) annotatedElement).getType().getName();
                            String groupName = determinedGroup(apiName);
                            if (groupName != null) {
                                ((LinkedHashMap) memberValues).put("group", groupName);
                                logger.warn("setting dubbo reference. api={} groupName={}", apiName, groupName);
                            }
                        }
                    } catch (NoSuchFieldException | IllegalAccessException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Throwable t) {
            logger.error("configReferenceAnno err.", t);
        }
    }

    /**
     * 对service注解的值进行配置与覆盖
     */
    public static <A extends Annotation> void configServiceAnno(ServiceConfig serviceConfig) {
        try {
            if (serviceConfig instanceof ServiceBean) {
                ServiceBean serviceBean = (ServiceBean) serviceConfig;
                //iot里边，这个是有可能为空的，所以还是需要进行判空
                if (serviceBean.getService() != null &&
                        void.class.equals(serviceBean.getService().interfaceClass())
                        && "".equals(serviceBean.getService().interfaceName())) {
                    if (serviceBean.getRef().getClass().getInterfaces().length > 0) {
                        serviceConfig.setInterface(serviceBean.getRef().getClass().getInterfaces()[0]);
                    } else {
                        return;
                    }
                }
                String apiName = serviceBean.getInterface();
                String groupName = determinedGroup(apiName);
                if (groupName != null) {
                    serviceBean.setGroup(groupName);
                    logger.warn("setting dubbo service. api={} groupName={}", apiName, groupName);
                }
            }
        } catch (Throwable t) {
            logger.error("configServiceAnno err.", t);
        }
    }

}
