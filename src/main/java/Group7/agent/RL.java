package Group7.agent;

import Group7.Game;
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
import Interop.Percept.Vision.ObjectPerceptType;

import java.util.ArrayList;
import java.util.Set;

//This agent can avoid collision effectively.

public class RL implements Guard {

    ArrayList<ActionHistory> actionHistory;

    public boolean ss = false;

    public RL(){
        actionHistory = new ArrayList<>();
    }

    public int turns = 0;

    @Override
    public GuardAction getAction(GuardPercepts percepts) {
        Txt IO = new Txt();
        Built_In  bi = new Built_In();
        Capturer cp =new Capturer();

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


        if (objectPerceptArrayList.size() == 0){
            System.out.println("No object in view");
        }else {
           if (needRotate(objectPerceptArrayList)){

               Angle RotateAngle = properAngle(objectPerceptArrayList);

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

    public Angle properAngle(ArrayList<ObjectPercept> objectPerceptArrayList){

        //TODO: using neural network as approximation function to implement RL


        return Angle.fromDegrees(45);
    }


    public int[] putPointAsInput( ArrayList<ObjectPercept> objectPerceptArrayList,ArrayList<SoundPercept> soundPerceptArrayList,ArrayList<SmellPercept> smellPerceptArrayList){

        int size = objectPerceptArrayList.size();

        int[] input = new int[size+2];

        Built_In bi = new Built_In();

        ArrayList<ObjectPercept> orderPoints = sortPoints(objectPerceptArrayList);

        boolean flag1 = false;

        boolean flag2 = false;

        for (int i = 0;i<orderPoints.size();i++){

            //if the point is solid, then, input = 1;
            if (orderPoints.get(i).getType().isSolid()){
                input[i]=1;
            }

        }

        if (soundPerceptArrayList.size()!=0){


            for (int i = 0;i<soundPerceptArrayList.size();i++){

                if (!flag1 && soundPerceptArrayList.get(i).getType().equals(SoundPerceptType.Yell)){

                    flag1 = true;
                    input[size] = 1;

                }else if (!flag1 && soundPerceptArrayList.get(i).getType().equals(SoundPerceptType.Noise)){

                    flag1 = true;
                    input[size] = 2;
                }
            }

        }

        if (smellPerceptArrayList.size()!=0){

            for (int i = 0;i<smellPerceptArrayList.size();i++){
                if (!flag2&&smellPerceptArrayList.get(i).getType().equals(SmellPerceptType.Pheromone1)){
                    flag2 = true;
                    input[size+1] = 1;
                }else if (!flag2&&smellPerceptArrayList.get(i).getType().equals(SmellPerceptType.Pheromone2)){
                    input[size+1] = 2;
                    flag2 = true;
                }else if (!flag2&&smellPerceptArrayList.get(i).getType().equals(SmellPerceptType.Pheromone3)){
                    input[size+1] = 3;
                    flag2 = true;
                }else if (!flag2&&smellPerceptArrayList.get(i).getType().equals(SmellPerceptType.Pheromone4)){
                    input[size+1] = 4;
                    flag2 = true;
                }else if (!flag2&&smellPerceptArrayList.get(i).getType().equals(SmellPerceptType.Pheromone5)){
                    input[size+1] = 5;
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
