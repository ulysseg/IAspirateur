package me.ulysse.iaspirateur.ia;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class PopulationTest {

    // TODO PT plus dans l'api que dans les tests à voir...
    @Test
    public void evolvePopulation() throws IOException {
        int populationSize = 10;
        int nbOfGenerations = 1000;

        Population population = new Population(VacuumCleaner.randomList(populationSize));

        for (int i = 0; i < nbOfGenerations; i++) {
            if(i > 0) {
                System.out.println("Creating population " + i);
                population = population.createNextGeneration();
            }

            System.out.println("Calculating fitness of population " + i);
            population.calculateFitness();

            System.out.println("Best individual " + population.bestIndividual());
        }
    }
}