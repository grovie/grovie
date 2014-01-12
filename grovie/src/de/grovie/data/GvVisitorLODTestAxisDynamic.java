package de.grovie.data;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.math3.linear.RealMatrix;

import com.tinkerpop.blueprints.Vertex;

import de.grovie.data.object.GvAxis;
import de.grovie.data.object.GvGeometryFactory;
import de.grovie.data.object.GvGeometryTex;
import de.grovie.exception.GvExRendererDrawGroupRetrieval;
import de.grovie.exception.GvExRendererIndexBuffer;
import de.grovie.exception.GvExRendererVertexArray;
import de.grovie.exception.GvExRendererVertexBuffer;
import de.grovie.renderer.GvBufferSet;
import de.grovie.renderer.GvDrawGroup;
import de.grovie.renderer.GvPrimitive;
import de.grovie.util.graph.GvVisitor;
import de.grovie.util.math.GvMatrix;

public class GvVisitorLODTestAxisDynamic extends GvVisitor {

	public int countT;
	public int countRU;
	public int countRL;
	public int countRH;
	public int countPlant;
	public int countAxis;
	public int countGU;

	ArrayList<RealMatrix> lMatrixStack;

	HashMap<String, RealMatrix> lCache;
	HashMap<String, GvAxis> lCacheAxes;
	
	GvDrawGroup lDrawGroup;

	public GvVisitorLODTestAxisDynamic(HashMap<String, RealMatrix> cache,
			HashMap<String, GvAxis> cacheAxes,
			GvDrawGroup drawGroup)
	{
		lCache = cache;
		
		lCacheAxes = cacheAxes;
		
		countT=0;
		countRU=0;
		countRL=0;
		countRH=0;
		countPlant=0;
		countAxis=0;
		countGU=0;
		lMatrixStack = new ArrayList<RealMatrix>();
		lMatrixStack.add(GvMatrix.getIdentityRealMatrix());
		this.lDrawGroup = drawGroup;
	}

	@Override
	public void visit(Vertex vertex) {
		if(vertex.getProperty("Type").equals("Translate"))
		{
//			System.out.println("LOD Plant scale - Node Translate: " + vertex.getId());
			float x = ((Float)vertex.getProperty("x")).floatValue();
			float y = ((Float)vertex.getProperty("y")).floatValue();
			float z = ((Float)vertex.getProperty("z")).floatValue();
			RealMatrix lastMatrix = lMatrixStack.get(lMatrixStack.size()-1);
			lMatrixStack.add(lastMatrix.multiply(GvMatrix.getMatrixTranslation(x,y,z)));
			countT++;
		}
		else if(vertex.getProperty("Type").equals("RU"))
		{
//			System.out.println("LOD Plant scale - Node RU: " + vertex.getId());
			float angle = ((Float)vertex.getProperty("angle")).floatValue();
			RealMatrix lastMatrix = lMatrixStack.get(lMatrixStack.size()-1);
			lMatrixStack.add(lastMatrix.multiply(GvMatrix.getMatrixRotationRU(angle)));
			countRU++;
		}
		else if(vertex.getProperty("Type").equals("RL"))
		{
//			System.out.println("LOD Plant scale - Node RL: " + vertex.getId());
			float angle = ((Float)vertex.getProperty("angle")).floatValue();
			RealMatrix lastMatrix = lMatrixStack.get(lMatrixStack.size()-1);
			lMatrixStack.add(lastMatrix.multiply(GvMatrix.getMatrixRotationRL(angle)));
			countRL++;
		}
		else if(vertex.getProperty("Type").equals("RH"))
		{
//			System.out.println("LOD Plant scale - Node RH: " + vertex.getId());
			float angle = ((Float)vertex.getProperty("angle")).floatValue();
			RealMatrix lastMatrix = lMatrixStack.get(lMatrixStack.size()-1);
			lMatrixStack.add(lastMatrix.multiply(GvMatrix.getMatrixRotationRH(angle)));
			countRH++;
		}
		else if(vertex.getProperty("Type").equals("Plant"))
		{
//			System.out.println("LOD Plant scale - Node Plant: " + vertex.getId());
			countPlant++;
		}
		else if(vertex.getProperty("Type").equals("Axis"))
		{			
			//drawing
			String groImpNodeId = this.getGroIMPNodeId(vertex);
			
			//axis and cached info
			GvAxis axis = lCacheAxes.get(groImpNodeId);
			RealMatrix objSpaceMat = axis.getMatrix();
			float length = axis.getLength();
			float radius = axis.getRadius();
			float error = axis.getError();
			System.out.println("Axis "+ groImpNodeId + " error: " + error);
			
			if((objSpaceMat != null)&&(length>0)&&(radius>0))
			{
				RealMatrix worldSpaceMat = lMatrixStack.get(lMatrixStack.size()-1).multiply(objSpaceMat);
				float[] finalMat = GvMatrix.convertRowMajorToColumnMajor(worldSpaceMat.getData());

				GvGeometryTex geomTube = GvGeometryFactory.getTubeTextured(radius, length,  20, length);
				
				GvBufferSet bufferSet;
				try {
					bufferSet = lDrawGroup.getBufferSet(true, 1, 0, GvPrimitive.PRIMITIVE_TRIANGLE_STRIP, true);
					bufferSet.insertGeometry(geomTube.getVertices(), geomTube.getNormals(), geomTube.getIndices(), geomTube.getUv(), finalMat);
				} catch (GvExRendererIndexBuffer e) {
					System.out.println("Display visitor error:" + "error inserting in bufferset");
					e.printStackTrace();
				}
				catch (GvExRendererDrawGroupRetrieval e) {
					System.out.println("Display visitor error:" + "error inserting in bufferset");
					e.printStackTrace();
				}
				//end drawing
				catch (GvExRendererVertexBuffer e) {
					System.out.println("Display visitor error:" + "error inserting in bufferset");
					e.printStackTrace();
				} catch (GvExRendererVertexArray e) {
					System.out.println("Display visitor error:" + "error inserting in bufferset");
					e.printStackTrace();
				}
				
			}
			
			countAxis++;
		}
		else if(vertex.getProperty("Type").equals("GU"))
		{
//			System.out.println("LOD Plant scale - Node GU: " + vertex.getId());

			

			countGU++;
		}
	}

	@Override
	public void leave(Vertex vertex) {
		Object vType = vertex.getProperty("Type");
		if((vType.equals("Translate"))||
				(vType.equals("RU"))||
				(vType.equals("RL"))||
				(vType.equals("RH"))
				)
			lMatrixStack.remove(lMatrixStack.size()-1);
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
