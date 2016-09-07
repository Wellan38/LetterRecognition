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
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author alexa
 */
public class Test {
    public static void main(String[] args) throws IOException, Throwable
    {
        Service serv = new Service();
        
        NeuralNetwork net = serv.createNetwork();
        
        BufferedImage imA = ImageIO.read(new File("C:\\Users\\alexa\\OneDrive\\Documents\\NetBeansProjects\\LetterIdentificationServer\\src\\main\\webapp\\Training\\G\\5.png"));
        
        Double[] inA = serv.getCenteredImage(imA);
        
        Double[] res = serv.testNetwork(inA);
        
        for (int i = 0; i < res.length; i++)
        {
            System.out.println((char)(i+65) + " : " + res[i]);
        }
    }
}
