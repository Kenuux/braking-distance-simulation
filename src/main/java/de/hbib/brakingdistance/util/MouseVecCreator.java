package de.hbib.brakingdistance.util;

import de.hbib.brakingdistance.api.MouseState;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * @author Oualid Hbib
 */
public class MouseVecCreator implements MouseState {

    private Vector start, end, vector;
    private boolean hasVec, isCreatingVec = false;
    public Color vecColor = Color.yellow;

    /**
     * Sets the starting point of a Vec to create.
     */
    @Override
	public void mousePressAction(final MouseEvent e) {
		this.hasVec = false;
		this.start = new Vector(e.getX(), e.getY());
		this.end = new Vector(e.getX(), e.getY());
		this.vector = this.end.minus(this.start);
    }

    /**
     * Dynamically updates a Vec under creation.
     */
    @Override
	public void mouseDragAction(final MouseEvent e) {
		this.end.x = e.getX();
		this.end.y = e.getY();
		this.vector = this.end.minus(this.start);
		this.isCreatingVec = true;
    }

    /**
     * Creates a vec and stores it as an available Vec.
     */
    @Override
	public void mouseReleaseAction(final MouseEvent e) {
		this.hasVec = true;
		this.isCreatingVec = false;
    }

    /**
     * @return Whether a Vec is available or not.
     */
    public boolean hasVec() {
        return this.hasVec;
    }

    /**
     * @return The created Vec, if available, null otherwise.
     */
    public Vector getVec() {
        if (this.hasVec) {
			this.hasVec = false;
            return new Vector(this.vector);
        } else {
            return null;
        }
    }

    /**
     * @return The origin of the created Vec
     */
    public Vector getOrigin() {
        return new Vector(this.start);
    }

    /**
     * Draws the Vec if it is being created, from the mouse press position
     */
    @Override
	public void drawState(final Graphics g) {
        if (this.isCreatingVec) this.vector.draw(g, this.start, this.vecColor);
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
