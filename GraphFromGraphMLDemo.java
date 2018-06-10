//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

//package edu.uci.ics.jung.samples;


import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;

import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseMultigraph;
import edu.uci.ics.jung.io.GraphMLReader;
import edu.uci.ics.jung.io.GraphMLWriter;

import edu.uci.ics.jung.visualization.VisualizationViewer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import java.io.*;
import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Transformer;
import org.xml.sax.SAXException;

public class GraphFromGraphMLDemo {
    VisualizationViewer<Number, Number> vv;

    public GraphFromGraphMLDemo(String filename) throws ParserConfigurationException, SAXException, IOException {
        Factory<Number> vertexFactory = new Factory<Number>() {
            int n = 0;

            public Number create() {
                return this.n++;
            }
        };
        Factory<Number> edgeFactory = new Factory<Number>() {
            int n = 0;

            public Number create() {
                return this.n++;
            }
        };
        GraphMLReader<UndirectedGraph<Number, Number>, Number, Number> gmlr = new GraphMLReader(vertexFactory, edgeFactory);
        final UndirectedGraph<Number, Number> graph = new UndirectedSparseMultigraph();
        gmlr.load(filename, graph);
        graph.getVertexCount();
        graph.getEdgeCount();

        graphMaker gm1 = new graphMaker(graph);
        JFrame frame1 = new JFrame("visualisation du graphe");
        JMenuBar menuBar = new JMenuBar();
        JMenuItem save;
        save = new JMenuItem("Save",
                KeyEvent.VK_S);
        JMenu menu = new JMenu("File");
        menu.setSize(300,300);
        menu.setMnemonic(KeyEvent.VK_F);
        menu.add(save);
        menuBar.add(menu);

        final JFileChooser fc = new JFileChooser();
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
        frame1.getContentPane().add(gm1);
        //frame1.setJMenuBar(menuBar);
        frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame1.pack();
        frame1.setVisible(true);


    }
}