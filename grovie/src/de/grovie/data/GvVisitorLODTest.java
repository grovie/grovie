package de.grovie.data;

import java.util.ArrayList;

import org.apache.commons.math3.linear.RealMatrix;

import com.tinkerpop.blueprints.Vertex;

import de.grovie.util.graph.GvVisitor;
import de.grovie.util.math.GvMatrix;

public class GvVisitorLODTest extends GvVisitor {

	public int countT;
	public int countRU;
	public int countRL;
	public int countRH;
	public int countPlant;
	public int countAxis;
	public int countGU;
	
	ArrayList<RealMatrix> lMatrixStack;
	
	public GvVisitorLODTest()
	{
		countT=0;
		countRU=0;
		countRL=0;
		countRH=0;
		countPlant=0;
		countAxis=0;
		countGU=0;
		lMatrixStack = new ArrayList<RealMatrix>();
		lMatrixStack.add(GvMatrix.getIdentityRealMatrix());
	}
	
	@Override
	public void visit(Vertex vertex) {
		if(vertex.getProperty("Type").equals("Translate"))
		{
			System.out.println("LOD Plant scale - Node Translate: " + vertex.getId());
			float x = ((Float)vertex.getProperty("x")).floatValue();
			float y = ((Float)vertex.getProperty("y")).floatValue();
			float z = ((Float)vertex.getProperty("z")).floatValue();
			RealMatrix lastMatrix = lMatrixStack.get(lMatrixStack.size()-1);
			lMatrixStack.add(lastMatrix.multiply(GvMatrix.getMatrixTranslation(x,y,z)));
			countT++;
		}
		else if(vertex.getProperty("Type").equals("RU"))
		{
			System.out.println("LOD Plant scale - Node RU: " + vertex.getId());
			float angle = ((Float)vertex.getProperty("angle")).floatValue();
			RealMatrix lastMatrix = lMatrixStack.get(lMatrixStack.size()-1);
			lMatrixStack.add(lastMatrix.multiply(GvMatrix.getMatrixRotationRU(angle)));
			countRU++;
		}
		else if(vertex.getProperty("Type").equals("RL"))
		{
			System.out.println("LOD Plant scale - Node RL: " + vertex.getId());
			float angle = ((Float)vertex.getProperty("angle")).floatValue();
			RealMatrix lastMatrix = lMatrixStack.get(lMatrixStack.size()-1);
			lMatrixStack.add(lastMatrix.multiply(GvMatrix.getMatrixRotationRL(angle)));
			countRL++;
		}
		else if(vertex.getProperty("Type").equals("RH"))
		{
			System.out.println("LOD Plant scale - Node RH: " + vertex.getId());
			float angle = ((Float)vertex.getProperty("angle")).floatValue();
			RealMatrix lastMatrix = lMatrixStack.get(lMatrixStack.size()-1);
			lMatrixStack.add(lastMatrix.multiply(GvMatrix.getMatrixRotationRH(angle)));
			countRH++;
		}
		else if(vertex.getProperty("Type").equals("Plant"))
		{
			System.out.println("LOD Plant scale - Node Plant: " + vertex.getId());
			System.out.println(lMatrixStack.get(lMatrixStack.size()-1).toString());
//			System.out.println("GUBaseId: " + vertex.getProperty("GUBaseId"));
			countPlant++;
			
		}
		else if(vertex.getProperty("Type").equals("Axis"))
		{
			System.out.println("LOD Plant scale - Node Axis: " + vertex.getId());
			countAxis++;
		}
		else if(vertex.getProperty("Type").equals("GU"))
		{
			System.out.println("LOD Plant scale - Node GU: " + vertex.getId());
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

}
