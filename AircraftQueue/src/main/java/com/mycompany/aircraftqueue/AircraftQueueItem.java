package com.mycompany.aircraftqueue;
import java.time.LocalDateTime;

public class AircraftQueueItem
{
    public final Aircraft first;
    public final LocalDateTime second;

    public AircraftQueueItem(Aircraft a)
    {
        first = a;
        second = LocalDateTime.now();
    }
}
