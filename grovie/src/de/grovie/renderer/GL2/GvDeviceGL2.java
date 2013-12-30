package de.grovie.renderer.GL2;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL2;

import de.grovie.exception.GvExceptionRendererShaderProgram;
import de.grovie.exception.GvExceptionRendererVertexBuffer;
import de.grovie.renderer.GvDevice;
import de.grovie.renderer.GvGraphicsWindow;
import de.grovie.renderer.GvRenderer;
import de.grovie.renderer.GvShaderProgram;
import de.grovie.renderer.GvVertexBuffer;
import de.grovie.renderer.windowsystem.GvWindowSystem;

public class GvDeviceGL2 extends GvDevice{
	
	public GvDeviceGL2(GvRenderer renderer) {
		super(renderer);
	}

	@Override
	public GvGraphicsWindow  createWindow(
			GvWindowSystem windowSystem,
			GvRenderer renderer) {
		
		GvWindowSystem winSys = windowSystem.getInstance(renderer);
		
		winSys.getCanvas().setEventListener(renderer.getIllustrator());
		
		return new GvGraphicsWindowGL2(winSys);
	}

	@Override
	public GvShaderProgram createShaderProgram(String vertexShaderSource,
			String fragmentShaderSource) throws GvExceptionRendererShaderProgram  {
		try{
			//get reference to jogl gl2
			GL2 gl2 = ((GvIllustratorGL2)lRenderer.getIllustrator()).getGL2();
				
			//create shaders
			int shaderVId = gl2.glCreateShader(GL2.GL_VERTEX_SHADER);
			int shaderFId = gl2.glCreateShader(GL2.GL_FRAGMENT_SHADER);

			//set shader sources
			gl2.glShaderSource(shaderVId, 1, new String[]{vertexShaderSource}, (int[]) null, 0);
			gl2.glShaderSource(shaderFId, 1, new String[]{fragmentShaderSource}, (int[]) null, 0);

			//compile shaders
			gl2.glCompileShader(shaderVId);
			gl2.glCompileShader(shaderFId);

			//create program
			int shaderProgramId = gl2.glCreateProgram();
			gl2.glAttachShader(shaderProgramId, shaderVId);
			gl2.glAttachShader(shaderProgramId, shaderFId);

			//link, validate and use program
			gl2.glLinkProgram(shaderProgramId);
			gl2.glValidateProgram(shaderProgramId);
			gl2.glUseProgram(shaderProgramId); 

			//print logs
			GvDeviceGL2.printLog(gl2,shaderVId);
			GvDeviceGL2.printLog(gl2,shaderFId);
			GvDeviceGL2.printLog(gl2,shaderProgramId);
			
			//unbind program
			gl2.glUseProgram(0);
			
			return new GvShaderProgram(shaderProgramId, vertexShaderSource, fragmentShaderSource);
		}
		catch(Exception e)
		{
			throw new GvExceptionRendererShaderProgram("Error generating shader program.");
		}
	}

	@Override
	public GvVertexBuffer createVertexBuffer(long sizeInBytes) throws GvExceptionRendererVertexBuffer {
		try{
			//get reference to jogl gl2
			GL2 gl2 = ((GvIllustratorGL2)lRenderer.getIllustrator()).getGL2();
			
			//generate vertex buffer id
			int vboId[] = new int[1];
			IntBuffer vboIdBuffer = IntBuffer.wrap(vboId);
			gl2.glGenBuffers(1, vboIdBuffer);
			
			//bind VBO
			gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, vboId[0]);

			//set total size of buffer (allocate)
			gl2.glBufferData(GL2.GL_ARRAY_BUFFER, 	//type of buffer
					sizeInBytes, 					//size in bytes of buffer
					null, 							//no data to be copied into VBO at this moment
					GL2.GL_STATIC_DRAW 				//buffer usage hint
					);
			
			//unbind VBO
			gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
			
			return new GvVertexBuffer(vboId[0], sizeInBytes);
		}
		catch(Exception e)
		{
			throw new GvExceptionRendererVertexBuffer("Error generating VBO.");
		}
	}

	/**
	 * Prints OpenGL log for shaders
	 * @param gl2
	 * @param obj
	 */
	private static void printLog(GL2 gl2, int obj)
	{
		int maxLen[] = new int[1];
		IntBuffer maxLength = IntBuffer.wrap(maxLen);

		if(gl2.glIsShader(obj))
			gl2.glGetShaderiv(obj,GL2.GL_INFO_LOG_LENGTH,maxLength);
		else
			gl2.glGetProgramiv(obj,GL2.GL_INFO_LOG_LENGTH,maxLength);

		int len = maxLen[0];
		byte infoLog[] = new byte[len];
		ByteBuffer infoLogBuffer = ByteBuffer.wrap(infoLog);

		int infoLen[] = new int[1];
		IntBuffer infoLength = IntBuffer.wrap(infoLen);

		if (gl2.glIsShader(obj))
			gl2.glGetShaderInfoLog(obj, maxLen[0], infoLength, infoLogBuffer);
		else
			gl2.glGetProgramInfoLog(obj, maxLen[0], infoLength, infoLogBuffer);

		if (infoLen[0] > 0)
		{
			System.out.println(new String(infoLog));
		}
	}
}
