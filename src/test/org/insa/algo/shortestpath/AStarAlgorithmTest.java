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
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class AStarAlgorithmTest {

    private static Graph fractoul;
    private static Graph paname;

    private static ShortestPathData data;
    private static AStarAlgorithm AStarAlgorithm;
    private static BellmanFordAlgorithm bellmanFordAlgorithm;
    private static ShortestPathSolution solutionAStar;
    private static ShortestPathSolution solutionBellmand;

    private static double delta;

    private static ArcInspector lenghtallAllowed;
    private static ArcInspector lenghtCarRoadOnly;
    private static ArcInspector timeAllAllowed;
    private static ArcInspector timeCarRoadOnly;
    private static ArcInspector timePedestrianRoad;


    @BeforeClass
    public static void initAll() throws Exception{

        GraphReader reader;

        //erreur autorisée
        delta = 0.0005d;

        //Scenario map fractal spirale 
        String mapName = "/home/toutant/Bureau/Cours/3MIC/S2/BE-Graphe/Maps/fractal-spiral.mapgr";
        reader = new BinaryGraphReader(new DataInputStream(new BufferedInputStream(new FileInputStream(mapName))));
        fractoul = reader.read();

        mapName = "/home/toutant/Bureau/Cours/3MIC/S2/BE-Graphe/Maps/paris.mapgr";
        reader = new BinaryGraphReader(new DataInputStream(new BufferedInputStream(new FileInputStream(mapName))));
        paname = reader.read();

        lenghtallAllowed =  ArcInspectorFactory.getAllFilters().get(0);
        lenghtCarRoadOnly = ArcInspectorFactory.getAllFilters().get(1);
        timeAllAllowed = ArcInspectorFactory.getAllFilters().get(2);
        timeCarRoadOnly = ArcInspectorFactory.getAllFilters().get(3);
        timePedestrianRoad = ArcInspectorFactory.getAllFilters().get(4);
    }

    //fonction utilse pour TempsValide et DistanceValide
    public Node ChoisirSousChemin(List<Arc> listarcs, List<Arc> cheminInterne, Node destination, int size) {

        int i = 0;

        for (Arc arc : listarcs) {
            if (i < size/2) {
                destination = arc.getDestination();
                cheminInterne.add(arc);
                i++;
            }
        }
        return destination;
    }

    public void TempsValide(ShortestPathSolution sol, Graph g) {

        // Si le chemin est un pcc alors n'importe quel sous chemin est un plus court chemin
        Path path = sol.getPath();
        List<Arc> listarcs = sol.getPath().getArcs();
        List<Arc> CheminInterne = new LinkedList<Arc>();

        Node origine = path.getOrigin();
        Node destination = path.getDestination();

        int size = listarcs.size();

        // On prend une destination au milieu du path
        destination = ChoisirSousChemin(listarcs, CheminInterne, destination, size);

        data = new ShortestPathData(g, origine, destination, lenghtallAllowed);
        AStarAlgorithm  = new AStarAlgorithm(data);
        solutionAStar = AStarAlgorithm.doRun();

        if (solutionAStar.isFeasible()){

            double minimumTravelTime  =  0;

            for (Arc arcs : CheminInterne) {
                minimumTravelTime +=  arcs.getMinimumTravelTime();
            }

            assertEquals(solutionAStar.getPath().getMinimumTravelTime(), minimumTravelTime, delta);
        }
    }

    public void TestValiditeTemps(Graph g) {
          
        Node origine = g.get(0);
        Node destination = g.get(g.size() - 1);

        data = new ShortestPathData(g, origine, destination, lenghtallAllowed);
        AStarAlgorithm = new AStarAlgorithm(data);
        bellmanFordAlgorithm = new BellmanFordAlgorithm(data);
        solutionBellmand = bellmanFordAlgorithm.doRun();
        solutionAStar = AStarAlgorithm.doRun();

        assertEquals(solutionBellmand.isFeasible(), solutionAStar.isFeasible());

        if (solutionBellmand.isFeasible()){
            assertEquals(solutionBellmand.getPath().getMinimumTravelTime(), solutionAStar.getPath().getMinimumTravelTime(), delta);
            TempsValide(solutionAStar, g);
        }
    }

    public void DistanceValide(ShortestPathSolution sol, Graph g) {

        // Si le chemin est un pcc alors n'importe quel sous chemin est un plus court chemin
        Path path  =  sol.getPath();
        List<Arc> listarcs = sol.getPath().getArcs();
        List<Arc> CheminInterne = new LinkedList<Arc>();

        Node origine = path.getOrigin();
        Node destination = path.getDestination();

        int size = listarcs.size();

        // On prend une destination au milieu du path
        destination = ChoisirSousChemin(listarcs, CheminInterne, destination, size);

        data = new ShortestPathData(g, origine, destination,  lenghtallAllowed);
        AStarAlgorithm = new AStarAlgorithm(data);
        solutionAStar = AStarAlgorithm.doRun();

        if (solutionAStar.isFeasible()){

            double lengthMeters = 0;

            for (Arc arcs : CheminInterne) {
                lengthMeters += arcs.getLength();
            }

            assertEquals(solutionAStar.getPath().getLength(), lengthMeters, delta);
        }
    }

    public void TestValiditeDistance(Graph g) {

        Node origine =  g.get(0);
        Node destination =  g.get(g.size()-1);

        data = new ShortestPathData(g, origine, destination,  lenghtallAllowed);
        AStarAlgorithm  = new AStarAlgorithm(data);
        bellmanFordAlgorithm = new BellmanFordAlgorithm(data);
        solutionBellmand = bellmanFordAlgorithm.doRun();
        solutionAStar = AStarAlgorithm.doRun();

        assertEquals(solutionBellmand.isFeasible(), solutionAStar.isFeasible());

        if (solutionBellmand.isFeasible()){
            assertEquals(solutionBellmand.getPath().getLength(), solutionAStar.getPath().getLength(), delta);
            DistanceValide(solutionAStar, g);
        }
    }

    public void CheminDistanceNull(Graph g) {
  
        Node origine = g.get(0);
        Node destination = g.get(0);
        
        data = new ShortestPathData(g, origine, destination,  lenghtallAllowed);
        AStarAlgorithm  = new AStarAlgorithm(data);
        bellmanFordAlgorithm = new BellmanFordAlgorithm(data);
        solutionBellmand = bellmanFordAlgorithm.doRun();
        solutionAStar = AStarAlgorithm.doRun();
        
        assertEquals(solutionBellmand.isFeasible(), solutionAStar.isFeasible());
        
        if (solutionBellmand.isFeasible()){
            assertEquals(solutionBellmand.getPath().getLength(), solutionAStar.getPath().getLength(), delta);
        }
    }

    public void OrigineNoFils(Graph g){

        Node origine = g.get(0);
        Node destination = g.get(0);

        for (Node node : g.getNodes()){
            if (!node.hasSuccessors()){
                origine = node;
            }
        }

        data = new ShortestPathData(g, origine, destination,  lenghtallAllowed);
        AStarAlgorithm  = new AStarAlgorithm(data);
        bellmanFordAlgorithm = new BellmanFordAlgorithm(data);
        solutionBellmand = bellmanFordAlgorithm.doRun();
        solutionAStar = AStarAlgorithm.doRun();

        assertEquals(solutionBellmand.isFeasible(), solutionAStar.isFeasible());
    }

    public void TestDistance(Graph g, ArcInspector filtre){

        Node origine = g.get(0);
        Node destination = g.get(g.size()-1);

        data = new ShortestPathData(g, origine, destination, filtre);
        AStarAlgorithm  = new AStarAlgorithm(data);
        bellmanFordAlgorithm = new BellmanFordAlgorithm(data);
        solutionBellmand = bellmanFordAlgorithm.doRun();
        solutionAStar = AStarAlgorithm.doRun();

        assertEquals(solutionBellmand.isFeasible(), solutionAStar.isFeasible());

        if (solutionBellmand.isFeasible()){
            assertEquals(solutionBellmand.getPath().getLength(), solutionAStar.getPath().getLength(), delta);
        }
    }

    public void TestTemps(Graph g, ArcInspector filtre){

        Node origine = g.get(0);
        Node destination = g.get(g.size() - 1);

        data = new ShortestPathData(g, origine, destination, filtre);
        AStarAlgorithm  = new AStarAlgorithm(data);
        bellmanFordAlgorithm = new BellmanFordAlgorithm(data);
        solutionBellmand = bellmanFordAlgorithm.doRun();
        solutionAStar = AStarAlgorithm.doRun();

        assertEquals(solutionBellmand.isFeasible(), solutionAStar.isFeasible());

        if (solutionBellmand.isFeasible()){
             assertEquals(solutionBellmand.getPath().getMinimumTravelTime(), solutionAStar.getPath().getMinimumTravelTime(), delta);
        }
    }

    /** Test map fractale spirale */
    @Test
    public void MapFractCheminNullDistance(){
        CheminDistanceNull(fractoul);
    }

    @Test
    public void FractNoSuccessorsDistance(){
        OrigineNoFils(fractoul);
    }

    @Test
    public void FractDistanceAllAllowed(){
        TestDistance(fractoul, lenghtallAllowed);
    }

    @Test
    public void FractDistanceCarOnly(){
        TestDistance(fractoul, lenghtCarRoadOnly);
    }

    @Test
    public void FractTempsAllAllowed(){
        TestTemps(fractoul, timeAllAllowed);
    }

    @Test
    public void FractTempsCarOnly(){
        TestTemps(fractoul, timeCarRoadOnly);
    }

    @Test
    public void FractTempsPedestrian(){
        TestTemps(fractoul, timePedestrianRoad);
    }

    @Test
    public void FractTestValiditeTemps() {
        TestValiditeTemps(fractoul);
    }
    @Test
    public void FractTestValiditeDistance() {
        TestValiditeDistance(fractoul);
    }

    /** Tests paname */
    @Test
    public void PanameOrigineNoSucc(){
        OrigineNoFils(paname);
    }

    @Test
    public void PanameCheminDistNull(){
        CheminDistanceNull(paname);
    }

    @Test
    public void PanameTestDistTout(){
        TestDistance(paname, lenghtallAllowed);
    }

    @Test
    public void PanameTestDistVoitu(){
        TestTemps(paname, lenghtCarRoadOnly);
    }

    @Test
    public void PanameTestTempsTout(){
        TestTemps(paname, timeAllAllowed);
    }

    @Test
    public void PanameTestTempsVoitu(){
        TestTemps(paname, timeCarRoadOnly);
    }

    //TODO: debug cette fonction
   /*  @Test
    public void BrazilTempsPedestrian(){
        TestTemps(paname, timePedestrianRoad);
    } */

    @Test
    public void PanameValiditeDistance() {
        TestValiditeDistance(paname);
    }

    @Test
    public void PanameValiditeTemps() {
        TestValiditeTemps(paname);
    }
}