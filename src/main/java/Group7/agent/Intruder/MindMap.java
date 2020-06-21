package Group7.agent.Intruder;

import Interop.Action.Action;
import Interop.Action.Move;
import Interop.Action.Rotate;
import Interop.Geometry.Angle;
import Interop.Geometry.Direction;
import Interop.Geometry.Point;
import Interop.Percept.IntruderPercepts;
import Interop.Percept.Percepts;
import Interop.Percept.Vision.ObjectPercept;
import Interop.Percept.Vision.ObjectPerceptType;

import java.util.*;


// Store the visual percepts information of the agent gained all over the different turns


public class MindMap {

    private AgentState state; //position + direction of the agent
    private Point targetPos =null; //position of the target

    private int[][] mapData; //matrix storing the visual percepts types

//    different types stored by the matrix
    public static final int Unvisited = 0;
    public static final int Wall = 2;
    public static final int Door = 3;
    public static final int Teleport = 4;
    public static final int TargetArea = 5;
    public static final int Window = 6;
    public static final int Shaded = 7;
    public static final int Guard = 8;
    public static final int Intruder =9;
    public static final int Sentry =10;
    public static final int Empty = 1;


    //default size is arbitrary 25
    public MindMap(){
        int height = 25;
        int width = 25;

        // instanciate the mindmap. The agent is set at the center of the matrix.
        mapData = new int[height*2][width*2];
        mapData[height][width]= Intruder;
        this.state = new AgentState(new Point(height,width),0);
    }

//    check if the point (height width) is within the matrix dimensions, if not expand the matrix dimensions which must be expended
    private void checkExpention(int height,int width){
        if(height<0){
            ExtendMap(height,0);
        }
        if(width<0){
            ExtendMap(0,width);
        }
        if(width>=mapData[0].length){
            ExtendMap(0,width-mapData[0].length+1);
        }
        if(height>=mapData.length){
            ExtendMap(height-mapData.length+1,0);
        }
    }

//    extends the matrix. height, width can be negative or positive
    private void ExtendMap(int height, int width){
      if(height>0){
          expandBottom(height);
      }
      if(height<0){
          expandTop(-height);
      }
      if(width>0){
          expandRight(width);
      }
      if(width<0){
          expandLeft(-width);
      }
    }


    public void computeTargetPoint(Direction d){
//        if(null==directionFirstTurn){
//            directionFirstTurn = d;
//            posFirstTurn = state.getPos();
////            targetPos = findClosestUnvisitedPoint(d);
////        System.out.println("targetPos = " + targetPos.toString());
//        }else{
//           targetPos = findIntersection(d);
//            System.out.println("targetPos = " + targetPos.toString());
//        }
        //TODO
        if(targetPos==null){
            // target pos must be positive -> otherwise, expend the matrix with checkEpention()
            targetPos = new Point(160,295);
        }

    }


    public boolean isVisited(int a, int b){
        return getData(a,b)!=Unvisited;
    }

//    gets the data type of the point x,y in the matrix. If x,y are not within the dimensions of the matrix, it is being extended
    public int getData(int x, int y){
        checkExpention(x,y);
        if(x<0){
            x=0;
        }
        if(y<0){
            y=0;
        }
        return mapData[x][y];
    }

    public int[][] getMapData() {
        return mapData;
    }

    //updates the mapData according to the (visual) percepts
    public void updateGridMap(Percepts percepts){

        //all the objects in vision
        Set<ObjectPercept> objectPercepts = percepts.getVision().getObjects().getAll();
        List<ObjectPercept> ls = new ArrayList<ObjectPercept>(objectPercepts);

        double[] currentPosition = state.getRealPosArray();

//        store all the visual point into the matrix
        for (int i = 0;i<ls.size();i++){

//            conversion of the data points coordinates to the mapData coordinate system
            double[] cor = getRelativeLocationOfOrigin(ls.get(i).getPoint());

//            translated perception points coordinates
            int ox = (int) Math.round(cor[0] + currentPosition[0]);
            int oy = (int) Math.round(cor[1] + currentPosition[1]);

//            checks if the data point is within the dimensions of the matrix
             checkExpention(ox,oy);
             if(ox<0){
                 ox = 0;
             }
             if(oy <0){
                 oy = 0;
             }

            ObjectPerceptType type = ls.get(i).getType();

//          updates the info of the corresponding mapData case
            switch (type) {
                case Wall:
                       mapData[ox][oy] = Wall;
                    break;

                case Door  :
                        mapData[ox][oy] = Door;
                    break;

                case Window  :
                       mapData[ox][oy] = Window;
                    break;

                case Teleport:
                        mapData[ox][oy] = Teleport;
                    break;

                case SentryTower:
                        mapData[ox][oy] = Sentry;
                    break;

                case EmptySpace:
                        mapData[ox][oy] = Empty;
                    break;

                case ShadedArea:
                        mapData[ox][oy] = Shaded;
                    break;

                case Guard:
                        mapData[ox][oy] = Guard;
                    break;

                case Intruder:
                    mapData[ox][oy] = Intruder;
                    break;

                case TargetArea:
                    mapData[ox][oy] = TargetArea;
                    break;
            }
    }

//        set all unmarked spots within the field of view of the agent as Empty
        setVisited(percepts);
//        System.out.println("After update");
//        printMatrix(mapData,targetPos,state.getPos());
  }

//        set all unmarked spots within the field of view of the agent as Empty
  public void setVisited(Percepts percepts){

//        checks if the current mapData location is not marked as Unvisited, otherwise, mark it as Empty (thus visited)
        if(mapData[state.getX()][state.getY()]==Unvisited){
            mapData[state.getX()][state.getY()]=Empty;
        }

//        range of the field of view
      int range = (int)percepts.getVision().getFieldOfView().getRange().getValue();

//        range angle of the field of view
      double range_angle = percepts.getVision().getFieldOfView().getViewAngle().getRadians();

//      angle that are the boundaries of the field of view
      double angleFrom = formatAngleRadians(Math.toRadians(state.getAngle())-range_angle/2);
      double angleTo = formatAngleRadians(Math.toRadians(state.getAngle())+range_angle/2);

      for (int i = -range; i <= range ; i++) {
          for (int j = -range; j <= range ; j++) {
              if(exists(i+state.getX(),j+state.getY())&& (i!=0 || j!=0)) {
//                  calculates the points of the matrix that are in the field of view
                  double adjacent = i;
                  double opposed = j;

                  double angle = Math.atan(opposed / adjacent);
                  if (i < 0) {
                      angle += Math.toRadians(180);
                  }
                  angle = formatAngleRadians(angle);

                  if(angleTo<Math.toRadians(90) && angleFrom>Math.toRadians(270)) { //special case
                      if (angleFrom <= angle && angle <= Math.toRadians(360)) { //check if the angle is in the field of view
                          if (!isVisited(state.getX() + i, state.getY() + j)) {
                              mapData[state.getX() + i][state.getY() + j] = Empty;
                          }
                      }
                      if (0 <= angle && angle <= angleTo) { //check if the angle is in the field of view
                          if (!isVisited(state.getX() + i, state.getY() + j)) {
                              mapData[state.getX() + i][state.getY() + j] = Empty;
                          }
                      }
                  }else {
                      if (angleFrom <= angle && angle <= angleTo) { //check if the angle is in the field of view
                          if (!isVisited(state.getX() + i, state.getY() + j)) {
                              mapData[state.getX() + i][state.getY() + j] = Empty;
                          }
                      }
                  }
              }
          }
      }
  }

    public void clearEmpty(){ //clear all the empty area, set them to unvisited
        System.out.println("MindMap.clearEmpty");
        for (int i = 0; i <mapData.length ; i++) {
            for (int j = 0; j <mapData[0].length ; j++) {
                if(mapData[i][j]==Empty){
                    mapData[i][j]=Unvisited;
                }
            }
        }
    }

    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLUE = "\u001B[34m";

public static double formatAngle(double angle){
    double out = angle;
    if(angle<0){
        angle+=360;
    }
    if(angle>=360){
        angle-=360;
    }
    return out;
}

    public static double formatAngleRadians(double angle){
        double out = angle;
        if(angle<0){
            angle+=2*Math.PI;
        }
        if(angle>=360){
            angle-=2*Math.PI;
        }
        return out;
    }

//    prints the mapData
    public static void printMatrix(int[][] matrix,Point targetPos, Point agent){
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
                        int type = matrix[i][j];

                        if(i == agent.getX() && j == agent.getY()){
                            System.out.print(ANSI_GREEN + "A " + ANSI_RESET);
                        }
                        else if (targetPos != null && i == targetPos.getX() && j == targetPos.getY()) {
                            System.out.print(ANSI_GREEN + "T " + ANSI_RESET);
                        } else if (type == Wall) {
                            System.out.print(ANSI_RED + matrix[i][j] + " " + ANSI_RESET);
                        } else if (type == Empty) {
                            System.out.print(ANSI_BLUE + matrix[i][j] + " " + ANSI_RESET);
                        } else {
                            System.out.print(matrix[i][j] + " ");
                        }
                    }
                }
            }
            System.out.println();
        }
        System.out.println("-----------");
        System.out.println();
    }

    //method to update the state based on the chosen action, assuming it will not be rejected by the game controller
    public void updateState(Action a){
       if( a instanceof Move){ //action is a move
           double moveLength= ((Move) a).getDistance().getValue();

           Vector pos = new Vector(state.getRealPos());

           Vector agentOrientation = new Vector(state.getAngle());

           agentOrientation.setLength(moveLength);

           Vector newpos = pos.add(agentOrientation);

           System.out.println("Move of "+moveLength);
           System.out.println("New Pos = " + newpos.getString());
           System.out.println("Angle = "+state.getAngle());
//         updates the positions of the agent
           state.setPos(newpos.x,newpos.y);

       }

       else if ( a instanceof Rotate){ //action is a rotation
           double old_angle = state.getAngle();

           double rotation_angle = ((Rotate) a).getAngle().getDegrees();

           double new_angle = old_angle + rotation_angle;

//           checks if the angle is within the range 0-360
           new_angle = formatAngle(new_angle);

           System.out.println("Rotation of "+rotation_angle);
           System.out.println("Pos = " + state.getPos().toString());
           System.out.println("New Angle = "+state.getAngle());

           state.setAngle(new_angle);

       }else{
           System.out.println("NoAction ");
       }

    }


    public double[] getRelativeLocationOfOrigin(Point point){

        double[] val = new double[2];

        double x = point.getX();
        double y = point.getY();

//        current direction angle of the agent is the y axis the visual system
        Vector direction = new Vector(state.getAngle());

//        its perpendicular is the x axis of the system
        Vector p = direction.get2DPerpendicularVector();

//        normalise the axis to x and y
        direction.setLength(y);
        p.setLength(x);

        Vector values = direction.add(p);

        val[0] =values.x;
        val[1] =values.y;

        return val;
    }

    public Point getTargetPos() {
        return targetPos;
    }

    public AgentState getState() {
        return state;
    }

    /**
     * Method to check the walkable areas so it doesn't go on the walls etc
     * @return 0 if can walk on a "case" of the map and 1 if it can not because there is an obstacle there
     */
    public int[][] walkableExtended(){
        int[][] out = new int[mapData.length][mapData[0].length];

        for (int i = 0; i < mapData.length; i++) {
            for (int j = 0; j < mapData[0].length ; j++) {
                if(mapData[i][j]==Wall){
                    out[i][j] = 1;
                    out[i+1][j] = 1;
                    out[i-1][j] = 1;
                    out[i+1][j+1] = 1;
                    out[i+1][j-1] = 1;
                    out[i-1][j-1] = 1;
                    out[i-1][j-1] = 1;
                    out[i][j-1] = 1;
                    out[i][j+1] = 1;
                }
            }
        }
//        printMatrix(out, targetPos, state.getPos());
        return out;
    }

    public int[][] walkableExtended2(){
        int[][] out = new int[mapData.length][mapData[0].length];

        for (int i = 0; i < mapData.length; i++) {
            for (int j = 0; j < mapData[0].length ; j++) {
                if(mapData[i][j]==Wall){
                    out[i][j] = Wall;
//                    out[i+1][j] = Wall;
//                    out[i-1][j] = Wall;
//                    out[i+1][j+1] = Wall;
//                    out[i+1][j-1] = Wall;
//                    out[i-1][j-1] = Wall;
//                    out[i-1][j-1] = Wall;
//                    out[i][j-1] = Wall;
//                    out[i][j+1] = Wall;
                }else {
                    if(out[i][j]==Unvisited) {
                        out[i][j] =mapData[i][j];
                    }
                }
                }
            }
//        printMatrix(out, targetPos, state.getPos());
        return out;
    }

    public int[][] walkable(){
        int[][] out = new int[mapData.length][mapData[0].length];

        for (int i = 0; i < mapData.length; i++) {
            for (int j = 0; j < mapData[0].length ; j++) {
                if(mapData[i][j]==Wall){
                    out[i][j] = 1;
                }
            }
        }
//        printMatrix(out, targetPos, state.getPos());
        return out;
    }

    public ArrayList<Point> unWalkablePointList(){

        ArrayList<Point> out = new ArrayList<>();

         int[][] walkmatrix = walkableExtended();

         for (int i = 0; i < walkmatrix.length; i++) {
            for (int j = 0; j < walkmatrix[0].length ; j++) {
                if(walkmatrix[i][j]==1){
                    out.add(new Point(i,j));
                }
            }
        }

//        printMatrix(out);
        return out;
    }

//  gets the data information of a sector
    public ArrayList<Integer> getSectorInfo(double angleFrom, double angleTo, int range){

        angleFrom = Math.toRadians(angleFrom);
        angleTo = Math.toRadians(angleTo);
        ArrayList<Integer> out = new ArrayList<>();

        int[][] mat = walkableExtended2();

        for (int i = -range; i <= range ; i++) {
            for (int j = -range; j <= range ; j++) {
                if(exists(i+state.getX(),j+state.getY())&& (i!=0 || j!=0)) {
                    double adjacent = i;
                    double opposed = j;

                        double angle = Math.atan(opposed / adjacent);
                        if (i < 0) {
                            angle += Math.toRadians(180);
                        }
                        angle =formatAngleRadians(angle);

                    if(angleTo<Math.toRadians(90) && angleFrom>Math.toRadians(270)) { //special case
                        if (angleFrom <= angle && angle <= Math.toRadians(360)) { //check if the angle is in the field of view
                            if (!isVisited(state.getX() + i, state.getY() + j)) {
                                mapData[state.getX() + i][state.getY() + j] = Empty;
                            }
                        }
                        if (0 <= angle && angle <= angleTo) { //check if the angle is in the field of view
                            if (!isVisited(state.getX() + i, state.getY() + j)) {
                                mapData[state.getX() + i][state.getY() + j] = Empty;
                            }
                        }
                    }else {
                        if (angleFrom <= angle && angle < angleTo) {
                            out.add(mat[state.getX() + i][state.getY() + j]);
//                            System.out.println("add"+"j = " + j+"i = " + i);

                        }
                    }
                }
            }
        }
        return out;
    }

    public boolean exists(int x, int y){
        if(x<mapData.length && x>0 && y>0 && y<mapData[0].length){
            return true;
        }else{
            return false;
        }
    }

    public int[][] getSubMap(int range){

        int startX = state.getX() - range;
        int startY = state.getY() - range;
        int[][] partialMap = new int[range*2][range*2];

        for (int i = 0;i<partialMap.length;i++){
            for (int j = 0;j<partialMap[0].length;j++){
                partialMap[i][j] = mapData[startX+i][startY+j];
            }
        }

        return partialMap;
    }

    /**
     * @param size to expand
     * @return new matrix (expanded) with 1 extra bottom row
     */
    public void expandBottom(int size){
        // expand in the x direction if needed  to the bottom
        int[][] tmp = new int[mapData.length + size][mapData[0].length];
        for(int i = 0; i < mapData.length; i++){
            System.arraycopy(mapData[i], 0, tmp[i], 0, mapData[0].length);
        }
        mapData = tmp;
    }


    /**
     * @param size to expand
     * @return new matrix (expanded) with 1 extra top row
     */
    public void expandTop(int size){
        // expand in the x direction if needed  to the top
        int[][] tmp = new int[mapData.length + size][mapData[0].length];
        for(int i = 0; i < mapData.length; i++){
            System.arraycopy(mapData[i], 0, tmp[i + size], 0, mapData[0].length);
        }
        if(targetPos!=null) {
            targetPos = new Point(targetPos.getX() + 1, targetPos.getY());
        }
        state.setPos(state.getX()+1,state.getY());
        mapData = tmp;
    }

    /**
     * @param size to expand
     * @return new matrix (expanded) with 1 extra Left column
     */
    public void expandLeft(int size) {
        // expand in the y direction if needed to the left
        int[][] tmp = new int[mapData.length][mapData[0].length + size];
        for (int i = size; i < mapData.length; i++) {
            for (int j = 0; j < mapData[0].length; j++) {
                tmp[i][j + 1] = mapData[i][j];
             }
        }
        if(targetPos!=null) {
            targetPos = new Point(targetPos.getX(), targetPos.getY() + 1);
        }
        state.setPos(state.getX(),state.getY()+1);
        mapData = tmp;
    }

    /**
     * @param size to expand
     * @return new matrix (expanded) with 1 extra right column
     */
    public void expandRight(int size){
        // expand in the y direction if needed to the right
        int[][] tmp = new int[mapData.length][mapData[0].length + size];
        for(int i = 0; i < mapData.length; i++){
            System.arraycopy(mapData[i], 0, tmp[i], 0, mapData[0].length);
        }
        mapData = tmp;
    }
}



//    public Point findClosestUnvisitedPoint(Direction d){
//        double  dir = d.getDegrees();
//        System.out.println("direction = " + -dir);
//        double this_direction = state.getAngle();
//        System.out.println("direction = " +(-dir+this_direction));
////        direction.setLength(0.1);
//        Vector direction = new Vector(-d.getDegrees()+ state.getAngle());
//        direction.setLength(5);
//        Vector pos = state.vectorPos();
//        double increment = 5;
//        boolean found = false;
//        while(!found){
//            Vector target = pos.add2(direction);
//            if(!isVisited(target)){
//                found = true;
//            }
//            else{
//                direction.addLength(increment);
//            }
//        }
//        pos.add(direction);
//        checkExpention((int)pos.x,(int)pos.y);
//        if(pos.x<0){
//            pos.x =0;
//        }
//        if(pos.y<0){
//            pos.y =0;
//        }
//        return new Point(pos.x,pos.y);
//    }
//
//    public Point findIntersection(Direction d){
//        if(state.getX()==posFirstTurn.getX() && state.getY()==posFirstTurn.getY()){ //the agent has not moved
//            System.out.println("the agent has not moved");
//            directionFirstTurn = d;
//            return findClosestUnvisitedPoint(d);
//        }
//        System.out.println("d = " + d.getRadians());
//        System.out.println(posFirstTurn);
//        System.out.println("directionFirstTurn = " + directionFirstTurn.getRadians());
//        System.out.println(state.getPos());
////        building 4 points
//        Point a1 = posFirstTurn;
////        System.out.println("a1= "+a1.getX()+" "+a1.getY());
//        Vector va1 = new Vector(posFirstTurn);
//        Point a2 = state.getPos();
////        System.out.println("a2= "+a2.getX()+" "+a2.getY());
//        Vector vb2 = state.vectorPos().add(new Vector(d));
//        Vector vb1 = va1.add(new Vector(directionFirstTurn));
//        Point b1 = new Point(vb1.x,vb1.y);
// //       System.out.println("b1= "+b1.getX()+" "+b1.getY());
//        Point b2 = new Point(vb2.x,vb2.y);
// //       System.out.println("b2= "+b2.getX()+" "+b2.getY());
//
////        calculating the equations of the lines
//        double m1 = (a1.getY()-a2.getY())/(a1.getX()-a2.getX());
////        System.out.println("m1 = " + m1);
//        double m2 = (b1.getY()-b2.getY())/(b1.getX()-b2.getX());
////        System.out.println("m2 = " + m2);
////        case lines are parallel
//        if(Math.abs(m1-m2)<=0.05){
//            System.out.println("parallel");
//            return findClosestUnvisitedPoint(d);
//        }
//        double c1 = a1.getY()-m1*a1.getX();
////        System.out.println("c1 = " + c1);
//        double c2 = b1.getY()-m2*b1.getX();
////        System.out.println("c2 = " + c2);
//
////        solving the system
//        double interX =(c2-c1)/(m1-m2);
////        System.out.println("interX = " + interX);
//        double interY = m1*interX+c1;
////        System.out.println("interY = " + interY);
//        checkExpention((int)interX,(int)interY);
//        return new Point(interX,interY);
//    }