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

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

public class OpenGLES20Activity extends Activity {

    private MyGLSurfaceView mGLView;
    private MyGLRenderer mRenderer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity
        //mGLView = new MyGLSurfaceView(this);
        mGLView = (MyGLSurfaceView)findViewById(R.id.gl_surface_view);
       // setContentView(R.layout.main);
        //setContentView(mGLView);

        final Button button = (Button) findViewById(R.id.button_set_min_filter);
        button.setOnClickListener(new View.OnClickListener() {
              public void onClick(View v) {
                  Camera();
              }
          });

        final Button button2 = (Button) findViewById(R.id.button_set_mag_filter);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                changeControl();
            }
        });

        mGLView.setEGLContextClientVersion(2);

        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new MyGLRenderer(this);
       // mGLView.setRenderer(mRenderer, displayMetrics.density);
        mGLView.setRenderer(mRenderer, displayMetrics.density);
    }

    public void Camera() {
        mRenderer.camera.changeCameraState();
    }

    public void changeControl() {
        if (mRenderer.camera.control == true) mRenderer.camera.control = false;
        else {
            mRenderer.camera.control = true;
            mRenderer.camera.state = 3;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // The following call pauses the rendering thread.
        // If your OpenGL application is memory intensive,
        // you should consider de-allocating objects that
        // consume significant memory here.
        mGLView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // The following call resumes a paused rendering thread.
        // If you de-allocated graphic objects for onPause()
        // this is a good place to re-allocate them.
        mGLView.onResume();
    }
}