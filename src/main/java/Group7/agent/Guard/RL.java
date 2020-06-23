package Group7.agent.Guard;
/**
 * @author: Tianchen Luo
 * Trying to using Neural Network learning the shape of wall and finding the best rotation angle to rotate and avoid collision
 * But performance is not good enough and time limited to improve this method, we give it up and chose to use greedy
 */

import Group7.Game;
import Group7.agent.Guard.Built_In;
import Group7.agent.Guard.Capturer_Guard;
import Group7.agent.Guard.Txt;
import Group7.agent.Guard.encapAction;
import Group7.agent.NeuralNetwork.NeuralNetwork;
import Interop.Action.*;
import Interop.Agent.Guard;
import Interop.Geometry.Angle;
import Interop.Geometry.Distance;
import Interop.Geometry.Point;
import Interop.Percept.GuardPercepts;
import Interop.Percept.Scenario.SlowDownModifiers;
import Interop.Percept.Smell.SmellPercept;
import Interop.Percept.Smell.SmellPerceptType;
import Interop.Percept.Sound.SoundPercept;
import Interop.Percept.Sound.SoundPerceptType;
import Interop.Percept.Vision.ObjectPercept;

import java.util.ArrayList;
import java.util.Set;

//This agent can avoid collision effectively.

public class RL implements Guard {

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

    ArrayList<ActionHistory> actionHistory;

    public boolean ss = false;

    public RL(){
        actionHistory = new ArrayList<>();
    }

    public int turns = 0;

    public boolean beginTraining = false;

    public boolean rotateFinish = true;

    public boolean needLeftRotate = false;
    public boolean needRightRotate = false;



    public int rotateTimes = 0;

    @Override
    public GuardAction getAction(GuardPercepts percepts) {
        Txt IO = new Txt();
        Built_In bi = new Built_In();
        Capturer_Guard cp =new Capturer_Guard();

        Angle moveAngle = Angle.fromRadians(percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians() * Game._RANDOM.nextDouble());

        SlowDownModifiers slowDownModifiers =  percepts.getScenarioGuardPercepts().getScenarioPercepts().getSlowDownModifiers();
        double modifier = 1;
        if (percepts.getAreaPercepts().isInWindow()){
            modifier = slowDownModifiers.getInWindow();
        }else if (percepts.getAreaPercepts().isInSentryTower()){
            modifier = slowDownModifiers.getInSentryTower();
        }else if (percepts.getAreaPercepts().isInDoor()){
            modifier = slowDownModifiers.getInDoor();
        }

        Set<ObjectPercept> objectPercepts = percepts.getVision().getObjects().getAll();
        Set<SoundPercept> soundPercepts = percepts.getSounds().getAll();
        Set<SmellPercept> smellPercepts = percepts.getSmells().getAll();
        ArrayList<ObjectPercept> objectPerceptArrayList = new ArrayList<ObjectPercept>(objectPercepts);
        ArrayList<SoundPercept> soundPerceptArrayList = new ArrayList<SoundPercept>(soundPercepts);
        ArrayList<SmellPercept> smellPerceptArrayList = new ArrayList<>(smellPercepts);


       if (rotateTimes == IO.readTime(IO.times)){
           needRightRotate = false;
           needLeftRotate = false;
           rotateTimes = 0;
       }


       if (needLeftRotate){
           rotateTimes++;
           return new Rotate(Angle.fromDegrees(-30));
       }

       if (needRightRotate){
           rotateTimes++;
           return new Rotate(Angle.fromDegrees(30));
       }



        if (beginTraining && !needLeftRotate && !needRightRotate){

            boolean good =!needRotate(objectPerceptArrayList);

            double[] input = IO.readInput(IO.input);
            if (good){
                trainNeuralNetwork(input,getReward(IO.readTime(IO.decision),good));
                beginTraining = false;
            }else {
                trainNeuralNetwork(input,getReward(IO.readTime(IO.decision),!good));
                beginTraining = false;
            }

            beginTraining = false;
        }







        //if last action is not accept, then we know the agent stuck need to rotate.
        if (!percepts.wasLastActionExecuted() && !beginTraining){

            beginTraining = true;

            double[] input = putPointAsInput(objectPerceptArrayList,soundPerceptArrayList,smellPerceptArrayList);

            //write down the situation for input
            IO.writeInput(IO.input,input);

            Angle RotateAngle = properAngle(objectPerceptArrayList,soundPerceptArrayList,smellPerceptArrayList);

            int choice = changeIntoDirection(RotateAngle);
            System.out.println("the choice is: "+ choice);

            int times = calculateTimes(RotateAngle);
            //System.out.println("Possible for training with rotate times "+times);

            IO.writeDecision(IO.decision,choice);

            IO.writeTime(IO.times,times);

            if (times<0){
                needLeftRotate = true;
            }else{
                needRightRotate = true;
            }

            if (needRightRotate){
                rotateTimes++;
                return new Rotate(Angle.fromDegrees(30));
            }

            if (needLeftRotate){
                rotateTimes++;
                return new Rotate(Angle.fromDegrees(-30));
            }

        }









        //When facing the situation need to train, record the current action, state. Using NN to choose
        if (!percepts.wasLastActionExecuted()){


                if (needRotate(objectPerceptArrayList) && rotateFinish){
                    rotateFinish = false;

                    double[] input = putPointAsInput(objectPerceptArrayList,soundPerceptArrayList,smellPerceptArrayList);

                    //write down the situation for input
                    IO.writeInput(IO.input,input);


                    //TODO: Finish complete Rotate
                    Angle RotateAngle = properAngle(objectPerceptArrayList,soundPerceptArrayList,smellPerceptArrayList);

                    int choice = changeIntoDirection(RotateAngle);

                    int times = calculateTimes(RotateAngle);
                    System.out.println("Possible for training with rotate times"+times);

                    IO.writeDecision(IO.decision,choice);
                    IO.writeTime(IO.times,times);

                    encapAction e = new encapAction(times);

                    rotateTimes++;

                    System.out.println("rotate sequence: "+rotateTimes);

                    beginTraining = true;

                    System.out.println(e.RLrotate.size());

                    if (times == 1) {
                        rotateFinish = true;
                        return new Rotate(Angle.fromDegrees(30));
                    }else if (times == -1){
                        rotateFinish = true;
                        return new Rotate(Angle.fromDegrees(-30));
                    }else {
                        return e.RLrotate.get(rotateTimes);
                    }

                    //actionHistory.add(new ActionHistory(2,moveAngle.getDegrees()));
                }
        }



//        if(!percepts.wasLastActionExecuted())
//        {
//            actionHistory.add(new ActionHistory(2,moveAngle.getDegrees()));
//            return new Rotate(moveAngle);
//
//        }
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
            if (!flag&&objectPerceptArrayList.get(i).getType().isSolid() && Math.abs(x)<0.8&&y<1.4){
                val = true;
                flag = true;
            }

        }

        return val;

    }


    //find the degree with best evaluation
    public Angle properAngle(ArrayList<ObjectPercept> objectPerceptArrayList,ArrayList<SoundPercept> soundPerceptArrayList,ArrayList<SmellPercept> smellPerceptArrayList){

        double[] input = putPointAsInput(objectPerceptArrayList,soundPerceptArrayList,smellPerceptArrayList);
        //TODO: using neural network as approximation function to implement RL
        NeuralNetwork n1 = new NeuralNetwork(input,1);
        NeuralNetwork n2 = new NeuralNetwork(input,2);
        NeuralNetwork n3 = new NeuralNetwork(input,3);
        NeuralNetwork n4 = new NeuralNetwork(input,4);
        NeuralNetwork n5 = new NeuralNetwork(input,5);
        NeuralNetwork n6 = new NeuralNetwork(input,6);
        NeuralNetwork n7 = new NeuralNetwork(input,7);
        NeuralNetwork n8 = new NeuralNetwork(input,8);
        NeuralNetwork n9 = new NeuralNetwork(input,9);
        NeuralNetwork n10 = new NeuralNetwork(input,10);
        NeuralNetwork n11 = new NeuralNetwork(input,11);
        NeuralNetwork n12 = new NeuralNetwork(input,12);

        double[] proba = new double[12];

        proba[0] = n1.forward();
        proba[1] = n2.forward();
        proba[2] = n3.forward();
        proba[3] = n4.forward();
        proba[4] = n5.forward();
        proba[5] = n6.forward();
        proba[6] = n7.forward();
        proba[7] = n8.forward();
        proba[8] = n9.forward();
        proba[9] = n10.forward();
        proba[10] = n11.forward();
        proba[11] = n12.forward();

        int index = 0;

        for (int i = 0;i<proba.length;i++){

//            if (proba[index]<proba[i]){
//                index = i;
//            }'
            System.out.println(i+" --- "+proba[i]);
        }

        System.out.println("The best choice with value: "+proba[index]+"and index is: "+index);

        //TODO: positive and negative

        if (index == 11){
            return Angle.fromDegrees(0);
        }

        if (index<=5){
            return Angle.fromDegrees(45*(index+1));
        }else {
            index = index - 11;
            return Angle.fromDegrees(45*(index));
        }



    }

    public void trainNeuralNetwork(double[] input,double[] reward){

        int trainTime = 20;

        NeuralNetwork n1 = new NeuralNetwork(input,reward[0],1,inputWeightPath1,biasWeightPath1,hiddenWeightPath1);
        NeuralNetwork n2 = new NeuralNetwork(input,reward[1],2,inputWeightPath2,biasWeightPath2,hiddenWeightPath2);
        NeuralNetwork n3 = new NeuralNetwork(input,reward[2],3,inputWeightPath3,biasWeightPath3,hiddenWeightPath3);
        NeuralNetwork n4 = new NeuralNetwork(input,reward[3],4,inputWeightPath4,biasWeightPath4,hiddenWeightPath4);
        NeuralNetwork n5 = new NeuralNetwork(input,reward[4],5,inputWeightPath5,biasWeightPath5,hiddenWeightPath5);
        NeuralNetwork n6 = new NeuralNetwork(input,reward[5],6,inputWeightPath6,biasWeightPath6,hiddenWeightPath6);
        NeuralNetwork n7 = new NeuralNetwork(input,reward[6],7,inputWeightPath7,biasWeightPath7,hiddenWeightPath7);
        NeuralNetwork n8 = new NeuralNetwork(input,reward[7],8,inputWeightPath8,biasWeightPath8,hiddenWeightPath8);
        NeuralNetwork n9 = new NeuralNetwork(input,reward[8],9,inputWeightPath9,biasWeightPath9,hiddenWeightPath9);
        NeuralNetwork n10 = new NeuralNetwork(input,reward[9],10,inputWeightPath10,biasWeightPath10,hiddenWeightPath10);
        NeuralNetwork n11 = new NeuralNetwork(input,reward[10],11,inputWeightPath11,biasWeightPath11,hiddenWeightPath11);
        NeuralNetwork n12 = new NeuralNetwork(input,reward[11],12,inputWeightPath12,biasWeightPath12,hiddenWeightPath12);


        n1.train(trainTime);
        n2.train(trainTime);
        n3.train(trainTime);
        n4.train(trainTime);
        n5.train(trainTime);
        n6.train(trainTime);
        n7.train(trainTime);
        n8.train(trainTime);
        n9.train(trainTime);
        n10.train(trainTime);
        n11.train(trainTime);
        n12.train(trainTime);

    }

    public double[] getReward(int choice, boolean good){
        double[] reward = new double[12];

        if (good){
            for (int i = 0;i<reward.length;i++){

                reward[i] = 0.5;

            }

            reward[choice] = 1;
        }else {

            for (int i = 0;i<reward.length;i++){

                reward[i] = 0.6;

            }
            reward[choice] = 0.5;
        }

        return reward;
    }


    public double[] putPointAsInput( ArrayList<ObjectPercept> objectPerceptArrayList,ArrayList<SoundPercept> soundPerceptArrayList,ArrayList<SmellPercept> smellPerceptArrayList){

        int size = objectPerceptArrayList.size();

        double[] input = new double[21];

        Built_In bi = new Built_In();

        ArrayList<ObjectPercept> orderPoints = sortPoints(objectPerceptArrayList);

        boolean flag1 = false;

        boolean flag2 = false;


    if (orderPoints.size()<=19){
        for (int i = 0;i<orderPoints.size();i++){

            //if the point is solid, then, input = 1;
            if (orderPoints.get(i).getType().isSolid()){
                input[i]=1;
            }

        }
    }else {
        for (int i = 0;i<19;i++){
            input[i] = 1;
        }
    }


        if (soundPerceptArrayList.size()!=0){


            for (int i = 0;i<soundPerceptArrayList.size();i++){

                if (!flag1 && soundPerceptArrayList.get(i).getType().equals(SoundPerceptType.Yell)){

                    flag1 = true;
                    input[19] = 0.5;

                }else if (!flag1 && soundPerceptArrayList.get(i).getType().equals(SoundPerceptType.Noise)){

                    flag1 = true;
                    input[19] = 1;
                }
            }

        }

        if (smellPerceptArrayList.size()!=0){

            for (int i = 0;i<smellPerceptArrayList.size();i++){
                if (!flag2&&smellPerceptArrayList.get(i).getType().equals(SmellPerceptType.Pheromone1)){
                    flag2 = true;
                    input[20] = 0.2;
                }else if (!flag2&&smellPerceptArrayList.get(i).getType().equals(SmellPerceptType.Pheromone2)){
                    input[20] = 0.4;
                    flag2 = true;
                }else if (!flag2&&smellPerceptArrayList.get(i).getType().equals(SmellPerceptType.Pheromone3)){
                    input[20] = 0.6;
                    flag2 = true;
                }else if (!flag2&&smellPerceptArrayList.get(i).getType().equals(SmellPerceptType.Pheromone4)){
                    input[20] = 0.8;
                    flag2 = true;
                }else if (!flag2&&smellPerceptArrayList.get(i).getType().equals(SmellPerceptType.Pheromone5)){
                    input[20] = 1;
                    flag2 = true;
                }
            }

        }


        return  input;

    }

    public int[] putPointAsInput( ArrayList<ObjectPercept> objectPerceptArrayList){

        int size = objectPerceptArrayList.size();

        int[] input = new int[size+2];

        ArrayList<ObjectPercept> orderPoints = sortPoints(objectPerceptArrayList);

        for (int i = 0;i<orderPoints.size();i++){

            //if the point is solid, then, input = 1;
            if (orderPoints.get(i).getType().isSolid()){
                input[i]=1;
            }

        }
        return  input;
    }

    public int changeIntoDirection(Angle angle){
        int val = 0;
        switch ((int)angle.getDegrees()){
            case 30:
                val = 1;
            case 60:
                val = 2;
            case 90:
                val = 3;
            case 120:
                val = 4;
            case 150:
                val = 5;
            case 180:
                val = 6;
            case 210:
                val = 7;
            case 240:
                val = 8;
            case 270:
                val = 9;
            case 300:
                val = 10;
            case 330:
                val = 11;
            case 360:
                val = 12;
        }

        return val;

    }

    public int calculateTimes(Angle angle){
        double val = angle.getDegrees();

        int re = 0;

        if (val<=180){
            re = (int)(val/30);
        }else {

            re =-(int) ((360-val)/30);

        }
        return re;
    }

    //make sure object arraylist has size greater than 0;
    public ArrayList<ObjectPercept> sortPoints( ArrayList<ObjectPercept> objectPerceptArrayList){

        ArrayList<ObjectPercept> aa = new ArrayList<>();

        ArrayList<ObjectPercept> vall = new ArrayList<>();



        ArrayList<Point> aaa = new ArrayList<>();

        ArrayList<Point> val = new ArrayList<>();

        for (int i = 0;i<objectPerceptArrayList.size();i++){

            aa.add(objectPerceptArrayList.get(i));
        }

       for (int i = 0;i<aa.size();i++){

           vall.add(findSmallestX(objectPerceptArrayList));
           objectPerceptArrayList.remove(findSmallestX(objectPerceptArrayList));


       }

        return vall;

    }

    public ObjectPercept findSmallestX (ArrayList<ObjectPercept> objectPerceptArrayList){

        ObjectPercept smalll = objectPerceptArrayList.get(0);

        for (int i = 0;i<objectPerceptArrayList.size();i++){


            if (smalll.getPoint().getX()>objectPerceptArrayList.get(i).getPoint().getX()){
                smalll = objectPerceptArrayList.get(i);
            }

        }

        return smalll;

    }

}
class tit {

    /**
     * using to initialize the weight
     */

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
