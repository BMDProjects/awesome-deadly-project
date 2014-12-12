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

        final Button camera = (Button) findViewById(R.id.Camera);
        camera.setOnClickListener(new View.OnClickListener() {
              public void onClick(View v) {
                  Camera();
              }
          });

        final Button control = (Button) findViewById(R.id.Control);
        control.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                changeControl();
            }
        });

        final Button decrease = (Button) findViewById(R.id.decrease);
        decrease.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                decrease();
            }
        });

        final Button increase = (Button) findViewById(R.id.increase);
        increase.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                increase();
            }
        });

        final Button dolphin = (Button) findViewById(R.id.dolphin);
        dolphin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dolphinChange();
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
        if (MyGLRenderer.control == true) {
            MyGLRenderer.camera.vectorReached = false;
            MyGLRenderer.control = false;
            MyGLRenderer.camera.state = 0;
        }
        else {
            MyGLRenderer.camera.vectorReached = false;
            MyGLRenderer.control = true;
            MyGLRenderer.camera.state = 3;
        }
    }

    public void increase() {
        if(MyGLRenderer.getDolphinSpeed() < 10.0f) mRenderer.setDolphinSpeed(MyGLRenderer.getDolphinSpeed() + 0.1f);
    }

    public void decrease() {
        if(!MyGLRenderer.approxEqual(0.0, MyGLRenderer.getDolphinSpeed(), 0.05f)) {
            MyGLRenderer.setDolphinSpeed(MyGLRenderer.getDolphinSpeed() - 0.1f);
        }
        else MyGLRenderer.setDolphinSpeed(0.0f);
    }

    public void dolphinChange() {
        if (MyGLRenderer.dolphinFollow == true) {
            MyGLRenderer.dolphinFollow = false;
        }
        else {
            MyGLRenderer.dolphinFollow = true;
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