package de.hbib.brakingdistance.api;

import de.hbib.brakingdistance.handler.GuiHandler;
import de.hbib.brakingdistance.util.Vector;
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

    @Getter private final CarType carType;
    @Getter private final Vector pos;
    @Getter private final Vector vel;
    @Getter private final double radius;

    private BufferedImage bufferedImage;
    @Setter private boolean brake = false;
    private int highestSpeed = 0;

    public Car(final CarType carType, final Vector pos, final Vector vel, final double radius) {
        this.carType = carType;
        this.pos = pos;
        this.vel = vel;
        this.radius = radius;

        try {
            this.bufferedImage = ImageIO.read(this.getClass().getClassLoader().getResourceAsStream(carType.getFileName()));
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public void draw(final Graphics graphics) {
        final double angle = Math.atan2(this.vel.y, this.vel.x);
        final double rads = Math.toRadians(Math.toDegrees(angle));
        final double sin = Math.abs(Math.sin(rads));
        final double cos = Math.abs(Math.cos(rads));
        final int w = (int) Math.floor(this.bufferedImage.getWidth() * cos + this.bufferedImage.getHeight() * sin);
        final int h = (int) Math.floor(this.bufferedImage.getHeight() * cos + this.bufferedImage.getWidth() * sin);
        final BufferedImage rotatedImage = new BufferedImage(w, h, this.bufferedImage.getType());
        final AffineTransform at = new AffineTransform();
        at.translate(w / 2, h / 2);
        at.rotate(rads, 0, 0);
        at.translate(-this.bufferedImage.getWidth() / 2, -this.bufferedImage.getHeight() / 2);
        final AffineTransformOp rotateOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        rotateOp.filter(this.bufferedImage, rotatedImage);

        graphics.drawImage(rotatedImage, (int) (this.pos.x - this.radius), (int) (this.pos.y - this.radius), (img, infoflags, x, y, width, height) -> false);

        graphics.setColor(Color.white);

        String text = Math.round(this.highestSpeed) + " km/h";

        if (this.brake) {
            final double bremsweg = (this.highestSpeed / 10.0) * (this.highestSpeed / 10.0);
            text = (int) bremsweg + "m Bremsweg bei " + this.highestSpeed + " km/h";
        }

        graphics.setFont(new Font(Font.DIALOG, Font.BOLD, 12));
        graphics.drawString(text, (int) (this.pos.x - this.radius) + this.bufferedImage.getWidth() / 4, (int) (this.pos.y - this.radius) - 15);
    }

    public void update() {
        final double speed = Math.sqrt(this.vel.y * this.vel.y + this.vel.x * this.vel.x);

        if (this.highestSpeed < speed)
            this.highestSpeed = (int) (speed * GuiHandler.getInstance().getSpeedIntensitySlider().getValue());

        if (this.brake) {
            double percent = 0.025 * GuiHandler.getInstance().getBrakeSlider().getValue();
            percent = percent > 1 ? 1 : percent;
            percent = percent < 0 ? 0 : percent;
            final double d = this.vel.x * percent;
            final double y = this.vel.y * percent;
            this.vel.subtract(d, y);
        }

        this.pos.add(this.vel);
    }
}
