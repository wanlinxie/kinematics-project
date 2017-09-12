package c2g2.kinematics;

import org.joml.Vector3d;

public class RevoluteJoint3D extends Joint3D {

	/*
	 * When the two links look like what follows (form into a straight line)
	 * -------o-------
	 * The joint rotateAngle is zero
	 */
	public RevoluteJoint3D(Vector3d pos, double p1, double p2) {
		position = pos;
		setParam(p1, p2);
	}
	
	private	double rotateAngle1 = 0.; //theta
	private double rotateAngle2 = 0.; //phi
	

	public void setParam(double p1, double p2) { 
		rotateAngle1 = p1;
		rotateAngle2 = p2;
	}
	
	public double[] getParam(){
		double[] params = {rotateAngle1, rotateAngle2};
		return params;
	}

}
