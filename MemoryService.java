/*
 * 
 */
package memservice;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.logging.Level;

import javax.ws.rs.core.Context;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import javax.annotation.PostConstruct;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.PathParam;
import javax.annotation.Resource;
import javax.jms.JMSException;

import com.google.gson.Gson;
import com.mycompamy.aicraftqueueitems.AircraftQueueItem;
import javax.annotation.PreDestroy;

/**
 * REST Web Service
 */
@Path("/")
public class MemoryService
{
    public static final Logger logger = Logger.getLogger(MemoryService.class.getCanonicalName());
    private ConcurrentHashMap<Integer, AircraftQueueItem> memory = new ConcurrentHashMap<>();

    @Context Application app;

    @PostConstruct
    private void init()
    {
        Map<String, Object> properties = app.getProperties();
        memory = (ConcurrentHashMap) properties.get("MEMORY");
    }    

    @PreDestroy
    private void cleanUp()
    {
        logger.log(Level.INFO, MemoryService.class.getName() + " exit size: " + memory.size());
        addSession.close();
        remSession.close();
    }
    
    // memory/resources/status
    @GET
    @Path("status")
    @Produces(MediaType.APPLICATION_JSON)
    public Response status()
    {
        if (memory.isEmpty())
            return Response.status(Response.Status.NOT_FOUND).entity("no data").build();

        Gson gson = new Gson();
        AircraftQueueItem[] arr = new AircraftQueueItem[memory.size()];
        int current = 0;
        
        synchronized(memory)
        {
            for (Map.Entry<Integer, AircraftQueueItem> entry : memory.entrySet())
            {
                arr[current] = entry.getValue();
                current++;
            }
        }

        return Response.status(Response.Status.OK).entity(gson.toJson(arr)).build();
    }
    
}
