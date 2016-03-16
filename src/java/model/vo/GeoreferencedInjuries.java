/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.vo;

/**
 *
 * @author nelson
 */
public class GeoreferencedInjuries {

    private int injury_id;
    private double x;
    private double y;
    
    public GeoreferencedInjuries(){
    }

    public int getInjury_id() {
        return injury_id;
    }

    public void setInjury_id(int injury_id) {
        this.injury_id = injury_id;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

}
