package Group9.agent;

import Group9.Game;
import Group9.map.objects.Wall;
import Interop.Action.*;
import Interop.Agent.Guard;
import Interop.Geometry.Angle;
import Interop.Geometry.Direction;
import Interop.Geometry.Distance;
import Interop.Geometry.Point;
import Interop.Percept.GuardPercepts;
import Interop.Percept.Scenario.SlowDownModifiers;
import Interop.Percept.Smell.SmellPercept;
import Interop.Percept.Smell.SmellPerceptType;
import Interop.Percept.Sound.SoundPercept;
import Interop.Percept.Sound.SoundPerceptType;
import Interop.Percept.Vision.ObjectPercept;
import Interop.Percept.Vision.ObjectPerceptType;

import java.io.*;
import java.util.ArrayList;
import java.util.Set;

public class Capturer implements Guard {

    public static boolean debug = true;

    public boolean detectIntruderFirst = false;

    public boolean detectIntruderSecond = false;

    private int trackSequence = 0;

    private int trackBuffer = 0;

    ArrayList<ActionHistory> actionHistory;

    public Capturer(){
        actionHistory = new ArrayList<>();
    }

    @Override
    public GuardAction getAction(GuardPercepts percepts) {

        Txt IO = new Txt();
        Built_In  bi = new Built_In();

        Set<ObjectPercept> objectPercepts = percepts.getVision().getObjects().getAll();
        Set<SoundPercept> soundPercepts = percepts.getSounds().getAll();
        Set<SmellPercept> smellPercepts = percepts.getSmells().getAll();
        ArrayList<ObjectPercept> objectPerceptArrayList = new ArrayList<ObjectPercept>(objectPercepts);
        ArrayList<SoundPercept> soundPerceptArrayList = new ArrayList<SoundPercept>(soundPercepts);
        ArrayList<SmellPercept> smellPerceptArrayList = new ArrayList<>(smellPercepts);

        SlowDownModifiers slowDownModifiers =  percepts.getScenarioGuardPercepts().getScenarioPercepts().getSlowDownModifiers();
        double modifier = 1;
        if (percepts.getAreaPercepts().isInWindow()){
            modifier = slowDownModifiers.getInWindow();
        }else if (percepts.getAreaPercepts().isInSentryTower()){
            modifier = slowDownModifiers.getInSentryTower();
        }else if (percepts.getAreaPercepts().isInDoor()){
            modifier = slowDownModifiers.getInDoor();
        }


        if (!detectIntruderFirst&&!detectIntruderSecond){
           if (bi.hasObjectInView(objectPerceptArrayList, ObjectPerceptType.Intruder)){
               Point point = bi.getIntruder(objectPerceptArrayList);

               if (debug){
                   printPointXY(point);
               }

               IO.writePoint1(IO.point1,point);


               if (debug) System.out.println("---------------------------First found Intruder, stay to find--------------------------------");
               detectIntruderFirst = true;

               return new NoAction();
           }
        }

        if (detectIntruderFirst&&!detectIntruderSecond){

            if (bi.hasObjectInView(objectPerceptArrayList, ObjectPerceptType.Intruder)){
                if (debug) System.out.println("---------------------------Second found Intruder, stay to find--------------------------------");
                detectIntruderSecond = true;

                Point point1 = IO.readPoint(IO.point1);
                Point point2 = bi.getIntruder(objectPerceptArrayList);

                if (possibleCapturePoints(predict(point1,point2,10)).size()!=0){
                    Point possibleCapturePoint = possibleCapturePoints(predict(point1,point2,10)).get(0);
                    System.out.println("The capture point is: --------------------");
                    printPointXY(possibleCapturePoint);
                    IO.writePoint1(IO.targetPoint,possibleCapturePoint);
                    IO.writeTime(IO.times,turnsRequired(possibleCapturePoint));

                    encapAction e = new encapAction(possibleCapturePoint);
                    if (debug) System.out.println(e.capture.get(trackSequence).getClass());


                    return e.capture.get(0);
                }

            }else {
                if (debug) System.out.println("fail to track, add buffer");
                detectIntruderSecond = false;
                addTrackBuffer(1);
            }

        }

        if (trackBuffer == 1){
            setTrackBuffer(0);
            detectIntruderFirst = false;
        }

        int times = IO.readTime(IO.times);

        if (detectIntruderFirst&&detectIntruderSecond&& trackSequence<times-1 && times!=0){

            if (debug) System.out.println("---------------------------Start Tracking--------------------------------");

            addTrackSequence(1);

            Point x = IO.readPoint(IO.targetPoint);

            encapAction e = new encapAction(x);

            if (debug) System.out.println("Track sequence is: "+trackSequence);

            if (debug) System.out.println(e.capture.get(trackSequence).getClass());

            return e.capture.get(trackSequence);

        }

        if (detectIntruderFirst&&detectIntruderSecond&&trackSequence==times-1){

            detectIntruderSecond=false;
            detectIntruderFirst=false;
            setTrackSequence(0);
        }



        Angle moveAngle = Angle.fromRadians(percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians() * Game._RANDOM.nextDouble());




        if (objectPerceptArrayList.size() == 0){
            System.out.println("No object in view");
        }else {
            if (needRotate(objectPerceptArrayList)){

                actionHistory.add(new ActionHistory(2,moveAngle.getDegrees()));
                return new Rotate(moveAngle);
            }
        }


        if(!percepts.wasLastActionExecuted())
        {
            actionHistory.add(new ActionHistory(2,moveAngle.getDegrees()));
            return new Rotate(moveAngle);

        }
        actionHistory.add(new ActionHistory(1,percepts.getScenarioGuardPercepts().getMaxMoveDistanceGuard().getValue() * modifier));
        return new Move(new Distance(percepts.getScenarioGuardPercepts().getMaxMoveDistanceGuard().getValue() * modifier));

    }



    public boolean needRotate(ArrayList<ObjectPercept> objectPerceptArrayList){


        boolean flag = false;
        boolean val = false;


        for (int i = 0;i<objectPerceptArrayList.size();i++){

            double x = objectPerceptArrayList.get(i).getPoint().getX();
            double y = objectPerceptArrayList.get(i).getPoint().getY();

            //if the point is solid, then, input = 1;
            if (!flag&&objectPerceptArrayList.get(i).getType().isSolid() && Math.abs(x)<0.8){
                val = true;
                flag = true;
            }

        }

        return val;

    }





    public  ArrayList<Point> predict(Point a, Point b,int numP){
        ArrayList<Point> val = new ArrayList<>();

        Built_In bi = new Built_In();
        double slope = bi.findSlope(a,b);

        double ax = a.getX();
        double ay = a.getY();

        double bx = b.getX();
        double by = b.getY();

        double xD = bx-ax;
        double yD = by-ay;

        double currentX = bx;
        double currentY = by;

        for (int i = 0;i<numP;i++){

            val.add(new Point(currentX+(i+1)*xD,currentY+(i+1)*yD));
            //currentX = currentX+xD;
            //currentY = currentY+xD;
        }

        return val;
    }

    public  Point findCapturePoint(ArrayList<Point> pp){
        double[] distanceArray = new double[pp.size()];

        for (int i = 0;i<distanceArray.length;i++){
            distanceArray[i] = pp.get(i).getDistanceFromOrigin().getValue();
        }

        int index = 0;

        for (int i = 0;i<distanceArray.length;i++){

            if (distanceArray[i]<distanceArray[index]){
                index = i;
            }

        }

        return pp.get(index);


    }

    public  void printPointXY(Point x){
        System.out.println("X is: "+ x.getX());
        System.out.println("Y is: "+ x.getY());
        System.out.println("------------------------------");


    }


    public  void printArraylist(ArrayList<Point> pp){
        for (int i = 0;i<pp.size();i++){

            System.out.println("X id: "+pp.get(i).getX());
            System.out.println("Y is: "+pp.get(i).getY());
            System.out.println("----------------------------");

        }
    }


    public int turnsRequired(Point x){

        double degree = x.getClockDirection().getDegrees();

        if (degree>200) degree = degree - 360;

        double absDegree = Math.abs(degree);

        int rotateTimes = (int)Math.ceil((absDegree)/(45.0));

       // if (debug) System.out.println("Angle is:  ----"+absDegree);

       // if(debug) System.out.println("rotate time is: "+rotateTimes);

        int moveTimes = (int)Math.ceil((x.getDistanceFromOrigin().getValue())/(1.40));

        //if (debug) System.out.println("Distance is:  ----"+x.getDistanceFromOrigin().getValue());



        //if(debug) System.out.println("move time is: "+moveTimes);


        return rotateTimes+moveTimes;
    }

    public  ArrayList<Point> possibleCapturePoints(ArrayList<Point> points){
        int[] turnsRequire = new int[points.size()];

        for (int i = 0;i<turnsRequire.length;i++){

            turnsRequire[i] = turnsRequired(points.get(i));

        }


        ArrayList<Point> val = new ArrayList<>();

        for (int i = 0;i<turnsRequire.length;i++){

            if (turnsRequire[i]<=(i+1)){
                val.add(points.get(i));
            }

        }


        return val;


    }

    public void setTrackSequence(int val){
        trackSequence = val;
    }

    public void addTrackSequence(int val){
        trackSequence = trackSequence + val;
    }

    public int getTrackSequence(){
        return  trackSequence;
    }

    public void setTrackBuffer(int val){
        trackBuffer = val;
    }

    public void addTrackBuffer(int val)
    {
        trackBuffer = trackBuffer + val;

    }

    public int getTrackBuffer(){
        return trackBuffer;
    }

    public void printActionList(ArrayList<GuardAction> list){
        for (int i = 0;i< list.size();i++){
            System.out.println("The action is: "+list.get(i).getClass());
        }

    }

    public static void main(String[] args) {
        Point p1 = new Point(0,-1.4);
        Point p2 = new Point( 0,1.3);



    }

}


class Txt{

    public  String track= "src/main/resources/encap.txt";
    public  String point1= "src/main/resources/point1.txt";
    public  String targetPoint= "src/main/resources/targetPoint.txt";
    public  String times= "src/main/resources/times.txt";

    public  void writePoint1(String fileName,Point x){

        try{
            File writename = new File(fileName);

            BufferedWriter out = new BufferedWriter(new FileWriter(writename));

            out.write(""+x.getX()+"\r\n");
            out.write(""+x.getY()+"\r\n");

            out.flush();

            out.close();
        }catch (Exception e){
            e.printStackTrace();
        }




    }

    public void writeTime(String fileName,int i){

        try{
            File writename = new File(fileName);

            BufferedWriter out = new BufferedWriter(new FileWriter(writename));

            out.write(""+i+"\r\n");

            out.flush();

            out.close();
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public void writeActionSequence(String fileName,double[] array){
        try{
            File writename = new File(fileName);

            BufferedWriter out = new BufferedWriter(new FileWriter(writename));


            for (int i = 0;i<array.length;i++){
                out.write(""+array[i]+"\r\n");
            }


            out.flush();

            out.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Integer readTime(String fileName){

        File file = new File(fileName);
        BufferedReader reader = null;

        int ii = -1;

        try {

            reader = new BufferedReader(new FileReader(file));

            String tempString = null;

            int line = 0;

            while ((tempString = reader.readLine()) != null) {

                int i = Integer.parseInt(tempString);

                ii = i;

                //System.out.println("line " + line + ": " + tempString);

                line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ii;


    }




    public Point readPoint(String fileName){
        File file = new File(fileName);
        BufferedReader reader = null;

        double[] array = new double[2];

        try {

            reader = new BufferedReader(new FileReader(file));

            String tempString = null;

            int line = 0;

            while ((tempString = reader.readLine()) != null) {

                Double i = Double.parseDouble(tempString);

                array[line] = i;

                //System.out.println("line " + line + ": " + tempString);

                line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Point point = new Point(array[0],array[1]);

        return point;


    }



    public static void main(String[] args) {
        double[] a = new double[3];
        a[0] = 0;
        a[1] = 1;
        a[2] = 2;
        Point x = new Point(1,2);
       // writePoint1(point1,x);


        Capturer cp = new Capturer();

        ArrayList<Integer> aa = new ArrayList<>();
        aa.add(1);
        aa.add(2);
        //System.out.println(aa.get(1));


        //System.out.println(readTime(times));

    }


}

class tttt{

    public static void main(String[] args) {
        Capturer cp = new Capturer();

        Built_In bi = new Built_In();



        Point point = new Point(2.124,
        2.5235);

        encapAction e = new encapAction(point);

       // cp.printActionList(e.capture);

        RL rt = new RL();


        Point p1 = new Point(3,2);
        Point p2 = new Point(-3.5,2);
        Point p3 = new Point(-2.7,5);
        Point p4 = new Point(-2.5,2);
        Point p5 = new Point(-5.6,3);
        Point p6 = new Point(-0.5,2);
        Point p7 = new Point(0.6,3);
        Point p8 = new Point(1.3,4);
        Point p9 = new Point(5.3,6);

        ObjectPercept objectPercept1 = new ObjectPercept(ObjectPerceptType.EmptySpace,p1);
        ObjectPercept objectPercept2 = new ObjectPercept(ObjectPerceptType.Wall,p2);
        ObjectPercept objectPercept3 = new ObjectPercept(ObjectPerceptType.Wall,p3);
        ObjectPercept objectPercept4 = new ObjectPercept(ObjectPerceptType.Door,p4);
        ObjectPercept objectPercept5 = new ObjectPercept(ObjectPerceptType.Wall,p5);
        ObjectPercept objectPercept6 = new ObjectPercept(ObjectPerceptType.Wall,p6);
        ObjectPercept objectPercept7 = new ObjectPercept(ObjectPerceptType.Wall,p7);
        ObjectPercept objectPercept8 = new ObjectPercept(ObjectPerceptType.Wall,p8);
        ObjectPercept objectPercept9 = new ObjectPercept(ObjectPerceptType.Teleport,p9);

        Direction direction =  Direction.fromRadians(2);

        SmellPercept smellPercept = new SmellPercept(SmellPerceptType.Pheromone5,new Distance(2));
        SmellPercept smellPercept1 = new SmellPercept(SmellPerceptType.Pheromone2,new Distance(2));
        SoundPercept soundPercept = new SoundPercept(SoundPerceptType.Yell,direction);

        ArrayList<ObjectPercept> p = new ArrayList<>();
        ArrayList<SmellPercept> smell = new ArrayList<>();

        ArrayList<SoundPercept> sound = new ArrayList<>();


        smell.add(smellPercept1);
        smell.add(smellPercept);
        sound.add(soundPercept);

        p.add(objectPercept1);
        p.add(objectPercept2);
        p.add(objectPercept3);
        p.add(objectPercept4);
        p.add(objectPercept5);
        p.add(objectPercept6);
        p.add(objectPercept7);
        p.add(objectPercept8);
        p.add(objectPercept9);

        RL rl = new RL();


        System.out.println(sound.size());

        ArrayList<ObjectPercept> a = rl.sortPoints(p);

        //cp.printArraylist(bi.getAllPoints(a));

        bi.printIntegerArray(rl.putPointAsInput(a,sound,smell));



       // cp.printArraylist(a);
//        System.out.println(aaa.size());



    }
}
