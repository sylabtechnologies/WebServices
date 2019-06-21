package com.mycompany.aircraftqueue;

public class Aircraft implements Cloneable
{
    private final int id;
    private final String name;
    private final AircraftType type;
    private final AircraftSize size;
    
    public Aircraft(int id, String name, AircraftType type, AircraftSize size)
    {
        this.id = id;
        this.type = type;
        this.size = size;
        this.name = name;
    }

    public AircraftType getType()
    {
        return type;
    }

    public AircraftSize getSize()
    {
        return size;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public Object clone()
    {
        try
        {
            return super.clone();
        }
        catch (CloneNotSupportedException e )
        {
            throw new Error("cant happen");
        }
    }    

    public String toString()
    {
        return String.format("[ %d, %s, %s, %s ]", id, name, type, size);
    }
    
}