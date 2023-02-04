/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battleshiptanshui;

import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.*;
import java.io.*;
import javax.swing.JOptionPane;



public class Cliente extends Jugador {
    int turn=0;
    int j1=0;
    int disparos=0;
    


    
    private Socket socket;

    private DataInputStream input;

    private DataOutputStream output;

    private JugadorGUI playerGUI;

    public Cliente(String name, Color myColor, Color opponentColor) {
        super(name, myColor, opponentColor);

        this.playerGUI = new JugadorGUI(this);

        this.playerGUI.setMyTurn(true);
    }

    public void start(String host, int port) throws IOException {

        try {
            socket = new Socket(host, port);

            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());

            sendMessage("conectado");

            new ClientPlayerThread().start();

        } catch (ConnectException ex) {
            JOptionPane.showMessageDialog(playerGUI, ex.getMessage());
            System.exit(0);
        } catch (Exception ex) {
            System.out.println("Host or port is unreachable.");
            System.exit(0);
        }
    }

    /**
     * This method send any given message to the server
     *
     * @param message the given message
     */
    @Override
    public void sendMessage(String message) {
        try {
            output.writeUTF(message);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(playerGUI, ex.getMessage());
        }
    }

    /**
     * Return the receiving message from server
     *
     * @return the read messge
     */
    @Override
    public String readMessage() {

        String readUTF = null;
        try {
            readUTF = input.readUTF().trim();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(playerGUI, "Server desconectado!");
        }

        return readUTF;
    }

    public void closeConnection() {
        try {
            socket.close();
            output.close();
            input.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private class ClientPlayerThread extends Thread {

        @Override
        public void run() {

            while (true) {

                String msg = readMessage();

                if (msg == null) {
                    System.exit(0);
                }

                if (msg.equals("start")) {

                    playerGUI.setVisible(true);
                    playerGUI.setNotification("Jugador 2 conectado, coloca tus buques.");

                    playerGUI.enableShipPlaceMode(true);

                } else if (msg.equals("placed")) {

                    reset();

                    if (playerGUI.hasPlacedShips()) {

                        sendMessage("play");

                        playerGUI.enableAttackMode(true);

                        if (playerGUI.isMyTurn()) {
                            playerGUI.setNotification("Tu turno");
                        } else {
                            playerGUI.setNotification("Turno del otro jugador");
                        }
                    } else {
                        playerGUI.setNotification("El jugador 2 colocó sus buques.. "
                                + "coloca los tuyos para comenzar..");
                    }
                } else if (msg.equals("play")) {

                    reset();

                    playerGUI.enableAttackMode(true);
                    
                    if (playerGUI.isMyTurn()) {
                        playerGUI.setNotification("Tu turno");
                    } else {
                        playerGUI.setNotification("Turno del otro jugador");
                    }

                } else if (msg.equals("exit")) {

                    closeConnection();
                    System.exit(0);
                } else if (msg.equals("reset")) {

                    playerGUI.reset(); 
                    playerGUI.setMyTurn(true);


                    reset();
                    
                    
                    
                    
                } else {
                    
                   if(turn<8){  
                       
                       if (disparos <1){
                           
                       
                    String[] split = msg.split(":");

                    if (split[0].equals("hit")) {
                        
                        String[] location = split[1].split(",");
                        int i = Integer.parseInt(location[0].trim());
                        int j = Integer.parseInt(location[1].trim());
                        j1=j1+10;
                        
                        System.out.println("Puntuacion de J1 es:" +j1);
                        
                        playerGUI.hit(i, j);
                        
                         disparos=disparos+1;
                         
                            

                        decreaseRestHits();

                        if (getNumOfRemainingHits() == 0) {
                            sendMessage("gano:" + getPlayerName());

                            playerGUI.setNotification("Ganaste el juego!");
                            int opt = JOptionPane.showConfirmDialog(playerGUI, "Quieres jugar de nuevo?", "Ganaste",
                                    JOptionPane.YES_NO_OPTION);

                            if (opt == 0) {
                                playerGUI.reset();
                                sendMessage("reset");
                                reset();
                                playerGUI.setMyTurn(true);
                                setWin(true);
                            } else {
                                sendMessage("exit");
                                closeConnection();
                                System.exit(0);
                            }
                        }

                    } else if (split[0].equals("miss")) {
                        
                        String[] location = split[1].split(",");
                        int i = Integer.parseInt(location[0].trim());
                        int j = Integer.parseInt(location[1].trim());
                        j1=j1-10;
                        
                        System.out.println("Puntuacion de J1 es:" +j1);
                        playerGUI.miss(i, j);
                         
                      
                        disparos=disparos+1;

                    } else if (split[0].equals("ganaste")) {
                        playerGUI.setNotification(split[1] + " ganaste el juego!" + "tu puntuacion es "+ j1);

                    } else {

                        String[] data = msg.split(",");

                        int i = Integer.parseInt(data[0].trim()); 
                        int j = Integer.parseInt(data[1].trim()); 
                        boolean attack = playerGUI.attack(i, j);

                        if (!win) {
                            sendMessage((attack ? "hit:" : "miss:") + (i + "," + j));
                        }
                        
                    }

                }if (disparos==1){
                    
                
                         playerGUI.setMyTurn(false);
                        playerGUI.setNotification("Es turno del otro jugador");
                        turn=turn+1;
                     disparos=0;
                     }
                   }
                    
                   if (turn==8){
                       playerGUI.setMyTurn(false);
                        
                       turn=turn+1;
                                        
                   }
                    if (turn==10){
                        playerGUI.enableAttackMode(false);
                       JOptionPane.showMessageDialog(playerGUI," fin de la partida tu puntuacion  es:" +j1);
                   }
                     
            }

        }
    }
}
    
    public static void main(String args[]) {

        int port = 4500; 
        String host = "localhost"; 

        if (args.length >= 2) {
            host = args[0].trim();
            try {
                port = Integer.parseInt(args[1].trim());
            } catch (NumberFormatException e) {
                System.out.println("Puerto incorrecto! puerto default utilizando.");
            }
        }
        Cliente client = new Cliente("Jugador 1", Color.yellow, Color.yellow);

        try {
            client.start(host, port);
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}