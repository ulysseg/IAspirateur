import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Population implements Serializable {
	protected List<WallE> robots;

	public Population() {
		robots = new ArrayList<WallE>();
	}

	public Population(int nbRobots) {
		robots = new ArrayList<WallE>();

		for(int i=0; i < nbRobots; i++) {
			robots.add(new WallE());
		}
	}

	/**
	 * TODO: Add the best robot that was in the previous population
	 * @return
	 */
	public Population evolve() {
		// Create empty population
		Population p = new Population();
		Random rand = new Random();

		// We add the best robot no matter what
		// TODO: mettre genre 5~10% des meilleurs
		p.robots.add(new WallE(this.getTheBest()));

		while(p.robots.size() < this.robots.size()) {
			WallE father, mother;
			father = this.selection();
			// We prevent from crossing with the same robot
			while(father == (mother = this.selection()));
			father = new WallE(father);
			mother = new WallE(mother);

			// We cross robots with a likelihood of 60%
			if(rand.nextDouble() <= 0.6) {
				father.cross(mother);
			}

			// We mutate robots with a likelihood of 10%
			father.mutate(0.1);
			mother.mutate(0.1);

			p.robots.add(father);
			p.robots.add(mother);
		}

		return p;
	}

	public static Population evolve(int populationSize, int nbOfGenerations, boolean forever) {
		Population p = new Population(populationSize);

		for (int i = 0; i < nbOfGenerations || forever; i++) {
			System.out.println("Debut population "+i);
			p.calculateFitness();
			// Ecrire la population tous les 10 generations
			if(i%10 == 0) {
				try (
						OutputStream file = new FileOutputStream("population"+i+".ser");
						OutputStream buffer = new BufferedOutputStream(file);
						ObjectOutput output = new ObjectOutputStream(buffer);
						){
					output.writeObject(p);
				}  
				catch(IOException ex){
					System.out.println(ex);
				}
			}
			p = p.evolve();
			System.out.println("Fin population "+i);
		}

		// Update fitness
		System.out.println("Debut update fitness population resultat");
		p.calculateFitness();
		System.out.println("Fin update fitness population resultat");

		return p;
	}
	
	/**
	 * Pour reprendre les generations apres deserialization
	 * @param nbOfGenerations
	 * @param forever
	 * @return
	 */
	public  Population evolve(int nbOfGenerations, boolean forever, int iteration) {
		Population p = this;

		for (int i = 0; i < nbOfGenerations || forever; i++) {
			System.out.println("Debut population "+i);
			p.calculateFitness();
			// Ecrire la population tous les 10 generations
			if(i%10 == 0) {
				try (
						OutputStream file = new FileOutputStream("population"+i+iteration+".ser");
						OutputStream buffer = new BufferedOutputStream(file);
						ObjectOutput output = new ObjectOutputStream(buffer);
						){
					output.writeObject(p);
				}  
				catch(IOException ex){
					System.out.println(ex);
				}
			}
			p = p.evolve();
			System.out.println("Fin population "+i);
		}

		// Update fitness
		System.out.println("Debut update fitness population resultat");
		p.calculateFitness();
		System.out.println("Fin update fitness population resultat");

		return p;
	}

	/**
	 * Retourne un individu en en piochant deux au hasard,
	 * puis en choisissant le meilleur avec une probabilite
	 * de 75%
	 */
	public WallE selection() {
		// Pioche au hasard deux individus differents
		Random rand = new Random();
		int index1 = 0, index2 = 0;
		index1 = rand.nextInt(this.robots.size());
		while(index1 == index2) {
			index2 = rand.nextInt(this.robots.size());
		}

		double fitness1 = this.robots.get(index1).fitness;
		double fitness2 = this.robots.get(index2).fitness;

		// Determine le meilleur du moins bon
		WallE better, worse;
		if(fitness1 > fitness2) {
			better = this.robots.get(index1);
			worse = this.robots.get(index2);
		} else {
			worse = this.robots.get(index1);
			better = this.robots.get(index2);
		}

		// Dans 75% des cas, onp rend le meilleur des deux
		if(rand.nextDouble() <= 0.75) {
			return better;
		} else {
			return worse;
		}
	}

	public void calculateFitness() {
		for (WallE w : this.robots) {
			w.calculateFitness();
		}
	}

	public WallE getTheBest() {
		WallE theBest = null;
		for (WallE w : this.robots) {
			if(theBest == null || w.fitness > theBest.fitness) {
				theBest = w;
			}
		}
		return theBest;
	}

	@Override
	public String toString() {
		String s = "";

		for(WallE i : this.robots) {
			s += i.toString() + "\n";
		}

		return s;
	}
}
