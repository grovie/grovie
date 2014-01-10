package de.grovie.data;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.math3.linear.RealMatrix;

import com.tinkerpop.blueprints.Vertex;

import de.grovie.util.graph.GvVisitor;
import de.grovie.util.math.GvMatrix;

public class GvVisitorLODPrecompute extends GvVisitor {

	public int countT;
	public int countRU;
	public int countRL;
	public int countRH;
	public int countPlant;
	public int countAxis;
	public int countGU;
	
	HashMap<String, RealMatrix> lCache;
	
	ArrayList<RealMatrix> lMatrixStack;
	
	public GvVisitorLODPrecompute()
	{
		resetCounters();
		
		lCache = new HashMap<String, RealMatrix>(); //cache of matrices
		
		lMatrixStack = new ArrayList<RealMatrix>();
		lMatrixStack.add(GvMatrix.getIdentityRealMatrix());
	}
	
	@Override
	public void visit(Vertex vertex) {
		Object groimpId = vertex.getProperty("GID");
		String groimpIdStr=null;
		if(groimpId!=null)
		{
			groimpIdStr = ((Long)groimpId).toString();
		}
		
		if(vertex.getProperty("Type").equals("Translate"))
		{
			System.out.println("LOD Precompute - Node Translate: " + vertex.getId());
			
			RealMatrix cachedMatrix = lCache.get(groimpIdStr);
			if(cachedMatrix != null)
			{
				lMatrixStack.add(cachedMatrix);
				System.out.println("cached matrix found");
			}
			else
			{
				float x = ((Float)vertex.getProperty("x")).floatValue();
				float y = ((Float)vertex.getProperty("y")).floatValue();
				float z = ((Float)vertex.getProperty("z")).floatValue();
				RealMatrix lastMatrix = lMatrixStack.get(lMatrixStack.size()-1);
				RealMatrix newMatrix = lastMatrix.multiply(GvMatrix.getMatrixTranslation(x,y,z));
				lMatrixStack.add(newMatrix);
				lCache.put(groimpIdStr, newMatrix);
			}
			countT++;
		}
		else if(vertex.getProperty("Type").equals("RU"))
		{
			System.out.println("LOD Precompute - Node RU: " + vertex.getId());
			
			RealMatrix cachedMatrix = lCache.get(groimpIdStr);
			if(cachedMatrix != null)
			{
				lMatrixStack.add(cachedMatrix);
				System.out.println("cached matrix found");
			}
			else
			{
				float angle = ((Float)vertex.getProperty("angle")).floatValue();
				RealMatrix lastMatrix = lMatrixStack.get(lMatrixStack.size()-1);
				RealMatrix newMatrix = lastMatrix.multiply(GvMatrix.getMatrixRotationRU(angle));
				lMatrixStack.add(newMatrix);
				
				lCache.put(groimpIdStr, newMatrix);
			}
			countRU++;
		}
		else if(vertex.getProperty("Type").equals("RL"))
		{
			System.out.println("LOD Precompute - Node RL: " + vertex.getId());
			RealMatrix cachedMatrix = lCache.get(groimpIdStr);
			if(cachedMatrix != null)
			{
				lMatrixStack.add(cachedMatrix);
				System.out.println("cached matrix found");
			}
			else
			{
				float angle = ((Float)vertex.getProperty("angle")).floatValue();
				RealMatrix lastMatrix = lMatrixStack.get(lMatrixStack.size()-1);
				RealMatrix newMatrix = lastMatrix.multiply(GvMatrix.getMatrixRotationRL(angle));
				lMatrixStack.add(newMatrix);
				
				lCache.put(groimpIdStr, newMatrix);
			}
			countRL++;
		}
		else if(vertex.getProperty("Type").equals("RH"))
		{
			System.out.println("LOD Precompute - Node RH: " + vertex.getId());
			RealMatrix cachedMatrix = lCache.get(groimpIdStr);
			if(cachedMatrix != null)
			{
				lMatrixStack.add(cachedMatrix);
				System.out.println("cached matrix found");
			}
			else
			{
				
				float angle = ((Float)vertex.getProperty("angle")).floatValue();
				RealMatrix lastMatrix = lMatrixStack.get(lMatrixStack.size()-1);
				RealMatrix newMatrix = lastMatrix.multiply(GvMatrix.getMatrixRotationRH(angle));
				lMatrixStack.add(newMatrix);
				
				lCache.put(groimpIdStr, newMatrix);
			}
			countRH++;
		}
		else if(vertex.getProperty("Type").equals("Plant"))
		{
			System.out.println("LOD Precompute - Node Plant: " + vertex.getId());
			countPlant++;
			
		}
		else if(vertex.getProperty("Type").equals("Axis"))
		{
			System.out.println("LOD Precompute - Node Axis: " + vertex.getId());
			countAxis++;
		}
		else if(vertex.getProperty("Type").equals("GU"))
		{
			System.out.println("LOD Precompute - Node GU: " + vertex.getId());
			RealMatrix cachedMatrix = lCache.get(groimpIdStr);
			if(cachedMatrix != null)
			{
				lMatrixStack.add(cachedMatrix);
				System.out.println("cached matrix found");
			}
			else
			{
				float length = ((Float)vertex.getProperty("Length")).floatValue();
				RealMatrix lastMatrix = lMatrixStack.get(lMatrixStack.size()-1);
				RealMatrix newMatrix = lastMatrix.multiply(GvMatrix.getMatrixTranslation(0,length,0));
				lMatrixStack.add(newMatrix);
				
				lCache.put(groimpIdStr, newMatrix);
			}
			countGU++;
		}
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
}
