package Group7.agent.NeuralNetwork;

public class NeuralNetwork {

    //vector of input
    private double[] inputVector;

    //vector represent the hidden layer
    private double[] hiddenLayer;

    //number of neurons in hidden layers
    private int hiddenNumber = 20;

    //value of weight of the input vector
    private double[][] weightOfInputVector;

    //value of weight of the hidden vector
    private double[] weightOfHiddenLayer;

    //learning rate
    private double learningRate = 0.7;

    //iteration times
    private double iteration = 1000;

    //target
    private double target;

    //bias
    private double bias = 1;

    //bias' weight
    private double[] weightOfBias;

    public boolean DEBUG = true;
    //30 thousands times


    public static String iniHiddenWeightPath= "initialHiddenWeight.txt";
    public static String iniBiasWeightPath = "initialBiasWeight.txt";
    public static String iniInputWeightPath = "initialInputWeight.txt";

    public static String sampleForTrainPath = "/Users/luotianchen/DiceUp/src/main/resources/ANN/sampleForTrain.txt";
    public static String initial_InputWeight = "/Users/luotianchen/DiceUp/src/main/resources/ANN/initial_InputWeight.txt";
    public static String initial_HiddenWeight = "/Users/luotianchen/DiceUp/src/main/resources/ANN/initial_HiddenWeight.txt";
    public static String initial_BiasWeight = "/Users/luotianchen/DiceUp/src/main/resources/ANN/initial_BiasWeight.txt";


    public static String hiddenWeightPath1 = "src/main/resources/hiddenWeight1.txt";
    public static String hiddenWeightPath2 = "src/main/resources/hiddenWeight2.txt";
    public static String hiddenWeightPath3 = "src/main/resources/hiddenWeight3.txt";
    public static String hiddenWeightPath4 = "src/main/resources/hiddenWeight4.txt";
    public static String hiddenWeightPath5 = "src/main/resources/hiddenWeight5.txt";
    public static String hiddenWeightPath6 = "src/main/resources/hiddenWeight6.txt";
    public static String hiddenWeightPath7 = "src/main/resources/hiddenWeight7.txt";
    public static String hiddenWeightPath8 = "src/main/resources/hiddenWeight8.txt";
    public static String hiddenWeightPath9 = "src/main/resources/hiddenWeight9.txt";
    public static String hiddenWeightPath10 = "src/main/resources/hiddenWeight10.txt";
    public static String hiddenWeightPath11 = "src/main/resources/hiddenWeight11.txt";
    public static String hiddenWeightPath12 = "src/main/resources/hiddenWeight12.txt";

    public static String inputWeightPath1 = "src/main/resources/inputWeight1.txt";
    public static String inputWeightPath2 = "src/main/resources/inputWeight2.txt";
    public static String inputWeightPath3 = "src/main/resources/inputWeight3.txt";
    public static String inputWeightPath4 = "src/main/resources/inputWeight4.txt";
    public static String inputWeightPath5 = "src/main/resources/inputWeight5.txt";
    public static String inputWeightPath6 = "src/main/resources/inputWeight6.txt";
    public static String inputWeightPath7 = "src/main/resources/inputWeight7.txt";
    public static String inputWeightPath8 = "src/main/resources/inputWeight8.txt";
    public static String inputWeightPath9 = "src/main/resources/inputWeight9.txt";
    public static String inputWeightPath10 = "src/main/resources/inputWeight10.txt";
    public static String inputWeightPath11 = "src/main/resources/inputWeight11.txt";
    public static String inputWeightPath12 = "src/main/resources/inputWeight12.txt";

    public static String biasWeightPath1 = "src/main/resources/biasWeight1.txt";
    public static String biasWeightPath2 = "src/main/resources/biasWeight2.txt";
    public static String biasWeightPath3 = "src/main/resources/biasWeight3.txt";
    public static String biasWeightPath4 = "src/main/resources/biasWeight4.txt";
    public static String biasWeightPath5 = "src/main/resources/biasWeight5.txt";
    public static String biasWeightPath6 = "src/main/resources/biasWeight6.txt";
    public static String biasWeightPath7 = "src/main/resources/biasWeight7.txt";
    public static String biasWeightPath8 = "src/main/resources/biasWeight8.txt";
    public static String biasWeightPath9 = "src/main/resources/biasWeight9.txt";
    public static String biasWeightPath10 = "src/main/resources/biasWeight10.txt";
    public static String biasWeightPath11 = "src/main/resources/biasWeight11.txt";
    public static String biasWeightPath12 = "src/main/resources/biasWeight12.txt";


    public String inputFile;
    public String biasFile;
    public String hiddenFile;






    public NeuralNetwork(double[] inputVector,double target,int index,String inputFile,String biasFile,String hiddenFile) {

        Files f = new Files();
        hiddenLayer = new double[hiddenNumber];
        this.inputVector = inputVector;
        this.target = target;
        this.inputFile = inputFile;
        this.biasFile = biasFile;
        this.hiddenFile = hiddenFile;
        //choose to use random initialization
//        weightOfInputVector = new double[hiddenNumber][inputVector.length];
//        weightOfHiddenLayer = new double[hiddenNumber];
//        weightOfBias = new double[hiddenNumber];
//        initialization();

        switch (index){
            case 1: {
                weightOfInputVector = f.readInputWeightForUse(inputWeightPath1).clone();weightOfHiddenLayer = f.readHiddenWeightForUse(hiddenWeightPath1).clone();weightOfBias = f.readBiasWeightForUse(biasWeightPath1).clone();
            }
            case 2:{
                weightOfInputVector = f.readInputWeightForUse(inputWeightPath2).clone();weightOfHiddenLayer = f.readHiddenWeightForUse(hiddenWeightPath2).clone();weightOfBias = f.readBiasWeightForUse(biasWeightPath2).clone();
            }
            case 3:{
                weightOfInputVector = f.readInputWeightForUse(inputWeightPath3).clone();weightOfHiddenLayer = f.readHiddenWeightForUse(hiddenWeightPath3).clone();weightOfBias = f.readBiasWeightForUse(biasWeightPath3).clone();
            }
            case 4:{
                weightOfInputVector = f.readInputWeightForUse(inputWeightPath4).clone();weightOfHiddenLayer = f.readHiddenWeightForUse(hiddenWeightPath4).clone();weightOfBias = f.readBiasWeightForUse(biasWeightPath4).clone();
            }
            case 5:{
                weightOfInputVector = f.readInputWeightForUse(inputWeightPath5).clone();weightOfHiddenLayer = f.readHiddenWeightForUse(hiddenWeightPath5).clone();weightOfBias = f.readBiasWeightForUse(biasWeightPath5).clone();
            }
            case 6:{
                weightOfInputVector = f.readInputWeightForUse(inputWeightPath6).clone();weightOfHiddenLayer = f.readHiddenWeightForUse(hiddenWeightPath6).clone();weightOfBias = f.readBiasWeightForUse(biasWeightPath6).clone();
            }
            case 7:{
                weightOfInputVector = f.readInputWeightForUse(inputWeightPath7).clone();weightOfHiddenLayer = f.readHiddenWeightForUse(hiddenWeightPath7).clone();weightOfBias = f.readBiasWeightForUse(biasWeightPath7).clone();
            }
            case 8:{
                weightOfInputVector = f.readInputWeightForUse(inputWeightPath8).clone();weightOfHiddenLayer = f.readHiddenWeightForUse(hiddenWeightPath8).clone();weightOfBias = f.readBiasWeightForUse(biasWeightPath8).clone();
            }
            case 9:{
                weightOfInputVector = f.readInputWeightForUse(inputWeightPath9).clone();weightOfHiddenLayer = f.readHiddenWeightForUse(hiddenWeightPath9).clone();weightOfBias = f.readBiasWeightForUse(biasWeightPath9).clone();
            }
            case 10:{
                weightOfInputVector = f.readInputWeightForUse(inputWeightPath10).clone();weightOfHiddenLayer = f.readHiddenWeightForUse(hiddenWeightPath10).clone();weightOfBias = f.readBiasWeightForUse(biasWeightPath10).clone();
            }
            case 11:{
                weightOfInputVector = f.readInputWeightForUse(inputWeightPath11).clone();weightOfHiddenLayer = f.readHiddenWeightForUse(hiddenWeightPath11).clone();weightOfBias = f.readBiasWeightForUse(biasWeightPath11).clone();
            }
            case 12:{
                weightOfInputVector = f.readInputWeightForUse(inputWeightPath12).clone();weightOfHiddenLayer = f.readHiddenWeightForUse(hiddenWeightPath12).clone();weightOfBias = f.readBiasWeightForUse(biasWeightPath12).clone();
            }

        }
    }

    //to get the result
    public NeuralNetwork(double[] inputVector,int index) {

        Files f = new Files();
        hiddenLayer = new double[hiddenNumber];
        this.inputVector = inputVector;


        if (index == 1){
            setWeightOfInputVector(f.readInputWeightForUse(inputWeightPath1).clone());
            setWeightOfHiddenLayer(f.readHiddenWeightForUse(hiddenWeightPath1).clone());
            setWeightOfBias(f.readBiasWeightForUse(biasWeightPath1));
        }else if (index == 2){
            setWeightOfInputVector(f.readInputWeightForUse(inputWeightPath2).clone());
            setWeightOfHiddenLayer(f.readHiddenWeightForUse(hiddenWeightPath2).clone());
            setWeightOfBias(f.readBiasWeightForUse(biasWeightPath2));
        }else if (index == 3){
            setWeightOfInputVector(f.readInputWeightForUse(inputWeightPath3).clone());
            setWeightOfHiddenLayer(f.readHiddenWeightForUse(hiddenWeightPath3).clone());
            setWeightOfBias(f.readBiasWeightForUse(biasWeightPath3));
        }else if (index == 4){
            setWeightOfInputVector(f.readInputWeightForUse(inputWeightPath4).clone());
            setWeightOfHiddenLayer(f.readHiddenWeightForUse(hiddenWeightPath4).clone());
            setWeightOfBias(f.readBiasWeightForUse(biasWeightPath4));
        }else if (index == 5){
            setWeightOfInputVector(f.readInputWeightForUse(inputWeightPath5).clone());
            setWeightOfHiddenLayer(f.readHiddenWeightForUse(hiddenWeightPath5).clone());
            setWeightOfBias(f.readBiasWeightForUse(biasWeightPath5));
        }else if (index == 6){
            setWeightOfInputVector(f.readInputWeightForUse(inputWeightPath6).clone());
            setWeightOfHiddenLayer(f.readHiddenWeightForUse(hiddenWeightPath6).clone());
            setWeightOfBias(f.readBiasWeightForUse(biasWeightPath6));
        }else if (index == 7){
            setWeightOfInputVector(f.readInputWeightForUse(inputWeightPath7).clone());
            setWeightOfHiddenLayer(f.readHiddenWeightForUse(hiddenWeightPath7).clone());
            setWeightOfBias(f.readBiasWeightForUse(biasWeightPath7));
        }else if (index == 8){
            setWeightOfInputVector(f.readInputWeightForUse(inputWeightPath8).clone());
            setWeightOfHiddenLayer(f.readHiddenWeightForUse(hiddenWeightPath8).clone());
            setWeightOfBias(f.readBiasWeightForUse(biasWeightPath8));
        }else if (index == 9){
            setWeightOfInputVector(f.readInputWeightForUse(inputWeightPath9).clone());
            setWeightOfHiddenLayer(f.readHiddenWeightForUse(hiddenWeightPath9).clone());
            setWeightOfBias(f.readBiasWeightForUse(biasWeightPath9));
        }else if (index == 10){
            setWeightOfInputVector(f.readInputWeightForUse(inputWeightPath10).clone());
            setWeightOfHiddenLayer(f.readHiddenWeightForUse(hiddenWeightPath10).clone());
            setWeightOfBias(f.readBiasWeightForUse(biasWeightPath10));
        }else if (index == 11){
            setWeightOfInputVector(f.readInputWeightForUse(inputWeightPath11).clone());
            setWeightOfHiddenLayer(f.readHiddenWeightForUse(hiddenWeightPath11).clone());
            setWeightOfBias(f.readBiasWeightForUse(biasWeightPath11));
        }else if (index == 12){
            setWeightOfInputVector(f.readInputWeightForUse(inputWeightPath12).clone());
            setWeightOfHiddenLayer(f.readHiddenWeightForUse(hiddenWeightPath12).clone());
            setWeightOfBias(f.readBiasWeightForUse(biasWeightPath12));
        }

    }

    public NeuralNetwork(){
        hiddenLayer = new double[hiddenNumber];
//        this.inputVector = inputVector;
//        this.target = target;
        weightOfInputVector = new double[hiddenNumber][198];
        weightOfHiddenLayer = new double[hiddenNumber];
        weightOfBias = new double[hiddenNumber];
        inputVector = new double[21];

        initialization();
    }




    /**
     * Initialize the weight with a random double value greater than 0 and less than 1
     */
    public void initialization() {

        for (int i = 0; i < weightOfInputVector.length; i++) {
            for (int j = 0; j < weightOfInputVector[0].length; j++) {
                weightOfInputVector[i][j] = Math.random()/1000;
            }
        }

        for (int i = 0; i < weightOfHiddenLayer.length; i++) {
            weightOfHiddenLayer[i] = Math.random()/1000;
        }

        for (int i = 0; i < weightOfBias.length; i++) {
            weightOfBias[i] = Math.random()/1000;
        }

    }




    //sigmoid function
    public double activationFunction(double input){
        return 1 / (1 + Math.pow(Math.E, -input));
    }




    public double forward() {
        double sum = 0;
        double[] tempInputHiddenLayer = new double[hiddenLayer.length];
        double[] tempOutputHiddenLayer = new double[hiddenLayer.length];


        //first do forward prop
        for (int i = 0;i<hiddenLayer.length;i++){
            for (int j = 0;j<inputVector.length;j++){

                sum = sum + inputVector[j]*weightOfInputVector[i][j];

            }

            sum = sum + bias*weightOfBias[i];
            tempInputHiddenLayer[i] = sum;
            tempOutputHiddenLayer[i] = activationFunction(sum);
        }

        hiddenLayer = tempOutputHiddenLayer.clone();

        //sum before calculated by sigmoid function
        double netOutput = 0;

        for (int i = 0;i<hiddenLayer.length;i++){

            netOutput = netOutput + hiddenLayer[i]*weightOfHiddenLayer[i];

        }

        double output = activationFunction(netOutput);
        return output;
    }

    //a full forward and backward procedure
    public void forwardAndBackward(){
        double sum = 0;
        double[] tempInputHiddenLayer = new double[hiddenLayer.length];
        double[] tempOutputHiddenLayer = new double[hiddenLayer.length];



        //first do forward prop
        for (int i = 0;i<hiddenLayer.length;i++){
            for (int j = 0;j<inputVector.length;j++){

                sum = sum + inputVector[j]*weightOfInputVector[i][j];

            }
            sum = sum + bias*weightOfBias[i];
            tempInputHiddenLayer[i] = sum;
            tempOutputHiddenLayer[i] = activationFunction(sum);
        }

        hiddenLayer = tempOutputHiddenLayer.clone();

        //sum before calculated by sigmoid function
        double netOutput = 0;

        for (int i = 0;i<hiddenLayer.length;i++){

            netOutput = netOutput + hiddenLayer[i]*weightOfHiddenLayer[i];

        }

        double output = activationFunction(netOutput);

        //then, do back prop

        double Error = 0.5*(Math.pow(target-output,2));

        //update the weight of hidden layer

        double[] tempHiddenLayerWeight = new double[weightOfHiddenLayer.length];

        //update weight of hidden layer and record it in a temp array
        for (int i = 0; i<tempHiddenLayerWeight.length;i++){

            double CA = -(target-output);
            double CB = output*(1-output);
            double CC = tempOutputHiddenLayer[i];

            double blackBox = CA*CB*CC;

            tempHiddenLayerWeight[i] = weightOfHiddenLayer[i] - learningRate*blackBox;

        }

        //update weight of input vector

        for (int i = 0; i<hiddenLayer.length;i++){
            for (int j  = 0;j<inputVector.length;j++){

                double CA = -(target-tempOutputHiddenLayer[i]);
                double CB = tempOutputHiddenLayer[i]*(1-tempOutputHiddenLayer[i]);
                double CC = inputVector[j];

                double blackBox = CA*CB*CC;

                //update weight of input vector here
                weightOfInputVector[i][j] = weightOfInputVector[i][j] - learningRate*blackBox;

            }

        }

        //update weight for bias
        for (int i = 0;i<hiddenLayer.length;i++){
            double CA = -(target-tempOutputHiddenLayer[i]);
            double CB = tempOutputHiddenLayer[i]*(1-tempOutputHiddenLayer[i]);
            double CC = bias;

            double blackBox = CA*CB*CC;

            //update weight of bias here
            weightOfBias[i] = weightOfBias[i] - learningRate*blackBox;


        }

        //eventually update weight of hidden layer
        weightOfHiddenLayer = tempHiddenLayerWeight.clone() ;
        setWeightOfHiddenLayer(tempHiddenLayerWeight.clone());
        setWeightOfInputVector(weightOfInputVector);
        setWeightOfBias(weightOfBias);

        if (!DEBUG) {
            for (int i = 0; i < weightOfInputVector.length; i++) {
                for (int j = 0; j < weightOfInputVector[0].length; j++) {

                    System.out.println(weightOfInputVector[i][j]);

                }

            }
        }
       // if (DEBUG) System.out.println(Error);

    }


    // method to train the network.
    public void train(int iterationTimes){
        int ctr = 0;

        Files f = new Files();

        while (ctr<iterationTimes){

            setWeightOfBias(f.readBiasWeightForUse(biasFile));
            setWeightOfHiddenLayer(f.readHiddenWeightForUse(hiddenFile));
            setWeightOfInputVector(f.readInputWeightForUse(inputFile));

            forwardAndBackward();
            ctr++;

            f.writeBiasOrHiddenWeight(biasFile,getWeightOfBias());
            f.writeBiasOrHiddenWeight(hiddenFile,getWeightOfHiddenLayer());
            f.writeInputWeightOrSample(inputFile,getWeightOfInputVector());
        }

    }

    //setter and getter part
    public int getHiddenNumber(){
        return  hiddenNumber;
    }

    public int getInputVectorLength(){
        return inputVector.length;
    }

    public double[] getWeightOfHiddenLayer(){
        return weightOfHiddenLayer;
    }

    public double[] getWeightOfBias(){
        return weightOfBias;
    }

    public double[][] getWeightOfInputVector(){
        return weightOfInputVector;
    }

    public void setInputVector(double[] inputVector) {
        this.inputVector = inputVector;
    }

    public void setWeightOfHiddenLayer(double[] weightOfHiddenLayer) {
        this.weightOfHiddenLayer = weightOfHiddenLayer;
    }

    public void setWeightOfBias(double[] weightOfBias) {
        this.weightOfBias = weightOfBias;
    }

    public void setWeightOfInputVector(double[][] weightOfInputVector) {
        this.weightOfInputVector = weightOfInputVector;
    }

    public void setHiddenNumber(int hiddenNumber) {
        this.hiddenNumber = hiddenNumber;
    }

    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
    }

    public double getTarget() {
        return target;
    }

    public double getLearningRate() {
        return learningRate;
    }

    public double[] getHiddenLayer() {
        return hiddenLayer;
    }

    public double[] getInputVector() {
        return inputVector;
    }
}

class tttt{

    public static String hiddenWeightPath1 = "src/main/resources/hiddenWeight1.txt";
    public static String hiddenWeightPath2 = "src/main/resources/hiddenWeight2.txt";
    public static String hiddenWeightPath3 = "src/main/resources/hiddenWeight3.txt";
    public static String hiddenWeightPath4 = "src/main/resources/hiddenWeight4.txt";
    public static String hiddenWeightPath5 = "src/main/resources/hiddenWeight5.txt";
    public static String hiddenWeightPath6 = "src/main/resources/hiddenWeight6.txt";
    public static String hiddenWeightPath7 = "src/main/resources/hiddenWeight7.txt";
    public static String hiddenWeightPath8 = "src/main/resources/hiddenWeight8.txt";
    public static String hiddenWeightPath9 = "src/main/resources/hiddenWeight9.txt";
    public static String hiddenWeightPath10 = "src/main/resources/hiddenWeight10.txt";
    public static String hiddenWeightPath11 = "src/main/resources/hiddenWeight11.txt";
    public static String hiddenWeightPath12 = "src/main/resources/hiddenWeight12.txt";

    public static String inputWeightPath1 = "src/main/resources/inputWeight1.txt";
    public static String inputWeightPath2 = "src/main/resources/inputWeight2.txt";
    public static String inputWeightPath3 = "src/main/resources/inputWeight3.txt";
    public static String inputWeightPath4 = "src/main/resources/inputWeight4.txt";
    public static String inputWeightPath5 = "src/main/resources/inputWeight5.txt";
    public static String inputWeightPath6 = "src/main/resources/inputWeight6.txt";
    public static String inputWeightPath7 = "src/main/resources/inputWeight7.txt";
    public static String inputWeightPath8 = "src/main/resources/inputWeight8.txt";
    public static String inputWeightPath9 = "src/main/resources/inputWeight9.txt";
    public static String inputWeightPath10 = "src/main/resources/inputWeight10.txt";
    public static String inputWeightPath11 = "src/main/resources/inputWeight11.txt";
    public static String inputWeightPath12 = "src/main/resources/inputWeight12.txt";

    public static String biasWeightPath1 = "src/main/resources/biasWeight1.txt";
    public static String biasWeightPath2 = "src/main/resources/biasWeight2.txt";
    public static String biasWeightPath3 = "src/main/resources/biasWeight3.txt";
    public static String biasWeightPath4 = "src/main/resources/biasWeight4.txt";
    public static String biasWeightPath5 = "src/main/resources/biasWeight5.txt";
    public static String biasWeightPath6 = "src/main/resources/biasWeight6.txt";
    public static String biasWeightPath7 = "src/main/resources/biasWeight7.txt";
    public static String biasWeightPath8 = "src/main/resources/biasWeight8.txt";
    public static String biasWeightPath9 = "src/main/resources/biasWeight9.txt";
    public static String biasWeightPath10 = "src/main/resources/biasWeight10.txt";
    public static String biasWeightPath11 = "src/main/resources/biasWeight11.txt";
    public static String biasWeightPath12 = "src/main/resources/biasWeight12.txt";

    public static void main(String[] args) {

        double[] input = new double[21];

        for (int i = 0;i<21;i++){
            if (Math.random()<0.5){
                input[i] = 1;
                System.out.println(input[i]);
            }
        }
//
//        NeuralNetwork nn  = new NeuralNetwork(input,1,2,inputWeightPath2,biasWeightPath2,hiddenWeightPath2);
//
//        nn.train(20);

        //initialize weight
            Files files = new Files();
//            NeuralNetwork neuralNetwork = new NeuralNetwork();
//
//            double[][] input = new double[neuralNetwork.getHiddenNumber()][neuralNetwork.getInputVectorLength()];
//            double[] bias = new double[neuralNetwork.getHiddenNumber()];
//            double[] hidden = new double[neuralNetwork.getHiddenNumber()];
//
//        for (int i = 0; i < input.length; i++) {
//            for (int j = 0; j < input[0].length; j++) {
//                input[i][j] = Math.random()/1000;
//                //System.out.println(input[i][j]);
//            }
//        }
//
//        for (int i = 0; i < hidden.length; i++) {
//           hidden[i] = Math.random()/1000;
//           System.out.println(hidden[i]);
//        }
//
//        for (int i = 0; i < bias.length; i++) {
//            bias[i] = Math.random()/1000;
//        }

//
//        files.writeBiasOrHiddenWeight(biasWeightPath1,bias);
//        files.writeBiasOrHiddenWeight(biasWeightPath2,bias);
//        files.writeBiasOrHiddenWeight(biasWeightPath3,bias);
//        files.writeBiasOrHiddenWeight(biasWeightPath4,bias);
//        files.writeBiasOrHiddenWeight(biasWeightPath5,bias);
//        files.writeBiasOrHiddenWeight(biasWeightPath6,bias);
//        files.writeBiasOrHiddenWeight(biasWeightPath7,bias);
//        files.writeBiasOrHiddenWeight(biasWeightPath8,bias);
//        files.writeBiasOrHiddenWeight(biasWeightPath9,bias);
//        files.writeBiasOrHiddenWeight(biasWeightPath10,bias);
//        files.writeBiasOrHiddenWeight(biasWeightPath11,bias);
//        files.writeBiasOrHiddenWeight(biasWeightPath12,bias);
//
//        files.writeBiasOrHiddenWeight(hiddenWeightPath1,hidden);
//        files.writeBiasOrHiddenWeight(hiddenWeightPath2,hidden);
//        files.writeBiasOrHiddenWeight(hiddenWeightPath3,hidden);
//        files.writeBiasOrHiddenWeight(hiddenWeightPath4,hidden);
//        files.writeBiasOrHiddenWeight(hiddenWeightPath5,hidden);
//        files.writeBiasOrHiddenWeight(hiddenWeightPath6,hidden);
//        files.writeBiasOrHiddenWeight(hiddenWeightPath7,hidden);
//        files.writeBiasOrHiddenWeight(hiddenWeightPath8,hidden);
//        files.writeBiasOrHiddenWeight(hiddenWeightPath9,hidden);
//        files.writeBiasOrHiddenWeight(hiddenWeightPath10,hidden);
//        files.writeBiasOrHiddenWeight(hiddenWeightPath11,hidden);
//        files.writeBiasOrHiddenWeight(hiddenWeightPath12,hidden);
//
//        files.writeInputWeightOrSample(inputWeightPath2,input);
//        files.writeInputWeightOrSample(inputWeightPath1,input);
//        files.writeInputWeightOrSample(inputWeightPath3,input);
//        files.writeInputWeightOrSample(inputWeightPath4,input);
//        files.writeInputWeightOrSample(inputWeightPath5,input);
//        files.writeInputWeightOrSample(inputWeightPath6,input);
//        files.writeInputWeightOrSample(inputWeightPath7,input);
//        files.writeInputWeightOrSample(inputWeightPath8,input);
//        files.writeInputWeightOrSample(inputWeightPath9,input);
//        files.writeInputWeightOrSample(inputWeightPath10,input);
//        files.writeInputWeightOrSample(inputWeightPath11,input);
//        files.writeInputWeightOrSample(inputWeightPath12,input);



            NeuralNetwork nn = new NeuralNetwork(input,1);



            double[] a = nn.getWeightOfBias();
            double[] b = files.readBiasWeightForUse(biasWeightPath1);



            for (int i = 0;i<a.length;i++){
                System.out.println(a[i]);
            }
        for (int i = 0;i<b.length;i++){
            System.out.println(b[i]);
        }

    }
}

