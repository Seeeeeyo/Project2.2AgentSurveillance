package Group7.gui2;

import Group7.agent.container.IntruderContainer;
import Group7.map.dynamic.DynamicObject;
import Group7.agent.container.GuardContainer;
import javafx.application.Application;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Gui extends Application {
    private File mapFile = new File("./src/main/java/Group7/map/maps/test_2.map");
    private MainController mainController = new MainController(this, mapFile, true);
    private MainScene scene = new MainScene(new StackPane(), mainController.getGame().getGameMap(), this);
    private Stage primary = new Stage();

    // Threshold For Distance
    private final double THRESHOLD = 10.0;

    // List For Integers
    private List<Integer> listOfIndex = new ArrayList<>();


    public static void Gui(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) {
        primaryStage.setHeight(GuiSettings.defaultHeight);
        primaryStage.setWidth(GuiSettings.defaultWidth);
        primaryStage.setTitle("Orwells Dream");
        primaryStage.setScene(scene);
        primary = primaryStage;
        primaryStage.show();
        scene.rescale();
        Thread thread = new Thread(mainController);
        thread.start();
        primaryStage.setOnCloseRequest(event -> {
            System.out.println("See Ya");
            mainController.kill();
        });
    }


    public void drawMovables(List<GuardContainer> guards, List<IntruderContainer> intruders, List<DynamicObject<?>> objects) {
        System.out.println("Intruders_Size: " + intruders.size());
        System.out.println("List_Index_Size: " + listOfIndex.size());

        for (int i = 0; i < intruders.size(); i++) {
            for (GuardContainer guard : guards) {
                double distance = getDistance(intruders.get(i).getPosition().getX(), intruders.get(i).getPosition().getY(), guard.getPosition().getX(), guard.getPosition().getY());
                if (distance < THRESHOLD && !intruders.get(i).isCaptured()) {
                    System.out.println("Distance_To_Eliminate: " + distance);
                    intruders.get(i).setCaptured(true);
                    mainController.getGame().deleteIntruder(i);
                }
            }
        }

        scene.drawMovables(guards, intruders, objects);
    }

    public void activateHistory() {
        if (!scene.isHasHistory()) {
            scene.activateHistory();
        }
    }

    public Stage getPrimary() {
        return primary;
    }

    public MainController getMainController() {
        return mainController;
    }

    public void restartGame(boolean generateHistory) {
        mainController.kill();
        mainController = new MainController(this, mapFile, generateHistory);
        Thread thread = new Thread(mainController);
        thread.start();
    }

    public void setMapFile(File mapFile) {
        this.mapFile = mapFile;
    }

    public File getMapFile() {
        return mapFile;
    }

    public double getDistance(double intruderX, double intruderY, double guardX, double guardY) {
        return Math.sqrt((guardX - intruderX) * (guardX - intruderX) + (guardY - intruderY) * (guardY - intruderY));
    }
}
