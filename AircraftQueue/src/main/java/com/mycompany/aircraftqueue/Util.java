package com.mycompany.aircraftqueue;
import java.util.concurrent.PriorityBlockingQueue;

public class Util
{
    private final static int INITIAL_CAPACITY = 1000;
    private final static int MINIMUM_CAPACITY = 100;
    
    public static void print(String str)
    {
        System.out.println(str);
    }

    public static PriorityBlockingQueue<AircraftQueueItem> pqueue()
    {
        return pqueue(INITIAL_CAPACITY);
    }

    
    public static PriorityBlockingQueue<AircraftQueueItem> pqueue(int capacity)
    {
        if (capacity <= MINIMUM_CAPACITY) capacity = MINIMUM_CAPACITY;
        return new PriorityBlockingQueue<AircraftQueueItem>(capacity);
    }
   
}