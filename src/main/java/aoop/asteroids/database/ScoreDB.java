/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aoop.asteroids.database;

import java.util.Collection;
import java.util.List;
import java.util.Observable;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

/**
 * DataBse class
 * @author adeng
 */
public class ScoreDB extends Observable{
    
    private EntityManagerFactory emf;
    private EntityManager em;
    
    /**
     * openDatabase
     */
    public void openDataSource(){
        emf = Persistence.createEntityManagerFactory("$objectdb/db/asteroids.odb");
        em = emf.createEntityManager();
    }
    
    /**
     * closedatabase
     */
    public void closeDataSource(){
        em.close();
        emf.close();
    }
    
    /**
     * add new score into our database
     * @param name
     */
    public void addScore(String name){
        if(em.find(Score.class, name)==null){
            em.getTransaction().begin();
            Score s = new Score(name,0);
            em.persist(s);
            em.getTransaction().commit();
        }
    }
    
    /**
     * romove one record from the database
     * @param name
     */
    public void removePlayer(String name) {
	    em.getTransaction().begin();
    	em.createQuery("DELETE FROM Score s WHERE s.nickname='" + name +  "'", Score.class).executeUpdate() ;
    	em.getTransaction().commit();
    }
    
    /**
     * romove everything inside of the database
     */
    public void removeAllPlayers () {
    	em.getTransaction().begin();
    	em.createQuery("DELETE FROM Score",Score.class).executeUpdate() ;
    	em.getTransaction().commit();
    }
    
    /**
     * update exsiting database
     * @param name
     */
    public void updateScore(String name){
        em.getTransaction().begin();
        TypedQuery <Score> query =
        em.createQuery("SELECT s FROM Score s WHERE s.nickname='"+name+"'", Score.class);
        Score s = query.getSingleResult();
        s.setScore(s.getScore()+1);
        em.getTransaction().commit();
    }
    
    /**
     * add a winner score to the database
     * @param name
     */
    public void addWinnerScore(String name){
        Collection<Score> result = this.getAllScores();
        for(Score s: result){
            if (s.getName().equals(name)){
                    this.updateScore(name);
            }
        }
        this.addScore(name);
    }
    
    /**
     * get all records from the database
     * @return
     */
    public Collection<Score> getAllScores(){
        em.getTransaction().begin();
        TypedQuery <Score> query =
        em.createQuery("SELECT s FROM Score s", Score.class);
        em.getTransaction().commit();
        List<Score> results = query.getResultList();
        return results;
    }

}

