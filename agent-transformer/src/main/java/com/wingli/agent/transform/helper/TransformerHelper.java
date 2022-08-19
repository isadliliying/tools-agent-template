package com.wingli.agent.transform.helper;

import javassist.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.function.BiConsumer;

/**
 * 工具相关
 */
public class TransformerHelper {

    private static final Logger logger = LoggerFactory.getLogger(TransformerHelper.class);

    /**
     * 保存是否运行在jar in jar中的状态
     */
    private static volatile boolean isRunInFatJar;

    /**
     * 根据类加载器判断当前是否在jar in jar形式中
     */
    public static void initRunInSpringFatJar(ClassLoader loader) {
        isRunInFatJar = loader.getClass().getName().equals("org.springframework.boot.loader.LaunchedURLClassLoader");
        logger.warn("detect current classloader:{} isRunInFatJar:{}", loader.getClass().getName(), isRunInFatJar);
    }

    /**
     * 是否在jar in jar形式中运行
     */
    public static boolean isRunInSpringFatJar() {
        return isRunInFatJar;
    }

    /**
     * 为什么要特地用一个方法来unshade呢？因为shade插件进行shadow时，会替换类中定义的字符串！！！！
     */
    public static String unShadeIfNecessary(String shaded) {
        if (isRunInSpringFatJar()) {
            return shaded.replaceAll("shaded\\.(.*)", "$1");
        } else {
            return shaded;
        }
    }

    /**
     * 在方法开始前插桩
     */
    public static byte[] insertMethodBefore(String targetMethodName, ClassLoader loader, byte[] classfileBuffer, String src) {
        return insertMethodBefore(targetMethodName, null, loader, classfileBuffer, src);
    }

    /**
     * 在方法开始前插桩
     */
    public static byte[] insertMethodBefore(String targetMethodName, String desc, ClassLoader loader, byte[] classfileBuffer, String src) {
        classfileBuffer = TransformerHelper.dealMethodByName(targetMethodName, desc, loader, classfileBuffer, (ctClass, ctMethod) -> {
            try {
                ctMethod.insertBefore(src);
            } catch (CannotCompileException e) {
                e.printStackTrace();
            }
        });
        return classfileBuffer;
    }

    /**
     * 用finally包裹方法体，并插桩到finally中
     */
    public static byte[] insertMethodAsFinally(String targetMethodName, ClassLoader loader, byte[] classfileBuffer, String src) {
        return insertMethodAsFinally(targetMethodName, null, loader, classfileBuffer, src);
    }

    /**
     * 用finally包裹方法体，并插桩到finally中
     */
    public static byte[] insertMethodAsFinally(String targetMethodName, String desc, ClassLoader loader, byte[] classfileBuffer, String src) {
        classfileBuffer = TransformerHelper.dealMethodByName(targetMethodName, desc, loader, classfileBuffer, (ctClass, ctMethod) -> {
            try {
                ctMethod.insertAfter(src, true);
            } catch (CannotCompileException e) {
                e.printStackTrace();
            }
        });
        return classfileBuffer;
    }

    /**
     * 对方法进行插桩处理
     */
    public static byte[] dealMethodByName(String targetMethodName, String desc, ClassLoader loader, byte[] classfileBuffer, BiConsumer<CtClass, CtMethod> consumer) {
        CtClass ctClass = null;
        CtMethod ctMethod = null;
        try {
            //构建class pool
            ClassPool classPool = new ClassPool();
            classPool.appendSystemPath();
            if (loader != null) {
                classPool.appendClassPath(new LoaderClassPath(loader));
            }
            //匹配到对应的method
            ctClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));
            if (ctClass == null) {
                return classfileBuffer;
            }
            if (desc == null) {
                ctMethod = ctClass.getDeclaredMethod(targetMethodName);
            } else {
                ctMethod = ctClass.getMethod(targetMethodName, desc);
            }
            if (ctMethod == null) {
                return classfileBuffer;
            }
            consumer.accept(ctClass, ctMethod);

            //返回修改后的字节码
            return ctClass.toBytecode();
        } catch (Exception exp) {
            exp.printStackTrace();
        } finally {
            if (ctClass != null) {
                ctClass.detach();
            }
        }
        return classfileBuffer;
    }

}
