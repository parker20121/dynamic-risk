package parker.dynamicrisk.etl;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.io.FileSystemUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author Matt Parker
 */
public class RealtorDataExtractor {
        
    public static void main( String args[] ){
        
        if ( args.length != 2){
            System.out.println("usage: java parker.dynamicrisk.tika.RealtorDataExtractor [output dir] [file directory]");
            return;
        }
        
        File fileDirectory = new File( args[1] );
        
        if ( !fileDirectory.exists() ){
            System.out.println("Cannot find import file directory: " + args[1] );
            return;
        }
        
        File outputDirectory = new File( args[0] );
        
        if ( !outputDirectory.exists() ){
            System.out.println("Creating output directory.");
            outputDirectory.mkdirs();
        } else {
            System.out.println("Output directory already exists.");
        }
        
        Map extractors = new TreeMap();
        extractors.put("pdf", new PdfParser() );
        extractors.put("doc", new DocParser() );
        extractors.put("docx", new DocXParser() );
        
        for ( File file : fileDirectory.listFiles() ){
                        
            String fileExtension = FilenameUtils.getExtension(file.getName());
            
            ContentExtractor ce = (ContentExtractor)extractors.get(fileExtension);
            
            if ( ce != null ){
                
                try {
                    
                    int hashcode = file.getName().hashCode();
        
                    String subdirectoryPath = outputDirectory + File.separator + hashcode;
                    File subdirectory = new File(subdirectoryPath);

                    if ( !subdirectory.exists() ){
                        subdirectory.mkdirs();
                    }
                    
                    FileUtils.copyFile(file, new File(subdirectory, file.getName()) );
                    ce.parse( subdirectory, file, hashcode );
                    
                } catch ( Exception e ){
                    System.out.println( "Error processing '" + file.getName() + "'. " + e.toString() );
                    e.printStackTrace();
                }
                
            } else {
                System.out.println("No extractor found for " + file.getName() + ". Skipping.");
            }
            
        }
        
    }
    
}
