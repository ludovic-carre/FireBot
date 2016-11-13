package simulation.evenement;

import robot.*;
import simulation.*;

public class EvenementDeversement extends Evenement {

	private Robot robot;

	private int volume;

	private Incendie incendie;
	
	public EvenementDeversement(double date, Robot r, int volume, Incendie incendie) {
		super(date);
		this.robot = r;
		this.volume = volume;
		this.incendie = incendie;
	}

	public void execute() {
		System.out.println("Deversement"); // DEBUG
		this.incendie.diminuerIntensite(this.volume);
		this.robot.diminuerQteReservoir(this.volume);
	}
}
