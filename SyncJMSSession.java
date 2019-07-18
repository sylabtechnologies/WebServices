/**
 * try 
 *
    SyncJMSSession addSession = new SyncJMSSession(
        memory,
        factory, 
        addQueue,
        (msg)-> { add(fromJson(msg));}
        );

    SyncJMSSession delSession = new SyncJMSSession(
        memory,
        factory, 
        addQueue,
        (msg)-> { delete(fromJson(msg));}
        );

 */

package memservice;

import java.util.concurrent.ConcurrentHashMap;
import javax.jms.*;
import com.google.gson.Gson;
import com.mycompamy.aicraftqueueitems.AircraftQueueItem;
import com.mycompamy.aicraftqueueitems.Aircraft;
import java.util.logging.Level;
import java.util.logging.Logger;

class SyncJMSSession
{
    private ConcurrentHashMap<Integer, AircraftQueueItem> memory;
    private Gson gson = new Gson();
    
    private Connection  connection;
    private Session     session;
    private MessageConsumer consumer;
    
    SyncJMSSession(ConcurrentHashMap<Integer, AircraftQueueItem> mem,
        ConnectionFactory factory, Queue queue, MessageListener myListener) throws JMSException
    {
        memory = mem;
        connection = factory.createConnection();
        session    = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        consumer = session.createConsumer(queue);
        consumer.setMessageListener((MessageListener) new MyListener());
        connection.start();
    }

    AircraftQueueItem fromJson(Message message)
    {
        try
        {
            String jsonStr = ((TextMessage) message).getText();
            return gson.fromJson( jsonStr, AircraftQueueItem.class);
        }
        catch (Exception ex)
        {
            Logger.getLogger(SyncJMSSession.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void add(AircraftQueueItem qi) {memory.put(qi.first.getId(), qi);}
    void delete(AircraftQueueItem qi) {memory.remove(qi.first.getId());}

    void close()
    {
        try {
            if (session != null) session.close();
            if (connection != null) connection.close();
        } catch (JMSException ex)
        {
            Logger.getLogger(SyncJMSSession.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
