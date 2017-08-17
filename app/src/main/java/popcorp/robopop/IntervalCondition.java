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
class IntervalCondition implements StopCondition {

    //====Variables====//
    private int previousPopCount = 0;
    private int desiredInterval = -1; //Desired interval in indices

    private int previousWindowSize = 0;
    private int maxWindowSize = 0;
    private int startIndex = 150*sampleRate;  // The number represents the seconds from recording start
    private boolean havePeaked = false;
    private int currentIndex = 0;
    private int counter = 0;
    private int currentWindowSize = 0;

    //====Methods====//
    IntervalCondition(int desiredInterval){
        this.desiredInterval=desiredInterval;
    }

    @Override
    public boolean conditionSatisfied(LinkedList<Integer> popList) {

        boolean conditionSatisfied = false;

        // Calculate
        setCurrentWindowSize(popList);
        setPeaked();
        setCurrentIndex(popList);

        // Check satisfaction
        if(currentIndex >= startIndex && havePeaked && satisfiedInterval(popList)){
            conditionSatisfied = true;
        }
        // Calculate for next iteration
        previousWindowSize = currentWindowSize;
        previousPopCount = popList.size();

        return conditionSatisfied;
    }


    private boolean satisfiedInterval(LinkedList<Integer> popList){
        Log.i("IGOR", "INTERVAL - CHECKING");
        Iterator<Integer> iterator = popList.listIterator(max(previousPopCount - 1, 0));
        Integer previous = null;
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

    private void setCurrentWindowSize(LinkedList<Integer> popList){
        currentWindowSize = popList.size() - previousPopCount;
    }

    private void setCurrentIndex(LinkedList<Integer> popList){
        if(popList.size() <= 0) {
            return;
        }
        currentIndex = popList.getLast();
    }
}
