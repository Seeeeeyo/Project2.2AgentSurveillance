package Group7.agent.Intruder;

import Group7.Game;
import Interop.Action.IntruderAction;
import Interop.Action.Rotate;
import Interop.Agent.Intruder;
import Interop.Geometry.Angle;
import Interop.Percept.IntruderPercepts;
import Interop.Percept.Scenario.SlowDownModifiers;

import java.util.ArrayList;

public class GeneticAgent implements Intruder
{

    private MindMap map = new MindMap();

    //The array list for the moving history of every turns.
    ArrayList<ActionHistory> actionHistory;

    @Override
    public IntruderAction getAction(IntruderPercepts percepts) {
        System.out.println();
        System.out.println("GeneticAgent.getAction");

        map.updateGridMap(percepts);
        map.computeTargetPoint(percepts.getTargetDirection());

        if(!percepts.wasLastActionExecuted())
        {
            System.out.println("GeneticAgent.getAction rejected");

            Angle random_rotation_angle =  Angle.fromRadians(percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians() * Game._RANDOM.nextDouble());
            System.out.println("rotation_angle = " + random_rotation_angle.getRadians());
            actionHistory.add(new ActionHistory(2,random_rotation_angle.getDegrees()));
            return new Rotate(random_rotation_angle);
        }


        GeneticAlgorithm.computePath(map,percepts);

       return null;
    }

    private double getSpeedModifier(IntruderPercepts guardPercepts)
    {
        SlowDownModifiers slowDownModifiers =  guardPercepts.getScenarioIntruderPercepts().getScenarioPercepts().getSlowDownModifiers();
        if(guardPercepts.getAreaPercepts().isInWindow())
        {
            return slowDownModifiers.getInWindow();
        }
        else if(guardPercepts.getAreaPercepts().isInSentryTower())
        {
            return slowDownModifiers.getInSentryTower();
        }
        else if(guardPercepts.getAreaPercepts().isInDoor())
        {
            return slowDownModifiers.getInDoor();
        }

        return 1;
    }



    class ActionHistory{

        int type;
    /*
    1: move
    2: rotate
    3: NoAction
    4: Yell
    5: DropPheromone
     */

        double val;

        public ActionHistory(int type, double val){
            this.type = type;

            if (type == 3||type == 4){
                val = 0;
            }else {
                this.val = val;
            }
        }
}
}
