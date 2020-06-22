package Group7.map.mapGenerator;

import Group7.map.mapGenerator.logic.MapGenerator;
import Group7.map.mapGenerator.logic.RoomFactory;

import java.io.*;
import java.util.Random;


public class MapWriterManual {

    public static void main(String[] args) throws IOException {


        String inFilePath = "./src/main/java/Group7/map/maps/testMapGeneratorFixedParameters.map";
        String inFilePathCodeMap = "./src/main/java/Group7/map/maps/codeMap";
        String outFilePath = "./src/main/java/Group7/map/maps/generatedMap.map";


        int columnPointer = 0;
        int rowPointer = 0;
        boolean placedIntrudersSpawn = false;
        String readLine;
        final int scale = 1;

        //wall = 0.0 , 0.0, 2.0 , 0.0  , 2.0 , 2.0 , 0.0 , 2.0
        double x1,x2,x3,x4;
        double y1,y2, y3, y4;

        final double originalX1 = 0, originalX2 = 1.0, originalX3 = 1.0, originalX4 = 0;
        final double originalY1 = 0, originalY2 = 0.0, originalY3 = 1.0, originalY4 = 1;

        int innerSpaceBlockCount = 0;
        int wallBlockCount = 0;

        //System.out.println(myMap);
        //System.out.println("--------");


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

                while ((readLine = bufferedReader2.readLine()) != null) {

                    String string = readLine;

                    char[] array = string.toCharArray();
                    // System.out.println(array);

                    for (int i = 0; i < array.length; i++) {
                        //System.out.println(i + ": " + array[i]);

                        if (array[i] == '#') {
                            wallBlockCount++;
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
                            columnPointer += scale;

                        } else if (array[i] == '.') {
                            innerSpaceBlockCount++;

                            // Generate random integer in range 0 to 999
                            int toPlaceOrNotTo = rand.nextInt(1000);

                            if (toPlaceOrNotTo > 1 && toPlaceOrNotTo < scale * 50) {

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

                                columnPointer += scale;

                            } else {

                                columnPointer += scale;
                                System.out.print(".");

                            }
                        } else if (array[i] == ' ') {

                            columnPointer += scale;
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

                            columnPointer += scale;

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

                            columnPointer += scale;
                        }

                        else if (array[i] == '9') {    //in case you manually placed the spawn block


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

                            columnPointer += scale;
                        }


                    }

                    rowPointer += scale;
                    columnPointer = 0;
                    writer.write('\n');
                    System.out.println("");

                }

                System.out.println("---------------------");
                System.out.println("Wall blocks count : " + wallBlockCount);
                System.out.println("Inner Space Blocks count: " + innerSpaceBlockCount);
                System.out.println("---------------------");
            }

        } catch (IOException e) {
            System.out.println("Error reading the file containing the fixed parameters");
            e.printStackTrace();
        }

    }

}
