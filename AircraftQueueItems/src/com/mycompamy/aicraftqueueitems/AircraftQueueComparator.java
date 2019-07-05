package com.mycompamy.aicraftqueueitems;
import java.util.Comparator;

public class AircraftQueueComparator implements Comparator<AircraftQueueItem> 
{ 
    @Override
    public int compare(AircraftQueueItem a1, AircraftQueueItem a2)
    {
        AircraftType t1 = a1.first.getType();
        AircraftType t2 = a2.first.getType();
        int cmp = t1.compareTo(t2);
        if (cmp != 0) return cmp;

        AircraftSize s1 = a1.first.getSize();
        AircraftSize s2 = a2.first.getSize();
        cmp = s1.compareTo(s2);
        if (cmp != 0) return cmp;

        return a1.second.compareTo(a2.second);
    }
}

