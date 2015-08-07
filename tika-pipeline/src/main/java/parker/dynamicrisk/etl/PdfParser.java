package parker.dynamicrisk.etl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.parser.pdf.PDFParserConfig;
import org.apache.tika.sax.BodyContentHandler;

/**
 *
 * @author Matt Parker
 */
public class PdfParser implements ContentExtractor {
    
    PDFParser pdfparser;      
    PDFParserConfig config;
    
    public PdfParser(){
        pdfparser = new PDFParser();
        config = pdfparser.getPDFParserConfig();
        config.setSuppressDuplicateOverlappingText(true);
        config.setEnableAutoSpace(true);  
    }
    
    public void parse( File outputDirectory, File pdfFile, int hashcode ) throws Exception {

            //Setup text output file.
        String textfile = hashcode + ".txt";
        File outfile = new File( outputDirectory, textfile );        
        BufferedWriter writer = new BufferedWriter( new FileWriter( outfile ));
        
            //Extract text content
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        FileInputStream inputstream = new FileInputStream(pdfFile);
        ParseContext pcontext = new ParseContext();

            //Parsing the document using PDF parser                              
        pdfparser.parse(inputstream, handler, metadata, pcontext);
        
            //Write content to file.
        writer.write( handler.toString() );
        
            //Getting metadata of the document
        String[] metadataNames = metadata.names();
        writer.write("\n");
        
        for (String name : metadataNames) {
            writer.write( name + " : " + metadata.get(name) + "\n");
        }       
        
        writer.write("\n\n");
        writer.close();
        
            //Extract images.
        try {            
            PDDocument document = PDDocument.load( pdfFile );
            
            List pages = document.getDocumentCatalog().getAllPages();
            Iterator iter = pages.iterator(); 
            int i = 1;

            while (iter.hasNext()) {
                PDPage page = (PDPage) iter.next();
                PDResources resources = page.getResources();
                Map pageImages = resources.getImages();
                if (pageImages != null) { 
                    Iterator imageIter = pageImages.keySet().iterator();
                    while (imageIter.hasNext()) {
                        String key = (String) imageIter.next();
                        PDXObjectImage image = (PDXObjectImage) pageImages.get(key);
                        if ( image.getHeight() > 100 && image.getWidth() > 100 ){
                            File imageFile = new File(outputDirectory, hashcode + "_image_" + i );
                            image.write2file(imageFile);
                            i++;
                        }
                    }
                }
            }            
            
            document.close();
            
        } catch (IOException ex) {
            System.out.println( ex.toString() );
            ex.printStackTrace();            
        }
                      
    }

}
