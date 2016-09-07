/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alexandre.letteridentification.util;

import java.util.*;
 
public class Neuron {   
    static int counter = 0;
    final public int id;  // auto increment, starts at 0
    Synapse biasConnection;
    final Double bias = 1.;
    Double output;
     
    List<Synapse> Inconnections = new ArrayList<Synapse>();
    HashMap<Integer,Synapse> connectionLookup = new HashMap<Integer,Synapse>();
     
    public Neuron(){        
        id = counter;
        counter++;
    }
     
    /**
     * Compute Sj = Wij*Aij + w0j*bias
     */
    public void calculateOutput(){
        Double s = 0.;
        for(Synapse con : Inconnections){
            Neuron leftNeuron = con.getFromNeuron();
            Double weight = con.getWeight();
            Double a = leftNeuron.getOutput(); //output from previous layer
            
            s = s + (weight*a);
        }
        s = s + (biasConnection.getWeight()*bias);
         
        output = g(s);
    }
     
     
    Double g(Double x) {
        return sigmoid(x);
    }
 
    Double sigmoid(Double x) {
        return 1.0 / (1.0 +  (Math.exp(-x)));
    }
     
    public void addInConnectionsS(List<Neuron> inNeurons){
        for(Neuron n: inNeurons){
            Synapse con = new Synapse(n,this);
            Inconnections.add(con);
            connectionLookup.put(n.id, con);
        }
    }
     
    public Synapse getConnection(int neuronIndex){
        return connectionLookup.get(neuronIndex);
    }
 
    public void addInConnection(Synapse con){
        Inconnections.add(con);
    }
    public void addBiasConnection(Neuron n){
        Synapse con = new Synapse(n,this);
        biasConnection = con;
        Inconnections.add(con);
    }
    public List<Synapse> getAllInConnections(){
        return Inconnections;
    }
     
    public Double getBias() {
        return bias;
    }
    public Double getOutput() {
        return output;
    }
    public void setOutput(Double o){
        output = o;
    }
}