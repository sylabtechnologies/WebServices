/**
 * 
 */

package com.mycompany.memservice;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;
import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
import javax.annotation.PostConstruct;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.persistence.PersistenceContext;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;

// import com.mycompany.aircraftqueue.AicraftQueueItem;
import com.mycompany.aircraftqueue.Aircraft;
import static com.mycompany.aircraftqueue.Util.print;

import com.google.gson.Gson;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

// class AircraftQueueItem {} 

/**
 * REST Web Service
 *
 * @author Dennis
 */
@Path("memory")
public class MemoryResource
{
    public static final Logger logger = Logger.getLogger(MemoryResource.class.getCanonicalName());

    private Gson gson;
    private static final Aircraft noData = new Aircraft(-1, null, null, null);
    private ConcurrentHashMap<Integer, AircraftQueueItem> myMap;

    @Context
    private UriInfo context;
    
    @PostConstruct
    private void init()
    {
        gson = new Gson();
    }

    @POST
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)    
    public Response postJson(@Context Application app, InputStream is)
    {
        AircraftQueueItem qi = extractJson(is);
        if (qi == null)
        {
            logger.log(Level.SEVERE, "Error adding plane");
            return Response.status(Response.Status.BAD_REQUEST).entity("not OK").build();
        }

        myMap = getMap(app);
        myMap.put(qi.first.id, qi);
        return Response.status(Response.Status.OK).entity("OK").build();
    }
    
    @DELETE
    @Path("/delete")
    @Consumes(MediaType.APPLICATION_JSON)    
    public Response deleteJson(@Context Application app, InputStream is)
    {
        AircraftQueueItem qi = extractJson(is);

        if (qi == null)
        {
            logger.log(Level.SEVERE, "Error deleting plane");
            return Response.status(Response.Status.BAD_REQUEST).entity("not OK").build();
        }

        int aircraftId = qi.first.id;
        myMap = getMap(app);
        Aircraft a = myMap.remove(aircraftId).first;

        if ( a == null)
        {
            logger.log(Level.SEVERE, "Error locating plane " + aircraftId);
            return Response.status(Response.Status.BAD_REQUEST).entity("not OK").build();
        }
        else
            return Response.status(Response.Status.OK).entity("OK " + aircraftId).build();
    }

    @DELETE
    @Path("/deleteAll")
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteAll(@Context Application app)
    {
        myMap = getMap(app);
        String allData = gson.toJson( myMap );
        myMap.clear();
        return allData;
    }

    private ConcurrentHashMap<Integer, AircraftQueueItem> getMap(Application app)
    {
        Map<String, Object> properties = app.getProperties();
        return (ConcurrentHashMap<Integer, AircraftQueueItem>) properties.get("MAP");
    }

    private AircraftQueueItem extractJson(InputStream is)
    {
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
            logger.log(Level.SEVERE, "Error parsing postJson()");
            print("error parsing Json");
            return null;
        }
    
        return gson.fromJson(jsonBuilder.toString(), AircraftQueueItem.class);
    }

}
