package com.example.surfaceview;

import static android.graphics.Color.BLACK;

import android.content.Context;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    MySurfaceView v;

    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        RelativeLayout layout = findViewById(R.id.main);
        v = new MySurfaceView(this);
        RelativeLayout.LayoutParams surfaceParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );
        layout.addView(v, 0, surfaceParams); // index 0 to place it behind the button



    }
}

class MySurfaceView extends SurfaceView implements Runnable{
    private int[][] pixelMatrix = {
            {0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}

    };
    private int currentColumn = 0;
    private int numRows = pixelMatrix.length; // height of image
    private int numCols = pixelMatrix[0].length; // width of image
    private Paint paint = new Paint();
    Thread t = null;
    SurfaceHolder holder;
    boolean isItOk = false;
    int screenWidth, screenHeight;

    public MySurfaceView(Context context) {
        super(context);
        holder = getHolder();
        //picture = BitmapFactory.decodeResource(getResources(), R.drawable.pixel_house);
        resume();
        setZOrderOnTop(false);
    }
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenWidth = w;
        screenHeight = h;
    }

    @Override
    public void run() {

        Canvas canvas;
        while (isItOk){
            if (!holder.getSurface().isValid()) {
                continue;
            }
            canvas = null;
            try {
                canvas = holder.lockCanvas(null);
                synchronized (holder) {
                    canvas.drawColor(BLACK);
                    int pixelHeight = screenHeight / numRows;
                    int pixelWidth = screenWidth / 25; // narrow light column
                    int xStart = (screenWidth - pixelWidth) / 2;

                    for (int row = 0; row < numRows; row++) {
                        if (pixelMatrix[row][currentColumn] == 1) {
                            paint.setColor(Color.WHITE);
                        } else {
                            paint.setColor(Color.rgb(000,000,000));
                        }

                        canvas.drawRect(
                                xStart,
                                row * pixelHeight,
                                xStart + pixelWidth,
                                (row + 1) * pixelHeight,
                                paint
                        );
                    }

                    // Go to the next column
                    currentColumn = (currentColumn + 1) % numCols;
                }
            }
            finally{
                    if (canvas != null) {
                        holder.unlockCanvasAndPost(canvas);
                    }
            }
            try {
                Thread.sleep(33);
            } catch (InterruptedException e) {
                    e.printStackTrace();
            }
        }

    }
    public void resume(){
        isItOk = true;
        t = new Thread(this);
        t.start();
    }
    public void pause(){
        isItOk = false;
        while(isItOk){
            try {
                t.join();
            } catch (InterruptedException c){
                c.printStackTrace();
            }
            break;
        }
        t = null;
    }
}