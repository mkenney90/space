package com.zephyr;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;

import java.awt.geom.Line2D;

public class Rock extends Sprite {
    private double xSpeed;
    private double ySpeed;
    private int radius;
    private int mass;
    private double impactForce;

    private int shieldLevel;
    private Boolean damaged;
    private int numPoints;
    private int numShadowPoints;
    private int xCoords[];
    private int yCoords[];
    private int sxCoords[];
    private int syCoords[];
    private int dxCoords[];
    private int dyCoords[];

    private int maxStrength;
    private int strength;
    private double rotation;
    private double rotationDelta;
    protected Boolean visible;

    public Rock(int x, int y, int points) {
        super(x, y);
        this.numPoints = points;
        this.numShadowPoints = numPoints - 1;
        
        xSpeed = 0;
        ySpeed = Math.random() * 0.5 + 0.6;

        rotation = (int) (Math.random() * 360);
        rotationDelta = Util.randomRange(-4, 4);
        visible = true;

        damaged = false;
        shieldLevel = (Math.random() > .80 ? 1 : 0);
        radius = (int) Util.randomRange(15, 25);
        maxStrength = (int) Math.ceil(radius / 6);
        strength = maxStrength;
        mass = radius / 10;

        // build the polygon based on the number of points passed to the constructor
        xCoords = new int[numPoints];
        yCoords = new int[numPoints];
        
        for (int i=0;i<numPoints;i++) {
            double angle = i * (Math.PI / ((double)numPoints / 2));
            double cosValue = Math.cos(angle);
            double sinValue = Math.sin(angle);
            double randomOffset = Util.randomRange(-3, 3);
            
            if (Math.random() < 0.1) {
                xCoords[i] = (int) Math.round(cosValue * radius - 2 + randomOffset);
                yCoords[i] = (int) Math.round(sinValue * radius - 2 + randomOffset);
            } else {
                xCoords[i] = (int) Math.round(cosValue * radius + randomOffset);
                yCoords[i] = (int) Math.round(sinValue * radius + randomOffset);
            }
        }        

        // damage crack line coords
        dxCoords = new int[4];
        dyCoords = new int[4];

        setShadowCoords(xCoords, yCoords);
        setDamageCracksCoords(xCoords, yCoords, radius);
    }

    public Polygon getPoly() {
        return new Polygon(xCoords, yCoords, numPoints);
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

    public Boolean getDamaged() {
        return damaged || strength < maxStrength / 2;
    }

    public void takeDamage(int damage) {
        strength -= damage;
        damaged = true;
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
        int[] xRot = new int[numPoints];
        int[] yRot = new int[numPoints];
    
        for (int i=0;i<numPoints;i++) {
            xRot[i] = xCoords[i];
            yRot[i] = yCoords[i];
        }
        
        Polygon p1 = new Polygon(xRot, yRot, numPoints);
        AffineTransform at = new AffineTransform();
    
        at.rotate(Math.toRadians(rotation), x, y);
        at.translate(x, y);
    
        Area a1 = new Area(at.createTransformedShape(p1));
        at.setToIdentity();
        
        return a1;
    }
    
    public Area getShadowBounds() {
        int[] xRot = new int[numShadowPoints];
        int[] yRot = new int[numShadowPoints];
    
        for (int i=0;i<numShadowPoints;i++) {
            xRot[i] = sxCoords[i];
            yRot[i] = syCoords[i];
        }
        
        Polygon p1 = new Polygon(xRot, yRot, numShadowPoints);
        AffineTransform at = new AffineTransform();
    
        at.rotate(Math.toRadians(rotation), x, y);
        at.translate(x, y);
    
        Area a1 = new Area(at.createTransformedShape(p1));
        
        return a1;
    }

    public void setShadowCoords(int[] xCoords, int[] yCoords) {
        // Initialize shadow coordinates array
        sxCoords = new int[numShadowPoints];
        syCoords = new int[numShadowPoints];
        
        // Number of points to modify for shadow effect
        int numShadowInsidePoints = numShadowPoints / 2 - 1;
        
        // Loop to populate shadow coordinates
        for (int i = 0; i < numShadowPoints; i++) {
            if (i >= numShadowPoints - numShadowInsidePoints) {
                int index = numShadowPoints - i;
                sxCoords[i] = xCoords[index];
                syCoords[i] = yCoords[index] - Util.randomRange(5, 10);
            } else {
                sxCoords[i] = xCoords[i];
                syCoords[i] = yCoords[i];
            }
        }
    }

    public void setDamageCracksCoords(int[] xCoords, int[] yCoords, int radius) {
        int maxCrackLineOffset = radius / 3;
        int crackLineOffset = Util.randomRange(maxCrackLineOffset / 2, maxCrackLineOffset);

        int[][] coords = {
            {xCoords[0], yCoords[0]},
            {crackLineOffset, crackLineOffset},
            {-crackLineOffset, -crackLineOffset},
            {xCoords[4], yCoords[4]}
        };
        
        for (int i = 0; i < 4; i++) {
            dxCoords[i] = coords[i][0];
            dyCoords[i] = coords[i][1];
        }
    }

    // creates 3 lines to draw and rotates them to match the rock's rotation
    public Shape[] getDamageCrackLine() {
        AffineTransform at = new AffineTransform();
        
        Line2D[] dLines = {
            new Line2D.Double(dxCoords[0], dyCoords[0], dxCoords[1], dyCoords[1]),
            new Line2D.Double(dxCoords[1], dyCoords[1], dxCoords[2], dyCoords[2]),
            new Line2D.Double(dxCoords[2], dyCoords[2], dxCoords[3], dyCoords[3])
        };
        
        at.rotate(Math.toRadians(rotation), x, y);
        at.translate(x, y);
        
        Shape[] damageCracks = {
            at.createTransformedShape(dLines[0]),
            at.createTransformedShape(dLines[1]),
            at.createTransformedShape(dLines[2]),
        };
                
        return damageCracks;
    }

    public void move() {
        rotation += rotationDelta;
        x = (x + (float) xSpeed);
        y = (y + (float) ySpeed);
        impactForce = (mass * (xSpeed + ySpeed)) / 5;
        setVisible(strength > 0);
    }
}
