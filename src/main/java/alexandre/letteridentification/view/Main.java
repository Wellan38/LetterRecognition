/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alexandre.letteridentification.view;

import alexandre.letteridentification.service.Service;
import alexandre.letteridentification.util.NeuralNetwork;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 *
 * @author Alexandre
 */
public class Main {
    
    public static String path = "C:\\Users\\alexa\\OneDrive\\Documents\\NetBeansProjects\\LetterRecognitionServer\\src\\main\\webapp\\Training";
    
    public static void main(String[] args) throws Throwable
    {
        Service serv = new Service();
        
        NeuralNetwork net = serv.createNetwork();
        
        for (int i = 0; i < 20000; i++)
        {
            int index_char = (int)(65 + (90 - 65 + 1) * Math.random());
            char letter = (char)index_char;
            
            int nb_files = new File(path + "\\" + letter).list().length;
            
            int index_file = (int)(nb_files * Math.random() + 1);
            
            BufferedImage im = ImageIO.read(new File(path + "\\" + letter + "\\" + index_file + ".png"));
            
            Double[] input = serv.getCenteredImage(im);
            
            Double[] target = new Double[NeuralNetwork.NB_OUTPUT];
            
            for (int j = 0; j < NeuralNetwork.NB_OUTPUT; j++)
            {
                if (j == index_char - 65)
                {
                    target[j] = 1.;
                }
                else
                {
                    target[j] = 0.;
                }
            }
            
            net.train(input, target);
            
            if (i % 1000 == 0)
            {
                System.out.println(i);
            }
        }
        
        serv.saveWeights(net);
        
        /*
        NeuralNetwork net = serv.createNetwork();
        
        BufferedImage imA = ImageIO.read(new File("C:\\Users\\alexa\\OneDrive\\Documents\\NetBeansProjects\\LetterRecognitionServer\\src\\main\\webapp\\Training\\A\\5.png"));
        
        Double[] inA = serv.getCenteredImage(imA);
        
        Double[] res = serv.testNetwork(inA);
        
        for (int i = 0; i < res.length; i++)
        {
            System.out.println((char)(i+65) + " : " + res[i]);
        }
        */
        
        serv.createAdministrator("administrator@letterrecognition.com", "youcouldbemine38");
    }
}

