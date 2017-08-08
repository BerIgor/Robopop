package popcorp.robopop;

import java.util.LinkedList;

/**
 * Created by Igor on 03-Aug-17.
 */

public interface StopCondition {
    public boolean conditionSatisfied(LinkedList<Integer> popList);
}
