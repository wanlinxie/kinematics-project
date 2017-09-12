package c2g2.kinematics;

import org.joml.Vector2d;

public abstract class Joint2D {
	/*
	 * Position of the 2D joint
	 */
	protected Vector2d position = new Vector2d(0.,0.);
	
	protected boolean fixed = false;
	
	public Vector2d getPos(){
		return position;
	}

	public boolean isFixed() {
		return fixed;
	}
	
	public abstract void setParam(double p);
	public abstract double getParam();
}
