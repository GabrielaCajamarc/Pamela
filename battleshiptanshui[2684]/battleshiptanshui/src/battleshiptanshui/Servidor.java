/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battleshiptanshui;

import java.awt.Color;
import javax.swing.JOptionPane;
import java.net.*;
import java.io.*;


public class Servidor extends Jugador {
    int punt=0;
    
    int turn=0;
    
    int j1=0;
    
    int disparos=0;
    
    private ServerSocket serversocket;

    private Socket socket;

    private DataOutputStream output;

    private DataInputStream input;

    private JugadorGUI playerGUI;
    
    public boolean autoPlay = false;
    
     
    
   

    public Servidor(String name, Color myColor, Color opponentColor) {
        super(name, myColor, opponentColor);

        this.playerGUI = new JugadorGUI(this);

        this.playerGUI.setVisible(true);

        this.playerGUI.setMyTurn(false);
        
    }

    public void start(int port) throws IOException {

        System.out.printf("Servidor(%s) comenzo la conexion en el puerto: %d\n", getPlayerName(), port);
        System.out.println("Esperando al jugador 1...");
        System.out.println("Tu puntuacion es:" +j1);
        playerGUI.setNotification("Esperando al jugador 1...");

        serversocket = new ServerSocket(port);

        socket = serversocket.accept();

        output = new DataOutputStream(socket.getOutputStream());
        input = new DataInputStream(socket.getInputStream());

        new ServerPlayerThread().start();
        

    }

     /**
     * This method send any given message to the connected client
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
     * Return the receiving message from client
     * @return the read message
     */
    @Override
    public String readMessage() {

        String readUTF = null;
        try {
            readUTF = input.readUTF().trim();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(playerGUI, "Jugador cliente se desconecto");
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

    private class ServerPlayerThread extends Thread {
       
        @Override
        public void run() {

            while (true) {
                
                String msg = readMessage();
                
                if (msg == null) {
                    System.exit(0);
                }
                
                if (msg.equals("conectado")) {
                    
                    sendMessage("start");
                    
                    playerGUI.enableShipPlaceMode(true);
                    playerGUI.setNotification("Jugador 1 se conectó, coloque todos sus buques.");
                    System.out.println("Jugador 1 conectado!");

                } else if (msg.equals("placed")) {

                    if (playerGUI.hasPlacedShips()) {
                        
                        sendMessage("play");
                        
                        playerGUI.enableAttackMode(true);
                        
                        if (playerGUI.isMyTurn()) {
                            
                            playerGUI.setNotification("Tu turno");
                         
                            
                        } else {
                            playerGUI.setNotification("Es turno del otro jugador");
                        }
                    } else {
                        playerGUI.setNotification("El jugador 1 colocó sus buques.. coloca los tuyos para comenzar.");
                    }
                } else if (msg.equals("play")) {
                    
                    playerGUI.enableAttackMode(true);

                    if (playerGUI.isMyTurn()) {
                        playerGUI.setNotification("Tu turno");
                    } else {
                        playerGUI.setNotification("Es turno del otro jugador");
                    }

                } else if (msg.equals("exit")) {
                    
                    closeConnection();
                    System.exit(0);
                } else if (msg.equals("reset")) {
                    
                    playerGUI.reset();
                    autoPlay = false;
                    playerGUI.setMyTurn(false);
                    reset();
                    
 
                } else {
                    if(turn<9){
                        
                        if (disparos < 1){
                    
                    String[] split = msg.split(":");

                    if (split[0].equals("hit")) {
                        String[] location = split[1].split(",");
                        int i = Integer.parseInt(location[0].trim());
                        int j = Integer.parseInt(location[1].trim());
                        j1=j1+10;
                        System.out.println("Puntuacion de J2 es:" +j1);
                        playerGUI.hit(i, j);
                        
                        disparos=disparos+1;
                         decreaseRestHits();

                        if (getNumOfRemainingHits() == 0) {

                            sendMessage("ganaste:" + getPlayerName());
                            playerGUI.setNotification("GANASTE!");

                            int opt = JOptionPane.showConfirmDialog(playerGUI, "Ganaste!. Quieres jugar de nuevo?", "Ganaste",
                                    JOptionPane.YES_NO_OPTION);

                            if (opt == 0) {
                                playerGUI.reset();
                                sendMessage("reset");
                                reset();
                                autoPlay = false;
                                playerGUI.setMyTurn(false);
                            } else {
                                sendMessage("salir");
                                closeConnection();
                                System.exit(0);
                            }
                        }

                    } else if (split[0].equals("miss")) {
                        String[] location = split[1].split(",");
                        int i = Integer.parseInt(location[0].trim());
                        int j = Integer.parseInt(location[1].trim());
                        j1=j1-10;
                        System.out.println("Puntuacion de J2 es::" +j1);
                        playerGUI.miss(i, j);
                        
                        disparos=disparos+1;

                    } else if (split[0].equals("ganaste")) {
                        playerGUI.setNotification(split[1] + " ganó el juego!" + "tu puntuacion es "+j1);
                        autoPlay = false;

                    } else {
                        
                        String[] data = msg.split(",");
                        
                        int i = Integer.parseInt(data[0].trim()); 
                        int j = Integer.parseInt(data[1].trim()); 
                        boolean attack = playerGUI.attack(i, j);

                        sendMessage((attack ? "hit:" : "miss:") + (i + "," + j));
                        
                        if(autoPlay) {
                           
                            int[] guess = playerGUI.guessAttack();
                            int r = guess[0];
                            int c = guess[1];
                            
                            sendMessage(r + "," + c);
                        }

                    }
                }if (disparos==1){
                    
                
                         playerGUI.setMyTurn(false);
                        playerGUI.setNotification("Es turno del otro jugador");
                        turn=turn+1;
                     disparos=0;
                     }
                   }if (turn==8){
                       playerGUI.setMyTurn(false);
                       
                   JOptionPane.showMessageDialog(playerGUI," fin de la partida tu puntuacion  es:" +j1);
                   
                   
                       playerGUI.enableAttackMode(false);
                   
                      
                       
                   }
                 
                    
            }       
        }
    }
}
    public static void main(String[] args) {

        int port = 4500; 

        if (args.length >= 1) {

            try {
                port = Integer.parseInt(args[0].trim());
            } catch (NumberFormatException e) {
                System.out.println("Puerto incorrecto! puerto default utilizando.");
            }
        }
        Servidor server = new Servidor("Jugador 2", Color.yellow, Color.yellow);

        try {
            server.start(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

