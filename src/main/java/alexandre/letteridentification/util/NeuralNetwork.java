/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alexandre.letteridentification.util;

import java.text.*;
import java.util.*;
 
public class NeuralNetwork {
    static {
        Locale.setDefault(Locale.ENGLISH);
    }
    
    public final static int NB_INPUT = 6*6;
    public final static int NB_HIDDEN = 5*5;
    public final static int NB_OUTPUT = 26;
 
    final boolean isTrained = false;
    final Random rand = new Random();
    private List<Neuron> inputLayer = new ArrayList<Neuron>();
    private List<Neuron> hiddenLayer = new ArrayList<Neuron>();
    private List<Neuron> outputLayer = new ArrayList<Neuron>();
    Neuron bias = new Neuron();
    final int randomWeightMultiplier = -1;
 
    final Double epsilon = 0.00000000001;
 
    final Double learningRate = 0.3;
    final Double momentum = 0.;
 
    Double[] inputs;
 
    Double expectedOutputs[];
    Double resultOutputs[];
    Double output[];
 
    // for weight update all
    final HashMap<String, Double> weightUpdate = new HashMap<String, Double>();
 
    public NeuralNetwork() {
 
        /**
         * Create all neurons and connections Connections are created in the
         * neuron class
         */
        
        bias.setOutput(-1.);
        
        //Input layer
        for (int j = 0; j < NB_INPUT; j++) {
            Neuron neuron = new Neuron();
            inputLayer.add(neuron);
        }
        
        //Hidden layer
        for (int j = 0; j < NB_HIDDEN; j++) {
            Neuron neuron = new Neuron();
            neuron.addInConnectionsS(inputLayer);
            neuron.addBiasConnection(bias);
            hiddenLayer.add(neuron);
        }
        
        //Output layer
        for (int j = 0; j < NB_OUTPUT; j++) {
            Neuron neuron = new Neuron();
            neuron.addInConnectionsS(hiddenLayer);
            neuron.addBiasConnection(bias);
            outputLayer.add(neuron);
        }
 
        // initialize random weights
        for (Neuron neuron : hiddenLayer) {
            List<Synapse> connections = neuron.getAllInConnections();
            for (Synapse conn : connections) {
                Double newWeight = getRandom();
                conn.setWeight(newWeight);
            }
        }
        for (Neuron neuron : outputLayer) {
            List<Synapse> connections = neuron.getAllInConnections();
            for (Synapse conn : connections) {
                Double newWeight = getRandom();
                conn.setWeight(newWeight);
            }
        }
 
        // reset id counters
        Neuron.counter = 0;
        Synapse.counter = 0;
        
        inputs = new Double[NB_INPUT];
        output = new Double[NB_OUTPUT];
        resultOutputs = new Double[NB_OUTPUT];
        expectedOutputs = new Double[NB_OUTPUT];
 
        if (isTrained) {
            trainedWeights();
            updateAllWeights();
        }
    }
 
    // random
    Double getRandom() {
        return randomWeightMultiplier * (rand.nextDouble() * 2 - 1); // [-1;1[
    }
 
    /**
     * 
     * @param inputs
     *            There is equally many neurons in the input layer as there are
     *            in input variables
     */
    public void setInput(Double inputs[]) {
        for (int i = 0; i < inputLayer.size(); i++) {
            inputLayer.get(i).setOutput(inputs[i]);
        }
    }

    public List<Neuron> getInputLayer() {
        return inputLayer;
    }

    public List<Neuron> getHiddenLayer() {
        return hiddenLayer;
    }

    public List<Neuron> getOutputLayer() {
        return outputLayer;
    }
    
    
 
    public Double[] getOutput() {
        Double[] outputs = new Double[outputLayer.size()];
        for (int i = 0; i < outputLayer.size(); i++)
            outputs[i] = outputLayer.get(i).getOutput();
        return outputs;
    }
 
    /**
     * Calculate the output of the neural network based on the input The forward
     * operation
     */
    public void activate() {
        for (Neuron n : hiddenLayer)
            n.calculateOutput();
        for (Neuron n : outputLayer)
            n.calculateOutput();
    }
 
    /**
     * all output propagate back
     * 
     * @param expectedOutput
     *            first calculate the partial derivative of the error with
     *            respect to each of the weight leading into the output neurons
     *            bias is also updated here
     */
    public void applyBackpropagation(Double expectedOutput[]) {
 
        // error check, normalize value ]0;1[
        for (int i = 0; i < expectedOutput.length; i++) {
            Double d = expectedOutput[i];
            if (d < 0 || d > 1) {
                if (d < 0)
                    expectedOutput[i] = 0 + epsilon;
                else
                    expectedOutput[i] = 1 - epsilon;
            }
        }
 
        int i = 0;
        for (Neuron n : outputLayer) {
            List<Synapse> connections = n.getAllInConnections();
            for (Synapse con : connections) {
                Double ak = n.getOutput();
                Double ai = con.leftNeuron.getOutput();
                Double desiredOutput = expectedOutput[i];
 
                Double partialDerivative = -ak * (1 - ak) * ai
                        * (desiredOutput - ak);
                Double deltaWeight = -learningRate * partialDerivative;
                Double newWeight = con.getWeight() + deltaWeight;
                con.setDeltaWeight(deltaWeight);
                con.setWeight(newWeight + momentum * con.getPrevDeltaWeight());
            }
            i++;
        }
 
        // update weights for the hidden layer
        for (Neuron n : hiddenLayer) {
            List<Synapse> connections = n.getAllInConnections();
            for (Synapse con : connections) {
                Double aj = n.getOutput();
                Double ai = con.leftNeuron.getOutput();
                Double sumKoutputs = 0.;
                int j = 0;
                for (Neuron out_neu : outputLayer) {
                    Double wjk = out_neu.getConnection(n.id).getWeight();
                    Double desiredOutput = (Double) expectedOutput[j];
                    Double ak = out_neu.getOutput();
                    j++;
                    sumKoutputs = sumKoutputs
                            + (-(desiredOutput - ak) * ak * (1 - ak) * wjk);
                }
 
                Double partialDerivative = aj * (1 - aj) * ai * sumKoutputs;
                Double deltaWeight = -learningRate * partialDerivative;
                Double newWeight = con.getWeight() + deltaWeight;
                con.setDeltaWeight(deltaWeight);
                con.setWeight(newWeight + momentum * con.getPrevDeltaWeight());
            }
        }
    }
 
    public Double train(Double[] signals, Double[] target)
    {         
        inputs = signals;
        expectedOutputs = target;
        Double error = 0.;
        setInput(inputs);

        activate();

        output = getOutput();
        resultOutputs = output;

        for (int j = 0; j < expectedOutputs.length; j++) {
            Double err = 0.5 * Math.pow(output[j] - expectedOutputs[j], 2);
            error += err;
        }
        
        applyBackpropagation(expectedOutputs);
        
        //System.out.println("out = " + output[0] + ", expected = " + expectedOutputs[0] + ", error = " + error);

        return error;
    }
    
    public Double[] test(Double[] signals)
    {
        inputs = signals;
        
        setInput(inputs);

        activate();

        output = getOutput();
        resultOutputs = output;
        
        return output;
    }
 
    String weightKey(int neuronId, int conId) {
        return "N" + neuronId + "_C" + conId;
    }
 
    /**
     * Take from hash table and put into all weights
     */
    public void updateAllWeights() {
        // update weights for the output layer
        for (Neuron n : outputLayer) {
            List<Synapse> connections = n.getAllInConnections();
            for (Synapse con : connections) {
                String key = weightKey(n.id, con.id);
                Double newWeight = weightUpdate.get(key);
                con.setWeight(newWeight);
            }
        }
        // update weights for the hidden layer
        for (Neuron n : hiddenLayer) {
            List<Synapse> connections = n.getAllInConnections();
            for (Synapse con : connections) {
                String key = weightKey(n.id, con.id);
                Double newWeight = weightUpdate.get(key);
                con.setWeight(newWeight);
            }
        }
    }
 
    // trained data
    void trainedWeights() {
        weightUpdate.clear();
         
        weightUpdate.put(weightKey(3, 0), 1.03);
        weightUpdate.put(weightKey(3, 1), 1.13);
        weightUpdate.put(weightKey(3, 2), -.97);
        weightUpdate.put(weightKey(4, 3), 7.24);
        weightUpdate.put(weightKey(4, 4), -3.71);
        weightUpdate.put(weightKey(4, 5), -.51);
        weightUpdate.put(weightKey(5, 6), -3.28);
        weightUpdate.put(weightKey(5, 7), 7.29);
        weightUpdate.put(weightKey(5, 8), -.05);
        weightUpdate.put(weightKey(6, 9), 5.86);
        weightUpdate.put(weightKey(6, 10), 6.03);
        weightUpdate.put(weightKey(6, 11), .71);
        weightUpdate.put(weightKey(7, 12), 2.19);
        weightUpdate.put(weightKey(7, 13), -8.82);
        weightUpdate.put(weightKey(7, 14), -8.84);
        weightUpdate.put(weightKey(7, 15), 11.81);
        weightUpdate.put(weightKey(7, 16), .44);
    }
 
    public void printAllWeights() {
        System.out.println("printAllWeights");
        // weights for the hidden layer
        for (Neuron n : hiddenLayer) {
            List<Synapse> connections = n.getAllInConnections();
            for (Synapse con : connections) {
                Double w = con.getWeight();
                System.out.println("n=" + n.id + " c=" + con.id + " w=" + w);
            }
        }
        // weights for the output layer
        for (Neuron n : outputLayer) {
            List<Synapse> connections = n.getAllInConnections();
            for (Synapse con : connections) {
                Double w = con.getWeight();
                System.out.println("n=" + n.id + " c=" + con.id + " w=" + w);
            }
        }
        System.out.println();
    }
}