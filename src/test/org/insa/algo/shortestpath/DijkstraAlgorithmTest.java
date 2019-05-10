package org.insa.algo.shortestpath;

import org.insa.algo.ArcInspector;
import org.insa.algo.ArcInspectorFactory;
import org.insa.graph.*;
import org.insa.graph.io.BinaryGraphReader;
import org.insa.graph.io.GraphReader;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeNotNull;

public class DijkstraAlgorithmTest {

    private static Graph carre;
    private static Graph toulouse;
    //Le graph
    private static Graph graph;
    //Bellman
    private static Path pathBellman;
    //Dijkstra
    private static Path pathDijkstra;

    private static Node[] noeuds;

    private static Float[][] tab;

    private static ShortestPathData data;
    private static DijkstraAlgorithm dijkstraAlgorithm;
    private static BellmanFordAlgorithm bellmanFordAlgorithm;
    private static ShortestPathSolution solutionDijkstra;
    private static ShortestPathSolution solutionBellmand;

    private static double delta;

    private static ArcInspector lenghtallAllowed;
    private static ArcInspector lenghtCarRoadOnly;
    private static ArcInspector timeAllAllowed;
    private static ArcInspector timeCarRoadOnly;
    private static ArcInspector timePedestrianRoad;

    public void afficherTab(){
        for (int i = 0; i < tab.length; i++){
            for (int j = 0; j < tab[i].length; j++){
                System.out.print(tab[i][j]+" ");
            }
            System.out.print("\n");
        }
    }

    //fonction d'initialisation des tests
    public static void initAll() throws Exception{
        
        GraphReader reader;
        
        //10 mètres par seconde
        RoadInformation speed10 = new RoadInformation(RoadInformation.RoadType.MOTORWAY, null, true, 36, "");

        noeuds = new Node[6];
        for (int i = 0; i < noeuds.length; i++){
            noeuds[i] = new Node(i, null);
        }

        tab = new Float[noeuds.length][noeuds.length];
        for (int i = 0; i < tab.length; i++){
            for (int j = 0; j < tab.length; j++){
                tab[i][j] = Float.POSITIVE_INFINITY;
            }
        }

        //Exemple simple
        //Def du graph 
        Node.linkNodes(noeuds[0], noeuds[1], 7, speed10, null);
        Node.linkNodes(noeuds[0], noeuds[2], 8, speed10, null);
        Node.linkNodes(noeuds[1], noeuds[3], 4, speed10, null);
        Node.linkNodes(noeuds[1], noeuds[4], 1, speed10, null);
        Node.linkNodes(noeuds[1], noeuds[5], 5, speed10, null);
        Node.linkNodes(noeuds[2], noeuds[0], 7, speed10, null);
        Node.linkNodes(noeuds[2], noeuds[1], 2, speed10, null);
        Node.linkNodes(noeuds[2], noeuds[5], 2, speed10, null);
        Node.linkNodes(noeuds[4], noeuds[2], 2, speed10, null);
        Node.linkNodes(noeuds[4], noeuds[3], 2, speed10, null);
        Node.linkNodes(noeuds[4], noeuds[5], 3, speed10, null);
        Node.linkNodes(noeuds[5], noeuds[4], 3, speed10, null);
        graph=new Graph("ID","", Arrays.asList(noeuds),null);

        // erreur autorisée
        delta = 0.0005d;

        //Scenario map carre
        String mapName = "[chemin map carré]";
        reader  = new BinaryGraphReader(new DataInputStream(new BufferedInputStream(new FileInputStream(mapName))));
        carre = reader.read();
        
        //Scenario map toulouse
        mapName = "[chemin map toulouse]";
        reader = new BinaryGraphReader(new DataInputStream(new BufferedInputStream(new FileInputStream(mapName))));
        toulouse = reader.read();

        lenghtallAllowed=ArcInspectorFactory.getAllFilters().get(0);
        lenghtCarRoadOnly=ArcInspectorFactory.getAllFilters().get(1);
        timeAllAllowed=ArcInspectorFactory.getAllFilters().get(2);
        timeCarRoadOnly=ArcInspectorFactory.getAllFilters().get(3);
        timePedestrianRoad=ArcInspectorFactory.getAllFilters().get(4);
    }

    //TEST Simple Exemple
    @Test
    public void ShortestPathTestGrapheTest(){
        for (int j=0;j<noeuds.length;j++) {
            for (int i = 0; i < noeuds.length; i++) {
                if (i!=j) {
                    data = new ShortestPathData(graph, noeuds[j], noeuds[i], ArcInspectorFactory.getAllFilters().get(0));
                    dijkstraAlgorithm = new DijkstraAlgorithm(data);
                    bellmanFordAlgorithm = new BellmanFordAlgorithm(data);
                    solutionBellmand = bellmanFordAlgorithm.doRun();
                    solutionDijkstra = dijkstraAlgorithm.doRun();
                    assertEquals(solutionBellmand.isFeasible(),solutionDijkstra.isFeasible());
                    if (solutionBellmand.isFeasible()){
                        assertEquals(solutionBellmand.getPath().getLength(), solutionDijkstra.getPath().getLength(), delta);
                        tableau[j][i]=solutionBellmand.getPath().getLength();
                    }
                }else{
                    tableau[i][j]=0.0f;
                }
            }
        }
    }

}