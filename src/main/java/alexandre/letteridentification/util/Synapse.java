package alexandre.letteridentification.util;

public class Synapse {
    Double weight = 0.;
    Double prevDeltaWeight = 0.; // for momentum
    Double deltaWeight = 0.;
 
    final Neuron leftNeuron;
    final Neuron rightNeuron;
    static int counter = 0;
    final public int id; // auto increment, starts at 0
 
    public Synapse(Neuron fromN, Neuron toN) {
        leftNeuron = fromN;
        rightNeuron = toN;
        id = counter;
        counter++;
    }
 
    public Double getWeight() {
        return weight;
    }
 
    public void setWeight(Double w) {
        weight = w;
    }
 
    public void setDeltaWeight(Double w) {
        prevDeltaWeight = deltaWeight;
        deltaWeight = w;
    }
 
    public Double getPrevDeltaWeight() {
        return prevDeltaWeight;
    }
 
    public Neuron getFromNeuron() {
        return leftNeuron;
    }
 
    public Neuron getToNeuron() {
        return rightNeuron;
    }
}