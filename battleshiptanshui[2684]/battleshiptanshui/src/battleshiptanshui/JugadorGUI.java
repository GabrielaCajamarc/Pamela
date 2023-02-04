/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battleshiptanshui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.border.EmptyBorder;

public class JugadorGUI extends JFrame {
    
    int BarcG=0;
    
        

    private int shipSize = 3;

    private static final int ROW = 7, COL = 6;

    private JPanel leftGridPanel, rightGridPanel, bottomNotificationPanel;

    private JLabel notification;

    private JButton[][] myButtons;

    private JButton[][] opponentButtons;

    private JButton autoPlayButton;

    public boolean myTurn, placedShips;

    private int[][] status;

    private boolean[][] attackedGrids;

    private Jugador player;

    private Random random;

    private ShipPlacementHandler shipPlacementHandler;

    private AttackHandler attackHandler;
    
     
     
    public JugadorGUI(Jugador player) throws HeadlessException {
        super(player.getPlayerName());
        this.player = player;
        this.random = new Random();
        this.placedShips = false;

        init();

        createGUI();
        
        
    }

    public void init() {

        leftGridPanel = new JPanel();
        rightGridPanel = new JPanel();
        bottomNotificationPanel = new JPanel();

        notification = new JLabel();

        myButtons = new JButton[ROW][COL];
        opponentButtons = new JButton[ROW][COL];
        autoPlayButton = new JButton("Auto Play");

        status = new int[ROW][COL];
        attackedGrids = new boolean[ROW][COL];

        shipPlacementHandler = new ShipPlacementHandler();
        attackHandler = new AttackHandler(this);

        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                status[i][j] = 0;
                attackedGrids[i][j] = false;
            }
        }
    }

    public void createGUI() {

        leftGridPanel = new JPanel();
        leftGridPanel.setLayout(new GridLayout(ROW, COL));
        leftGridPanel.setBackground(player.getMyColor());

        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                myButtons[i][j] = new JButton();
                leftGridPanel.add(myButtons[i][j]);

                myButtons[i][j].setPreferredSize(new Dimension(40, 6));

                myButtons[i][j].addActionListener(shipPlacementHandler);

                myButtons[i][j].setEnabled(false);

                myButtons[i][j].setFocusPainted(false);

                myButtons[i][j].setOpaque(true);

            }
        }

        autoPlayButton.setPreferredSize(new Dimension(100, 25));
        autoPlayButton.setEnabled(false);

        autoPlayButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String txt = autoPlayButton.getText();
                Servidor server = (Servidor) player;
                if (txt.equals("Auto Play")) {
                    autoPlayButton.setText("Manual");
                    server.autoPlay = true;
                    if (!placedShips) {
                        autoPlaceShips();
                    }
                } else {
                    autoPlayButton.setText("Auto Play");
                    server.autoPlay = false;
                }
            }
            
        });

        rightGridPanel = new JPanel();
        rightGridPanel.setLayout(new GridLayout(7, 6));
        rightGridPanel.setBackground(player.getOpponentColor());

        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                opponentButtons[i][j] = new JButton();
                rightGridPanel.add(opponentButtons[i][j]);

                opponentButtons[i][j].setEnabled(false);

                opponentButtons[i][j].setOpaque(true);

                opponentButtons[i][j].setFocusPainted(false);

                opponentButtons[i][j].setBackground(Color.BLACK);

                opponentButtons[i][j].setPreferredSize(new Dimension(60, 6));

                opponentButtons[i][j].addActionListener(attackHandler);
            }
        }

        bottomNotificationPanel = new JPanel();

        bottomNotificationPanel.add(notification);

        if (player instanceof Servidor) {

            this.add(leftGridPanel, BorderLayout.WEST);
            this.add(rightGridPanel, BorderLayout.EAST);
            this.setLocation(new Point(950, 100));
            bottomNotificationPanel.add(autoPlayButton);

        } else {
            this.add(leftGridPanel, BorderLayout.EAST);
            this.add(rightGridPanel, BorderLayout.WEST);
            this.setLocation(new Point(80, 100));
        }

        this.add(bottomNotificationPanel, BorderLayout.SOUTH);

        this.setSize(600, 400);

        this.setResizable(false);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void reset() {

        shipSize = 3;

        placedShips = false;

        setNotification("Coloca tus buques de nuevo!");

        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {

                status[i][j] = 0;
                attackedGrids[i][j] = false;

                opponentButtons[i][j].setBackground(Color.gray);
                autoPlayButton.setText("Auto Play");
                myButtons[i][j].setBackground(Color.gray);
            }
        }
        enableAttackMode(false);

        enableShipPlaceMode(true);
    }

    public boolean isMyTurn() {
        return myTurn;
    }

    public void setMyTurn(boolean myTurn) {
        this.myTurn = myTurn;
    }

    public void enableAttackMode(boolean value) {
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 6; j++) {
                opponentButtons[i][j].setEnabled(value);
                autoPlayButton.setEnabled(value);
            }
        }
    }
    
   
    

    public void enableShipPlaceMode(boolean value) {
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 6; j++) {
                myButtons[i][j].setEnabled(value);
                autoPlayButton.setEnabled(value);
            }
        }
    }

    public void setNotification(String msg) {
        this.notification.setText(msg);
    }

    public void hit(int i, int j) {

        attackedGrids[i][j] = true;
        opponentButtons[i][j].setBackground(Color.GREEN);
        opponentButtons[i][j].setEnabled(false);
    }

    public void miss(int i, int j) {

        attackedGrids[i][j] = true;
        opponentButtons[i][j].setBackground(Color.RED);
        opponentButtons[i][j].setEnabled(false);
    }

    // i = row index, j = column index
    public boolean attack(int i, int j) {

        setMyTurn(true);
        setNotification("Tu turno");

        if (status[i][j] == 0) {
            return false;
        } else {
            myButtons[i][j].setOpaque(true);
            myButtons[i][j].setBackground(Color.RED);
            status[i][j] = 0;
            return true;
        }

    }

    public void autoPlaceShips() {

        while (!placedShips) {
            int i = random.nextInt(ROW);
            int j = random.nextInt(COL);

            placeShip(i, j);
        }
    }

    public int[] guessAttack() {
        while (true) {
            int i = random.nextInt(ROW);
            int j = random.nextInt(COL);

            if (!attackedGrids[i][j]) {
                return new int[]{i, j};
            }
        }
    }

    /**
     * To place the current sized ship from the clicked grid
     *
     * @param i - row index
     * @param j - col index
     */
    public void placeShip(int i, int j) {
         i = (int) (Math.random() * 8 + 1);
         j = (int) (Math.random() * 7 + 1);
        boolean up = false, down = false, right = false, left = false;
        if (i - shipSize < 0) {
            up = false;
        } else {
            int total = 0;
            for (int k = 0; k < shipSize; k++) {
                total += status[i - k][j];
            }

            if (total == 0) {
                up = true;
            }
        }
        

        if (i - shipSize > 0) {
            down = false;
        } else {
            int total = 0;
            for (int k = 0; k < shipSize; k++) {
                total += status[i - k][j];
            }

            if (total == 0) {
                down = true;
            }
        }

        if (j + shipSize > 3) {
            right = false;
        } else {
            int total = 0;
            for (int k = 0; k < shipSize; k++) {
                total += status[i][j + k];
            }

            if (total == 0) {
                right = true;
            }
        }

        if (j - shipSize < 0) {
            left = false;
        } else {
            int total = 0;
            for (int k = 0; k < shipSize; k++) {
                total += status[i][j - k];
            }

            if (total == 0) {
                left = true;
            }
        }

        if (!up && !down && !right && !left) {
            System.out.printf("No se puede "
                    + "colocar el buque de %d \n", shipSize);
        } else {

            int direction = 0;
            while (true) {
                direction = random.nextInt(4);
                if (direction == 0 && down) {
                    break;
                } else if (direction == 1 && right) {
                    break;
                } else if (direction == 2 && up) {
                    break;
                } else if (direction == 3 && left) {
                    break;
                }
            }

            switch (direction) {
                case 0: // down
                    for (int k = 0; k < shipSize; k++) {
                        status[i + k][j] = shipSize;
                        myButtons[i + k][j].setBackground(player.getMyColor());
                    }
                    break;
                case 1: // right
                    for (int k = 0; k < shipSize; k++) {
                        status[i][j + k] = shipSize;
                        myButtons[i][j + k].setBackground(player.getMyColor());
                    }
                    break;
                case 2: // up
                    for (int k = 0; k < shipSize; k++) {
                        status[i - k][j] = shipSize;
                        myButtons[i - k][j].setBackground(player.getMyColor());
                    }
                    break;
                case 3: // left
                    for (int k = 0; k < shipSize; k++) {
                        status[i][j - k] = shipSize;
                        myButtons[i][j - k].setBackground(player.getMyColor());
                    }
                    break;
                default:
                    break;
            }

           
                 BarcG= BarcG+1;
                               
            if(BarcG==3){
               shipSize=2;
            }
            if(BarcG==5){
               shipSize=0;
            }

            if (shipSize <= 0) {
                placedShips = true;
                player.sendMessage("placed");

                enableShipPlaceMode(false);
            }
        }

    }

    public boolean hasPlacedShips() {
        return placedShips;
    }

    private class ShipPlacementHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            if (player instanceof Servidor) {
                Servidor s = (Servidor) player;

                if (s.autoPlay) {
                    return;
                }
            }

            Object source = e.getSource();
            for (int i = 0; i < ROW; i++) {
                for (int j = 0; j < COL; j++) {
                    if (source == myButtons[i][j]) {
                        placeShip(i, j);
                        return;
                    }
                }
            }
        }
    }

    private class AttackHandler implements ActionListener {
        
        JugadorGUI gui;

        public AttackHandler(JugadorGUI gui) {
            this.gui = gui;
        }
        
       

        @Override
        public void actionPerformed(ActionEvent e) {
            
            if (player instanceof Servidor) {
                Servidor s = (Servidor) player;

                if (s.autoPlay) {
                    return;
                }
            }

            Object source = e.getSource();
            for (int i = 0; i < ROW; i++) {
                for (int j = 0; j < COL; j++) {
                    
                    if (source == opponentButtons[i][j]) {
                        if (isMyTurn()) {
                             player.sendMessage(i + "," + j);
                             
                        } else 
                             
                            JOptionPane.showMessageDialog(gui, "Turno del otro jugador");
                        
                        }
                    }
                }
            }
        }
    }



