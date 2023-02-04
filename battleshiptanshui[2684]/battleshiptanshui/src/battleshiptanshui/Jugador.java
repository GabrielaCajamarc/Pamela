/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battleshiptanshui;

import java.awt.Color;

public abstract class Jugador {

    private String name;
    private int numOfRemainingHits;
    private Color myColor, opponentColor;

    public boolean win = false;

    public Jugador(String name, Color myColor, Color opponentColor) {
        this.name = name;
        this.myColor = myColor;
        this.opponentColor = opponentColor;
        this.numOfRemainingHits = 5 + 4 + 3 + 2 + 1;
    }

    public String getPlayerName() {
        return name;
    }

    public void setWin(boolean win) {
        this.win = win;
    }

    public Color getMyColor() {
        return myColor;
    }

    public Color getOpponentColor() {
        return opponentColor;
    }

    public void decreaseRestHits() {
        numOfRemainingHits--;
    }

    public int getNumOfRemainingHits() {
        return numOfRemainingHits;
    }

    public void reset() {
        this.numOfRemainingHits = 5 + 4 + 3 + 2 + 1;
        win = false;
    }

    public abstract void sendMessage(String message);

    public abstract String readMessage();
}

