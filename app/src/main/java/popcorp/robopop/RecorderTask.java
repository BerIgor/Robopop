package popcorp.robopop;

import android.media.AudioRecord;
import android.os.AsyncTask;

/**
 * Created by Igor on 05-Aug-17.
 */

public class RecorderTask extends AsyncTask {

    volatile boolean keepRecording = true;
    private AudioRecord audioRecorder = null;
    private short recording[][] = null;
    private int bufferSize = -1;

    private int arrayIndex = 0;

    public RecorderTask(){

    }

    @Override
    protected Object doInBackground(Object[] params) {
        publishProgress();
        return null;
    }


}
