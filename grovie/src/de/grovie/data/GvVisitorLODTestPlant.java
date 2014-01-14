package de.grovie.data;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
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
import de.grovie.renderer.GvCamera;
import de.grovie.renderer.GvDrawGroup;
import de.grovie.renderer.GvPrimitive;
import de.grovie.util.graph.GvVisitorSelective;
import de.grovie.util.math.GvMatrix;

public class GvVisitorLODTestPlant  extends GvVisitorSelective {

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
	
	GvCamera lCamera;
	
	float lErrorThres;
	float lErrorThresPixel;

	public GvVisitorLODTestPlant(HashMap<String, RealMatrix> cache,
			HashMap<String, GvAxis> cacheAxes,
			GvDrawGroup drawGroup,
			GvCamera camera)
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
		this.lCamera = camera;
		lErrorThres=0;
		lErrorThresPixel=0;
	}

	@Override
	public boolean visit(Vertex vertex) {
		if(vertex.getProperty("Type").equals("Translate"))
		{
//			System.out.println("LOD Plant scale - Node Translate: " + vertex.getId());
			float x = ((Float)vertex.getProperty("x")).floatValue();
			float y = ((Float)vertex.getProperty("y")).floatValue();
			float z = ((Float)vertex.getProperty("z")).floatValue();
			RealMatrix lastMatrix = lMatrixStack.get(lMatrixStack.size()-1);
			lMatrixStack.add(lastMatrix.multiply(GvMatrix.getMatrixTranslation(x,y,z)));
			countT++;
			return true;
		}
		else if(vertex.getProperty("Type").equals("RU"))
		{
//			System.out.println("LOD Plant scale - Node RU: " + vertex.getId());
			float angle = ((Float)vertex.getProperty("angle")).floatValue();
			RealMatrix lastMatrix = lMatrixStack.get(lMatrixStack.size()-1);
			lMatrixStack.add(lastMatrix.multiply(GvMatrix.getMatrixRotationRU(angle)));
			countRU++;
			return true;
		}
		else if(vertex.getProperty("Type").equals("RL"))
		{
//			System.out.println("LOD Plant scale - Node RL: " + vertex.getId());
			float angle = ((Float)vertex.getProperty("angle")).floatValue();
			RealMatrix lastMatrix = lMatrixStack.get(lMatrixStack.size()-1);
			lMatrixStack.add(lastMatrix.multiply(GvMatrix.getMatrixRotationRL(angle)));
			countRL++;
			return true;
		}
		else if(vertex.getProperty("Type").equals("RH"))
		{
//			System.out.println("LOD Plant scale - Node RH: " + vertex.getId());
			float angle = ((Float)vertex.getProperty("angle")).floatValue();
			RealMatrix lastMatrix = lMatrixStack.get(lMatrixStack.size()-1);
			lMatrixStack.add(lastMatrix.multiply(GvMatrix.getMatrixRotationRH(angle)));
			countRH++;
			return true;
		}

		
		else if(vertex.getProperty("Type").equals("Plant"))
		{
//			System.out.println("LOD Plant scale - Node Plant: " + vertex.getId());
			//compute error threshold
			lErrorThres = computeErrorThres();
			lErrorThresPixel = lErrorThres/2.0f;
			System.out.println("Error thres for plant: " + lErrorThres);
			
			countPlant++;
			
			if(lErrorThres<0)
				return false;
			else
				return true;
		}
		else if(vertex.getProperty("Type").equals("Axis"))
		{			
			
			
			//drawing
			String groImpNodeId = this.getGroIMPNodeId(vertex);
			
			//axis and cached info
			GvAxis axis = lCacheAxes.get(groImpNodeId);
			
			//check if error larger than threshold
			float error = (float) Math.sqrt(axis.getError());
			if(error > lErrorThres)
				return true;
			
			//check if bifurcation error (axis-axis bend) smaller than threshold
			//not necessary to draw axis if smaller than threshold
			float errorBifur = (float) Math.sqrt(axis.getErrorBifuration());
			if(errorBifur < lErrorThres)
				return false;
			
			RealMatrix objSpaceMat = axis.getMatrix();
			float length = axis.getLength();
			float radius = axis.getRadius();
			
			if((objSpaceMat != null)&&(length>0)&&(radius>0))
			{
				RealMatrix worldSpaceMat = lMatrixStack.get(lMatrixStack.size()-1).multiply(objSpaceMat);
				float[] finalMat = GvMatrix.convertRowMajorToColumnMajor(worldSpaceMat.getData());

				float detailDegree = computeDetailDegree(radius);

				GvGeometryTex geomTube = GvGeometryFactory.getTubeTextured(radius, length,  detailDegree, length);
				
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
			return false;
		}
		else if(vertex.getProperty("Type").equals("GU"))
		{
			//drawing
			RealMatrix objSpaceMat = lCache.get(this.getGroIMPNodeId(vertex));
			RealMatrix worldSpaceMat = lMatrixStack.get(lMatrixStack.size()-1).multiply(objSpaceMat);
			float[] finalMat = GvMatrix.convertRowMajorToColumnMajor(worldSpaceMat.getData());
			
			float length = ((Float)vertex.getProperty("Length")).floatValue();
			float radius = ((Float)vertex.getProperty("Radius")).floatValue();
			
			float detailDegree = computeDetailDegree(radius);

			GvGeometryTex geomTube = GvGeometryFactory.getTubeTextured(radius, length,  detailDegree, length);
			
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
			
			countGU++;
			return true;
		}

		return true;
	}

	private float computeDetailDegree(float radius) {
		float numDivisions = (float) (Math.PI * radius / lErrorThresPixel);
		float degreeDetail = 180.0f/numDivisions;
		
		//clamp
		if(degreeDetail < 1.0f)
			degreeDetail = 1.0f;
		else if (degreeDetail > 90.0f)
			degreeDetail = 90.0f;
		
		return degreeDetail;
	}

	private float computeErrorThres() {
		double[][] currMat = lMatrixStack.get(lMatrixStack.size()-1).getData();
		Vector3D currPos = new Vector3D(currMat[0][3],currMat[1][3],currMat[2][3]);
		Vector3D camPos = new Vector3D(lCamera.lPosition[0],lCamera.lPosition[1],lCamera.lPosition[2]);
		Vector3D camView = new Vector3D(lCamera.lView[0],lCamera.lView[1],lCamera.lView[2]);
		
		double fovRadians = lCamera.lFov/180.0*Math.PI;
		double orthoDistToObj = (currPos.subtract(camPos)).dotProduct(camView);
		double screenRes = 640;
		double pixelErrorThres = 2;
		
		return (float) (pixelErrorThres*((2*orthoDistToObj*Math.tan(fovRadians/2.0))/screenRes));
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