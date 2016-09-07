/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alexandre.letteridentification.dao;

import alexandre.letteridentification.model.Weights;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 *
 * @author Alex-Laptop
 */
public class WeightsDao {
    
    public void create(Weights w) throws Throwable
    {
        EntityManager em = JpaUtil.obtenirEntityManager();
        
        try
        {
            em.persist(w);
        }
        catch(Exception e)
        {
            throw e;
        }
    }
    
    public Weights update(Weights w) throws Throwable {
        EntityManager em = JpaUtil.obtenirEntityManager();
        try {
            w = em.merge(w);
        }
        catch(Exception e){
            throw e;
        }
        return w;
    }
    
    public Weights findById(String id) throws Throwable {
        EntityManager em = JpaUtil.obtenirEntityManager();
        Weights w = null;
        try {
            w = em.find(Weights.class, id);
        }
        catch(Exception e) {
            throw e;
        }
        return w;
    }
    
    public List<Weights> findAll() throws Throwable {
        EntityManager em = JpaUtil.obtenirEntityManager();
        List<Weights> weights = null;
        try {
            Query q = em.createQuery("SELECT w FROM Weights w");
            weights = (List<Weights>) q.getResultList();
        }
        catch(Exception e) {
            throw e;
        }
        
        return weights;
    }
    
    public void remove(Weights w) throws Throwable
    {
        EntityManager em = JpaUtil.obtenirEntityManager();
        
        try
        {
            em.remove(em.find(w.getClass(), w.getId()));
        }
        catch (Exception e)
        {
            throw e;
        }
    }
}
