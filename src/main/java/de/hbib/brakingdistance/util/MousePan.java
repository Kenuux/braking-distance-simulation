package de.hbib.brakingdistance.util;

import de.hbib.brakingdistance.api.MouseState;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * @author Oualid Hbib
 */
public class MousePan implements MouseState {

    private Vector start;
	private Vector end;
	private final Vector oldPan;
	private Vector pan;

    public MousePan() {
		this.oldPan = new Vector();
		this.pan = new Vector();
    }

    /**
     * Sets the start of the pan addition to the press point.
     */
    @Override
	public void mousePressAction(final MouseEvent e) {
		this.oldPan.x = this.pan.x;
		this.oldPan.y = this.pan.y;
		this.start = new Vector(e.getX(), e.getY());
		this.end = new Vector(this.start.x, this.start.y);
    }

    /**
     * Changes the pan Vec while dragging to (position of mouse)-(start) added to the oldPan Vec.
     */
    @Override
	public void mouseDragAction(final MouseEvent e) {
		this.end.x = e.getX();
		this.end.y = e.getY();
        final Vector p = this.oldPan.plus(this.end.minus(this.start));
		this.pan.x = p.x;
		this.pan.y = p.y;
    }

    /**
     * Nothing is done on mouse release.
     */
    @Override
	public void mouseReleaseAction(final MouseEvent e) {

    }

    /**
     * Set the Vec object which will be treated as the pan Vec.
     *
     * @param p - the Vec object to treat as pan
     */
    public void setPanVec(final Vector p) {
		this.pan = p;
    }

    @Override
	public void drawState(final Graphics g) {
    }

    @Override
	public void mouseClickAction(final MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
	public void mouseMoveAction(final MouseEvent e) {
        // TODO Auto-generated method stub

    }
}
