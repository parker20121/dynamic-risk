package parker.dynamicrisk.nlp;

import java.io.Console;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

/**
 *
 * @author Matt Parker
 */
public class TestParser {

    public static final void main(String args[]) {

        InputStream chunkerModelIn = null;
        InputStream posTaggerModelIn = null;
        
        POSModel posTaggerModel = null;
        ChunkerModel chunkerModel = null;
        
        ChunkerME chunker = null;
        POSTaggerME tagger = null;
        
        try {
            System.out.println("Starting....");
            
            posTaggerModelIn = new FileInputStream("en-pos-maxent.bin");
            posTaggerModel = new POSModel(posTaggerModelIn);
           
            chunkerModelIn = new FileInputStream("en-chunker.bin");
            chunkerModel = new ChunkerModel(chunkerModelIn);
                        
            tagger = new POSTaggerME(posTaggerModel);
            chunker = new ChunkerME(chunkerModel);
            
            Console c = System.console();

            if (c == null) {
                System.err.println("No console.");
                System.exit(1);
            }

            String cmd = "";
            cmd = c.readLine("Enter your query: ");
                        
            while ( !cmd.equalsIgnoreCase("exit") && !cmd.equalsIgnoreCase("quit") ) {
              
                String[] sentence = cmd.split(" ");
                String[] pos = tagger.tag( sentence );

                System.out.println("     CMD: " + cmd.toString() );
                System.out.println("Sentence: " + Arrays.toString(sentence) );
                System.out.println("POS Tags: " + Arrays.toString(pos) );

                String[] tags = chunker.chunk(sentence, pos);
                System.out.println("Chunker tags: " + Arrays.toString(tags) );
                
                cmd = c.readLine("Enter your query: ");
                
            }

        } catch (IOException e) {          
            e.printStackTrace();
        } finally {
            
            if (posTaggerModelIn != null) {
                try {
                    posTaggerModelIn.close();
                } catch (IOException e) {
                }
            }
            
            if (chunkerModelIn != null) {
                try {
                    chunkerModelIn.close();
                } catch (IOException e) {
                }
            }
            
        }

    }

}
