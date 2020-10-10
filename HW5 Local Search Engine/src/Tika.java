import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;


public class Tika {

	public static void main(final String[] args) throws FileNotFoundException, IOException, SAXException, TikaException {
		// TODO Auto-generated method stub
		String filePaths = "E:/programs/eclipse-jee-neon-3-win32-x86_64/workspace/CSCI572-HW4/data/NBC_News/HTML_files/HTML_files/";
		
		PrintWriter outputWriter = new PrintWriter("E:/programs/eclipse-jee-neon-3-win32-x86_64/workspace/CSCI572-HW4/data/NBC_News/Tika/big2.txt");
		File documents = new File(filePaths);
		for(File doc : documents.listFiles()) {
			if(doc.isDirectory()){
				continue;
			}
			outputWriter.write( parseExample(doc) );
		}
		outputWriter.close();
	}

    /**
     * If you don't want content from embedded documents, send in
     * a {@link org.apache.tika.parser.ParseContext} that does not contain a
     * {@link Parser}.
     *
     * @return The content of a file.
     */
	 /**
	 This function is from Tika's original example from
	 https://svn.apache.org/repos/asf/tika/trunk/tika-example/src/main/java/org/apache/tika/example/ParsingExample.java
	 **/
	
    public static String parseExample(File FileName) throws IOException, SAXException, TikaException {
        //AutoDetectParser parser = new AutoDetectParser();
		HtmlParser parser = new HtmlParser();		
        BodyContentHandler handler = new BodyContentHandler(-1);
        Metadata metadata = new Metadata();		    ParseContext pcontext=new ParseContext();
        try (InputStream stream = new FileInputStream(FileName.getPath())) {
            parser.parse(stream, handler, metadata, pcontext);
			String out = handler.toString();
			out = out.replaceAll("\\d", " ");
			out = out.replaceAll("\\W", " ");
			out = out.replaceAll("\\s", " ");
			out = out.replaceAll("\\s+", " ");			
			//space between docs
			out += " ";
            return out;
        }
    }	

}
