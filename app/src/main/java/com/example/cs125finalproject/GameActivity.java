package com.example.cs125finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

public class GameActivity extends AppCompatActivity {

    String diff = "";

    FrameLayout game;
    GameSurface gameSurface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        diff = getIntent().getStringExtra("difficulty");

        game= new FrameLayout(this);
        gameSurface = new GameSurface(this);

        game.addView(gameSurface);
        setContentView(game);
    }

    @Override
    protected void onPause(){
        super.onPause();
        gameSurface.pause();
    }

    @Override
    protected void onResume(){
        super.onResume();
        gameSurface.resume();
    }

    public class GameSurface extends SurfaceView implements Runnable {

        Thread gameThread;
        SurfaceHolder holder;
        volatile boolean running = false;
        Display screenDisplay;
        Point sizeOfScreen;
        int screenWidth, screenHeight;

        int score = 0;

        Paint scorePaint, diffPaint;

        public GameSurface(Context context) {
            super(context);

            holder = getHolder();
            screenDisplay = getWindowManager().getDefaultDisplay();
            sizeOfScreen = new Point();
            screenDisplay.getSize(sizeOfScreen);
            screenWidth = sizeOfScreen.x;
            screenHeight = sizeOfScreen.y;

            diffPaint= new Paint();
            diffPaint.setTextSize(50);
            scorePaint= new Paint();
            scorePaint.setTextSize(50);
        }

        @Override
        public void run() {
            while (running) {
                if (!holder.getSurface().isValid())
                    continue;
                Canvas canvas = holder.lockCanvas();
                canvas.drawRGB(0, 150, 150);

                score++;

                canvas.drawText("Difficulty: " + diff, 10, 50, diffPaint);
                canvas.drawText("Score: " + score, 10, 100, scorePaint);
                holder.unlockCanvasAndPost(canvas);
            }
        }

        public void resume(){
            running=true;
            gameThread=new Thread(this);
            gameThread.start();
        }

        public void pause() {
            running = false;
            while (true) {
                try {
                    gameThread.join();
                }
                catch (InterruptedException e) {

                }
            }
        }
    }
}
