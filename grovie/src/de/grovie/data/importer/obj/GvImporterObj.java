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
		
		int indicesCount = countIndices(wvObj);
		geom.initIndices(indicesCount);
		geom.initVertices(indicesCount * 3);
		geom.initNormals(indicesCount * 3);
		
		int counter = 0;
		
		ArrayList<Group> groups = wvObj.getGroups();
		
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
						geom.setIndexValue(counter, counter);			//set index
						Vertex v = vertices.get(face.vertIndices[k]);	//set vertex coords
						geom.setVertexValue((counter*3), v.getX());
						geom.setVertexValue((counter*3)+1, v.getY());
						geom.setVertexValue((counter*3)+2, v.getZ());
						Vertex n = normals.get(face.normIndices[k]);	//set normal coords
						geom.setNormalValue((counter*3), n.getX());
						geom.setNormalValue((counter*3)+1, n.getY());
						geom.setNormalValue((counter*3)+2, n.getZ());
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
