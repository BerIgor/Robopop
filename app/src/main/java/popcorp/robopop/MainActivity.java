package popcorp.robopop;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedList;

import static android.media.AudioFormat.CHANNEL_IN_MONO;
import static android.media.AudioFormat.ENCODING_PCM_16BIT;

public class MainActivity extends AppCompatActivity {

    /**
     * This class is the Activity that is present on the screen during popcorn preparation.
     * It contains the AsyncTasks that handle recording and processing, as well as managing the
     * recording task.
     */

    //===== Constants =====//
    static final int desiredIntervalSeconds = 2;
    static final int windowSize = 4;
    static final int sampleRate = 44100;

    //====== Variables ======//
    // Recorder and Processor Tasks
    private RecorderTask recorderTask = null;
    private ProcessorTask processorTask = null;
    public LinkedList<Integer> popIndexList = new LinkedList<>(); // Data structure for peak indices
    private StopCondition stopCondition = null;
    private PeakFinder peakFinder = null;
    public int bufferSize = -1;
    public static short recording[][] = null;
    private AudioRecord audioRecorder = null;
    private boolean alreadySatisfied;
    private MediaPlayer mediaPlayer = null;

    // Layout Items
    ImageView noiseStep = null;
    ImageView peakStep = null;
    ImageView intervalStep = null;


    //====== Methods ======//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("IGOR", "MAIN - onCreate");
        super.onCreate(savedInstanceState);

        // Action Bar background
        android.support.v7.app.ActionBar bar = getSupportActionBar();
        Drawable d = getResources().getDrawable(R.drawable.popcorn_top, this.getTheme());
        bar.setBackgroundDrawable(d);

        // Recorder Setup
        bufferSize = AudioRecord.getMinBufferSize(sampleRate, CHANNEL_IN_MONO, ENCODING_PCM_16BIT);

        // Media Player - for final notification
        mediaPlayer = new MediaPlayer();
        mediaPlayer = MediaPlayer.create(this, getIntent().getIntExtra("Sound", R.raw.get_to_the_choppa));

        Log.i("IGOR", "MAIN - buffer size " + (new Integer(bufferSize)).toString());

        bufferSize = windowSize * sampleRate;
        audioRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, CHANNEL_IN_MONO, ENCODING_PCM_16BIT, 2*bufferSize);

        // Task Creation
        recorderTask = new RecorderTask();

        // ====Layout Creation====
        setContentView(R.layout.activity_main);
        ImageButton stopButton = (ImageButton)findViewById(R.id.stopButton);

        // Kernel Steps
        noiseStep = (ImageView) findViewById(R.id.noiseStep);
        peakStep = (ImageView)findViewById(R.id.peakStep);
        intervalStep = (ImageView)findViewById(R.id.intervalStep);
        // Text Steps - setting font
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Market_Deco.ttf");
        TextView noiseText = (TextView)findViewById(R.id.noiseStepText);
        TextView peakText = (TextView)findViewById(R.id.peakStepText);
        TextView intervalText = (TextView)findViewById(R.id.intervalStepText);

        noiseText.setTypeface(typeface);
        peakText.setTypeface(typeface);
        intervalText.setTypeface(typeface);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // ====End of Layout Creation====

        // Stop Button
        // Stop all tasks and return to the Startup Activity
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("IGOR", "MAIN - on STOP");
                finish();
            }
        });
    } //end of onCreate

    @Override
    protected void onStart(){
        super.onStart();

        Log.i("IGOR", "MAIN - onStart");

        popIndexList = new LinkedList<>(); // Data structure for peak indices
        recording = new short[2][bufferSize];

        // Stop Condition
        // Process the power and sound selected
        int power = getIntent().getIntExtra("Power", 0);
        stopCondition = new IntervalCondition(popIndexList, sampleRate * desiredIntervalSeconds, power);
//        stopCondition = new CountingCondition(desiredPopCount);

        peakFinder = PeakFinderFactory.getPeakFinder(sampleRate, power);

        // Flag for preventing multiple alerts
        alreadySatisfied = false;

        // Start the Recorder Thread
        recorderTask.execute();
    }

    /**
     * This method will be invoked whenever the app is sent to the background, and reset the app.
     * In such a case (until further knowledge is gained), the entire app should be restarted.
     */
    @Override
    protected void onPause() {
        super.onPause();
        stopAll(MainActivity.this);
        finish();
    }

    /**
     * Prepares everything for a second run of the app
     *
     * @param mainActivity is the main activity in which all should be stopped :)
     */
    private static void stopAll(MainActivity mainActivity) {
        mainActivity.peakFinder.reset();
        mainActivity.audioRecorder.stop();
        mainActivity.audioRecorder.release();
        if(!mainActivity.recorderTask.isCancelled()) {
            mainActivity.recorderTask.cancel(true);
        }
    }

    //===================Recorder Task===============================//
    private class RecorderTask extends AsyncTask <Object, Object, Object> implements AudioRecord.OnRecordPositionUpdateListener{

        /**
         * This private class handles the recording of audio data
         */


        //====== Variables ======//
        private int arrayIndex = 0;

        /**
         * Constructor
         */
        public RecorderTask() {
            Log.i("IGOR", "RECORDER - CTOR");
            // Configure Audio Recorder
            audioRecorder.setRecordPositionUpdateListener(this);
            audioRecorder.setPositionNotificationPeriod(bufferSize);
        }

        /**
         *Begins the recording process
         *
         * @param params - are insignificant and not used
         * @return null, always
         */
        @Override
        protected Object doInBackground(Object... params) {
            Log.i("IGOR", "RECORDER - doInBackground");
            audioRecorder.startRecording();

            return null;
        }


        /**
         * Invoked when recording head reaches some periodic marker
         *
         * Reads the new audio data, then creates and executes a processor task to handle the data
         */
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
            //publishProgress();
        }

        /**
         * Properly stops the audio recorder
         */
        @Override
        protected void onCancelled(Object o) {
            super.onCancelled();
            Log.i("IGOR", "RECORDER - Stopped ");
            audioRecorder.stop();
            audioRecorder.release();
        }

        @Override
        public void onMarkerReached(AudioRecord audioRecord){
            return;
        }
    }

    //===================Processor Task===============================//
    private class ProcessorTask extends AsyncTask<Object, Object, Object> {

        /**
         * This class handles audio data retrieved from the audio recorder
         */

        private int arrayIndex = 0; //Represents the index at which to start processing of data

        /**
         * Constructor
         * @param arrayIndexToProcess is the index of the array from which to start the processing
         *                            of data
         */
        ProcessorTask(int arrayIndexToProcess){
            arrayIndex = arrayIndexToProcess;
        }

        /**
         * Invokes the peak finder to update the popList
         *
         * @param params - are insignificant
         * @return always null
         */
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

        /**
         * Checks if the stopCondition is satisfied
         * If yes- creates an alert dialog and plays a sound
         *
         * @param values - are insignificant
         */
        @Override
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);
            Log.i("IGOR", "PROCESSOR - ON PROGRESS UPDATE");

            //TODO: Remove
            // DEBUG
            // Print the list
            Log.i("IGOR", popIndexList.toString());
            //DEBUG

            if (stopCondition.conditionSatisfied() && !alreadySatisfied) {
                //We're done
                alreadySatisfied = true;
                Log.i("IGOR", "PROCESSOR - I AM SATISFIED");
                mediaPlayer.start(); // no need to call prepare(), create() does that for you
                // Alert Dialog
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this, R.style.MyDialogTheme);
                alertDialogBuilder.setCancelable(false);

                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                alertDialog.setContentView(R.layout.done_dialog_layout);
                Button doneOkButton = (Button) alertDialog.findViewById(R.id.OKbutton);

                doneOkButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.cancel();
                        finish();
                    }
                });

                Typeface quickFont = Typeface.createFromAsset(getAssets(),"fonts/Mikodacs.otf");
                TextView doneTitleText = (TextView) alertDialog.findViewById(R.id.doneTitleText);
                doneTitleText.setTypeface(quickFont);

            } else {
                //We're not done yet
                int currentCondition = stopCondition.getCurrentCondition();
                switch (currentCondition){
                    case 0:
                        Log.i("OR", "CASE 0");
                        break;
                    case 1:
                        Log.i("OR", "CASE 1");
                        noiseStep.setImageResource(R.drawable.popped_kernel);
                        break;
                    case 2:
                        Log.i("OR", "CASE 2");
                        peakStep.setImageResource(R.drawable.popped_kernel);
                        break;
                    case 3:
                        Log.i("OR", "CASE 3");
                        intervalStep.setImageResource(R.drawable.popped_kernel);
                        break;
                }

            }

        }
    }
}
