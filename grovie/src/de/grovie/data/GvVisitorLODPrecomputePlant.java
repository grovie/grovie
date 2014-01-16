package de.grovie.data;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.linear.RealMatrix;

import com.tinkerpop.blueprints.Vertex;

import de.grovie.data.object.GvAxis;
import de.grovie.util.graph.GvVisitor;
import de.grovie.util.math.GvMatrix;

public class GvVisitorLODPrecomputePlant extends GvVisitor {

	HashMap<String, GvAxis> lCacheAxis; //cache of axis LOD info
	
	ArrayList<Vector3D> lPrevAxisStart; //stack of start position of prev axis
	
	public GvVisitorLODPrecomputePlant(HashMap<String, GvAxis> cacheAxis)
	{
		lCacheAxis = cacheAxis;
		lPrevAxisStart = new ArrayList<Vector3D>();
	}
	
	@Override
	public void visit(Vertex vertex) {
		
		//get groimp node id of this vertex
		String groimpId = this.getGroIMPNodeId(vertex);
		
		//get matrix of current axis
		GvAxis axis = lCacheAxis.get(groimpId);
		RealMatrix matCurrReal = axis.getMatrix(); 
		double[][] matCurr = matCurrReal.getData();
		
		//start position of current axis
		Vector3D ptMiddle = new Vector3D(matCurr[0][3],matCurr[1][3],matCurr[2][3]);
		
		//compute bend area error with prev axis
		if(lPrevAxisStart.size()>0)
		{
			//get start position of prev. axis
			Vector3D ptStart = lPrevAxisStart.get(lPrevAxisStart.size()-1);
			
			//get end position of current
			double[][] matNext = matCurrReal.multiply(GvMatrix.getMatrixTranslation(0, axis.getLength(), 0)).getData();
			Vector3D ptEnd = new Vector3D(matNext[0][3],matNext[1][3],matNext[2][3]);
			
			//compute area of triangle formed between 2 axes
			Vector3D vecA = ptStart.subtract(ptMiddle);
			double distA = Math.sqrt(vecA.getX()*vecA.getX() + vecA.getY()*vecA.getY() + vecA.getZ()*vecA.getZ());
			Vector3D vecB = ptEnd.subtract(ptMiddle);
			double sinAngle = Math.sin(Math.acos(vecA.dotProduct(vecB)));
			
			//set area as error of bifurcation in cache
			axis.setErrorBifuration((float) (0.5 * distA * axis.getLength() * sinAngle));
		}
		
		//push 
		lPrevAxisStart.add(ptMiddle);
		
		//compare xy,zy and xz plane errors for axis-scale LOD
		//individual plane curve areas have been computed in previous traversal
		//perform comparison and sqrt to obtain geometric distance error estimate here.
		float errorXY = axis.getErrorXY();
		float errorXZ = axis.getErrorXZ();
		float errorZY = axis.getErrorZY();
		if(errorXY>=errorXZ)
		{
			if(errorXY>=errorZY)
				axis.setError((float)(Math.sqrt(errorXY/2.0)));
			else
				axis.setError((float)(Math.sqrt(errorZY/2.0)));
		}
		else
		{
			if(errorXZ>=errorZY)
				axis.setError((float)(Math.sqrt(errorXZ/2.0)));
			else
				axis.setError((float)(Math.sqrt(errorZY/2.0)));
		}
	}

	@Override
	public void leave(Vertex vertex) {
		lPrevAxisStart.remove(lPrevAxisStart.size()-1);
	}

	/**
	 * Get corresponding groimp node id for current database vertex.
	 * @param vertex
	 * @return groimp node id
	 */
	private String getGroIMPNodeId(Vertex vertex)
	{
		//groimp node id
		Object groimpId = vertex.getProperty("GID");
		if(groimpId!=null)
		{
			return ((Long)groimpId).toString();
		}
		return null;
	}
}
