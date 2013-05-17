package com.arcbees.plugin.eclipse.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * When tycho is used, use maven to get this class from IDE-Templates
 */
public class FetchProperties {
    public enum Config {
        FILE, REQUIRED, TASKS, OPTIONAL;
        
        public String toString() {
            return name().toLowerCase();
        }
    }
    
    private String path;
    private PropertiesConfiguration configuration;

    public FetchProperties(String path) {
        this.path = path;
    }
    
    /**
     * TODO retry on failures
     * TODO report error
     * @throws MalformedURLException 
     * @throws ConfigurationException 
     */
    public PropertiesConfiguration fetch() throws MalformedURLException, ConfigurationException {
        URL url;
        try {
            url = new URL(path);
        } catch (MalformedURLException e) {
            throw e;
        }
        
        try {
            configuration = new PropertiesConfiguration(url);
        } catch (ConfigurationException e) {
            throw e;
        }
        
        System.out.println(configuration.toString());
        
        return configuration;
    }
    
    public PropertiesConfiguration getConfiguration() {
        return configuration;
    }
    
    public List<String> getFiles() {
        return getList(Config.FILE);
    }
    
    public List<String> getRequired() {
        return getList(Config.FILE);
    }
    
    public List<String> getTasks() {
        return getList(Config.TASKS);
    }
    
    public List<String> getOptional() {
        return getList(Config.OPTIONAL);
    }

    private List<String> getList(Config config) {
        if (configuration == null) {
            System.out.println("Did you fecth... exiting...");
            throw(new NullPointerException());
        }
        
        String key = config.toString();
        List<Object> objects = configuration.getList(key);
        
        List<String> strings = new ArrayList<String>();
        for (Object object : objects) {
            if (object != null) {
                strings.add(object.toString());
            }
        }
        return strings;
    }
}
