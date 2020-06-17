package Group7.agent.Intruder;

import Interop.Geometry.Point;
import Interop.Percept.IntruderPercepts;

import java.util.ArrayList;

public class Indiv {

//    private final static int[][] mapData = {
//            {0,0,0 ,0,0 ,0,0 ,0,0 ,1},
//            {0,0,0,0,0,0,0,0,0,1},
//            {0,0,0,0,0,0,0,0,0,1},
//            {0,0,0,0,0,0,0,0,0,1},
//            {0,0,0,0,0,0,0,0,0,1},
//            {0,0,0,0,0,0,0,0,0,1},
//            {0,0,0,0,0,0,0,0,0,1},
//            {1,1,1,1,1,1,1,1,1,1}
//    };

    public static MindMap map;
    public final static double time_interval = 1;
    private static double[] target;
    private static double[] start;
    private static  int nb_of_discrete_actions = 300; //arbitrary value
    private static double max_speed;
    private static double max_angle;
    private static double min_angle;

        private double fitness;
        private ArrayList<Double> speeds = new ArrayList<>();
        private ArrayList<Double> directions = new ArrayList<>();


    public static void setMap(MindMap map) {
        Indiv.map = map;
        start = map.getState().getRealPosArray();
        target = new double[]{map.getTargetPos().getX(),map.getTargetPos().getY()};
    }

    public static void setSenario(IntruderPercepts percepts){
        max_speed = percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder().getValue()/time_interval;
//        max_angle = percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees();
    }

    public static void setNb_of_discrete_actions(int nb_of_discrete_actions) {
        Indiv.nb_of_discrete_actions = nb_of_discrete_actions;
    }

    public Indiv(ArrayList<Double> speeds, ArrayList<Double> directions) {
        this.speeds = speeds;
        this.directions = directions;
        clean();
        comuteFitness();
    }

    public Indiv() {
            for (int i = 0; i < nb_of_discrete_actions ; i++) {
                speeds.add(Math.random()*max_speed);
                int positive = 1;
                if (Math.random()>0.5){
                    positive = -1;
                }
                directions.add(Math.random()*max_angle*positive);
            }
            comuteFitness();
        }

        public Indiv(Indiv newPath) {
            speeds = newPath.getSpeeds();
            directions = newPath.getDirections();
            comuteFitness();
        }

    public static double getMax_speed() {
        return max_speed;
    }

    public double getFitness() {
           return fitness;
       }


    public ArrayList<Double> getSpeeds() {
        return speeds;
    }

    public void setSpeeds(ArrayList<Double> speeds) {
        this.speeds = speeds;
    }

    public ArrayList<Double> getDirections() {
        return directions;
    }

    public void setDirections(ArrayList<Double> directions) {
        this.directions = directions;
    }

    protected Indiv clone() {
            Indiv clonedPath = new Indiv(arrayCopy(speeds),arrayCopy(directions));
            return clonedPath;
        }

        private ArrayList<Double> arrayCopy(ArrayList<Double> a){
            ArrayList<Double> out = new ArrayList<>();
                  for (Double d :a){
                      out.add(new Double(d));
                  }
               return out;
        }


        public void comuteFitness(){
            fitness = 0;
            fitness -= Math.pow(distance_cost(),5);
            fitness -= Math.sqrt(path_length_cost())*5;
            int collision_penality = 10000;
            fitness -= collision_penality*obstacle_cost();
        }

        public double distance_cost(){

            Vector end_point = new Vector(start);
            int i=0;
           for(Double d : directions){
             Vector move = new Vector(d);
             move.setLength(speeds.get(i)*time_interval);
             end_point.add(move);
               i++;
           }
          double out =  Math.sqrt(Math.pow(end_point.x-target[0],2)+Math.pow(end_point.y-target[1],2));
           if(out<0){
               System.out.println();
               System.out.println(out);
               System.out.println(start);
               System.out.println(end_point);
               System.out.println();
           }
           return out;
        }

        public double path_length_cost(){
        double out=0;

          for(Double speed: speeds){
              out+=speed*time_interval;
          }
          out =Math.sqrt(out)*5;
          return out;
        }

        public double obstacle_cost(){

            double collision_nb = 0;
            Vector end_point = new Vector(start);
            int i=0;
            for(Double d : directions){
                Vector move = new Vector(d);
                move.setLength(speeds.get(i)*time_interval);
                if(isCollision(new Point(end_point.x,end_point.y), new Point(end_point.x+move.x,end_point.y+move.y))){
                    collision_nb++;
                }
                i++;
                end_point.add(move);
            }
           return collision_nb;
        }



        public static boolean isCollision(Point s, Point s2){
            double minY = Math.min(s.getY(),s2.getY());
            double maxY = Math.max(s.getY(),s2.getY());
            double minX = Math.min(s.getX(),s2.getX());
            double maxX = Math.max(s.getX(),s2.getX());
//            System.out.println();
//            System.out.println("maxY = " + maxY);
//            System.out.println("minY = " + minY);
//            System.out.println("minX = " + minX);
//            System.out.println("maxX = " + maxX);
//             s.print();
//             s2.print();

            if(s.getX()-s2.getX()==0){
//                System.out.println("axe y ");
                for(Point p : map.unWalkablePointList()) {
//                    System.out.println("p = " + p.getX()+", "+p.getY());
                    if(p.getX()+0.5>=s.getX() && p.getX()-0.5<=s.getX()){

                        if((p.getY()-0.5<=maxY && maxY<= p.getY()+0.5) || (p.getY()-0.5<=minY && minY<= p.getY()+0.5)){
                            return true;
                        }
                    }
                }
                return false;
            }
            double m1 = (s.getY()-s2.getY())/(s.getX()-s2.getX());
            double b1 = s.getY() - s.getX()*m1;
//            System.out.println("b1 = " + b1);
//            System.out.println("m1 = " + m1);

            for(Point p : map.unWalkablePointList()){
//                System.out.println("p = " + p.getX()+", "+p.getY());
                Point p2 = findIntersectionX(m1,p.getX()+0.5,b1);
                if( p.getY()-0.5<=p2.getY() && p2.getY()<=p.getY()+0.5) {
                    if(p2.getX()>=minX && maxX>=p2.getX()) {
//                        System.out.println("case 3");
//                        System.out.println("p2 = " + p2.getX() + ", " + p2.getY());
                        return true;
                    }
                }
                p2 = findIntersectionX(m1,p.getX()-0.5,b1);
                if( p.getY()-0.5<=p2.getY() && p2.getY()<=p.getY()+0.5) {
                    if(p2.getX()>=minX && maxX>=p2.getX()) {
//                        System.out.println("case 4");
//                        System.out.println("p2 = " + p2.getX() + ", " + p2.getY());
                        return true;
                    }
                }
                if(m1!=0){
                p2 = findIntersectionY(m1,p.getY()+0.5,b1);
                    if(p.getX()-0.5<=p2.getX() && p2.getX()<=p.getX()+0.5) {
                        if(p2.getY()>=minY && maxY>=p2.getY()) {
//                            System.out.println("case 1");
//                            System.out.println("p2 = " + p2.getX() + ", " + p2.getY());
                            return true;
                        }
                    }
                    p2 = findIntersectionY(m1,p.getY() - 0.5,b1);
                    if (p.getX() - 0.5 <= p2.getX() && p2.getX() <= p.getX() + 0.5) {
                        if(p2.getY()>=minY && maxY>=p2.getY()) {
//                            System.out.println("case 2");
//                            System.out.println("p2 = " + p2.getX() + ", " + p2.getY());
                            return true;
                        }
                    }
                }else{
//                    System.out.println("Horizontal");
                }
            }
            return false;
        }


    public static Point findIntersectionY(double m1, double Y, double b1){
        return new Point((Y-b1)/m1,Y);
    }

    public static Point findIntersectionX(double m1, double X, double b1){
      return new Point(X,m1*X+b1);
    }
       private void clean(){
            int size = speeds.size();
            int decreased = 0;
           for (int j = 0; j < size ; j++) {
               Double d= speeds.get(j-decreased);
                if (d==0.0){
                    speeds.remove(j-decreased);
                    directions.remove(j-decreased);
                    decreased++;
                }
            }

    }

    public static double getMax_angle() {
        return max_angle;
    }

    public static double getMin_angle() {
        return min_angle;
    }

    //       public static void main(String [] args){
////           ArrayList<Double> speeds = new ArrayList<>();
////           speeds.add(0.0);
////           speeds.add(0.0);
////           speeds.add(0.0);
////           speeds.add(0.0);
////
////           ArrayList<Double> directions = new ArrayList<>();
////           directions.add(43.0);
////           directions.add(3.0);
////           directions.add(13.0);
////           directions.add(45.0);
////
////           Indiv a = new Indiv(speeds,directions);
////           System.out.println(a.speeds);
////           System.out.println(a.directions);
////           System.out.println(isCollision(new Point(6.8288648657381295,7.806497172302989),new Point(7.3900111356673985,9.618648953165438)));
//           ArrayList sp = new ArrayList();
//           sp.add(9.249092252042763);sp.add(9.249092252042763);sp.add( 4.163994506914808);sp.add( 8.497247805051707);sp.add( 5.99617942905757);sp.add( 8.639322869059713);sp.add( 8.639322869059713);sp.add( 9.48522431619105);sp.add( 8.580982999975813);sp.add( 8.580982999975813);sp.add( 4.511288045296569);sp.add( 10.0);sp.add( 9.804581496772641);sp.add( 9.804581496772641);sp.add( 9.804581496772641);sp.add( 8.160720825757602);sp.add( 10.0);sp.add( 5.537357372201091);sp.add( 5.457375266798804);sp.add( 7.836221286072354);sp.add( 5.5167383172768805);sp.add( 8.527436011845854);sp.add( 0.0);sp.add( 7.111245435286699);sp.add( 5.411742944468262);
//
//                   ArrayList dir = new ArrayList();
//           dir.add( 56.946304688365636); dir.add( 53.74616695533666); dir.add( 44.18880815295253); dir.add( 54.50601827181666); dir.add( 53.42018037180731); dir.add( 65.36333654287233); dir.add( 8.876607306396538); dir.add( 72.79447230681002); dir.add( 70.79681367524333); dir.add( 44.56818508713119); dir.add( 49.085091426188505); dir.add( 49.085091426188505); dir.add( 49.085091426188505); dir.add( 49.085091426188505); dir.add( 37.375997504012595); dir.add( 25.535955740755448); dir.add( 37.362519533279176); dir.add( 59.77500464105904); dir.add( 50.10960693532775); dir.add( 11.703518201741787); dir.add( -5.548412705068436); dir.add(83.45895672920493); dir.add( 29.644812847610282); dir.add( 51.42759345908297);
//
//           System.out.println(obstacle_cost(sp,dir));
//       }



}

