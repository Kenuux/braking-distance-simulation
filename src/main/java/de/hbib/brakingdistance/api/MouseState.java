package de.hbib.brakingdistance.api;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * @author Oualid Hbib
 */
public interface MouseState {

    public static final MouseState doNothing = new MouseState() {

        @Override
		public void mousePressAction(final MouseEvent e) {
        }

        @Override
		public void mouseDragAction(final MouseEvent e) {
        }

        @Override
		public void mouseReleaseAction(final MouseEvent e) {
        }

        @Override
		public void mouseClickAction(final MouseEvent e) {
        }

        @Override
		public void mouseMoveAction(final MouseEvent e) {
        }

        @Override
		public void drawState(final Graphics g) {
        }

    };

    /**
     * Perform the press action of this MouseState
     *
     * @param e - MouseEvent of press
     */
    public void mousePressAction(MouseEvent e);

    /**
     * Perform the drag action of this MouseState
     *
     * @param e - MouseEvent of drag
     */
    public void mouseDragAction(MouseEvent e);

    /**
     * Perform the release action of this MouseState
     *
     * @param e - MouseEvent of release
     */
    public void mouseReleaseAction(MouseEvent e);

    /**
     * Perform the click action of this MouseState
     *
     * @param e - MouseEvent of click
     */
    public void mouseClickAction(MouseEvent e);

    /**
     * Perform the move action of this MouseState
     *
     * @param e - MouseEvent of move
     */
    public void mouseMoveAction(MouseEvent e);

    /**
     * Draw something based on the MouseState
     *
     * @param g - Graphics to draw the state with.
     */
    public void drawState(Graphics g);


}
