/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alexandre.letteridentification.service;

import alexandre.letteridentification.dao.*;
import alexandre.letteridentification.model.*;
import alexandre.letteridentification.util.*;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Alexandre
 */
public class Service
{
    public NeuralNetwork createNetwork() throws Throwable
    {
        NeuralNetwork net = new NeuralNetwork();
        
        Weights w = findWeights();
        
        if (w != null)
        {
            List<Neuron> hiddenNeurons = net.getHiddenLayer();
            
            for (int i = 0; i < NeuralNetwork.NB_HIDDEN; i++)
            {
                List<Synapse> in = hiddenNeurons.get(i).getAllInConnections();
                
                for (int j = 0; j < in.size(); j++)
                {
                    in.get(j).setWeight(w.getWeigths()[i * in.size() + j]);
                }
            }
            
            List<Neuron> outputNeurons = net.getOutputLayer();
            
            for (int i = 0; i < NeuralNetwork.NB_OUTPUT; i++)
            {
                List<Synapse> in = outputNeurons.get(i).getAllInConnections();
                
                for (int j = 0; j < in.size(); j++)
                {
                    in.get(j).setWeight(w.getWeigths()[(NeuralNetwork.NB_INPUT + 1) * NeuralNetwork.NB_HIDDEN + i * in.size() + j]);
                }
            }
        }
        else
        {
            createWeights(new Double[(NeuralNetwork.NB_INPUT + 1) * NeuralNetwork.NB_HIDDEN + (NeuralNetwork.NB_HIDDEN + 1) * NeuralNetwork.NB_OUTPUT]);
            saveWeights(net);
        }
        
        return net;
    }
    
    public Weights findWeights() throws Throwable
    {
        JpaUtil.creerEntityManager();
        
        WeightsDao dao = new WeightsDao();
        
        List<Weights> weights = dao.findAll();
        
        if (weights.size() > 0)
        {
            return weights.get(0);
        }
        else
        {
            return null;
        }
    }
    
    public Double[] createInputFromImage(BufferedImage image)
    {        
        if (image.getRaster().getDataBuffer() instanceof DataBufferInt)
        {
            int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

            Double[] res = new Double[pixels.length];

            for (int i = 0; i < pixels.length; i++)
            {
                if (pixels[i] != 0)
                {
                    res[i] = 1.;
                }
                else
                {
                    res[i] = 0.;
                }
            }

            return res;
        }
        else
        {
            byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();

            Double[] res = new Double[pixels.length];

            for (int i = 0; i < pixels.length; i++)
            {
                if (pixels[i] != 0)
                {
                    res[i] = 1.;
                }
                else
                {
                    res[i] = 0.;
                }
            }

            return res;
        }
    }
    
    public Weights createWeights(Double[] weights) throws Throwable
    {
        String id = "WEIGHTS";
        
        JpaUtil.creerEntityManager();
        
        WeightsDao dao = new WeightsDao();
        
        Weights w = dao.findById(id);
        
        if (w == null)
        {
            w = new Weights(id, weights);

            JpaUtil.ouvrirTransaction();

            dao.create(w);

            JpaUtil.validerTransaction();

            JpaUtil.fermerEntityManager();

            return w;
        }
        else
        {
            JpaUtil.fermerEntityManager();
            
            return null;
        }
    }
    
    public Weights updateWeights(Double[] newWeights) throws Throwable
    {
        JpaUtil.creerEntityManager();
        
        WeightsDao dao = new WeightsDao();
        
        Weights w = findWeights();
        
        JpaUtil.creerEntityManager();
        
        if (w != null)
        {
            JpaUtil.ouvrirTransaction();
            
            w.setWeigths(newWeights);

            dao.update(w);

            JpaUtil.validerTransaction();

            JpaUtil.fermerEntityManager();

            return w;
        }
        else
        {
            JpaUtil.fermerEntityManager();

            return null;
        }
    }
    
    public void trainNetwork(Double[] input, Double[] target) throws Throwable
    {
        NeuralNetwork net = createNetwork();
        
        net.train(input, target);
        
        saveWeights(net);
    }
    
    public Double[] testNetwork(Double[] input) throws Throwable
    {
        NeuralNetwork net = createNetwork();
        
        Double[] res = net.test(input);
        
        return res;
    }
    
    public Boolean saveWeights(NeuralNetwork net) throws Throwable
    {
        Double[] we = new Double[(NeuralNetwork.NB_INPUT + 1) * NeuralNetwork.NB_HIDDEN + (NeuralNetwork.NB_HIDDEN + 1) * NeuralNetwork.NB_OUTPUT];
        
        List<Neuron> hidden = net.getHiddenLayer();
        
        for (int i = 0; i < hidden.size(); i++)
        {
            List<Synapse> in = hidden.get(i).getAllInConnections();
            
            for (int j = 0; j < in.size(); j++)
            {
                we[i * in.size() + j] = in.get(j).getWeight();
            }
        }
        
        List<Neuron> output = net.getOutputLayer();
        
        for (int i = 0; i < output.size(); i++)
        {
            List<Synapse> in = output.get(i).getAllInConnections();
            
            for (int j = 0; j < in.size(); j++)
            {
                we[(NeuralNetwork.NB_INPUT + 1) * NeuralNetwork.NB_HIDDEN + i * in.size() + j] = in.get(j).getWeight();
            }
        }
        
        updateWeights(we);
        
        return true;
    }
    
    public BufferedImage rescaleImage(BufferedImage img)
    {        
        int targetWidth = (int)(Math.sqrt(NeuralNetwork.NB_INPUT));
        
        int type = (img.getTransparency() == Transparency.OPAQUE) ?
        BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage ret = (BufferedImage)img;
        int w = img.getWidth();

        do {
            if (w > targetWidth) {
                w /= 2;
                if (w < targetWidth) {
                    w = targetWidth;
                }
            }

            BufferedImage tmp = new BufferedImage(w, w, type);
            Graphics2D g2 = tmp.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.drawImage(ret, 0, 0, w, w, null);
            g2.dispose();

            ret = tmp;
        } while (w != targetWidth);

        return ret;
    }
    
    public Double[] getResizedInput(BufferedImage im)
    {
        int[][] pixels = new int[im.getHeight()][im.getWidth()];
        
        for (int i = 0; i < im.getHeight(); i++)
        {
            for (int j = 0; j < im.getWidth(); j++)
            {
                if (im.getRGB(i, j) != 0)
                {
                    pixels[i][j] = 1;
                }
                else
                {
                    pixels[i][j] = 0;
                }
            }
        }

        Double[] res = new Double[NeuralNetwork.NB_INPUT];
        
        int nb_pix = (int)(im.getWidth() / Math.sqrt(NeuralNetwork.NB_INPUT));
        
        for (int i = 0; i < Math.sqrt(res.length); i++)
        {
            for (int j = 0; j < Math.sqrt(res.length); j++)
            {
                int nb_colored = 0;
                
                for (int k = i * nb_pix; k < (i+1) * nb_pix; k++)
                {
                    for (int l = j * nb_pix; l < (j+1) * nb_pix; l++)
                    {
                        if (pixels[l][k] == 1)
                        {
                            nb_colored++;
                        }
                    }
                }
                
                if (nb_colored > nb_pix * nb_pix * 0.1)
                {
                    res[(int)(i * Math.sqrt(res.length)) + j] = 1.;
                }
                else
                {
                    res[(int)(i * Math.sqrt(res.length)) + j] = 0.;
                }
            }
        }
        
        return res;
    }
    
    public Double[] getCenteredImage(BufferedImage im) throws IOException
    {
        int[][] pixels = new int[im.getHeight()][im.getWidth()];
        
        for (int i = 0; i < im.getHeight(); i++)
        {
            for (int j = 0; j < im.getWidth(); j++)
            {
                if (im.getRGB(j, i) != 0)
                {
                    pixels[i][j] = 1;
                }
                else
                {
                    pixels[i][j] = 0;
                }
            }
        }
        
        int firstRow = 0, lastRow = im.getHeight() - 1, firstCol = 0, lastCol = im.getWidth() - 1;
        
        for (int i = 0; i < im.getHeight(); i++)
        {
            boolean used = false;
            
            for (int j = 0; j < im.getWidth(); j++)
            {
                if (pixels[i][j] == 1)
                {
                    used = true;
                    break;
                }
            }
            
            if (!used)
            {
                firstRow = i+1;
            }
            else
            {
                break;
            }
        }
        
        for (int i = im.getHeight() - 1; i >= 0; i--)
        {
            boolean used = false;
            
            for (int j = 0; j < im.getWidth(); j++)
            {
                if (pixels[i][j] == 1)
                {
                    used = true;
                    break;
                }
            }
            
            if (!used)
            {
                lastRow = i-1;
            }
            else
            {
                break;
            }
        }
        
        for (int i = 0; i < im.getWidth(); i++)
        {
            boolean used = false;
            
            for (int j = 0; j < im.getHeight(); j++)
            {
                if (pixels[j][i] == 1)
                {
                    used = true;
                    break;
                }
            }
            
            if (!used)
            {
                firstCol = i+1;
            }
            else
            {
                break;
            }
        }
        
        for (int i = im.getWidth() - 1; i >= 0; i--)
        {
            boolean used = false;
            
            for (int j = 0; j < im.getHeight(); j++)
            {
                if (pixels[j][i] == 1)
                {
                    used = true;
                    break;
                }
            }
            
            if (!used)
            {
               lastCol = i+1;
            }
            else
            {
                break;
            }
        }
        
        int height = lastRow - firstRow;
        int width = lastCol - firstCol;
        
        int[][] res = new int[height][width];
        
        for (int i = height - 1; i >= 0; i--)
        {
            for (int j = 0; j < width; j++)
            {
                res[i][j] = (int)(pixels[i + firstRow][j + firstCol]);
            }
        }
        
        Double[] input = getResizedInput(res, height, width);
        
        return input;
    }
    
    public Double[] getResizedInput(int[][] pixels, int height, int width)
    {
        Double[] res = new Double[NeuralNetwork.NB_INPUT];
        
        int nb_pix_width = (int)(width / Math.sqrt(NeuralNetwork.NB_INPUT));
        int nb_pix_height = (int)(height / Math.sqrt(NeuralNetwork.NB_INPUT));
        
        for (int i = 0; i < Math.sqrt(res.length); i++)
        {
            for (int j = 0; j < Math.sqrt(res.length); j++)
            {
                int nb_colored = 0;
                
                for (int k = i * nb_pix_width; k < (i+1) * nb_pix_width; k++)
                {
                    for (int l = j * nb_pix_height; l < (j+1) * nb_pix_height; l++)
                    {
                        if (pixels[l][k] == 1)
                        {
                            nb_colored++;
                        }
                    }
                }
                
                if (nb_colored > nb_pix_height * nb_pix_width * 0.1)
                {
                    res[(int)(i * Math.sqrt(res.length)) + j] = 1.;
                }
                else
                {
                    res[(int)(i * Math.sqrt(res.length)) + j] = 0.;
                }
            }
        }
        
        return res;
    }
    
    public Statistics createStatistics(Character letter) throws Throwable
    {
        if (findStatisticsByLetter(letter) != null)
        {
            return null;
        }
        else
        {
            Statistics s = new Statistics("STATS-" + letter, letter);
            s.setNumber_first(0);
            s.setNumber_second(0);
            s.setNumber_third(0);
            s.setNumber_more(0);
            
            JpaUtil.creerEntityManager();
            
            StatisticsDao dao = new StatisticsDao();
            
            JpaUtil.ouvrirTransaction();
            
            dao.create(s);
            
            JpaUtil.validerTransaction();
            
            JpaUtil.fermerEntityManager();
            
            return s;
        }
    }
    
    public Statistics findStatisticsByLetter(Character letter) throws Throwable
    {
        JpaUtil.creerEntityManager();
        
        StatisticsDao dao = new StatisticsDao();
        
        List<Statistics> stats = dao.findAll();
        
        for (Statistics s : stats)
        {
            if (s.getLetter().equals(letter))
            {
                return s;
            }
        }
        
        return null;
    }
    
    public Statistics updateStatistics(Statistics s, Integer number_first, Integer number_second, Integer number_third, Integer number_more) throws Throwable
    {
        if (s == null)
        {
            return null;
        }
        else
        {
            s.setNumber_first(number_first);
            s.setNumber_second(number_second);
            s.setNumber_third(number_third);
            s.setNumber_more(number_more);
            
            JpaUtil.creerEntityManager();
            
            StatisticsDao dao = new StatisticsDao();
            
            JpaUtil.ouvrirTransaction();
            
            s = dao.update(s);
            
            JpaUtil.validerTransaction();
            
            JpaUtil.fermerEntityManager();
            
            return s;
        }
    }
    
    public Administrator createAdministrator(String email, String password) throws Throwable
    {
        if (findAdministratorByEmail(email) == null)
        {
            Administrator a = new Administrator(email, password);
            
            JpaUtil.creerEntityManager();
            
            AdministratorDao dao = new AdministratorDao();
            
            JpaUtil.ouvrirTransaction();
            
            dao.create(a);
            
            JpaUtil.validerTransaction();
            
            JpaUtil.fermerEntityManager();
            
            return a;
        }
        else
        {
            return null;
        }
    }
    
    public Administrator findAdministratorByEmail(String email) throws Throwable
    {
        JpaUtil.creerEntityManager();
        
        AdministratorDao dao = new AdministratorDao();
        
        Administrator a = dao.findById(email);
        
        JpaUtil.fermerEntityManager();
        
        return a;
    }
}