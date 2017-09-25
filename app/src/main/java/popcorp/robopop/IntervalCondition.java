package popcorp.robopop;

import android.util.Log;

import java.util.Iterator;
import java.util.LinkedList;

import static java.lang.Integer.valueOf;
import static java.lang.Math.max;
import static popcorp.robopop.MainActivity.sampleRate;

/**
 * This class will check for an interval condition satisfaction.
 * The List passed to conditionSatisfied should be as such:
 * E(i) is the i-th element of the list, then
 * E(i).value represents the sample number of the recording.
 *
 * conditionSatisfied will return true if for any E(i+1)-E(i)>=desiredInterval
 *
 * The list is a continuously growing list, meaning in each call to this function the list will have
 * an increased size compared to the previous call. But we're only interested in the new section,
 * therefore, using the private member previousPopCount, we can discard any old entries.
 */
class IntervalCondition extends StopCondition {

    //====Variables====//
    private int previousPopCount = 0;
    private int desiredInterval = -1; //Desired interval in indices

    private int maxWindowSize = 0;
    private boolean havePeaked = false;
    private int startIndex;
    private int currentIndex = 0;
    private int counter = 0;
    private int currentWindowSize = 0;
    private boolean conditionSatisfied = false;

    //====Methods====//
    IntervalCondition(LinkedList<Integer> popList, int desiredInterval, int powerSetting){
        super(popList);
        this.desiredInterval=desiredInterval;
        startIndex = (150 - (30*powerSetting)) * sampleRate;
    }

    @Override
    public boolean conditionSatisfied() {

//        boolean conditionSatisfied = false;

        // Calculate
        currentWindowSize = setCurrentWindowSize(popList, previousPopCount);
        setPeaked();
        currentIndex = setCurrentIndex(popList);

        // Check satisfaction
        if(currentIndex >= startIndex && havePeaked && satisfiedInterval(popList, previousPopCount, currentWindowSize, desiredInterval)){
            conditionSatisfied = true;
        }
        // Calculate for next iteration
//        previousWindowSize = currentWindowSize;
        previousPopCount = popList.size();

        return conditionSatisfied;
    }

    /*
    The following method will return true if there is an interval larger than desiredInterval
    between two elements in the popList
     */
    private static boolean satisfiedInterval(LinkedList<Integer> popList, int previousPopCount, int currentWindowSize, int desiredInterval){
        Log.i("IGOR", "INTERVAL - CHECKING");
        Iterator<Integer> iterator = popList.listIterator(max(previousPopCount - 1, 0));
        Integer previous;
        Integer current = null;

        if(currentWindowSize == 0){
            return true;
        }
        while (iterator.hasNext()) {
            previous = current;
            current = iterator.next();
            if (previous == null) {
                continue;
            }
            if ((current - previous) >= desiredInterval) {
                return true;
            }
        }
        return false;
    }

    /*
    The following method changes the value of the private variable havePeaked if we have reached
    the peak in pops/interval.

    Implementation-detail: if the number of pops in the current sample window increases for 5
    consecutive windows, we know it's a peak, otherwise restart the count.
     */
    private void setPeaked(){
        Log.i("IGOR", "CURRENT WINDOW PEAK COUNT == " + currentWindowSize);
        if(havePeaked || currentIndex < startIndex){
            return;
        }

        maxWindowSize = max(maxWindowSize,currentWindowSize);

        if(currentWindowSize < maxWindowSize){
            counter++;
            if(counter >= 5){
                Log.i("IGOR", "INTERVAL - PEAKED");
                havePeaked = true;
            }
        } else {
            counter = 0;
        }
    }

    /*
    This method returns the number of new pops in the popList
     */
    private static int setCurrentWindowSize(LinkedList<Integer> popList, int previousPopCount){
        return (popList.size() - previousPopCount);
    }

    /*
    Returns the index of the last element in popList
     */
    private static int setCurrentIndex(LinkedList<Integer> popList){
        if(popList.size() <= 0) {
            return 0;
        }
        return popList.getLast();
    }

    /*
    Returns an integer value representing the current status
     */
    public int getCurrentCondition(){
        if(conditionSatisfied){
            return 3;
        }
        if(havePeaked){
            return 2;
        }
        if(currentIndex >= startIndex){
            return 1;
        }
        return 0;
    }

}
