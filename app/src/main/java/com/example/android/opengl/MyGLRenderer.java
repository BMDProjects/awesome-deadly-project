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

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;
import android.content.Context;

import com.example.android.opengl.Text.TextManager;
import com.example.android.opengl.Text.TextObject;
import com.example.android.opengl.Text.riGraphicTools;

//import java.lang.*;
import java.lang.Math;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Random;


/**
 * Provides drawing instructions for a GLSurfaceView object. This class
 * must override the OpenGL ES drawing lifecycle methods:
 * <ul>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceCreated}</li>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onDrawFrame}</li>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceChanged}</li>
 * </ul>
 */






public class MyGLRenderer implements GLSurfaceView.Renderer {
    //all text
    public static float vertices[];
    public static short indices[];
    public static float uvs[];
    public FloatBuffer vertexBuffer;
    public ShortBuffer drawListBuffer;
    public FloatBuffer uvBuffer;
    //public Sprite sprite;
    public TextManager tm;
    float 	ssu = 1.0f;
    Context mContext;


    private static final String TAG = "MyGLRenderer";
    private Cube   mCube;
    private Cube   mCube2;
    private Cube   mCube3;

    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mRotationMatrix= new float[16];

    private final float[] centerOfCubeModelSpace = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
    private final float[] centerOfCubeEyeSpace = new float[4];
    private final float[] centerOfCubeWorldSpace = new float[4];

    //double theta = 0.0;
    float theta = 0.0f;
    float speed = 0.01f;

    float eyeX = 0.0f;
    float eyeY = 0.0f;
    float eyeZ = 0.0f;

    // We are looking toward the distance
    final float lookX = 0.0f;
    final float lookY = 0.0f;
    final float lookZ = 0.0f;

    // Set our up vector. This is where our head would be pointing were we holding the camera.
    final float upX = 0.0f;
    final float upY = 1.0f;
    final float upZ = 0.0f;

    //private float[] mTempMatrix = new float[16];

    private float mAngle;
    float x = 0.0f;
    float y = 0.0f;

    MyGLRenderer(Context context) {
        super();
        mContext = context;
    }



    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        // Create the image information
        SetupImage();
        // Create our texts
        SetupText();

        // Create the shaders, images
        int vertexShader = riGraphicTools.loadShader(GLES20.GL_VERTEX_SHADER, riGraphicTools.vs_Image);
        int fragmentShader = riGraphicTools.loadShader(GLES20.GL_FRAGMENT_SHADER, riGraphicTools.fs_Image);

        GLES20.glAttachShader(riGraphicTools.sp_Image, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(riGraphicTools.sp_Image, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(riGraphicTools.sp_Image);                  // creates OpenGL ES program executables

        int vshadert = riGraphicTools.loadShader(GLES20.GL_VERTEX_SHADER,
                riGraphicTools.vs_Text);
        int fshadert = riGraphicTools.loadShader(GLES20.GL_FRAGMENT_SHADER,
                riGraphicTools.fs_Text);

        riGraphicTools.sp_Text = GLES20.glCreateProgram();
        GLES20.glAttachShader(riGraphicTools.sp_Text, vshadert);
        GLES20.glAttachShader(riGraphicTools.sp_Text, fshadert);
        GLES20.glLinkProgram(riGraphicTools.sp_Text);

        GLES20.glUseProgram(riGraphicTools.sp_Image);

        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        mCube   = new Cube();
        mCube2 = new Cube();
        mCube3 = new Cube();
    }

    public void SetupText()
    {
        // Create our text manager
        tm = new TextManager();

        // Tell our text manager to use index 1 of textures loaded
        tm.setTextureID(1);

        // Pass the uniform scale
        tm.setUniformscale(ssu);

        // Create our new textobject
        TextObject txt = new TextObject("hello world", 300f, 300f);

        // Add it to our manager
        tm.addText(txt);

        // Prepare the text for rendering
        tm.PrepareDraw();
    }

    public void SetupImage()
    {
        // We will use a randomizer for randomizing the textures from texture atlas.
        // This is strictly optional as it only effects the output of our app,
        // Not the actual knowledge.
        Random rnd = new Random();

        // 30 imageobjects times 4 vertices times (u and v)
        uvs = new float[30*4*2];

        // We will make 30 randomly textures objects
        for(int i=0; i<30; i++)
        {
            int random_u_offset = rnd.nextInt(2);
            int random_v_offset = rnd.nextInt(2);

            // Adding the UV's using the offsets
            uvs[(i*8) + 0] = random_u_offset * 0.5f;
            uvs[(i*8) + 1] = random_v_offset * 0.5f;
            uvs[(i*8) + 2] = random_u_offset * 0.5f;
            uvs[(i*8) + 3] = (random_v_offset+1) * 0.5f;
            uvs[(i*8) + 4] = (random_u_offset+1) * 0.5f;
            uvs[(i*8) + 5] = (random_v_offset+1) * 0.5f;
            uvs[(i*8) + 6] = (random_u_offset+1) * 0.5f;
            uvs[(i*8) + 7] = random_v_offset * 0.5f;
        }

        // The texture buffer
        ByteBuffer bb = ByteBuffer.allocateDirect(uvs.length * 4);
        bb.order(ByteOrder.nativeOrder());
        uvBuffer = bb.asFloatBuffer();
        uvBuffer.put(uvs);
        uvBuffer.position(0);

        // Generate Textures, if more needed, alter these numbers.
        int[] texturenames = new int[2];
        GLES20.glGenTextures(2, texturenames, 0);

        // Retrieve our image from resources.
        int id = mContext.getResources().getIdentifier("drawable/font", null, mContext.getPackageName());

        // Temporary create a bitmap
        Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), id);

        // Bind texture to texturename
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturenames[0]);

        // Set filtering
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        // Load the bitmap into the bound texture.
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);

        // We are done using the bitmap so we should recycle it.
        bmp.recycle();

        // Again for the text texture
        id = mContext.getResources().getIdentifier("drawable/font", null, mContext.getPackageName());
        bmp = BitmapFactory.decodeResource(mContext.getResources(), id);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + 1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturenames[1]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
        bmp.recycle();
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        float[] scratch = new float[16];
        float[] sqScratch = new float[16];

        // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        theta += speed;
        eyeX = (float)(3.0f * Math.cos(theta)); //+ circle.cx;
        eyeZ = (float)(3.0f * Math.sin(theta)); //+ circle.cy;
        //eyeY = (float)(3.0f * Math.sin(theta));
       // eyeZ = 6.0f;
       // eyeX = 6.0f;
        eyeY = 6.0f;
        // Set the camera position (View matrix)
        //Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, centerOfCubeEyeSpace[0], centerOfCubeEyeSpace[1], centerOfCubeEyeSpace[2], 0f, 1.0f, 0.0f);

        // Use the following code to generate constant rotation.
        // Leave this code out when using TouchEvents.

        //float sqAngle = 30.0f;
          //USE THIS INSTEAD FOR ANGLE ------------------------------------------------------
        long time = SystemClock.uptimeMillis() % 10000L;
        float sqAngle = (360.0f / 10000.0f) * ((int) time);

        // GLES20.glEnable(GLES20.GL_CULL_FACE);
        //shows culling perfectly. culling is removing the backfaces of triangles

        Matrix.setIdentityM(scratch, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, scratch, 0, mViewMatrix, 0);
        Matrix.multiplyMM(scratch, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        if(tm!=null) tm.Draw(scratch);

        //Initiialize and Translate
        Matrix.setIdentityM(sqScratch, 0);
        Matrix.setIdentityM(mMVPMatrix, 0);
       // Matrix.translateM(sqScratch,  0, x, y, 0.0f);

        //Matrix.setRotateM(mRotationMatrix, 0, mAngle, 0, 0, 1.0f);

        //M

        // Calculate the projection and view transformation
       // Matrix.multiplyMM(mMVPMatrix, 0, sqScratch, 0, mViewMatrix, 0);
      //  Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
      //  Matrix.setRotateM(mRotationMatrix, 0, sqAngle, 0.25f, 0, 1.0f);
     //   Matrix.multiplyMM(sqScratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);

        Matrix.translateM(sqScratch, 0, 0.0f, 0.0f, -5.0f);
        Matrix.rotateM(sqScratch, 0, sqAngle, 0.0f, 1.0f, 0.0f);
        Matrix.translateM(sqScratch, 0, 0.0f, 0.0f, 2.0f);
        Matrix.multiplyMV(centerOfCubeWorldSpace , 0, sqScratch, 0, centerOfCubeModelSpace , 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, sqScratch, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
        centerOfCubeWorldSpace[0] += 0.01f;

        Matrix.multiplyMV(centerOfCubeEyeSpace , 0, mViewMatrix, 0, centerOfCubeWorldSpace , 0);
        mCube.draw3(mMVPMatrix, 1.0f);
        //mCube.draw3(sqScratch, 1.0f);
        //  Matrix.multiplyMV(mLightPosInWorldSpace, 0, sqScratch, 0, mLightPosInModelSpace, 0);
        // Matrix.multiplyMV(mLightPosInEyeSpace, 0, mViewMatrix, 0, mLightPosInWorldSpace, 0);

        //cube 2
        Matrix.setIdentityM(sqScratch, 0);
        Matrix.translateM(sqScratch,  0, 0.0f, 0.0f, 0.0f);
        Matrix.multiplyMM(mMVPMatrix, 0, sqScratch, 0, mViewMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        mCube2.draw3(mMVPMatrix, 50.0f);


        //cube 3
        Matrix.setIdentityM(sqScratch, 0);
        Matrix.setIdentityM(mMVPMatrix, 0);
        Matrix.translateM(sqScratch,  0, 0.0f, 0.0f, 0.0f);
        Matrix.multiplyMM(mMVPMatrix, 0, sqScratch, 0, mViewMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        Matrix.setRotateM(mRotationMatrix, 0, sqAngle, 0.5f, 0.3f, 1.0f);
        //Matrix.multiplyMM(mMVPMatrix, 0, mMVPMatrix, 0, mRotationMatrix, 0);
        Matrix.multiplyMM(sqScratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
        mCube3.draw3(sqScratch, 0.5f);
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        // Adjust the viewport based on geometry changes,
        // such as screen rotation
        GLES20.glViewport(0, 0, width, height);

        final float ratio = (float) width / height;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;
        final float far = 10.0f;

       // Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
        Matrix.frustumM(mProjectionMatrix, 0, left, right, -1, 1, 1, 100);
    }

    /**
     * Utility method for compiling a OpenGL shader.
     *
     * <p><strong>Note:</strong> When developing shaders, use the checkGlError()
     * method to debug shader coding errors.</p>
     *
     * @param type - Vertex or fragment shader type.
     * @param shaderCode - String containing the shader code.
     * @return - Returns an id for the shader.
     */
    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    /**
     * Utility method for debugging OpenGL calls. Provide the name of the call
     * just after making it:
     *
     * <pre>
     * mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
     * MyGLRenderer.checkGlError("glGetUniformLocation");</pre>
     *
     * If the operation is not successful, the check throws an error.
     *
     * @param glOperation - Name of the OpenGL call to check.
     */
    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }

    /**
     * Returns the rotation angle of the triangle shape (mTriangle).
     *
     * @return - A float representing the rotation angle.
     */
    public float getAngle() {
        return mAngle;
    }

    /**
     * Sets the rotation angle of the triangle shape (mTriangle).
     */
    public void setAngle(float angle) {
        mAngle = angle;
    }

}