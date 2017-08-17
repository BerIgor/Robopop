package popcorp.robopop;

import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Toast;


import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import static android.media.AudioFormat.CHANNEL_IN_MONO;
import static android.media.AudioFormat.ENCODING_PCM_16BIT;

public class MainActivity extends AppCompatActivity {


    //===== Constants =====//
    static final int desiredIntervalSeconds = 2;
    static final int windowSize = 4;
    static final int sampleRate = 44100;
    static final int factor = 25;
    static final double sPopWidth = 0.03; // pop width in seconds

    //====== Variables ======//
    // Recorder and Processor Tasks
    private RecorderTask recorderTask = null;
    private ProcessorTask processorTask = null;

    public LinkedList<Integer> popIndexList = new LinkedList<>(); // Data structure for peak indices
    private StopCondition stopCondition = null;
    private PeakFinder peakFinder = null;

    private Button stopButton = null;

    public int bufferSize = -1; //TODO: Check premissions

    public static short recording[][] = null;

    private AudioRecord audioRecorder = null;

    private Intent startupIntent = null;

    private MediaPlayer mediaPlayer = null;

    //====== Methods ======//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("IGOR", "MAIN - onCreate");
        super.onCreate(savedInstanceState);


        // Recorder Setup
        bufferSize = AudioRecord.getMinBufferSize(sampleRate, CHANNEL_IN_MONO, ENCODING_PCM_16BIT);

        // Media Player - for final notification
        mediaPlayer = new MediaPlayer();
        mediaPlayer = MediaPlayer.create(this, R.raw.sound);

        Log.i("IGOR", "MAIN - buffer size " + (new Integer(bufferSize)).toString());

        bufferSize = windowSize * sampleRate;
        audioRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, CHANNEL_IN_MONO, ENCODING_PCM_16BIT, 2*bufferSize);

        // Calculate pop width (to define window in which we ignore extra pops) in samples
        int popWidth = (int)(sPopWidth*sampleRate);

        Log.i("IGOR", "MAIN - pop width " + (new Integer(popWidth)).toString());

        // Processor Setup
//        peakFinder = new MovingAverage(factor, popWidth);
        peakFinder = new ZamirBerendorf(factor, popWidth);

        // Task Creation
        recorderTask = new RecorderTask();


        // ====Layout Creation====
        setContentView(R.layout.activity_main);
        stopButton = (Button)findViewById(R.id.stopButton);
        // ====End of Layout Creation====

        startupIntent = new Intent(this, StartupActivity.class);

        // ====Stop Button====
        // Stop all tasks and return to the Startup Activity
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("IGOR", "MAIN - on STOP");
                stopAll(MainActivity.this);
                //TODO: return this when you remove the mail thing
//                startActivity(startupIntent);
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
        // Process the power selected
        int power = getIntent().getIntExtra("Power", 0);
        stopCondition = new IntervalCondition(popIndexList, sampleRate * desiredIntervalSeconds, power);
//        stopCondition = new CountingCondition(desiredPopCount);

        // Start the Recorder Thread
        recorderTask.execute();
    }

    /*
    Method for stopping recording and processing
     */
    private static void stopAll(MainActivity mainActivity) {
        mainActivity.peakFinder.reset();
        mainActivity.audioRecorder.stop();
        mainActivity.audioRecorder.release();
        mainActivity.recorderTask.cancel(true);
        sendMail(mainActivity); //TODO: Remove when done developing
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

//    private void sendRecording(){
//        //Set output file path
//        final String fileName = getExternalCacheDir().getAbsolutePath()+"n.txt";
//        Log.i("OUTPUT_FILE", fileName);
//        //Attempt file creation
//        final File root = new File(fileName);
//        if(!root.canWrite()){
//            Log.i("DEBUG", "Cannot write");
//        }
//        //Save data to file
//        DataOutputStream fos = null;
//        try {
//            fos = new DataOutputStream(new FileOutputStream(root));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
////        int recorded = segmentCount*bufferSize;
//        for(int d=0; d<recordingList.size(); d++) {
//            try {
//                fos.writeShort((int)recordingList.get(d));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        try {
//            fos.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
////        String filename=root.getName();
//        Uri path = Uri.fromFile(root);
//        Intent emailIntent = new Intent(Intent.ACTION_SEND); // set the type to 'email'
//        emailIntent .setType("vnd.android.cursor.dir/email");
//        String to[] = {"or.zzamir@gmail.com"};
//        emailIntent .putExtra(Intent.EXTRA_EMAIL, to);// the attachment
//        emailIntent .putExtra(Intent.EXTRA_STREAM, Uri.parse(path.toString()));// the mail subject
//        emailIntent .putExtra(Intent.EXTRA_SUBJECT, "Subject");
//        startActivity(Intent.createChooser(emailIntent , "Send email..."));
//        //END of File Saving
//
//    }




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



            if (stopCondition.conditionSatisfied()) {
                Log.i("IGOR", "PROCESSOR - I AM SATISFIED");
                stopAll(MainActivity.this);
                mediaPlayer.start(); // no need to call prepare(); create() does that for you
                // Alert Dialog
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                String alertTitle = "Quick,";
                String alertMessage = "your popcorn is ready";
                String alertButtonText = "Okay";
                alertDialogBuilder.setMessage(alertMessage).setTitle(alertTitle);
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setPositiveButton(alertButtonText, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id){
                        startActivity(startupIntent);
                    }

                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                //TODO: Do something fancy
            }

        }
    }
}
