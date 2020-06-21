package Group7.agent.NeuralNetwork;

import java.io.*;

public class Files {
    //now, IO part works good
    //path for ANN trained 30 thousands times
    public static String hiddenWeightPath= "initialHiddenWeight.txt";
    public static String biasWeightPath = "initialBiasWeight.txt";
    public static String inputWeightPath = "initialInputWeight.txt";

    public static String iniHiddenWeightPath= "initialHiddenWeight.txt";
    public static String iniBiasWeightPath = "initialBiasWeight.txt";
    public static String iniInputWeightPath = "initialInputWeight.txt";

    public static String sampleForTrainPath = "/Users/luotianchen/DiceUp/src/main/resources/ANN/sampleForTrain.txt";
    public static String initial_InputWeight = "/Users/luotianchen/DiceUp/src/main/resources/ANN/initial_InputWeight.txt";
    public static String initial_HiddenWeight = "/Users/luotianchen/DiceUp/src/main/resources/ANN/initial_HiddenWeight.txt";
    public static String initial_BiasWeight = "/Users/luotianchen/DiceUp/src/main/resources/ANN/initial_BiasWeight.txt";


    public static String hiddenWeightPath1 = "hiddenWeight1.txt";
    public static String hiddenWeightPath2 = "hiddenWeight2.txt";
    public static String hiddenWeightPath3 = "hiddenWeight3.txt";
    public static String hiddenWeightPath4 = "hiddenWeight4.txt";
    public static String hiddenWeightPath5 = "NeuralNetwork/NN/hiddenWeight5.txt";
    public static String hiddenWeightPath6 = "hiddenWeight6.txt";
    public static String hiddenWeightPath7 = "hiddenWeight7.txt";
    public static String hiddenWeightPath8 = "hiddenWeight8.txt";
    public static String hiddenWeightPath9 = "hiddenWeight9.txt";
    public static String hiddenWeightPath10 = "hiddenWeight10.txt";
    public static String hiddenWeightPath11 = "hiddenWeight11.txt";
    public static String hiddenWeightPath12 = "hiddenWeight12.txt";

    public static String inputWeightPath1 = "inputWeight1.txt";
    public static String inputWeightPath2 = "inputWeight2.txt";
    public static String inputWeightPath3 = "inputWeight3.txt";
    public static String inputWeightPath4 = "inputWeight4.txt";
    public static String inputWeightPath5 = "inputWeight5.txt";
    public static String inputWeightPath6 = "inputWeight6.txt";
    public static String inputWeightPath7 = "inputWeight7.txt";
    public static String inputWeightPath8 = "inputWeight8.txt";
    public static String inputWeightPath9 = "inputWeight9.txt";
    public static String inputWeightPath10 = "inputWeight10.txt";
    public static String inputWeightPath11 = "inputWeight11.txt";
    public static String inputWeightPath12 = "inputWeight12.txt";

    public static String biasWeightPath1 = "biasWeight1.txt";
    public static String biasWeightPath2 = "biasWeight2.txt";
    public static String biasWeightPath3 = "biasWeight3.txt";
    public static String biasWeightPath4 = "biasWeight4.txt";
    public static String biasWeightPath5 = "biasWeight5.txt";
    public static String biasWeightPath6 = "biasWeight6.txt";
    public static String biasWeightPath7 = "biasWeight7.txt";
    public static String biasWeightPath8 = "biasWeight8.txt";
    public static String biasWeightPath9 = "biasWeight9.txt";
    public static String biasWeightPath10 = "biasWeight10.txt";
    public static String biasWeightPath11 = "biasWeight11.txt";
    public static String biasWeightPath12 = "biasWeight12.txt";

    //training times for ANN
    public static int trainingTimes = 10;

    //parameter that determines how much the update are influenced by the events that occurs later in time.
    public static double lambda = 0.7;

    //switcher to see debug message
    public static boolean debug = true;


    public static void main(String[] args){

        int times = 1;//choose how many times you want to train it.
        long startTime=System.currentTimeMillis();
//        trainWhateverTimes(times);
        long endTime=System.currentTimeMillis();

        System.out.println("It cost ： "+(endTime-startTime)+" ms to train "+times +" games");

    }





    //method for IO stream
    public static double[] readHiddenWeight(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;

        NeuralNetwork nn = new NeuralNetwork();

        double[] array = new double[nn.getHiddenNumber()];

        try {

            reader = new BufferedReader(new FileReader(file));

            String tempString = null;

            int line = 0;

            while ((tempString = reader.readLine()) != null) {

                Double i = Double.parseDouble(tempString);

                array[line] = i;

                //System.out.println("line " + line + ": " + tempString);

                line++;
            }

            reader.close();

        } catch (IOException e) {

            e.printStackTrace();
        }

        return array;
    }

    public static double[] readBiasWeight(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;

        NeuralNetwork nn = new NeuralNetwork();

        double[] array = new double[nn.getHiddenNumber()];

        try {

            reader = new BufferedReader(new FileReader(file));

            String tempString = null;

            int line = 0;

            while ((tempString = reader.readLine()) != null) {

                Double i = Double.parseDouble(tempString);

                array[line] = i;

                //System.out.println("line " + line + ": " + tempString);

                line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return array;

    }

    public static double[][] readInputWeight(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;

        NeuralNetwork nn = new NeuralNetwork();

        int row = nn.getHiddenNumber();

        int column = 198;

        double[][] array = new double[row][column];

        double [] temp = new double[row*column];

        try {
            reader = new BufferedReader(new FileReader(file));

            String tempString = null;

            int line = 0;


            while ((tempString = reader.readLine()) != null) {

                Double i = Double.parseDouble(tempString);

                temp[line] = i;

                //System.out.println("line " + line + ": " + tempString);

                line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int rowCtr = 0;

        int colCtr = 0;

        for (int i = 0;i<temp.length;i++) {

            if (i != 0 && i % column == 0) {
                rowCtr++;
            }

            if (colCtr == column) {
                colCtr = 0;
            }

            array[rowCtr][colCtr] = temp[i];

            colCtr++;

        }

        return array;

    }

    public double[][] readRecordOfAGame(String fileName){
        File file = new File(fileName);

        BufferedReader reader = null;

        NeuralNetwork nn = new NeuralNetwork();

        int row = 1000;

        int column = 29;

        double[][] array = new double[row][column];

        double [] temp = new double[row*column];

        try {
            reader = new BufferedReader(new FileReader(file));

            String tempString = null;

            int line = 0;


            while ((tempString = reader.readLine()) != null) {

                Double i = Double.parseDouble(tempString);

                temp[line] = i;

                //System.out.println("line " + line + ": " + tempString);

                line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int rowCtr = 0;

        int colCtr = 0;

        for (int i = 0;i<temp.length;i++) {

            if (i != 0 && i % column == 0) {
                rowCtr++;
            }

            if (colCtr == column) {
                colCtr = 0;
            }

            array[rowCtr][colCtr] = temp[i];

            colCtr++;

        }

        return cutDatabase(array);
    }

    public void writeBiasOrHiddenWeight(String fileName, double[] array) {

        try{
            File writename = new File(fileName);

            BufferedWriter out = new BufferedWriter(new FileWriter(writename));

            for (int i = 0;i<array.length;i++){
                out.write(""+array[i]+"\r\n");
            }

            out.flush(); // 把缓存区内容压入文件

            out.close(); // 最后记得关闭文件
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void writeInputWeightOrSample(String fileName,double[][] inputWeight) {



        try{
            File writename = new File(fileName);

            BufferedWriter out = new BufferedWriter(new FileWriter(writename));


            for (int i = 0;i<inputWeight.length;i++){
                for (int j = 0;j<inputWeight[0].length;j++){

                    out.write(""+inputWeight[i][j]+"\r\n");

                }
            }

            out.flush(); // 把缓存区内容压入文件

            out.close(); // 最后记得关闭文件
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static double[][] cutDatabase(double[][] database){
        int ctr = 0;

        for (int i = 0;i<database.length;i++){

            double sum = 0;

            for (int j = 0;j<database[0].length;j++){

                double[] temp = database[i];

                sum = temp[j]+sum;

            }

            if (sum != 0) ctr++;
        }

        double[][] cut = new double[ctr][29];

        for (int i = 0;i<ctr;i++){

            cut[i] = database[i].clone();

        }
        return  cut;
    }

    public  double[][] readInputWeightForUse(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;

        NeuralNetwork nn = new NeuralNetwork();

        int row = nn.getHiddenNumber();

        int column = 21;

        double[][] array = new double[row][column];

        double [] temp = new double[row*column];

        try {
            reader = new BufferedReader(new FileReader(file));

            String tempString = null;

            int line = 0;


            while ((tempString = reader.readLine()) != null) {

                Double i = Double.parseDouble(tempString);

                temp[line] = i;

                //System.out.println("line " + line + ": " + tempString);

                line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int rowCtr = 0;

        int colCtr = 0;

        for (int i = 0;i<temp.length;i++) {

            if (i != 0 && i % column == 0) {
                rowCtr++;
            }

            if (colCtr == column) {
                colCtr = 0;
            }

            array[rowCtr][colCtr] = temp[i];

            colCtr++;

        }

        return array;

    }

    public  double[] readBiasWeightForUse(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;

        NeuralNetwork nn = new NeuralNetwork();

        double[] array = new double[nn.getHiddenNumber()];

        try {

            reader = new BufferedReader(new FileReader(file));

            String tempString = null;

            int line = 0;

            while ((tempString = reader.readLine()) != null) {

                Double i = Double.parseDouble(tempString);

                array[line] = i;

                //System.out.println("line " + line + ": " + tempString);

                line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return array;

    }

    public  double[] readHiddenWeightForUse(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;

        NeuralNetwork nn = new NeuralNetwork();

        double[] array = new double[nn.getHiddenNumber()];

        try {

            reader = new BufferedReader(new FileReader(file));

            String tempString = null;

            int line = 0;

            while ((tempString = reader.readLine()) != null) {

                Double i = Double.parseDouble(tempString);

                array[line] = i;

                //System.out.println("line " + line + ": " + tempString);

                line++;
            }

            reader.close();

        } catch (IOException e) {

            e.printStackTrace();
        }

        return array;
    }
}
