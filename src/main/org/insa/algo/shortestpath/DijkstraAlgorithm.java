package org.insa.algo.shortestpath;

import org.insa.algo.AbstractSolution;
import org.insa.algo.utils.BinaryHeap;
import org.insa.graph.Arc;
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

        //Récupération du noeud de destination
        final Node destination = data.getDestination();

        //Initialisation des structures de données
        Map<Node, Label> labelNodes = new HashMap<Node,Label>();
        BinaryHeap<Label> filePriorite = new BinaryHeap<Label>();

        //Creation d'un Label pour l'origine et insertion dans la map ET la file de priorité
        Label lab = newLabel(0, data.getOrigin(), null, Boolean.TRUE);
        labelNodes.put(data.getOrigin(), lab);
        filePriorite.insert(lab);
        
        //Cas ou origine = destination : retourner un chemin non valide
        if (data.getOrigin().compareTo(data.getDestination())==0) {
        	return new ShortestPathSolution(data, AbstractSolution.Status.INFEASIBLE);
        }
        
        //Sinon, on marque l'origine
        notifyOriginProcessed(data.getOrigin());
        
        //Creation d'un label pour la destination et insertion dans la map + file de prio
        lab = newLabel(Double.POSITIVE_INFINITY, data.getDestination(), null, Boolean.FALSE);
        labelNodes.put(data.getDestination(), lab);
        
        boolean destReached = false;
        
        //Cas ou les noeuds sont differents, on boucle jusqu'à ce que la file de priorité soit vide
        while((!filePriorite.isEmpty()) && (!destReached)){
        	//On trouve le prochain noeud avec le cout le plus faible à l'aide de la file de priorite 
        	//et on le retire
        	Label monLabel = filePriorite.deleteMin();
        	
        	if (monLabel.compareTo(labelNodes.get(destination))<0){
        		//On vérifie pour chq arc a partir du noeud...
        		//??????????????? -> diff de eva mais compile???????
        		for (Arc monArc : monLabel.getCourant().getSuccessors()) {
        			//... sa destination
        			Node Arrivee = monArc.getDestination();
        			if (labelNodes.get(Arrivee) == null) {
        				//Si le noeud n'est pas dans la hashmap, on la met à jour
        				labelNodes.put(Arrivee, newLabel(monLabel.getCost()+monArc.getLength(), Arrivee, monLabel.getCourant(), false));
        				notifyNodeReached(Arrivee);
        				filePriorite.insert(labelNodes.get(Arrivee));
        				
        			} else if (!labelNodes.get(Arrivee).isVisite()) {
        				
        				//Si il y est, on verifié le cout :
        					//Si cout > cout de l'arc + sommet ou on est, maj de hashmap
        				if (Arrivee.equals(destination) && labelNodes.get(destination).getCost() == Double.POSITIVE_INFINITY) {
        					notifyNodeReached(destination);
        					filePriorite.insert(labelNodes.get(destination));
        				}
        				double cout = labelNodes.get(monLabel.getCourant()).getCost() + monArc.getLength();
        				
        				if (labelNodes.get(Arrivee).getCost() > cout) {
        					//maj du label de la destination de l'arc
        					labelNodes.get(Arrivee).setCost(cout);
        					labelNodes.get(Arrivee).setPere(monLabel.getCourant());
        					//maj du label (cout + pere) dans la file de prio
        					filePriorite.remove(labelNodes.get(Arrivee));
        					filePriorite.insert(labelNodes.get(Arrivee));
        				}
        			}
        		}
        		
        		//Si prochain noeud = arrivee
        		if (monLabel.getCourant().equals(destination)) {
        			notifyDestinationReached(monLabel.getCourant());
        			destReached=true;
        		}
        	}
        	
        	//On marque le noeud
        	notifyNodeMarked(monLabel.getCourant());
        	//Sommet visite
        	labelNodes.get(monLabel.getCourant()).setVisite(Boolean.TRUE);
        }
        
        AbstractSolution.Status status;
        LinkedList<Node> listNoeud=new LinkedList<Node>();
        Node noeud=destination;
        
        // Si pas de prédécesseur ou dest pas atteinte
        if ((labelNodes.get(destination).getPere() == null)){
            status=AbstractSolution.Status.INFEASIBLE;
        } else {
        	
            // Reconstruction du chemin
            while (labelNodes.get(noeud).getPere() != null) {
                listNoeud.addFirst(noeud);
                noeud = labelNodes.get(noeud).getPere();
            }
            
            listNoeud.addFirst(data.getOrigin());
            status = AbstractSolution.Status.OPTIMAL;
        }
        
        Path chemin = Path.createShortestPathFromNodes(data.getGraph(),listNoeud);
        solution = new ShortestPathSolution(data,status,chemin,labelNodes.size());

        return solution;
    }
}
