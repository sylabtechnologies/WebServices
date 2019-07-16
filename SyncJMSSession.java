/**
 * probably parametrize w/ java.lang.class AddListener.class or RemListener.class
 * 
 * and 
 * 
 * consumer.setMessageListener((My) MyClass.newInstance());
 * 
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
    
    private boolean isAddder;
    private Connection  connection;
    private Session     session;
    private MessageConsumer consumer;
    
    SyncJMSSession(ConcurrentHashMap<Integer, AircraftQueueItem> mem,
        ConnectionFactory factory, Queue queue, boolean action) throws JMSException
    {
        isAddder = action;
        memory = mem;

        connection = factory.createConnection();
        session    = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        consumer = session.createConsumer(queue);
        consumer.setMessageListener((MessageListener) new MyListener());
        connection.start();
    }

    /* probably move to MyMessageListener classes
    */

    private class MyListener implements MessageListener
    {
        @Override
        public void onMessage(Message message)
        {
            try
            {
                String jsonStr = ((TextMessage) message).getText();
                
                if (isAddder)
                {
                    AircraftQueueItem qi = gson.fromJson( jsonStr, AircraftQueueItem.class);
                    memory.put(qi.first.getId(), qi);
                }
                else
                {
                    Aircraft a = gson.fromJson( jsonStr, Aircraft.class);
                    memory.remove(a.getId());
                }
            }
            catch (Exception ex)
            {
                Logger.getLogger(SyncJMSSession.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }

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
