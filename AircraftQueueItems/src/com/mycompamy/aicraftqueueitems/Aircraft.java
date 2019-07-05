package com.mycompamy.aicraftqueueitems;
import com.google.gson.Gson;

public class Aircraft implements Cloneable
{
    private final int id;
    private final String name;
    private final AircraftType type;
    private final AircraftSize size;
    
    private static Gson gson = new Gson();
    
    public Aircraft(int id, String name, AircraftType type, AircraftSize size)
    {
        this.id = id;
        this.type = type;
        this.size = size;
        this.name = name;
    }

    public int getId()
    {
        return id;
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

    public String toJson()
    {
        return gson.toJson( this );
    }

    public static Aircraft fromJson(String aString)
    {
        return gson.fromJson(aString, Aircraft.class);
    }
    
    public String toString()
    {
        return String.format("[ %d, %s, %s, %s ]", id, name, type, size);
    }
    
}