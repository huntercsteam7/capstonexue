package com.MeadowEast.xue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class Common {
	
	
	public ByteArrayOutputStream readTextFile( InputStream is ) throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = is.read(buf)) != -1) {
                os.write(buf, 0, len);
            }
        }
        finally {
        	IOUtils.closeQuietly( is );
		    IOUtils.closeQuietly( os );
        }
        return os;
    }
	
	
	public static File CopyStreamToFile( InputStream is, File dirDestination, String strDestFileName ) throws Exception
	{
		//File sdCard = Environment.getExternalStorageDirectory();
		//File dir = new File (sdCard.getAbsolutePath() + "/dir1/dir2");

		FileOutputStream out = null;
		try {
		    // Ensure folder is created
			if ( !dirDestination.exists() )
				FileUtils.forceMkdir( dirDestination );
			
			File newFile = new File( dirDestination.getAbsolutePath(), strDestFileName );
		    out = new FileOutputStream( newFile );

		    IOUtils.copy(is, out);
		    
		    return newFile;

		} finally {
		    IOUtils.closeQuietly(is);
		    IOUtils.closeQuietly(out);
		}
	}

}
