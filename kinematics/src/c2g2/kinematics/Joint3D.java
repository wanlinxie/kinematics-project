package c2g2.kinematics;

import org.joml.Vector3d;

public abstract class Joint3D {
	/*
	 * Position of the 2D joint
	 */
	protected Vector3d position = new Vector3d(0.,0.,0.);
	
	protected boolean fixed = false;
	
	public Vector3d getPos(){
		return position;
	}

	public boolean isFixed() {
		return fixed;
	}
	
	public abstract void setParam(double p1, double p2);
	public abstract double[] getParam();
}
