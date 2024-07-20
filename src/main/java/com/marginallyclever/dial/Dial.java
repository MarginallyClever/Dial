package com.marginallyclever.dial;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.*;

/**
 * A dial that can be turned with the mouse wheel, mouse click+drag, or the keyboard +/- keys.
 * Attach an {@link ActionListener} to receive the "turn" command when the dial is turned.
 */
public class Dial extends JComponent {
	private double value=0;
	private double change=0;

	private boolean dragging=false;
	private int dragPreviousX,dragPreviousY;

	private final EventListenerList listeners = new EventListenerList();

	public Dial() {
		super();

		Dimension d = new Dimension(50,50);
		setMinimumSize(d);
		setPreferredSize(d);
		
		setRequestFocusEnabled(true);

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				switch (e.getKeyChar()) {
					case '+' -> onChange(1);
					case '-' -> onChange(-1);
					default -> {}
				}
			}
		});

		addMouseWheelListener( (e) -> onChange(-e.getWheelRotation()) );

		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				dragging=true;
				setPrevious(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				dragging=false;
			}
		});

		addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				super.mouseDragged(e);
				if(!dragging) return;

				int cx = getWidth()/2;
				int cy = getHeight()/2;

				// find the current mouse position relative to the center of the dial.
				float dx = e.getX()-cx;
				float dy = e.getY()-cy;
				float length = (float)Math.sqrt(dx*dx+dy*dy);
				if(length!=0) {
					dx/=length;
					dy/=length;
				}

				// find the previous mouse position relative to the center of the dial.
				float px = dragPreviousX-cx;
				float py = dragPreviousY-cy;
				float plength = (float)Math.sqrt(px*px+py*py);
				if(plength!=0) {
					px/=plength;
					py/=plength;
				}

				// find the orthogonal vector to the previous vector
				float ox = -py;
				float oy = px;

				// dot product of dx/dy and ox/oy is the change in angle.
				double y = ox*dx + oy*dy;
				double x = px*dx + py*dy;
				double change = Math.toDegrees(Math.atan2(y,x));
				if(change!=0) onChange(change);
				// remember the mouse moved.
				setPrevious(e);
			}
		});
	}

	private void onChange(double amount) {
		setChange(amount);
		fireActionEvent(new ActionEvent(this,ActionEvent.ACTION_PERFORMED,"turn"));
	}

	private void setPrevious(MouseEvent e) {
		dragPreviousX = e.getX();
		dragPreviousY = e.getY();
	}
	
	public double getChange() {
		return change;
	}

	/**
	 * Set the change value.  The change value is the amount the dial moved on the last update.
	 * @param change the change value
	 */
	public void setChange(double change) {
		setValue(value+change);
	}

	/**
	 * Returns the current value of the dial.
	 * @return the current value of the dial, a value between 0 (inclusive) and 360 (exclusive).
	 */
	public double getValue() {
		return value;
	}

	/**
	 * Set the value of the dial.  The value is unconstrained.
	 * @param arg0 the new value
	 */
	public void setValue(double arg0) {
		this.change = arg0 - this.value;
		this.value = arg0;
		repaint();
	}

	/**
	 * Subscribe to receivei the "turn" command when the dial is turned.
	 * @param listener the listener
	 */
	public void addActionListener(ActionListener listener) {
		listeners.add(ActionListener.class,listener);
	}
	
	public void removeActionListener(ActionListener listener) {
		listeners.remove(ActionListener.class,listener);
	}
	
	private void fireActionEvent(ActionEvent ae) {
		for( ActionListener listener : listeners.getListeners(ActionListener.class) ) {
			listener.actionPerformed(ae);
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Rectangle rect = this.getBounds();
		
		g.translate(rect.width/2, rect.height/2);
		int radius = Math.min(rect.width, rect.height) /2;

		Graphics2D g2 = (Graphics2D)g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		drawEdge(g2,radius);
		drawTurnIndicator(g2,radius);
		drawLabels(g2,radius);
	}
	
	private void drawLabels(Graphics2D g2, int radius) {
		int inset = 4;
		int v = radius/5;
		int x = inset-radius;
		int y = -radius+v/2 + inset; 
		// -
		g2.drawLine(x,y,x+v,y);
		// +
		x = radius-inset;
		g2.drawLine(x-v,y,x,y);
		g2.drawLine(x-v/2, -radius + inset,x-v/2,-radius+v+inset);
	}

	private void drawTurnIndicator(Graphics2D g2, int radius) {
		radius-=6;
		double radians = Math.toRadians(value);
		int x=(int)Math.round(Math.cos(radians)*radius);
		int y=(int)Math.round(Math.sin(radians)*radius);
		
		g2.setColor(Color.GRAY);
		g2.drawLine(0,-2,x,y-2);
	}

	private void drawEdge(Graphics2D g2,int r) {
		r-=3;
		int x=0;
		int y=-2;
		g2.setStroke(new BasicStroke(2));
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		// outline
		g2.setColor(Color.GRAY);
		g2.drawArc(x-r, y-r, x+r*2, y+r*2, 180+45, 180);
		g2.setColor(Color.GRAY);
		g2.drawArc(x-r, y-r, x+r*2, y+r*2, 45, 180);
		// raised
		r-=1;
		g2.setColor(Color.DARK_GRAY);
		g2.drawArc(x-r, y-r, x+r*2, y+r*2, 180+45, 180);
		g2.setColor(Color.LIGHT_GRAY);
		g2.drawArc(x-r, y-r, x+r*2, y+r*2, 45, 180);
		g2.setStroke(new BasicStroke(1));
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(200,200);
		frame.add(new Dial());
		frame.setVisible(true);
	}
}
