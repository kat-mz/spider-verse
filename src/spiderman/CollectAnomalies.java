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
 * Read from the HubInputFile with the format:
 * One integer
 *      i.    The dimensional number of the starting hub (int)
 * 
 * Step 4:
 * CollectedOutputFile name is passed in through the command line as args[3]
 * Output to CollectedOutputFile with the format:
 * 1. e Lines, listing the Name of the anomaly collected with the Spider who
 *    is at the same Dimension (if one exists, space separated) followed by 
 *    the Dimension number for each Dimension in the route (space separated)
 * 
 * @author Seth Kelley
 */

public class CollectAnomalies {
    private Collider collider;
    private ArrayList<ClusterNode> adjList;

    public static void main(String[] args) {

        if ( args.length < 4 ) {
            StdOut.println(
                "Execute: java -cp bin spiderman.CollectAnomalies <dimension INput file> <spiderverse INput file> <hub INput file> <collected OUTput file>");
                return;
        }

        //Read inputFiles
        String inputFile = args[0];
        String spiderVerse = args[1];
        String hubInput = args[2];
        String outputFile = args[3];

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

         //5. Set outputFile
         StdOut.setFile(outputFile);

       CollectAnomalies collect = new CollectAnomalies(collider);
       collect.collectAnomalies(hubInput, outputFile, collider);

    }

    public CollectAnomalies(Collider collider)
    {
        this.collider = collider;
        this.adjList = collider.getAdjList();
    }

    /**
     * 
     */
    public void collectAnomalies(String hubInput, String outputFile, Collider collider)
    {
        StdIn.setFile(hubInput);
    
        int hub = StdIn.readInt();

        this.adjList = collider.getAdjList();

        int hubIndex = collider.index(hub, adjList);
        ClusterNode hubNode = adjList.get(hubIndex);

        BFS(hubNode);
    }

    private void BFS(ClusterNode start) {

        boolean[] visited = new boolean[adjList.size()]; //Array that states if a dimension is visited - index of a dimension is same as its location as head in adjList
        ClusterNode[] parent = new ClusterNode[adjList.size()]; //Parent array that contains parent of each vertex(dimension) from BFS - Contains clusterNodes

        Queue<ClusterNode> queue = new LinkedList<ClusterNode>();

        int startIndex = collider.exists(start, adjList);

        queue.add(adjList.get(startIndex)); //Add start
        parent[collider.exists(start, adjList)] = start; //set parent to itself, since hub is starting spot
        visited[collider.exists(start, adjList)] = true; //mark as visited

        while(!queue.isEmpty())
        {
            //Dequeuing node to explore 
            ClusterNode parentNode = queue.poll();
            int parentIndex = collider.exists(parentNode, adjList);

            Person anomaly = isAnomaly(parentNode); //Anomaly for dimension
            Person spider = isSpider(parentNode); //Spider for dimension

            if(anomaly != null && !parentNode.equals(start)) //Check if currentNode has an anomaly
            {
              StdOut.print(anomaly.getName() + " ");

              //Remove from current Dimension(?)
              parentNode.getDimension().getPersonList().remove(anomaly);
              start.getDimension().getPersonList().add(anomaly);
              anomaly.setLocation(start.getDimension().getDimensionValue()); //Update location

              ClusterNode currentNode = parentNode; //Traversal node to use to trace back path

              //Check if contains spider
              if(spider != null)
              {
                StdOut.print(spider.getName() + " "); //Printer spider name :D

                while(currentNode != parent[collider.exists(currentNode, adjList)])
                {
                    StdOut.print(currentNode.getDimension().getDimensionValue() + " ");
                    currentNode = parent[collider.exists(currentNode, adjList)];
                }
                if(currentNode.equals(parent[collider.exists(currentNode, adjList)])) //Print the last hub area!
                {
                    StdOut.print(currentNode.getDimension().getDimensionValue());
                }

               parentNode.getDimension().getPersonList().remove(spider);
               start.getDimension().getPersonList().add(spider);
               spider.setLocation(start.getDimension().getDimensionValue()); //Update location
              }
              else //No spider, send out team
              {
                ArrayList<ClusterNode> path = new ArrayList<>();
                while(currentNode != parent[collider.exists(currentNode, adjList)]) //Construct path there
                {
                    path.add(currentNode);
                    currentNode = parent[collider.exists(currentNode, adjList)];
                }
                if(currentNode.equals(parent[collider.exists(currentNode, adjList)])) 
                {
                   path.add(currentNode);
                }
                for(int i = path.size()-1; i > 0; i--)
                {
                    StdOut.print(path.get(i).getDimension().getDimensionValue() + " ");
                }
                for(ClusterNode node: path)
                {
                    StdOut.print(node.getDimension().getDimensionValue() + " ");
                }
              }
              StdOut.println();
            }

            ClusterNode ptr = parentNode.getNextClusterNode();

            //Adding all unvisited neighbors and updating variables respectively
            while (ptr != null) 
            {
                int ptrIndex = collider.exists(ptr, adjList);

                if(!visited[ptrIndex])//If not visited
                {
                    
                    queue.add(adjList.get(ptrIndex)); //Add neighbor to queue
                    parent[ptrIndex] = parentNode; //Set parent to parentNode - similar to QuickUnion
                    visited[ptrIndex] = true; //Mark as visited
                }
                ptr = ptr.getNextClusterNode();
            }
        }
    }
    
    /**
     * Returns anomalies
     */

    private Person isAnomaly(ClusterNode node)
    {
        Dimension dimension = node.getDimension();

        ArrayList<Person> people = dimension.getPersonList();

        for(Person person : people) //For each person in dimension, check if anomaly
        {
            int currentDimensionValue = dimension.getDimensionValue();
            int signature = person.getSignature();

            if(currentDimensionValue != signature)
            {
                return person;
            }
        }
        return null;
    }

    /**
     * Returns spiders
     */

     private Person isSpider(ClusterNode node)
    {
        Dimension dimension = node.getDimension();

        ArrayList<Person> people = dimension.getPersonList();

        for(Person person : people) //For each person in dimension, check if anomaly
        {
            int currentDimensionValue = dimension.getDimensionValue();
            int signature = person.getSignature();

            if(currentDimensionValue == signature)
            {
                return person;
            }
        }
        return null;
    }
}
