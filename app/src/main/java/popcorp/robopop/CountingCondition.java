package popcorp.robopop;

import java.util.LinkedList;

/*
This class will return true using the conditionSatisfied method if the number of elements
in the popList is greater or equal to the desired index count
 */
public class CountingCondition implements StopCondition {

    private int desiredIndexCount = -1;

    CountingCondition(int desiredIndexCount){
        this.desiredIndexCount = desiredIndexCount;
    }

    @Override
    public boolean conditionSatisfied(LinkedList<Integer> popList) {
        if (popList.size() >= desiredIndexCount) {
            return true;
        }
        return false;
    }
}
