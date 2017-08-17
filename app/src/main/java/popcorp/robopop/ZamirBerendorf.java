package popcorp.robopop;

import android.util.Log;

import java.util.LinkedList;

public class ZamirBerendorf implements PeakFinder {

    private double averageDerivative = 0;

    private int totalIndex = 0;
    private boolean firstWindow = true;
    private boolean secondWindow = true;

    private int factor = 0;
    private int popWidth = 0;


    public ZamirBerendorf(int factor, int popWidth){
        this.factor = factor;
        this.popWidth = popWidth;
    }


    @Override
    public int getPops(LinkedList<Integer> peakList, short[] recording, int length) {

        Log.i("IGOR", "ZB - averageDerivative == " + (new Double(averageDerivative)).toString());
        Log.i("IGOR", "ZB - totalIndex == " + (new Integer(totalIndex)).toString());

        short modifiedRecording[] = new short[length];
        //Make the recording absolute
        for(int k=0; k<length; k++){
            modifiedRecording[k]=(short)Math.abs(recording[k]);
        }

        if(firstWindow){
            firstWindow = false;
            totalIndex += length;
            return 0;
        } else if (secondWindow){
            setAverageDerivative(modifiedRecording, length);
            secondWindow = false;
            totalIndex += length;
            return 0;
        }


        // Derivate twice
        length = calculateDerivative(modifiedRecording, length);
        length = calculateDerivative(modifiedRecording, length);

        // Set threshold
        double threshold = averageDerivative * factor;

        for(int i=0; i<length; i++){
            if(modifiedRecording[i] > threshold){
                peakList.add(i+totalIndex);
                i += popWidth;
            }
        }

        totalIndex += length;
        return 0;
    }

    @Override
    public void reset() {
        totalIndex = 0;
        firstWindow = true;
    }

    private void setAverageDerivative(short[] recording, int length){
        int sum = 0;
        for (int i=0; i<(length-1); i++){
            sum += Math.abs(recording[i+1] - recording[i]);
        }
        averageDerivative = sum/length;
    }

    /*
    calculateDerivative updates the array to hold the derivatives of its previous values

    @Modifies: the passed array
    @Returns: the new length of the array
     */
    private int calculateDerivative(short[] array, int length){
        int newLength = length - 1;
        for (int i=0; i<(length-1); i++){
            array[i] = (short) (array[i+1] - array[i]);
        }
        return newLength;
    }

}
