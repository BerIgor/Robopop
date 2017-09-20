package popcorp.robopop;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Iterator;
import java.util.LinkedList;

import static android.media.AudioFormat.CHANNEL_IN_MONO;
import static android.media.AudioFormat.ENCODING_PCM_16BIT;

public class MainActivity extends AppCompatActivity {


    //===== Constants =====//
    static final int desiredIntervalSeconds = 2;
    static final int windowSize = 4;
    static final int sampleRate = 44100;
    static final int factor = 25;


    //====== Variables ======//
    // Recorder and Processor Tasks
    private RecorderTask recorderTask = null;
    private ProcessorTask processorTask = null;

    public LinkedList<Integer> popIndexList = new LinkedList<>(); // Data structure for peak indices
    private StopCondition stopCondition = null;
    private PeakFinder peakFinder = null;

    private ImageButton stopButton = null;

    public int bufferSize = -1; //TODO: Check premissions

    public static short recording[][] = null;

    private AudioRecord audioRecorder = null;

    private Intent startupIntent = null;

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
        mediaPlayer = MediaPlayer.create(this, getIntent().getIntExtra("Sound", R.raw.sound0));

        Log.i("IGOR", "MAIN - buffer size " + (new Integer(bufferSize)).toString());

        bufferSize = windowSize * sampleRate;
        audioRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, CHANNEL_IN_MONO, ENCODING_PCM_16BIT, 2*bufferSize);


        // Task Creation
        recorderTask = new RecorderTask();


        // ====Layout Creation====
        setContentView(R.layout.activity_main);
        stopButton = (ImageButton)findViewById(R.id.stopButton);

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

        startupIntent = new Intent(this, StartupActivity.class);

        // ====Stop Button====
        // Stop all tasks and return to the Startup Activity
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("IGOR", "MAIN - on STOP");
                stopAll(MainActivity.this);
                startActivity(startupIntent);
            }
        });
    }

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

        // Start the Recorder Thread
//        while(recorderTask.isCancelled()) {
            recorderTask.execute();
//        }
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        Log.i("IGOR", "MAIN - onRestart");
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.i("IGOR", "MAIN - onPause");
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.i("IGOR", "MAIN - onStop");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.i("IGOR", "MAIN - onStop");
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.i("IGOR", "MAIN - onResume");
    }

    /*
    Method for stopping recording and processing
     */
    private static void stopAll(MainActivity mainActivity) {
        mainActivity.peakFinder.reset();
//        mainActivity.audioRecorder.stop();
//        mainActivity.audioRecorder.release();ssssss
//      if(!mainActivity.recorderTask.isCancelled()) {
            Log.i("IGOR", "MAIN - before cancel");
            mainActivity.recorderTask.cancel(true);
//          Log.i("IGOR", "MAIN - after cancel");
//        }
//        sendMail(mainActivity); //TODO: Remove when done developing
    }

    @Override
    public void onBackPressed() {
        Log.i("IGOR", "MAIN - on STOP back button");
        stopAll(MainActivity.this);
        startActivity(startupIntent);
    }

    /*
    Method for debugging purposes- will print a list of times
     */
    private void printTimes(){
        LinkedList<Integer> popTimes = new LinkedList<>();
        Iterator<Integer> it = popIndexList.iterator();
        while(it.hasNext()){
            popTimes.addLast(new Integer((int)(1000*((double)it.next()/sampleRate))));
        }
        Log.i("IGOR", popTimes.toString());


        LinkedList<Integer> intervalList = new LinkedList<>();
        Integer current;
        Integer previous = new Integer(0);
        for(int i=0; i<popTimes.size(); i++){
            current = popTimes.get(i);
            Integer diff = new Integer(current - previous);
            previous = current;
            intervalList.add(diff);
        }

        Log.i("IGOR", intervalList.toString());
        popTimes.clear();
        intervalList.clear();
    }


    private static void sendMail(MainActivity mainActivity){
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"or.zzamir@gmail.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "suck dick or");
        i.putExtra(Intent.EXTRA_TEXT   , mainActivity.popIndexList.toString());
        try {
            mainActivity.startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(mainActivity, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }


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
            //DEBUG

            //stopCondition.conditionSatisfied()
            if (true) {
                Log.i("IGOR", "PROCESSOR - I AM SATISFIED");
                stopAll(MainActivity.this);
                mediaPlayer.start(); // no need to call prepare(); create() does that for you
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
                        startActivity(startupIntent);
                    }
                });

                Typeface quickFont = Typeface.createFromAsset(getAssets(),"fonts/Mikodacs.otf");
                TextView doneTitleText = (TextView) alertDialog.findViewById(R.id.doneTitleText);
                doneTitleText.setTypeface(quickFont);

                //TODO: Do something fancy
            } else {
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
