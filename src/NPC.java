/**
 * Created by robert on 6/24/17.
 */
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class NPC {
    private double x;
    private double y;
    private int r;

    private double dx;
    private double dy;
    private double rad;
    private double speed;

    private int health;
    private int type;
    private int rank;

    private int turnDelay;
    private int frames = 0;
    private double angle;

    private Color color1;

    private boolean ready;
    private boolean dead;

    private PowerUp power;

    public NPC(int type, int rank){
        this.rank = rank;
        this.type = type;

        if(type ==1){
            color1 = Color.RED;
            if(rank == 1){
                speed = 2;
                r = 8;
                health = 3;
                turnDelay = 30;
            }
            if(rank == 2){
                color1 = Color.PINK.darker();
                speed = 3;
                r = 5;
                health = 2;
            }
            if(rank == 3){
                color1 = Color.ORANGE;
                speed = 4;
                r = 8;
                health = 4;
            }
            if(rank == 4){
                color1 = Color.GREEN.darker();
                speed = 8;
                r = 3;
                health = 2;
            }
        }
        x = Math.random() * GamePanel.WIDTH / 2 + GamePanel.WIDTH / 4;
        y = Math.random() * GamePanel.WIDTH / 2 + GamePanel.WIDTH / 4;

        angle = Math.random() * 140 + 20;
        rad = Math.toRadians(angle);

        dx = Math.cos(rad) * speed;
        dy = Math.sin(rad) * speed;

        ready = false;
        dead = false;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getR() {
        return r;
    }

    public int getType() {
        return type;
    }

    public int getRank() {
        return rank;
    }

    public boolean isDead() {
        return dead;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
        this.dx = Math.cos(rad) * speed;
        this.dy = Math.sin(rad) * speed;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getHealth() {
        return health;
    }

    public void hit(double d){
        health -= d;
        if(health <= 0){
            dead = true;
        }
    }

    public void update(){
        if (frames == turnDelay){
            frames = 0;
            angle += 80;
            rad = Math.toRadians(angle);
            dx = Math.cos(rad) * speed;
            dy = Math.sin(rad) * speed;
        }

        x += dx;
        y += dy;
        frames++;

        if(!ready){
            if(x > r && x < GamePanel.WIDTH - r && y > r && y < GamePanel.HEIGHT - r ){
                ready = true;
            }
        }

        if(x > GamePanel.WIDTH - r && dx > 0) dx = -dx;
        if(y > GamePanel.HEIGHT - r && dy > 0) dy = -dy;
        if(x < r && dx < 0) dx = -dx;
        if(y < r && dy < 0) dy = -dy;
    }

    public void draw(Graphics2D g){
        //g.setColor(color1);
        //g.fillRect((int) (x-r), (int) (y-r), 2*r, 2*r);
        //g.setStroke(new BasicStroke(3));
        //g.setColor(color1.darker());
        //g.drawRect((int) (x-r), (int) (y-r), 2*r, 2*r);
        //g.setStroke(new BasicStroke(1));

        BufferedImage img = null;
        try {
            img = ImageIO.read(new File("/home/robert/Documents/wasteland/src/res/npc.png"));
        } catch (IOException e) {System.out.println("IO Error");}
        g.drawImage(img, (int)x-r, (int)y-r, null);
    }
}
