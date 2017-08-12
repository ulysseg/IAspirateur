package me.ulysse.iaspirateur.ia;

import me.ulysse.iaspirateur.gui.Interface;
import me.ulysse.iaspirateur.util.Arrays2;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.util.TransferFunctionType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.util.Objects.requireNonNull;

// TODO export instance of this class as json or yaml, instead of Serializable
// TODO find alternate named because class starting by I is confusing
// TODO interface from this class!
public final class VacuumCleaner implements Robot {

    private static final double MIN_WEIGHT = -10;
    private static final double MAX_WEIGHT =  10;

	private final NeuralNetwork neuralNetwork;
	private double fitness;

	public static VacuumCleaner random() {
	    return new VacuumCleaner();
    }

    public static List<VacuumCleaner> randomList(int number) {
	    if(number < 1) {
	        throw new IllegalArgumentException();
        }
        List<VacuumCleaner> aspirateurs = new ArrayList<>(number);
        for (int i = 0; i < number; i++) {
            aspirateurs.add(random());
        }
        return aspirateurs;
    }

    public static VacuumCleaner copyOf(VacuumCleaner vacuumCleaner) {
	    return new VacuumCleaner(vacuumCleaner);
    }

	private VacuumCleaner() {
		neuralNetwork = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, 3, 3, 1);
		neuralNetwork.randomizeWeights(MIN_WEIGHT, MAX_WEIGHT);
		fitness = -1;
	}
	
	/**
	 * Copy constructor
	 */
	private VacuumCleaner(VacuumCleaner aspirateur) {
        requireNonNull(aspirateur);
		this.neuralNetwork = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, 3, 3, 1);
		this.neuralNetwork.setWeights(Arrays2.toPrimitive(aspirateur.neuralNetwork.getWeights()));
		this.fitness = aspirateur.fitness;
	}

	/**
	 * TODO: Corriger le fait qu'on peut avoir une valeur negative en sortie pour l'angle
	 * TODO: pour l'angle utiliser une fonction de transfert differente
	 */
	@Override
    public double calculateDirection(double leftBeamDistance, double centerBeamDistance, double rightBeamDistance) {
		this.neuralNetwork.setInput(leftBeamDistance, centerBeamDistance, rightBeamDistance);
		this.neuralNetwork.calculate();
		// TODO Essayer de diviser par moins que 10
        return (this.neuralNetwork.getOutput()[0] - 0.5)/5;
	}

    /**
     * Calculate the fitness of this robot which is the area it covers
     *
     * TODO Tests the robot with some random maps
     * TODO: generer un ensemble de maps a utiliser avec la fitness
     */
	public void evaluateFitness() {
		Interface anInterface = new Interface(this);
		fitness = anInterface.robotEngine().coverage();
	}

	public double fitness() {
        if (fitness == -1) {
            throw new IllegalStateException();
        }
        return fitness;
    }

    /**
	 * croisement en un point (tjs du point du croisement jusqu'a la fin)
	 * modifie this et i
	 * @param i
	 * TODO: Croisement en deux points
	 * TODO: Faire des mutations plus proches de la valeur precedente
	 */
    public void cross(VacuumCleaner i) {
		Random rand = new Random();
		// Point de croisement
		int crossPoint = rand.nextInt(this.neuralNetwork.getWeights().length);

		// Sauvegarde des coefficients de this du point de 
		// croisement jusqu'a la fin
		double[] valueTmp = new double[this.neuralNetwork.getWeights().length - crossPoint];
		for(int j = crossPoint, k = 0; j < this.neuralNetwork.getWeights().length; j++, k++) {
			valueTmp[k] = this.neuralNetwork.getWeights()[j];
		}

		// Copie de parametre vers this
		for(int j = crossPoint; j < this.neuralNetwork.getWeights().length; j++) {
			setWeightAtIndex(i.neuralNetwork.getWeights()[j], j);
		}

		// Copie de this vers parametre a l'aide valueTmp
		for(int j = crossPoint, k = 0; j < this.neuralNetwork.getWeights().length; j++, k++) {
			i.setWeightAtIndex(valueTmp[k], j);
		}
	}

	/**
	 * Does a mutation
	 */
    void mutate(double probability) {
		if(probability <= 0 || probability > 1) {
			throw new IllegalArgumentException();
		}

		Random rand = new Random();
		for (int i = 0; i < this.neuralNetwork.getWeights().length; i++) {
			if(rand.nextDouble() <= probability) {
				// Genere une valeur -10 et 10
				double randNeuronValue = MIN_WEIGHT + 2* MAX_WEIGHT *rand.nextDouble();
				// Genere une valeur entre -1 et 1
				randNeuronValue /= 10;
				randNeuronValue += this.neuralNetwork.getWeights()[i];
				if(randNeuronValue > 10) {
					randNeuronValue = 10;
				} else if(randNeuronValue < -10) {
					randNeuronValue = -10;
				}
				this.setWeightAtIndex(randNeuronValue, i);
			}
		}
	}

	private void setWeightAtIndex(double weight, int index) {
		double[] weights = Arrays2.toPrimitive(neuralNetwork.getWeights());
		weights[index] = weight;
		this.neuralNetwork.setWeights(weights);
	}

    public NeuralNetwork neuralNetwork() {
        return neuralNetwork;
    }

    @Override
    public String toString() {
        return "VacuumCleaner{" +
                "neuralNetwork=" + neuralNetwork +
                ", fitness=" + fitness +
                '}';
    }
}
