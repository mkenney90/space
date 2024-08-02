package com.zephyr;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;

public class Rock extends Sprite {
    private double xSpeed;
    private double ySpeed;
    private int radius;
    private int mass;
    private double impactForce;

    private int points;
    private int shadowPoints;
    private int xCoords[];
    private int yCoords[];
    private int s_xCoords[];
    private int s_yCoords[];

    private int strength;
    private double rotation;
    private double rotationDelta;
    protected Boolean visible;

    public Rock(int x, int y, int points) {
        super(x, y);
        this.points = points;
        this.shadowPoints = (int) (points / 2) + 1;

        strength = (int) Math.max(Math.random() * 4, 1);
        radius = (int) (Math.random() * 10) + 15;
        mass = radius / 10;

        xCoords = new int[points];
        yCoords = new int[points];
        
        // build the polygon based on the number of points passed to the constructor
        for (int i=0;i<points;i++) {
            double angle = i * (Math.PI / ((double)points / 2));
            double cosValue = Math.cos(angle);
            double sinValue = Math.sin(angle);
            double randomOffset = Math.random() * 6 - 3;
            
            if (Math.random() < 0.1) {
                xCoords[i] = (int) Math.round(cosValue * radius - 2 + randomOffset);
                yCoords[i] = (int) Math.round(sinValue * radius - 2 + randomOffset);
            } else {
                xCoords[i] = (int) Math.round(cosValue * radius + randomOffset);
                yCoords[i] = (int) Math.round(sinValue * radius + randomOffset);
            }
        }        

        s_xCoords = new int[shadowPoints];
        s_yCoords = new int[shadowPoints];

        // build the rock's shadow
        for (int i=0;i<shadowPoints;i++) {
            s_xCoords[i] = xCoords[i];
            s_yCoords[i] = yCoords[i];
        }
        s_xCoords[shadowPoints-1] = (int) Math.random() * 6 - 3;
        s_yCoords[shadowPoints-1] = (int) Math.random() * 6 - 3;

        xSpeed = 0;
        ySpeed = Math.random() * 0.5 + 0.6;

        rotation = (int) (Math.random() * 360);
        rotationDelta = (Math.random() * 8) - 4;
        visible = true;
    }

    public Polygon getPoly() {
        return new Polygon(xCoords, yCoords, points);
    }

    public int getRadius() {
        return radius;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int newStrength) {
        strength = newStrength;
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(int newRotation) {
        rotation = newRotation;
    }

    public void addRotation(double newDelta) {
        rotationDelta += newDelta;
    }

    public double getImpactForce() {
        return impactForce;
    }

    public void handleCollision(Rock otherRock) {
        double rif = impactForce;
        double orif = otherRock.getImpactForce();

        if (otherRock.getY() > y) {
            otherRock.addSpeed('y', rif);
            this.addSpeed('y', -orif);
        } else {
            otherRock.addSpeed('y', -rif);
            this.addSpeed('y', orif);
        }
        if (otherRock.getX() > x) {
            otherRock.addSpeed('x', rif);
            this.addSpeed('x', -orif);
        } else {
            otherRock.addSpeed('x', -rif);
            this.addSpeed('x', orif);
        }
    }

    public void setSpeed(char axis, double newSpeed) {
        if (axis == 'x') {
            xSpeed = newSpeed;
        } else {
            ySpeed = newSpeed;
        }
    }

    public void addSpeed(char axis, double deltaSpeed) {
        if (axis == 'x') {
            xSpeed += deltaSpeed;
        } else {
            ySpeed += deltaSpeed;
        }
    }
    
    public Area getBounds() {
        int[] xRot = new int[points];
        int[] yRot = new int[points];
    
        for (int i=0;i<points;i++) {
            xRot[i] = xCoords[i];
            yRot[i] = yCoords[i];
        }
        
        Polygon p1 = new Polygon(xRot, yRot, points);
        AffineTransform at = new AffineTransform();
    
        at.rotate(Math.toRadians(rotation), x, y);
        at.translate(x, y);
    
        Area a1 = new Area(at.createTransformedShape(p1));
        at.setToIdentity();
        
        return a1;
    }
    
    public Area getShadowBounds() {
        int[] xRot = new int[shadowPoints];
        int[] yRot = new int[shadowPoints];
    
        for (int i=0;i<shadowPoints;i++) {
            xRot[i] = s_xCoords[i];
            yRot[i] = s_yCoords[i];
        }
        
        Polygon p1 = new Polygon(xRot, yRot, shadowPoints);
        AffineTransform at = new AffineTransform();
    
        at.rotate(Math.toRadians(rotation), x, y);
        at.translate(x, y);
    
        Area a1 = new Area(at.createTransformedShape(p1));
        at.setToIdentity();
        
        return a1;
    }

    public void move() {
        rotation += rotationDelta;
        x = (x + (float) xSpeed);
        y = (y + (float) ySpeed);
        impactForce = (mass * (xSpeed + ySpeed)) / 5;
        setVisible(strength > 0);
    }
}
