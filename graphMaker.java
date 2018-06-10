import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.UndirectedSparseMultigraph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.io.GraphMLWriter;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.EditingModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.HashMap;
import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;

import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.MapTransformer;
import org.apache.commons.collections15.map.LazyMap;
import org.xml.sax.SAXException;

/**
 * Application de visualisation graphique des graphes
 * @author Nassim Achahbar & Wail Chalabi
 */
public class graphMaker  extends JApplet  {
    //creation du graphe
    Graph<Integer, String> graphe = new SparseMultigraph<Integer, String>();
    //creation du Layout
    AbstractLayout<Integer, String> layout;
    //BasicVisualizationServer<V,E> est parametre par le type des arretes
    // creation d'un objet de type VisualiwationViewer pour donner des details sur le graphe /
    VisualizationViewer<Integer, String> visualisation;

    public graphMaker(UndirectedGraph<Number, Number> graphLoaded) {

        graphMaker gm1 = this;
        this.layout = new StaticLayout(graphLoaded, new Dimension(900, 900));
        this.visualisation = new VisualizationViewer(this.layout);

        //couleur de l'arriere plan
        this.visualisation.setBackground(new Color(0x00BED6FF));

        Container content = this.getContentPane();
        //zoom
        GraphZoomScrollPane panel = new GraphZoomScrollPane(this.visualisation);
        content.add(panel);
        Factory<Number> vertexFactory = new VertexFactory();
        Factory<Number> edgeFactory = new EdgeFactory();

        EditingModalGraphMouse<Number, Number> graphMouse1 = new EditingModalGraphMouse(this.visualisation.getRenderContext(), vertexFactory, edgeFactory);

        this.visualisation.setGraphMouse(graphMouse1);
        this.visualisation.addKeyListener(graphMouse1.getModeKeyListener());
        this.visualisation.getRenderContext().setVertexLabelTransformer(MapTransformer.getInstance(LazyMap.decorate(new HashMap(), new ToStringLabeller())));
        this.visualisation.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
        this.visualisation.getRenderContext().setEdgeLabelTransformer(MapTransformer.getInstance(LazyMap.decorate(new HashMap(), new ToStringLabeller())));
        this.visualisation.setVertexToolTipTransformer(this.visualisation.getRenderContext().getVertexLabelTransformer());

        final ScalingControl scaler = new CrossoverScalingControl();
        JButton plus = new JButton("zoom avant");
        //ajout de l'action au bouton pour faire un zoom avant
        plus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scaler.scale(graphMaker.this.visualisation, 1.2F, graphMaker.this.visualisation.getCenter());
            }
        });
        JButton minus = new JButton("zoom arriere");
        //ajout de l'action au bouton pour faire un zoom arriere
        minus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scaler.scale(graphMaker.this.visualisation, 0.8F, graphMaker.this.visualisation.getCenter());
            }
        });
        //les pannels de controles

        JMenuBar menuBar = new JMenuBar();
        menuBar.setToolTipText("File");
        JMenu menu = new JMenu("File");
        menuBar.add(menu,"West");
        JMenuItem load, save;

        menu.setSize(700,700);
        menu.setMnemonic(KeyEvent.VK_F);
        save = new JMenuItem("Save",
                KeyEvent.VK_S);
        load = new JMenuItem("Load",
                KeyEvent.VK_T);
        load.setSize(300,300);
        final JFileChooser fc = new JFileChooser();
        load.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (e.getSource() == load) {
                    int returnVal = fc.showOpenDialog(gm1);

                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File file = fc.getSelectedFile();
                        String filename = file.toString();

                        try {
                            new GraphFromGraphMLDemo(filename);
                        }
                        catch (ParserConfigurationException e1) {
                            e1.printStackTrace();
                        }
                        catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        catch (SAXException e1) {
                            e1.printStackTrace();
                        }
                    }
                }

                //String filename = "attributes.graphml";

            }
        });

        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == save) {
                    int returnVal = fc.showSaveDialog(gm1);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File file = fc.getSelectedFile();
                        //This is where a real application would save the file.
                        GraphMLWriter<Integer, String> graphWriter =
                                new GraphMLWriter<Integer, String > ();

                        graphWriter.addVertexData("x", null, "0",
                                new Transformer<Integer, String>() {
                                    public String transform(Integer v) {
                                        return Double.toString(gm1.layout.getX(v));
                                    }
                                }
                        );

                        graphWriter.addVertexData("y", null, "0",
                                new Transformer<Integer, String>() {
                                    public String transform(Integer v) {
                                        return Double.toString(gm1.layout.getY(v));
                                    }
                                }
                        );
                        try {
                            PrintWriter out = new PrintWriter(
                                    new BufferedWriter(
                                            new FileWriter(file)));
                            graphWriter.save(gm1.graphe, out);
                        }
                        catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }

            }
        });



        menu.add(load);
        menu.add(save);
        //  JPanel control2 = new JPanel();
        JPanel control1 = new JPanel();
        JPanel control3 = new JPanel();
        JComboBox modeBox = graphMouse1.getModeComboBox();
        control1.add(plus);
        control1.add(minus);
        control1.add(modeBox);
        control3.add(menuBar,"West ");
        content.add(control1, "South");
        // content.add(control2, "North");
        content.add(control3,"North");

    }


    public graphMaker() {
        //edition du graphe a travers les methodes de jung
        //dimensions du graphe
        graphMaker gm = this;
        this.layout = new StaticLayout(this.graphe, new Dimension(900, 900));
        this.visualisation = new VisualizationViewer(this.layout);

        //couleur de l'arriere plan
        this.visualisation.setBackground(new Color(0x00BED6FF));

        Container content = this.getContentPane();
        //zoom
        GraphZoomScrollPane panel = new GraphZoomScrollPane(this.visualisation);
        content.add(panel);
        Factory<Number> vertexFactory = new VertexFactory();
        Factory<Number> edgeFactory = new EdgeFactory();

        EditingModalGraphMouse<Number, Number> graphMouse1 = new EditingModalGraphMouse(this.visualisation.getRenderContext(), vertexFactory, edgeFactory);

        this.visualisation.setGraphMouse(graphMouse1);
        this.visualisation.addKeyListener(graphMouse1.getModeKeyListener());
        this.visualisation.getRenderContext().setVertexLabelTransformer(MapTransformer.getInstance(LazyMap.decorate(new HashMap(), new ToStringLabeller())));
        this.visualisation.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
        this.visualisation.getRenderContext().setEdgeLabelTransformer(MapTransformer.getInstance(LazyMap.decorate(new HashMap(), new ToStringLabeller())));
        this.visualisation.setVertexToolTipTransformer(this.visualisation.getRenderContext().getVertexLabelTransformer());

        final ScalingControl scaler = new CrossoverScalingControl();
        JButton plus = new JButton("zoom avant");
        //ajout de l'action au bouton pour faire un zoom avant
        plus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scaler.scale(graphMaker.this.visualisation, 1.2F, graphMaker.this.visualisation.getCenter());
            }
        });
        JButton minus = new JButton("zoom arriere");
        //ajout de l'action au bouton pour faire un zoom arriere
        minus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scaler.scale(graphMaker.this.visualisation, 0.8F, graphMaker.this.visualisation.getCenter());
            }
        });
        //les pannels de controles


        JMenuBar menuBar = new JMenuBar();
        menuBar.setToolTipText("File");
        JMenu menu = new JMenu("File");
        menuBar.add(menu,"West");
        JMenuItem load, save;

        menu.setSize(700,700);
        menu.setMnemonic(KeyEvent.VK_F);
        save = new JMenuItem("Save",
                KeyEvent.VK_S);
        load = new JMenuItem("Load",
                KeyEvent.VK_T);
        load.setSize(300,300);
        final JFileChooser fc = new JFileChooser();
        load.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (e.getSource() == load) {
                    int returnVal = fc.showOpenDialog(gm);

                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File file = fc.getSelectedFile();
                        String filename = file.toString();

                        try {
                            new GraphFromGraphMLDemo(filename);
                        }
                        catch (ParserConfigurationException e1) {
                            e1.printStackTrace();
                        }
                        catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        catch (SAXException e1) {
                            e1.printStackTrace();
                        }
                    }
                }

                //String filename = "attributes.graphml";

            }
        });

        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == save) {
                    int returnVal = fc.showSaveDialog(gm);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File file = fc.getSelectedFile();
                        //This is where a real application would save the file.
                        GraphMLWriter<Integer, String> graphWriter =
                                new GraphMLWriter<Integer, String > ();

                        graphWriter.addVertexData("x", null, "0",
                                new Transformer<Integer, String>() {
                                    public String transform(Integer v) {
                                        return Double.toString(gm.layout.getX(v));
                                    }
                                }
                        );

                        graphWriter.addVertexData("y", null, "0",
                                new Transformer<Integer, String>() {
                                    public String transform(Integer v) {
                                        return Double.toString(gm.layout.getY(v));
                                    }
                                }
                        );
                        try {
                            PrintWriter out = new PrintWriter(
                                    new BufferedWriter(
                                            new FileWriter(file)));
                            graphWriter.save(gm.graphe, out);
                        }
                        catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }

            }
        });


        menu.add(load);
        menu.add(save);
      //  JPanel control2 = new JPanel();
        JPanel control1 = new JPanel();
        JPanel control3 = new JPanel();
        JComboBox modeBox = graphMouse1.getModeComboBox();
        control1.add(plus);
        control1.add(minus);
        control1.add(modeBox);
        control3.add(menuBar,"West ");
        content.add(control1, "South");
       // content.add(control2, "North");
        content.add(control3,"North");


    }

    public static void main(String args[]) throws ParserConfigurationException, SAXException, IOException {

        JFrame frame = new JFrame("Application de visualisation des graphes");
        graphMaker gm = new graphMaker();

        frame.getContentPane().add(gm);
        //frame.setJMenuBar(menuBar);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.pack();
        frame.setVisible(true);
    }

    class EdgeFactory implements Factory<Number> {
        int i = 0;

        EdgeFactory() {
        }
        //permet l'ajout d'arretes
        public Number create() {
            return this.i++;
        }
    }

    class VertexFactory implements Factory<Number> {
        int i = 0;

        VertexFactory() {
        }
        //permet l'ajout de sommet
        public Number create() {
            return this.i++;
        }
    }
    //changement de la couleur du sommet en vert
    Transformer<Integer, Paint> vertexPaint = new Transformer<Integer, Paint>() {
        public Paint transform(Integer i) {
            return Color.GREEN;
        }
    };
}