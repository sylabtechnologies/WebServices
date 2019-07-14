package AircraftQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.*;

public class AircraftJMSSession
{
    private  Connection  connection;
    private  Session     session;
    private MessageProducer messageProducer;
    private CompletionListener jmsListener = new MyListener();
    
    // throw exception if cant create
    public AircraftJMSSession(ConnectionFactory factory, Queue queue) throws JMSException
    {
        connection = factory.createConnection();
        session    = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        messageProducer = session.createProducer(queue);
        connection.start();
    }
    
    public void sendMessage(String msg)
    {
        try
        {
            TextMessage textMessage = session.createTextMessage(msg);
            messageProducer.send(textMessage, jmsListener);
        }
        catch (JMSException ex)
        {
            Logger.getLogger(AircraftQueueResource.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static class MyListener implements CompletionListener
    {
        @Override
        public void onCompletion(Message message) {}

        @Override
        public void onException(Message message, Exception exception)
        {
            Logger.getLogger(Message.class.getName()).log(Level.SEVERE, null, exception);
        }
    }

    void close()
    {
        try {
            if (session != null) session.close();
            if (connection != null) connection.close();
        } catch (JMSException ex)
        {
            Logger.getLogger(AircraftJMSSession.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
