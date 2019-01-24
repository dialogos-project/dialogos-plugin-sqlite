package edu.cmu.lti.dialogos.db.sqlite;

import com.clt.dialogos.plugin.PluginRuntime;
import com.clt.dialogos.plugin.PluginSettings;
import com.clt.diamant.IdMap;
import com.clt.diamant.graph.Graph;
import com.clt.diamant.graph.Node;
import com.clt.gui.FileChooser;
import com.clt.gui.Images;
import com.clt.io.FileExtensionFilter;
import com.clt.xml.XMLReader;
import com.clt.xml.XMLWriter;
import org.xml.sax.SAXException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Plugin implements com.clt.dialogos.plugin.Plugin {

    final static Database db = new Database();

    @Override
    public void initialize() {
        Node.registerNodeTypes(com.clt.speech.Resources.getResources().createLocalizedString("ScriptNode"),
                Arrays.asList(new Class<?>[]{SqliteNode.class}));
    }

    @Override
    public String getId() {
        return "dialogos.plugin.sqlite";
    }

    @Override
    public String getName() {
        return "SQLite Plugin";
    }

    @Override
    public Icon getIcon() {
        return null;
    }

    @Override
    public String getVersion() { return "2.1.0"; }   // DO NOT EDIT - This line is updated automatically by the make-release script.

    @Override
    public PluginSettings createDefaultSettings() {
        return new PluginSettings() {

            @Override
            public void writeAttributes(XMLWriter xmlWriter, IdMap idMap) {
                Graph.printAtt(xmlWriter, "dbURL", db.jdbcURL);
            }

            @Override
            protected void readAttribute(XMLReader xmlReader, String name, String value, IdMap idMap) throws SAXException {
                if (name.equals("dbURL")) {
                    db.setDatabaseURL(value);
                }
            }

            @Override
            public JComponent createEditor() {
                JPanel p = new JPanel();
                JTextField urlField = new JTextField(db.jdbcURL, 40);
                AbstractAction openSqliteFile = new AbstractAction("Open SQLite...", Images.load("OpenFile.png")) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        FileChooser fc = new FileChooser();
                        fc.setFileFilter(new FileExtensionFilter("db", "Sqlite database file"));
                        File file = fc.standardGetFile(p);
                        if (file != null)
                            try {
                                urlField.setText("jdbc:sqlite:" + file.getCanonicalPath());
                                db.setDatabaseURL(urlField.getText());
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                    }
                };
                urlField.addActionListener((ActionEvent e) -> {
                    db.setDatabaseURL(urlField.getText());
                });
                p.add(new JButton(openSqliteFile));
                p.add(urlField);
                // ability to select a file
                return p;
            }

            @Override
            protected PluginRuntime createRuntime(Component component) throws Exception {
                return new PluginRuntime() {
                    @Override
                    public void dispose() {
                        db.closeDatabase();
                    }
                };
            }
        };
    }
}
