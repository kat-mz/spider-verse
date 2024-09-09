package spiderman;
import java.util.*;

public class SpiderFile 
{
    private int canonEvents;
    private String anomalyName;
    private boolean success;
    private ArrayList<ClusterNode> path;
     
    public SpiderFile(int canonEvents, String anomalyName, boolean success, ArrayList<ClusterNode> path)
    {
        this.canonEvents = canonEvents;
        this.anomalyName = anomalyName;
        this.success = success;
        this.path = path;
    }

    public int returnCanonEvents()
    {
        return canonEvents;
    }

    public String returnAnomalyName()
    {
        return anomalyName;
    }
    
    public boolean returnSuccess()
    {
        return success;
    }

    public ArrayList<ClusterNode> returnPath()
    {
        return path;
    }
    
}
