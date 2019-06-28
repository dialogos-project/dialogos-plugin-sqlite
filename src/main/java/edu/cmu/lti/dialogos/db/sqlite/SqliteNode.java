package edu.cmu.lti.dialogos.db.sqlite;

import com.clt.diamant.*;
import com.clt.diamant.graph.Graph;
import com.clt.diamant.graph.Node;
import com.clt.diamant.graph.nodes.NodeExecutionException;
import com.clt.diamant.gui.NodePropertiesDialog;
import com.clt.script.exp.Value;
import com.clt.script.exp.values.StringValue;
import com.clt.xml.XMLReader;
import com.clt.xml.XMLWriter;
import org.xml.sax.SAXException;

import javax.swing.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/** this type of node takes an SQL expression and puts the result */
public class SqliteNode extends Node {

    /** actually, a query expression */
    public static final String QUERY = "queryExp";
    /** variable to store the result in */
    private static final String RESULT_VAR = "resultVar";

    public SqliteNode() {
        this.addEdge(); // have one port for an outgoing edge
        this.setProperty(QUERY, ""); // avoid running into null-pointers later
        this.setProperty(RESULT_VAR, "");
    }

    @Override
    public Node execute(WozInterface wozInterface, InputCenter inputCenter, ExecutionLogger executionLogger) {
        // assemble query from QUERY expression, if this fails, assume the expression itself is SQL
        String expressionString = this.getProperty(QUERY).toString();
        Value v;
        String query;
        try {
            v = this.parseExpression(expressionString).evaluate();
            query = ((StringValue) v).getString();
        } catch (Exception e) {
            // ignore the exception,
            // attempt to interpret the input as SQL directly
            query = expressionString;
        }
        //System.err.println(query);
        Value queryResponse = null;
        try {
            queryResponse = ((Plugin.SqlPluginRuntime) this.getPluginRuntime(Plugin.class, wozInterface)).getDatabase().executeStatement(query);
            //System.err.println(queryResponse);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NodeExecutionException(this, "problem with SQL: " + query, e);
        }
        // set variable to result of query
        String varName = this.getProperty(RESULT_VAR).toString();
        Slot var = getSlot(varName);
        var.setValue(queryResponse);
        // return next node
        return getEdge(0).getTarget();
    }

    /** get the variable slot from the graph that matches the name */
    private Slot getSlot(String name) {
        List<Slot> slots = this.getGraph().getAllVariables(Graph.LOCAL);
        for (Slot slot : slots) {
            if (name.equals(slot.getName()))
                return slot;
        }
        throw new NodeExecutionException(this, "unable to find variable with name " + name);
    }


    /**
     * display:
     * - a textfield for the expression that evaluates to SQL (or SQL directly)
     * - a dropdown for the variable to store into
     */
    @Override
    public JComponent createEditorComponent(Map<String, Object> properties) {
        JPanel p = new JPanel();
        JPanel horiz = new JPanel();
        horiz.add(new JLabel("SQL expression"));
        horiz.add(NodePropertiesDialog.createTextField(properties, QUERY));
        p.add(horiz);
        horiz = new JPanel();
        horiz.add(new JLabel("return value to:"));
        horiz.add(NodePropertiesDialog.createComboBox(properties, RESULT_VAR,
                this.getGraph().getAllVariables(Graph.LOCAL))
        );
        p.add(horiz);
        return p;
    }

    @Override
    public void writeAttributes(XMLWriter out, IdMap uid_map) {
        super.writeAttributes(out, uid_map);
        Graph.printAtt(out, RESULT_VAR, this.getProperty(RESULT_VAR).toString());
        Graph.printAtt(out, QUERY, this.getProperty(QUERY).toString());
    }

    @Override
    public void readAttribute(XMLReader r, String name, String value, IdMap uid_map) throws SAXException {
        if (name.equals(RESULT_VAR) || name.equals(QUERY)) {
            this.setProperty(name, value);
        } else {
            super.readAttribute(r, name, value, uid_map);
        }
    }

    @Override
    public void writeVoiceXML(XMLWriter xmlWriter, IdMap idMap) {
    }
}
