package popcorp.robopop;

import android.util.Log;

import java.util.LinkedList;


public class MovingAverage implements PeakFinder{


    //====== Variables ======//
    private int thresholdFactor;
    private int popWidth;
    private static int totalIndex = 1;

    //====== Methods ======//
    // C'tor
    public MovingAverage(int thresholdFactor,int popWidth){
        this.thresholdFactor=thresholdFactor;
        this.popWidth=popWidth;
    }

    @Override
    public int getPops(LinkedList<Integer> peakList, final short[] recording, int length){
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
            if(absRecording[i]>thresholdFactor*average){
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
        //TODO: Change to return the number of peaks found in this segment
        return 0;
    }
}
