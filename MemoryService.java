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
    private ConcurrentHashMap<Integer, String> memory = new ConcurrentHashMap<>();

    @Context Application app;

    private SyncJMSSession addSession;
    private SyncJMSSession remSession;

    // inject JMS queue
    @Resource(lookup = "jms/DefaultJMSConnectionFactory", type=javax.jms.ConnectionFactory.class)
    private javax.jms.ConnectionFactory jmsConnectionFactory;
    @Resource(lookup = "jms/addMemoryQueue", type = javax.jms.Queue.class)
    private javax.jms.Queue addMemoryQueue;
    @Resource(lookup = "jms/delMemoryQueue", type = javax.jms.Queue.class)
    private javax.jms.Queue remMemoryQueue;
    
    @PostConstruct
    private void init()
    {
        Map<String, Object> properties = app.getProperties();
        memory = (ConcurrentHashMap) properties.get("MEMORY");
        
        try {
            addSession = new SyncJMSSession(memory, jmsConnectionFactory, addMemoryQueue, true);
            remSession = new SyncJMSSession(memory, jmsConnectionFactory, remMemoryQueue, false);
        } catch (JMSException ex) {
            Logger.getLogger(SyncJMSSession.class.getName()).log(Level.SEVERE, null, ex);
        }
        
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
    @Produces(MediaType.TEXT_PLAIN)
    public Response status()
    {
        if (memory.isEmpty())
            return Response.status(Response.Status.NOT_FOUND).entity("no data").build();

        synchronized(memory)
        {
            Gson gson = new Gson();
            AircraftQueueItem[] arr = new AircraftQueueItem[memory.size()];

            int current = 0;
            for (Map.Entry<Integer, String> entry : memory.entrySet())
            {
                AircraftQueueItem qi = gson.fromJson(entry.getValue(), AircraftQueueItem.class);
                arr[current] = qi;
                current++;
            }

            return Response.status(Response.Status.OK).entity(gson.toJson(arr)).build();
        }

    }
    
}
