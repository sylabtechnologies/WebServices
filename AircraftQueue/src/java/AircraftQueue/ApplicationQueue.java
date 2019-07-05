package AircraftQueue;
import com.mycompamy.aicraftqueueitems.Aircraft;
import com.mycompamy.aicraftqueueitems.AircraftQueueItem;
import java.util.concurrent.PriorityBlockingQueue;

public class ApplicationQueue
{
    private static final int INITIAL_CAPACITY = 200;
    
    private PriorityBlockingQueue<AircraftQueueItem> queue
        = new PriorityBlockingQueue<>(INITIAL_CAPACITY,
          new com.mycompamy.aicraftqueueitems.AircraftQueueComparator());
    
    public void enqueue(Aircraft a)    
    {
        queue.put(new AircraftQueueItem(a));
    }
    
    public Aircraft dequeue()
    {
        AircraftQueueItem qi = queue.poll();
        return (qi == null) ? null : qi.first;
    }

    public int size()
    {
        return queue.size();
    }
    
    public AircraftQueueItem[] toArray()
    {
        Object[] lst = queue.toArray();
        AircraftQueueItem[] result = new AircraftQueueItem[lst.length];
        
        for (int i = 0; i < result.length; i++)
        {
            result[i] = (AircraftQueueItem) lst[i];
        }
        
        return result;
    }
    
}
