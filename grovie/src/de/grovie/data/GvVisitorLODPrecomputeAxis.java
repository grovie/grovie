package de.grovie.data;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.linear.RealMatrix;

import com.tinkerpop.blueprints.Vertex;

import de.grovie.util.graph.GvGraphUtil;
import de.grovie.util.graph.GvVisitor;
import de.grovie.util.math.GvMatrix;

public class GvVisitorLODPrecomputeAxis extends GvVisitor {
	static final int TRANSLATE = 0;
	static final int RU = 1;
	static final int RH = 2;
	static final int RL = 3;
	static final int GU = 4;
	static final int PLANT = 5;
	static final int AXIS = 6;
	static final int BUD= 7;
	
	public int countT;
	public int countRU;
	public int countRL;
	public int countRH;
	public int countPlant;
	public int countAxis;
	public int countGU;
	
	Object lastAxisId;
	HashMap<String,RealMatrix> lCacheAxis;
	HashMap<String,Float> lCacheAxisRad;
	HashMap<String,Float> lCacheAxisLen;
	
	HashMap<String, RealMatrix> lCache; //cached world transformation matrix for each GroIMP node ID
	
	ArrayList<RealMatrix> lStack;	//transformation matrix stack. in use only when flag is true
	int lStackSize;
	
	boolean lStackInOperation;	//flag is true if traversing new (uncached) part of graph
	
	int currType; //current visit's vertex type
	
	public GvVisitorLODPrecomputeAxis() 
	{
		resetCounters();
		
		lCache = new HashMap<String, RealMatrix>(); //cache of matrices
		lCacheAxis = new HashMap<String, RealMatrix>(); //cache of matrices
		
		lStack = new ArrayList<RealMatrix>();
		lStack.add(GvMatrix.getIdentityRealMatrix());
		lStackSize=0;
		
		lStackInOperation = false;
		
		lastAxisId=null;
		lCacheAxis = new HashMap<String, RealMatrix>(); //cache of matrices for axes
		lCacheAxisRad = new HashMap<String, Float>();
		lCacheAxisLen = new HashMap<String, Float>();
	}
	
	@Override
	public void visit(Vertex vertex) {
		
		//vertex type
		currType = getType(vertex); 
		
		Iterable<Vertex> axisEncoarseVertex = GvGraphUtil.getVerticesEncoarse(vertex);
		java.util.Iterator<Vertex> axisVertexIter = axisEncoarseVertex.iterator();
		Vertex axisVertex = null;
		String axisGroimpId = null;
		if(axisVertexIter.hasNext())
		{
			axisVertex = axisVertexIter.next();
			axisGroimpId = getGroIMPNodeId(axisVertex);	
		}
		
		//for transformation node types and GU
		if((currType>-1)&&(currType<5))
		{
			String groimpIdStr=null;
			RealMatrix matrix=null;
			try{
				groimpIdStr = getGroIMPNodeId(vertex);
				matrix = getCachedPreTransformMatrix(groimpIdStr);
			}catch(Exception ex)
			{
				System.out.println("Precompute visit: " + "Error fetching cached matrix");
				ex.printStackTrace();
			}
			
			if(matrix==null)
			{
				if(lStackInOperation)
				{
					try{
						//get post transform matrix from parent vertex
						matrix = lStack.get(lStackSize);
						//put into cache as pre-transform matrix for current vertex
						lCache.put(groimpIdStr, matrix);
						//compute post-transform matrix for current vertex and push into stack
						stackPush(matrix.multiply(getTransformMatrix(vertex,currType)));
					}
					catch(Exception ex)
					{
						System.out.println("Precompute visit: " + "Error in stacked op");
						ex.printStackTrace();
					}
				}
				else
				{
					try
					{
						Vertex parent = getParent(vertex);
						if(parent != null)
						{
							//compute post-transform matrix of parent, i.e. pre-transform matrix of this vertex
							String parentGroimpId = getGroIMPNodeId(parent);
							RealMatrix matrixParent = getCachedPreTransformMatrix(parentGroimpId);
							matrix = matrixParent.multiply(getTransformMatrix(parent,getType(parent)));
						}
						else //no parent, so post transform matrix of previous vertex is identity mat
						{
							matrix = GvMatrix.getIdentityRealMatrix();
						}
					}
					catch(Exception ex)
					{
						System.out.println("Precompute visit: " + "Error in getting parent matrix");
						ex.printStackTrace();
					}
					
					try{
						//put into cache as pre-transform matrix for current vertex
						lCache.put(groimpIdStr, matrix);
						//compute post-transform matrix for current vertex and push into stack
						stackPush(matrix.multiply(getTransformMatrix(vertex,currType)));
						//switch on stack operation, having encountered uncached portion of graph
						lStackInOperation = true;
					}
					catch(Exception ex)
					{
						System.out.println("Precompute visit: " + "Error in non-stacked op");
						ex.printStackTrace();
					}
				}
				
				//update macro scale (axis) positioning in object-space
				if(lCacheAxis.get(axisGroimpId)==null)
				{
					lCacheAxis.put(axisGroimpId, matrix.copy());
				}
			}
		}
		
		//update length of macro scale axis
		if(currType == BUD)
		{
			//position at bud
			double[][] budMat = lStack.get(lStackSize).getData();
			Vector3D budPos = new Vector3D(budMat[0][3],budMat[1][3],budMat[2][3]);
			
			//position at beginning of axis
			RealMatrix axisMatReal = lCacheAxis.get(axisGroimpId);
			double[][] axisMat = axisMatReal.getData();
			Vector3D axisPos = new Vector3D(axisMat[0][3],axisMat[1][3],axisMat[2][3]);
			
			//direction vector from start to end of axis
			Vector3D axisDir = budPos.subtract(axisPos);
			
			//length of axis
			double x = axisDir.getX();
			double y= axisDir.getY();
			double z = axisDir.getZ();
			float axisLen = (float) Math.sqrt(x*x + y*y + z*z);
			lCacheAxisLen.put(axisGroimpId, new Float(axisLen));
			
			if(axisLen > 0)
			{
				axisDir = axisDir.normalize();
				RealMatrix axisOrientMat = GvMatrix.getMatrixFromUpDirectionAndPosition(axisDir.toArray(),axisMat[0][3],axisMat[1][3],axisMat[2][3]);
				lCacheAxis.put(axisGroimpId, axisOrientMat);
			}
		}
		
		//update radius of macro scale axis
		if(currType == GU)
		{
			float guRadius = ((Float)vertex.getProperty("Radius")).floatValue();
			Float axisRadF = lCacheAxisRad.get(axisGroimpId);
			float axisRad = 0;
			if(axisRadF != null)
				axisRad = axisRadF.floatValue();
			if(guRadius > axisRad)
				lCacheAxisRad.put(axisGroimpId, new Float(guRadius));
		}
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
	
	/**
	 * Get cached pre-transform matrix for specified groimp node
	 * @param groimpNodeId
	 * @return
	 */
	private RealMatrix getCachedPreTransformMatrix(String groimpNodeId)
	{
		return lCache.get(groimpNodeId);
	}
	
	/**
	 * Get parent vertex of specified vertex. Assumed tree graph with only 1 parent.
	 * @param vertex
	 * @return parent vertex
	 */
	private Vertex getParent(Vertex vertex) {
		
		Iterable<Vertex> parents = GvGraphUtil.getVerticesParent(vertex, GvGraphUtil.SUCCESSOR);
		java.util.Iterator<Vertex> parentsIter = parents.iterator();
		if(parentsIter.hasNext())
			return parentsIter.next();
		
		parents = GvGraphUtil.getVerticesParent(vertex, GvGraphUtil.BRANCH);
		parentsIter = parents.iterator();
		if(parentsIter.hasNext())
			return parentsIter.next();
		
		return null;
	}

	/**
	 * Get the transformation matrix for the specified vertex.
	 * @param vertex
	 * @return
	 */
	private RealMatrix getTransformMatrix(Vertex vertex, int type) {
		
		if(type==TRANSLATE)
		{
			float x = ((Float)vertex.getProperty("x")).floatValue();
			float y = ((Float)vertex.getProperty("y")).floatValue();
			float z = ((Float)vertex.getProperty("z")).floatValue();
			
			return GvMatrix.getMatrixTranslation(x,y,z);
		}
		else if(type==RU)
		{
			float angle = ((Float)vertex.getProperty("angle")).floatValue();
			return GvMatrix.getMatrixRotationRU(angle);
		}
		else if(type==RL)
		{
			float angle = ((Float)vertex.getProperty("angle")).floatValue();
			return GvMatrix.getMatrixRotationRL(angle);
		}
		else if(type==RH)
		{
			float angle = ((Float)vertex.getProperty("angle")).floatValue();
			return GvMatrix.getMatrixRotationRH(angle);
		}
		else if(type==GU)
		{
			float length = ((Float)vertex.getProperty("Length")).floatValue();
			return GvMatrix.getMatrixTranslation(0,length,0);
		}
		
		return null;
	}

	/**
	 * Get the type of the currently visited vertex
	 * @param vertex
	 * @return
	 */
	private int getType(Vertex vertex)
	{
		if(vertex.getProperty("Type").equals("Translate"))
		{
//			System.out.println("LOD Precompute - Node Translate: " + vertex.getId());
//			countT++;
			return TRANSLATE;
		}
		if(vertex.getProperty("Type").equals("RL"))
		{
//			System.out.println("LOD Precompute - Node RL: " + vertex.getId());
//			countRL++;
			return RL;
		}
		if(vertex.getProperty("Type").equals("RU"))
		{
//			System.out.println("LOD Precompute - Node RU: " + vertex.getId());
//			countRU++;
			return RU;
		}
		if(vertex.getProperty("Type").equals("RH"))
		{
//			System.out.println("LOD Precompute - Node RH: " + vertex.getId());
//			countRH++;
			return RH;
		}
		if(vertex.getProperty("Type").equals("GU"))
		{
//			System.out.println("LOD Precompute - Node GU: " + vertex.getId());
//			countGU++;
			return GU;
		}
		if(vertex.getProperty("Type").equals("Plant"))
		{
//			System.out.println("LOD Precompute - Node Plant: " + vertex.getId());
//			countPlant++;
			return PLANT;
		}
		if(vertex.getProperty("Type").equals("Axis"))
		{
//			System.out.println("LOD Precompute - Node Axis: " + vertex.getId());
//			countAxis++;
			return AXIS;
		}
		if(vertex.getProperty("Type").equals("Bud"))
		{
//			System.out.println("LOD Precompute - Node Axis: " + vertex.getId());
//			countAxis++;
			return BUD;
		}
		return -1;
	}

	@Override
	public void leave(Vertex vertex) {
		Object vType = vertex.getProperty("Type");
		if((vType.equals("Translate"))||
				(vType.equals("RU"))||
				(vType.equals("RL"))||
				(vType.equals("RH"))||
				(vType.equals("GU"))
				)
		{
			if(lStackInOperation == true)
			{
				stackPop();
				if(lStackSize==0)
					lStackInOperation=false;
			}
		}
	}

	public void printCounters()
	{
		System.out.println("Count T:" + countT);
		System.out.println("Count RU:" + countRU);
		System.out.println("Count RL:" + countRL);
		System.out.println("Count RH:" + countRH);
		System.out.println("Count Plant:" + countPlant);
		System.out.println("Count Axis:" + countAxis);
		System.out.println("Count GU:" + countGU);
	}
	
	public void resetCounters()
	{
		countT=0;
		countRU=0;
		countRL=0;
		countRH=0;
		countPlant=0;
		countAxis=0;
		countGU=0;
	}
	
	private void stackPush(RealMatrix m)
	{
		lStack.add(m);
		lStackSize++;
	}
	
	private void stackPop()
	{
		lStack.remove(lStack.size()-1);
		lStackSize--;
	}
	
	public HashMap<String, RealMatrix> getCache()
	{
		return this.lCache;
	}
	
	public HashMap<String, RealMatrix> getCacheAxis()
	{
		return this.lCacheAxis;
	}
	
	public HashMap<String, Float> getCacheAxisRad()
	{
		return this.lCacheAxisRad;
	}
	
	public HashMap<String, Float> getCacheAxisLen()
	{
		return this.lCacheAxisLen;
	}
}
