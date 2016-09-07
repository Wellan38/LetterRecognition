/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alexandre.letteridentification.dao;

import alexandre.letteridentification.model.Administrator;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 *
 * @author Alex-Laptop
 */
public class AdministratorDao {
    
    public void create(Administrator a) throws Throwable
    {
        EntityManager em = JpaUtil.obtenirEntityManager();
        
        try
        {
            em.persist(a);
        }
        catch(Exception e)
        {
            throw e;
        }
    }
    
    public Administrator update(Administrator a) throws Throwable {
        EntityManager em = JpaUtil.obtenirEntityManager();
        try {
            a = em.merge(a);
        }
        catch(Exception e){
            throw e;
        }
        return a;
    }
    
    public Administrator findById(String id) throws Throwable {
        EntityManager em = JpaUtil.obtenirEntityManager();
        Administrator a = null;
        try {
            a = em.find(Administrator.class, id);
        }
        catch(Exception e) {
            throw e;
        }
        return a;
    }
    
    public List<Administrator> findAll() throws Throwable {
        EntityManager em = JpaUtil.obtenirEntityManager();
        List<Administrator> weights = null;
        try {
            Query q = em.createQuery("SELECT a FROM Administrator a");
            weights = (List<Administrator>) q.getResultList();
        }
        catch(Exception e) {
            throw e;
        }
        
        return weights;
    }
    
    public void remove(Administrator a) throws Throwable
    {
        EntityManager em = JpaUtil.obtenirEntityManager();
        
        try
        {
            em.remove(em.find(a.getClass(), a.getId()));
        }
        catch (Exception e)
        {
            throw e;
        }
    }
}
