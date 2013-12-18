package de.grovie.data.importer.obj;

import java.util.ArrayList;

import com.obj.Face;
import com.obj.Group;
import com.obj.Vertex;
import com.obj.WavefrontObject;

import de.grovie.data.object.GvGeometry;

/**
 * This class provides functionality to load an obj file into memory.
 * 
 * @author yong
 *
 */
public class GvImporterObj {

	/**
	 * Loads the obj file at specified path into a GvGeometry instance
	 * @param fileAbsPath
	 * @param geom
	 * @return true if loaded successfully, false otherwise
	 */
	public static boolean load(String fileAbsPath, GvGeometry geom)
	{
		//Use obj-importer from http://www.pixelnerve.com/processing/libraries/objimport/
		WavefrontObject wavefrontObj = new WavefrontObject(fileAbsPath);

		try
		{
			convertObjToGvGeom(wavefrontObj, geom);	
		}
		catch(Exception e)
		{
			return false;
		}
		
		return true;
	}

	/**
	 * Converts WavefrontObject type to GroViE's GvGeometry type
	 * @param wvObj
	 * @param geom
	 */
	private static void convertObjToGvGeom(WavefrontObject wvObj, GvGeometry geom)
	{
		ArrayList<Vertex> vertices = wvObj.getVertices();
		ArrayList<Vertex> normals = wvObj.getNormals();
		ArrayList<Group> groups = wvObj.getGroups();
		
		//copy vertex coords
		int counter = 0;
		geom.initVertices(vertices.size() * 3);
		for(int i=0; i<vertices.size(); ++i)
		{
			Vertex v = vertices.get(i);
			geom.setVertexValue(counter, v.getX()); counter++;
			geom.setVertexValue(counter, v.getY()); counter++;
			geom.setVertexValue(counter, v.getZ()); counter++;
		}
		
		//copy normal coods
		counter = 0;
		geom.initNormals(normals.size() * 3);
		for(int i=0; i<normals.size(); ++i)
		{
			Vertex n = normals.get(i);
			geom.setNormalValue(counter, n.getX()); counter++;
			geom.setNormalValue(counter, n.getY()); counter++;
			geom.setNormalValue(counter, n.getZ()); counter++;
		}
		
		//count number of faces
		geom.initIndices(countIndices(wvObj));
		counter = 0;
		for(int i=0; i< groups.size(); ++i)
		{
			Group g = groups.get(i);
			ArrayList<Face> faces = g.getFaces();
			
			for(int j=0; j< faces.size(); ++j)
			{
				Face face = faces.get(j);
				if(face.getType() == Face.GL_TRIANGLES)
				{
					for(int k=0; k<face.vertIndices.length; ++k)
					{
						if(counter ==0)
							System.out.println("first index: " + face.vertIndices[k]);
						geom.setIndexValue(counter, face.vertIndices[k]);
						counter++;
					}
				}
			}
		}
	}
	
	/**
	 * Counts the number of indices in WavefrontObject instance
	 * @param wvObj
	 * @return number of indices
	 */
	private static int countIndices(WavefrontObject wvObj)
	{
		int indexCount = 0;
		int groupCount = (wvObj.getGroups()).size();
		for(int i=0; i<groupCount; ++i)
		{
			int faceCount = wvObj.getGroups().get(i).getFaces().size();
			indexCount += (faceCount * 3);
		}
		return indexCount;
	}
}
