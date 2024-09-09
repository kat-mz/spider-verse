package spiderman;
import java.util.*;

public class Dimension
{
    private int dimensionNumber;
    private int canonEvents;
    private int dimensionWeight;
    private ArrayList<Person> persons; //Current people at that dimension(?)
    

    public Dimension(int dimensionNumber, int canonEvents, int dimensionWeight)
    {
        this.dimensionNumber = dimensionNumber;
        this.canonEvents = canonEvents;
        this.dimensionWeight = dimensionWeight;
        this.persons = new ArrayList();
    }

    
    //Getter and setter methods
    public int getDimensionValue()
    {
        return dimensionNumber;
    }
    public void setDimensionValue(int dimensionNumber)
    {
        this.dimensionNumber = dimensionNumber;
    }


    public int getCanonEvents()
    {
        return canonEvents;
    }
    public void setCanonEvents(int canonEvents)
    {
        this.canonEvents = canonEvents;
    }

    public int getDimensionWeight()
    {
        return dimensionWeight;
    }
    public void setDimensionWeight(int dimensionWeight)
    {
        this.dimensionWeight = dimensionWeight;
    }

    public ArrayList<Person> getPersonList()
    {
        return persons;
    }

    public void addPersonList(ArrayList<Person> persons)
    {
        this.persons = persons;
    }

    public void addPerson(Person person)
    {
        persons.add(person);
    }

    public void removePerson (Person person)
    {
        persons.remove(person);
    }
}


   