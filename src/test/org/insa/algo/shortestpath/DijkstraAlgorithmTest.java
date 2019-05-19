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

public class DijkstraAlgorithmTest {

    private static Graph carre;
    private static Graph toulouse;
    //Le graph
    private static Graph graph;

    private static Node[] noeuds;

    private static Float[][] tab;

    private static ShortestPathData data;
    private static DijkstraAlgorithm dijkstraAlgorithm;
    private static BellmanFordAlgorithm bellmanFordAlgorithm;
    private static ShortestPathSolution solutionDijkstra;
    private static ShortestPathSolution solutionBellmand;

    private static double delta;

    private static ArcInspector lengthAllAllowed;
    private static ArcInspector lengthCarRoadOnly;
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
    @BeforeClass
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
        graph = new Graph("ID","", Arrays.asList(noeuds),null);

        // erreur autorisée
        delta = 0.0005d;

        //Scenario map carre
        String mapName = "[chemin map carré]";
        reader = new BinaryGraphReader(new DataInputStream(new BufferedInputStream(new FileInputStream(mapName))));
        carre = reader.read();
        
        //Scenario map toulouse
        mapName = "[chemin map toulouse]";
        reader = new BinaryGraphReader(new DataInputStream(new BufferedInputStream(new FileInputStream(mapName))));
        toulouse = reader.read();

        lengthAllAllowed = ArcInspectorFactory.getAllFilters().get(0);
        lengthCarRoadOnly = ArcInspectorFactory.getAllFilters().get(1);
        timeAllAllowed = ArcInspectorFactory.getAllFilters().get(2);
        timeCarRoadOnly = ArcInspectorFactory.getAllFilters().get(3);
        timePedestrianRoad = ArcInspectorFactory.getAllFilters().get(4);
    }

    //TEST Simple Exemple
    @Test
    public void ShortestPathTestGrapheTest(){

        for (int j = 0; j < noeuds.length; j++) {
            for (int i = 0; i < noeuds.length; i++) {

                if (i != j) {

                    data = new ShortestPathData(graph, noeuds[j], noeuds[i], ArcInspectorFactory.getAllFilters().get(0));
                    dijkstraAlgorithm = new DijkstraAlgorithm(data);
                    bellmanFordAlgorithm = new BellmanFordAlgorithm(data);
                    solutionBellmand = bellmanFordAlgorithm.doRun();
                    solutionDijkstra = dijkstraAlgorithm.doRun();

                    assertEquals(solutionBellmand.isFeasible(),solutionDijkstra.isFeasible());

                    if (solutionBellmand.isFeasible()) {
                        assertEquals(solutionBellmand.getPath().getLength(), solutionDijkstra.getPath().getLength(), delta);
                        tab[j][i] = solutionBellmand.getPath().getLength();
                    }

                } else {
                    tab[i][j]=0.0f;
                }
            }
        }
    }

    //Différentes fonctions de test

    public void testValiditeTemps(Graph g) {

        Node origine = g.get(0);
        Node destination = g.get(g.size()-1);

        data = new ShortestPathData(g, origine, destination, ArcInspectorFactory.getAllFilters().get(0));
        dijkstraAlgorithm = new DijkstraAlgorithm(data);
        bellmanFordAlgorithm = new BellmanFordAlgorithm(data);
        solutionBellmand = bellmanFordAlgorithm.doRun();
        solutionDijkstra = dijkstraAlgorithm.doRun();

        assertEquals(solutionBellmand.isFeasible(), solutionDijkstra.isFeasible());

        if (solutionBellmand.isFeasible()){
            assertEquals(solutionBellmand.getPath().getMinimumTravelTime(), solutionDijkstra.getPath().getMinimumTravelTime(), delta);
            // Lancement du test de plus court chemin en temps
            ValiditeTemps (solutionDijkstra, g);
        }
    }

    public void MapCheminNullDistance(Graph g) {

        Node origine = g.get(0);
        Node destination = g.get(0);

        data = new ShortestPathData(g, origine, destination, ArcInspectorFactory.getAllFilters().get(0));
        dijkstraAlgorithm = new DijkstraAlgorithm(data);
        bellmanFordAlgorithm = new BellmanFordAlgorithm(data);
        solutionBellmand = bellmanFordAlgorithm.doRun();
        solutionDijkstra = dijkstraAlgorithm.doRun();
        assertEquals(solutionBellmand.isFeasible(), solutionDijkstra.isFeasible());

        if (solutionBellmand.isFeasible()){
            assertEquals(solutionBellmand.getPath().getLength(), solutionDijkstra.getPath().getLength(), delta);
        }
    }


    public void MapCheminOriginNoSuccessorsDistance(Graph g){

        Node origine = g.get(0);
        // On recherche un noeud sans successeur
        for (Node node : g.getNodes()){
            if (!node.hasSuccessors()) {
                origine =  node;
            }
        }
        // Soit on a trouvé un noeud sans successeur
        // Soit le noeud est l'origine et on se ramène à un problème d'étude origine = destination
        Node destination = g.get(0);

        data = new ShortestPathData(g, origine, destination, ArcInspectorFactory.getAllFilters().get(0));
        dijkstraAlgorithm = new DijkstraAlgorithm(data);
        bellmanFordAlgorithm = new BellmanFordAlgorithm(data);
        solutionBellmand = bellmanFordAlgorithm.doRun();
        solutionDijkstra = dijkstraAlgorithm.doRun();
        assertEquals(solutionBellmand.isFeasible(), solutionDijkstra.isFeasible());
    }

    public void MapCheminOkDistance(Graph g,  ArcInspector filtre){

        Node origine = g.get(0);
        Node destination = g.get(g.size()-1);

        data = new ShortestPathData(g, origine, destination, filtre);
        dijkstraAlgorithm = new DijkstraAlgorithm(data);
        bellmanFordAlgorithm = new BellmanFordAlgorithm(data);
        solutionBellmand = bellmanFordAlgorithm.doRun();
        solutionDijkstra = dijkstraAlgorithm.doRun();
        
        assertEquals(solutionBellmand.isFeasible(), solutionDijkstra.isFeasible());
        
        if (solutionBellmand.isFeasible()){
            assertEquals(solutionBellmand.getPath().getLength(), solutionDijkstra.getPath().getLength(), delta);
        }
    }

    public void MapCheminOkTemps(Graph g, ArcInspector filtre){

        Node origine = g.get(0);
        Node destination = g.get(g.size() - 1);

        data = new ShortestPathData(g, origine, destination, filtre);
        dijkstraAlgorithm = new DijkstraAlgorithm(data);
        bellmanFordAlgorithm = new BellmanFordAlgorithm(data);
        solutionBellmand = bellmanFordAlgorithm.doRun();
        solutionDijkstra = dijkstraAlgorithm.doRun();
        
        assertEquals(solutionBellmand.isFeasible(), solutionDijkstra.isFeasible());
        
        if (solutionBellmand.isFeasible()){
            assertEquals(solutionBellmand.getPath().getMinimumTravelTime(), solutionDijkstra.getPath().getMinimumTravelTime(), delta);
        }
    }

    // Vérifie si le plus court chemin entre deux points du ShortestPathSolution fourni est bien le plus court chemin en distance
    public void ValiditeDistance (ShortestPathSolution soluce, Graph g) {

    	// Si le chemin est un pcc alors n'importe quel sous chemin est un plus court chemin
    	Path path =  soluce.getPath();
    	List<Arc> lArcs =  soluce.getPath().getArcs();
    	List<Arc> CheminInterne = new LinkedList<Arc>();

    	Node origine =  path.getOrigin();
    	Node destination =  path.getDestination();

    	int size = lArcs.size();

    	// On prend une destination au milieu du path
    	int i = 0;
        destination = chooseADest(lArcs, CheminInterne, destination, size, i);

        // On trouve un pcc entre deux points du path
        data = new ShortestPathData(g, origine, destination, ArcInspectorFactory.getAllFilters().get(0));
        dijkstraAlgorithm = new DijkstraAlgorithm(data);
        solutionDijkstra = dijkstraAlgorithm.doRun();
    
        // Si Dijkstra admet un chemin alors
        // on teste si le plus court chemin trouvé correspond à la portion de Path de Dijkstra entre ces deux points
        if (solutionDijkstra.isFeasible()){

        	double lengthMeters =0;

        	for (Arc arcs : CheminInterne) {
        		lengthMeters += arcs.getLength();
        	}
            // la comparaison se fait en distance
            assertEquals(solutionDijkstra.getPath().getLength(), lengthMeters, delta);
           
        }
    }
    
    // Permet de choisir un node au milieu du Path donné en argument
    public Node chooseADest(List<Arc> lArcs, List<Arc> cheminInterne, Node destination, int size, int i) {
        for (Arc arc : lArcs) {
            if (i<size/2) {
            destination = arc.getDestination();
            cheminInterne.add(arc);
            i++;
            }
        }
        return destination;
    }

    public void testValiditeDistance(Graph carre) {

        Node origine = carre.get(0);
        Node destination = carre.get(carre.size() - 1);
        
        data = new ShortestPathData(carre, origine, destination, ArcInspectorFactory.getAllFilters().get(0));
        dijkstraAlgorithm = new DijkstraAlgorithm(data);
        bellmanFordAlgorithm = new BellmanFordAlgorithm(data);
        solutionBellmand = bellmanFordAlgorithm.doRun();
        solutionDijkstra = dijkstraAlgorithm.doRun();
        
        assertEquals(solutionBellmand.isFeasible(), solutionDijkstra.isFeasible());
        
        if (solutionBellmand.isFeasible()){
            assertEquals(solutionBellmand.getPath().getLength(), solutionDijkstra.getPath().getLength(), delta);
            // Lancement du test de plus court chemin
            ValiditeDistance (solutionDijkstra, carre);
        }
    }
    
    // Vérifie si le plus court chemin entre deux points du ShortestPathSolution fourni est bien un plus court chemin en temps
    public void ValiditeTemps (ShortestPathSolution soluce, Graph map) {

        // Si le chemin = plus court chemin (pcc)
        // n'importe quel sous chemin est un plus court chemin
    	Path path = soluce.getPath();
    	List<Arc> lArcs = soluce.getPath().getArcs();
    	List<Arc> CheminInterne=new LinkedList<Arc>();
    	Node origine = path.getOrigin();
    	Node destination = path.getDestination();

    	int size = lArcs.size();

    	// Destination au milieu du path
    	int i = 0;
        destination = chooseADest(lArcs, CheminInterne, destination, size, i);
        
        // On trouve un pcc entre deux points du path
        data = new ShortestPathData(map, origine, destination, ArcInspectorFactory.getAllFilters().get(0));
        dijkstraAlgorithm = new DijkstraAlgorithm(data);
        solutionDijkstra = dijkstraAlgorithm.doRun();

        // Si Dijkstra admet un chemin alors
        // Test si le plus court chemin trouvé = portion de Path de Dijkstra entre ces deux points
        if (solutionDijkstra.isFeasible()){
           
        	double minimumTravelTime = 0;
                
        	for (Arc arcs : CheminInterne) {
        		minimumTravelTime += arcs.getMinimumTravelTime();
        	}
            // la comparaison se fait en temps
            assertEquals(solutionDijkstra.getPath().getMinimumTravelTime(), minimumTravelTime,delta);
        }
    }

        // Tests map carré 
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
            MapCheminOkDistance(carre,lengthAllAllowed);
        }
    
        @Test
        public void CarreDistanceCarOnly(){
            MapCheminOkDistance(carre,lengthCarRoadOnly);
        }
    
        @Test
        public void CarreTempsAllAllowed(){
            MapCheminOkTemps(carre,timeAllAllowed);
        }
    
        @Test
        public void carreTempsCarOnly(){
            MapCheminOkTemps(carre,timeCarRoadOnly);
        }
    
        @Test
        public void CarreTempsPedestrian(){
            MapCheminOkTemps(carre,timePedestrianRoad);
        }
    
        @Test
        public void CarreTestValiditeTemps() {
            testValiditeTemps(carre);
        }
        @Test
        public void CarreTestValiditeDistance() {
            testValiditeDistance(carre);
        }

        // Fin tests map carré 
        
        
        // Tests map insa
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
            MapCheminOkDistance(toulouse, lengthAllAllowed);
        }
    
        @Test
        public void InsaDistanceCarOnly(){
            MapCheminOkTemps(toulouse, lengthCarRoadOnly);
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