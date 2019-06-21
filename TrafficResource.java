package com.mycompany.trafficservice;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.POST;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.mycompany.aircraftqueue.AicraftQueue;
import com.mycompany.aircraftqueue.Aircraft;
import static com.mycompany.aircraftqueue.Util.print;

import com.google.gson.Gson;
import java.io.*;
import java.util.concurrent.ExecutorService;
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
    @Context
    private UriInfo context;
    @Context
    private static Gson gson = new Gson();
    private static final Aircraft noData = new Aircraft(-1, null, null, null);
    private AicraftQueue queue;

    public TrafficResource()
    {
        queue = new AicraftQueue();
    }

    @POST
    @Path("enqueue")
    @Consumes(MediaType.APPLICATION_JSON)    
    public Response postJson(InputStream is)
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
        // print(jsonBuilder.toString());

        Aircraft a = null;
        if (statusOK)
        {
            a = gson.fromJson(jsonBuilder.toString(), Aircraft.class);
            if (a.getType() == null || a.getName() == null || a.getSize() == null)
                statusOK = false;
        }
        
        if (!statusOK)
            return Response.status(Response.Status.BAD_REQUEST).entity("not OK").build();
        else
        {
            queue.enqueue(a);
            return Response.status(Response.Status.OK).entity(a.getName() + " OK").build();
        }
        
    }

    @DELETE
    @Path(value = "dequeue")
    @Produces(value = MediaType.APPLICATION_JSON)
    public String getJson()
    {
        Aircraft a = queue.dequeue();
        if ( a == null)
            return "EMPTY";
        else
            return gson.toJson( a );
    }

}
