// THE FACADE

package AircraftQueue;

import com.google.gson.Gson;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;

import java.io.*;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.mycompamy.aicraftqueueitems.*;
import java.time.format.DateTimeFormatter;
import javax.jms.JMSException;

@Path("/")
public class AircraftQueueResource
{
    public static final Logger logger = Logger.getLogger(AircraftQueueResource.class.getCanonicalName());
    private static final Aircraft noData = new Aircraft(-1, null, null, null);

    private Gson gson = new Gson();
    private ApplicationQueue myQueue = null;
    private AircraftJMSSession addSession;
    private AircraftJMSSession remSession;
    
    @Context
    private UriInfo context;

    @Context Application app;

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
        myQueue = (ApplicationQueue) properties.get("QUEUE");
        
        try {
            addSession = new AircraftJMSSession(jmsConnectionFactory, addMemoryQueue);
            remSession = new AircraftJMSSession(jmsConnectionFactory, remMemoryQueue);
        } catch (JMSException ex) {
            Logger.getLogger(AircraftQueueResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }    

    @PreDestroy
    private void cleanUp()
    {
        logger.log(Level.INFO, AircraftQueueResource.class.getName() + " exited");
        addSession.close();
        remSession.close();
    }
    
    
    @POST
    @Path("enqueue")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response postJson(InputStream is)
    {
        boolean statusOK = true;
        StringBuilder jsonBuilder = new StringBuilder();
        
        try (BufferedReader in = new BufferedReader(new InputStreamReader(is)))
        {
            String line = null;
            while ((line = in.readLine()) != null)
                jsonBuilder.append(line);
        }
        catch (Exception e)
        {
            writeSevereWarning(e.getMessage() + "\n -  cant parse JSON)");
            statusOK = false;
        }

        Aircraft a = null;
        if (statusOK)
        {
            a = Aircraft.fromJson(jsonBuilder.toString());
            if (a.getType() == null || a.getName() == null || a.getSize() == null)
                statusOK = false;
        }
        
        if (statusOK)
        {
            myQueue.enqueue(a);
            addSession.sendMessage(JsonTuple(a));
            return Response.status(Response.Status.OK).entity(a.getId() + " OK").build();
        }
        else
        {
            writeSevereWarning("Error parsing postJson()");
            return Response.status(Response.Status.BAD_REQUEST).entity("cant parse").build();
        }
        
    }

    // produce negative id if queue is empty
    @DELETE
    @Path(value = "dequeue")
    @Produces(value = MediaType.APPLICATION_JSON)
    public String deleteJson()
    {
        Aircraft a = myQueue.dequeue();
        if ( a == null)
            a = noData;
        else
            remSession.sendMessage(a.toJson());
        
        return a.toJson();
    }

    // http://localhost:8080/queue/resources/status
    @GET
    @Path(value = "status")
    @Produces(value = MediaType.TEXT_PLAIN)
    public String getQueue()
    {
        AircraftQueueItem[] arr = myQueue.toArray();
        
        if (arr == null || arr.length == 0) return "all landed";
        
        DateTimeFormatter fmtr = DateTimeFormatter.ofPattern("HH:mm:ss");
        StringBuilder textBuilder = new StringBuilder();
        for (int i = 0; i < arr.length; i++)
        {
            textBuilder.append(arr[i].second.format(fmtr));
            textBuilder.append(" : ");
            textBuilder.append(arr[i].first);
            textBuilder.append("\n");
        }
        
        return textBuilder.toString();
    }
    
    private void writeSevereWarning(String error)
    {
        logger.log(Level.SEVERE, error);
    }

    private String JsonTuple(Aircraft a)
    {
        AircraftQueueItem qi = new AircraftQueueItem(a);
        return gson.toJson(qi);
    }
    
}
