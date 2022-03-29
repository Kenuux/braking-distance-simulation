package de.hbib.brakingdistance.util;

import java.awt.*;

/**
 * @author Oualid Hbib
 */
public class Vector {

    public double x, y, z;

    public Vector() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
    }

    public Vector(final double xx, final double yy, final double zz) {
		this.x = xx;
		this.y = yy;
		this.z = zz;
    }

    public Vector(final double xx, final double yy) {
		this.x = xx;
		this.y = yy;
		this.z = 0;
    }

    public Vector(final Vector v) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
    }

    public Vector set(final Vector v) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
        return this;
    }

    public Vector add(final Vector v) {
		this.x += v.x;
		this.y += v.y;
		this.z += v.z;
        return this;
    }

    public Vector add(final double xx, final double yy, final double zz) {
		this.x += xx;
		this.y += yy;
		this.z += zz;
        return this;
    }

    public Vector add(final double xx, final double yy) {
		this.x += xx;
		this.y += yy;
        return this;
    }

    public Vector plus(final Vector v) {
        return new Vector(this.x + v.x, this.y + v.y, this.z + v.z);
    }

    public Vector plus(final double xx, final double yy) {
        return new Vector(this.x + xx, this.y + yy, this.z);
    }

    public Vector subtract(final Vector v) {
		this.x -= v.x;
		this.y -= v.y;
		this.z -= v.z;
        return this;
    }

    public Vector subtract(final double xx, final double yy) {
		this.x -= xx;
		this.y -= yy;
        return this;
    }

    public Vector minus(final Vector v) {
        return new Vector(this.x - v.x, this.y - v.y, this.z - v.z);
    }

    public Vector minus(final double xx, final double yy) {
        return new Vector(this.x - xx, this.y - yy, this.z);
    }

    public double dot(final Vector v) {
        return this.x * v.x + this.y * v.y + this.z * v.z;
    }

    public Vector scale(final double f) {
		this.x *= f;
		this.y *= f;
		this.z *= f;
        return this;
    }

    public Vector scaleV(final double f) {
        return new Vector(this.x * f, this.y * f, this.z * f);
    }

    public double mag() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public Vector cross(final Vector v) {
        return new Vector(this.y * v.z - this.z * v.y,
				this.z * v.x - this.x * v.z,
				this.x * v.y - this.y * v.x);
    }

    public void draw(final Graphics g, final Vector origin, final Color c) {
        final int mag = (int) this.mag();
        final Graphics2D g2 = (Graphics2D) g;
        g2.setColor(c);

        //transform
        g2.translate(origin.x, origin.y);
        g2.rotate(Math.atan2(this.y, this.x));

        //draw vector
        g2.drawLine(0, 0, mag, 0);
        final int[] xs = {mag - 10, mag - 10, mag};
        final int[] ys = {5, -5, 0};
        g2.fillPolygon(xs, ys, 3);

        //undo transform
        g2.rotate(-Math.atan2(this.y, this.x));
        g2.translate(-origin.x, -origin.y);
    }

    @Override
	public String toString() {
        return "(" + this.x + "," + this.y + "," + this.z + ")";
    }
}
