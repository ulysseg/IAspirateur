package me.ulysse.iaspirateur.ia;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.Objects.requireNonNull;

public final class Population {

	private final List<VacuumCleaner> robots;
    private final ExecutorService service;

	public Population(List<VacuumCleaner> robots) {
		if(robots.isEmpty()) {
			throw new IllegalArgumentException();
		}
		this.robots = robots;
        this.service = Executors.newFixedThreadPool(robots.size());
	}

	/**
	 * @return a new {@link Population} created from this.
	 */
	public Population createNextGeneration() {
		// Create empty population
		List<VacuumCleaner> newGeneration = new ArrayList<>();
		Random rand = new Random();

		// Add the best robot of "this" population no matter what
		// TODO: mettre genre 5~10% des meilleurs
        newGeneration.add(VacuumCleaner.copyOf(bestIndividual()));

        // Keep population at the same size
		while(newGeneration.size() < this.robots.size()) {
			VacuumCleaner father, mother;
			father = this.select();
			// We prevent from crossing with the same robot
			while(father == (mother = this.select()));
			father = VacuumCleaner.copyOf(father);
			mother = VacuumCleaner.copyOf(mother);

			// We cross robots with a likelihood of 60%
			if(rand.nextDouble() <= 0.6) {
				father.cross(mother);
			}

			// We mutate robots with a likelihood of 10%
			father.mutate(0.1);
			mother.mutate(0.1);

            newGeneration.add(father);
            newGeneration.add(mother);
		}

		return new Population(newGeneration);
	}

	/**
	 * Retourne un individu en en piochant deux au hasard,
	 * puis en choisissant le meilleur avec une probabilite
	 * de 75%
	 */
    private VacuumCleaner select() {
		// Pioche au hasard deux individus differents
		Random rand = new Random();
		int index1 = 0, index2 = 0;
		index1 = rand.nextInt(this.robots.size());
		while(index1 == index2) {
			index2 = rand.nextInt(this.robots.size());
		}


		double fitness1 = robots.get(index1).fitness();
		double fitness2 = robots.get(index2).fitness();

		// Determine le meilleur du moins bon
		VacuumCleaner better, worse;
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

    /**
     * Calculate the fitness of each individual of the population
     * TODO do this out of this class
     */
	public void calculateFitness() {
        CountDownLatch latch = new CountDownLatch(robots.size());

        for (VacuumCleaner w : this.robots) {
            service.submit(() -> {
                w.evaluateFitness();
                latch.countDown();
            });
		}

        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

	public VacuumCleaner bestIndividual() {
	    // list can not be empty
        // should we keep them sorted?
		VacuumCleaner best = robots.get(0);
		for (VacuumCleaner w : robots) {
			if(w.fitness() > best.fitness()) {
                best = w;
			}
		}
		return best;
	}

	@Override
	public String toString() {
		String s = "";

		for(VacuumCleaner r : this.robots) {
			s += r.toString() + "\n";
		}

		return s;
	}
}
