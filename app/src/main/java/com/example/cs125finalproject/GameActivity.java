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
import android.os.Bundle;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

public class GameActivity extends AppCompatActivity {

    boolean jumping = false;
    boolean up = false;
    String diff = "";

    FrameLayout game;
    GameSurface gameSurface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        diff = getIntent().getStringExtra("difficulty");

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

        int pacSpeed = 30;
        int score, jumpCount, jumpCountMax;

        Paint scorePaint, diffPaint;
        Player pac = new Player(pacSpeed, 0, 0, R.drawable.pacman);
        Player ghost1 = new Player(0, screenWidth * 2, 0, R.drawable.ghost);;
        Player ghost2 = new Player(0, screenWidth * 2, 0, R.drawable.ghost);;

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
            diffPaint.setColor(Color.WHITE);
            scorePaint= new Paint();
            scorePaint.setTextSize(50);
            scorePaint.setColor(Color.WHITE);

            pac.setY(screenHeight - (pac.getBitmap().getHeight() * 2));
            setGhost(ghost1, "top");
            setGhost(ghost2, "bot");

            score = 0;

            jumpCount = 0;
            jumpCountMax = (50 * pac.getSpeed()) % screenHeight;
        }

        @Override
        public void run() {
            while (running) {
                if (!holder.getSurface().isValid())
                    continue;
                Canvas canvas = holder.lockCanvas();
                canvas.drawRGB(0, 0, 0);

                score++;
                if (jumping) {
                    if (up) {
                        pac.moveY(pac.getSpeed() * -1);
                    } else {
                        pac.moveY(pac.getSpeed());
                    }
                    jumpCount += pac.getSpeed();
                    if (jumpCount == jumpCountMax) {
                        jumpCount = 0;
                        jumping = false;
                    }
                }
                if (ghost1 != null && ghost2 != null) {

                    ghost1.moveX(ghost1.getSpeed() * -1);
                    if (ghost1.getX() <= (screenWidth * -1)) {
                        setGhost(ghost1, "top");
                    }
                    ghost2.moveX(ghost2.getSpeed() * -1);
                    if (ghost2.getX() <= (screenWidth * -1)) {
                        setGhost(ghost2, "bot");
                    }

                    if (pac.isIntersecting(ghost1) || pac.isIntersecting(ghost2)) {
                        running = false;
                        Intent restartIntent = new Intent();
                        finish();
                    }

                    canvas.drawText("Difficulty: " + diff, 10, 50, diffPaint);
                    canvas.drawText("Score: " + score, 10, 100, scorePaint);
                    canvas.drawBitmap(pac.getBitmap(), pac.getX(), pac.getY(), null);
                    canvas.drawBitmap(ghost1.getBitmap(), ghost1.getX(), ghost1.getY(), null);
                    canvas.drawBitmap(ghost2.getBitmap(), ghost2.getX(), ghost2.getY(), null);
                }

                holder.unlockCanvasAndPost(canvas);
            }
        }

        public void resume(){
            running = true;
            gameThread = new Thread(this);
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

        public void setGhost(Player p, String ori) {
            p.setX(screenWidth);
            if (ori.equals("top")) {
                p.setY(screenHeight - (p.getBitmap().getHeight() * 2));
            } else {
                p.setY(p.getBitmap().getHeight() * 2);
            }
            p.setSpeed(pacSpeed + (int) (Math.random() * 25) - 25);
        }
    }

    public class Player {
        int speed, x, y;
        Bitmap bitmap;

        public Player(int nSpeed, int nX, int nY, int id) {
            speed = nSpeed;
            x = nX;
            y = nY;
            bitmap = BitmapFactory.decodeResource(getResources(), id);
            bitmap = Bitmap.createScaledBitmap(bitmap,100,100,true);
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

        public boolean isIntersecting(Player p) {
            Rect r1= new Rect(x, y, x + bitmap.getWidth(), y + bitmap.getHeight());
            Rect r2= new Rect(p.getX(), p.getY(),
                    p.getX() + p.getBitmap().getWidth(),
                    p.getY() + p.getBitmap().getHeight());
            return r1.intersect(r2);
        }
    }
}
