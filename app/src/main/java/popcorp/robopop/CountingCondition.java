package popcorp.robopop;

import java.util.LinkedList;

/*
This class will return true using the conditionSatisfied method if the number of elements
in the popList is greater or equal to the desired index count
 */
public class CountingCondition extends StopCondition {

    private int desiredIndexCount = -1;

    CountingCondition(LinkedList<Integer> popList, int desiredIndexCount){
        super(popList);
        this.desiredIndexCount = desiredIndexCount;
    }

    @Override
    public boolean conditionSatisfied() {
        if (popList.size() >= desiredIndexCount) {
            return true;
        }
        return false;
    }

    // TODO: fix dummy stuff
    @Override
    public int getCurrentCondition(){
        return 0;
    }
}
