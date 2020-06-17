package Group7.agent.Intruder;

import Group7.Game;
import Interop.Action.*;
import Interop.Agent.Guard;
import Interop.Agent.Intruder;
import Interop.Geometry.Angle;
import Interop.Geometry.Distance;
import Interop.Geometry.Point;
import Interop.Percept.GuardPercepts;
import Interop.Percept.IntruderPercepts;
import Interop.Percept.Scenario.SlowDownModifiers;
import Interop.Percept.Smell.SmellPerceptType;
import Interop.Percept.Vision.ObjectPercept;
import Interop.Percept.Vision.ObjectPerceptType;

import java.util.*;

/**
 * sequence of update:
 *
 * 1:update current position, first is angle.
 * 2:update map.
 * 3:making decisions.
 *
 *
 * Pay attention that Math.sin using radians, not degree
 */

public class MapAgentAs implements Intruder {


    public boolean debug = true;
    //----map parameter---
    final public double itself = 99;

    final public double unknownPlace = 0;

    final public double emptySpace = 11;

    final public double teleport = 22;

    final public double window = 33;

    final public double door = 44;

    final public double sentryTower = 55;

    final public double targetPlace = 66;

    final public double shadedArea = 77;

    final public double intruder = 88;

    final public double wall = 99;

    final public double guard = 10;

    //matrix for the map, needed be updated every turn
    public double[][] map;

    //summation of rotation angle.
    private double currentAngle;

    //The array list for the moving history of every turns.
    ArrayList<ActionHistory> actionHistory;

    //First element is row index, second is column index
    public double[] currentPosition;

    public double[] targetPosition;

    public int xx;

    public  int yy;

    public int turns = 0;

    public MapAgentAs(int height, int width){

        map = new double[height*2][width*2];

        map[height][width] = 10000;

        currentPosition = new double[2];
        targetPosition = new double[2];
        targetPosition[0] = height+40;
        targetPosition[1] = width-100;

        currentPosition[0] = height;
        currentPosition[1] = width;

        currentAngle = 0;

        xx = height;
        yy = width;

        actionHistory = new ArrayList<>();
        actionHistory.add(new ActionHistory(2,0));
    }



    public IntruderAction getAction(IntruderPercepts percepts) {
        System.out.println();
        System.out.println("AstarAgent.getAction");

        Set<ObjectPercept> objectPercepts = percepts.getVision().getObjects().getAll();
        ArrayList<ObjectPercept> objectPerceptArrayList = new ArrayList<ObjectPercept>(objectPercepts);

        updateCurrentAngle();
        updateCurrentPosition();
        printCurrentPosition();

        if (debug) System.out.println("Agent's Angle is: "+currentAngle);
        updateMap(objectPerceptArrayList);


        ArrayList<Integer> listOfActions = computePath();
        System.out.println("listOfActions = " + listOfActions);

        if(listOfActions.size() ==0){
            System.out.println("no path found");
            actionHistory.add(new ActionHistory(3,1));
            return new NoAction();
        }
        int astar_move = listOfActions.get(0);
        System.out.println("astar_move = " + astar_move);

        if(!percepts.wasLastActionExecuted())
        {
            System.out.println("AstarAgent.getAction rejected");

            Angle random_rotation_angle =  Angle.fromRadians(percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians() * Game._RANDOM.nextDouble());
            System.out.println("rotation_angle = " + random_rotation_angle.getRadians());
            actionHistory.add(new ActionHistory(2,random_rotation_angle.getDegrees()));
            return new Rotate(random_rotation_angle);
        }

        System.out.println("last action accepted");

        double max_dist = percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder().getValue() * getSpeedModifier(percepts);


        IntruderAction out_action = doAction(astar_move,currentAngle,percepts);

        if(out_action ==null){
            System.out.println("Error ");
            return new NoAction();
        }

        return out_action;
//        }

    }


    private static final int[][] moves =  // list of all the possible moves
            {{-1, 0},
                    {1,0},
                    {0,1},
                    {0,-1}
            };

    /**
     *
     * @return a list of (x,y) coordinates from first to last move
     */

    public ArrayList<Integer> computePath(){
        int[][] searchStates = walkable();

        List<int[]> listOfPositions = new ArrayList<>();

        int[] initialState = new int[]{(int)Math.round(currentPosition[0]),(int)Math.round(currentPosition[1])};
        System.out.println("initialState = " + initialState[0]+", "+initialState[1]);
        searchStates[initialState[0]][initialState[1]] = 1; // the robot is there at the start so no need to explore it again

//       printMatrix(searchStates);
        int[] target = new int[]{(int)Math.round(targetPosition[0]),(int)Math.round(targetPosition[1])};

        System.out.println("target: x = " + target[0]+", y = " +target[1]);

        // add the 4 actions
        ArrayList<int []> states = new ArrayList<>();

        Point a = new Point(initialState[0],initialState[1]);
        Point b = new Point(target[0], target[1]);
        Distance dist = new Distance(a,b);

        states.add(new int[] {(int)dist.getValue(),1,initialState[0],initialState[1]});

        // create a matrix of actions
        int[][] actions = new int[searchStates.length][searchStates[0].length];
        int count = 0;
        boolean stop = false;

        while(states.size()>0 && !stop) {
            count++;
            // to sort the state list regarding their cost. The cost is the first value
            states.sort(new Comparator<int[]>() {
                @Override
                public int compare(int[] o1, int[] o2) {
                    return Integer.compare(o1[0], o2[0]);
                }
            });

            int[] checkedState = states.remove(0);

            for (int i = 0; i < moves.length; i++) { // to check the 4 different moves

                int possibleNewX = checkedState[2] + moves[i][0]; // new x coordinate after one of the 4 moves
                int possibleNewY = checkedState[3] + moves[i][1]; // new y coordinate after one of the 4 moves

                int instantxdiff = 0;
                int instantydiff = 0;
//                int xdiff = mindMap.getState().getX() - initialState[0];
//                int ydiff = mindMap.getState().getY() - initialState[1];
int xdiff =0;
int ydiff = 0;


                possibleNewX += xdiff; // new x coordinate after one of the 4 moves
                possibleNewY += ydiff; // new y coordinate after one of the 4 moves

//                // expand in the x direction if needed  to the bottom
//                if (searchStates.length <= possibleNewX) {
//                    actions = expandBottom(actions);
//                    searchStates = expandBottom(searchStates);
//                    mindMap.expandBottom(1);
////                   System.out.println("Expended");
////                   printMatrix(searchStates);
//                }
//
//                // expand in the x direction if needed  to the top
//                if (possibleNewX < 0) {
//                    actions = expandTop(actions);
//                    searchStates = expandTop(searchStates);
//                    mindMap.expandTop(1);
//                    possibleNewX = 0;
//                    instantxdiff = 1;
////                   System.out.println("Expended");
////                   printMatrix(searchStates);
//                }
//
//                // expand in the y direction if needed to the right
//                if (searchStates[0].length <= possibleNewY) {
//                    actions = expandRight(actions);
//                    searchStates = expandRight(searchStates);
//                    mindMap.expandRight(1);
////                   System.out.println("Expended");
////                   printMatrix(searchStates);
//                }
//
//                // expand in the y direction if needed to the left
//                if (possibleNewY < 0) {
//                    actions = expandLeft(actions);
//                    searchStates = expandLeft(searchStates);
//                    mindMap.expandLeft(1);
//                    possibleNewY = 0;
//                    instantydiff = 1;
////                   System.out.println("Expended");
////                   printMatrix(searchStates);
//                }

                if (target[0] == possibleNewX - xdiff && target[1] == possibleNewY - ydiff) { // checks if the agent is in the target area
                    actions[possibleNewX][possibleNewY] = i + 1;

                    stop = true;
                }
                if (!stop) {
                    if (searchStates[possibleNewX][possibleNewY] == 0) { // if unvisited and walkable
                        Point pointA = new Point(target[0],target[1]);
                        Point pointB = new Point(possibleNewX-xdiff-instantxdiff, possibleNewY-ydiff-instantydiff);
                        Distance euclDistance = new Distance(pointA,pointB);
                        int cost = (int) Math.floor(euclDistance.getValue()); // cost of this move, equal to 1 here since only move of 1 case
                        int newCost = cost+checkedState[1]; // new cost = addition of the previous cost (previous moves) and this move
                        //System.out.println("newCost = " + newCost);
                        int[] newState = {newCost,checkedState[1]+1, possibleNewX - xdiff - instantxdiff, possibleNewY - ydiff - instantydiff}; // potential new state
                        searchStates[possibleNewX][possibleNewY] = 1; // no need to explore anymore
                        states.add(newState);
                        actions[possibleNewX][possibleNewY] = i + 1;
                        //System.out.println("add " + newCost + " " + (possibleNewX - xdiff - instantxdiff) + " " + (possibleNewY - ydiff - instantydiff));
                        //  printMatrix(searchStates);
                   /*
                   i = 1 is going to the top
                   i = 2 is going to the bottom
                   i = 3 is going to the right
                   i = 4 is going to the left
                    */
                    }
                }
            }
        }

        // MindMap.printMatrix(actions,mindMap.getTargetPos(),mindMap.getState().getPos());

//        int xdiff = mindMap.getState().getX() - initialState[0];
        int xdiff = 0;
        // System.out.println("xdiff = " + xdiff);
//        int ydiff = mindMap.getState().getY() - initialState[1];
        int ydiff = 0;
        // System.out.println("ydiff = " + ydiff);

        //int xdiff = mindMap.getState().getX() - target[0];
        //int ydiff = mindMap.getState().getY() - target[1];

        int xCoorTarget = target[0] +xdiff;
        int yCoorTarget = target[1] +ydiff;
        //  actions[xCoorTarget][yCoorTarget] = 9; // 9 is an arbitrary value set to identify the target point
        int x = xCoorTarget;
        int y = yCoorTarget;
        //int x = mindMap.getState().getX();
        //int y = mindMap.getState().getY();
        //actions[xCoorTarget][yCoorTarget] = 9; // 9 is an arbitrary value set to identify the target point
        int[] lastPosition = {x,y};

        listOfPositions.add(lastPosition);

        ArrayList<Integer> list_of_moves = new ArrayList<>();

        while( x != (initialState[0]+xdiff) || y != (initialState[1]+ydiff)){
            list_of_moves.add(actions[x][y]);

            //  System.out.println("action coord x = " + (moves[actions[x][y]-1][0]));
            // System.out.println("action coord y = " + (moves[actions[x][y]-1][1]));

            int x2 = x - moves[actions[x][y]-1][0];
            int y2 = y - moves[actions[x][y]-1][1];

            int[] position = {x2,y2};
            listOfPositions.add(position);

            x = x2;
            y = y2;
            // System.out.println("x = " + x);
            // System.out.println("y = " + y);
        }


        //Collections.reverse(listOfPositions); // inverse the position order so the first index of the array is the first position
//               for (int j = 0; j < listOfPositions.size(); j++) {
        //   System.out.println("x position of " + j + "th move = " + listOfPositions.get(j)[0]);
        //  System.out.println("y position of " + j + "th move = " + listOfPositions.get(j)[1]);
        //  System.out.println(" ----------- ");
//               }
//       return listOfPositions;
        Collections.reverse(list_of_moves); // inverse the position order so the first index of the array is the first position
        return list_of_moves;
    }

    private IntruderAction doAction( int nb, double current_angle, IntruderPercepts percepts){
        double angle = 0;
        switch (nb){
            case 1:
                angle = 270;
                break;
            case 2:
                angle = 90;
                break;
            case 3:
                angle = 0;
                break;
            case 4:
                angle = 180;
                break;
        }

        if(current_angle == angle){
            double max_dist = percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder().getValue()*getSpeedModifier(percepts);
            if(max_dist>1){
                max_dist=1;
            }
            System.out.println("Move "+max_dist);
            actionHistory.add(new ActionHistory(1,max_dist));
            return new Move(new Distance(max_dist));
        }else{
            return rotateTo(angle,percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle());
        }
    }

    private Rotate rotateTo(double a, Angle max_rotation){

        double old =currentAngle;
        Angle rotation_angle = Angle.fromDegrees(a-old);


        if(rotation_angle.getDegrees() > max_rotation.getDegrees()){
            rotation_angle =max_rotation;
        }else if(rotation_angle.getDegrees() < -max_rotation.getDegrees()){
            rotation_angle = max_rotation;
            rotation_angle = Angle.fromRadians(-rotation_angle.getRadians());
        }
        System.out.println("Rotate to "+a+"; old angle is "+old+"; new angle is "+(old+rotation_angle.getDegrees()));
        actionHistory.add(new ActionHistory(2,rotation_angle.getDegrees()));
        return new Rotate(rotation_angle);
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


    /**
     * Method to check the walkable areas so it doesn't go on the walls etc
     * @return 0 if can walk on a "case" of the map and 1 if it can not because there is an obstacle there
     */
    public int[][] walkable(){
        double[][] mapData =map;
        int[][] out = new int[mapData.length][mapData[0].length];

        for (int i = 0; i < mapData.length; i++) {
            for (int j = 0; j < mapData[0].length ; j++) {
                if(mapData[i][j]==wall){
                    out[i][j] = 1;
                }
            }
        }
        return out;
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

        /**
         * debug method to see the action
         */
        public void printAction(){

            System.out.println("------------------The action you are checking is:---------------------");
            switch (type){
                case 1: System.out.println("Action is Move, with value:  "+val);
                    break;

                case 2: System.out.println("Action is Rotate, with value:  "+val);
                    break;

                case 3: System.out.println("Action is NoAction, with value: "+val);
                    break;

                case 4: System.out.println("Action is Yell, with value: "+val);
                    break;

                case 5: System.out.println("Action is Drop_Pheromone, with Pheromone Type:  "+val);
                    break;
            }


        }


    }


    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public void printMatrix(double[][] matrix){
        for (int i = -2; i < matrix.length; i++) {
            for (int j = -1; j < matrix[0].length; j++) {
                if(i==-2){
                    if(j==-1){
                        System.out.print("0 |");
                    }
                    else if(j>=10) {
                        System.out.print(j + " ");
                    }
                    else{
                        System.out.print(j + "  ");
                    }
                }
                else if(i==-1){
                    System.out.print("----");
                }else {
                    if(j==-1){
                        if(i>=10) {
                            System.out.print((i) + "|");
                        }
                        else {
                            System.out.print((i) + " |");
                        }
                    }else {
                        double type = matrix[i][j];

                        if(i == currentPosition[0] && j == currentPosition[0]){
                            System.out.print(ANSI_GREEN + "A  " + ANSI_RESET);
                        }
                        if ( i == targetPosition[0] && j == targetPosition[1]) {
                            System.out.print(ANSI_GREEN + "T  " + ANSI_RESET);
                        } else if (type == wall) {
                            System.out.print(ANSI_RED + 1 + "  " + ANSI_RESET);
                        } else if (type == emptySpace) {
                            System.out.print(ANSI_BLUE + 8 + " " + ANSI_RESET);
                        } else {
                            System.out.print(0 + "  ");
                        }
                    }
                }
            }
            System.out.println();
        }
        System.out.println("-----------");
        System.out.println();
    }

    //-----------------------update methods------------------------

    /**
     * Update the map based on what it perceived
     * @param objectPerceptArrayList
     */
    public void updateMap(ArrayList<ObjectPercept> objectPerceptArrayList){

            double relativeX = currentPosition[0] - xx;
            if (debug) System.out.println("Relative X is: "+relativeX);

            double relativeY = currentPosition[1] - yy;
            if (debug) System.out.println("Relative Y is: "+relativeY);

            for (int i = 0;i<objectPerceptArrayList.size();i++){

                ObjectPerceptType type = objectPerceptArrayList.get(i).getType();

                double[] cor = getRelativeLocationOfOrigin(objectPerceptArrayList.get(i).getPoint());

                int x = (int)(cor[0] + relativeX +currentPosition[0]);
                if (debug) System.out.println("X is: "+x);
                int y = (int)(cor[1] + relativeY+currentPosition[1]);
                if (debug) System.out.println("final Y is: "+y);
                if (debug) System.out.println("Relative Y is: "+relativeY);
                if (debug) System.out.println("Cor y is："+ cor[1]);
                if (debug) System.out.println("---------------------");



                    if (debug) System.out.println("The point is: "+objectPerceptArrayList.get(i).getType().name());

                switch (type) {

                    case Wall: if (debug) System.out.println("Update point successfully: wall"+x +", "+y);map[x][y] = wall;
                    break;

                    case EmptySpace: if (debug) System.out.println("Update point successfully: empty space "+x +", "+y);map[x][y] = emptySpace;
                    break;

                    case Door:if (debug) System.out.println("Update point successfully: door"); map[x][y] = door;
                    break;

                    //case Intruder: map[x][y] = intruder;
                    //break;

                    case TargetArea: if (debug) System.out.println("Update point successfully: target Place");map[x][y] = targetPlace;
                    break;

                    case SentryTower:if (debug) System.out.println("Update point successfully:sentry tower");map[x][y] = sentryTower;
                    break;

                    case Window: if (debug) System.out.println("Update point successfully: window");map[x][y] = window;
                    break;

                   // case Guard:map[x][y] = guard;
                    //break;

                    case ShadedArea:if (debug) System.out.println("Update point successfully: shaded Area");map[x][y] = shadedArea;
                    break;

                    case Teleport: if (debug) System.out.println("Update point successfully: Teleport");map[x][y] = teleport;
                    break;

                }

            }
         printMatrix(map);
    }


    public double[] updateCurrentPosition(){


        int type = actionHistory.get(actionHistory.size()-1).type;


        if ( type== 1){

            double moveLength = actionHistory.get(actionHistory.size()-1).val;

            double angle = getProperAngle();
            if (debug) System.out.println("Proper Angle is: "+angle);

            double xABS = Math.cos(Math.toRadians(angle))*moveLength;

            double yABS = Math.sin(Math.toRadians(angle))*moveLength;

            if (getQuadrant() == 1){
                currentPosition[0] = xABS + currentPosition[0];
                currentPosition[1] = yABS + currentPosition[1];
            }else if (getQuadrant() == 2){
                currentPosition[0] = -xABS + currentPosition[0];
                currentPosition[1] = yABS + currentPosition[1];
            }else if (getQuadrant() == 3){
                currentPosition[0] = -xABS + currentPosition[0];
                currentPosition[1] = -yABS + currentPosition[1];
            }else if (getQuadrant() == 4){
                currentPosition[0] = xABS + currentPosition[0];
                currentPosition[1] = -yABS + currentPosition[1];
            }

            return currentPosition;

            // if only rotate, then current position won't change, just update current angle
        }else{
            return currentPosition;
        }



    }

    /**
     * If current angle is greater than 360, it needs to be set as angle - 360
     */
    public void updateCurrentAngle(){


        if (actionHistory.get(actionHistory.size()-1).type == 2){
            if (debug) System.out.println("update angle");
           setCurrentAngle(currentAngle + actionHistory.get(actionHistory.size()-1).val);

        }

        if (currentAngle>360){
            setCurrentAngle(currentAngle - 360);
        }


    }


    //-----------------------get methods------------------------

    public double getProperAngle(){
        double val = 0;

        int quadrant = getQuadrant();

        if (quadrant == 1){
            val = 90 - currentAngle;
        }else if(quadrant == 2){
            val = currentAngle - 270;
        }else if (quadrant == 3){
            val = 90 - (currentAngle - 180);
        }else{
            val = currentAngle - 90;
        }

        return val;
    }

    public int getQuadrant(){

        if (currentAngle>= 0 && currentAngle<=90){
            return 1;
        }else if(currentAngle>90 && currentAngle<=180){
            return 4;
        }else if (currentAngle>180 && currentAngle<=270){
            return 3;
        }else {
            return 2;
        }
    }

    /**
     * Find the relative location of the origin point.
     * @param point
     * @return element 1 is the row position, 2nd is column position
     * done!
     */
    public double[] getRelativeLocationOfOrigin(Point point){

        double[] val = new double[2];

        double x = point.getX();
        //if (debug) System.out.println("x is: "+x);

        double y = point.getY();
       // if (debug) System.out.println("y is: "+y);

        //if (debug) System.out.println(currentAngle);

        val[0] = x*Math.cos(Math.toRadians(-currentAngle)) - y*Math.sin(Math.toRadians(-currentAngle));

        val[1] = y*Math.cos(Math.toRadians(-currentAngle)) + x*Math.sin(Math.toRadians(-currentAngle));

        return val;
    }

    //-----------------------print methods------------------------

    public void printMemoryMap(){
        for (int i = 0;i<map.length;i++){
            for(int j = 0;j<map[0].length;j++){

                System.out.print(map[i][j]+" ");


            }
            System.out.println();
        }
    }

    public void printCurrentPosition(){
        System.out.println("------------------Currently the position of guard agent is---------------------");
        System.out.println("In row: "+currentPosition[0]);
        System.out.println("In column: "+currentPosition[1]);
    }

    public void printCurrentAngle(){

        System.out.println("Current Angle is: "+currentAngle);
    }

    public void printArray(double[] array){
        for (int i = 0;i<array.length;i++){
            System.out.println("The "+i+" element is: "+array[i]);
        }
    }

    public void printArray(int[] array){
        for (int i = 0;i<array.length;i++){
            System.out.println("The "+i+" element is: "+array[i]);
        }
    }




    //------------------getter and setter---------------------

    public double getCurrentAngle(){
        return currentAngle;
    }

    public void setCurrentAngle(double val){
        currentAngle = val;
    }



}


