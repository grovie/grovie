package de.grovie.data.object;

public class GvGeometryFactory {

	public static GvGeometryTex getTubeTextured(float radius, float length, float detailDegrees, float detailLength)
	{
		int numVerticesBase = (int)(360.0f / detailDegrees);
		int numLayers = (int)(length / detailLength);
		int numVertices = numVerticesBase * (numLayers+1);
		int sizeVertices = numVertices * 3;
		int sizeIndices = numLayers * ((numVerticesBase+1)*2); //each vertical layer of tube drawn as a triangle strip
		
		double detailRadians = detailDegrees/180.0f * Math.PI;
		float[] vertices = new float[sizeVertices];
		float[] normals = new float[sizeVertices];
		int[] indices = new int[sizeIndices];
		float[] uv = new float[numVertices * 2];
		
		//compute base coordinates
		for(int i=0; i<numVerticesBase; ++i)
		{
			int indexOffset = i*3;
			int indexOffset1 = indexOffset+1;
			int indexOffset2 = indexOffset1+1;
			
			double angle = i * detailRadians;

			normals[indexOffset] = (float)Math.cos(angle);
			normals[indexOffset1] = 0;
			normals[indexOffset2] = (float)Math.sin(angle);
			
			vertices[indexOffset] = (float) (radius * normals[indexOffset]);
			vertices[indexOffset1] = 0;
			vertices[indexOffset2] = (float) (radius * normals[indexOffset2]);
		}
		
		//compute coordinates of vertex layers above base
		for(int j=1; j< numLayers+1; ++j)
		{
			int indexOffsetLayer = j*(numVerticesBase*3);
			
			for(int i=0; i<numVerticesBase; ++i)
			{
				int indexOffset = i*3;
				int indexOffset2 = indexOffset+2;
				int indexOffsetFinal = indexOffsetLayer + (i*3);
				int indexOffsetFinal1 = indexOffsetFinal+1;
				int indexOffsetFinal2 = indexOffsetFinal+2;

				normals[indexOffsetFinal] = normals[indexOffset];
				normals[indexOffsetFinal1] = 0;
				normals[indexOffsetFinal2] = normals[indexOffset2];
				
				vertices[indexOffsetFinal] = vertices[indexOffset];
				vertices[indexOffsetFinal1] = j * detailLength;
				vertices[indexOffsetFinal2] = vertices[indexOffset2];
			}
		}
		
		//indices
		for(int i=0; i<numLayers; ++i)
		{
			int indexOffset = i * ((numVerticesBase+1)*2);
			
			int vertexOffset = i*numVerticesBase;
			
			for(int j=0; j<numVerticesBase; ++j)
			{
				int indexOffsetInLayer = indexOffset + (j*2);
				int vertexOffsetInLayer = vertexOffset + j;
				
				indices[indexOffsetInLayer] = vertexOffsetInLayer;
				indices[indexOffsetInLayer+1] = vertexOffsetInLayer + numVerticesBase;
			}
			indices[indexOffset+(numVerticesBase*2)] = vertexOffset;
			indices[indexOffset+(numVerticesBase*2)+1] = vertexOffset + numVerticesBase;
		}
		
		//uv
		float circum = (float) (Math.PI * radius * 2);
		float arcDist = circum/numVerticesBase;
		for(int layer=0; layer<numLayers+1; ++layer)
		{
			int indexOffsetLayer = layer * numVerticesBase * 2;
			
			for(int point=0; point<numVerticesBase; ++point)
			{
				float uvx = point * arcDist;
				float uvy = layer * detailLength;
				
				int indexOffsetPoint = indexOffsetLayer + (point * 2);
				
				uv[indexOffsetPoint] = uvx;
				uv[indexOffsetPoint+1] = uvy;
			}
		}
		
		GvGeometryTex geom = new GvGeometryTex(vertices, normals, indices, uv);
		return geom;
	}
	
}
