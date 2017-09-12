package c2g2.kinematics;

import org.joml.Vector2d;

public class RevoluteJoint2D extends Joint2D {

	/*
	 * When the two links look like what follows (form into a straight line)
	 * -------o-------
	 * The joint rotateAngle is zero
	 */
	public RevoluteJoint2D(Vector2d pos, double p) {
		position = pos;
		setParam(p);
	}
	
	private	double rotateAngle = 0.;
	
	public double getRotationAngle() {
		return rotateAngle;
	}
	
	public void setRotationAngle(double p) {
		rotateAngle = p;
	}
	
	/*
	 * Implement the method from the Joint2D class
	 * @see c2g2.kinematics.Joint2D#setParam(double)
	 */
	public void setParam(double p) {
		rotateAngle = p;
	}
	public double getParam(){
		return rotateAngle;
	}
}
