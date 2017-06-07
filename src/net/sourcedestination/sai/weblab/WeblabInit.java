package net.sourcedestination.sai.weblab;

import java.lang.reflect.Modifier;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Registers default plugins by reading the "sai-web-plugins" context parameter from the web.xml file. 
 * The value for this parameter should be a white-space separated list of valid, loadable Java class names
 * 
 * @author jmorwick
 *
 */
@WebListener 
public class WeblabInit implements ServletContextListener { 

    public void contextInitialized(ServletContextEvent sce) { 
    	// register plugin classes
        String pluginsParam = sce.getServletContext().getInitParameter("sai-weblab-plugins");
        if(pluginsParam == null) pluginsParam = "";
    	for(String classname: pluginsParam.split("\\s+")) {
            Class<?> plugin = null;
            try {
                plugin = Class.forName(classname); // load the class
            } catch(ClassNotFoundException e) {
                // TODO: log error
                continue;
            }
            
            // ensure the plugin class is concrete
            if(Modifier.isAbstract(plugin.getModifiers())) {
                // TODO: log error
                continue;
            }
            
            //
            // request is valid, process it by adding to the appropriate collections
            // TODO: log this
            
            Plugins.PLUGINS.add(plugin);
    	}
    } 

    public void contextDestroyed(ServletContextEvent sce) { 
    } 
}