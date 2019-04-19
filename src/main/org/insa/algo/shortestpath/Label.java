package org.insa.algo.shortestpath;

import org.insa.graph.Node;

public class Label implements Comparable<Label> {
	

	protected double cout;
	private Node courant;
	private Node pere;
	// si marque est vrai ; le sommet a été visité
	private boolean marque;
	
	public Label (double cout,Node actu, Node predecesseur, boolean visite) {
		this.cout=cout;
		this.courant=actu;
		this.pere=predecesseur;
		this.marque=visite;
	}

	//Getters
	public double getCost() {
		return cout;
	}

	public Node getPere() {
		return pere;
	}

	public boolean isVisite() {
		return marque;
	}
	
	public Node getCourant() {
		return courant;
	}
	
	//Setters
	public void setCost(double cout) {
		this.cout = cout;
	}
	
	public void setPere(Node predecesseur) {
		this.pere = predecesseur;
	}
	
	public void setVisite(boolean visite) {
		this.marque = visite;
	}


	
	@Override
    public int compareTo(Label l) {
        if(this.cout > l.cout) {
            return 1;
        }
        else if (this.cout == l.cout) {
            return 0;
        }
        else {
            return -1;
        }
    }
	

    //Override pour s'assurer qu'il n'y a qu'un seul label pour chaque noeud
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!Label.class.isAssignableFrom(obj.getClass())) {
			return false;
		}
		final Label other = (Label) obj;
		if (this.getCourant().equals(other.getCourant())){
			return true;
		}
		else{
			return false;
		}
	}
	
	@Override
	public String toString() {
	    if (pere==null){
            return "Noeud actu : "+this.getCourant().getId()+" Noeud predec "+this.getPere()+" - Cout "+this.cout;
        }else {
            return "Noeud actu : " + this.getCourant().getId() + " Noeud predec " + this.getPere().getId() + " - Cout " + this.cout;
        }
	}
}


	
	

	
	
