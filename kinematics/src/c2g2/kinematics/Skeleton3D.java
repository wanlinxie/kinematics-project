package c2g2.kinematics;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.joml.Vector2d;
import org.joml.Vector3d;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.joml.Quaterniond;
import org.joml.AxisAngle4d;

public class Skeleton3D {
	
	private RigidLink3D	root = null;
	
	//construct skeleton from xml file
	public Skeleton3D(String xml) {
		try {	
	         File inputFile = new File(xml);
	         DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	         DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	         Document doc = dBuilder.parse(inputFile);
	         
	         doc.getDocumentElement().normalize();
	         System.out.println("Root element :" 
	            + doc.getDocumentElement().getNodeName());
	         NodeList nList = doc.getElementsByTagName("root");
	         Element e = (Element)nList.item(0);
	         
			 double px1 = Double.parseDouble(e.getAttribute("x1"));
			 double py1 = Double.parseDouble(e.getAttribute("y1"));
			 double pz1 = Double.parseDouble(e.getAttribute("z1"));
			 double px2 = Double.parseDouble(e.getAttribute("x2"));
			 double py2 = Double.parseDouble(e.getAttribute("y2"));
			 double pz2 = Double.parseDouble(e.getAttribute("z2"));
//			 System.out.println(px1);
//			 System.out.println(py1);
//			 System.out.println(px2);
//			 System.out.println(py2);
				
		     root = new RigidLink3D();
		     //Vector3d tempaxis = new Vector3d();
		     Vector3d ref0 = new Vector3d(0,1,0);
		     Vector3d ref1 = new Vector3d();
		     Vector3d ref2 = new Vector3d();
		     Vector3d pt0 =  new Vector3d(0,0,1);
		     
		     Vector3d axis1=new Vector3d(pt0);
				axis1.cross(ref0);
				axis1.normalize();

				
		     
		     
		     Vector3d pt1 = new Vector3d(px1, py1, pz1);
		     Vector3d pt2 = new Vector3d(px2, py2, pz2);
		     double[] angles = calculateAngle3d(pt1,pt2,pt0.add(pt2),ref0);
		     RevoluteJoint3D a0 = new RevoluteJoint3D(pt1, angles[0], angles[1]);
		     RevoluteJoint3D a1 = new RevoluteJoint3D(pt2, 0, 0.0);
		     Quaterniond quat = new Quaterniond(new AxisAngle4d(angles[0],new Vector3d(pt1).sub(pt2).normalize()));
			 quat.transform(ref0,ref1);
			 Quaterniond quat1 = new Quaterniond(new AxisAngle4d(angles[1],axis1.x(), axis1.y(),axis1.z()));
			 quat1.transform(ref1,ref2);
			 //ref2.negate();
		     LinkConnection3D rootconnection = new LinkConnection3D();
		     rootconnection.setChild(root);
		     rootconnection.setJoint(a0);
		     root.setParent(rootconnection);
		     root.setLength(a0.getPos().distance(a1.getPos()));
//		     System.out.println("Distance: "+root.getLength());
		     buildSkeletonFromDoc(e, root, a1, 1, ref2);
		         
//		     System.out.println("----------------------------");
		     
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private void buildSkeletonFromDoc(Element e, RigidLink3D parent, RevoluteJoint3D joint0, int level, Vector3d ref){
		NodeList nList = e.getElementsByTagName("joint"+level);

		if (nList.getLength()==0) {
			System.out.println("pos:");
			System.out.println(joint0.getPos());
			LinkConnection3D dummyLink = new LinkConnection3D();
			dummyLink.setParent(parent);
			dummyLink.setJoint(joint0);
			parent.addChild(dummyLink);
			return;
		}
		
		System.out.println("list length: "+Integer.toString(nList.getLength()));
		
		for(int i=0; i< nList.getLength(); i++) {
			Element cElement = (Element)nList.item(i);
			if(cElement==null){
				continue;
			}
			
			
			double px = Double.parseDouble(cElement.getAttribute("x"));
	        double py = Double.parseDouble(cElement.getAttribute("y"));
	        double pz = Double.parseDouble(cElement.getAttribute("z"));
		    System.out.println(px);
		    System.out.println(py);
		    System.out.println(pz);
		    Vector3d newPt = new Vector3d(px,py,pz);
		    
		    RigidLink3D ri = new RigidLink3D();
		    LinkConnection3D connection3d = new LinkConnection3D();
		    RevoluteJoint3D ji = new RevoluteJoint3D(new Vector3d(px, py, pz), 0.0, 0.0);
		    RevoluteJoint3D j0 = new RevoluteJoint3D(joint0.getPos(),0.0, 0.0);
		    connection3d.setParent(parent);
		    connection3d.setJoint(j0);
		    connection3d.setChild(ri);
		    ri.setParent(connection3d);
		    parent.addChild(connection3d);
	        ////
		    Vector3d v1,v2;
		    v1= parent.getParentJoint().getPos();
		    v2= joint0.getPos();
		   // double angle2 = ;
		    double[] angle = calculateAngle3d(v1,v2, newPt,ref);
		    Vector3d ref2= new Vector3d();
		    Quaterniond quat = new Quaterniond(new AxisAngle4d(angle[0],new Vector3d(v1).sub(v2).normalize()));
		    quat.transform(ref, ref2);
		    j0.setParam(angle[0], angle[1]);
//		    System.out.println("angle: " + angle);
		    double segmentLength = calculateDistance(v2,new Vector3d(px,py,pz));
//		    System.out.println("distance: " + segmentLength);
		    ri.setLength(segmentLength);
		    ////
	        buildSkeletonFromDoc(cElement, ri, ji, level+1, ref2);
		} 
	}
	
	private double calculateDistance(Vector3d v1, Vector3d v2){
		Vector3d tmp = new Vector3d(v1);
		
		return tmp.distance(v2);
	}
	private double[] calculateAngle3d(Vector3d v1, Vector3d v2, Vector3d v3, Vector3d ref){
		//// v1-v2, v3-v2
		Vector3d  d0= new Vector3d();
		d0.set(v2);
		Vector3d  d1= new Vector3d();
		d1.set(v1);
		d1.sub(d0);
		d1.normalize();
		Vector3d d2= new Vector3d();
		d2.set(v3);
		d2.sub(d0);
		d2.normalize();
		double[] angle =  new double[2]; 
		angle[0]= Math.acos(d2.dot(d1));  // theta
		/// use ref to determine phi
		Vector3d n1 = new Vector3d(d1);
		n1.cross(ref);
		Vector3d n2 = new Vector3d(d1);
		n2.cross(d2);
		angle[1]=Math.acos(n2.dot(n1));
		if (n1.dot(d2) > 0) angle[0] = -angle[0];
		return angle;
	}
	
	public RigidLink3D getRoot() {
		return root;
	}
	
	public void setRoot(RigidLink3D r){
		root = r;
	}
	public void forwardK() {
		new ForwardKinematics(this);	
	}

}
