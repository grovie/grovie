package de.grovie.data;

import com.tinkerpop.blueprints.Vertex;

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

public class GvVisitorDraw extends GvVisitor {

	GvDrawGroup lDrawGroup;
	
	public GvVisitorDraw(GvDrawGroup drawGroup)
	{
		this.lDrawGroup = drawGroup;
	}
	
	@Override
	public void visit(Vertex vertex) {
		//get type of vertex
		String type = vertex.getProperty("Type");
		
		//if vertex is a tube
		if(type.equals("Tube"))
		{
			//get length and radius
			float length = ((Float)vertex.getProperty("Length")).floatValue();
			float radius = ((Float)vertex.getProperty("Radius")).floatValue();
			
			GvGeometryTex geomTube = GvGeometryFactory.getTubeTextured(radius, length,  10, 1);
			
			GvBufferSet bufferSet;
			try {
				bufferSet = lDrawGroup.getBufferSet(true, 1, 0, GvPrimitive.PRIMITIVE_TRIANGLE_STRIP, true);
				bufferSet.insertGeometry(geomTube.getVertices(), geomTube.getNormals(), geomTube.getIndices(), geomTube.getUv());
			} catch (GvExRendererVertexBuffer | GvExRendererVertexArray
					| GvExRendererIndexBuffer e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (GvExRendererDrawGroupRetrieval e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

}
