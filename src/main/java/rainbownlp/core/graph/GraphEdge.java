package rainbownlp.core.graph;

import org.jgrapht.graph.DefaultEdge;

public class GraphEdge extends DefaultEdge {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public GraphEdge()
	{
		super();
	}
	
    //~ Methods ----------------------------------------------------------------

//	@Override
	public Object getSource()
	{
		//"(" + source + " : " + target + ")"
		return super.toString().split(":")[0].replaceAll("^\\(", "");
	}
//	@Override
	public Object getTarget()
	{
		//"(" + source + " : " + target + ")"
		return super.toString().split(":")[1].replaceAll("\\)$", "");
	}
}
