package c2g2.kinematics;

public class LinkConnection3D {

	/*
	 * When the two links look like what follows (form into a straight line)
	 * ------(o)------
	 * Here the left link is the parent link, and the right link is the child link
	 * The joint rotateAngle is zero
	 */
	private RigidLink3D parent = null;
	
	private RigidLink3D child = null;
	
	private RevoluteJoint3D joint = null;
	
	public RevoluteJoint3D getJoint() {
		return joint;
	}
	
	public RigidLink3D getParent() {
		return parent;
	}
	
	public RigidLink3D getChild(){
		return child;
	}
	
	public boolean isEnd() {
		return child == null;
	}
	
	public void setParent(RigidLink3D p){
		parent = p;
	}
	
	public void setChild(RigidLink3D c){
		child = c;
	}
	
	public void setJoint(RevoluteJoint3D j){
		joint = j;
	}
	
}
