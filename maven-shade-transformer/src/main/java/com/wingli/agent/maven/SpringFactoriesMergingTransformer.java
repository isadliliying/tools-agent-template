package com.wingli.agent.maven;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugins.shade.relocation.Relocator;
import org.apache.maven.plugins.shade.relocation.SimpleRelocator;
import org.apache.maven.plugins.shade.resource.ReproducibleResourceTransformer;

/**
 * 合并 spring.factories
 * 参照了maven原有的属性合并
 */
public class SpringFactoriesMergingTransformer implements ReproducibleResourceTransformer {

    // Set this in pom configuration with <resource>...</resource>
    private String resource;

    private final Properties data = new Properties();

    private long time;

    /**
     * Return the data the properties being merged.
     *
     * @return the data
     */
    public Properties getData() {
        return this.data;
    }

    @Override
    public boolean canTransformResource(String resource) {
        return this.resource != null && this.resource.equalsIgnoreCase(resource);
    }

    @Override
    @Deprecated
    public void processResource(String resource, InputStream inputStream, List<Relocator> relocators)
            throws IOException {
        processResource(resource, inputStream, relocators, 0);
    }

    @Override
    public void processResource(String resource, InputStream inputStream, List<Relocator> relocators, long time)
            throws IOException {
        Properties properties = new Properties();
        properties.load(inputStream);
        inputStream.close();
        properties.forEach((name, value) -> properties.setProperty((String) name, replaceValue(relocators, (String) value)));
        properties.forEach((name, value) -> process((String) name, (String) value));

        if (time > this.time) {
            this.time = time;
        }
    }

    private String replaceValue(List<Relocator> relocators, String value) {
        for (Relocator relocator : relocators) {
            if (relocator instanceof SimpleRelocator) {
                return ((SimpleRelocator) relocator).applyToSourceContent(value);
            }
        }
        return value;
    }

    private void process(String name, String value) {
        //处理属性合并，以逗号进行分隔
        String existing = this.data.getProperty(name);
        if (existing != null) {
            List<String> list = new LinkedList<String>(Arrays.asList(existing.split(",")));
            list.add(value);
            Set<String> set = new HashSet<String>(list);
            String uniqueValue = StringUtils.join(set, ",");
            this.data.setProperty(name, uniqueValue);
        } else {
            this.data.setProperty(name, value);
        }
    }

    @Override
    public boolean hasTransformedResource() {
        return !this.data.isEmpty();
    }

    @Override
    public void modifyOutputStream(JarOutputStream os) throws IOException {
        JarEntry jarEntry = new JarEntry(this.resource);
        jarEntry.setTime(this.time);
        os.putNextEntry(jarEntry);
        this.data.store(os, "Merged by SpringFactoriesMergingTransformer");
        os.flush();
        this.data.clear();
    }

    public String getResource() {
        return this.resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

}