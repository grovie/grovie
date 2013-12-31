package de.grovie.util.file;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileResource {

	private static FileResource lFileResource;
	
	public static FileResource get()
	{
		if(lFileResource==null)
		{
			lFileResource = new FileResource();
		}
		return lFileResource;
	}
	
	private FileResource()
	{
	}
	
	public static String getResourceAsString(String resourceFileUrl) throws IOException
	{
		InputStream inputStream = FileResource.get().getClass().getResourceAsStream(resourceFileUrl);
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		
		String resultString = "";
		String inputString;
        while ((inputString = bufferedReader.readLine()) != null)
        {
            resultString += inputString;
            resultString += "\n";
        }
        bufferedReader.close();
        
        return resultString;
	}
}
