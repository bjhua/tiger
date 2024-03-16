import control.CommandLine;
import parser.Parser;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

// the Tiger compiler main class.
public class Tiger {
    public static void main(String[] args) throws Exception {
        InputStream fileStream;
        Parser parser;

        // ///////////////////////////////////////////////////////
        // handle command line arguments
        CommandLine cmd = new CommandLine();
        // the file to be compiled:
        String fileName = cmd.scan(args);
        if (fileName == null) {
            // no file is given, then exit silently.
            return;
        }

        // /////////////////////////////////////////////////////////
        // otherwise, we continue the normal compilation phases.
        // first, create a new stream from the input file:
        fileStream = new BufferedInputStream(new FileInputStream(fileName));
        // then create a parser:
        parser = new Parser(fileName, fileStream);
        // parse the file using the parser:
        parser.parse();
        // close the stream before exiting.
        fileStream.close();
    }
}


