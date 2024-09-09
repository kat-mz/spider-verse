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
 * SpotInputFile name is passed through the command line as args[2]
 * Read from the SpotInputFile with the format:
 * Two integers (line seperated)
 *      i.    Line one: The starting dimension of Spot (int)
 *      ii.   Line two: The dimension Spot wants to go to (int)
 * 
 * Step 4:
 * TrackSpotOutputFile name is passed in through the command line as args[3]
 * Output to TrackSpotOutputFile with the format:
 * 1. One line, listing the dimenstional number of each dimension Spot has visited (space separated)
 * 
 * @author Seth Kelley
 */

public class TrackSpot {

    private ArrayList<ClusterNode> adjList;
    
    public static void main(String[] args) {

        if ( args.length < 4 ) {
            StdOut.println(
                "Execute: java -cp bin spiderman.TrackSpot <dimension INput file> <spiderverse INput file> <spot INput file> <trackspot OUTput file>");
                return;
        }

          //Read inputFiles
          String inputFile = args[0];
          String spiderverseInput = args[1];
          String spotInput = args[2];
          String outputFile = args[3];
  
          StdOut.println("Input file(s): " + inputFile + " " + spiderverseInput + " " + spotInput);
          StdOut.println("Output file: " + outputFile);
  
          //2. Create clusters object
          Clusters c = new Clusters();
  
         //3. Read the file
          c.readCluster(inputFile); 
          c.addPeople(spiderverseInput, c);
 
         //3a. Create collider object
          Collider collider = new Collider();
          collider.collider(c);
 
         //4. Read persons file, create persons!
        

        //5. Set outputFile
        StdOut.setFile(outputFile);

         TrackSpot t = new TrackSpot(collider.getAdjList());
         t.trackSpot(spotInput);
  
      
         
        
    }

    public TrackSpot (ArrayList<ClusterNode> adjList) //Constructor
    {
        this.adjList = adjList;
    }

    public void trackSpot(String inputFile)
    {
        StdIn.setFile(inputFile);

        int currentDimension = StdIn.readInt(); //Starting point of spot 
        int goalDimension = StdIn.readInt(); //End goal of spot

        boolean[] visited = new boolean[adjList.size()]; //Vertices for each index of adjList - one per dimension

        dfsR(currentDimension, goalDimension, visited);
    
    }

    private boolean dfsR(int currentDimension, int goalDimension, boolean[] visited)
    {
        int index = findIndex(currentDimension); //Where a dimension is located as a vertex in adjList
        visited[index] = true; //Set dimension as visited

        if(currentDimension == goalDimension)
        {
            StdOut.print(currentDimension);
            return true;
        }
        
        StdOut.print(currentDimension + " ");
        ClusterNode vertex = adjList.get(index); //Get ClusterNode that contains dimension one is currently at
    
            while(vertex != null) //DFS search - go to next vertex, and call DFS on that vertex 
            {
                if(vertex.getNextClusterNode() == null)
                {
                    break;
                }
                int nextVertexIndex = findIndex(vertex.getNextClusterNode().getDimension().getDimensionValue()); 
                
                if(!visited[nextVertexIndex])
                {
                    ClusterNode next = adjList.get(nextVertexIndex);
                    if(dfsR(next.getDimension().getDimensionValue(), goalDimension, visited))
                    {
                        return true;
                    }
                }
                vertex = vertex.getNextClusterNode();
            }
       return false;
    }

    /**
     * Find the index that starts with the dimensionNode in the adjList
     * @param value
     * @return
     */

    private int findIndex(int value)
    {
        for(int i = 0; i < adjList.size(); i++)
        {
            if(adjList.get(i).getDimension().getDimensionValue() == value)
            {
                return i;
            }
        }
        return -1;
    }
}
