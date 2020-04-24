import org.w3c.dom.Document;
import org.xcsp.common.Utilities;
import org.xcsp.parser.XParser;
import org.xcsp.parser.entries.XVariables;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class CSPSolver {
    public CSPSolver(Path cspFile) throws Exception {
        Document doc = Utilities.loadDocument(cspFile.toString());
        XParser parser = new XParser(doc);

        HashSet<Variable> variables = new HashSet<>();
        HashSet<Constraint> constraints = new HashSet<>();
        HashMap<String, Variable> idToVariableMap = new HashMap<>();

        parser.vEntries.forEach(vEntry -> {
            Variable variable = new Variable(vEntry.id);
            variables.add(variable);
            idToVariableMap.put(vEntry.id, variable);
        });

        parser.cEntries.forEach(cEntry -> {
            LinkedHashSet<XVariables.XVar> vars = new LinkedHashSet<>();
            cEntry.collectVars(vars);

            System.out.println("Constraint entry:");
            //System.out.println(cEntry);
            System.out.println(cEntry.id);
            System.out.println(cEntry.attributes.keySet());
            System.out.println(cEntry.flags);

            System.out.println(cEntry.classes);
            vars.forEach(xVar -> {
                System.out.println(xVar);
                System.out.println(xVar.id() +" " + xVar.degree + " " + xVar.dom + " " + xVar.idPrefix() );
                Variable variable = new Variable(xVar.id);
                //variables.add(variable);
            });
        });
    }
}
