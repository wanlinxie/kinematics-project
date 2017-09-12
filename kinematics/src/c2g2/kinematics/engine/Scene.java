package c2g2.kinematics.engine;

import java.util.ArrayList;

import org.joml.Vector2d;
import org.joml.Vector3d;

import c2g2.kinematics.*;

public class Scene {
	
	public boolean is2D; //true -> 2D, false ->3D
	
	public Skeleton2D skeleton2d;
	private RigidLink2D m2_highlight_RL;
	private LinkConnection2D m2_highlight_LC;
	private LinkConnection2D m2_dragging_joint;
	
	public Skeleton3D skeleton3d;
	private RigidLink3D m3_highlight_RL;
	private LinkConnection3D m3_highlight_LC;
	//private LinkConnection3D m3_dragging_joint;
	
	
	private int m_edit;  // 0: non-edit, 1: RigidLink, 2: LinkConnection
	private int m_LC_index;
	private int m_highlight_index;
	
	public Scene() {
		is2D = true;
		skeleton2d = null;
		skeleton3d = null;
		
		m2_highlight_RL = null;
		m2_highlight_LC = null;
		m2_dragging_joint = null;
		m3_highlight_RL = null;
		m3_highlight_LC = null;
		//m3_dragging_joint = null;
		
		m_edit = 0;
		m_LC_index = 0;
		
	}
	
	public void setis2D(boolean dim) {
		is2D = dim;
	}
	void loadfromXML(String filename){
		if(is2D == true) {
			skeleton2d = new Skeleton2D(filename);
			System.out.println("TEST");
			if(skeleton2d == null) {
				System.out.println("NULL");
			} else
				System.out.println("NOT NULL");
		}
		else
			skeleton3d = new Skeleton3D(filename);
	}

	void test(){
		skeleton2d = new Skeleton2D("src/resources/models/test.xml");
//		skeleton.buildTestSkeleton2();
	}
	
	public ArrayList<Vector2d> get2DJointPos() {
		ArrayList<Vector2d> output = new ArrayList<Vector2d>();
		if(skeleton2d == null) {
			System.out.println("NULL3");
		}
		RigidLink2D current = skeleton2d.getRoot();
		visit2DNode(current, output);
		return output;	
	}
	
	private void visit2DNode(RigidLink2D current, ArrayList<Vector2d> output){
		if (m_edit==1 && m2_highlight_RL == current)
			m_highlight_index = output.size();
		if (m_edit==2 && m2_highlight_LC == current.getParent())
			m_highlight_index = output.size();
		output.add(current.getParentJoint().getPos());
		if (m_edit==2 && m2_highlight_LC == current.getChild(0))
			m_highlight_index = output.size();
		output.add(current.getChildJoint().getPos());

		for(int i=0; i<current.childsize(); i++){
			if(current.getChild(i).isEnd()){
				return;
			}
			visit2DNode(current.getChild(i).getChild(), output);
		}
		return;
	}
	
	public ArrayList<Vector3d> get3DJointPos() {
		ArrayList<Vector3d> output = new ArrayList<Vector3d>();
		RigidLink3D current = skeleton3d.getRoot();
		visit3DNode(current, output);
		return output;	
	}
	
	private void visit3DNode(RigidLink3D current, ArrayList<Vector3d> output){
		if (m_edit==1 && m3_highlight_RL == current)
			m_highlight_index = output.size();
		if (m_edit==2 && m3_highlight_LC == current.getParent())
			m_highlight_index = output.size();
		output.add(current.getParentJoint().getPos());
		if (m_edit==2 && m3_highlight_LC == current.getChild(0))
			m_highlight_index = output.size();
		output.add(current.getChildJoint().getPos());

		for(int i=0; i<current.childsize(); i++){
			if(current.getChild(i).isEnd()){
				return;
			}
			visit3DNode(current.getChild(i).getChild(), output);
		}
		return;
	}

	public int getHighlightIndex(){
		return m_highlight_index;
	}
	
	public RigidLink2D getHighlightRL2D(){
		if (m_edit == 1) return this.m2_highlight_RL;
		else return null;		
	}

	public LinkConnection2D getHighlightLC2D(){
		if (m_edit == 2) return this.m2_highlight_LC;
		else return null;		
	}
	public RigidLink3D getHighlightRL3D(){
		if (m_edit == 1) return this.m3_highlight_RL;
		else return null;		
	}

	public LinkConnection3D getHighlightLC3D(){
		if (m_edit == 2) return this.m3_highlight_LC;
		else return null;		
	}

	public void setEditStatus(boolean editable){
		if (editable && m_edit < 1){
			m_edit += 2;
			if (skeleton2d != null) {
				if (m2_highlight_RL == null){
					m2_highlight_RL = skeleton2d.getRoot();
					m_edit=1;
				}
			} else {
				if (m3_highlight_RL == null){
					m3_highlight_RL = skeleton3d.getRoot();
					m_edit=1;
				}
			}
		} else {
			if(!editable){
				if (m_edit > 0) m_edit -= 2;
			}
		}
	}
	
	public int getEditStatus(){
		return m_edit;
	}

	public void increaseHighlight(){
		if (m_edit < 1) return;
		if (m_edit == 1){
			if(skeleton2d != null) m2_highlight_RL.setLength(m2_highlight_RL.getLength()*1.1);
			else m3_highlight_RL.setLength(m3_highlight_RL.getLength()*1.1);
		}
		if (m_edit ==2){
			if(skeleton2d != null) m2_highlight_LC.getJoint().setParam(0.1+m2_highlight_LC.getJoint().getParam());
			else m3_highlight_LC.getJoint().setParam(0.1+m3_highlight_LC.getJoint().getParam()[0],m3_highlight_LC.getJoint().getParam()[1]);
		}
	}
	public void decreaseHighlight(){
		if (m_edit < 1) return ;
		if (m_edit == 1){
			if(skeleton2d != null) m2_highlight_RL.setLength(m2_highlight_RL.getLength()*0.9);
			else m3_highlight_RL.setLength(m3_highlight_RL.getLength()*0.9);
		}
		if (m_edit ==2){
			if(skeleton2d != null) m2_highlight_LC.getJoint().setParam(-0.1+m2_highlight_LC.getJoint().getParam());
			else m3_highlight_LC.getJoint().setParam(-0.1+m3_highlight_LC.getJoint().getParam()[0],m3_highlight_LC.getJoint().getParam()[1]);
		}
	}
	public void increaseHighlight2(){
		if (m_edit < 1) return;
		if (m_edit == 1){
			if(skeleton2d != null) m2_highlight_RL.setLength(m2_highlight_RL.getLength()*1.1);
			else m3_highlight_RL.setLength(m3_highlight_RL.getLength()*1.1);
		}
		if (m_edit ==2){
			if(skeleton2d != null) m2_highlight_LC.getJoint().setParam(0.1+m2_highlight_LC.getJoint().getParam());
			else m3_highlight_LC.getJoint().setParam(m3_highlight_LC.getJoint().getParam()[0],0.1+m3_highlight_LC.getJoint().getParam()[1]);
		}
	}
	public void decreaseHighlight2(){
		if (m_edit < 1) return ;
		if (m_edit == 1){
			if(skeleton2d != null) m2_highlight_RL.setLength(m2_highlight_RL.getLength()*0.9);
			else m3_highlight_RL.setLength(m3_highlight_RL.getLength()*0.9);
		}
		if (m_edit ==2){
			if(skeleton2d != null) m2_highlight_LC.getJoint().setParam(-0.1+m2_highlight_LC.getJoint().getParam());
			else m3_highlight_LC.getJoint().setParam(m3_highlight_LC.getJoint().getParam()[0],-0.1+m3_highlight_LC.getJoint().getParam()[1]);
		}
	}
	// action: 1-- prev sibling, 2-- next sibling, 3-- parent, 4-- son
	public boolean changeHighlight(int action){
		System.out.println("...change hight ()");
		if (m_edit <= 0) return false;
		switch (action){
		case 1:  // prev sibling
			if (m_edit == 1 || m_LC_index <=0) return false;
			m_LC_index-- ;
			if(skeleton2d != null) m2_highlight_LC = m2_highlight_LC.getParent().getChild(m_LC_index);
			else m3_highlight_LC = m3_highlight_LC.getParent().getChild(m_LC_index);
			return true;
		case 2:  // next sibling
			if(skeleton2d != null) {
				if (m_edit == 1 || m_LC_index+1 >= m2_highlight_LC.getParent().childsize() ) return false;
				m_LC_index++ ;
				m2_highlight_LC = m2_highlight_LC.getParent().getChild(m_LC_index);
				return true;
			}
			else {
				if (m_edit == 1 || m_LC_index+1 >= m3_highlight_LC.getParent().childsize() ) return false;
				m_LC_index++ ;
				m3_highlight_LC = m3_highlight_LC.getParent().getChild(m_LC_index);
				return true;
			}
		case 3: // parent
			if (m_edit == 1){ // RightLink
				//if (m_highlight_RL.isRoot()) return false;
				if(skeleton2d != null)  m2_highlight_LC = m2_highlight_RL.getParent();
				else m3_highlight_LC = m3_highlight_RL.getParent();
				m_LC_index =0;
				m_edit =2;
				return true;
			}else{  // LinkConnection
				if(skeleton2d != null) {
					if (m2_highlight_LC.getChild().isRoot()) return false;
					m2_highlight_RL = m2_highlight_LC.getParent();
					m_LC_index = 0;
					m_edit = 1;
					return true;
				} else {
					if (m3_highlight_LC.getChild().isRoot()) return false;
					m3_highlight_RL = m3_highlight_LC.getParent();
					m_LC_index = 0;
					m_edit = 1;
					return true;
				}
			}
		case 4: // child
			System.out.println(".. move to child");
			if (m_edit==1) {
				if(skeleton2d != null) {
					System.out.println(m2_highlight_RL);
					if (m2_highlight_RL.childsize() <= 0) return false;
					m2_highlight_LC = m2_highlight_RL.getChild(0);
					m_edit = 2;
					System.out.println(".. move to child.. done");
					return true;
				} else {
					System.out.println(m3_highlight_RL);
					if (m3_highlight_RL.childsize() <= 0) return false;
					m3_highlight_LC = m3_highlight_RL.getChild(0);
					m_edit = 2;
					System.out.println(".. move to child.. done");
					return true;
				}
			} else {
				if(skeleton2d != null) {
					System.out.println(m2_highlight_LC);
					
					if (m2_highlight_LC.isEnd()) return false;
					m2_highlight_RL = m2_highlight_LC.getChild();
					m_edit = 1;
					System.out.println(".. move to child_done");
					return true;
				} else {
					System.out.println(m3_highlight_LC);
					
					if (m3_highlight_LC.isEnd()) return false;
					m3_highlight_RL = m3_highlight_LC.getChild();
					m_edit = 1;
					System.out.println(".. move to child_done");
					return true;
				}
			}
		}
		return false;
	}
	//inverse stuff
	public boolean getDragStatus(){
		return (this.m2_dragging_joint!=null);
	}
	
	public void stopDrag(){
		this.m2_dragging_joint = null;	
	}
	
	public int Dragable(double x, double y){
		m2_dragging_joint = getJointAtPos(skeleton2d.getRoot(), new Vector2d(x,y));
		if (m2_dragging_joint !=null) return 1;
		return 0;
	}

	public int DragTo(double x, double y){
		 InverseKinematics IK = new InverseKinematics(this.skeleton2d);
		 return IK.dragJointTo(m2_dragging_joint, new Vector2d (x,y));
		 
	}

	public LinkConnection2D getDraggingJoint(){
		return m2_dragging_joint;
	}
	
	public LinkConnection2D getJointAtPos(RigidLink2D r, Vector2d v){
		LinkConnection2D res;
		if (r.getParentJoint().getPos().distance(v)<0.05){
			return r.getParent();
		}
		if (r.getChildJoint().getPos().distance(v) <0.05){
			return r.getChild(0);
		}
		
		for(int i=0; i<r.childsize(); i++){
			if(!r.getChild(i).isEnd()){
				res = getJointAtPos(r.getChild(i).getChild(), v);
				if (res!=null) return res;
			}
		}
		return null;
	}
	
	
}
