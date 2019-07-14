package memservice;

import java.util.concurrent.ConcurrentHashMap;
import javax.jms.*;
import com.google.gson.Gson;
import com.mycompamy.aicraftqueueitems.Aircraft;
import java.util.logging.Level;
import java.util.logging.Logger;

class SyncJMSSession
{
    private ConcurrentHashMap<Integer, String> memory;
    private Gson gson = new Gson();
    
    private boolean isAddder;
    private Connection  connection;
    private Session     session;
    private MessageConsumer consumer;
    
    SyncJMSSession(ConcurrentHashMap<Integer, String> mem,
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

    private class MyListener implements MessageListener
    {
        @Override
        public void onMessage(Message message)
        {
            try
            {
                String jsonStr = ((TextMessage) message).getText();
                Aircraft a = gson.fromJson( jsonStr, Aircraft.class);
                
                if (isAddder)
                    memory.put(a.getId(), jsonStr);
                else
                    memory.remove(a.getId());
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
