package com.mycompany.trafficservice;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.mycompany.aircraftqueue.AicraftQueue;
import com.mycompany.aircraftqueue.Aircraft;
import static com.mycompany.aircraftqueue.Util.print;

import com.google.gson.Gson;
import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.persistence.PersistenceContext;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;

/**
 * REST Web Service
 *
 * @author Dennis
 */

@Path("/")
public class TrafficResource
{
    public static final Logger logger = Logger.getLogger(TrafficResource.class.getCanonicalName());

    private Gson gson;
    private static final Aircraft noData = new Aircraft(-1, null, null, null);

    @Context
    private UriInfo context;
    
    @PostConstruct
    private void init()
    {
        gson = new Gson();
    }

    @POST
    @Path("enqueue")
    @Consumes(MediaType.APPLICATION_JSON)    
    public Response postJson(@Context Application app, InputStream is)
    {
        boolean statusOK = true;
        StringBuilder jsonBuilder = new StringBuilder();
        
        try (BufferedReader in = new BufferedReader(new InputStreamReader(is)))
        {
            String line = null;
            while ((line = in.readLine()) != null)
            {
                jsonBuilder.append(line);
            }
        }
        catch (Exception e)
        {
            print("error parsing Json");
            statusOK = false;
        }
        // Util.print(jsonBuilder.toString());

        Aircraft a = null;
        if (statusOK)
        {
            a = gson.fromJson(jsonBuilder.toString(), Aircraft.class);
            if (a.getType() == null || a.getName() == null || a.getSize() == null)
                statusOK = false;
        }
        
        if (statusOK)
        {
            AicraftQueue q = getQueue(app);
            q.enqueue(a);
            return Response.status(Response.Status.OK).entity(a.getName() + " OK").build();
        }
        else
            return Response.status(Response.Status.BAD_REQUEST).entity("not OK").build();
        
    }

    @DELETE
    @Path(value = "dequeue")
    @Produces(value = MediaType.APPLICATION_JSON)
    public String getJson(@Context Application app)
    {
        AicraftQueue q = getQueue(app);
        Aircraft a = q.dequeue();
        if ( a == null) a = noData;
        return gson.toJson( a );
    }

    private AicraftQueue getQueue(Application app)
    {
        Map<String, Object> properties = app.getProperties();
        return (AicraftQueue) properties.get("QUEUE");
    }

}
