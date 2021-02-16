package aoop.asteroids.model.game;

import aoop.asteroids.model.networking.Spectator;
import aoop.asteroids.controller.HostController;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Observable;
import java.util.Random;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *	The game class is the backbone of all simulations of the asteroid game. It 
 *	contains all game object and keeps track of some other required variables 
 *	in order to specify game rules.
 *	<p>
 *	The game rules are as follows:
 *	<ul>
 *		<li> All game objects are updated according to their own rules every 
 *			game tick. </li>
 *		<li> Every 200th game tick a new asteroid is spawn. An asteroid cannot 
 *			spawn within a 50 pixel radius of the player. </li>
 *		<li> There is a maximum amount of asteroids that are allowed to be 
 *			active simultaneously. Asteroids that spawn from destroying a 
 *			larger asteroid do count towards this maximum, but are allowed to 
 *			spawn if maximum is exceeded. </li>
 *		<li> Destroying an asteroid spawns two smaller asteroids. I.e. large 
 *			asteroids spawn two medium asteroids and medium asteroids spawn two 
 *			small asteroids upon destruction. </li>
 *		<li> The player dies upon colliding with either a buller or an 
 *			asteroid. </li>
 *		<li> Destroying every 5th asteroid increases the asteroid limit by 1, 
 *			increasing the difficulty. </li>
 *	</ul>
 *	<p>
 *	This class implements Runnable, so all simulations will be run in its own 
 *	thread. This class extends Observable in order to notify the view element 
 *	of the program, without keeping a reference to those objects.
 *
 *	@author Yannick Stoffers
 */
public class Game extends Observable implements Runnable, Serializable
{

    /** All the ships allwoed in the game. */
    private ArrayList<Spaceship> ships;
    /** List of bullets. */
    private Collection <Bullet> bullets;

    /** List of asteroids. */
    private Collection <Asteroid> asteroids;

    /** Random number generator. */
    private static Random rng;

    /** Game tick counter for spawning random asteroids. */
    public int cycleCounter;


    /** Asteroid limit. */
    private int asteroidsLimit;
    /** Signle palyer variable. */
    private boolean single;
    
    /**
     * This boolean is to make sure when it is multiplaying mode,
     * when there is one player left. The player would not be able
     * to restart the game.
     */
    private boolean morethan2playes;



    /** 
     *	Indicates whether the a new game is about to be started. 
     *
     *	@see #run()
     */
    private boolean aborted;

    /** Initializes a new game from scratch. */
    public Game ()
    {
        Game.rng = new Random ();
        this.ships = new ArrayList<>();
        this.single = false;
        this.morethan2playes = true;
        this.initGameData ();
    }

    /** Sets all game data to hold the values of a new game. */
    public void initGameData ()
    {   
        if(this.morethan2playes == false) return;
        this.aborted = false;
        this.cycleCounter = 0;
        this.asteroidsLimit = 7;
        this.bullets = new ArrayList <> ();
        this.asteroids = new ArrayList <> ();
        for(Spaceship s:this.ships){
            if(this.single == true){
                s.reinit (0);
            }else{
                s.reinit(1);
            }
        }
    }

    /** 
     *	Links the given controller to the spaceship. 
     *
     *	@param p the controller that is supposed to control the spaceship.
     */
    public void linkController (HostController p, int x)
    {
        p.addShip (this.ships.get(x));

    }

    public ArrayList<Spaceship> getShips(){
        return this.ships;
    }
  
    public void addShip(Spaceship s){
        this.ships.add(s);
    }
  
    public void setShips(Game game){
        this.ships = game.getShips();
    }
    
    public void setMorethan2playes(boolean b){
        this.morethan2playes = b;
    }

	/** 
	 *	Returns a clone of the asteroid set, preserving encapsulation.
	 *
	 *	@return a clone of the asteroid set.
	 */
    public Collection <Asteroid> getAsteroids ()
    {
        Collection <Asteroid> c = new ArrayList <> ();
        for (Asteroid a : this.asteroids) c.add (a.clone ());
        return c;
    }
  
    public void setAsteroids (Game game){
        this.asteroids = game.getAsteroids();
    }

	/** 
	 *	Returns a clone of the bullet set, preserving encapsulation.
	 *
	 *	@return a clone of the bullet set.
	 */
    public Collection <Bullet> getBullets ()
    {
        Collection <Bullet> c = new ArrayList <> ();
        for (Bullet b : this.bullets) c.add (b.clone ());
        return c;
    }
  
    public void setBullets(Game game){
        this.bullets = game.getBullets();
    }

    public void setSingleMode(){
        this.single = true;
    }
	/**
	 *	Method invoked at every game tick. It updates all game objects first. 
	 *	Then it adds a bullet if the player is firing. Afterwards it checks all 
	 *	objects for collisions and removes the destroyed objects. Finally the 
	 *	game tick counter is updated and a new asteroid is spawn upon every 
	 *	200th game tick.
	 */
    public void update ()
    {
        for (Asteroid a : this.asteroids) a.nextStep ();
        for (Bullet b : this.bullets) b.nextStep ();
        for(Spaceship s : this.ships){
            s.nextStep ();

            if (s.isFiring ())
            {
                double direction = s.getDirection ();
                this.bullets.add (new Bullet(s.getLocation (), s.getVelocityX () + Math.sin (direction) * 15, s.getVelocityY () - Math.cos (direction) * 15));
                s.setFired ();
            }
        }
        this.checkCollisions ();
        this.removeDestroyedObjects ();
        if (this.cycleCounter == 0 && this.asteroids.size () < this.asteroidsLimit) this.addRandomAsteroid ();
        this.cycleCounter++;
        this.cycleCounter %= 200;
        this.setChanged ();
        this.notifyObservers ();
  }
  
    public void update(Game receivedGame){
        this.setAsteroids(receivedGame);
        this.setBullets(receivedGame);
        this.setShips(receivedGame);
        this.setChanged ();
        this.notifyObservers ();
    }
  
	/** 
	 *	Adds a randomly sized asteroid at least 50 pixels removed from the 
	 *	player.
	 */
    private void addRandomAsteroid ()
    {
        int prob = Game.rng.nextInt (3000);

        for(Spaceship s:this.ships){
            Point loc, shipLoc = s.getLocation ();
            int x, y;
            do
            {
                loc = new Point (Game.rng.nextInt (800), Game.rng.nextInt (800));
                x = loc.x - shipLoc.x;
                y = loc.y - shipLoc.y;
            }
            while (Math.sqrt (x * x + y * y) < 50);

            if (prob < 1000)		this.asteroids.add (new LargeAsteroid  (loc, Game.rng.nextDouble () * 6 - 3, Game.rng.nextDouble () * 6 - 3));
            else if (prob < 2000)	this.asteroids.add (new MediumAsteroid (loc, Game.rng.nextDouble () * 6 - 3, Game.rng.nextDouble () * 6 - 3));
            else					this.asteroids.add (new SmallAsteroid  (loc, Game.rng.nextDouble () * 6 - 3, Game.rng.nextDouble () * 6 - 3));
         }
    }

	/** 
	 *	Checks all objects for collisions and marks them as destroyed upon
	 *	collision. All objects can collide with objects of a different type, 
	 *	but not with objects of the same type. I.e. bullets cannot collide with 
	 *	bullets etc.
	 */
    private void checkCollisions ()
    { // Destroy all objects that collide.
        for (Bullet b : this.bullets)
        { // For all bullets.
            for (Asteroid a : this.asteroids)
            { // Check all bullet/asteroid combinations.
                if (a.collides (b))
                { // Collision -> destroy both objects.
                        b.destroy ();
                        a.destroy ();
                }
            }

            for(Spaceship s:this.ships){
                if (b.collides (s))
                { // Collision with playerß -> destroy both objects
                  b.destroy ();
                  s.destroy ();
                }
            }
        }

        for (Asteroid a : this.asteroids)
        { // For all asteroids, no cross check with bullets required.
            for (Spaceship s : this.ships){ 

              if (a.collides (s))
              { // Collision with player -> destroy both objects.
                a.destroy ();
                s.destroy ();
              }
            }
        }
    }

	/**
	 * 	Increases the score of the player by one and updates asteroid limit 
	 *	when required.
	 */
    private void increaseScore ()
    {
        for(Spaceship s:this.ships){
          s.increaseScore ();
          if (s.getScore () % 5 == 0) this.asteroidsLimit++;
        }
    }

	/**
	 *	Removes all destroyed objects. Destroyed asteroids increase the score 
	 *	and spawn two smaller asteroids if it wasn't a small asteroid. New 
	 *	asteroids are faster than their predecessor and travel in opposite 
	 *	direction.
	 */
    private void removeDestroyedObjects ()
    {   
        Collection <Asteroid> newAsts = new ArrayList <> ();
        for (Asteroid a : this.asteroids)
        {
            if (a.isDestroyed ())
            {
                    if(this.single == true) this.increaseScore ();
                    Collection <Asteroid> successors = a.getSuccessors ();
                    newAsts.addAll (successors);
            }
            else newAsts.add (a);
        }
        this.asteroids = newAsts;
        Collection <Bullet> newBuls = new ArrayList <> ();
        for (Bullet b : this.bullets) if (!b.isDestroyed ()) newBuls.add (b);
        this.bullets = newBuls;
    }

	/**
	 *	Returns whether the game is over. The game is over when the spaceship 
	 *	is destroyed.
	 *
	 *	@return true if game is over, false otherwise.
	 */ 
    public boolean gameOver ()
    {
        for(Spaceship s:ships){
            if(!s.isDestroyed()){
                return false;
            }
        }
        return true;
    }

	/** 
	 *	Aborts the game. 
	 *
	 *	@see #run()
	 */
    public void abort ()
    {
         this.aborted = true;
    }

	/**
	 *	This method allows this object to run in its own thread, making sure 
	 *	that the same thread will not perform non essential computations for 
	 *	the game. The thread will not stop running until the program is quit. 
	 *	If the game is aborted or the player died, it will wait 100 
	 *	milliseconds before reevaluating and continuing the simulation. 
	 *	<p>
	 *	While the game is not aborted and the player is still alive, it will 
	 *	measure the time it takes the program to perform a game tick and wait 
	 *	40 minus execution time milliseconds to do it all over again. This 
	 *	allows the game to update every 40th millisecond, thus keeping a steady 
	 *	25 frames per second. 
	 *	<p>
	 *	Decrease waiting time to increase fps. Note 
	 *	however, that all game mechanics will be faster as well. I.e. asteroids 
	 *	will travel faster, bullets will travel faster and the spaceship may 
	 *	not be as easy to control.
	 */
    @Override
    public void run ()
    { // Update -> sleep -> update -> sleep -> etc...
        long executionTime, sleepTime;
        while (true)
        {
            if (!this.gameOver () && !this.aborted)
            {
                executionTime = System.currentTimeMillis ();
                this.update ();
                executionTime -= System.currentTimeMillis ();
                sleepTime = Math.max (0, 40 + executionTime);
            }
            else sleepTime = 100;
                    
            try
            {
                Thread.sleep (sleepTime);
            }
            catch (InterruptedException e)
            {
                Logger.getLogger(Spectator.class.getName()).log(Level.SEVERE, "Could not perfrom action: Thread.sleep(...)");
            }
        }
    }
    
    /**
     * Write the game into byte[].
     * @return
     * @throws IOException 
     */
    public byte[] serialize() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(this);
        return out.toByteArray();
    }

	
    /**
     * Convert byte[] back to game.
     * @param bytes
     * @return
     * @throws IOException
     * @throws ClassNotFoundException 
     */
    public Game deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        try(ByteArrayInputStream b = new ByteArrayInputStream(bytes)){
          try(ObjectInputStream o = new ObjectInputStream(b)){
            return (Game)o.readObject();
          }
        }
    }
    
}
