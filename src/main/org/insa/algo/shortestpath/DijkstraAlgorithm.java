package org.insa.algo.shortestpath;

import org.insa.algo.AbstractSolution;
import org.insa.algo.utils.BinaryHeap;
import org.insa.graph.Arc;
import org.insa.graph.Graph;
import org.insa.graph.Node;
import org.insa.graph.Path;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class DijkstraAlgorithm extends ShortestPathAlgorithm {

    public DijkstraAlgorithm(ShortestPathData data) {
        super(data);
    }

    protected Label newLabel(double cout, Node actu, Node predecesseur, boolean visite){
        return new Label(cout, actu, predecesseur, visite);
    }

    @Override
    protected ShortestPathSolution doRun() {

        ShortestPathData data = getInputData();
        ShortestPathSolution solution = null;

        Graph graph = data.getGraph();

        //Récupération du noeud de destination
        final Node destination = data.getDestination();

        //Initialisation des structures de données
        Map<Node, Label> labelNodes = new HashMap<Node,Label>();
        BinaryHeap<Label> filePriorite = new BinaryHeap<Label>();

        //Creation d'un Label pour l'origine et insertion dans la map et la file de priorité
        Label lab = newLabel(0, data.getOrigin(), null, Boolean.FALSE);
        labelNodes.put(data.getOrigin(), lab);
        filePriorite.insert(lab);

        return solution;
    }

}
