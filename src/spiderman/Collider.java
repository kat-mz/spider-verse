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
 * ColliderOutputFile name is passed in through the command line as args[2]
 * Output to ColliderOutputFile with the format:
 * 1. e lines, each with a different dimension number, then listing
 *       all of the dimension numbers connected to that dimension (space separated)
 * 
 * @author Seth Kelley
 */

public class Collider {

    private ArrayList<ClusterNode> adjList = new ArrayList<>();

    public static void main(String[] args) {

        if ( args.length < 3 ) {
            StdOut.println(
                "Execute: java -cp bin spiderman.Collider <dimension INput file> <spiderverse INput file> <collider OUTput file>");
                return;
        }

         //Read inputFiles
         String inputFile = args[0];
         String inputFile2 = args[1];
         String outputFile = args[2];
 
         StdOut.println("Input file(s): " + inputFile + " " + inputFile2);
         StdOut.println("Output file: " + outputFile);
 
         //2. Create clusters object
         Clusters c = new Clusters();
 
        //3. Read the file
         c.readCluster(inputFile); 
         c.addPeople(inputFile2, c);

        //3a. Create collider object
         Collider collider = new Collider();
         collider.collider(c);

        //4. Read persons file, create persons!
 
         //5. Set outputFile
         StdOut.setFile(outputFile);
 
         //6. Print cluster
         collider.printCollider();
         collider.printPersons();
   
        
    }

    public void collider(Clusters c)
    {

        ClusterNode[] clusterArray = c.getArrayClusterNodes(); //get Array of ClusterNodes

        for (int i = 0; i < clusterArray.length; i++) //Run loop for number of dimensions in the clusters
        {
            ClusterNode vertex = clusterArray[i]; 
            ClusterNode ptr = vertex.getNextClusterNode(); //Create a pointer for each index of the ClusterNode Array//Create a pointer for each index of the ClusterNode Array

            int vertexExists = exists(vertex, adjList);

            if(vertexExists != -1) //Key already exists, append to end
            {
                ClusterNode node = adjList.get(vertexExists);
                node.addToEnd(node, ptr);
            }
            else //Key does not exist, simply copy over the linked list
            {
                adjList.add(vertex);
            }

            //Now to create the links!
            while(ptr != null)
            {
                int ptrExists = exists(ptr, adjList);

                if(ptrExists != -1)
                {
                    ClusterNode node = adjList.get(ptrExists);
                    node.addToEnd(node, new ClusterNode(vertex.getDimension(), null));
                }
                else{
                    ClusterNode temp = new ClusterNode(ptr.getDimension(), null);
                    temp.setNextClusterNode(new ClusterNode(vertex.getDimension(), null));
                    adjList.add(temp);
                }

            
                ptr = ptr.getNextClusterNode();
            }
           
            }
    }
    
    /*
     * Returns the index of a clusternode in a list given the node
     */

    public int exists(ClusterNode node, ArrayList<ClusterNode> list)
    {
        int value = node.getDimension().getDimensionValue();

        for(int i = 0; i < list.size(); i++)
        {
            if(list.get(i).getDimension().getDimensionValue() == value)
                return i;
        }
        return -1;
    }
    /*
     * Return the index of a dimension given the dimension value
     */

    public int index(int dimension, ArrayList<ClusterNode> list)
    {
        for(int i = 0; i < list.size(); i++)
        {
            if(list.get(i).getDimension().getDimensionValue() == dimension)
                return i;
        }
        return -1;
    }

    
    public void printCollider()
    {
        for(int i = 0; i < adjList.size(); i++)
        {
            ClusterNode ptr = adjList.get(i);
            while(ptr != null)
            {
                StdOut.print(ptr.getDimension().getDimensionValue() + " ");
                ptr = ptr.getNextClusterNode();
            }
            StdOut.println();  
        }

       
    }

    public void printPersons()
    {
        for(int i = 0; i < adjList.size(); i++)
        {
            ClusterNode ptr = adjList.get(i);
            while(ptr != null)
            {
                
                ArrayList<Person> person = ptr.getDimension().getPersonList();

                for(int j = 0; j < person.size(); j++)
                {
                    System.out.print(person.get(j).getName());
                }

                System.out.println(ptr.getDimension().getDimensionValue());

                ptr = ptr.getNextClusterNode();
            }
            System.out.println();  
        }
        
    }

    public ArrayList<ClusterNode> getAdjList()
    {
        return adjList;
    }

    }

