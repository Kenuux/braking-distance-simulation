package de.hbib.brakingdistance.handler;

import de.hbib.brakingdistance.api.MouseState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * @author Oualid Hbib
 */
public class MouseStateHandler {
	
	@SuppressWarnings("unused")
	private static final String VERSION = "1.2";
	
	private MouseState leftButtonState, rightButtonState, allButtonState;
	
	public MouseState getLeftState() {
		return leftButtonState;
	}
	/**
	 * @param s - the MouseState to set as leftButtonState
	 */
	public void setLeftState(MouseState s) {
		leftButtonState = s;
	}
	
	public MouseState getRightState() {
		return rightButtonState;
	}
	/**
	 * @param s - the MouseState to set as leftButtonState
	 */
	public void setRightState(MouseState s) {
		rightButtonState = s;
	}
	
	public MouseState getAllState() {
		return allButtonState;
	}
	/**
	 * @param s - the MouseState to set as allButtonState, the state which acts for any mouse button used
	 */
	public void setAllState(MouseState s) {
		allButtonState = s;
	}
	
	
	
	/**
	 * Perform the press action of either the left or right
	 * state, based on which mouse button was used
	 * @param e - MouseEvent of press
	 */
	public void pressAction(MouseEvent e){
		if(allButtonState!=null)allButtonState.mousePressAction(e);
		if(SwingUtilities.isRightMouseButton(e)){
			rightButtonState.mousePressAction(e);
		}else if(SwingUtilities.isLeftMouseButton(e)){
			leftButtonState.mousePressAction(e);
		}
	}
	
	/**
	 * Perform the drag action of either the left or right
	 * state, based on which mouse button was used
	 * @param e - MouseEvent of drag
	 */
	public void dragAction(MouseEvent e){
		if(allButtonState!=null)allButtonState.mouseDragAction(e);
		if(SwingUtilities.isRightMouseButton(e)){
			rightButtonState.mouseDragAction(e);
		}else if(SwingUtilities.isLeftMouseButton(e)){
			leftButtonState.mouseDragAction(e);
		}
	}
	
	/**
	 * Perform the release action of either the left or right
	 * state, based on which mouse button was used
	 * @param e - MouseEvent of release
	 */
	public void releaseAction(MouseEvent e){
		if(allButtonState!=null)allButtonState.mouseReleaseAction(e);
		if(SwingUtilities.isRightMouseButton(e)){
			rightButtonState.mouseReleaseAction(e);
		}else if(SwingUtilities.isLeftMouseButton(e)){
			leftButtonState.mouseReleaseAction(e);
		}
	}
	
	/**
	 * Perform the click action of either the left or right
	 * state, based on which mouse button was used
	 * @param e - MouseEvent of click
	 */
	public void clickAction(MouseEvent e) {
		if(allButtonState!=null)allButtonState.mouseClickAction(e);
		if(SwingUtilities.isRightMouseButton(e)){
			rightButtonState.mouseClickAction(e);
		}else if(SwingUtilities.isLeftMouseButton(e)){
			leftButtonState.mouseClickAction(e);
		}	
	}
	
	/**
	 * Perform the move action of both the left and right
	 * state
	 * @param e - MouseEvent of move
	 */
	public void moveAction(MouseEvent e) {
		if(allButtonState!=null)allButtonState.mouseMoveAction(e);
		rightButtonState.mouseMoveAction(e);
		leftButtonState.mouseMoveAction(e);	
	}
	
	/**
	 * Calls the drawState function of both the left and right 
	 * mouse button states of this handler.
	 * @param g - Graphics object to pass into drawState
	 */
	public void drawStates(Graphics g){
		if(allButtonState!=null)allButtonState.drawState(g);
		rightButtonState.drawState(g);
		leftButtonState.drawState(g);
	}

	
}
