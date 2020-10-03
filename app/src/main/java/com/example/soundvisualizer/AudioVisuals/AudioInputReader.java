package com.example.soundvisualizer.AudioVisuals;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Build;

import com.example.soundvisualizer.R;

public class AudioInputReader {

    private final VisualizerView mVisualizerView;
    private final Context mContext;
    private MediaPlayer mPlayer;
    private Visualizer mVisualizer;

    public AudioInputReader(VisualizerView visualizerView, Context context) {
        this.mVisualizerView = visualizerView;
        this.mContext = context;
        initVisualizer();
    }

    private void initVisualizer() {
        // Set up media player
        mPlayer = MediaPlayer.create(mContext, R.raw.htmlthesong);
        mPlayer.setLooping(true);

        // Setup the Visualizer
        // Connect it to the media player
        mVisualizer = new Visualizer(mPlayer.getAudioSessionId());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mVisualizer.setMeasurementMode(Visualizer.MEASUREMENT_MODE_PEAK_RMS);
            mVisualizer.setScalingMode(Visualizer.SCALING_MODE_NORMALIZED);
        }

        // Set the size of the byte array returned for visualization
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[0]);

        mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            @Override
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes, int i) {

            }

            @Override
            public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int i) {

                    // Fast fourier transform

                // If the Visualizer is ready and has data, send that data to the VisualizerView
                if(mVisualizer != null && mVisualizer.getEnabled()){
                    mVisualizerView.updateFFT(bytes);
                }
            }
        }, Visualizer.getMaxCaptureRate(), false, true);

        // Start everything
        mVisualizer.setEnabled(true);
        mPlayer.start();
    }

    public void shutdown(boolean isFinishing){
        if(mPlayer != null){
            mPlayer.pause();
            if(isFinishing){
                mVisualizer.release();
                mPlayer.release();
                mPlayer = null;
                mVisualizer = null;
            }

            if(mVisualizer != null){
                mVisualizer.setEnabled(false);
            }
        }
    }

    public void restart(){
        if (mPlayer != null) {
            mPlayer.start();
        }

        mVisualizer.setEnabled(true);
        mVisualizerView.restart();
    }

}