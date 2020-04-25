package Group9.agent.Intruder;

import Interop.Action.*;
import Interop.Geometry.Angle;
import Interop.Geometry.Direction;
import Interop.Geometry.Point;
import Interop.Percept.GuardPercepts;
import Interop.Percept.Vision.ObjectPercept;
import Interop.Percept.Vision.ObjectPerceptType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/*Store the visual percepts information of the agent gained all over the different turns
* */
public class MindMap {

    AgentState state; //position + direction of the agent

    //any 0 in this array represents a empty space on the or an are which has not already been explored
    //any 1 represents a wall
    private int[][] mapData;
    private static final int Unvisited = 0;
    private static final int Visited = 1;
    private static final int Wall = 2;
    private static final int Door = 3;
    private static final int Teleport = 4;
    private static final int TargetArea = 5;

    //default size is arbitrary 50
    public MindMap(){
        new MindMap(50, 50, Direction.fromRadians(0));
    }

    /*instanciate the mindmap. The agent is set at the center of the matrix.
    * */
    public MindMap(double height, double width, Direction angle){
        mapData = new int[(int)height][(int)width];
        setState(new AgentState(new Point(height/2,width/2),angle);
    }

    private void ExtendMap(int height, int with){
      //TODO
//        create a new matrix longer with param height and width
//                copy the previous onto this new one
//        set the new one as actual MinMap
    }

    //reset the map data
    private void resetMap(){
        for(int i=0 ; i<mapData.length-1 ; i++){
            for(int j=0 ; i<mapData.length-1; j++){
                mapData[i][j] =  Unvisited;
            }
        }
    }

    /*
    * a matrix cell can store only one integer. Thus, when updating the matrix, first set the visited area, then were specific items such as walls
    * are perceived, overwrite the visited area and change the corresponding matrix cells to store the specific item.
    */
    //set an area as visited
    public void visitAera(Point a1, Point a2, Point a3, Point a4){
        updateAreaMemory(a1,a2,a3,a4,Visited);
    }

    public void visit(Point a){
        visit(a.getX(),a.getY());
    }

    public void visit(double x, double y) {
        visit((int)Math.round(x),(int)Math.round(y));
    }

    public void visit(int a, int b){
        mapData[a][b] = Visited;
    }

    public boolean isVisited(double x, double y){
        return isVisited((int)Math.round(x),(int)Math.round(y));
    }

    public boolean isVisited(int a, int b){
        return mapData[a][b]!=Unvisited;
    }

    public void setState(AgentState state) {
        this.state = state;
    }

    public int getData(int x, int y){
        return mapData[x][y];
    }

    public ArrayList<String> toStringInfo(ArrayList<Integer> a ){
        ArrayList<String> result = new ArrayList<>();
        for(Integer i : a){
            result.add(toStringInfo(i));
        }
        return result;
    }

    public String toStringInfo(int x){
        switch (x){
            case 0:
                return "Unvisited";
            case 1:
                return "Visited";
            case 2:
                return "Wall";
            case 3:
                return "Door";
            case 4:
                return "Teleport";
        }
        System.out.println(x+" is not corresponding to any data.");
        return ("error " + x);
    }

    public ArrayList<Integer> getAreaData(Point a1, Point a2, Point a3, Point a4){

        ArrayList<Integer> info = new ArrayList<>();
        int minX=(int)Math.round(a1.getX());
        int maxX=(int)Math.round(a1.getX());
        int minY=(int)Math.round(a1.getY());
        int maxY=(int)Math.round(a1.getY());


        minX = Math.min(minX,(int)Math.round(a2.getX()));
        maxX = Math.max(maxX,(int)Math.round(a2.getX()));
        minY = Math.min(minY,(int)Math.round(a2.getY()));
        maxY = Math.max(maxY,(int)Math.round(a2.getY()));

        minX = Math.min(minX,(int)Math.round(a3.getX()));
        maxX = Math.max(maxX,(int)Math.round(a3.getX()));
        minY = Math.min(minY,(int)Math.round(a3.getY()));
        maxY = Math.max(maxY,(int)Math.round(a3.getY()));

        minX = Math.min(minX,(int)Math.round(a4.getX()));
        maxX = Math.max(maxX,(int)Math.round(a4.getX()));
        minY = Math.min(minY,(int)Math.round(a4.getY()));
        maxY = Math.max(maxY,(int)Math.round(a4.getY()));

        for(int i = minX; i<maxX; i++){
            for(int j = minY; j<maxY; j++){
                if(!info.contains(mapData[i][j])){
                    info.add(mapData[i][j]);
                }
            }
        }
        return info;
    }

    //this area must have a rectangular shape
    public void updateAreaMemory(Point a1, Point a2, Point a3, Point a4, int objectType){
        int minX=(int)Math.round(a1.getX());
        int maxX=(int)Math.round(a1.getX());
        int minY=(int)Math.round(a1.getY());
        int maxY=(int)Math.round(a1.getY());


        minX = Math.min(minX,(int)Math.round(a2.getX()));
        maxX = Math.max(maxX,(int)Math.round(a2.getX()));
        minY = Math.min(minY,(int)Math.round(a2.getY()));
        maxY = Math.max(maxY,(int)Math.round(a2.getY()));

        minX = Math.min(minX,(int)Math.round(a3.getX()));
        maxX = Math.max(maxX,(int)Math.round(a3.getX()));
        minY = Math.min(minY,(int)Math.round(a3.getY()));
        maxY = Math.max(maxY,(int)Math.round(a3.getY()));

        minX = Math.min(minX,(int)Math.round(a4.getX()));
        maxX = Math.max(maxX,(int)Math.round(a4.getX()));
        minY = Math.min(minY,(int)Math.round(a4.getY()));
        maxY = Math.max(maxY,(int)Math.round(a4.getY()));

        for(int i = minX; i<maxX; i++){
            for(int j = minY; j<maxY; j++){
                mapData[i][j] = objectType;
            }
        }
    }

    public void setLineObject(Point a1, Point a2, int objectType){
        updateAreaMemory(a1,a2,a1,a2, objectType);
    }


    public void setCornerWall(Point a1, Point corner, Point a2){
        setWall(a1,corner);
        setWall(corner,a2);
    }

    public void setWall(Point a1, Point a2){
        setLineObject( a1, a2,Wall);
    }

    public void setDoor(Point a1, Point a2){
        setLineObject( a1, a2,Door);
    }

    public void setTeleport(Point a1, Point a2, Point a3, Point a4){
        updateAreaMemory(a1,a2,a3,a4,Teleport);
    }

    //checks if a point is in a  triangular shape. (a view field has a triangular shape)
    //TODO test the method
    public boolean isInTriangle(Point a, Point b, Point c, Point m){
        boolean ok = true;
        if (0 < new Vector(a,b).vectorialProduct(new Vector(a,m)).scalarProduct(new Vector(a,m).vectorialProduct(new Vector(a,c))))
            ok = false;
        if (0 < new Vector(b,a).vectorialProduct(new Vector(b,m)).scalarProduct(new Vector(b,m).vectorialProduct(new Vector(b,c))))
            ok = false;
        if (0 < new Vector(a,c).vectorialProduct(new Vector(c,m)).scalarProduct(new Vector(c,m).vectorialProduct(new Vector(b,c))))
            ok = false;
        return ok;
    }

    /**
     *
     * @param currX perceived x
     * @param currY perceived y
     * @return x value based on the coordinate of initial point
     */
    public double changeToStartingPointCoordinateX(double currX, double currY){
        double val = 0;

        double sumOfRotation = 0;

        for (int i = 0;i<moveHistory.size();i++){

            //if the action is rotation
            if (moveHistory.get(i).getActionType() == 2){
                sumOfRotation = sumOfRotation + moveHistory.get(i).getVal();
            }
        }

        double X = coordinateBasedOnInitialPoint(currX,currY,sumOfRotation)[0];

        val = X - selfLocation[0];

        return val;
    }

    //The coordinate for object before making rotation.
    public double[] coordinateBasedOnInitialPoint(double x, double y,double sumOfRotateAngle) {
        double[] xy = new double[2];

        double previousX =  (x*Math.cos(Math.toRadians(sumOfRotateAngle)) + y*Math.sin(Math.toRadians(sumOfRotateAngle)));

        double previousY =  (y*Math.cos(Math.toRadians(sumOfRotateAngle)) - x*Math.sin(Math.toRadians(sumOfRotateAngle)));

        xy[0] = previousX;

        xy[1] = previousY;

        return xy;

    }




    //after doing re-rotate calculation, these method will return a value compare to ini valuel
    public double changeToStartingPointCoordinateY(double currX,double currY){
        double val = 0;

        double sumOfRotation = 0;

        for (int i = 0;i<moveHistory.size();i++){

            //if the action is rotation
            if (moveHistory.get(i).getActionType() == 2){
                sumOfRotation = sumOfRotation + moveHistory.get(i).getVal();
            }
        }

        double Y = coordinateBasedOnInitialPoint(currX,currY,sumOfRotation)[1];

        val = Y - selfLocation[0];

        return val;
    }


    //given a rotation angle and moving distance, return the xy- coordinates of where agent is based on the previous point.
    public double[] getXandYAfterRotationMove(double degree, double distance){

        double[] xy = new double[2];

        if (degree >0){
            //x value
            xy[0] = distance * Math.sin(Math.toRadians(degree));

            //y value
            xy[1] = distance * Math.cos(Math.toRadians(degree));
        }else {

            xy[0] = -distance * Math.sin(Math.toRadians(-degree));

            xy[1] = distance * Math.cos(Math.toRadians(-degree));
        }


        return xy;
    }

    /**
     * update the Map after executing an action
     * @param
     * @param
     * @return a new map being updated
     */
    public void updateGridMap(GuardPercepts percepts){

        //currently, the explore agent only need to execute move or rotate

        //all the objects in vision
        Set<ObjectPercept> objectPercepts = percepts.getVision().getObjects().getAll();

        List<ObjectPercept> ls = new ArrayList<ObjectPercept>(objectPercepts);

        Iterator<ObjectPercept> iterator = objectPercepts.iterator();

        to:for (int i = 0;i<ls.size();i++){

            //TODO change the bellow codes, they should compute the object coordinates in the matrix. (they are different form the coordinates
            //in the field of view of the agent.
            double objectX = changeToStartingPointCoordinateX(ls.get(i).getPoint().getX(),ls.get(i).getPoint().getY());

            double objectY = changeToStartingPointCoordinateY(ls.get(i).getPoint().getX(),ls.get(i).getPoint().getY());



            ObjectPerceptType type = ls.get(i).getType();

            switch (type) {
//                case Wall:
//                    if(stateSituation[(int)(selfLocation[1]+initialY - objectY)][(int)(selfLocation[0]+initialX+objectX)] == unknownPlace) stateSituation[(int)(selfLocation[1]+initialY - objectY)][(int)(selfLocation[0]+initialX+objectX)] = wall;
//                    continue to;
//
//                case Door  :
//                    if(stateSituation[(int)(selfLocation[1]+initialY - objectY)][(int)(selfLocation[0]+initialX+objectX)] == unknownPlace) stateSituation[(int)(selfLocation[1]+initialY - objectY)][(int)(selfLocation[0]+initialX+objectX)] = door;
//                    continue to;
//
//                case Window  :
//                    if(stateSituation[(int)(selfLocation[1]+initialY - objectY)][(int)(selfLocation[0]+initialX+objectX)] == unknownPlace) stateSituation[(int)(selfLocation[1]+initialY+initialY - objectY)][(int)(selfLocation[0]+initialX+objectX)] = window;
//                    continue to;
//
//                case Teleport:
//                    if(stateSituation[(int)(selfLocation[1]+initialY - objectY)][(int)(selfLocation[0]+initialX+objectX)] == unknownPlace) stateSituation[(int)(selfLocation[1] - objectY)][(int)(selfLocation[0]+initialX+objectX)] = teleport;
//                    continue to;
//
//                case SentryTower:
//                    if(stateSituation[(int)(selfLocation[1]+initialY- objectY)][(int)(selfLocation[0]+initialX+objectX)] == unknownPlace) stateSituation[(int)(selfLocation[1] +initialY- objectY)][(int)(selfLocation[0]+initialX+objectX)] = sentryTower;
//                    continue to;
//
//                case EmptySpace:
//                    if(stateSituation[(int)(selfLocation[1]+initialY - objectY)][(int)(selfLocation[0]+initialX+objectX)] == unknownPlace) stateSituation[(int)(selfLocation[1]+initialY - objectY)][(int)(selfLocation[0]+initialX+objectX)] = emptySpace;
//                    continue to;
//
//                case ShadedArea:
//                    if(stateSituation[(int)(selfLocation[1]+initialY - objectY)][(int)(selfLocation[0]+initialX+objectX)] == unknownPlace) stateSituation[(int)(selfLocation[1]+initialY - objectY)][(int)(selfLocation[0]+initialX+objectX)] = shadedArea;
//                    continue to;
//
//                case Guard:
//                    if(stateSituation[(int)(selfLocation[1]+initialY - objectY)][(int)(selfLocation[0]+initialX+objectX)] == unknownPlace) stateSituation[(int)(selfLocation[1]+initialY - objectY)][(int)(selfLocation[0]+initialX+objectX)] = guard;
//                    continue to;
//
//                case Intruder:
//                    if(stateSituation[(int)(selfLocation[1]+initialY - objectY)][(int)(selfLocation[0]+initialX+objectX)] == unknownPlace) stateSituation[(int)(selfLocation[1]+initialY - objectY)][(int)(selfLocation[0]+initialX+objectX)] = intruder;
//                    continue to;
//                case TargetArea:
//                    continue to;

            }

        }


    }


    //method to update the state based on the chosen action
    public void updateState(Action a){
       if( a instanceof Move){
               Vector pos = new Vector(state.getPos());
               Vector agentOrientation = new Vector(state.getAngle());
               agentOrientation.setLength(((Move) a).getDistance().getValue());
               Vector newpos = pos.add(agentOrientation);
               state.setPos(newpos.x,newpos.y);
       }else if ( a instanceof Rotate){
           state.setAngle(Direction.fromRadians( ((Rotate) a).getAngle().getRadians()));
       }

    }


    /**
     * Based on the current map situation, change the size of the map in order to achieve a better map.
     * @param lastSituation
     * @return a map after changed by a proper size.
     */
    public double[][] changeGridMapSize(double[][] lastSituation){
        double[][] newState = lastSituation.clone();

        return newState;
    }

    public void printGridMap(){

        for (int i = 0;i<mapData.length;i++){
            for (int j = 0;j<mapData[0].length;j++){

                System.out.print(mapData[i][j]+" ");


            }
            System.out.println();
        }



    }

    public boolean isGridMapEmpty(){

        for (int i = 0;i<mapData.length;i++){
            for (int j = 0;j<mapData[0].length;j++){

                if (mapData[i][j] != 0){
                    return false;
                }
            }
        }
        return true;
    }
}
