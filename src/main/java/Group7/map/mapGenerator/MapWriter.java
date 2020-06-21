package Group7.map.mapGenerator;

import Group7.map.mapGenerator.logic.MapGenerator;
import Group7.map.mapGenerator.logic.RoomFactory;

import java.io.*;
import java.util.Random;


public class MapWriter {

    public static void main(String[] args) throws IOException {


        String inFilePath = "./src/main/java/Group7/map/maps/testMapGeneratorFixedParameters.map";
        String inFilePathCodeMap = "./src/main/java/Group7/map/maps/codeMap";

        String outFilePath = "./src/main/java/Group7/map/maps/generatedMap.map";

        final int maximumHeight = 20;
        final int maximumWidth = 20;
        final boolean fixedSize = false;
        final int maximumX = 200;
        final int maximumY = 70;
        final int rooms = 50;
        final int maxTargetBlocks = 3;

        int columnPointer = 0;
        int rowPointer = 0;
        boolean placedIntrudersSpawn = false;
        boolean placedGuardsSpawn = false;
        String readLine;


        //wall = 0.0 , 0.0, 2.0 , 0.0  , 2.0 , 2.0 , 0.0 , 2.0
        //
        double x1 = 0, x2 = 1.0, x3 = 1.0, x4 = 0;
        double y1 = 0, y2 = 0, y3 = 1.0, y4 = 1.0;

        final double originalX1 = 0, originalX2 = 1.0, originalX3 = 1.0, originalX4 = 0;
        final double originalY1 = 0, originalY2 = 0.0, originalY3 = 1.0, originalY4 = 1;

        RoomFactory roomFactory = new RoomFactory();

        roomFactory.setMaximumHeight(maximumHeight);
        roomFactory.setMaximumWidth(maximumWidth);
        roomFactory.setFixedSize(fixedSize);
        roomFactory.setMaximumX(maximumX);
        roomFactory.setMaximumY(maximumY);

        MapGenerator generator = new MapGenerator(roomFactory);
        Map myMap = generator.generateMap(rooms);

        //System.out.println(myMap);
        //System.out.println("--------");

        FileWriter fw = new FileWriter(inFilePathCodeMap);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(String.valueOf(myMap));
        bw.newLine();
        bw.close();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(inFilePath));
             Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFilePath), "utf-8"))
        ) {
            while ((readLine = bufferedReader.readLine()) != null) {

                String string = readLine;
                System.out.println(string);

                writer.write(string);
                writer.write(System.lineSeparator());
            }


            try (BufferedReader bufferedReader2 = new BufferedReader(new FileReader(inFilePathCodeMap));) {

                Random rand = new Random();
                int randomColumnSpawnInt = rand.nextInt(50);
                int randomColumnSpawnGuardsInt = rand.nextInt(20);

                while ((readLine = bufferedReader2.readLine()) != null) {

                    String string = readLine;

                    int inisdeBlockCounter = 0;
                    int targetAreasPlaced = 0;

                    char[] array = string.toCharArray();
                    // System.out.println(array);

                    for (int i = 0; i < array.length; i++) {
                        //System.out.println(i + ": " + array[i]);

                        if (array[i] == '#') {

                            x1 = originalX1 + columnPointer;
                            x2 = originalX2 + columnPointer;
                            x3 = originalX3 + columnPointer;
                            x4 = originalX4 + columnPointer;
                            y1 = originalY1 + rowPointer;
                            y2 = originalY2 + rowPointer;
                            y3 = originalY3 + rowPointer;
                            y4 = originalY4 + rowPointer;

                            System.out.print("#");
                            String wallBlock = ("wall = " + x1 + columnPointer + "," + y1 + "," + x2 + columnPointer + "," + y2 + "," + x3 + columnPointer + "," + y3 + "," + x4 + columnPointer + "," + y4);
                            writer.write(wallBlock);
                            writer.write('\n');
                            columnPointer += 1;

                        } else if (array[i] == '.') {

                            inisdeBlockCounter += 1;

                            if (inisdeBlockCounter == randomColumnSpawnInt && placedIntrudersSpawn == false) {

                                x1 = originalX1 + columnPointer;
                                x2 = originalX2 + columnPointer;
                                x3 = originalX3 + columnPointer;
                                x4 = originalX4 + columnPointer;
                                y1 = originalY1 + rowPointer;
                                y2 = originalY2 + rowPointer;
                                y3 = originalY3 + rowPointer;
                                y4 = originalY4 + rowPointer;

                                System.out.print("2");
                                String spawnAreaIntrudersBlock = ("spawnAreaIntruders = " + x1 + columnPointer + "," + y1 + "," + x2 + columnPointer + "," + y2 + "," + x3 + columnPointer + "," + y3 + "," + x4 + columnPointer + "," + y4);
                                writer.write(spawnAreaIntrudersBlock);
                                writer.write('\n');

                                columnPointer += 1;
                                placedIntrudersSpawn = true;

                            }


                            else if (rowPointer >= 70 && columnPointer == randomColumnSpawnGuardsInt && placedGuardsSpawn == false) {

                                x1 = originalX1 + columnPointer;
                                x2 = originalX2 + columnPointer;
                                x3 = originalX3 + columnPointer;
                                x4 = originalX4 + columnPointer;

                                y1 = originalY1 + rowPointer;
                                y2 = originalY2 + rowPointer;
                                y3 = originalY3 + rowPointer;
                                y4 = originalY4 + rowPointer;

                                System.out.print("3");
                                String spawnAreaIntrudersBlock = ("spawnAreaGuards = " + x1 + columnPointer + "," + y1 + "," + x2 + columnPointer + "," + y2 + "," + x3 + columnPointer + "," + y3 + "," + x4 + columnPointer + "," + y4);
                                writer.write(spawnAreaIntrudersBlock);
                                writer.write('\n');
                                columnPointer++;
                                placedGuardsSpawn = true;

                            }

                            // Generate random integer in range 0 to 999
                            int toPlaceOrNotTo = rand.nextInt(1000);

                            if (toPlaceOrNotTo > 1 && toPlaceOrNotTo < 50) {

                                x1 = originalX1 + columnPointer;
                                x2 = originalX2 + columnPointer;
                                x3 = originalX3 + columnPointer;
                                x4 = originalX4 + columnPointer;

                                y1 = originalY1 + rowPointer;
                                y2 = originalY2 + rowPointer;
                                y3 = originalY3 + rowPointer;
                                y4 = originalY4 + rowPointer;

                                System.out.print("1");
                                String shadedBlock = ("shaded = " + x1 + columnPointer + "," + y1 + "," + x2 + columnPointer + "," + y2 + "," + x3 + columnPointer + "," + y3 + "," + x4 + columnPointer + "," + y4);
                                writer.write(shadedBlock);
                                writer.write('\n');

                                columnPointer += 1;

                            } else if (toPlaceOrNotTo <= 0.05 && rowPointer >= 50 && targetAreasPlaced <= maxTargetBlocks) {
                                // Assuming we place the targetAreas exclusively in the bottom half

                                x1 = originalX1 + columnPointer;
                                x2 = originalX2 + columnPointer;
                                x3 = originalX3 + columnPointer;
                                x4 = originalX4 + columnPointer;

                                y1 = originalY1 + rowPointer;
                                y2 = originalY2 + rowPointer;
                                y3 = originalY3 + rowPointer;
                                y4 = originalY4 + rowPointer;

                                System.out.print("9");
                                String targetBlock = ("targetArea = " + x1 + columnPointer + "," + y1 + "," + x2 + columnPointer + "," + y2 + "," + x3 + columnPointer + "," + y3 + "," + x4 + columnPointer + "," + y4);
                                writer.write(targetBlock);
                                writer.write('\n');

                                targetAreasPlaced++;
                                System.out.print(targetAreasPlaced);
                                columnPointer += 1;

                            } else {

                                columnPointer += 1;
                                System.out.print(".");

                            }
                        } else if (array[i] == ' ') {

                            columnPointer += 1;
                            System.out.print(" ");
                        }

                        // in case you manually placed the spawn block into the codemap
                        else if (array[i] == '2') {

                            x1 = originalX1 + columnPointer;
                            x2 = originalX2 + columnPointer;
                            x3 = originalX3 + columnPointer;
                            x4 = originalX4 + columnPointer;

                            y1 = originalY1 + rowPointer;
                            y2 = originalY2 + rowPointer;
                            y3 = originalY3 + rowPointer;
                            y4 = originalY4 + rowPointer;

                            System.out.print("2");
                            String spawnAreaIntrudersBlock = ("spawnAreaIntruders = " + x1 + columnPointer + "," + y1 + "," + x2 + columnPointer + "," + y2 + "," + x3 + columnPointer + "," + y3 + "," + x4 + columnPointer + "," + y4);
                            writer.write(spawnAreaIntrudersBlock);
                            writer.write('\n');

                            columnPointer += 1;

                        } else if (array[i] == '3') {    //in case you manually placed the spawn block


                            x1 = originalX1 + columnPointer;
                            x2 = originalX2 + columnPointer;
                            x3 = originalX3 + columnPointer;
                            x4 = originalX4 + columnPointer;

                            y1 = originalY1 + rowPointer;
                            y2 = originalY2 + rowPointer;
                            y3 = originalY3 + rowPointer;
                            y4 = originalY4 + rowPointer;

                            System.out.print("3");
                            String spawnAreaGuardsBlock = ("spawnAreaGuards = " + x1 + columnPointer + "," + y1 + "," + x2 + columnPointer + "," + y2 + "," + x3 + columnPointer + "," + y3 + "," + x4 + columnPointer + "," + y4);
                            writer.write(spawnAreaGuardsBlock);
                            writer.write('\n');

                            columnPointer += 1;
                        }

                    }

                    rowPointer += 1;
                    columnPointer = 0;
                    writer.write('\n');
                    System.out.println("");

                }

            }

        } catch (IOException e) {
            System.out.println("Error reading the file containing the fixed parameters");
            e.printStackTrace();
        }

    }

}
