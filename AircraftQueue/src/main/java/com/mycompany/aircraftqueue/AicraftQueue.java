package com.mycompany.aircraftqueue;
import java.util.concurrent.PriorityBlockingQueue;

public class AicraftQueue
{
    private PriorityBlockingQueue<AircraftQueueItem> queue = Util.pqueue();
    
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
    
    public Aircraft[] toArray()
    {
        Object[] lst = queue.toArray();
        Aircraft[] result = new Aircraft[lst.length];
        
        for (int i = 0; i < result.length; i++)
        {
            AircraftQueueItem qi = (AircraftQueueItem) lst[i];
            result[i] = qi.first;
        }
                
        return result;
    }
    
}
