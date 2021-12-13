package com.ndds.freedomclouds;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;

import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.ArrayList;

public class OpenGLScreen extends GLSurfaceView {
    CustomRenderer customRenderer = null;
    public MainActivity activity;
    public OpenGLScreen(Context context, AttributeSet attrs) {

        super(context,attrs);
        setEGLContextClientVersion(2);

    }
    @SuppressWarnings("unchecked")
    public void initRenderer(Context context, Object ... rendererParams){
        boolean isGiftRenderer = getTag().equals("2");
        customRenderer = isGiftRenderer ? new GiftRenderer(context,this) : new CustomRenderer(context,this,(ArrayList<Bitmap>) rendererParams[0], (String[]) rendererParams[1]);
        setEGLConfigChooser(true);
        setEGLConfigChooser( 8, 8, 8, 8, 16, 0 );

        setRenderer(customRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
    public boolean autoRotate = false;
    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float previousX;
    private float previousY;
    private float downY;
    private float downX;
    private Handler handler = new Handler();
    public void glow(){
        if(getTag().equals("2"))
            return;
        customRenderer.blendFactor = 0.0f;
        customRenderer.doGlow = 1;
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        requestRender();
    }
    Handler idleHandler;
    ObjectAnimator idleAnimator;
    public void setIdleHandler(Handler handler, ObjectAnimator objectAnimator){
        idleHandler = handler;
        idleAnimator = objectAnimator;
    }
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.autoRotate = false;
                this.downY = y;
                this.downX = x;
                idleAnimator.end();
                idleHandler.removeCallbacksAndMessages(null);
                handler.removeCallbacksAndMessages(null);
                setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
                requestRender();
                break;
            case MotionEvent.ACTION_UP:
                this.idleHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(!idleAnimator.isStarted())
                            idleAnimator.start();
                    }
                },5000);
                this.handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(OpenGLScreen.this.customRenderer.quickSpinAngle > 0 || OpenGLScreen.this.customRenderer.doGlow != 0)
                            return;
                        OpenGLScreen.this.autoRotate = true;
                        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
                        requestRender();
                    }
                },1000);
                break;
            case MotionEvent.ACTION_MOVE:

                float dx = x - previousX;
                float dy = y - previousY;
//                float factorX = 1.0f;
//                float factorY = -1.0f;
                // reverse direction of rotation above the mid-line
//                if (y > getHeight() / 2) {
//                    dx = dx * -1 ;
//                }
//                if(Math.abs(dx) < 10)
//                    return true;
                // reverse direction of rotation to left of the mid-line
//                if (y > getHeight() / 2) {
//                    factorY = 1.0f;
//                    //dy = dy * -1 ;
//                }
//                if (x < getWidth() / 2) {
//                    factorX = -1.0f;
//                    //dy = dy * -1 ;
//                }
                if (Math.abs(y-downY) > 50) {


                    customRenderer.setAngleY(
                            customRenderer.getAngleY() +
                                    ((dy) * TOUCH_SCALE_FACTOR));
                }else{
                    customRenderer.setAngleX(
                            customRenderer.getAngleX() +
                                    ((dx) * TOUCH_SCALE_FACTOR));

                }


//                customRenderer.setAngle(
//                        customRenderer.getAngle() +
//                                ((dx + dy) * TOUCH_SCALE_FACTOR));
                requestRender();
                break;
        }

        previousX = x;
        previousY = y;
        return true;

    }
}
