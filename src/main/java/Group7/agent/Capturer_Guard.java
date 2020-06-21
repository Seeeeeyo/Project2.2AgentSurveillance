package Group7.agent;

import Group7.Game;
import Group7.agent.NeuralNetwork.NeuralNetwork;
import Interop.Action.*;
import Group7.map.objects.Wall;
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

public class Capturer_Guard implements Guard {

    public static boolean debug = true;

    public boolean detectIntruderFirst = false;

    public boolean detectIntruderSecond = false;

    private int trackSequence = 0;

    private int trackBuffer = 0;

    ArrayList<ActionHistory> actionHistory;

    public boolean dropPheromone1 = false;
    public boolean dropPheromone2 = false;
    public boolean dropPheromone3 = false;
    public boolean dropPheromone4 = false;
    public boolean dropPheromone5 = false;

    public boolean foundTarget = false;
    public boolean foundDoor = false;
    public boolean avoid1 = false;
    public boolean avoid2 = false;

    public int sentryCounter = 0;

    public int targetDCounter = 0;

    public int doorCounter = 0;

    public int index;

    public Capturer_Guard(){
        actionHistory = new ArrayList<>();
    }


    public Capturer_Guard(int index){
        this.index = index;
        actionHistory = new ArrayList<>();
    }


    @Override
    public GuardAction getAction(GuardPercepts percepts) {

//        System.out.println("The index for agnet is:   " +index);
//        System.out.println("-----------------------------");

        double numberOfGuard = 3;

        double degreePiece = 360.0/numberOfGuard;

        switch (index){
            case 0:
        }

        Txt IO = new Txt();
        Built_In bi = new Built_In();

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


        //if detect yell, prioritize to go there to help
        if (bi.hasDetectYell(soundPerceptArrayList,SoundPerceptType.Yell)){
            Angle angle = bi.getYellDirection(soundPerceptArrayList);
            return new Rotate(angle);
        }

        //if here noise, rotate to try to track
        if (!detectIntruderFirst && !detectIntruderSecond &&bi.hasDetectYell(soundPerceptArrayList,SoundPerceptType.Noise)){
            int rnd = -1;

            if (Math.random() > 0.5){
                rnd = 1;
            }
            Angle angle = Angle.fromDegrees(rnd*45);

            return new Rotate(angle);
        }

        if (!detectIntruderFirst&&!detectIntruderSecond){
            if (bi.hasObjectInView(objectPerceptArrayList, ObjectPerceptType.Intruder)){
                Point point = bi.getIntruder(objectPerceptArrayList);


                IO.writePoint1(IO.point1,point);


                if (debug) System.out.println("---------------------------First found Intruder, stay to find--------------------------------");
                detectIntruderFirst = true;


                return new Yell();
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

        if (detectIntruderFirst&&detectIntruderSecond&& trackSequence<times-2 && times!=0){

            if (debug) System.out.println("---------------------------Start Tracking--------------------------------");

            addTrackSequence(1);

            Point x = IO.readPoint(IO.targetPoint);

            encapAction e = new encapAction(x);

            if (debug) System.out.println("Track sequence is: "+trackSequence);

            if (debug) System.out.println(e.capture.get(trackSequence).getClass());

            return e.capture.get(trackSequence);

        }

        if (detectIntruderFirst&&detectIntruderSecond&&trackSequence==times-2){

            detectIntruderSecond=false;
            detectIntruderFirst=false;
            setTrackSequence(0);
        }



        Angle moveAngle = Angle.fromRadians(percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians() * Game._RANDOM.nextDouble());

        //if detect guards yell, then rotate to go there
        if (!detectIntruderFirst && !detectIntruderSecond && bi.hasDetectYell(soundPerceptArrayList, SoundPerceptType.Yell)){
            Angle yellRotate= bi.getYellDirection(soundPerceptArrayList);

            actionHistory.add(new ActionHistory(2,yellRotate.getDegrees()));
            return new Rotate(yellRotate);
        }


        //try not to enter the shaded Area
        if (!detectIntruderFirst && !detectIntruderSecond && bi.hasObjectInView(objectPerceptArrayList,ObjectPerceptType.ShadedArea)){
            return new Rotate(Angle.fromDegrees(45));
        }

        if (objectPerceptArrayList.size() == 0){
            System.out.println("No object in view");
        }else {
            if (needRotate(objectPerceptArrayList)){

                actionHistory.add(new ActionHistory(2,moveAngle.getDegrees()));
                return new Rotate(moveAngle);
            }
        }

        if (!detectIntruderFirst && !detectIntruderSecond &&bi.hasObjectInView(objectPerceptArrayList,ObjectPerceptType.Teleport)&&!dropPheromone1){
            dropPheromone1 = true;
            return new DropPheromone(SmellPerceptType.Pheromone1);
        }

        if (targetDCounter == 10){
            targetDCounter = 0;
            foundTarget = false;
        }

        if (foundTarget){
            targetDCounter++;
        }

        if (!detectIntruderFirst && !detectIntruderSecond &&bi.hasObjectInView(objectPerceptArrayList,ObjectPerceptType.TargetArea) && !foundTarget){
            foundTarget = true;
            targetDCounter++;
            return new DropPheromone(SmellPerceptType.Pheromone3);
        }

        if (foundDoor){
            doorCounter++;
        }

        if (doorCounter == 10){
            doorCounter = 0;
            foundDoor = false;
        }

        if (!detectIntruderFirst && !detectIntruderSecond &&bi.hasObjectInView(objectPerceptArrayList,ObjectPerceptType.Door) && !foundDoor){
            foundDoor = true;
            doorCounter++;
            return new DropPheromone(SmellPerceptType.Pheromone3);
        }

        if (!detectIntruderFirst && !detectIntruderSecond &&percepts.getAreaPercepts().isInSentryTower() && sentryCounter == 5){
            sentryCounter = 0;
            return new DropPheromone(SmellPerceptType.Pheromone2);
        }

        if (index == 3){
            if (percepts.getAreaPercepts().isInSentryTower()){
                sentryCounter++;
                return new Rotate(Angle.fromDegrees(45));
            }
        }




        if (bi.hasDetectSmell(smellPerceptArrayList,SmellPerceptType.Pheromone1) && !avoid1){
            avoid1 = true;
            System.out.println("Smell pheromone1, there is already agent entered teleport, avoid");
            return new Rotate(Angle.fromDegrees(45));
        }

        if (bi.hasDetectSmell(smellPerceptArrayList,SmellPerceptType.Pheromone2) && !avoid2){
            avoid2 = true;
            System.out.println("Smell pheromone2, there is already agent in sentry Tower, avoid");
            return new Rotate(Angle.fromDegrees(45));
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

class tit {


    public static String hiddenWeightPath1 = "src/main/resources/hiddenWeight1.txt";
    public static String hiddenWeightPath2 = "src/main/resources/hiddenWeight2.txt";
    public static String hiddenWeightPath3 = "src/main/resources/hiddenWeight3.txt";
    public static String hiddenWeightPath4 = "src/main/resources/hiddenWeight4.txt";
    public static String hiddenWeightPath5 = "src/main/resources/hiddenWeight5.txt";
    public static String hiddenWeightPath6 = "src/main/resources/hiddenWeight6.txt";
    public static String hiddenWeightPath7 = "src/main/resources/hiddenWeight7.txt";
    public static String hiddenWeightPath8 = "src/main/resources/hiddenWeight8.txt";
    public static String hiddenWeightPath9 = "src/main/resources/hiddenWeight9.txt";
    public static String hiddenWeightPath10 = "src/main/resources/hiddenWeight10.txt";
    public static String hiddenWeightPath11 = "src/main/resources/hiddenWeight11.txt";
    public static String hiddenWeightPath12 = "src/main/resources/hiddenWeight12.txt";

    public static String inputWeightPath1 = "src/main/resources/inputWeight1.txt";
    public static String inputWeightPath2 = "src/main/resources/inputWeight2.txt";
    public static String inputWeightPath3 = "src/main/resources/inputWeight3.txt";
    public static String inputWeightPath4 = "src/main/resources/inputWeight4.txt";
    public static String inputWeightPath5 = "src/main/resources/inputWeight5.txt";
    public static String inputWeightPath6 = "src/main/resources/inputWeight6.txt";
    public static String inputWeightPath7 = "src/main/resources/inputWeight7.txt";
    public static String inputWeightPath8 = "src/main/resources/inputWeight8.txt";
    public static String inputWeightPath9 = "src/main/resources/inputWeight9.txt";
    public static String inputWeightPath10 = "src/main/resources/inputWeight10.txt";
    public static String inputWeightPath11 = "src/main/resources/inputWeight11.txt";
    public static String inputWeightPath12 = "src/main/resources/inputWeight12.txt";

    public static String biasWeightPath1 = "src/main/resources/biasWeight1.txt";
    public static String biasWeightPath2 = "src/main/resources/biasWeight2.txt";
    public static String biasWeightPath3 = "src/main/resources/biasWeight3.txt";
    public static String biasWeightPath4 = "src/main/resources/biasWeight4.txt";
    public static String biasWeightPath5 = "src/main/resources/biasWeight5.txt";
    public static String biasWeightPath6 = "src/main/resources/biasWeight6.txt";
    public static String biasWeightPath7 = "src/main/resources/biasWeight7.txt";
    public static String biasWeightPath8 = "src/main/resources/biasWeight8.txt";
    public static String biasWeightPath9 = "src/main/resources/biasWeight9.txt";
    public static String biasWeightPath10 = "src/main/resources/biasWeight10.txt";
    public static String biasWeightPath11 = "src/main/resources/biasWeight11.txt";
    public static String biasWeightPath12 = "src/main/resources/biasWeight12.txt";

    public static void main(String[] args) {


//        RL rl = new RL();
//        NeuralNetwork n = new NeuralNetwork();
//        t.writeDecision(t.decision,3);
//        System.out.println(t.readTime(t.decision));


        double[] input = new double[19];

        for (int i = 0; i < input.length; i++) {

            input[i] = 1;
        }


    }
}

class Txt{

    public  String track= "src/main/resources/encap.txt";
    public  String point1= "src/main/resources/point1.txt";
    public  String targetPoint= "src/main/resources/targetPoint.txt";
    public  String times= "src/main/resources/times.txt";
    public  String goalDirection= "src/main/resources/goalDirection.txt";
    public  String direction= "src/main/resources/direction.txt";
    public  String times2 = "src/main/resources/times2.txt";
    public  String decision = "src/main/resources/Decision.txt";
    public  String input = "src/main/resources/Input.txt";

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

    public void writeDirection(String fileName,int direction){

        try{
            File writename = new File(fileName);

            BufferedWriter out = new BufferedWriter(new FileWriter(writename));

            out.write(""+direction+"\r\n");

            out.flush();

            out.close();
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public void writeGoalDirection(String fileName,int goalDirection){

        try{
            File writename = new File(fileName);

            BufferedWriter out = new BufferedWriter(new FileWriter(writename));

            out.write(""+goalDirection+"\r\n");

            out.flush();

            out.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void writeDecision(String fileName,int goalDirection){

        try{
            File writename = new File(fileName);

            BufferedWriter out = new BufferedWriter(new FileWriter(writename));

            out.write(""+goalDirection+"\r\n");

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

    public void writeInput(String fileName,double[] array){
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

    public Integer readDirection(String fileName){

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

    public  double[] readInput(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;

        NeuralNetwork nn = new NeuralNetwork();

        double[] array = new double[nn.getInputVectorLength()];

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

        return array;

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


        Capturer_Guard cp = new Capturer_Guard();

        ArrayList<Integer> aa = new ArrayList<>();
        aa.add(1);
        aa.add(2);
        //System.out.println(aa.get(1));


        //System.out.println(readTime(times));

    }


}


