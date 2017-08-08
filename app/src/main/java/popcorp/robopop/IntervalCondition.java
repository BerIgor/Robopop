package popcorp.robopop;

import java.util.Iterator;
import java.util.LinkedList;

/*
This class will check for an interval condition satisfaction.
The List passed to conditionSatisfied should be as such:
E(i) is the i-th element of the list, then
E(i).value represents the sample number of the recording.

conditionSatisfied will return true if for any E(i+1)-E(i)>=desiredInterval

The list is a continuously growing list, meaning in each call to this function the list will have
an increased size compared to the previous call. But we're only interested in the new section,
therefore, using the private member previousPopCount, we can discard any old entries.
 */
public class IntervalCondition implements StopCondition {
//TODO: Start considering interval condition only after certain period of time
    private int previousPopCount = 0;
    private int desiredInterval = -1; //Desired interval in indices

    IntervalCondition(int desiredInterval){
        this.desiredInterval=desiredInterval;
    }

    @Override
    public boolean conditionSatisfied(LinkedList<Integer> popList) {
        Iterator<Integer> iterator = popList.listIterator(previousPopCount);
        Integer previous = null;
        Integer current = null;

        while (iterator.hasNext()) {
            previous = current;
            current = iterator.next();

            if(previous==null){
                continue;
            }

            if((current - previous)>=desiredInterval) {
                return true;
            }
        }
        previousPopCount += popList.size(); //Update previous pop count before returning
        return false;
    }
}
