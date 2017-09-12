package c2g2.kinematics;

import java.util.ArrayList;

/*
 * The class that represents a rigid link or a robotic arm.
 * 
 * Here we assume the links are connected into a tree structure. So
 * each link has one parent, and multiple children
 */
public class RigidLink2D {
	/*
	 * physical length of the link
	 */
	private double length = 0.1;
	
	/*
	 * The connection to the parent link 
	 * If the link is a root link, its parent LinkConnection2D must 
	 * have a fixed joint. See isRoot() method
	 */
	public RigidLink2D() {
		// TODO Auto-generated constructor stub
		children = new ArrayList<LinkConnection2D>();
	}
	
	private LinkConnection2D parent = null;
	
	private ArrayList<LinkConnection2D> children = null;
	
	public int childsize(){
		return children.size();
	}
	
	public boolean isRoot() {
		return parent.getParent() == null;
	}
	
	public void setLength(double len){
		length = len;
	}
	
	public double getLength(){
		return length;
	}
	
	public void setParent(LinkConnection2D p){
		parent = p;
	}
	
	public LinkConnection2D getParent(){
		return parent;
	}
	
	public void addChild(LinkConnection2D c){
		children.add(c);
	}
	
	public LinkConnection2D getChild(int i){
		return children.get(i);
	}
	
	public Joint2D getParentJoint(){
		return parent.getJoint();
	}
	
	public Joint2D getChildJoint() {
		if (children.size()==0) {
			System.err.println("no child joint");
			return null;
		}
		return children.get(0).getJoint();
	}
	
	
}
