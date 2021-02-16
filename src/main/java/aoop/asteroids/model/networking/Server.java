
package aoop.asteroids.model.networking;

import aoop.asteroids.model.game.Game;
import aoop.asteroids.database.ScoreDB;
import aoop.asteroids.gui.AsteroidsFrame;
import aoop.asteroids.gui.HostFrame;
import aoop.asteroids.controller.HostController;
import aoop.asteroids.model.game.Spaceship;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *Server Thread, contains the real game and all clients
 * @author group1
 */
public class Server extends Observable implements Runnable {
    
    private final InetSocketAddress host;
    private ServerNetworkIO IO;
    private ArrayList<SocketAddress> spectators;
    private ArrayList<SocketAddress> players;
    private Game game;
    private final HostController player;
    private boolean hasSpectator;
    private ScoreDB database;
    private boolean gameOver;
    private volatile boolean running;
    public static final String[] colors = { "Cyan", "Green", "Pink", "Orange"};

    /**
     * constructor
     * @param address
     * @param name
     */
    public Server(InetSocketAddress address,String name){
        
        this.host = address;
        this.hasSpectator=false;
        this.gameOver = false;
        this.running = false;
        this.spectators = new ArrayList<>();
        this.players = new ArrayList<>();
        this.game = new Game();
        game.addShip(new Spaceship(name));
        this.IO = new ServerNetworkIO(host.getPort());
        this.player = new HostController ();
        this.game.linkController (player,0);
        this.database = new ScoreDB();
        database.openDataSource();
        
        this.game.addObserver ((Observable o, Object arg) -> {
            if (hasSpectator) {
                try {
                    for (SocketAddress address1 : spectators) {
                        byte[] data = game.serialize();
                        IO.sendPacket(new DatagramPacket(data, data.length, address1));
                    }
                }catch (Exception e) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, e);
                }
            }
            if (Server.this.game.getShips().size()>1) {
                for (SocketAddress address2 : players) {
                    try {
                        byte[] data = game.serialize();
                        IO.sendPacket(new DatagramPacket(data, data.length, address2));
                    }catch (IOException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        
    }

    /**
     * checks whether the game is running
     * @return 
     */
    public synchronized boolean  isRunning() {
        return running;
    }

    /**
     * thread run method
     */
    @Override
    public void run() {
        Thread t = new Thread (game);
        AsteroidsFrame frame = new AsteroidsFrame (game, player);
        HostFrame viewconnection = new HostFrame(this);
        
        Thread t1 = new Thread(new Runnable()  {
        @Override
        public void run() {
            while(true){
                DatagramPacket packet = IO.getPacket();
                processInput(packet);
                try {
                    Thread.currentThread().sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        });
        t1.start();
        
        while(!isRunning()) {
            
            try {
                Thread.sleep(1);
            } catch (InterruptedException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        while(isRunning()){

                t.start ();
                t1.stop();
                while(this.running == true){
                    try{
                       checkGameOver();
                       DatagramPacket packet = IO.getPacket();
                       processInput(packet);
                    }catch(Exception e){
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, e);
                    }
                    if(this.game.getShips().size()==1){
                        this.game.setMorethan2playes(false);
                        game.update();
                        game.abort();
                    }
                    if(this.gameOver){
                        this.getWinner().increaseScore();
                        for(Spaceship s : this.game.getShips()){
                            database.addScore(s.getName());
                        }
                        database.addWinnerScore(this.getWinner().getName());
                        this.resetGame();
                    }
                }
        }
    }
    /**
     * modifies the game, adds or removes clients according to received info
     * @param packet
     */
    public void processInput(DatagramPacket packet){
        String message = new String(packet.getData(), 0, packet.getLength());
        try {
            if (message.equals("Byebye")) {
                spectators.remove(packet.getSocketAddress());
            }else if(message.equals("Ciao")){
                int i = players.indexOf(packet.getSocketAddress());
                this.game.getShips().remove(i+1);
                players.remove(packet.getSocketAddress());
                this.setChanged();
                this.notifyObservers();
            }else if (message.startsWith("Join")) {
                this.game.setMorethan2playes(true);
                addPlayer(packet.getSocketAddress(),message);
                this.setChanged();
                this.notifyObservers();
            } else if (message.equals("hello")) {
                spectators.add(packet.getSocketAddress());
                this.hasSpectator = true;
            } else {
                boolean[] commands = toBooleanA(packet.getData());
                ArrayList <Spaceship> s=this.game.getShips();
                int index = players.indexOf(packet.getSocketAddress());
                s.get(index+1).setUp(commands[0]);
                s.get(index+1).setLeft(commands[1]);
                s.get(index+1).setRight(commands[2]);
                s.get(index+1).setIsFiring(commands[3]);
            }
        } catch(Exception e) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, e);
        }
    }


    /**
     * adds a new player to the game, as well as its info
     * @param address,message
     * @param message
     */
    public void addPlayer(SocketAddress address, String message){
        String[] input = message.split(":");
        if(this.game.getShips().size()<5){
            players.add(address);
            Spaceship ship = new Spaceship();
            ship.setName(input[1]);
            ship.setColor(userColor(colors[this.players.indexOf(address)]));
            this.game.addShip(ship);
        }else{
            String reject = "The room is full";
            byte[] rejectBytes = reject.getBytes();
            IO.sendPacket(new DatagramPacket(rejectBytes, rejectBytes.length, address));
        }
    }

    /**
     * returns all ships in the current game
     * @return 
     */
    public ArrayList<Spaceship> getConnections(){
        return this.game.getShips();
    }

    /**
     * set the game to run
     */
    public synchronized void setRunning(){
        this.running = true;
    }

    /**
     * returns true if the game is finished
     * and has just one player left alive
     */
    public void checkGameOver(){
        int count = this.game.getShips().size(); 
        
        if(count > 1){
            for (Spaceship s : this.game.getShips()){
                if(s.isDestroyed()){
                    count--;
                }
            }

        }
        
        if(count == 1){
            this.gameOver = true;
        }
    }

    /**
     * returns the winning spaceship
     * @return 
     */
    public Spaceship getWinner(){
       for (Spaceship s : this.game.getShips()){
           if(!s.isDestroyed()){
               return s;
           }
       }
       return null;
    }

    /**
     * create a new game, reset all objects
     */
    public void resetGame(){
        this.game.abort();
        this.gameOver = false;
        this.game.initGameData ();
    }

    /**
     * converts string to color
     * @param color
     * @return Color
     */
    public Color userColor(String color){
        switch(color){
            case("Cyan"):
                return Color.CYAN;
            case("Green"):
                return Color.GREEN;
            case("Pink"):
                return Color.PINK;
            case("Orange"):
                return Color.ORANGE;
            case("White"):
                return Color.WHITE;
        }
        return Color.BLACK;
    }


    /**
     * converts byte[] to integer
     * @param data
     * @return 
     */
    public static int toInt(byte[] data) {
        if (data == null || data.length != 4) return 0x0;
        return (int)(
            (0xff & data[0]) << 24  |
            (0xff & data[1]) << 16  |
            (0xff & data[2]) << 8   |
            (0xff & data[3]) << 0
            );
    }


    /**
     * converts byte[] to boolean
     * @param data
     * @return 
     */
    public static boolean toBoolean(byte[] data) {
        return (data == null || data.length == 0) ? false : data[0] != 0x00;
    }


    /**
     * converts byte[] to boolean[]
     * @param data
     * @return 
     */
    public static boolean[] toBooleanA(byte[] data) {
        if (data == null || data.length < 4) return null;
        int len = toInt(new byte[]{data[0], data[1], data[2], data[3]});
        boolean[] bools = new boolean[len];
        for (int i = 0, j = 4, k = 7; i < bools.length; i++) {
            bools[i] = ((data[j] >> k--) & 0x01) == 1;
            if (k < 0) { j++; k = 7; }
        }
        return bools;
    }  
    
}
      

