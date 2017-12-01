package com.daila.preetam.ladder;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

public class GameLadder extends SurfaceView implements SurfaceHolder.Callback {
    private MainThread mt;
    private Random rand = new Random();
    private Context ct;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private int side;
    private int height;
    private int width;
    private int ballHeight;
    private int ballWidth;
    private int brickHeight;
    private int brickWidth;

    private float brickStartHeight;
    private float brickEndHeight;
    private float brickStartWidth;
    private float brickEndWidth;

    private float ballStartHeight;
    private float ballEndHeight;
    private float ballStartWidth;
    private float ballEndWidth;

    private float heightDifference;
    private float widthDifference;

    Bitmap ball, brick;
    Bitmap right, left;
    Rect r1, r2, r3, r4, r5, r6;
    Rect [] r = new Rect[1000];

    private int [] tilepos = new int[1000];
    private int i, j;
    private int h;
    private int ballNextPos;
    private int brickNextPos;
    private int score;
    private double time;
    private int intTime;
    private int status = 0;
    private int count;
    private int highScore;

    public GameLadder(Context context){
        //constructor with few default properties
        super(context);
        ct = context;
        getHolder().addCallback(this);
        setFocusable(true);

        // initialising images to bitmap.
        ball = BitmapFactory.decodeResource(getResources(), R.drawable.ballgreen);
        brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick);
        right = BitmapFactory.decodeResource(getResources(), R.drawable.right);
        left = BitmapFactory.decodeResource(getResources(), R.drawable.left);

        //initialising permanent high score, loading and saving to shared preferences.
        pref = ct.getSharedPreferences(getResources().getString(R.string.preffer), 0);
        editor = pref.edit();
        highScore = pref.getInt("highScore", 0);
        editor.putInt("highScore", highScore);
        editor.apply();

        i = 0;
        j = 1;
        h = 0;
        ballNextPos = 0;
        brickNextPos = 0;
        score = 0;
        time = 20.000;
        intTime = (int)time;
        count = 0;
        tilepos[0] = 0;
        //randomly generating array list of bricks positions
        for(i = 1; i < 1000; i++){
            if(tilepos[i - 1] == 0){
                side = rand.nextInt() / 3000;
                if(side % 2 == 0){
                    tilepos[i] = 1;
                }else{
                    tilepos[i] = -1;
                }
            }else if(tilepos[i - 1] == 1 || tilepos[i - 1] == -1){
                tilepos[i] = 0;
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //initialising thread and starting it when surface created for first time.
        mt = new MainThread(getHolder(), this);
        mt.setRunning(true);
        mt.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //stopping the thread when surface destroyed.
        boolean retry = true;
        int counter = 0;
        while (retry && counter<1000){
            counter++;
            try {
                mt.setRunning(false);
                mt.join();
                retry = false;
                mt = null;
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }

    }

    public void update(){
      //  h += 1;
        //decreasing available time
        if(time > 0 && status == 1) {
            time -= 0.1;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //getting x and y positions where display being touched.
        float dx = event.getX();
        float dy = event.getY();

        //when screen being touched
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            brickNextPos = tilepos[j];
            j++;
            if(status != -1) {//when game is not over
                status = 1; // set game to running
                if (dx < getWidth() / 2) { //for left half side of screen
                    if (ballNextPos != -1) {
                        //ball will jump to left side
                        ballNextPos -= 1;
                        // adding bonus time depending upon game score
                        if(score < 50){
                            time += 0.700;
                        }else if(score < 100){
                            time += 0.600;
                        }else if(score < 150){
                            time += 0.500;
                        }else if(score < 200){
                            time += 0.400;
                        }else if(score >= 200){
                            time += 0.300;
                        }
                    }
                } else { //for right half side of screen
                    if (ballNextPos != 1) {
                        //ball will jump to right side
                        ballNextPos += 1;
                        // adding bonus time depending upon game score
                        if(score < 50){
                            time += 0.700;
                        }else if(score < 100){
                            time += 0.600;
                        }else if(score < 150){
                            time += 0.500;
                        }else if(score < 200){
                            time += 0.400;
                        }else if(score >= 200){
                            time += 0.300;
                        }
                    }
                }
                h += getWidth() / 4;
            }else{ //when game is over.
                if(count == 1) { //need to tap screen 2 times
                    mt.setRunning(false); // stopping thread.
                    // resetting remaining properties to default.
                    score = -1;
                    i = 0;
                    j = 1;
                    h = 0;
                    ballNextPos = 0;
                    brickNextPos = 0;
                    time = 20.000;
                    tilepos[0] = 0;
                    status = 0; //changing game to new.
                    mt.setRunning(true); //starting thread again.
                    count = 0;
                }else{ //counting 2 taps.
                    count++;
                }
            }
            //comparing that ball has been landed to wrong stairs.
            if(ballNextPos != brickNextPos || time <= 0.0){
                //taking 0.4 sec pause to show player ball's wrong jump.
                try{Thread.sleep(400);}catch (Exception e){e.printStackTrace();}
                //resetting few properties to default.
                i = 0;
                j = 1;
                h = 0;
                ballNextPos = 0;
                brickNextPos = 0;
                status = -1; //set game to over.
                tilepos[0] = 0;
            }else{ //after correct move adding 1 to score
                score++;
                if(highScore < score){
                    highScore = score;
                    editor.putInt("highScore", highScore);
                    editor.apply();
                }
            }
        }

        return super.onTouchEvent(event);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        //setting background color to very dark blue.
        canvas.drawColor(Color.rgb(20,20,60));

        // initialising position and length-width properties.
        height = canvas.getHeight();
        width = canvas.getWidth();
        ballHeight = ball.getHeight();
        ballWidth = ball.getWidth();
        brickHeight = brick.getHeight();
        brickWidth = brick.getWidth();

        brickStartWidth = width/3;
        brickEndWidth = width*2/3;

        ballStartWidth = width*9/20;
        ballEndWidth = width*11/20;

        heightDifference = width/4;
        widthDifference = width/3;

        ballStartHeight = height - width/5;
        ballEndHeight = height - width/10;
        brickStartHeight = height - width/10;
        brickEndHeight = height;

        //initialising rectangle which is used to place and fit bitmaps on canvas.
        r1 = new Rect(0, 0, ballWidth, ballHeight);
        r2 = new Rect((int)ballStartWidth + (int)(ballNextPos * widthDifference), (int)ballStartHeight - (int)heightDifference, (int)ballEndWidth + (int)(ballNextPos * widthDifference), (int)ballEndHeight - (int)heightDifference );

        r3 = new Rect(0, 0, right.getWidth(), right.getHeight());
        r4 = new Rect(0, height - width/4, width/2, height);
        r5 = new Rect(width/2, height - width/4, width, height);

        r6 = new Rect(0, 0, brickWidth, brickHeight);

        //for finding position for stairs on canvas.
        i = 1;
        while(i < 1000) {
            r[i - 1] = new Rect((int) brickStartWidth + (int)(tilepos[i - 1] * widthDifference), (int) brickStartHeight + h - (int)(heightDifference) * i, (int) brickEndWidth + (int)(tilepos[i - 1] * widthDifference), (int) brickEndHeight + h - (int)(heightDifference) * i);
            i++;
        }
        //using paint to draw text on canvas
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(30);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        //draing ball
        canvas.drawBitmap(ball, r1, r2, null);

        canvas.drawText("Score : " + score, 20, 30, paint);
        intTime = (int)time;
        canvas.drawText("Time : " + intTime, width - 160, 30, paint);

        //set game to over, if remaining time equals to zero or score become 999.
        if(intTime == 0 || score == 999){
            status = -1;
        }
        canvas.drawText(""+highScore, width/2-20, 30, paint);

        paint.setTextSize(60);
        if(status == -1){ //drawing game over text and score when game is over.
            canvas.drawText("GAME OVER", width/2 - 160, height - ((width/4)* 2) - width/10 - 60, paint);
            int sx = 0;
            //finding optimal location to draw score.
            if(score / 100 > 0){
                sx = 60;
            }else if(score / 100 > 0){
                sx = 45;
            }else if(score / 10 > 0){
                sx = 30;
            }else if(score / 10 == 0){
                sx = 15;
            }
            canvas.drawText("" + score, width/2 - sx, height - ((width/4) * 2) - width/10, paint);
        }
        //drawing left and right buttons when game is not over.
        if(status != -1) {
            canvas.drawBitmap(right, r3, r4, null);
            canvas.drawBitmap(left, r3, r5, null);
        }
        //drawing 1000 bricks on canvas.
        for(i = 0; i < 1000; i++) {
            canvas.drawBitmap(brick, r6, r[i], null);
        }


    }
}
