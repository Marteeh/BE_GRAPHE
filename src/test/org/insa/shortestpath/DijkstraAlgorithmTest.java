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
    private static Graph insa;
    //Le graph
    private static Graph graph;
    //Bellman
    private static Path pathBellman;
    //Dijkstra
    private static Path pathDijkstra;

    private static Node[] noeuds;

    private static Float[][] tab;

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
        Node.linkNodes(noeuds[0], noeuds[1], 7,s peed10, null);
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
    }

}