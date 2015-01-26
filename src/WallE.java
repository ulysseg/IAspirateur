import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.util.TransferFunctionType;


public class WallE implements Serializable {
	NeuralNetwork nn;
	double fitness;

	public static final double MINVALUECOEF = -10;
	public static final double MAXVALUECOEF =  10;

	public WallE() {
		this.nn = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, 3, 3, 1);
		this.nn.randomizeWeights(MINVALUECOEF, MAXVALUECOEF);
	}
	
	/**
	 * Copy constructor
	 * @param i
	 */
	public WallE(WallE w) {
		this.nn = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, 3, 3, 1);
		this.nn.setWeights(DoubleTodouble(w.nn.getWeights()));
		this.fitness = w.fitness;
	}

	/**
	 * Fitness of this robot. Area covered by the robot.
	 * Tests the robot with some random maps
	 * @return
	 * TODO: generer un ensemble de maps a utiliser avec la fitness
	 */
	public double calculateFitness() {
		Interface i = new Interface(this);
		this.fitness = i.fitness;

		//while(fitness == 0);
		//		RobotEngine r = new RobotEngine(this);
		//		fitness = r.moveNoGUI();
		System.out.println("Fitness="+fitness);
		//		r.move();
		//Interface f = new Interface();

		return this.fitness;
		//		return fitness;
	}

	/**
	 * TODO: Corriger le fait qu'on peut avoir une valeur negative en sortie pour l'angle
	 * TODO: pour l'angle utiliser une fonction de transfert differente
	 * @param distanceLeftBeam
	 * @param distanceCenterBeam
	 * @param distanceRightBeam
	 * @return
	 */
	public double getValue(double distanceLeftBeam, double distanceCenterBeam, double distanceRightBeam) {
		double value = 0;
		this.nn.setInput(distanceLeftBeam, distanceCenterBeam, distanceRightBeam);
		this.nn.calculate();
		// TODO: Essayer de diviser par moins que 10
		value = (this.nn.getOutput()[0] - 0.5)/5;

		return value;
	}

	/**
	 * croisement en un point (tjs du point du croisement jusqu'a la fin)
	 * modifie this et i
	 * @param i
	 * TODO: Croisement en deux points
	 * TODO: Faire des mutations plus proches de la valeur precedente
	 */
	public void cross(WallE i) {
		Random rand = new Random();
		// Point de croisement
		int crossPoint = rand.nextInt(this.nn.getWeights().length);

		// Sauvegarde des coefficients de this du point de 
		// croisement jusqu'a la fin
		double[] valueTmp = new double[this.nn.getWeights().length - crossPoint];
		for(int j = crossPoint, k = 0; j < this.nn.getWeights().length; j++, k++) {
			valueTmp[k] = this.nn.getWeights()[j];
		}

		// Copie de parametre vers this
		for(int j = crossPoint; j < this.nn.getWeights().length; j++) {
			this.setWeightAtIndex(i.nn.getWeights()[j], j);
		}

		// Copie de this vers parametre a l'aide valueTmp
		for(int j = crossPoint, k = 0; j < this.nn.getWeights().length; j++, k++) {
			i.setWeightAtIndex(valueTmp[k], j);
		}
	}

	/**
	 * Does a mutation
	 */
	public void mutate(double probability) {
		Random rand = new Random();

		for (int i = 0; i < this.nn.getWeights().length; i++) {
			if(rand.nextDouble() <= probability) {
				// Genere une valeur -10 et 10
				double randNeuronValue = MINVALUECOEF + 2*MAXVALUECOEF*rand.nextDouble();
				// Genere une valeur entre -1 et 1
				randNeuronValue /= 10;
				randNeuronValue += this.nn.getWeights()[i];
				if(randNeuronValue > 10) {
					randNeuronValue = 10;
				} else if(randNeuronValue < -10) {
					randNeuronValue = -10;
				}
				this.setWeightAtIndex(randNeuronValue, i);
			}
		}
	}

	public void setWeightAtIndex(double value, int index) {
		double[] weights = DoubleTodouble(this.nn.getWeights());
		weights[index] = value;
		this.nn.setWeights(weights);
	}

	/**
	 * TODO: to test
	 * Convert a Double[] to a double[]
	 * @param in
	 * @return
	 */
	public static double[] DoubleTodouble(Double[] in) {
		double[] tempArray = new double[in.length];
		int i = 0;
		for(Double d : in) {
			tempArray[i] = (double) d;
			i++;
		}
		return tempArray;
	}

	public static void main(String[] args) {
		
		Population res = Population.evolve(50, 1, true);
		WallE theBest = res.getTheBest();
		System.out.println("Fitness du best : "+theBest.fitness);
		res.getTheBest().calculateFitness();
		
		// Serialization
		try (
				OutputStream file = new FileOutputStream("tmp.ser");
				OutputStream buffer = new BufferedOutputStream(file);
				ObjectOutput output = new ObjectOutputStream(buffer);
				){
			output.writeObject(theBest);
		}  
		catch(IOException ex){
			System.out.println(ex);
		}

		// Deserialization
//		try(
////			InputStream file = new FileInputStream("robot_ligne_droite.ser");
////				InputStream file = new FileInputStream("robot_ligne_droite_2.ser");
//				InputStream file = new FileInputStream("population20.ser");
//				InputStream buffer = new BufferedInputStream(file);
//				ObjectInput input = new ObjectInputStream (buffer);
//				){
//			//deserialize the List
//			Population p = (Population) input.readObject();
//			//display its data
//			p.getTheBest().calculateFitness();
//		}
//		catch(ClassNotFoundException ex){
//			System.out.println(ex);
//		}
//		catch(IOException ex){
//			System.out.println(ex);
//		}



	}
}
