/**
 * Created by Karstan on 21/06/2017.
 */
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/*
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class k_nearest_neighbour {
/*
    //Refactored code... Because the stuff Dan gave us was a nightmare to read.

    static CSVWriter writer;
    static ArrayList<String[]> trainingData, testingData;//this array will contain the dataset
    static float[][] distance;        //this array will contain the distance from the new instance to each instance in the dataset
    static Map<String, Integer> confusionMatrix;

    static String x,y;
    static int j,i=0;

    public static void main(String[] args) throws IOException {
        int qty = -1;

        try{
            trainingData = populateDataSet("breast_cancer_train.csv");
            testingData = populateDataSet("breast_cancer_test.csv");

            String tempInput = requestInput("Diagnose based on how many neighbours?");

            //While requested number  is outside desired threshold.
            while(qty > trainingData.size() || qty < 1){
                qty = Integer.parseInt(tempInput);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        //Setting up for the confusion matrix:
        confusionMatrix = new HashMap<String, Integer>();
        confusionMatrix.put("malignmalign", 0); // ACTUAL-PREDICTED
        confusionMatrix.put("malignbenign", 0);
        confusionMatrix.put("benignmalign", 0);
        confusionMatrix.put("benignbenign", 0);

        String name = "predictions"+qty+".csv";
        //Open new writer that overwrites adn immediately close.
        //Will give us an empty file.
        CSVWriter writer = new CSVWriter(new FileWriter(name));
        String[] headings = {"Patient ID","Diagnosis form Test Data","Calculated Diagnosis"};
        writeData(name, headings);
        writer.close();


		 //Loop through the testing patients. Reset distances and calculate diagnosis for each.

        for(String[] temp : testingData){
            String[] tempDiagnosis = calculatePatientDistance(temp, qty);
            //Write each row to the file
            writeData(name, tempDiagnosis);

            //Messy command determining what part of the matrix to increment
            confusionMatrix.put(tempDiagnosis[1]+tempDiagnosis[2], confusionMatrix.get(tempDiagnosis[1]+tempDiagnosis[2])+1);
        }
        // Ask user if new instances are to be inputed.
        System.out.println();

        printConfusionMatrix(qty, testingData.size());

        System.out.println();
        System.out.println("GOODBYE .........");
    }


     // Print the confusion matrix..

    static void printConfusionMatrix(int qty, int size) {
        System.out.println("K="+qty+"\t  Malign | Benign");
        System.out.println("Malign \t| " + confusionMatrix.get("malignmalign") + "\t | " + confusionMatrix.get("malignbenign"));
        System.out.println("Benign \t| " + confusionMatrix.get("benignmalign") + "\t | " + confusionMatrix.get("benignbenign"));

        float accuracy = (confusionMatrix.get("malignmalign")+confusionMatrix.get("benignbenign"))/(float)size;
        System.out.println("Accuracy: " + accuracy); //	Correct/total
        float sensitivity = (float)confusionMatrix.get("malignmalign")/(confusionMatrix.get("malignmalign")+confusionMatrix.get("benignbenign"));
        System.out.println("Sensitivity: " + sensitivity);
        float precision = (float)(confusionMatrix.get("malignmalign") + confusionMatrix.get("malignbenign"))/(confusionMatrix.get("malignmalign") + confusionMatrix.get("benignmalign"));
        System.out.println("Precision: " + precision); // true positives/calculated positives
        float specifity = (float)(confusionMatrix.get("benignbenign") + confusionMatrix.get("benignmalign"))/(confusionMatrix.get("benignbenign") + confusionMatrix.get("malignbenign"));
        System.out.println("Specifity: " + specifity);
    }

    /*
     * Uses given string to attempt file opening. Proceeds to transfer contents of file
     * into the data field.
     *
     * Returns the data, and declares the number of instances of said data.
     */


/*
    static ArrayList<String[]> populateDataSet(String datasetfile) throws IOException{
        ArrayList<String[]> data = new ArrayList<String[]>();
        String [] nextLine;

        CSVReader reader = new CSVReader(new FileReader(datasetfile));

        while ((nextLine = reader.readNext()) != null) { //while the line inputed is not empty do
            data.add(nextLine);
        }
        data.remove(0); // Removing the header row
        return data;
    }


     // Take a string as an output file and print the data handed to it.

    static void writeData(String outputFileName, String[] data) throws IOException{
        CSVWriter writer = new CSVWriter(new FileWriter(outputFileName, true));

        //This accepts a String[] array and produces one comma-separated line.
        writer.writeNext(data);
        writer.close();
    }

    static String[] calculatePatientDistance(String[] patient, int qty){
        //Reset the distance data
        distance = resetDistances(trainingData);
        //For each element in the training data,
        for(i = 0; i < trainingData.size(); i++) {
            //And each column therein
            for(j = 1; j < trainingData.get(0).length; j++) {
                //Compare current patient symptom a, b, c.... etc
                x=patient[j];
                //To the same symptom with the training data
                y=trainingData.get(i)[j];
                //Then if NOT the same...
                if (x.compareTo(y)!=0)
                    //Increment the distance.
                    distance[i][1]++;
            }
            // Now the computed distances are ready, do display them
            //System.out.println("The distance between the new patient and patient"+distance[i][0]+" and " + patient[0] + " is "+distance[i][1]);
        }
        System.out.println();
        System.out.println("Diagnosing patient " + patient[0] + "... (Actual Diagnosis: " + patient[patient.length-1] + ")");
        String[] csvRow = {patient[0], patient[patient.length-1], getSmallest(distance, qty)};
        //getSmallest(distance, qty);
        return csvRow;
    }


     // Accepts a string to display to the user, then expects an input as a response.

    static String requestInput(String displayText){
        Scanner in = new Scanner(System.in);

        System.out.println(displayText);
        String input = in.nextLine();
        in.close();

        return input;
    }

// Build an array the size of the data set and fill it with ID && 0 pairings

    static float[][] resetDistances(ArrayList<String[]> dataSet){
        float[][] distances = new float[dataSet.size()][2]; // {{ PATIENT ID , DISTANCE }}
        for(int i = 0; i < distances.length; i++){
            //Patient ID
            distances[i][0] = Float.parseFloat(dataSet.get(i)[0]);
            //Distance
            distances[i][1] = 0;
        }
        return distances;
    }

    static String getSmallest(float[][] searchSet, int qty){
        //Sort by distance
        searchSet = bubbleSort(searchSet);

        //Show top qty
        for(int i = 0; i < qty; i++){
            int index = getPatientIndex(trainingData, searchSet[i][0]);
            int diagnosis = trainingData.get(index).length-1;
            System.out.println("Patient: " + searchSet[i][0] + " | " + searchSet[i][1] + " - " + trainingData.get(index)[diagnosis]);
        }
        System.out.println("Calculated Diagnosis: " + getClosest(searchSet, qty));
        return getClosest(searchSet, qty);
    }



    /*
     * Normalisation logic
     * Iterates through columns. Grabs the largest and smallest values in a column and
     * scales them to values between 0 and 1.
     */

/*
    static ArrayList<String[]> normalize(ArrayList<String[]> data){
        //Parse each entry to a float...

        for(int i = 1; i < 10; i++){ //Iterating over columns. Data[][1-10] contain the values to normalise
            Float max = null, min = null;
            for(String[] temp : data){ //Iterating over rows.
                //Assigning the floats.
                min = (min == null || Float.parseFloat(temp[i]) < min) ? Float.parseFloat(temp[i]) : min;
                max = (max == null || Float.parseFloat(temp[i]) > max) ? Float.parseFloat(temp[i]) : max;
            }
            //Min and max should be defined by now.
            //System.out.println("Min: " + min);
            for(String[] temp : data){
                Float tempString = normalize(Float.parseFloat(temp[i]), min, max);
                //System.out.println("Col " + i + ": " + tempString);
                temp[i] = Float.toString(tempString);
            }
        }
        return data;
    }

    /*
     * Normalisation arithmatic
     * return (n-min)/(max-min);
     * Got the equation from here:
	 * http://www.dataminingblog.com/standardization-vs-normalization/
     */
/*
    static float normalize(float n, float min, float max){

        return (n-min)/(max-min);
    }

    static boolean isInArray(int[] results, int index){
        for(int temp : results){
            if(temp == index) return true;
        }
        return false;
    }

    static void printDistance(ArrayList<String[]> data){
        for(int i = 0; i < data.size(); i++)
            System.out.print(distance[i]+", ");
    }

    static float[][] bubbleSort(float[][] toSort){
        int i;
        boolean working = true;
        float[] temp;

        while(working){
            working = false; //In case no changes are made
            for(i = 0; i < toSort.length-1; i++){
                if(toSort[i][1] > toSort[i+1][1]){ //[0] patient #. [1] distance. Sort so smaller number goes down.
                    temp = toSort[i];
                    toSort[i] = toSort[i+1];
                    toSort[i+1] = temp;
                    working = true; //A change was made. Cycle again.
                }
            }
        }
        return toSort;
    }


    // Calculating the most likely diagnosis.

    static String getClosest(float[][] array, int qty){
        Map<String, Float> diagnosis = new HashMap<String, Float>();

        //Build the map
        for(int i = 0; i < qty; i++){
            int index = getPatientIndex(trainingData, array[i][0]);
            int diagnosisCol = trainingData.get(index).length-1;

            String temp = trainingData.get(index)[diagnosisCol]; //Get diagnosis
            //If diagnoses isn't in the map add it.
            if(diagnosis.get(temp) == null){
                diagnosis.put(temp, 0.0F);
            }
            //Add the weight to the map
            diagnosis.put(temp, diagnosis.get(temp) + applyWeight(array[i]));
        }
        //Iterate over the map
        Map.Entry<String, Float> biggest = null;
        for(Map.Entry<String, Float> entry : diagnosis.entrySet()){
            System.out.println(entry.getKey() + " | " + entry.getValue());
            if(biggest == null || entry.getValue() > biggest.getValue()){
                biggest = entry;
            }
        }
        return biggest.getKey();
    }

    static float applyWeight(float[] array){
        //Where 0 is the patient number and 1 is the distance to current diagnosis.
        //System.out.println("Weighted distance: " + 1.f/(1+similarPatient[1])); // 1.f to force float conversion
        return (1.f/(1+array[1]));
    }

    /*
     * Search through a dataSet for the patient ID. Linear search. Returns
     * -1 if not contained.
     */

/*
    static int getPatientIndex(ArrayList<String[]> data, float id){
        for(String[] temp : data){
            if(Float.parseFloat(temp[0]) == id){
                return data.indexOf(temp);
            }
        }

        return -1;
    }
}
*/