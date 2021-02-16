/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aoop.asteroids.database;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Entity class of Score
 * @author s3198928
 */
@Entity
public class Score implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    private String nickname;
    private int score;
    
   /**
    * constructor
    * @param name
    * @param score 
    */
    public Score(String name, int score){
        this.nickname = name;
        this.score = score;
    }
    
    /**
     * getname
     * @return
     */
    public String getName(){
        return this.nickname;
    }
    
    /**
     * getscore
     * @return
     */
    public int getScore(){
        return this.score;
    }
    
    /**
     * setname
     * @param name
     */
    public void setName(String name){
        this.nickname = name;
    }
    
    /**
     * setscore
     * @param s
     */
    public void setScore(int s){
        this.score = s;
    }

    /**
     * convertittostring
     * @return
     */
    @Override
    public String toString() {
        return this.nickname + ": " + this.score;
    }
    
}
