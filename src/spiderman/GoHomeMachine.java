package spiderman;
import java.util.*;

/**
 * Steps to implement this class main method:
 * 
 * Step 1:
 * DimensionInputFile name is passed through the command line as args[0]
 * Read from the DimensionsInputFile with the format:
 * 1. The first line with three numbers:
 *      i.    a (int): number of dimensions in the graph
 *      ii.   b (int): the initial size of the cluster table prior to rehashing
 *      iii.  c (double): the capacity(threshold) used to rehash the cluster table 
 * 2. a lines, each with:
 *      i.    The dimension number (int)
 *      ii.   The number of canon events for the dimension (int)
 *      iii.  The dimension weight (int)
 * 
 * Step 2:
 * SpiderverseInputFile name is passed through the command line as args[1]
 * Read from the SpiderverseInputFile with the format:
 * 1. d (int): number of people in the file
 * 2. d lines, each with:
 *      i.    The dimension they are currently at (int)
 *      ii.   The name of the person (String)
 *      iii.  The dimensional signature of the person (int)
 * 
 * Step 3:
 * HubInputFile name is passed through the command line as args[2]
 * Read from the SpotInputFile with the format:
 * One integer
 *      i.    The dimensional number of the starting hub (int)
 * 
 * Step 4:
 * AnomaliesInputFile name is passed through the command line as args[3]
 * Read from the AnomaliesInputFile with the format:
 * 1. e (int): number of anomalies in the file
 * 2. e lines, each with:
 *      i.   The Name of the anomaly which will go from the hub dimension to their home dimension (String)
 *      ii.  The time allotted to return the anomaly home before a canon event is missed (int)
 * 
 * Step 5:
 * ReportOutputFile name is passed in through the command line as args[4]
 * Output to ReportOutputFile with the format:
 * 1. e Lines (one for each anomaly), listing on the same line:
 *      i.   The number of canon events at that anomalies home dimensionafter being returned
 *      ii.  Name of the anomaly being sent home
 *      iii. SUCCESS or FAILED in relation to whether that anomaly made it back in time
 *      iv.  The route the anomaly took to get home
 * 
 * @author Seth Kelley
 */

public class GoHomeMachine {

private Collider collider;
private ArrayList<ClusterNode> adjList;
private ClusterNode hub;
    
    public static void main(String[] args) {

        if ( args.length < 5 ) {
            StdOut.println(
                "Execute: java -cp bin spiderman.GoHomeMachine <dimension INput file> <spiderverse INput file> <hub INput file> <anomalies INput file> <report OUTput file>");
                return;
        }

        //Read inputFiles
        String inputFile = args[0];
        String spiderVerse = args[1];
        String hubInput = args[2];
        String anomalies = args[3];
        String outputFile = args[4];

        StdOut.println("Input file(s): " + inputFile + " " + spiderVerse + " " + hubInput);
        StdOut.println("Output file: " + outputFile);

        //2. Create clusters object
        Clusters c = new Clusters();

       //3. Read the file
        c.readCluster(inputFile); 
        c.addPeople(spiderVerse, c);

       //3a. Create collider object
        Collider collider = new Collider();
        collider.collider(c);

        CollectAnomalies collect = new CollectAnomalies(collider);
        collect.collectAnomalies(hubInput, outputFile, collider);

         //5. Set outputFile
         StdOut.setFile(outputFile);

        GoHomeMachine machine = new GoHomeMachine(collider, collider.getAdjList(), hubInput);
        machine.goHomeMachine(anomalies);
        
    }

public GoHomeMachine(Collider collider, ArrayList<ClusterNode> adjList, String hubInput)
{
    this.collider = collider;
    this.adjList = collider.getAdjList();

    StdIn.setFile(hubInput);
    int hubNum = StdIn.readInt();

    this.hub = adjList.get(collider.index(hubNum, adjList));
}

public void goHomeMachine(String inputFile)
{
    StdIn.setFile(inputFile);
    int anomalies = StdIn.readInt();

    Dimension hubDimension = hub.getDimension();
    ArrayList<Person> hubPeople = hubDimension.getPersonList();
    ArrayList<Person> removePeople = new ArrayList<>();

    for(int i = 0; i < anomalies; i++) //For each anomaly - send back to their dimension
    {
        String anomalyName = StdIn.readString();
        int time = StdIn.readInt();

       for(Person person: hubPeople)
       {
        if(person.getName().equals(anomalyName))
        {
            person.setTime(time); //Update their time

            ClusterNode home = adjList.get(collider.index(person.getSignature(), adjList));
            home.getDimension().getPersonList().add(person);
        }
       }
    }

    ArrayList<SpiderFile> reports = dijkstra(adjList.get(collider.index(928, adjList)));

    for(SpiderFile report: reports)
    {
        StdOut.print(report.returnCanonEvents() + " ");
        StdOut.print(report.returnAnomalyName() + " ");

        boolean success = report.returnSuccess();

        if(success)
        {
            StdOut.print("SUCCESS" + " ");
        }
        else
        {
            StdOut.print("FAILED" + " ");
        }

        ArrayList<ClusterNode> path = report.returnPath();

        for(int i = path.size()-1; i >= 0; i--)
        {
            StdOut.print(path.get(i).getDimension().getDimensionValue()+ " ");          
        }

        StdOut.println();
    }

}

/**
 * Creates an arrayList of SpiderFile that contain the canon events remaining, anomaly name, path taken to be sent back, and success of the anomaly
 * @param start
 */
private ArrayList<SpiderFile> dijkstra(ClusterNode start)
{
    ArrayList<SpiderFile> reports = new ArrayList<>(); //SpiderFile to be returned to faciliate printing
    ArrayList<ClusterNode> done = new ArrayList<ClusterNode>(); //Vertices with KNOWN optimal path
    ArrayList<ClusterNode> fringe = new ArrayList<ClusterNode>(); //Vertices with UNKNOWN optimal path

    ClusterNode[] prev = new ClusterNode[adjList.size()]; //previous vertex/parent
    int[] distance = new int[adjList.size()]; //Current shortest distance from s to v

    for(int i = 0; i < adjList.size(); i++)
    {
        prev[i] = null;
        distance[i] = Integer.MAX_VALUE;
    }

    int startIndex = collider.exists(start, adjList);
    distance[startIndex] = 4; //Put in the weight of the start later

    fringe.add(start); //Add start to fringe

    while(!fringe.isEmpty())
    {
        ClusterNode min = fringe.get(0); //Set first as minimum till changed

        done.add(min); //Add min to done
        fringe.remove(min); //Remove min from fringe
        int minIndex = collider.exists(min, adjList);

       
        //Updating the arrays of the minimum path and path taken
        ClusterNode neighbor = adjList.get(minIndex).getNextClusterNode();

        while(neighbor != null && !done.contains(neighbor)) //For all neighbors of min not in done
        {
            int neighborIndex = collider.exists(neighbor, adjList);
            
            if(distance[neighborIndex] == Integer.MAX_VALUE) //Unvisited
            {
                distance[neighborIndex] = distance[minIndex] + neighbor.getDimension().getDimensionWeight() + min.getDimension().getDimensionWeight();
                fringe.add(neighbor);
                prev[neighborIndex] = min;
            }
            else if (distance[neighborIndex] > (distance[minIndex] + neighbor.getDimension().getDimensionWeight() + min.getDimension().getDimensionWeight())) //Already visited
            {
                distance[neighborIndex] = distance[minIndex] + neighbor.getDimension().getDimensionWeight()+min.getDimension().getDimensionWeight();
                prev[neighborIndex] = min;
            }
            neighbor = neighbor.getNextClusterNode();
        } 
        
         //CHECK IF MIN HAS AN ANOMALY 
         ArrayList<Person> list = min.getDimension().getPersonList();
         if(!list.isEmpty())
         {
            Person anomaly = null;
            for(int i = 0; i < list.size(); i++) //Go through list at dimension
            {
                anomaly = list.get(i);
                if(anomaly.getSignature() == anomaly.getLocation())
                {
                    anomaly = null;
                }
                if(min != start && anomaly != null)
                {
                    int signature = anomaly.getSignature();
                    anomaly.setLocation(signature); //Set person back to dimension
                   int timeTaken = start.getDimension().getDimensionWeight() + distance[minIndex];
                   ClusterNode currentNode = min;
                   ArrayList<ClusterNode> pathTaken = new ArrayList<>();
       
                   while(currentNode != null && currentNode != prev[collider.exists(currentNode, adjList)]) //Construct path there if not start
                   {
                      pathTaken.add(currentNode);
                      currentNode = prev[collider.exists(currentNode, adjList)];   
                   }
                   if(timeTaken > anomaly.getTime()) //Fail
                   { 
                    int canonEvents = min.getDimension().getCanonEvents()-1;
                    if(canonEvents >= 0)
                    {
                      min.getDimension().setCanonEvents(canonEvents); //Update canonEvents
                    }
                      SpiderFile report = new SpiderFile(min.getDimension().getCanonEvents(), anomaly.getName(), false, pathTaken);
                      reports.add(report);
                   }
                   else //Success
                   {
                      SpiderFile report = new SpiderFile(min.getDimension().getCanonEvents(), anomaly.getName(), true, pathTaken);
                      reports.add(report);
                   }
                  
                }
            }
            
         }
    }
    return reports;
}

}