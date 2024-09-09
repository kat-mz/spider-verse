package spiderman;

import java.util.ArrayList;

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
 * 
 * Step 2:
 * ClusterOutputFile name is passed in through the command line as args[1]
 * Output to ClusterOutputFile with the format:
 * 1. n lines, listing all of the dimension numbers connected to 
 *    that dimension in order (space separated)
 *    n is the size of the cluster table.
 * 
 * @author Seth Kelley
 */

public class Clusters {

    private ClusterNode[] clusters;
    private double capacity;
    private int currentClusters; //How many dimensions are currently added
    private int size = 0; //Size of current array 


    public static void main(String[] args) {

        //Read inputFiles
        String inputFile = args[0];
        String outputFile = args[1];
    
        StdOut.println("Input file: " + inputFile);
        StdOut.println("Output file: " + outputFile);

        //2. Create clusters object
        Clusters c = new Clusters();

         //3. Read the file
        c.readCluster(inputFile); 

        //4. Set outputFile
        StdOut.setFile(outputFile);

        //5. Print cluster
        c.printCluster();
    }


    //Public constructor class - default
    public Clusters()
    {
        clusters = null;
        capacity = 0.0;
        currentClusters = 0;
    }

    //Method that reads the file
    public void readCluster(String inputDimensionsFile)
    {
        StdIn.setFile(inputDimensionsFile);
       
        int dimensions = StdIn.readInt();
        this.size = StdIn.readInt();
        this.capacity = StdIn.readInt();

        clusters = new ClusterNode[size];
       
        for(int i = 0; i < dimensions; i++)
        {   
            int dimensionNumber = StdIn.readInt();
            int canonEvents = StdIn.readInt();
            int dimensionWeight = StdIn.readInt();

            Dimension dimension = new Dimension(dimensionNumber, canonEvents, dimensionWeight);
            ClusterNode c = new ClusterNode(dimension, null);
           
            int index = c.getDimension().getDimensionValue()%clusters.length; //hash function

            c.setNextClusterNode(clusters[index]); //Adding to front of the linked list 
            clusters[index] = c;

            currentClusters++;

            if((double)currentClusters/size >= capacity)
            {
                resize();
            }
            
        }

        connectClusters(); 
    }
    
    /**
     * Doubles the size of the arra - update the variable!
     * Rehashes all clusterNodes
     */
    public void resize()
    {
        int newSize = size*2;

        ClusterNode[] newClusterHashTable = new ClusterNode[newSize];

        for(int i = 0; i < clusters.length; i++)
        {
            ClusterNode head = clusters[i];

            while(head != null)
            {
                
                ClusterNode next = head.getNextClusterNode();
                int index = (head.getDimension().getDimensionValue()) % newSize; //Recalculate hashcode
                 
                head.setNextClusterNode(newClusterHashTable[index]);
                newClusterHashTable[index] = head;
           
                head = next;
            }

        }
        clusters = newClusterHashTable;
        size = newSize;
    }

    /**
     * Connects previous clusters by adding the FIRST node of previous indexes in cluster to the END of the current cluster
     * 0, 1 have previous clusters of n-1, n-2 and 0, n-1 respectively
     */
    public void connectClusters()
    {

        for(int i = 0; i < clusters.length; i++)
        {
            //Indexes of previous clusters
            int prev1 = -1;
            int prev2 = -1;


            //Get the previous indexes - special case for index 0 and 1
            if(i == 0)
            {
                prev1 = clusters.length-1;
                prev2 = clusters.length-2;
            }

            else if(i == 1)
            {
                prev1 = 0;
                prev2 = clusters.length-1;
            }
            else{

                prev1 = i-1;
                prev2 = i-2;
            }

            //Check for HashTables of Size 2
            if(prev1 == i)
            {
                prev1 = -1;
            }
            if(prev2 == i)
            {
                prev2 = -1;
            }

            ClusterNode pre1 = null;
            ClusterNode pre2 = null;

            //Create copyNodes of the dimensions
            if(prev1 != -1)
            {
                 pre1 =  new ClusterNode(clusters[prev1].getDimension(), null);
            }

            if(prev2 != -1)
            {
                pre2 =  new ClusterNode(clusters[prev2].getDimension(), null);
            }

             ClusterNode ptr = clusters[i];
            while(ptr.getNextClusterNode() != null)
            {
                ptr = ptr.getNextClusterNode();
            }

            if(pre1 != null)
                ptr.setNextClusterNode(pre1);
            
            ptr = ptr.getNextClusterNode();
            
            if(pre2 != null)
                ptr.setNextClusterNode(pre2);

        }
        
}

//     Output to ClusterOutputFile with the format:
//  * 1. n lines, listing all of the dimension numbers connected to 
//  *    that dimension in order (space separated)
//  *    n is the size of the cluster table.
    public void printCluster()
    {
        for(int i = 0; i < clusters.length; i++)
        {
            ClusterNode ptr = clusters[i];
            while(ptr != null)
            {
                StdOut.print(ptr.getDimension().getDimensionValue() + " ");
                ptr = ptr.getNextClusterNode();
            }
            StdOut.println();  
        }

    }

    public ClusterNode[] getArrayClusterNodes()
    {
        return clusters;
    }

    public int numOfClusters()
    {
        return currentClusters;
    }

    public void addPeople(String file, Clusters cluster)
    {
        StdIn.setFile(file);
        int numOfPeople = StdIn.readInt();

        for(int i = 0; i < numOfPeople; i++)
        {
            int location = StdIn.readInt();
            String name = StdIn.readString();
            int signature = StdIn.readInt();

            Person person = new Person(location, name, signature, 0);

            ClusterNode[] clusters = cluster.getArrayClusterNodes();

            for(int j = 0; j < clusters.length; j++) //Iterate through clusters to find dimension match
            {
                ClusterNode ptr = clusters[j];
                
                while(ptr != null)
                {
                    if(ptr.getDimension().getDimensionValue() == location)
                    {
                        ArrayList<Person> persons = ptr.getDimension().getPersonList();
                       
                       if(!persons.contains(person))
                       {
                            persons.add(person);
                       }
                    }
                    ptr = ptr.getNextClusterNode();
                }

            }
        }

        
    }

}
