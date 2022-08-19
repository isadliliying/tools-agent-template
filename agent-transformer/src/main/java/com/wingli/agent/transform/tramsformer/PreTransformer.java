package com.wingli.agent.transform.tramsformer;

import com.wingli.agent.transform.helper.AnnoConditionConfigHolder;
import com.wingli.agent.transform.helper.TransformerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.loader.LaunchedURLClassLoader;
import org.springframework.boot.loader.archive.Archive;
import org.springframework.boot.loader.archive.JarFileArchive;

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.List;

/**
 * 提供基础支持的代码插桩
 * 包括 bean 的禁用、jar in jar依赖路径的添加
 */
public class PreTransformer implements ClassFileTransformer {

    private static final Logger logger = LoggerFactory.getLogger(PreTransformer.class);

    private Instrumentation inst;

    public PreTransformer(Instrumentation inst) {
        this.inst = inst;
    }

    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (className == null) return classfileBuffer;
        className = className.replace("/", ".");
        try {
            //处理bean的禁用
            if (className.equals("org.springframework.context.annotation.ConditionEvaluator")) {
                if (TransformerHelper.isRunInSpringFatJar()) {
                    //如果不禁用掉，会导致启动失败，因为 https://juejin.cn/post/7067363361368834061
                    AnnoConditionConfigHolder.addDisableClass("shaded.com.wingli.agent.helper.util.SpringContextHolder");
                }
                //其它地方也会使用这个来禁用某些bean的加载
                String clz = TransformerHelper.unShadeIfNecessary("shaded.com.wingli.agent.helper.AnnoConditionHelper");
                String codeSrc = "if (" + clz + ".isShouldBeDisable(metadata)) return true;";
                String methodDesc = "(Lorg/springframework/core/type/AnnotatedTypeMetadata;Lorg/springframework/context/annotation/ConfigurationCondition$ConfigurationPhase;)Z";
                classfileBuffer = TransformerHelper.insertMethodBefore("shouldSkip", methodDesc, loader, classfileBuffer, codeSrc);
            }
            //将相关依赖添加进classloader的搜索路径
            if (className.equals("org.springframework.boot.SpringApplication")) {
                //依赖加载 org.springframework.boot.SpringApplication 类时的类加载器来判定是否在以jar in jar形式启动，如果有实现自定义了类加载器，那这个判断会失效
                TransformerHelper.initRunInSpringFatJar(loader);
                //将 agent jar 中的 jar in jar 添加到类搜索路径，必须先执行这个，否则无法找到helper模块中的类
                if (TransformerHelper.isRunInSpringFatJar()) {
                    appendAgentNestedJars(loader);
                }
            }
            //---------------------- 其它处理逻辑 end --------------------------
        } catch (Throwable t) {
            logger.error("transform err.", t);
        }
        return classfileBuffer;
    }

    /**
     * 将 agent 的 nested jar 加入到对应的依赖中
     */
    private void appendAgentNestedJars(ClassLoader classLoader) {
        String agentJarPath = getAgentJarPath();
        if (agentJarPath == null) return;

        //LaunchedURLClassLoader 是属于 springboot-loader 的类，没有放到jar in jar里边，所以它是被AppClassLoader加载的（目前只处理LaunchedURLClassLoader，有自定义类加载器的话也可以搞）
        if (classLoader instanceof LaunchedURLClassLoader) {
            LaunchedURLClassLoader launchedURLClassLoader = (LaunchedURLClassLoader) classLoader;
            try {
                Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                method.setAccessible(true);
                //遍历 agent jar，处理所有对应目录下的jar包，使用 JarFileArchive 获取到的url才可以处理jar in jar
                JarFileArchive jarFileArchive = new JarFileArchive(new File(agentJarPath));
                List<Archive> archiveList = jarFileArchive.getNestedArchives(new Archive.EntryFilter() {
                    @Override
                    public boolean matches(Archive.Entry entry) {
                        if (entry.isDirectory()) {
                            return false;
                        }
                        return entry.getName().startsWith("BOOT-INF/lib/") && entry.getName().endsWith(".jar");
                    }
                });
                for (Archive archive : archiveList) {
                    method.invoke(launchedURLClassLoader, archive.getUrl());
                    logger.warn("add url to classloader={}. url={}", classLoader, archive.getUrl());
                }
            } catch (Throwable t) {
                logger.error("append classloader err.", t);
            }
        }

    }

    /**
     * 获取 agent jar 的路径，抄springboot的
     */
    private String getAgentJarPath() {
        ProtectionDomain protectionDomain = getClass().getProtectionDomain();
        CodeSource codeSource = protectionDomain.getCodeSource();
        URI location = null;
        try {
            location = (codeSource == null ? null : codeSource.getLocation().toURI());
        } catch (URISyntaxException e) {
            return null;
        }
        String path = (location == null ? null : location.getSchemeSpecificPart());
        if (path == null) {
            throw new IllegalStateException("Unable to determine code source");
        }
        File root = new File(path);
        if (!root.exists()) {
            throw new IllegalStateException(
                    "Unable to determine code source from " + root);
        }
        return path;
    }

}
