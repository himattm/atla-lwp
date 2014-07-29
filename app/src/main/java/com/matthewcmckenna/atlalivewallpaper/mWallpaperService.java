package com.matthewcmckenna.atlalivewallpaper;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;


/**
 * Created by Matt-Alien on 7/28/2014.
 */
public class mWallpaperService extends WallpaperService {

    public void onCreate() {
        super.onCreate();
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public Engine onCreateEngine() {
        return new MyWallpaperEngine();
    }

    class MyWallpaperEngine extends Engine {

        private final Handler handler = new Handler();
        private final Runnable drawRunner = new Runnable() {
            @Override
            public void run() {
                draw();
            }
        };

        private boolean visible = true;

        public Bitmap aang, avatarState;
        public Paint avatarStatePaint;
        public boolean goingUp = true;
        public boolean alwaysOn = false;
        public boolean solid;
        public int avatarStateAlpha = 0;
        SharedPreferences prefs;

        MyWallpaperEngine() {
            // Create Paint to set alpha
            avatarStatePaint = new Paint();

            // Get SharedPreferences
            prefs = PreferenceManager.getDefaultSharedPreferences(mWallpaperService.this);

            // Bitmap Options
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;

            // load the bitmaps
            aang = BitmapFactory.decodeResource(getResources(), R.drawable.trans_aang, options);
            avatarState = BitmapFactory.decodeResource(getResources(), R.drawable.avatar_state, options);
        }


        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            this.visible = visible;
            // if screen wallpaper is visible then draw the image otherwise do not draw
            if (visible) {
                handler.post(drawRunner);
            } else {
                handler.removeCallbacks(drawRunner);
            }
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            this.visible = false;
            handler.removeCallbacks(drawRunner);
        }

        public void onOffsetsChanged(float xOffset, float yOffset, float xStep, float yStep, int xPixels, int yPixels) {
            draw();
        }

        void draw() {
            final SurfaceHolder holder = getSurfaceHolder();

            Canvas c = null;
            try {
                c = holder.lockCanvas();
                // clear the canvas

                // Parse The Color From the Prefernces
                c.drawColor(Color.parseColor(prefs.getString("colorListPref", "#4066b1")));


                if (c != null) {
                    // draw the background image
                    c.drawBitmap(aang, (c.getWidth() - aang.getWidth()) / 2, (c.getHeight() - aang.getHeight()) / 2, null);


                    // Check if Always On or Off
                    if (prefs.getString("fadeSpeed", "").equals("Always On")) {
                        alwaysOn = true; // to set alpha to 255
                        avatarStateAlpha = 255;
                        solid = true; // to prevent fade
                    } else if (prefs.getString("fadeSpeed", "").equals("Always Off")) {
                        alwaysOn = false; // to set alpha to 0
                        avatarStateAlpha = 0;
                        solid = true; // to prevent fade
                    }else{
                        solid = false; // not solid so fading below
                    }


                    if (!solid) {

                        // Checks to make sure alpha is fading the correct way
                        if (avatarStateAlpha >= 255) {
                            avatarStateAlpha = 255; // Catches if it goes over and prevent blinking
                            goingUp = false;
                        }
                        if (avatarStateAlpha <= 0) {
                            avatarStateAlpha = 0;
                            goingUp = true;
                        }


                        if (goingUp) {
                            avatarStateAlpha += Integer.parseInt(prefs.getString("fadeSpeed", "1"));
                        } else {
                            avatarStateAlpha -= Integer.parseInt(prefs.getString("fadeSpeed", "1"));
                        }
                    }

                    avatarStatePaint.setAlpha(avatarStateAlpha);
                    c.drawBitmap(avatarState, (c.getWidth() - avatarState.getWidth()) / 2, (c.getHeight() - avatarState.getHeight()) / 2, avatarStatePaint);

                    // get the width of canvas
                    int width = c.getWidth();

                }
            } finally {
                if (c != null)
                    holder.unlockCanvasAndPost(c);
            }

            handler.removeCallbacks(drawRunner);
            if (visible) {
                handler.postDelayed(drawRunner, 10); // delay 10 mileseconds
            }

        }
    }
}
