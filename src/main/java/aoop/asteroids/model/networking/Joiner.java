package aoop.asteroids.model.networking;

import aoop.asteroids.model.game.Game;
import aoop.asteroids.gui.AsteroidsFrame;
import aoop.asteroids.controller.JoinerController;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.SocketAddress;
import javax.swing.JOptionPane;

/**
 *Thread for new players who join multiplayer mode game
 * @author group1
 */
public class Joiner extends Thread {

    private Game game;
    private boolean[] commands;
    private final SocketAddress host;
    private String name;
    private JoinerController control;
    private AsteroidsFrame frame;
    private final ClientNetworkIO IO;

    /**
     * constructor
     * @param address
     * @param name
     */
    public Joiner(SocketAddress address, String name){
        this.host=address;
        this.name = name;
        IO = new ClientNetworkIO(host);
        this.game=new Game();
        this.commands = new boolean[4];
        this.control = new JoinerController();
        control.addJoiner(this);
    }

    /**
     * thread run method
     */
    @Override
    public void run(){
        try {
            IO.sendMessage("Join:"+name);
            this.frame = new AsteroidsFrame (game,this);
            frame.addKeyListener(this.control);
            while(true) {
                DatagramPacket packet = IO.getPacket();
                processInput(packet);
                byte[] data= toByte(commands);
                IO.sendPacket(new DatagramPacket(data, data.length, host));
            }
          } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(Spectator.class.getName()).log(Level.SEVERE, "There is no such host");
        }
    }

    /**
     * disconnect from game
     */
    public void disconnect(){
        this.IO.sendMessage("Ciao");
        this.IO.disconnect();
        this.stop();
    }
    
    public boolean[] getCommands(){
        return this.commands;
    }

    /**
     * converts boolean[] to byte[]
     * @param data
     * @return 
     */
    public static byte[] toByte(boolean[] data) {
        if (data == null) return null;
        int len = data.length;
        byte[] lena = toByte(len);
        byte[] byts = new byte[lena.length + (len / 8) + (len % 8 != 0 ? 1 : 0)];
        System.arraycopy(lena, 0, byts, 0, lena.length);
        for (int i = 0, j = lena.length, k = 7; i < data.length; i++) {
            byts[j] |= (data[i] ? 1 : 0) << k--;
            if (k < 0) {
                j++;
                k = 7;
            }
        }
        return byts;
}
    /**
     * converts integer to byte[]
     * @param data
     * @return 
     */
    public static byte[] toByte(int data) {
            return new byte[] {
                    (byte)((data >> 24) & 0xff),
                    (byte)((data >> 16) & 0xff),
                    (byte)((data >> 8) & 0xff),
                    (byte)((data >> 0) & 0xff),
            };
    }

    /**
     * converts boolean to byte[]
     * @param data
     * @return 
     */
    public static byte[] toByte(boolean data) {
        return new byte[]{(byte) (data ? 0x01 : 0x00)};
    }
    /**
     * processes packet received from server
     * @param packet
     * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     */
    public void processInput(DatagramPacket packet) throws IOException, ClassNotFoundException{
        String message = new String(packet.getData(), 0, packet.getLength());
        if (message.equals("The room is full")){
            this.frame.setVisible(false);
            JOptionPane.showMessageDialog(null, "The room is full","Warning", JOptionPane.INFORMATION_MESSAGE);
        }else{
            Game receivedModel = new Game().deserialize(packet.getData());
            game.update(receivedModel);
        }
    }
}
