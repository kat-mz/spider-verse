package spiderman;

public class Person {
    int location;
    String name;
    int signature;
    int timeToReturn;
    
    public Person()
    {
        int i = 928;
        String name = "Spiderman";
        int signature = 0;
        int timeToReturn = 0;
    }

    public Person(int location, String name, int signature, int timeToReturn)
    {
        this.location = location;
        this.name = name;
        this.signature = signature;
        this.timeToReturn = timeToReturn;
    }

    public int getLocation()
    {
        return location;
    }

    public String getName()
    {
        return name;
    }

    public int getSignature()
    {
        return signature;
    }

    public void setLocation(int location)
    {
        this.location = location;
    }

    public int getTime()
    {
        return timeToReturn;
    }
    
    public void setTime(int time)
    {
        timeToReturn = time;
    }
}
