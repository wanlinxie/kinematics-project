package c2g2.kinematics;
import org.joml.Vector3d;
import org.joml.Vector2d;
import org.joml.Matrix3d;


import java.io.File;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.lang.Math;

public class Skeleton2D {
	
	private RigidLink2D	root = null;
	
		//construct skeleton from xml file
		public Skeleton2D(String xml) {
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
				 double px2 = Double.parseDouble(e.getAttribute("x2"));
				 double py2 = Double.parseDouble(e.getAttribute("y2"));
//				 System.out.println(px1);
//				 System.out.println(py1);
//				 System.out.println(px2);
//				 System.out.println(py2);
				
				
		         root = new RigidLink2D();
		         Joint2D a0 = new RevoluteJoint2D(new Vector2d(px1, py1), 3.14);
		         Joint2D a1 = new RevoluteJoint2D(new Vector2d(px2, py2), 0.0);
		         LinkConnection2D rootconnection = new LinkConnection2D();
		         rootconnection.setChild(root);
		         rootconnection.setJoint(a0);
		         root.setParent(rootconnection);
		         root.setLength(a0.getPos().distance(a1.getPos()));
//		         System.out.println("Distance: "+root.getLength());
		         buildSkeletonFromDoc(e, root, a1, 1);
		         
//		         System.out.println("----------------------------");
		     
		      } catch (Exception e) {
		         e.printStackTrace();
		      }
			
		}
	
	private void buildSkeletonFromDoc(Element e, RigidLink2D parent, Joint2D joint0, int level){
		NodeList nList = e.getElementsByTagName("joint"+level);

		if (nList.getLength()==0) {
			System.out.println("pos:");
			System.out.println(joint0.getPos());
			LinkConnection2D dummyLink = new LinkConnection2D();
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
		    System.out.println(px);
		    System.out.println(py);
		    
		    RigidLink2D ri = new RigidLink2D();
		    LinkConnection2D connection2d = new LinkConnection2D();
		    Joint2D ji = new RevoluteJoint2D(new Vector2d(px, py), 0.0);
		    Joint2D j0 = new RevoluteJoint2D(joint0.getPos(),0.0);
		    connection2d.setParent(parent);
		    connection2d.setJoint(j0);
		    connection2d.setChild(ri);
		    ri.setParent(connection2d);
		    parent.addChild(connection2d);
	        ////
		    Vector2d v1,v2;
		    v1= parent.getParentJoint().getPos();
		    v2= joint0.getPos();
		    
		    double angle = calculateAngle(v1,v2, new Vector2d(px,py));
		    j0.setParam(angle);
//		    System.out.println("angle: " + angle);
		    double segmentLength = calculateDistance(v2,new Vector2d(px,py));
//		    System.out.println("distance: " + segmentLength);
		    ri.setLength(segmentLength);
		    ////
	        buildSkeletonFromDoc(cElement, ri, ji, level+1);
		}
	}
	////
	private double calculateDistance(Vector2d v1, Vector2d v2){
		Vector2d tmp=new Vector2d(v1);
		
		return tmp.distance(v2);
	}
	private double calculateAngle(Vector2d v1, Vector2d v2, Vector2d v3){
		Vector3d  d0= new Vector3d();
		d0.set(v2,0);
		Vector3d  d1= new Vector3d();
		d1.set(v1,0);
		d1.sub(d0);
		d1.normalize();
		Vector3d d2= new Vector3d();
		d2.set(v3,0);
		d2.sub(d0);
		d2.normalize();
		double angle = Math.acos(d2.dot(d1));
		if (d2.cross(d1).z > 0) angle = -angle;
		return angle;
	}
	
	public void buildTestSkeleton1(){
		root = new RigidLink2D();
		LinkConnection2D pLinkConnection2D = new LinkConnection2D();
		pLinkConnection2D.setChild(root);
		Joint2D j0 = new RevoluteJoint2D(new Vector2d(0.2, 0.3), 0.0);
		pLinkConnection2D.setJoint(j0);
		
		root.setParent(pLinkConnection2D);
		
		LinkConnection2D cLinkConnection2D = new LinkConnection2D();
		Joint2D j1 = new RevoluteJoint2D(new Vector2d(0.4, 0.3), 0.0);
		cLinkConnection2D.setParent(root);
		cLinkConnection2D.setJoint(j1);
		root.addChild(cLinkConnection2D);
		
		System.out.println("test constructor");
	}
	
	public void buildTestSkeleton2(){
		root = new RigidLink2D();
		LinkConnection2D link0 = new LinkConnection2D();
		link0.setChild(root);
		Joint2D a = new RevoluteJoint2D(new Vector2d(0.0f, 0.3f), 0.0);
		link0.setJoint(a);
		root.setParent(link0);
		
		
		
		LinkConnection2D link1 = new LinkConnection2D();
		LinkConnection2D link2 = new LinkConnection2D();
		LinkConnection2D link3 = new LinkConnection2D();
		LinkConnection2D link4 = new LinkConnection2D();
		LinkConnection2D link5 = new LinkConnection2D();
		LinkConnection2D link6 = new LinkConnection2D();
		
		root.addChild(link1);
		root.addChild(link2);
		root.addChild(link3);
		
		RigidLink2D r1 = new RigidLink2D();
		RigidLink2D r2 = new RigidLink2D();
		RigidLink2D r3 = new RigidLink2D();
		link1.setParent(root);
		link1.setChild(r1);
		link2.setParent(root);
		link2.setChild(r2);
		link3.setParent(root);
		link3.setChild(r3);
		
		link4.setParent(r1);
		link5.setParent(r2);
		link6.setParent(r3);
		
		Joint2D a1 = new RevoluteJoint2D(new Vector2d(0.0f, 0.1f), 0.0);
		Joint2D a2 = new RevoluteJoint2D(new Vector2d(-0.1f, -0.1f), 0.0);
		Joint2D a3 = new RevoluteJoint2D(new Vector2d(-0.0f, -0.1f), 0.0);
		Joint2D a4 = new RevoluteJoint2D(new Vector2d(0.1f, -0.1f), 0.0);
		link1.setJoint(a1);
		link2.setJoint(a1);
		link3.setJoint(a1);
		link4.setJoint(a2);
		link5.setJoint(a3);
		link6.setJoint(a4);
		
		r1.setParent(link1);
		r2.setParent(link1);
		r3.setParent(link1);
		r1.addChild(link4);
		r2.addChild(link5);
		r3.addChild(link6);
		
		Joint2D a5 = new RevoluteJoint2D(new Vector2d(0.0f, -0.3f), 0.0);
		LinkConnection2D link7 = new LinkConnection2D();
		RigidLink2D r4 = new RigidLink2D();
		link7.setParent(r2);
		link5.setChild(r4);
		link7.setParent(r4);
		link7.setJoint(a5);
	
		r4.setParent(link7);
		r4.addChild(link7);
	}
	
	public RigidLink2D getRoot() {
		return root;
	}
	
	public void setRoot(RigidLink2D r){
		root = r;
	}
	
	/*
	//update rotation angle of joint
	public void updateAngle(){
		
	}
	
	//update length of rigidlink
	public void updateLength(){
		
	}
	public double[] getParams(){
		RigidLink2D r = getRoot();
		return null;
	}*/

	public void forwardK() {
		new ForwardKinematics(this);	
	}
}
