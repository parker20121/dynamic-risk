package parker.dynamicrisk.tika;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;

/**
 *
 * @author Matt Parker
 */
public class TikaEtl {

    final AutoDetectParser parser = new AutoDetectParser();

    public static void main(String args[]) throws Exception {

        String inputFileDirectory = args[0];
        String outputFileDirectory = args[1];
        
        File dir = new File(inputFileDirectory);

        if (!dir.exists()) {      
            System.out.println("Cannot find directory: '" + inputFileDirectory + "'");
            return;
        }

        for (File file : dir.listFiles()) {

            if ( file.getAbsolutePath().endsWith("docx") ){
                
            } else if ( file.getAbsolutePath().endsWith("pdf")) {
                
            } else {
                
            }
            
            InputStream input = new FileInputStream( file );
            Metadata metadata = new Metadata();
            ParseContext context = new ParseContext();
            context.set(Parser.class, extractParser);
            parser.parse(input, handler, metadata, context);

        }
    }

}
