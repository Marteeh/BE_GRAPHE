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

    private static Graph carre;
    private static Graph toulouse;

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

        //Scenario Map carré
        String mapName = "chemin";
        reader = new BinaryGraphReader(new DataInputStream(new BufferedInputStream(new FileInputStream(mapName))));
        carre = reader.read();

        mapName = "chemin";
        reader = new BinaryGraphReader(new DataInputStream(new BufferedInputStream(new FileInputStream(mapName))));
        toulouse = reader.read();

        lenghtallAllowed = ArcInspectorFactory.getAllFilters().get(0);
        lenghtCarRoadOnly = ArcInspectorFactory.getAllFilters().get(1);
        timeAllAllowed = ArcInspectorFactory.getAllFilters().get(2);
        timeCarRoadOnly = ArcInspectorFactory.getAllFilters().get(3);
        timePedestrianRoad = ArcInspectorFactory.getAllFilters().get(4);
    }

    public void testValiditeTemps(Graph g) {
          
        Node origine = g.get(0);
        Node destination = g.get(g.size() - 1);

        data = new ShortestPathData(g, origine, destination, ArcInspectorFactory.getAllFilters().get(0));
        AStarAlgorithm = new AStarAlgorithm(data);
        bellmanFordAlgorithm = new BellmanFordAlgorithm(data);
        solutionBellmand = bellmanFordAlgorithm.doRun();
        solutionAStar = AStarAlgorithm.doRun();

        assertEquals(solutionBellmand.isFeasible(), solutionAStar.isFeasible());

        if (solutionBellmand.isFeasible()){
            assertEquals(solutionBellmand.getPath().getMinimumTravelTime(), solutionAStar.getPath().getMinimumTravelTime(), delta);
            ValiditeTemps (solutionAStar, g);
        }
    }

    public void MapCheminNullDistance(Graph g) {
  
        Node origine = g.get(0);
        Node destination = g.get(0);
        
        data = new ShortestPathData(g, origine, destination, ArcInspectorFactory.getAllFilters().get(0));
        AStarAlgorithm  = new AStarAlgorithm(data);
        bellmanFordAlgorithm = new BellmanFordAlgorithm(data);
        solutionBellmand = bellmanFordAlgorithm.doRun();
        solutionAStar = AStarAlgorithm.doRun();
        
        assertEquals(solutionBellmand.isFeasible(), solutionAStar.isFeasible());
        
        if (solutionBellmand.isFeasible()){
            assertEquals(solutionBellmand.getPath().getLength(), solutionAStar.getPath().getLength(), delta);
        }
    }

    public void MapCheminOriginNoSuccessorsDistance(Graph g){

        Node origine = g.get(0);
        Node destination = g.get(0);

        for (Node node:g.getNodes()){
            if (!node.hasSuccessors()) {
                origine = node;
            }
        }

        data = new ShortestPathData(g, origine, destination, ArcInspectorFactory.getAllFilters().get(0));
        AStarAlgorithm  = new AStarAlgorithm(data);
        bellmanFordAlgorithm = new BellmanFordAlgorithm(data);
        solutionBellmand = bellmanFordAlgorithm.doRun();
        solutionAStar = AStarAlgorithm.doRun();

        assertEquals(solutionBellmand.isFeasible(), solutionAStar.isFeasible());
    }

    public void MapCheminOkDistance(Graph g,  ArcInspector filter){

        Node origine = g.get(0);
        Node destination = g.get(g.size()-1);

        data = new ShortestPathData(g, origine, destination, filter);
        AStarAlgorithm  = new AStarAlgorithm(data);
        bellmanFordAlgorithm = new BellmanFordAlgorithm(data);
        solutionBellmand = bellmanFordAlgorithm.doRun();
        solutionAStar = AStarAlgorithm.doRun();

        assertEquals(solutionBellmand.isFeasible(), solutionAStar.isFeasible());

        if (solutionBellmand.isFeasible()){
            assertEquals(solutionBellmand.getPath().getLength(), solutionAStar.getPath().getLength(), delta);
        }
    }

    public void MapCheminOkTemps(Graph g, ArcInspector filter){

        Node origine = g.get(0);
        Node destination = g.get(g.size()-1);

        data = new ShortestPathData(g, origine, destination, filter);
        AStarAlgorithm  = new AStarAlgorithm(data);
        bellmanFordAlgorithm = new BellmanFordAlgorithm(data);
        solutionBellmand = bellmanFordAlgorithm.doRun();
        solutionAStar = AStarAlgorithm.doRun();

        assertEquals(solutionBellmand.isFeasible(), solutionAStar.isFeasible());

        if (solutionBellmand.isFeasible()){
             assertEquals(solutionBellmand.getPath().getMinimumTravelTime(), solutionAStar.getPath().getMinimumTravelTime(), delta);
        }
    }


    public void ValiditeDistance (ShortestPathSolution sol, Graph g) {

        // Si le chemin est un pcc alors n'importe quel sous chemin est un plus court chemin
        Path path  =  sol.getPath();
        List<Arc> listarcs  =  sol.getPath().getArcs();
        List<Arc> CheminInterne = new LinkedList<Arc>();

        Node origine  =  path.getOrigin();
        Node destination  =  path.getDestination();

        int size  =  listarcs.size();

        // On prend une destination au milieu du path
        int i  =  0;
        destination = chooseADest(listarcs,  CheminInterne,  destination,  size,  i);

        data = new ShortestPathData(g, origine, destination, ArcInspectorFactory.getAllFilters().get(0));
        AStarAlgorithm  = new AStarAlgorithm(data);
        solutionAStar = AStarAlgorithm.doRun();

        if (solutionAStar.isFeasible()){

            double lengthMeters  = 0;

            for (Arc arcs : CheminInterne) {
                lengthMeters +=  arcs.getLength();
            }

            assertEquals(solutionAStar.getPath().getLength(), lengthMeters, delta);
        }
    }

    public Node chooseADest(List<Arc> listarcs, List<Arc> cheminInterne, Node destination, int size, int i) {

        for (Arc arc : listarcs) {
            if (i < size/2) {
                destination = arc.getDestination();
                cheminInterne.add(arc);
                i++;
            }
        }
        return destination;
    }

    public void testValiditeDistance(Graph carre) {

        Node origine =  carre.get(0);
        Node destination =  carre.get(carre.size()-1);

        data = new ShortestPathData(carre, origine, destination, ArcInspectorFactory.getAllFilters().get(0));
        AStarAlgorithm  = new AStarAlgorithm(data);
        bellmanFordAlgorithm = new BellmanFordAlgorithm(data);
        solutionBellmand = bellmanFordAlgorithm.doRun();
        solutionAStar = AStarAlgorithm.doRun();

        assertEquals(solutionBellmand.isFeasible(), solutionAStar.isFeasible());

        if (solutionBellmand.isFeasible()){
            assertEquals(solutionBellmand.getPath().getLength(), solutionAStar.getPath().getLength(), delta);
            ValiditeDistance (solutionAStar, carre);
        }
    }
    public void ValiditeTemps (ShortestPathSolution sol, Graph g) {
        // Si le chemin est un pcc alors n'importe quel sous chemin est un plus court chemin
        Path path  =  sol.getPath();
        List<Arc> listarcs  =  sol.getPath().getArcs();
        List<Arc> CheminInterne = new LinkedList<Arc>();
        Node origine  =  path.getOrigin();
        Node destination  =  path.getDestination();

        int size  =  listarcs.size();

        // On prend une destination au milieu du path
        int i  =  0;
        destination  =  chooseADest(listarcs,  CheminInterne,  destination,  size,  i);

        data = new ShortestPathData(g, origine, destination, ArcInspectorFactory.getAllFilters().get(0));
        AStarAlgorithm  = new AStarAlgorithm(data);
        solutionAStar = AStarAlgorithm.doRun();

        if (solutionAStar.isFeasible()){

            double minimumTravelTime  =  0;

            for (Arc arcs : CheminInterne) {
                minimumTravelTime + =  arcs.getMinimumTravelTime();
            }

            assertEquals(solutionAStar.getPath().getMinimumTravelTime(), minimumTravelTime, delta);
        }


    }

    /** Test map carré */
    @Test
    public void MapCarreCheminNullDistance(){
        MapCheminNullDistance(carre);
    }

    @Test
    public void CarreNoSuccessorsDistance(){
        MapCheminOriginNoSuccessorsDistance(carre);
    }

    @Test
    public void CarreDistanceAllAllowed(){
        MapCheminOkDistance(carre, lenghtallAllowed);
    }

    @Test
    public void CarreDistanceCarOnly(){
        MapCheminOkDistance(carre, lenghtCarRoadOnly);
    }

    @Test
    public void CarreTempsAllAllowed(){
        MapCheminOkTemps(carre, timeAllAllowed);
    }

    @Test
    public void carreTempsCarOnly(){
        MapCheminOkTemps(carre, timeCarRoadOnly);
    }

    @Test
    public void CarreTempsPedestrian(){
        MapCheminOkTemps(carre, timePedestrianRoad);
    }

    @Test
    public void CarreTestValiditeTemps() {
        testValiditeTemps(carre);
    }
    @Test
    public void CarreTestValiditeDistance() {
        testValiditeDistance(carre);
    }

    /** TEST INSA */
    @Test
    public void InsaNoSuccessorsDistance(){
        MapCheminOriginNoSuccessorsDistance(toulouse);
    }

    @Test
    public void MapInsaCheminNullDistance(){
        MapCheminNullDistance(toulouse);
    }

    @Test
    public void InsaDistanceAllAllowed(){
        MapCheminOkDistance(toulouse, lenghtallAllowed);
    }

    @Test
    public void InsaDistanceCarOnly(){
        MapCheminOkTemps(toulouse, lenghtCarRoadOnly);
    }

    @Test
    public void InsaTempsAllAllowed(){
        MapCheminOkTemps(toulouse, timeAllAllowed);
    }

    @Test
    public void InsaTempsCarOnly(){
        MapCheminOkTemps(toulouse, timeCarRoadOnly);
    }

    //TODO: debug cette fonction
    @Test
    public void InsaTempsPedestrian(){
        MapCheminOkTemps(toulouse, timePedestrianRoad);
    }

    @Test
    public void InsaTestValiditeDistance() {
        testValiditeDistance(toulouse);
    }

    @Test
    public void InsaTestValiditeTemps() {
        testValiditeTemps(toulouse);
    }

}