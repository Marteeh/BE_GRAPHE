package org.insa.algo.shortestpath;

import org.insa.graph.Node;

public class LabelStar extends Label {

    private double estimCout;

    public LabelStar(double cout, Node courant, Node father, boolean marque, double estim) {
        super(cout, courant, father, marque);
        this.estimCout = estim;
    }

    //Getter pour estimation
    public double getEstimCout(){
        return estimCout;
    }

    @Override
    public int compareTo(Label l){
        
        double leCout = this.cout + this.estimCout;
        double labelCout = l.cout + ((LabelStar)l).estimCout;
        int retour = 0;

        if (labelCout < leCout){
            retour = 1;
        } 
        else if (labelCout == leCout){

            if ( ((LabelStar)l).estimCout < this.estimCout ){
                retour = 1;
            }
            else if ( ((LabelStar)l).estimCout == this.estimCout ){
                retour = 0;
            }
            else {
                retour = -1;
            }
        }
        else {
            retour = -1;
        }

    return retour;
    }

}