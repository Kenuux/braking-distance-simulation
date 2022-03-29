package de.hbib.brakingdistance.api;

import de.hbib.brakingdistance.handler.GuiHandler;
import de.hbib.brakingdistance.util.Vec;
import lombok.Getter;
import lombok.Setter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author Oualid Hbib
 */
public class Car {

    @Getter private CarType carType;
    @Getter private Vec pos;
    @Getter private Vec vel;
    @Getter private double radius;

    private BufferedImage bufferedImage;
    @Setter private boolean brake = false;
    private int highestSpeed = 0;

    public Car(CarType carType, Vec pos, Vec vel, double radius) {
        this.carType = carType;
        this.pos = pos;
        this.vel = vel;
        this.radius = radius;

        try {
            this.bufferedImage = ImageIO.read(getClass().getClassLoader().getResourceAsStream("car" + (carType.ordinal() + 1) + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics graphics) {
        double angle = Math.atan2(this.vel.y, this.vel.x);
        final double rads = Math.toRadians(Math.toDegrees(angle));
        final double sin = Math.abs(Math.sin(rads));
        final double cos = Math.abs(Math.cos(rads));
        final int w = (int) Math.floor(this.bufferedImage.getWidth() * cos + this.bufferedImage.getHeight() * sin);
        final int h = (int) Math.floor(this.bufferedImage.getHeight() * cos + this.bufferedImage.getWidth() * sin);
        final BufferedImage rotatedImage = new BufferedImage(w, h, this.bufferedImage.getType());
        final AffineTransform at = new AffineTransform();
        at.translate(w / 2, h / 2);
        at.rotate(rads,0, 0);
        at.translate(-this.bufferedImage.getWidth() / 2, -bufferedImage.getHeight() / 2);
        final AffineTransformOp rotateOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        rotateOp.filter(bufferedImage,rotatedImage);

        graphics.drawImage(rotatedImage, (int) (pos.x - radius), (int) (pos.y - radius), (img, infoflags, x, y, width, height) -> false);

        graphics.setColor(Color.white);

        String s = Math.round(highestSpeed) + " km/h";

        if(brake) {
            double bremsweg = (highestSpeed / 10.0) * (highestSpeed / 10.0);
            s = (int) bremsweg + "m Bremsweg bei " + highestSpeed + " km/h";
        }
        graphics.setFont(new Font(Font.DIALOG, Font.BOLD, 12));
        graphics.drawString(s, (int) (pos.x - this.radius) + this.bufferedImage.getWidth() / 4, (int) (pos.y - this.radius) - 15);
    }

    public void update() {
        double speed = Math.sqrt(vel.y * vel.y + vel.x * vel.x);

        if(highestSpeed < speed)
            highestSpeed = (int) (speed * GuiHandler.getInstance().getSpeedIntensitySlider().getValue());

        if(brake) {
            double percent = 0.025 * GuiHandler.getInstance().getBrakeSlider().getValue();
            percent = percent > 1 ? 1 : percent;
            percent = percent < 0 ? 0 : percent;
            double d = vel.x * percent;
            double y = vel.y * percent;
            vel.subtract(d, y);
        }

        this.pos.add(this.vel);
    }
}
