package popcorp.robopop;

import java.util.LinkedList;


public interface PeakFinder {

        void getPops(LinkedList<Integer> peakList, final short[] recording, int length);

        void reset();

}
