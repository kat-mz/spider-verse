package spiderman;

public class ClusterNode 
{

    private Dimension dimension;
    private ClusterNode next;

    /*
     * Constructor
     * @param dimension it supports
     * @param link to next node in the list
     */

    public ClusterNode()
    {
        dimension = null;
        next = null;
    }

    public ClusterNode (Dimension dimension, ClusterNode next)
    {
        this.dimension = dimension;
        this.next = next;
    }

    //Getter and Setter methods

    public Dimension getDimension()
    {
        return dimension;
    }
    public void setDimension(Dimension dimension)
    {
        this.dimension = dimension;
    }

    public ClusterNode getNextClusterNode()
    {
        return next;
    }
    public void setNextClusterNode(ClusterNode next)
    {
        this.next = next;
    }

    public void addToEnd(ClusterNode head, ClusterNode add)
    {
        ClusterNode ptr = head;

        while(ptr.getNextClusterNode()!= null)
        {
            ptr = ptr.getNextClusterNode();
        }

        ptr.setNextClusterNode(add);
    }
    
}
