package c2g2.kinematics.engine;


public class Main {

  
    public static void main(String[] args) {
        Scene scene = new Scene();
        scene.setis2D(true); //2D
        scene.loadfromXML("src/resources/models/test.xml");
        
        //scene.setis2D(false); //3D
        //scene.loadfromXML("src/resources/models/sk1.xml");

        Renderer r = new Renderer(scene); 
        r.run();
    }

}