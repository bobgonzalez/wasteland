/**
 * Created by robert on 6/24/17.
 */
import java.awt.*;

public class Enemy {
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

    private Color color1;

    private boolean ready;
    private boolean dead;

    public Enemy(int type, int rank){
        this.rank = rank;
        this.type = type;

        if(type ==1){
            color1 = Color.BLUE;
            if(rank == 1){
                speed = 2;
                r = 5;
                health = 1;
            }
            if(rank == 2){
                color1 = Color.PINK;
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
                color1 = Color.BLACK;
                speed = 8;
                r = 3;
                health = 2;
            }
        }
        x = Math.random() * GamePanel.WIDTH / 2 + GamePanel.WIDTH / 4;
        y = -r;

        double angle = Math.random() * 140 + 20;
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

    public void hit(){
        health--;
        if(health <= 0){
            dead = true;
        }
    }

    public void update(){
        x += dx;
        y += dy;

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
        g.setColor(color1);
        g.fillOval((int) (x-r), (int) (y-r), 2*r, 2*r);
        g.setStroke(new BasicStroke(3));
        g.setColor(color1.darker());
        g.drawOval((int) (x-r), (int) (y-r), 2*r, 2*r);
        g.setStroke(new BasicStroke(1));
    }
}
