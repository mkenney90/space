package com.zephyr.enemies;

import java.awt.*;
import java.io.IOException;

import javax.imageio.ImageIO;

public abstract class BaseEnemy {
    protected int x, y; // Position of the enemy
    protected int width, height; // Size of the enemy
    protected int speed; // Speed of the enemy movement
    protected int health; // Health of the enemy
    protected Color color; // Color of the enemy
    protected boolean visible; // Visibility of the enemy
    protected Rectangle bounds; // Bounds for collision detection
    protected Image image;
    protected String imageFile;

    // Constructor
    public BaseEnemy(int x, int y, int width, int height, int speed, int health, Color color, String imageFile) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.health = health;
        this.color = color;
        this.visible = true;
        this.bounds = new Rectangle(x, y, width, height);
        this.imageFile = imageFile;

        System.out.println(imageFile);

        loadImage();
    }

    protected void loadImage() {
        if (imageFile == null) return;
        try {
            image = ImageIO.read(getClass().getResourceAsStream(imageFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    protected void getImageDimensions() {
        width = image.getWidth(null);
        height = image.getHeight(null);
    }
    
    public Image getImage() {
        return image;
    }

    // Abstract method for enemy-specific behavior
    public abstract void update();

    // Render the enemy
    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        if (getImage() != null) {
            g2d.drawImage(image, x, y, null);
        } else {
            g2d.setColor(color);
            g2d.fillRect(x, y, width, height);
            g2d.setColor(Color.black);
            g2d.drawRect(x, y, width, height);
        }
    }

    // Move the enemy
    public void move() {
        x += speed;
        bounds.setLocation(x, y);
    }

    // Handle collision with another object (e.g., lasers)
    public boolean checkCollision(Rectangle other) {
        return bounds.intersects(other);
    }

    // Reduce health when taking damage
    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            visible = false;
        }
    }

    // Getter and Setter methods
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
        this.bounds.setLocation(x, y);
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
        this.bounds.setLocation(x, y);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
