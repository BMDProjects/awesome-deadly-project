/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * A view container where OpenGL ES graphics can be drawn on screen.
 * This view can also be used to capture touch events, such as a user
 * interacting with drawn objects.
 */
public class MyGLSurfaceView extends GLSurfaceView {

    private MyGLRenderer mRenderer;
    private float mDensity;

    public MyGLSurfaceView(Context context, MyGLRenderer renderer) {
        super(context);
        // Set the Renderer for drawing on the GLSurfaceView
        //mRenderer = new MyGLRenderer(context);
        //setRenderer(renderer);
       // mRenderer = renderer;
        // Create an OpenGL ES 2.0 context.
       /* setEGLContextClientVersion(2);
*/
        // Render the view only when there is a change in the drawing data
        // setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    public MyGLSurfaceView(Context context, AttributeSet attrs, int defStyle) {
        //super(context, attrs, defStyle);
        super(context, attrs);
    }

    public MyGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setRenderer(MyGLRenderer renderer, float density)
    {
        mRenderer = renderer;
        mDensity = density;
        super.setRenderer(renderer);
    }

    /*@Override
    public void setRenderer(MyGLRenderer renderer)
    {
        mRenderer = renderer;
        super.setRenderer(renderer);
    }*/

    private final float TOUCH_SCALE_FACTOR = 90.0f / 320;
    private float mPreviousX;
    private float mPreviousY;

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        //  float x = e.getX();
        //    float y = e.getY();




        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mRenderer.changeCameraState();
                mRenderer.touched = true;
                break;

/*                float dx = x - mPreviousX;
                float dy = y - mPreviousY;

               //reverse direction of rotation above the mid-line
                if (y > getHeight() / 2) {
                    dx = dx * -1 ;
                }

                // reverse direction of rotation to left of the mid-line
                if (x < getWidth() / 2) {
                    dy = dy * -1 ;
                }

                setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

               float sceneY = (y/1280)*-2.0f + 1.0f;
               float sceneX = -((x/720)*-2.0f + 0.5f);
                mRenderer.x = sceneX;
                mRenderer.y = sceneY;

                //requestRender();



                mRenderer.setAngle(
                        mRenderer.getAngle() +
                        ((dx + dy) * TOUCH_SCALE_FACTOR));  // = 180.0f / 320
                requestRender();
                setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);*/

            default:
                break;
        }



        // mPreviousX = x;
        // mPreviousY = y;
        return true;
    }
}