package popcorp.robopop;

import android.util.Log;

import java.util.LinkedList;


public class MovingAverage extends PeakFinder{


    //====== Variables ======//
    private static int totalIndex = 1;

    //====== Methods ======//
    /**
     * C'tor
     * @param factor, the factor to be used for peak threshold calculation
     * @param popWidth, is the width in indices of each expected peak (pop)
     */
    public MovingAverage(int factor, int popWidth){
        super(factor, popWidth);
    }

    /**
     * The works of this class
     *
     * @param peakList, is a List to be updated with the found peaks
     * @param recording, is an array holding the audio recording
     * @param length, is the length of the array "recording"
     */
    @Override
    public void getPops(LinkedList<Integer> peakList, final short[] recording, int length){
        float average = (float)recording[0];
        int i=1;

        short absRecording[] = new short[length];

        //Make the recording absolute
        for(int k=0; k<length; k++){
            absRecording[k]=(short)Math.abs(recording[k]);
        }

        for(;i<popWidth;i++){
            average = (average*i+absRecording[i])/(i+1);
        }
        totalIndex += popWidth;

        for(; i<length; i++){
            average = (average*i+absRecording[i])/(i+1);
            if(absRecording[i]>factor*average){
                //It's a peak
                peakList.add(totalIndex);//was i instead of totalIndex
                int nextStep=Math.min(i+popWidth,length);
                for(;i<nextStep;i++){
                    average = (average*i+absRecording[i])/(i+1);
                    totalIndex++;
                }
            }
            totalIndex++;
        }
        return;
    }

    /**
     * Resets values used by the class
     */
    public void reset(){
        totalIndex = 1;
    }
}
