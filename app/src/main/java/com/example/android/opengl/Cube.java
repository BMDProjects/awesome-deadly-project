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

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Random;

/**
 * A two-dimensional square for use as a drawn object in OpenGL ES 2.0.
 */
public class Cube {

    private final String vertexShaderCode =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;" +
<<<<<<< HEAD
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    // the matrix must be included as a modifier of gl_Position
                    // Note that the uMVPMatrix factor *must be first* in order
                    // for the matrix multiplication product to be correct.
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    private final String vertexShaderCode2 =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;" +
            "attribute vec4 vPosition;" +
            "attribute vec4 aColor;       " +

            "varying vec4 vColor;     " +
            "void main() {" +
            "   vColor = aColor;    " +
=======
            "attribute vec4 vPosition;" +
            "void main() {" +
>>>>>>> ab207015e95c41478b673050146348355986fc1e
            // The matrix must be included as a modifier of gl_Position.
            // Note that the uMVPMatrix factor *must be first* in order
            // for the matrix multiplication product to be correct.
            "  gl_Position = uMVPMatrix * vPosition;" +
            "}";

<<<<<<< HEAD
    private final String fragmentShaderCode2 =
            "precision mediump float;" +
            "varying vec4 vColor;    " +
=======
    private final String fragmentShaderCode =
            "precision mediump float;" +
            "uniform vec4 vColor;" +
>>>>>>> ab207015e95c41478b673050146348355986fc1e
            "void main() {" +
            "  gl_FragColor = vColor;" +
            "}";

<<<<<<< HEAD


    private final FloatBuffer vertexBuffer;
    private final FloatBuffer colorBuffer;
    private final ShortBuffer drawListBuffer;
    private final FloatBuffer vertexInfoBuffer;
    private final int mProgram;
    private final int mProgram2;
    private int mPositionHandle;
    private int mColorHandle;
    private int mColorHandle2;
=======
    private final FloatBuffer vertexBuffer;
    private final ShortBuffer drawListBuffer;
    private final int mProgram;
    private int mPositionHandle;
    private int mColorHandle;
>>>>>>> ab207015e95c41478b673050146348355986fc1e
    private int mMVPMatrixHandle;

    public int delayer = 20;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static float squareCoords[] = {
            -0.5f, 0.5f, 0.5f,   // top leftF 0
            -0.5f, -0.5f, 0.5f,   // bottom leftF 1
            0.5f, -0.5f, 0.5f,   // bottom rightF 2
            0.5f, 0.5f, 0.5f,   //top rightF 3
            - 0.5f, 0.5f, -0.5f,   // top leftB 4
            -0.5f, -0.5f, -0.5f,   // bottom leftB 5
            0.5f, -0.5f, -0.5f,   // bottom rightB 6
            0.5f, 0.5f, -0.5f    // top rightB 7
    };

<<<<<<< HEAD
    //nice multicoloured
    /*static float cubeData[] = {
            -0.5f, 0.5f, 0.5f,   // top leftF 0
            1.0f, 0.0f, 0.0f, 1.0f,
            -0.5f, -0.5f, 0.5f,   // bottom leftF 1
            1.0f, 1.0f, 0.0f, 1.0f,
            0.5f, -0.5f, 0.5f,   // bottom rightF 2
            1.0f, 0.0f, 1.0f, 1.0f,
            0.5f, 0.5f, 0.5f,   //top rightF 3
            0.0f, 1.0f, 0.0f, 1.0f,
            - 0.5f, 0.5f, -0.5f,   // top leftB 4
            0.0f, 0.0f, 1.0f, 1.0f,
            -0.5f, -0.5f, -0.5f,   // bottom leftB 5
            1.0f, 0.0f, 1.0f, 1.0f,
            0.5f, -0.5f, -0.5f,   // bottom rightB 6
            0.0f, 1.0f, 1.0f, 1.0f,
            0.5f, 0.5f, -0.5f    // top rightB 7
    };*/

    //attempt at 8 solid different colors
    static float cubeData[] = {
            -0.5f, 0.5f, 0.5f,   // top leftF 0
            1.0f, 0.0f, 0.0f, 1.0f,
            -0.5f, -0.5f, 0.5f,   // bottom leftF 1
            1.0f, 0.0f, 0.0f, 1.0f,
            0.5f, -0.5f, 0.5f,   // bottom rightF 2
            1.0f, 0.0f, 0.0f, 1.0f,
            0.5f, 0.5f, 0.5f,   //top rightF 3
            1.0f, 0.0f, 0.0f, 1.0f,
            - 0.5f, 0.5f, -0.5f,   // top leftB 4
            0.0f, 0.0f, 1.0f, 1.0f,
            -0.5f, -0.5f, -0.5f,   // bottom leftB 5
            0.0f, 0.0f, 1.0f, 1.0f,
            0.5f, -0.5f, -0.5f,   // bottom rightB 6
            0.0f, 0.0f, 1.0f, 1.0f,
            0.5f, 0.5f, -0.5f,    // top rightB 7
            0.0f, 0.0f, 1.0f, 1.0f,
    };

=======
>>>>>>> ab207015e95c41478b673050146348355986fc1e
    private final short drawOrder[] = { 0, 1, 2,
                                        0, 2, 3,
                                        0, 4, 3,
                                        7, 4, 3,
                                        1, 5, 2,
                                        5, 2, 6,
                                        0, 1, 5,
                                        0, 4, 5,
                                        4, 5, 6,
                                        4, 6, 7,
                                        3, 6, 2,
                                        3, 7, 2,}; // order to draw vertices

<<<<<<< HEAD
    static float colors[] = {
            0.5f, 0.5f, 0.5f, 1.0f,
            0.5f, 0.5f, 0.5f, 1.0f,
            0.5f, 0.5f, 0.5f, 1.0f,
            0.5f, 0.5f, 0.5f, 1.0f,
            0.5f, 0.5f, 0.5f, 1.0f,
            0.5f, 0.5f, 0.5f, 1.0f,
            0.5f, 0.5f, 0.5f, 1.0f,
            0.5f, 0.5f, 0.5f, 1.0f,
    };

    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    public float[] color = {0.0f, 1.0f, 1.0f, 0.0f};

=======
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    public float[] color = {0.0f, 1.0f, 1.0f, 0.0f,
                            0.0f, 0.0f, 1.0f, 0.0f,
                            0.0f, 1.0f, 0.0f, 1.0f};


    /**
     * Sets up the drawing object data for use in an OpenGL ES context.
     */
>>>>>>> ab207015e95c41478b673050146348355986fc1e
    public Cube() {
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
        // (# of coordinate values * 4 bytes per float)
                squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

<<<<<<< HEAD
        ByteBuffer colBuf = ByteBuffer.allocateDirect( colors.length * 4);
        colBuf.order(ByteOrder.nativeOrder());
        colorBuffer = colBuf.asFloatBuffer();
        colorBuffer.put(colors);
        colorBuffer.position(0);

        ByteBuffer Buf = ByteBuffer.allocateDirect(cubeData.length * 4);
        Buf.order(ByteOrder.nativeOrder());
        vertexInfoBuffer = Buf.asFloatBuffer();
        vertexInfoBuffer.put(cubeData);
        vertexInfoBuffer.position(0);


        mProgram = GLES20.glCreateProgram();
        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables

        mProgram2 = GLES20.glCreateProgram();
        int vertexShader2 = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode2); //change to 2 -----------------------------------------------------


        //change these to 2 or back


        int fragmentShader2 = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode2);  //change to 2 -------------------------------------------------
        GLES20.glBindAttribLocation(mProgram2, 0, "vPosition");
        GLES20.glBindAttribLocation(mProgram2, 1, "aColor"); //binding doesnt affect if it works or not
        GLES20.glAttachShader(mProgram2, vertexShader2);
        GLES20.glAttachShader(mProgram2, fragmentShader2);
        GLES20.glLinkProgram(mProgram2);
=======
        // prepare shaders and OpenGL program
        int vertexShader = MyGLRenderer.loadShader(
                GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(
                GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables
>>>>>>> ab207015e95c41478b673050146348355986fc1e
    }



    public void colorChange() {
        delayer++;
<<<<<<< HEAD
        if (delayer >= 30) {
=======
        if (delayer == 30) {
>>>>>>> ab207015e95c41478b673050146348355986fc1e
            Random random = new Random();
            for (int i = 0; i < 4; i++) {
                color[i] = random.nextFloat();
            }
            delayer = 0;
        }
    }
    /**
     * Encapsulates the OpenGL ES instructions for drawing this shape.
     *
     * @param mvpMatrix - The Model View Project matrix in which to draw
     * this shape.
     */
<<<<<<< HEAD
    public void drawThatWorks(float[] mvpMatrix) {
    //dont mess with this until sure it works. Otherwise bugs that take infinite time to fix.
=======
    public void draw(float[] mvpMatrix) {
>>>>>>> ab207015e95c41478b673050146348355986fc1e
        // Add program to OpenGL environment
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(
                mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
<<<<<<< HEAD
=======

        // Set color for drawing the triangle
>>>>>>> ab207015e95c41478b673050146348355986fc1e
        //colorChange();
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        MyGLRenderer.checkGlError("glGetUniformLocation");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        MyGLRenderer.checkGlError("glUniformMatrix4fv");

        // Draw the square
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
<<<<<<< HEAD
    }

    public void draw1(float[] mvpMatrix) {
        // Add program to OpenGL environment
        GLES20.glUseProgram(mProgram2);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(
                mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        //mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");


        // Set color for drawing the triangle
        //colorChange();
        //GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        mColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
        colorBuffer.position(0);
        GLES20.glVertexAttribPointer(mColorHandle, 4, GLES20.GL_FLOAT, false, 0, colorBuffer);
        GLES20.glEnableVertexAttribArray(mColorHandle);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        MyGLRenderer.checkGlError("glGetUniformLocation");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        MyGLRenderer.checkGlError("glUniformMatrix4fv");

        // Draw the square
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, drawOrder.length,
=======

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        MyGLRenderer.checkGlError("glGetUniformLocation");


        float[] transMatrix = new float[16];

        Matrix.setIdentityM(transMatrix,0);
        Matrix.translateM(transMatrix,0,5.0f,0,0);
        Matrix.multiplyMM(transMatrix, 0, mvpMatrix, 0, transMatrix, 0);



        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, transMatrix, 0);
        MyGLRenderer.checkGlError("glUniformMatrix4fv");

        // Draw the square
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length,
>>>>>>> ab207015e95c41478b673050146348355986fc1e
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
<<<<<<< HEAD
        GLES20.glDisableVertexAttribArray(mColorHandle);
    }

    private final int mStrideBytes = 7 * 4;
    private final int mPositionOffset = 0;
    private final int mPositionDataSize = 3;
    private final int mColorOffset = 3;
    private final int mColorDataSize = 4;

    public void draw2(float[] mvpMatrix) {
        GLES20.glUseProgram(mProgram2);

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram2, "uMVPMatrix");
        mPositionHandle = GLES20.glGetAttribLocation(mProgram2, "vPosition");
        mColorHandle = GLES20.glGetAttribLocation(mProgram2, "aColor");

        vertexInfoBuffer.position(mPositionOffset);
        GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false,
                mStrideBytes, vertexInfoBuffer);

        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Pass in the color information
        vertexInfoBuffer.position(mColorOffset);
        GLES20.glVertexAttribPointer(mColorHandle, mColorDataSize, GLES20.GL_FLOAT, false,
                mStrideBytes, vertexInfoBuffer);

        GLES20.glEnableVertexAttribArray(mColorHandle);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        //holy shit check this out
        //ahaaaaaaaaaaaaaa 9 / 3 = 3 triangles. last parameter number of vertices
        //GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 9);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length,GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
        GLES20.glDisableVertexAttribArray(mPositionHandle);
=======
>>>>>>> ab207015e95c41478b673050146348355986fc1e
    }

}