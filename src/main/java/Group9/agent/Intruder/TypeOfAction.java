package Group9.agent.Intruder;

import Interop.Geometry.Angle;

/**
 * When exploring the map, agent has to rotate, there are some types of rotation:
 *      1: avoid objects (need back)
 *      2: back to the original direction
 *      3:change explore direction
 *
 */
public class TypeOfAction {

    private double val;
    private int actionType;//rotate, move
    private int type;


    public TypeOfAction(int actionType, double val, int type){
        this.type = type;
        this.actionType = actionType;
        this.val = val;

    }


    public int getType(){
        return type;
    }

    public void setType(int val){
        type = val;
    }

    public double getVal(){
        return val;
    }

    public int getActionType(){
        return actionType;
    }

}

