package com.example.yre.media_0723;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

import com.netcompss.ffmpeg4android.Prefs;

import java.io.File;
import java.util.List;


public class MainActivity extends Activity {


    private static final int ACTION_TAKE_VIDEO = 3;
    private VideoView mVideoView;
    public static boolean isOwner = false;
    public File videoFile;
    public String videoPath;
    public Uri videoUri;
    public Intent videoIntent;

    Button.OnClickListener mTakeVidOnClickListener =
            new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dispatchTakeVideoIntent();
                }
            };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACTION_TAKE_VIDEO && resultCode == RESULT_OK){

            videoIntent = data;

            videoUri = data.getData();

            mVideoView = (VideoView) findViewById(R.id.videoView1);
            mVideoView.setVideoURI(videoUri);

            videoPath = getRealPathFromURI(videoUri);
            videoFile = new File(videoPath);

            mVideoView.start();


        }

    }

    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(takeVideoIntent, ACTION_TAKE_VIDEO);
        onActivityResult(ACTION_TAKE_VIDEO, RESULT_OK, takeVideoIntent);
    }


    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }



    //==============================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button vidBtn = (Button) findViewById(R.id.btnShot);

        setBtnListenerOrDisable(
                vidBtn,
                mTakeVidOnClickListener,
                MediaStore.ACTION_VIDEO_CAPTURE
        );

        Button btnSend = (Button) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i(Prefs.TAG, "run Sending.");
                Intent path = new Intent(MainActivity.this, MainActivity_mew.class);
                path.putExtra("path",videoPath);
                startActivity(path);
            }
        });



        Button btnCompress = (Button) findViewById(R.id.btnCompress);
        btnCompress.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i(Prefs.TAG, "run Compress.");
                startAct(CompressProgressBar.class);

            }
        });

        Button btnSplit = (Button) findViewById(R.id.btnSplit);
        btnSplit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(Prefs.TAG, "run Spilt.");

                Intent file = new Intent(MainActivity.this, SplitProgressBar.class);
                file.putExtra("file",videoIntent);
                startActivity(file);
            }
        });

//        Button btnMerge = (Button) findViewById(R.id.btnMerge);
//        btnMerge.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.i(Prefs.TAG, "run Merge.");
//                startAct(MergeProgressBar.class);
//            }
//        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static boolean isIntentAvailable(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list =
                packageManager.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    private void setBtnListenerOrDisable(
            Button btn,
            Button.OnClickListener onClickListener,
            String intentName
    ) {
        if (isIntentAvailable(this, intentName)) {   //weisha..
            btn.setOnClickListener(onClickListener);
        } else {
            btn.setText(
                    getText(R.string.cannot).toString() + " " + btn.getText());
            btn.setClickable(false);
        }
    }

    private void startAct(Class act) {
        Intent intent = new Intent(this, act);
        Log.d(Prefs.TAG, "Starting act:" + act);
        this.startActivity(intent);
    }

}
