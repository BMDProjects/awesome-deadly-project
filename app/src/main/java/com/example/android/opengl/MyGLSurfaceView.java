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

        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    public MyGLSurfaceView(Context context, AttributeSet attrs, int defStyle) {
        //super(context, attrs, defStyle);
        super(context, attrs);
    }

    public MyGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setRenderer(MyGLRenderer renderer, float density) {
        mRenderer = renderer;
        mDensity = density;
        super.setRenderer(renderer);
    }


    private final float TOUCH_SCALE_FACTOR = 90.0f / 320;
    private float mPreviousX;
    private float mPreviousY;

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        float x = e.getX();
        float y = e.getY();

        if (MyGLRenderer.control) {
            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                float xCoord = (float) (2.0f * (x / getWidth()) - 1.0);
                float yCoord = (float) (2.0f * (y / getHeight()) - 1.0);

                if (yCoord >= -1 && yCoord <= -0.5 && xCoord <= 0.5 && xCoord >= -0.5f) {
                    mRenderer.rotatingUp = true;
                    return true;
                } else mRenderer.rotatingUp = false;

                if (yCoord >= 0.3f && yCoord <= 0.8f && xCoord <= 0.5f && xCoord >= -0.5f) {
                    mRenderer.rotatingDown = true;
                    return true;
                } else mRenderer.rotatingDown = false;

                if (yCoord >= -0.5f && yCoord <= 0.5f && xCoord <= -0.5f && xCoord >= -1.0f) {
                    mRenderer.rotatingLeft = true;
                    return true;
                } else mRenderer.rotatingLeft = false;

                if (yCoord >= -0.5f && yCoord <= 0.5f && xCoord <= 1.0f && xCoord >= 0.5f) {
                    mRenderer.rotatingRight = true;
                    return true;
                } else mRenderer.rotatingRight = false;

            } else if (e.getAction() == MotionEvent.ACTION_UP) {
                mRenderer.rotatingUp = false;
                mRenderer.rotatingDown = false;
                mRenderer.rotatingLeft = false;
                mRenderer.rotatingRight = false;
                return true;
            }
        }
        return super.onTouchEvent(e);
    }
}