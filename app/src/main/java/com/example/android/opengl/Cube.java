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
            // The matrix must be included as a modifier of gl_Position.
            // Note that the uMVPMatrix factor *must be first* in order
            // for the matrix multiplication product to be correct.
            "  gl_Position = uMVPMatrix * vPosition;" +
            "}";

    private final String fragmentShaderCode2 =
            "precision mediump float;" +
            "varying vec4 vColor;    " +
            "void main() {" +
            "  gl_FragColor = vColor;" +
            "}";

    private final String vertexShaderCode3 =
            "uniform mat4 uMVPMatrix;" +
            "attribute vec4 vPosition;" +
            "attribute vec4 aColor;       " +

            "varying vec4 vColor;     " +
            "void main() {" +
            "   vColor = aColor;    " +
            "  gl_Position = uMVPMatrix * vPosition;" +
            "}";

    private final String fragmentShaderCode3 =
            "precision mediump float;" +
            "varying vec4 vColor;    " +
            "void main() {" +
            "  gl_FragColor = vColor;" +
            "}";



    private final float cubeScale = 1.0f;
    private final FloatBuffer vertexBuffer;
    private final FloatBuffer colorBuffer;
    private final ShortBuffer drawListBuffer;
    private final FloatBuffer vertexInfoBuffer;
    private final FloatBuffer mCubePositions;
    private final FloatBuffer mCubeColors;
    private final FloatBuffer mCubeNormals;
    private final int mProgram;
    private final int mProgram2;
    private final int mProgram3;
    private int mPositionHandle;
    private int mColorHandle;
    private int mColorHandle2;
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

    final float[] cubePositionData =
            {
                    // In OpenGL counter-clockwise winding is default. This means that when we look at a triangle,
                    // if the points are counter-clockwise we are looking at the "front". If not we are looking at
                    // the back. OpenGL has an optimization where all back-facing triangles are culled, since they
                    // usually represent the backside of an object and aren't visible anyways.
                    // Front face
                    -1.0f, 1.0f, 1.0f,
                    -1.0f, -1.0f, 1.0f,
                    1.0f, 1.0f, 1.0f,
                    -1.0f, -1.0f, 1.0f,
                    1.0f, -1.0f, 1.0f,
                    1.0f, 1.0f, 1.0f,

                    // Right face
                    1.0f, 1.0f, 1.0f,
                    1.0f, -1.0f, 1.0f,
                    1.0f, 1.0f, -1.0f,
                    1.0f, -1.0f, 1.0f,
                    1.0f, -1.0f, -1.0f,
                    1.0f, 1.0f, -1.0f,

                    // Back face
                    1.0f, 1.0f, -1.0f,
                    1.0f, -1.0f, -1.0f,
                    -1.0f, 1.0f, -1.0f,
                    1.0f, -1.0f, -1.0f,
                    -1.0f, -1.0f, -1.0f,
                    -1.0f, 1.0f, -1.0f,

                    // Left face
                    -1.0f, 1.0f, -1.0f,
                    -1.0f, -1.0f, -1.0f,
                    -1.0f, 1.0f, 1.0f,
                    -1.0f, -1.0f, -1.0f,
                    -1.0f, -1.0f, 1.0f,
                    -1.0f, 1.0f, 1.0f,

                    // Top face
                    -1.0f, 1.0f, -1.0f,
                    -1.0f, 1.0f, 1.0f,
                    1.0f, 1.0f, -1.0f,
                    -1.0f, 1.0f, 1.0f,
                    1.0f, 1.0f, 1.0f,
                    1.0f, 1.0f, -1.0f,

                    // Bottom face
                    1.0f, -1.0f, -1.0f,
                    1.0f, -1.0f, 1.0f,
                    -1.0f, -1.0f, -1.0f,
                    1.0f, -1.0f, 1.0f,
                    -1.0f, -1.0f, 1.0f,
                    -1.0f, -1.0f, -1.0f,
            };

    final float[] cubeColorData =
            {
                    // Front face (red)
                    1.0f, 0.0f, 0.0f, 1.0f,
                    1.0f, 0.0f, 0.0f, 1.0f,
                    1.0f, 0.0f, 0.0f, 1.0f,
                    1.0f, 0.0f, 0.0f, 1.0f,
                    1.0f, 0.0f, 0.0f, 1.0f,
                    1.0f, 0.0f, 0.0f, 1.0f,

                    // Right face (green)
                    0.0f, 1.0f, 0.0f, 1.0f,
                    0.0f, 1.0f, 0.0f, 1.0f,
                    0.0f, 1.0f, 0.0f, 1.0f,
                    0.0f, 1.0f, 0.0f, 1.0f,
                    0.0f, 1.0f, 0.0f, 1.0f,
                    0.0f, 1.0f, 0.0f, 1.0f,

                    // Back face (blue)
                    0.0f, 0.0f, 1.0f, 1.0f,
                    0.0f, 0.0f, 1.0f, 1.0f,
                    0.0f, 0.0f, 1.0f, 1.0f,
                    0.0f, 0.0f, 1.0f, 1.0f,
                    0.0f, 0.0f, 1.0f, 1.0f,
                    0.0f, 0.0f, 1.0f, 1.0f,

                    // Left face (yellow)
                    1.0f, 1.0f, 0.0f, 1.0f,
                    1.0f, 1.0f, 0.0f, 1.0f,
                    1.0f, 1.0f, 0.0f, 1.0f,
                    1.0f, 1.0f, 0.0f, 1.0f,
                    1.0f, 1.0f, 0.0f, 1.0f,
                    1.0f, 1.0f, 0.0f, 1.0f,

                    // Top face (cyan)
                    0.0f, 1.0f, 1.0f, 1.0f,
                    0.0f, 1.0f, 1.0f, 1.0f,
                    0.0f, 1.0f, 1.0f, 1.0f,
                    0.0f, 1.0f, 1.0f, 1.0f,
                    0.0f, 1.0f, 1.0f, 1.0f,
                    0.0f, 1.0f, 1.0f, 1.0f,

                    // Bottom face (magenta)
                    1.0f, 0.0f, 1.0f, 1.0f,
                    1.0f, 0.0f, 1.0f, 1.0f,
                    1.0f, 0.0f, 1.0f, 1.0f,
                    1.0f, 0.0f, 1.0f, 1.0f,
                    1.0f, 0.0f, 1.0f, 1.0f,
                    1.0f, 0.0f, 1.0f, 1.0f
            };

    // X, Y, Z
    // The normal is used in light calculations and is a vector which points
    // orthogonal to the plane of the surface. For a cube model, the normals
    // should be orthogonal to the points of each face.
    final float[] cubeNormalData =
            {
                    // Front face
                    0.0f, 0.0f, 1.0f,
                    0.0f, 0.0f, 1.0f,
                    0.0f, 0.0f, 1.0f,
                    0.0f, 0.0f, 1.0f,
                    0.0f, 0.0f, 1.0f,
                    0.0f, 0.0f, 1.0f,

                    // Right face
                    1.0f, 0.0f, 0.0f,
                    1.0f, 0.0f, 0.0f,
                    1.0f, 0.0f, 0.0f,
                    1.0f, 0.0f, 0.0f,
                    1.0f, 0.0f, 0.0f,
                    1.0f, 0.0f, 0.0f,

                    // Back face
                    0.0f, 0.0f, -1.0f,
                    0.0f, 0.0f, -1.0f,
                    0.0f, 0.0f, -1.0f,
                    0.0f, 0.0f, -1.0f,
                    0.0f, 0.0f, -1.0f,
                    0.0f, 0.0f, -1.0f,

                    // Left face
                    -1.0f, 0.0f, 0.0f,
                    -1.0f, 0.0f, 0.0f,
                    -1.0f, 0.0f, 0.0f,
                    -1.0f, 0.0f, 0.0f,
                    -1.0f, 0.0f, 0.0f,
                    -1.0f, 0.0f, 0.0f,

                    // Top face
                    0.0f, 1.0f, 0.0f,
                    0.0f, 1.0f, 0.0f,
                    0.0f, 1.0f, 0.0f,
                    0.0f, 1.0f, 0.0f,
                    0.0f, 1.0f, 0.0f,
                    0.0f, 1.0f, 0.0f,

                    // Bottom face
                    0.0f, -1.0f, 0.0f,
                    0.0f, -1.0f, 0.0f,
                    0.0f, -1.0f, 0.0f,
                    0.0f, -1.0f, 0.0f,
                    0.0f, -1.0f, 0.0f,
                    0.0f, -1.0f, 0.0f
            };

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

        mCubePositions = ByteBuffer.allocateDirect(cubePositionData.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCubePositions.put(cubePositionData).position(0);

        mCubeColors = ByteBuffer.allocateDirect(cubeColorData.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCubeColors.put(cubeColorData).position(0);

        mCubeNormals = ByteBuffer.allocateDirect(cubeNormalData.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCubeNormals.put(cubeNormalData).position(0);

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

        mProgram3 = GLES20.glCreateProgram();
        int vertexShader3 = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode3);
        int fragmentShader3 = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode3);
        GLES20.glBindAttribLocation(mProgram3, 0, "vPosition");
        GLES20.glBindAttribLocation(mProgram3, 1, "aColor");
        GLES20.glAttachShader(mProgram3, vertexShader3);
        GLES20.glAttachShader(mProgram3, fragmentShader3);
        GLES20.glLinkProgram(mProgram3);
    }



    public void colorChange() {
        delayer++;
        if (delayer >= 30) {
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
    public void drawThatWorks(float[] mvpMatrix) {
        //dont mess with this until sure it works. Otherwise bugs that take infinite time to fix.
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
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
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
    }

    public void draw3(float[] mvpMatrix) {

        GLES20.glUseProgram(mProgram3);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram3, "uMVPMatrix");
        mPositionHandle = GLES20.glGetAttribLocation(mProgram3, "vPosition");
        mColorHandle = GLES20.glGetAttribLocation(mProgram3, "aColor");
        //mNormalHandle = GLES20.glGetAttribLocation(mProgram3, "a_Normal");

        mCubePositions.position(0);
        GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false,
                0, mCubePositions);

        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Pass in the color information
        mCubeColors.position(0);
        GLES20.glVertexAttribPointer(mColorHandle, mColorDataSize, GLES20.GL_FLOAT, false,
                0, mCubeColors);

        GLES20.glEnableVertexAttribArray(mColorHandle);

        // Pass in the normal information
        /*mCubeNormals.position(0);
        GLES20.glVertexAttribPointer(mNormalHandle, mNormalDataSize, GLES20.GL_FLOAT, false,
                0, mCubeNormals);

        GLES20.glEnableVertexAttribArray(mNormalHandle);*/

        float[] scale_matrix = new float[16];
        //float[] scale = {1.3f,1.3f,1.3f};
        //float[] scale = {0.6f,0.6f,0.6f};
        //float[] scale = {0.4f,0.4f,0.4f};
        //float[] scale = {0.8f,1.2f,0.6f};
        float[] scale = {cubeScale,cubeScale,cubeScale};
        Matrix.setIdentityM(scale_matrix, 0);
        Matrix.scaleM(scale_matrix, 0, scale[0], scale[1], scale[2]);
        Matrix.multiplyMM(mvpMatrix, 0, mvpMatrix, 0, scale_matrix, 0);

        // Pass in the combined matrix.
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        // Draw the cube.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);
    }

}