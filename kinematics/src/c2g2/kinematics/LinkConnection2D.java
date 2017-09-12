package c2g2.kinematics;

public class LinkConnection2D {

	/*
	 * When the two links look like what follows (form into a straight line)
	 * ------(o)------
	 * Here the left link is the parent link, and the right link is the child link
	 * The joint rotateAngle is zero
	 */
	private RigidLink2D parent = null;
	
	private RigidLink2D child = null;
	
	private Joint2D joint = null;
	
	public Joint2D getJoint() {
		return joint;
	}
	
	public RigidLink2D getParent() {
		return parent;
	}
	
	public RigidLink2D getChild(){
		return child;
	}
	
	public boolean isEnd() {
		return child == null;
	}
	
	public void setParent(RigidLink2D p){
		parent = p;
	}
	
	public void setChild(RigidLink2D c){
		child = c;
	}
	
	public void setJoint(Joint2D j){
		joint = j;
	}
	
}
