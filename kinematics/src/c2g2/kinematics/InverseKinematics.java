package c2g2.kinematics;
import java.util.ArrayList;

import org.joml.Matrix3d;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Quaterniond;
/*
 * This is the class to implement your inverse kinematics algorithm.
 * 
 * TODO:
 * Please include your data structure and methods for the inverse kinematics here.
 */
public class InverseKinematics {
	
	private Skeleton2D skeleton = null;
	
	public InverseKinematics(Skeleton2D ske) {
		if ( ske == null ) throw new NullPointerException("The provided skeleton is NULL");
		skeleton = ske;
		
	}
	////
	public int dragJointTo(LinkConnection2D j, Vector2d pos){
		 ArrayList<Vector2d> jacob;
		 ArrayList<Double> parameters = new ArrayList<Double>();
		 getParamFromFixedPoint(j, parameters);
		 Vector2d b = new Vector2d(pos);
		 double beta = 0.1; // scaler
		 Vector2d pos2 = calculateEndPos(parameters);
		 //// 
		 b.sub(pos2);  // change of position needed.
//		 System.out.println("dragJoint from pos2: "+pos2);
		 while (pos2.distance(pos)>0.01){
			 b.set(pos);
			 b.sub(pos2);
			 jacob = calculateJacobian(parameters);
//			 System.out.println("jacob:"+ jacob);
//			 System.out.println("b :" + b);
			 double [] dtheta = solveJacob(jacob, b, beta);
//			 System.out.println("d theta :" + dtheta);
			 if (dtheta == null) break;
			 for (int i=2;i+1<parameters.size();i+=2){
				 parameters.set(i,parameters.get(i)+ dtheta[i/2-1]);
			 }
			 pos2 = calculateEndPos(parameters);
		 }
		 ApplyParameter(j,parameters);
		return 1;
	}
	private void ApplyParameter(LinkConnection2D j, ArrayList<Double> param){
		int n = param.size();
//		System.out.println("apply parameter:  "+param.get(n-1));
		j.getJoint().setParam(param.get(n-1));
				boolean a = j.getJoint().isFixed();
				boolean b =false;
				if (j.getChild()!=null) b = j.getChild().isRoot();
				if (a  || b){
					return;
				}
				
				//output.add(0, j.getParent().getLength());
				if (n<=3) return;
				param.remove(n-1);
				param.remove(n-2);
				ApplyParameter(j.getParent().getParent(),param);
				return;
	}
	//// output parameter structure x, y, angle, length, angle, length....
	private void getParamFromFixedPoint(LinkConnection2D j, ArrayList<Double> output){
		output.add(0, j.getJoint().getParam());
		//System.out.println("angle0: "+j.getJoint().getParam());
		boolean a = j.getJoint().isFixed();
		
		//System.out.println("angle: "+j.getJoint().getParam());
		boolean b =false;
		if (j.getChild()!=null) b = j.getChild().isRoot();
		//System.out.println("angle2: "+j.getJoint().getParam());
		if (a  || b){
//			System.out.println("x,y: "+j.getJoint().getPos().x()+",  "+j.getJoint().getPos().y());
			output.add(0,j.getJoint().getPos().y());
			output.add(0,j.getJoint().getPos().x());
			
			return;
		}
//		System.out.println("length: ..........");
		
		output.add(0, j.getParent().getLength());
//		System.out.println("length: "+j.getParent().getLength());
		getParamFromFixedPoint(j.getParent().getParent(),output);
		return;
	}
	//// Nx2 array for dx/d theta and dy/d theta
	private ArrayList<Vector2d> calculateJacobian(ArrayList<Double> parameters){
		ArrayList<Vector2d> jacob = new ArrayList<Vector2d>();
		ArrayList<Double> p=new ArrayList<Double>(parameters);
		int n = p.size();
		int m = (n-3)/2;
		//Vector2d Angle = new Vector2d(0.0, 1.0f);
		//// 
		double dA = 0.01; //// d_theta 
		Vector2d endPos1, endPos2;
		for (int i = 2; i+1< parameters.size(); i+=2){
			p.set(i,parameters.get(i)+dA);
			endPos1 = calculateEndPos(p);
			p.set(i,parameters.get(i)-dA);
			endPos2 = calculateEndPos(p);
			jacob.add(new Vector2d((endPos1.x()-endPos2.x())/2/dA,(endPos1.y()-endPos2.y())/2/dA));
			
			p.set(i,parameters.get(i));  //// restore the orginal value
			
		}
		return jacob;
	}
	//// calculate pos based on angle, length array
	private Vector2d calculateEndPos(ArrayList<Double> p){
		//// this is part of forward kinematics
		Vector2d pos = new Vector2d(p.get(0),p.get(1)); //// this is the root position
		Vector2d PD = new Vector2d(0,1.0); //// default root direction, vertical up
		double angle, length;
		double cosA, sinA;
		Matrix3d m;
		Vector3d s1 ;
		for(int i=2;i+1<p.size();i+=2){
			angle = p.get(i); length = p.get(i+1);
			cosA = Math.cos(angle);
			sinA = Math.sin(angle);
			m = new Matrix3d(cosA,-sinA, pos.x(),sinA,cosA, pos.y(),0,0,1);
			s1 = new Vector3d(PD.normalize().mul(length),1.0);
			s1.mul(m.transpose());
			pos.set(s1.x(),s1.y());
			
		}
		//// pos is now the end position
		return pos;
	}
	//// jacob: nx2
	private double[] solveJacob(ArrayList<Vector2d> jacob, Vector2d b, double beta){
		int n = jacob.size();
		double[][] JTJ = new double[n][n];
		double[] Y = new double [n];
//		System.out.println("JTJ : " + JTJ);
		for (int i=0;i<n;i++){
			Y[i] = jacob.get(i).x()*b.x()+jacob.get(i).y()*b.y();
			for(int j=0;j<n;j++){
				JTJ[i][j]=jacob.get(i).x()*jacob.get(j).x()+jacob.get(i).y()*jacob.get(j).y();
				if (i==j)
					JTJ[i][j]+=beta;
//				System.out.print(" ,  "+JTJ[i][j]);
			}
//			System.out.println("");
		}
//		System.out.println("Y :" + Y);
		return LinearSolver(JTJ,Y);
	}
	////M: nxn matrix, Y: n vector
	private double[] LinearSolver(double[][] M, double[] Y){
		//
		int n = Y.length;
		double[] X = new double[n];
		//System.out.print("Y :");
		for (int i= 0; i< n; i++){
			X[i] = Y[i];
	//		System.out.print(", "+Y[i]);
		}
		//System.out.println("");
		double det = determinant(M);
		//System.out.println("JTJ det = " + det);
		if (Math.abs(det)<1e-6) return null;  //// no solution
		
		double[][] M_inv = new double[n][n];
		double signI=1.0;
		double signJ=1.0;
		for (int i= 0; i< n; i++){
			signJ=1.0;
			for (int j = 0 ; j<n; j++){
				M_inv[i][j] = signI * signJ * determinant(subMatrix(M, i,j))/det;
				signJ *= -1.0;
			}
			signI *= -1.0;
		}
		// M_inv * Y
		for (int i=0; i< n ; i++){
			X[i]=0;
			for (int j=0; j<n; j++){
				X[i] += M_inv[i][j]*Y[j];
			}
		}
		return X;
	}
	////
	private double determinant(double[][] M){
		int n = M.length;
		if (n==1)return M[0][0];
		double det = 0.0;
		double sign = 1.0;
		for (int i=0;i<n;i++){
			det += sign * M[0][i]*determinant(subMatrix(M,0,i));
			sign *= -1.0;
		}
		
		return det;
	}
	//// return matrix without i row and j column
	private double[][] subMatrix(double[][] M, int i, int j){
		int n=M.length;
		int m=M[0].length;
		double[][] sM = new double[n-1][m-1];
		for (int ii=1; ii<n;ii++){
			for (int jj=1;jj<m;jj++){
				
				sM[ii-1][jj-1] = M[(ii>i)?ii:ii-1][(jj>j)?jj:jj-1];
			}
		}
		return sM;
	}
}
