package popcorp.robopop;

import java.util.LinkedList;

/**
 * An base class for peak finding classes
 */
public abstract class PeakFinder {

    //==== Variables ====//
    protected int factor = 0;
    protected int popWidth = 0;

    //====Methods====//
    /**
     * C'tor
     * @param factor, the factor to be used for peak threshold calculation
     * @param popWidth, is the width in indices of each expected peak (pop)
     */
    public PeakFinder(int factor, int popWidth) {
        this.factor = factor;
        this.popWidth = popWidth;
    }

    /**
     * This method must update peakList with the indices, from when getPops was first called, at
     * which a peak was detected
     *
     * @param peakList, the list to update with the indices
     * @param recording, is an array representing an audio recording
     * @param length, is the length of the array
     */
    abstract void getPops(LinkedList<Integer> peakList, final short[] recording, int length);

    /**
     * The reset method must enable reuse of the instance without destruction
     */
    abstract void reset();

}
