/**
 * 
*/

package AircraftQueue;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.annotation.PostConstruct;
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

/** @author Dennis */
@Path("/")
public class AircraftQueueResource
{
    public static final Logger logger = Logger.getLogger(AircraftQueueResource.class.getCanonicalName());
    private static final Aircraft noData = new Aircraft(-1, null, null, null);

    private ApplicationQueue myQueue = null;
    private String myMemService = null;
    
    @Context
    private UriInfo context;

    @Context Application app;
    
    @PostConstruct
    private void init()
    {
        Map<String, Object> properties = app.getProperties();
        myQueue = (ApplicationQueue) properties.get("QUEUE");
        myMemService = (String) properties.get("MEMSVC");
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
        if ( a == null) a = noData;
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
    
}
