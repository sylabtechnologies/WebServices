/**
 * use tuple pattern
 */

package com.mycompamy.aicraftqueueitems;
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