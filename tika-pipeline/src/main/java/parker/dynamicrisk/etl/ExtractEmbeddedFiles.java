package parker.dynamicrisk.etl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.Detector;
import org.apache.tika.exception.TikaException;
import org.apache.tika.extractor.EmbeddedDocumentExtractor;
import org.apache.tika.extractor.ParsingEmbeddedDocumentExtractor;
import org.apache.tika.io.FilenameUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * 
 * @author Matt Parker
 * 
 * @deprecated 
 * 
 */
public class ExtractEmbeddedFiles {

    private Parser parser = new AutoDetectParser();
    private Detector detector = ((AutoDetectParser)parser).getDetector();
    private TikaConfig config = TikaConfig.getDefaultConfig();

    public static void main (String args[]) throws Exception {  
        System.out.println("Running extactor...");
        ExtractEmbeddedFiles extractor = new ExtractEmbeddedFiles();       
        InputStream in = new FileInputStream(args[1]);
        Path outputDir = Paths.get(args[0]);       
        extractor.extract(in, outputDir);       
        System.out.println("done.");
    }
    
    public void extract(InputStream is, Path outputDir) throws SAXException, TikaException, IOException {
        Metadata m = new Metadata();
        ParseContext c = new ParseContext();
        ContentHandler h = new BodyContentHandler(-1);

        c.set(Parser.class, parser);
        EmbeddedDocumentExtractor ex = new MyEmbeddedDocumentExtractor(outputDir, c);
        c.set(EmbeddedDocumentExtractor.class, ex);
        parser.parse(is, h, m, c);
    }

    private class MyEmbeddedDocumentExtractor extends ParsingEmbeddedDocumentExtractor {
        
        private final Path outputDir;
        private int fileCount = 0;

        private MyEmbeddedDocumentExtractor(Path outputDir, ParseContext context) {
            super(context);
            this.outputDir = outputDir;
        }

        @Override
        public boolean shouldParseEmbedded(Metadata metadata) {
            return true;
        }

        @Override
        public void parseEmbedded(InputStream stream, ContentHandler handler, Metadata metadata, boolean outputHtml) throws SAXException, IOException {

            //try to get the name of the embedded file from the metadata
            String name = metadata.get(Metadata.RESOURCE_NAME_KEY);
            System.out.println("filename: " + name );
            
            if (name == null) {
                name = "file_" + fileCount++;
            } else {
                //make sure to select only the file name (not any directory paths
                //that might be included in the name) and make sure to normalize the name
                name = FilenameUtils.normalize(FilenameUtils.getName(name));
            }

            //now try to figure out the right extension for the embedded file
            MediaType contentType = detector.detect(stream, metadata);

            if (name.indexOf('.')==-1 && contentType!=null) {
                try {
                    name += config.getMimeRepository().forName(contentType.toString()).getExtension();
                } catch (MimeTypeException e) {
                    e.printStackTrace();
                }
            }
            
            //Should add check to make sure that you aren't overwriting a file
            Path outputFile = outputDir.resolve(name);
            
            //do a better job than this of checking
            Files.createDirectories(outputFile.getParent());
            Files.copy(stream, outputFile);
            
        }
    }
}