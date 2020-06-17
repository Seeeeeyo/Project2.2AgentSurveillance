package Group7.agent.Intruder;

import Interop.Action.Action;
import Interop.Action.Move;
import Interop.Action.Rotate;
import Interop.Geometry.Angle;
import Interop.Geometry.Direction;
import Interop.Geometry.Point;
import Interop.Percept.IntruderPercepts;
import Interop.Percept.Vision.ObjectPercept;
import Interop.Percept.Vision.ObjectPerceptType;

import java.util.*;


// Store the visual percepts information of the agent gained all over the different turns


public class MindMap {

    private AgentState state; //position + direction of the agent
    private Point targetPos =null;
    private Direction directionFirstTurn;
    private Point posFirstTurn;
    private Point posPrevTurn=null;

    //any 0 in this array represents a empty space on the or an are which has not already been explored
    //any 1 represents a wall
    private int[][] mapData;
    private static final int Unvisited = 0;
    private static final int Visited = 1;
    private static final int Wall = 2;
    private static final int Door = 3;
    private static final int Teleport = 4;
    private static final int TargetArea = 5;
    private static final int Window = 6;
    private static final int Shaded = 7;
    private static final int Guard = 8;
    private static final int Intruder =9;
    private static final int Sentry =10;
    private static final int Empty = 11;
    public int xx;
    public  int yy;
    //default size is arbitrary 20
    public MindMap(){
        int height = 50;
        int width = 50;
        xx = height;
        yy = width;

        mapData = new int[height*2][width*2];
        mapData[height][width]=9;
        this.state = new AgentState(new Point(height,width),0);
    }

// instanciate the mindmap. The agent is set at the center of the matrix.

    private void checkExpention(int height,int width){
//        System.out.println(mapData.length);
//        System.out.println(mapData[0].length);
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


    public Point findClosestUnvisitedPoint(Direction d){
        double  dir = d.getDegrees();
        System.out.println("direction = " + -dir);
        double this_direction = state.getAngle();
        System.out.println("direction = " +(-dir+this_direction));
//        direction.setLength(0.1);
        Vector direction = new Vector(-d.getRadians()+Math.toRadians(state.getAngle()));
        direction.setLength(5);
        Vector pos = state.vectorPos();
        double increment = 5;
        boolean found = false;
        while(!found){
            Vector target = pos.add2(direction);
            if(!isVisited(target)){
                found = true;
            }
            else{
                direction.addLength(increment);
            }
        }
        pos.add(direction);
        checkExpention((int)pos.x,(int)pos.y);
        if(pos.x<0){
            pos.x =0;
        }
        if(pos.y<0){
            pos.y =0;
        }
        return new Point(pos.x,pos.y);
    }

    public Point findIntersection(Direction d){
        if(state.getX()==posFirstTurn.getX() && state.getY()==posFirstTurn.getY()){ //the agent has not moved
            System.out.println("the agent has not moved");
            directionFirstTurn = d;
            return findClosestUnvisitedPoint(d);
        }
        System.out.println("d = " + d.getRadians());
        System.out.println(posFirstTurn);
        System.out.println("directionFirstTurn = " + directionFirstTurn.getRadians());
        System.out.println(state.getPos());
//        building 4 points
        Point a1 = posFirstTurn;
//        System.out.println("a1= "+a1.getX()+" "+a1.getY());
        Vector va1 = new Vector(posFirstTurn);
        Point a2 = state.getPos();
//        System.out.println("a2= "+a2.getX()+" "+a2.getY());
        Vector vb2 = state.vectorPos().add(new Vector(d));
        Vector vb1 = va1.add(new Vector(directionFirstTurn));
        Point b1 = new Point(vb1.x,vb1.y);
 //       System.out.println("b1= "+b1.getX()+" "+b1.getY());
        Point b2 = new Point(vb2.x,vb2.y);
 //       System.out.println("b2= "+b2.getX()+" "+b2.getY());

//        calculating the equations of the lines
        double m1 = (a1.getY()-a2.getY())/(a1.getX()-a2.getX());
//        System.out.println("m1 = " + m1);
        double m2 = (b1.getY()-b2.getY())/(b1.getX()-b2.getX());
//        System.out.println("m2 = " + m2);
//        case lines are parallel
        if(Math.abs(m1-m2)<=0.05){
            System.out.println("parallel");
            return findClosestUnvisitedPoint(d);
        }
        double c1 = a1.getY()-m1*a1.getX();
//        System.out.println("c1 = " + c1);
        double c2 = b1.getY()-m2*b1.getX();
//        System.out.println("c2 = " + c2);

//        solving the system
        double interX =(c2-c1)/(m1-m2);
//        System.out.println("interX = " + interX);
        double interY = m1*interX+c1;
//        System.out.println("interY = " + interY);
        checkExpention((int)interX,(int)interY);
        return new Point(interX,interY);
    }

    public Direction getDirectionFirstTurn() {
        return directionFirstTurn;
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
            targetPos = new Point(10,60);
        }

    }

    public boolean isVisited(Vector v){
        return getData(v)!=Unvisited;
    }

    public boolean isVisited(Point p){
        return getData(p)!=Unvisited;
    }

    public boolean isVisited(double x, double y){
        return getData(x,y)!=Unvisited;
    }

    public boolean isVisited(int a, int b){
        return getData(a,b)!=Unvisited;
    }


    public int getData(Point p){
        return getData(p.getY(),p.getY());
    }

    public int getData(Vector v){
        return getData(v.x,v.y);
    }
    public int getData(double x, double y){
        return getData((int)Math.round(x),(int)Math.round(y));
    }

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



/**
     * update the Map after executing an action
     * @param
     * @param
     * @return a new map being updated
 * */
    public void updateGridMap(IntruderPercepts percepts){

        //currently, the explore agent only need to execute move or rotate
        //all the objects in vision
        Set<ObjectPercept> objectPercepts = percepts.getVision().getObjects().getAll();

        List<ObjectPercept> ls = new ArrayList<ObjectPercept>(objectPercepts);

        double[] currentPosition = state.getRealPosArray();
//        System.out.println("current Position = " + currentPosition[0] +"; "+ currentPosition[1]);
//        System.out.println("current Angle = "+ state.getAngle());

        for (int i = 0;i<ls.size();i++){

          //  System.out.println("Before translation "+ls.get(i).getPoint().toString());
            double[] cor = getRelativeLocationOfOrigin(ls.get(i).getPoint());
           // System.out.println("cor = " + cor[0]+ "; "+cor[1]);

            int ox = (int) Math.round(cor[0] + currentPosition[0]);
            int oy = (int) Math.round(cor[1] + currentPosition[1]);
         //   System.out.println("oy = " + oy);
        //    System.out.println("ox = " + ox);

             checkExpention(ox,oy);
             if(ox<0){
                 ox = 0;
             }
             if(oy <0){
                 oy = 0;
             }

            ObjectPerceptType type = ls.get(i).getType();
//            System.out.println("type = " + type.toString());
            switch (type) {
                case Wall:
                       mapData[ox][oy] = Wall;
//                    System.out.println("Add wall"+ mapData[ox][oy]+" in "+ox+"; "+oy);
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
//     System.out.println();
//     System.out.println("after update: ");
//     printMatrix(mapData,targetPos,state.getPos());
  }
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLUE = "\u001B[34m";


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
                            System.out.print(ANSI_GREEN + "A  " + ANSI_RESET);
                        }
                        else if (targetPos != null && i == targetPos.getX() && j == targetPos.getY()) {
                            System.out.print(ANSI_GREEN + "T  " + ANSI_RESET);
                        } else if (type == Wall) {
                            System.out.print(ANSI_RED + matrix[i][j] + "  " + ANSI_RESET);
                        } else if (type == Empty) {
                            System.out.print(ANSI_BLUE + matrix[i][j] + " " + ANSI_RESET);
                        } else {
                            System.out.print(matrix[i][j] + "  ");
                        }
                    }
                }
            }
            System.out.println();
        }
        System.out.println("-----------");
        System.out.println();
    }

    //method to update the state based on the chosen action
    public void updateState(Action a){
       if( a instanceof Move){
           double moveLength= ((Move) a).getDistance().getValue();

           Vector pos = new Vector(state.getRealPos());

           Vector agentOrientation = new Vector(Math.toRadians(state.getAngle()));

           agentOrientation.setLength(moveLength);

           Vector newpos = pos.add(agentOrientation);

           System.out.println("Move of "+moveLength);
           System.out.println("New Pos = " + newpos.getString());
           System.out.println("Angle = "+state.getAngle());

           state.setPos(newpos.x,newpos.y);

       }

       else if ( a instanceof Rotate){
           double old_angle = state.getAngle();

           double rotation_angle = ((Rotate) a).getAngle().getDegrees();

           double new_angle = old_angle + rotation_angle;

           if(new_angle>=360){
               new_angle-=360;
           }

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


        Vector direction = new Vector(Math.toRadians(state.getAngle()));

        Vector p = direction.get2DPerpendicularVector();

//        p.mul(-1);
//        System.out.println("p = " + p.getString());

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
    public int[][] walkable(){
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
        targetPos = new Point(targetPos.getX()+1,targetPos.getY());
        state.setPos(state.getX()+1,state.getY());
        mapData = tmp;
    }

    /**
     * @param size to expand
     * @return new matrix (expanded) with 1 extra Left column
     */
    public void expandLeft(int size) {
//        System.out.println("Before left exp ");
//        printMatrix(mapData,targetPos);
        // expand in the y direction if needed to the left
        int[][] tmp = new int[mapData.length][mapData[0].length + size];
        for (int i = size; i < mapData.length; i++) {
            for (int j = 0; j < mapData[0].length; j++) {
                tmp[i][j + 1] = mapData[i][j];
             }
//            System.arraycopy(mapData[i], 0, tmp[i], 0, mapData[0].length);
        }
        targetPos = new Point(targetPos.getX(),targetPos.getY()+1);
//        System.out.println(targetPos.toString());
        state.setPos(state.getX(),state.getY()+1);
//        System.out.println(state.getPos().toString());
        mapData = tmp;
//        System.out.println("After left exp ");
//        printMatrix(mapData,targetPos);
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