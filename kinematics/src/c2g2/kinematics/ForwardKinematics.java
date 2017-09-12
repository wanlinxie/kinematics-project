package c2g2.kinematics;

import org.joml.Matrix3d;
import org.joml.Matrix4d;
import org.joml.Vector2d;
import org.joml.Vector3f;
import org.joml.Vector3d;
import org.joml.Quaterniond;
import org.joml.AxisAngle4d;

/*!
 * Class that implements the Forward kinematics algorithm
 */
public class ForwardKinematics {
	
	private Skeleton2D skeleton2d = null;
	private Skeleton3D skeleton3d = null;
	
	public ForwardKinematics(Skeleton2D ske) {
		if ( ske == null ) throw new NullPointerException("The provided skeleton is NULL");

		skeleton2d = ske;
		updatePositions2D(ske);
	}
	public ForwardKinematics(Skeleton3D ske) {
		if ( ske == null ) throw new NullPointerException("The provided skeleton is NULL");

		skeleton3d = ske;
		updatePositions3D(ske);
	}
	
	public void updatePositions2D(Skeleton2D ske){
		RigidLink2D r = ske.getRoot();
		updatePositions2D(r, new Vector2d(0.,1.));
		return;
	}
	
	private void updatePositions2D(RigidLink2D RL, Vector2d PD){ //// PD : parent stick direction
		Vector2d v1 = new Vector2d(RL.getParentJoint().getPos());
		Vector2d v2 = new Vector2d();
		//Vector2d v3 = RL.getChildJoint().getPos();
		double angle = RL.getParentJoint().getParam();
		double cosA = Math.cos(angle);
		double sinA = Math.sin(angle);
		Matrix3d m = new Matrix3d(cosA,-sinA,v1.x(),sinA,cosA,v1.y(),0,0,1);
		Vector3d s1 = new Vector3d(PD.normalize().mul(RL.getLength()),1.0);
//		System.out.println("parent joint:"+v1.x+" ,"+v1.y+"   angle: "+angle+"    length: "+RL.getLength());
//		System.out.println("child joint before:"+v3.x+" ,"+v3.y);
//		System.out.println("s1 before rotation:"+s1.x+" ,"+s1.y);
//		System.out.println("RL distance: " + RL.getLength());
		s1.mul(m.transpose());
//		System.out.println("s1 after rotation:"+s1.x+" ,"+s1.y);
		v2.set(s1.x(),s1.y());
		
		for (int i =0 ; i< RL.childsize();i++){
			RL.getChild(i).getJoint().position = v2;
		}
		Vector2d orientation = v1.sub(v2).normalize();
		
//		System.out.println("child joint  after:"+v2.x+" ,"+v2.y);
		for(int i=0; i<RL.childsize(); i++){
			if(RL.getChild(i).isEnd()){
				return;
			}
			updatePositions2D(RL.getChild(i).getChild(), orientation);
		}
	}
	
	
	//3D
	public void updatePositions3D(Skeleton3D ske){
		RigidLink3D r = ske.getRoot();
		updatePositions3D(r, new Vector3d(0.,0.,1.), new Vector3d(0.,1.,0.));  // y is the root arm refer, z is the phi 0
		return;
	}
	/// PD is the parent axis direction
	private void updatePositions3D(RigidLink3D RL, Vector3d PD, Vector3d ref){ 
		Vector3d v1 = new Vector3d(RL.getParentJoint().getPos());
		Vector3d v2 = new Vector3d();
		//Vector2d v3 = RL.getChildJoint().getPos();
		double[] angles = RL.getParentJoint().getParam();
		double cosA = Math.cos(angles[0]);
		double sinA = Math.sin(angles[0]);
		
		double cosB = Math.cos(angles[1]);
		double sinB = Math.sin(angles[1]);
		//// rotate from parent arm with certain angle Theta
		// find the rotation axis = parent arm cross 0 phi angle direction.
		Vector3d axis1=new Vector3d(PD);
		System.out.println(PD);
		System.out.println(ref);
		axis1.cross(ref);
		axis1.normalize();
		Vector3d tmp = new Vector3d(PD);
		tmp.normalize().mul(RL.getLength());
		Vector3d pt0 = new Vector3d((float)tmp.x(),(float)tmp.y(),(float)tmp.z());
		Vector3d pt1 = new Vector3d();
		Vector3d pt2 = new Vector3d();
		Vector3d ref1 = new Vector3d();
		Vector3d ref2 = new Vector3d();
		//Vector3f test = new Vector3f(0,0,0);
		//Vector3f test2 = new Vector3f(0.2f,0.3f,0.1f);
		Quaterniond quat0 = new Quaterniond(new AxisAngle4d(angles[1],axis1.x(), axis1.y(),axis1.z()));
		// rotate pt0 on quat0
		//quat0.transform(test,test2);
		System.out.println("quat0: "+angles[1]+" ,  "+axis1.x()+" ,  "+ axis1.y()+" ,  "+axis1.z());
		quat0.transform(pt0,pt1);
		quat0.transform(ref,ref1);
		System.out.println(pt0);
		System.out.println(pt1);
		//// rotate around parent arm as axis for angle phi
		Quaterniond quat1 = new Quaterniond(new AxisAngle4d(angles[0],PD.x(), PD.y(),PD.z()));
		quat1.transform(pt1,pt2);
		
		quat1.transform(ref1,ref2);
		ref.negate();
		System.out.println("quat1: "+angles[0]+" ,  "+PD.x()+" ,  "+ PD.y()+" ,  "+PD.z());
		System.out.println (pt1);
		System.out.println(pt2);
		///// test code
		//Quaterniond quat2 = new Quaterniond(new AxisAngle4d(3.14/2,0.2, 0.0, 0.0));
		//Vector3d ttt = new Vector3d();
		//quat2.transform(new Vector3d(0,1,0),ttt);
		//	System.out.println("ttt: "+ttt);
		
		
		
		pt2.add(v1);
		/*
		Matrix3d m = new Matrix3d(cosA,-sinA,v1.x(),sinA,cosA,v1.y(),0,0,1);
		Vector3d s1 = new Vector3d(PD.normalize().mul(RL.getLength()));
//		System.out.println("parent joint:"+v1.x+" ,"+v1.y+"   angle: "+angle+"    length: "+RL.getLength());
//		System.out.println("child joint before:"+v3.x+" ,"+v3.y);
//		System.out.println("s1 before rotation:"+s1.x+" ,"+s1.y);
//		System.out.println("RL distance: " + RL.getLength());
		s1.mul(m.transpose());
//		System.out.println("s1 after rotation:"+s1.x+" ,"+s1.y);
 * */

		v2.set(pt2.x(),pt2.y(),pt2.z());
		System.out.println("update point: "+pt2.x()+ ",   "+ pt2.y() + ",     "+ pt2.z());
		for (int i =0 ; i< RL.childsize();i++){
			RL.getChild(i).getJoint().position = v2;
		}
		Vector3d orientation = v1.sub(v2).normalize();
		
//		System.out.println("child joint  after:"+v2.x+" ,"+v2.y);
		for(int i=0; i<RL.childsize(); i++){
			if(RL.getChild(i).isEnd()){
				return;
			}
			updatePositions3D(RL.getChild(i).getChild(), orientation, ref2);
		}
	}
	
	
	
}
