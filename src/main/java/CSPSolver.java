import org.w3c.dom.Document;
import org.xcsp.common.Utilities;
import org.xcsp.parser.XParser;

import java.nio.file.Path;

public class CSPSolver {
    public CSPSolver(Path cspFile) throws Exception {
        Document doc = Utilities.loadDocument(cspFile.toString());
        XParser parser = new XParser(doc);

        System.out.println(parser.cEntries);
        System.out.println(parser.vEntries);
        System.out.println(parser.oEntries);
    }
}
