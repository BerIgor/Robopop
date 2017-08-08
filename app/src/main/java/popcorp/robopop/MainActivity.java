package popcorp.robopop;

import android.content.Intent;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import java.util.LinkedList;
import java.util.ListIterator;

import static android.media.AudioFormat.CHANNEL_IN_MONO;
import static android.media.AudioFormat.ENCODING_PCM_16BIT;

public class MainActivity extends AppCompatActivity {


    //====== Variables ======//
    // Recorder and Processor Tasks
    private RecorderTask recorderTask = null;
    private ProcessorTask processorTask = null;

    public LinkedList<Integer> popIndexList = new LinkedList<Integer>(); // Data structure for peak indices
    private StopCondition stopCondition = null;
    private PeakFinder peakFinder = null;

    private int desiredIntervalSeconds = 2;
    private int desiredPopCount = 460;
    private Button stopButton = null;

    private int windowSize = 2;

    public static int sampleRate = 44100;
    public int bufferSize = -1; //TODO: Check premissions

    public static short recording[][] = null;

    private AudioRecord audioRecorder = null;

    //====== Methods ======//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("IGOR", "MAIN - onCreate");
        super.onCreate(savedInstanceState);


        // Recorder Setup
        bufferSize = AudioRecord.getMinBufferSize(sampleRate, CHANNEL_IN_MONO, ENCODING_PCM_16BIT);

        Log.i("IGOR", "MAIN - buffer size " + (new Integer(bufferSize)).toString());

        bufferSize = windowSize * sampleRate;
        audioRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, CHANNEL_IN_MONO, ENCODING_PCM_16BIT, 2*bufferSize);

 //       int cunt = audioRecorder.getBufferSizeInFrames();
 //       Log.i("IGOR", "MAIN - buffer size in frames " + (new Integer(cunt)).toString());

        // Processor Setup
        peakFinder = new MovingAverage(10, 700);

        // Task Creation
        recorderTask = new RecorderTask();


        // ====Layout Creation====
        setContentView(R.layout.activity_main);
        stopButton = (Button)findViewById(R.id.stopButton);
        // ====End of Layout Creation====

        final Intent startupIntent = new Intent(this, StartupActivity.class);

        // ====Stop Button====
        // Stop all tasks and return to the Startup Activity
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("IGOR", "MAIN - on STOP");

                audioRecorder.stop();
                audioRecorder.release();

                recorderTask.cancel(true);
//                processorTask.cancel(true);

                startActivity(startupIntent);
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        Log.i("IGOR", "MAIN - onStart");
        popIndexList = new LinkedList<Integer>(); // Data structure for peak indices
        recording = new short[2][bufferSize];

        // Stop Condition
        stopCondition = new IntervalCondition(sampleRate * desiredIntervalSeconds);
//        stopCondition = new CountingCondition(desiredPopCount);

        // Start the Recorder Thread
        recorderTask.execute();
    }


//        //TODO: REMOVE
//        addToArray();
//        //TODO: STOP REMOVE
//
//        Log.i("MAIN", popIndexList.toString());
//        Log.i("MAIN- GOT ", (new Integer(popIndexList.size())).toString());



//    //TODO: Remove below here
//    private LinkedList<Short> recordingList = new LinkedList<>();
//    private int arrayIndex = 0;
//    private int segmentCount = 0;

//    Adds the amplitude of peaks
//    public void addToArray(){
//        for(int i=0; i<bufferSize; i++){
//            recordingList.add(recording[arrayIndex][i]);
//        }
//        arrayIndex = 1 - arrayIndex;
//    }
/*
    public void summarize(){

        //Set output file path
        final String fileName = getExternalCacheDir().getAbsolutePath()+"n.txt";
        Log.i("OUTPUT_FILE", fileName);
        //Attempt file creation
        final File root = new File(fileName);
        if(!root.canWrite()){
            Log.i("DEBUG", "Cannot write");
        }

        //Save data to file
        DataOutputStream fos = null;

        try {
             fos = new DataOutputStream(new FileOutputStream(root));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
//        int recorded = segmentCount*bufferSize;

        for(int d=0; d<recordingList.size(); d++) {
            try {
                fos.writeShort((int)recordingList.get(d));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        String filename=root.getName();
        Uri path = Uri.fromFile(root);
        Intent emailIntent = new Intent(Intent.ACTION_SEND); // set the type to 'email'
        emailIntent .setType("vnd.android.cursor.dir/email");
        String to[] = {"or.zzamir@gmail.com"};
        emailIntent .putExtra(Intent.EXTRA_EMAIL, to);// the attachment
        emailIntent .putExtra(Intent.EXTRA_STREAM, Uri.parse(path.toString()));// the mail subject
        emailIntent .putExtra(Intent.EXTRA_SUBJECT, "Subject");
        startActivity(Intent.createChooser(emailIntent , "Send email..."));
        //END of File Saving
    }
    //TODO: STOP REMOVE

    public void restart(){

    }
*/

    //===================Recorder Task===============================//
    private class RecorderTask extends AsyncTask <Object, Object, Object> implements AudioRecord.OnRecordPositionUpdateListener{

        //====== Variables ======//
        private int arrayIndex = 0;


        public RecorderTask() {
            Log.i("IGOR", "RECORDER - CTOR");

            // Configure Audio Recorder
            audioRecorder.setRecordPositionUpdateListener(this);
            audioRecorder.setPositionNotificationPeriod(bufferSize);
        }

        @Override
        protected Object doInBackground(Object... params) {
            Log.i("IGOR", "RECORDER - doInBackground");
            audioRecorder.startRecording();

            return null;
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);
            Log.i("IGOR", "RECORDER - onProgressUpdate - "+Thread.currentThread().getName().toString());
            processorTask.execute();
            return;
        }

        @Override
        public void onMarkerReached(AudioRecord recorder) {
            return;
        }

        // Invoked when recording head reaches some periodic marker
        @Override
        public void onPeriodicNotification(AudioRecord recorder) {
            Log.i("IGOR", "RECORDER - onPeriodicNotification: SAVING TO " + (new Integer(arrayIndex)).toString());
            if (processorTask != null) {
                processorTask.cancel(true);
            }
            processorTask = new ProcessorTask(arrayIndex);

            int read = audioRecorder.read(recording[arrayIndex], 0, bufferSize);
            Log.i("IGOR", "RECORDER - recorded " + (new Integer(read)).toString());
            arrayIndex = 1 - arrayIndex;
            processorTask.execute();
//            publishProgress();
        }

        // Irrelevant because activated only after runInBackground returns, which happens way
        // before we invoke cancel
        @Override
        protected void onCancelled(Object o) {
            super.onCancelled();
            Log.i("IGOR", "RECORDER - Stopped ");
            audioRecorder.stop();
            audioRecorder.release();
        }
    }

    //===================Processor Task===============================//

    private class ProcessorTask extends AsyncTask<Object, Object, Object> {

        private int arrayIndex = 0;

        ProcessorTask(int arrayIndexToProcess){
            arrayIndex = arrayIndexToProcess;
        }

        @Override
        protected Object doInBackground(Object... params) {
            if (isCancelled()) {
                Log.i("IGOR", "PROCESSOR - isCancelled");
                return null;
            }
            Log.i("IGOR", "PROCESSOR - doInBackground");
            peakFinder.getPops(popIndexList,recording[arrayIndex], bufferSize);
            Log.i("IGOR", "PROCESSOR - PROCESSING " + (new Integer(arrayIndex)).toString());
            publishProgress();
            return null;
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);
            Log.i("IGOR", "PROCESSOR - ON PROGRESS UPDATE");

            // DEBUG
            // Print the list
            Log.i("IGOR", popIndexList.toString());
        }
    }
}
