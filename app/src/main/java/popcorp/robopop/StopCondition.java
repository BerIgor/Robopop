package popcorp.robopop;

import java.util.LinkedList;

/**
 * StopCondition implementations are responsible for checking whether the stop condition is satisfied
 * given a list of indices at which peaks occurred in an audio recording.
 */
public abstract class StopCondition {

    protected LinkedList<Integer> popList;

    public StopCondition(LinkedList<Integer> popList){
        this.popList = popList;
    }

    public abstract boolean conditionSatisfied();
}
