/**
 */
package AircraftQueue;

import java.util.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.servlet.ServletContext;

@javax.ws.rs.ApplicationPath("resources")
public class ApplicationConfig extends Application
{
    private ApplicationQueue queue = null;
    private String memService = null;
    
    public ApplicationConfig(@Context ServletContext servletContext)
    {
        queue = new ApplicationQueue();
        memService = servletContext.getInitParameter("PROJECT_MEMORY");
    }
    
    @Override
    public Map<String, Object> getProperties()
    {
        Map<String, Object> map = new HashMap<>();
        map.put("QUEUE", queue);
        map.put("MEMSVC", memService);
        return map;
    }
    
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(AircraftQueue.AircraftQueueResource.class);
    }
    
}
