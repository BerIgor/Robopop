package popcorp.robopop;

import android.util.Log;

import java.util.LinkedList;

/**
 * The ZamirBerendorf class represents and implements an algorithm for peak finding in an audio
 * recording.
 *
 * The algorithm:
 *
 * if firstRecording
 *      ignore recording
 *      exit
 * if secondRecording
 *      calculate averageDerivative
 *      threshold = factor * averageDerivative
 *      exit
 * else
 *      foreach two adjacent elements in the recording
 *          if derivative(elements) > threshold
 *              mark as peak
 *              advance index by peakWidth
 *
 *
 * Representation Invariant:
 * For any two numbers A and B, the derivative is defined as
 *      derivative(A,B) = A - B
 *
 * Then a derivative of an array Arr of length Arr.length, is defined as an array Der:
 * for 0 <= i < Arr.length
 *      Der[i] = derivative(Arr[i+1],Arr[i])
 *
 * s.t. Der.length == Arr.length-1
 *
 */
public class ZamirBerendorf implements PeakFinder {

    //==== Variables ====//
    private double averageDerivative = 0;
    private int totalIndex = 0;  // This variable holds the total index of the recording so far
    private boolean firstWindow = true;
    private boolean secondWindow = true;
    private int factor = 0;
    private int popWidth = 0;



    //====Methods====//
    /**
     * C'tor
     * @param factor, the factor to be used for peak threshold calculation
     * @param popWidth, is the width in indices of each expected peak (pop)
     */
    public ZamirBerendorf(int factor, int popWidth){
        this.factor = factor;
        this.popWidth = popWidth;
    }

    /**
     * The works of this class
     *
     * @param peakList, is a List to be updated with the found peaks
     * @param recording, is an array holding the audio recording
     * @param length, is the length of the array "recording"
     */
    @Override
    public void getPops(LinkedList<Integer> peakList, short[] recording, int length) {

        Log.i("IGOR", "ZB - averageDerivative == " + (new Double(averageDerivative)).toString());
        Log.i("IGOR", "ZB - totalIndex == " + (new Integer(totalIndex)).toString());

        short modifiedRecording[] = new short[length];
        //Make the recording absolute
        for(int k=0; k<length; k++){
            modifiedRecording[k]=(short)Math.abs(recording[k]);
        }

        if(firstWindow){
            // Ignore the first window due to possible noise
            firstWindow = false;
            totalIndex += length;
            return;
        } else if (secondWindow){
            // Ignore the second window, but calculate the derivative average
            setAverageDerivative(modifiedRecording, length);
            secondWindow = false;
            totalIndex += length;
            return;
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
        return;
    }

    /**
     * Resets values used by the class
     */
    @Override
    public void reset() {
        totalIndex = 0;
        firstWindow = true;
    }

    /**
     * Calculates the average derivative of the array recording.
     * For an array A with length l, the average derivative avgDer is then:
     *      For dA, the derivative of the array
     *      avgDer = dA/l
     *
     * @param recording is the array from which the average derivative is to be calculated
     * @param length is the length of the array
     */
    private void setAverageDerivative(short[] recording, int length){
        int sum = 0;
        for (int i=0; i<(length-1); i++){
            sum += Math.abs(recording[i+1] - recording[i]);
        }
        averageDerivative = sum/length;
    }


    /**
     * Updates the array to hold the derivatives of its previous values.
     * For an array A of numerical values, the derivative der is defined as
     *      der = A[i+1] - A[i]      for any 0=<i<=A.length
     *
     * NOTE: This method modifies the array
     *
     * @param array the array to be derived and modified
     * @param length of the array array
     * @return the new length of the array, which is (length-1)
     */
    private int calculateDerivative(short[] array, int length){
        int newLength = length - 1;
        for (int i=0; i<(length-1); i++){
            array[i] = (short) (array[i+1] - array[i]);
        }
        return newLength;
    }

}
