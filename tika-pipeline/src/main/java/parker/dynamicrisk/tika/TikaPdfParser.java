package parker.dynamicrisk.tika;

import java.io.File;
import java.io.FileInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.parser.pdf.PDFParserConfig;
import org.apache.tika.sax.BodyContentHandler;

/**
 *
 * @author Matt Parker
 */
public class TikaPdfParser {

    public static void main(String args[]) throws Exception {

        if ( args.length != 1 ){
            System.out.println("usage: parker.dynamicrisk.tika.TikaPdfParser [pdf file name]");
            return;
        }
                
        File pdfFile = new File(args[0]);
                
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        FileInputStream inputstream = new FileInputStream(pdfFile);
        ParseContext pcontext = new ParseContext();

            //Parsing the document using PDF parser              
        PDFParser pdfparser = new PDFParser();  
        
        PDFParserConfig config = pdfparser.getPDFParserConfig();
        config.setSuppressDuplicateOverlappingText(true);
        config.setExtractInlineImages(true);
        config.setEnableAutoSpace(true);
        
        pdfparser.parse(inputstream, handler, metadata, pcontext);
               
            //Getting the content of the document
        System.out.println("Contents of the PDF :" + handler.toString());
        
            //Getting metadata of the document
        System.out.println("Metadata of the PDF:");
        String[] metadataNames = metadata.names();

        for (String name : metadataNames) {
            System.out.println(name + " : " + metadata.get(name));           
        }
        
    }

}
