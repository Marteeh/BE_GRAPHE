package org.insa.algo.shortestpath;

import org.insa.algo.AbstractInputData;
import org.insa.graph.Node;
import org.insa.graph.Point;

public class AStarAlgorithm extends DijkstraAlgorithm {

    Node dest;
    AbstractInputData.Mode mode;
    int vitesseMax;
    
    public AStarAlgorithm(ShortestPathData data) {

        super(data);
        this.dest = data.getDestination();
        this.mode = data.getMode();
        this.vitesseMax = data.getMaximumSpeed();

        if(vitesseMax == -1)
            vitesseMax = 200;
    }

    @Override
    protected Label newLabel(double cout,Node actu, Node predecesseur, boolean visite){

        double coutestim;
        if (mode == AbstractInputData.Mode.LENGTH){
            coutestim = Point.distance(dest.getPoint(),actu.getPoint());
        } else {
            coutestim = ((Point.distance(dest.getPoint(),actu.getPoint())*3600.0)/((double)(vitesseMax)*1000.0));
        }
        
        return new LabelStar(cout,actu,predecesseur,visite,coutestim);
    }
}