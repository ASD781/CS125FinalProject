package com.example.cs125finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity {

    boolean jumping = false;
    boolean up = false;
    String diff = "";
    Map<String, Integer> diffPeriodMap;

    FrameLayout game;
    GameSurface gameSurface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        diff = getIntent().getStringExtra("difficulty");
        diffPeriodMap = new HashMap<>();
        diffPeriodMap.put("Easy", 1000);
        diffPeriodMap.put("Medium", 2000);
        diffPeriodMap.put("Hard", 3000);

        game = new FrameLayout(this);
        gameSurface = new GameSurface(this);

        game.addView(gameSurface);
        setContentView(game);

        gameSurface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!jumping) {
                    jumping = true;
                    up = !up;
                }
            }
        });
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

        boolean gameOverReady = false;

        int pacSpeed = 30;
        int score;

        Paint scorePaint, diffPaint;
        Bitmap border1, border2;
        Timer timer;
        MediaPlayer mediaPlayer;

        Player pac = new Player(pacSpeed, 0, 0, R.drawable.us, "N/A");
        Player[] ghosts = new Player[] {
                new Player(0, screenWidth * 2, 0, R.drawable.geoff, "top"),
                new Player(0, screenWidth * 2, 0, R.drawable.ben, "bot"),
        };

        public GameSurface(Context context) {
            super(context);
            setVisibility(View.VISIBLE);

            holder = getHolder();
            screenDisplay = getWindowManager().getDefaultDisplay();
            sizeOfScreen = new Point();
            screenDisplay.getSize(sizeOfScreen);
            screenWidth = sizeOfScreen.x;
            screenHeight = sizeOfScreen.y;

            diffPaint= new Paint();
            diffPaint.setTextSize(50);
            diffPaint.setColor(Color.WHITE);
            scorePaint= new Paint();
            scorePaint.setTextSize(50);
            scorePaint.setColor(Color.WHITE);

            border1 = BitmapFactory.decodeResource(getResources(), R.drawable.color);
            border1 = Bitmap.createScaledBitmap(border1,screenWidth,50,true);
            border2 = BitmapFactory.decodeResource(getResources(), R.drawable.color);
            border2 = Bitmap.createScaledBitmap(border1,screenWidth,50,true);

            pac.setY(screenHeight - 250 - pac.getBitmap().getHeight());
            for (Player p: ghosts) {
                setGhost(p);
            }

            score = 0;

            mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.gamemusic);
            mediaPlayer.start();

            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    score++;
                }
            }, 0, diffPeriodMap.get(diff));
        }

        @Override
        public void run() {
            while (running) {
                if (!holder.getSurface().isValid()) {
                    continue;
                }
                Canvas canvas = holder.lockCanvas();
                canvas.drawRGB(0, 0, 0);

                if (!ghostsNull()) {

                    if (jumping) {
                        if (up) {
                            pac.moveY(pac.getSpeed() * -1);
                            if (pac.getY() < 250) {
                                pac.setY(250);
                                jumping = false;
                            }
                        } else {
                            pac.moveY(pac.getSpeed());
                            if (pac.getY() > screenHeight - 250 - pac.getBitmap().getHeight()) {
                                pac.setY(screenHeight - 250 - pac.getBitmap().getHeight());
                                jumping = false;
                            }
                        }
                    }

                    for (Player p: ghosts) {
                        p.moveX(p.getSpeed() * -1);
                        if (p.getX() <= (screenWidth * -1)) {
                            setGhost(p);
                        }
                    }

                    if (pac.isIntersecting(ghosts)) {
                        running = false;
                        gameOverReady = true;
                    }

                    canvas.drawText("Difficulty: " + diff, 10, 50, diffPaint);
                    canvas.drawText("Score: " + score, 10, 100, scorePaint);
                    canvas.drawBitmap(border1, 0, 200, null);
                    canvas.drawBitmap(border2, 0, screenHeight - 200, null);
                    canvas.drawBitmap(pac.getBitmap(), pac.getX(), pac.getY(), null);
                    for (Player p: ghosts) {
                        canvas.drawBitmap(p.getBitmap(), p.getX(), p.getY(), null);
                    }
                }

                holder.unlockCanvasAndPost(canvas);
            }

            if (gameOverReady) {
                Intent gameOverIntent = new Intent(GameActivity.this, GameOverActivity.class);
                gameOverIntent.putExtra("score", score);
                startActivity(gameOverIntent);
                /*runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setVisibility(View.GONE);
                    }
                });*/
                setVisibility(View.GONE);
                //throw new IndexOutOfBoundsException();
                running = false;
                finish();
            }
        }

        public void resume(){
            running = true;
            gameThread = new Thread(this);
            gameThread.start();
        }

        public void pause() {
            running = false;
            mediaPlayer.stop();
            mediaPlayer.release();
            while (true) {
                try {
                    gameThread.join();
                }
                catch (InterruptedException e) {

                }
            }
        }

        public void setGhost(Player p) {
            p.setX(screenWidth * 2);
            if (p.getOri().equals("top")) {
                p.setY(250);
            } else {
                p.setY(screenHeight - 250 - p.getBitmap().getHeight());
            }
            p.setSpeed(pacSpeed + (int) (Math.random() * 25) - 25);
        }

        public boolean ghostsNull() {
            for (Player p: ghosts) {
                if (p == null) {
                    return true;
                }
            }
            return false;
        }
    }

    public class Player {
        int speed, x, y;
        Bitmap bitmap;
        String ori;

        public Player(int nSpeed, int nX, int nY, int id, String nOri) {
            speed = nSpeed;
            x = nX;
            y = nY;
            bitmap = BitmapFactory.decodeResource(getResources(), id);
            bitmap = Bitmap.createScaledBitmap(bitmap,100,100,true);
            ori = nOri;
        }

        public int getSpeed() {
            return speed;
        }

        public void setSpeed(int nSpeed) {
            speed = nSpeed;
        }

        public int getX() {
            return x;
        }

        public void moveX(int dx) {
            x += dx;
        }

        public void setX(int nX) {
            x = nX;
        }

        public int getY() {
            return y;
        }

        public void moveY(int dy) {
            y += dy;
        }

        public void setY(int nY) {
            y = nY;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

        public void setOri(String nOri) {
            ori = nOri;
        }

        public String getOri() {
            return ori;
        }

        public boolean isIntersecting(Player p) {
            Rect r1= new Rect(x, y, x + bitmap.getWidth(), y + bitmap.getHeight());
            Rect r2= new Rect(p.getX(), p.getY(),
                    p.getX() + p.getBitmap().getWidth(),
                    p.getY() + p.getBitmap().getHeight());
            return r1.intersect(r2);
        }

        public boolean isIntersecting(Player[] pArr) {
            boolean intersecting = false;
            Rect r1= new Rect(x, y, x + bitmap.getWidth(), y + bitmap.getHeight());
            for (Player p: pArr) {
                Rect r2 = new Rect(p.getX(), p.getY(),
                        p.getX() + p.getBitmap().getWidth(),
                        p.getY() + p.getBitmap().getHeight());
                if (r1.intersect(r2)) {
                    intersecting = true;
                    break;
                }
            }
            return intersecting;
        }
    }
}
