package simulation;

import java.awt.Color;
import java.util.EnumMap;
import java.util.PriorityQueue;
import java.util.List;
import java.util.HashSet;
import java.util.Iterator;
import gui.GUISimulator;
import gui.Rectangle;
import simulation.*;
import gui.Simulable;
import gui.Text;
import enumerations.*;
import simulation.evenement.*;
import chemin.*;


public class Simulateur implements Simulable {
	private GUISimulator gui;
	private DonneesSimulation simulation;
	private int x_step;
	private int y_step;
	// TEMPORARY JUST TO TEST STUFF
	private HashSet<Destination> chemin;

	private long dateSimulation;

	private PriorityQueue<Evenement> listeEvenements = new PriorityQueue<Evenement> ();

	public Simulateur(GUISimulator gui,DonneesSimulation simulation){
		this.gui = gui;
		this.simulation = simulation;
		this.dateSimulation = 0;
		this.x_step = (int) Math.floor(this.gui.getPanelHeight() / this.simulation.getNbLignes());
		this.y_step = (int) Math.floor(this.gui.getPanelWidth() / this.simulation.getNbColonnes());
		gui.setSimulable(this);
	}
	
	@Override
	public void next(){
		System.out.println("Clicked on next"); // DEBUG
		this.incrementeDate();
		this.draw();
		this.drawPath();
	}

	@Override
	public void restart(){
		System.out.println("Clicked on restart"); // DEBUG
		// TODO : reinitialiser proprement la date et la liste d'evenements
		this.draw();
	}

	//TEMPORARY JUST TO TEST STUFF
	public void setPath(HashSet<Destination> chemin){
		this.chemin = chemin;
	}

	public void drawPath(){
		Color couleur_case = Color.decode("#3607ea");
		Iterator<Destination> dest_iterator = this.chemin.iterator();
		while(dest_iterator.hasNext()){
			Destination dest = dest_iterator.next();
			Case noeud = dest.getPosition();
			//System.out.println(dest.getTemps());
			gui.addGraphicalElement(new Rectangle(this.y_step*noeud.getColonne(),this.x_step*noeud.getLigne(),couleur_case,couleur_case,y_step));
			this.chemin.remove(noeud);
		}
	}

	private void draw(){
		this.gui.reset();
		Color couleur_case;
		NatureTerrain nature_terrain;
		/* EAU, FORET, ROCHE, TERRAIN_LIBRE, HABITAT */
		String[] terrain_s = {"#40a4df","#0A290A","#45463D","#66CD00","#663300"};
		EnumMap<NatureTerrain, Color> couleur_terrain = new EnumMap<NatureTerrain, Color>(NatureTerrain.class);
		for(int i = 0; i < 5; i++)
			couleur_terrain.put(NatureTerrain.values()[i],Color.decode(terrain_s[i]));

		/* On affiche le terrain de la carte */
		int nbLignes = this.simulation.getNbLignes();
		int nbColonnes = this.simulation.getNbColonnes();	
		for(int i = 0; i < nbLignes; i++){
			for(int j = 0; j < nbColonnes; j++){
				couleur_case = couleur_terrain.get(this.simulation.getNatureTerrain(i,j));
				gui.addGraphicalElement(new Rectangle(this.y_step*j,this.x_step*i,couleur_case,couleur_case,y_step));
			}
		}

		/* On ajoute le feu !!! */
		couleur_case = Color.decode("#cc0000");
		Coordonnee c;
		for(int i = 0; i < this.simulation.getNbIncendies(); i++){
			c = this.simulation.getCoordonneeIncendie(i);
			gui.addGraphicalElement(new Rectangle(y_step*c.getColonne(),x_step*c.getLigne(),couleur_case,couleur_case,y_step));
		}

		/* Et enfin les robots */
		couleur_case = Color.decode("#c0c0c0");
		for(int i = 0; i < this.simulation.getNbRobots(); i++){
			c = this.simulation.getCoordonneeRobot(i);
			gui.addGraphicalElement(new Rectangle(y_step*c.getColonne(),x_step*c.getLigne(),couleur_case,couleur_case,y_step));
		}

	}


	public void ajouteEvenement(Evenement e) {
		listeEvenements.add(e);
	}

	public void incrementeDate() {
		this.dateSimulation ++;
		System.out.println(this.dateSimulation); // DEBUG
		Evenement e;
		// TODO : on recherche 2 fois de suite le meilleur, c'est dommage
		while (! this.simulationTerminee() && listeEvenements.peek().getDate() <= this.dateSimulation) {
			e = listeEvenements.poll();
			e.execute();
		}
	}

	public boolean simulationTerminee() {
		return listeEvenements.isEmpty();
	}


	public long getDateSimulation() {
		return this.dateSimulation;
	}
}
