package popcorp.robopop;

import android.util.Log;

public class PeakFinderFactory {

    public static PeakFinder getPeakFinder(int sampleRate, int power){

        double sPopWidth;
        int factor;
        switch (power) {
            case 2:
                sPopWidth = 0.04;
                factor = 8;
                break;
            default:
                sPopWidth = 0.03;
                factor = 25;
                break;
        }

        int popWidth = (int)(sPopWidth*sampleRate);

        if (power == 2){
            Log.i("IGOR", "PFF - MovingAverage");
            return new MovingAverage(factor, popWidth);
        }
        Log.i("IGOR", "PFF - ZB");
        return new ZamirBerendorf(factor, popWidth);
    }

}
