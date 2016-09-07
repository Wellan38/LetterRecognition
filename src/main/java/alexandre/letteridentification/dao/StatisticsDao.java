/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alexandre.letteridentification.dao;

import alexandre.letteridentification.model.Statistics;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 *
 * @author Alex-Laptop
 */
public class StatisticsDao {
    
    public void create(Statistics s) throws Throwable
    {
        EntityManager em = JpaUtil.obtenirEntityManager();
        
        try
        {
            em.persist(s);
        }
        catch(Exception e)
        {
            throw e;
        }
    }
    
    public Statistics update(Statistics s) throws Throwable {
        EntityManager em = JpaUtil.obtenirEntityManager();
        try {
            s = em.merge(s);
        }
        catch(Exception e){
            throw e;
        }
        return s;
    }
    
    public Statistics findById(String id) throws Throwable {
        EntityManager em = JpaUtil.obtenirEntityManager();
        Statistics s = null;
        try {
            s = em.find(Statistics.class, id);
        }
        catch(Exception e) {
            throw e;
        }
        return s;
    }
    
    public List<Statistics> findAll() throws Throwable {
        EntityManager em = JpaUtil.obtenirEntityManager();
        List<Statistics> weights = null;
        try {
            Query q = em.createQuery("SELECT s FROM Statistics s");
            weights = (List<Statistics>) q.getResultList();
        }
        catch(Exception e) {
            throw e;
        }
        
        return weights;
    }
    
    public void remove(Statistics s) throws Throwable
    {
        EntityManager em = JpaUtil.obtenirEntityManager();
        
        try
        {
            em.remove(em.find(s.getClass(), s.getId()));
        }
        catch (Exception e)
        {
            throw e;
        }
    }
}
