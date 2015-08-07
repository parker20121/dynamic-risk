package parker.dynamicrisk.etl;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import javax.imageio.ImageIO;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.microsoft.OfficeParser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.parser.microsoft.ooxml.OOXMLParser;

/**
 *
 * @author Matt Parker
 */
public class DocXParser implements ContentExtractor {

    public void parse(File outputDirectory, File docFile, int hashcode) throws Exception {
        
            //Setup text output file.
        String textfile = hashcode + ".txt";
        File outfile = new File( outputDirectory, textfile );        
        BufferedWriter writer = new BufferedWriter( new FileWriter( outfile ));
        
            //Extract text content
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        FileInputStream inputstream = new FileInputStream(docFile);
        ParseContext pcontext = new ParseContext();

        OOXMLParser parser = new OOXMLParser();
        parser.parse(inputstream, handler, metadata, pcontext);
        
        writer.write( handler.toString() );
        
            //Getting metadata of the document
        String[] metadataNames = metadata.names();
        writer.write("\n");
        
        for (String name : metadataNames) {
            writer.write( name + " : " + metadata.get(name) + "\n");
        }       
        
        writer.write("\n\n");
        writer.close();
        
        XWPFDocument doc = new XWPFDocument(new FileInputStream(docFile));
        BufferedImage jpg = null;
        
        int imageId = 1;
        
        for ( XWPFPictureData pic : doc.getAllPictures() ){        
            String extension = pic.suggestFileExtension();
            File imageFile = new File( outputDirectory, "image_" + imageId + "." + extension );
            BufferedImage image = ImageIO.read( new ByteArrayInputStream( pic.getData() )); 
            ImageIO.write(image, extension, imageFile);
        }        
            
    }
    
}
