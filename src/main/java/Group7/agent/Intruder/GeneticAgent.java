package Group7.agent.Intruder;

import Group7.Game;
import Interop.Action.IntruderAction;
import Interop.Action.Move;
import Interop.Action.Rotate;
import Interop.Agent.Intruder;
import Interop.Geometry.Angle;
import Interop.Geometry.Distance;
import Interop.Percept.IntruderPercepts;
import Interop.Percept.Scenario.SlowDownModifiers;

import java.util.ArrayList;

public class GeneticAgent implements Intruder
{

    private MindMap map = new MindMap();

    double [] speedsAr= {1.352357584687929, 1.4, 1.1645393344958084, 0.38746434887890635, 1.3964097573158172, 1.4, 0.9225410015730483, 0.27374665724019565, 1.4, 1.1113443245539298, 0.17130907357531347, 0.15956768492858395, 0.2550646855931071, 1.1063752175607136, 1.4, 1.4, 1.4, 1.4, 1.390396467001856, 1.2861256650447859, 0.29808349286167324, 1.3648278077183287, 1.4, 1.1351064976657517, 1.2724969834981528, 0.9323391395011862, 1.2301788014689035, 1.21041262180047, 0.9695726062396629, 0.3120995845775025, 1.4, 1.4, 1.3651186439347602, 1.3651186439347602, 1.3651186439347602, 1.3651186439347602, 1.3651186439347602, 1.3651186439347602, 1.3651186439347602, 1.3651186439347602, 1.3651186439347602, 1.3651186439347602, 1.3651186439347602, 1.3651186439347602, 0.9551085328119928, 0.8997851206183781, 1.275164950082839, 1.2161139761954416, 1.3880339687894145, 1.4, 1.4, 1.4, 1.2432591360990763, 1.2432591360990763, 1.3868309318122318, 1.4, 0.8236476886362366, 0.4552344882034432, 1.338748131726042, 0.6006743977851556, 1.341612910989737, 0.8433366265481596, 0.8433366265481596, 1.4, 0.8433366265481596, 1.2993119082835884, 1.2993119082835884, 0.8433366265481596, 0.9102311184087866, 1.4, 1.4, 1.4, 1.4, 1.4, 1.4, 0.8661633814364453, 0.7436355113628939, 0.4815071819257925, 0.9960619747494799, 0.44215738541701405, 1.4, 1.4, 1.4, 0.41693727209060427, 1.2736191732700974, 1.2736191732700974, 1.254370658560933, 1.2736191732700974, 1.2736191732700974, 1.2736191732700974, 1.4, 1.3782320642543533, 0.5098688489069771, 1.3944813602396962, 0.5098688489069771, 1.3944813602396962, 1.3944813602396962, 1.3944813602396962, 0.5098688489069771, 1.3944813602396962, 0.8731367501340913, 0.6777172671031595, 1.039092707402747, 0.7221293202447449, 0.757356368821846, 1.4,
    };
    ArrayList<Double> speeds = toArraylist(speedsAr);

    double [] dirAr ={ -301.004881022004, -243.4158700813834, 136.0087241353989, 102.90200697919494, -230.912797239827, 81.82119790588023, -194.75221339213832, 32.387240042513525, 111.91651257719445, 93.34347722339163, 83.91039375248664, -315.5591895134197, 134.43328525719247, -201.04075182577975, -212.73951084905346, 112.71669269388221, 112.71669269388221, 112.71669269388221, 84.37580710159972, 64.66689589147674, -273.91980123344274, 114.79859511790075, 119.55713585572782, 101.8409675229384, -269.0192427532976, 124.64997142593563, -311.4027013792719, 86.79130911219052, 29.25575818918924, -180.0, 94.35627908311909, 119.28609465574694, 119.28609465574694, 119.28609465574694, 119.28609465574694, 119.28609465574694, 119.28609465574694, 119.28609465574694, 119.28609465574694, 119.28609465574694, 119.28609465574694, 119.28609465574694, 127.7798984240927, 127.7798984240927, -222.40728202868445, -207.388992983276, -310.32786043699423, 110.76362099572873, 141.75380217304377, 109.40392642525926, 109.40392642525926, 109.40392642525926, 109.40392642525926, 109.40392642525926, 180.0, 151.7623164702385, 123.04207913633317, -333.55536694125584, 128.1449233995088, -322.8886666027916, 128.52578379727004, 100.09777437322879, 100.09777437322879, -281.9820089210389, 100.09777437322879, 100.09777437322879, 100.09777437322879, 100.09777437322879, -281.9820089210389, -281.9820089210389, -228.3642277673208, -228.3642277673208, -228.3642277673208, 129.26095647589779, 129.26095647589779, 180.0, 171.20988209708554, 171.20988209708554, -180.74050317470264, 140.03139962730887, 102.18852635575129, 102.18852635575129, 102.18852635575129, 55.98174371200857, -280.43889128736623, -280.43889128736623, 170.09113023383142, -280.43889128736623, -280.43889128736623, -280.43889128736623, 113.48185266687861, 147.19191331444765, 106.33547251224886, 126.81278378420262, 106.33547251224886, 126.81278378420262, 126.81278378420262, 126.81278378420262, 106.33547251224886, 126.81278378420262, -65.7795244623282, 44.06940116196216, 43.24598207508017, -196.576998658108, 152.24021131930937, 108.31232703145106,
    };
    ArrayList<Double> directions = toArraylist(dirAr);

    private Indiv path;


    @Override
    public IntruderAction getAction(IntruderPercepts percepts) {
        System.out.println();
        System.out.println("GeneticAgent.getAction");

        map.updateGridMap(percepts);
        map.computeTargetPoint(percepts.getTargetDirection());

        Indiv.setSenario(map,percepts);

        if(!percepts.wasLastActionExecuted())
        {
            System.out.println("GeneticAgent.getAction rejected");

            Angle random_rotation_angle =  Angle.fromRadians(percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians() * Game._RANDOM.nextDouble());
            System.out.println("rotation_angle = " + random_rotation_angle.getRadians());
            System.exit(1);
            return new Rotate(random_rotation_angle);
        }

        if(path == null) {
            System.out.println("First turn");
//            path = GeneticAlgorithm.computePath();
            path = new Indiv(speeds,directions);
        }
        double collision = path.obstacle_cost();
        System.out.println("collision = " + collision);
        if(collision>0) {
            System.out.println("Re compute");
            path = GeneticAlgorithm.computePath(GeneticAlgorithm.reproduct(new Indiv[]{path,path}));
        }

       IntruderAction a = computeAction(path,percepts);
        map.updateState(a);
        return a;
    }

    public ArrayList<Double> toArraylist(double[]a ){
        ArrayList<Double> out = new ArrayList<>();
        for (int i = 0; i < a.length; i++) {
            out.add(a[i]);
        }
        return out;
    }

    public IntruderAction computeAction(Indiv path, IntruderPercepts percepts){
        double current_angle = map.getState().getAngle();
        double angle = path.getDirections().get(0);
        System.out.println("angle = " + angle);
        System.out.println("current_angle = " + current_angle);

     if(current_angle == angle){ // if the agent is in the good direction, move
         double dist = path.getSpeeds().get(0)/Indiv.getTime_interval();
         System.out.println("dist = " + dist);
         double max_dist = percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder().getValue()*getSpeedModifier(percepts);

       if(max_dist>=dist){
            path.getDirections().remove(0);
            path.getSpeeds().remove(0);
        }else{
               double dist2go = dist - max_dist;
               dist = max_dist;
               path.getSpeeds().set(0, dist2go / Indiv.getTime_interval());
       }
      return new Move(new Distance(dist));

     }else{ //otherwise, rotate to the good direction

         return rotateTo(angle,percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle());

     }
}

    private Rotate rotateTo(double astar_angle, Angle max_rotation){

        double old_angle = map.getState().getAngle();
        Angle rotation_angle = Angle.fromDegrees(astar_angle-old_angle);

        if(rotation_angle.getDegrees() > max_rotation.getDegrees()){
            rotation_angle =max_rotation;
        }else if(rotation_angle.getDegrees() < -max_rotation.getDegrees()){
            rotation_angle = max_rotation;
            rotation_angle = Angle.fromRadians(-rotation_angle.getRadians());
        }
        System.out.println("Rotate to "+astar_angle+"; old angle is "+old_angle+"; new angle is "+(old_angle+rotation_angle.getDegrees()));

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
}
