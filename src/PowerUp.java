/**
 * Created by robert on 7/8/17.
 */
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PowerUp {
    private double x;
    private double y;
    private int r;
    private int lifeFrames = 200;
    private Color color1;

    private int type;

    public PowerUp(int type, double x, double y){
        this.type = type;
        this.x = x;
        this.y = y;
        this.r = 20;
        color1 = new Color(150,70,150);
    }

    public boolean update(){
        lifeFrames--;
        if(lifeFrames <= 0){
            return true;
        }
        return false;
    }

    public void draw(Graphics2D g){
        //g.setColor(color1);
        //g.fillRect((int) (x-r), (int) (y-r), 2*r, 2*r);
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File("/home/robert/Documents/wasteland/src/res/power_red2.png"));
        } catch (IOException e) {System.out.println("IO Error");}
        g.drawImage(img, (int)x-r, (int)y-r, null);
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
}
