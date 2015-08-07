package parker.dynamicrisk.etl;

import java.io.File;

/**
 *
 * @author Matt Parker
 */
public interface ContentExtractor {
    
    public void parse( File outputDirectory, File file, int hashcode ) throws Exception;
    
}
