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
    Shaders shaders = new Shaders();

    //private final FloatBuffer vertexBuffer;
    //private final ShortBuffer drawListBuffer;
    private final FloatBuffer mCubePositions;
    private final FloatBuffer mCubeColors;
    private final FloatBuffer mCubeNormals;
    private final int mProgram;
    private final int mProgram2;
    private final int mProgram3;
    private final int mProgramPerPixel;
    private int mPositionHandle;
    private int mColorHandle;
    private int mColorHandle2;
    private int mMVPMatrixHandle;

    private int mMVMatrixHandle;
    private int mLightPosHandle;
    private int mNormalHandle;

    public int delayer = 20;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;

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

    /*final float[] cubeNormalData =
            {
                    // Front face
                    0.0f, 0.0f, -1.0f,
                    0.0f, 0.0f, -1.0f,
                    0.0f, 0.0f, -1.0f,
                    0.0f, 0.0f, -1.0f,
                    0.0f, 0.0f, -1.0f,
                    0.0f, 0.0f, -1.0f,

                    // Right face
                    -1.0f, 0.0f, 0.0f,
                    -1.0f, 0.0f, 0.0f,
                    -1.0f, 0.0f, 0.0f,
                    -1.0f, 0.0f, 0.0f,
                    -1.0f, 0.0f, 0.0f,
                    -1.0f, 0.0f, 0.0f,

                    // Back face
                    0.0f, 0.0f, 1.0f,
                    0.0f, 0.0f, 1.0f,
                    0.0f, 0.0f, 1.0f,
                    0.0f, 0.0f, 1.0f,
                    0.0f, 0.0f, 1.0f,
                    0.0f, 0.0f, 1.0f,

                    // Left face
                    1.0f, 0.0f, 0.0f,
                    1.0f, 0.0f, 0.0f,
                    1.0f, 0.0f, 0.0f,
                    1.0f, 0.0f, 0.0f,
                    1.0f, 0.0f, 0.0f,
                    1.0f, 0.0f, 0.0f,

                    // Top face
                    0.0f, -1.0f, 0.0f,
                    0.0f, -1.0f, 0.0f,
                    0.0f, -1.0f, 0.0f,
                    0.0f, -1.0f, 0.0f,
                    0.0f, -1.0f, 0.0f,
                    0.0f, -1.0f, 0.0f,

                    // Bottom face
                    0.0f, 1.0f, 0.0f,
                    0.0f, 1.0f, 0.0f,
                    0.0f, 1.0f, 0.0f,
                    0.0f, 1.0f, 0.0f,
                    0.0f, 1.0f, 0.0f,
                    0.0f, 1.0f, 0.0f
            };*/


    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    public float[] color = {0.0f, 1.0f, 1.0f, 0.0f};

    public Cube() {
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
        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, shaders.getVertexShader(0));
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, shaders.getFragmentShader(0));
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables


        int vertexShader2 = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, shaders.getVertexShader(2));
        int fragmentShader2 = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, shaders.getFragmentShader(2));
        mProgram2 = MyGLRenderer.createAndLinkProgram(vertexShader2, fragmentShader2,
                new String[]{"vPosition", "aColor"});

        int vertexShader4 = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, shaders.getVertexShader(3));
        int fragmentShader4 = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, shaders.getFragmentShader(3));
        mProgram3 = MyGLRenderer.createAndLinkProgram(vertexShader4, fragmentShader4,
                new String[]{"a_Position",  "a_Color", "a_Normal"});

        int perPixelVertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, shaders.getVertexShader(4));
        int perPixelfragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, shaders.getFragmentShader(4));
        mProgramPerPixel = MyGLRenderer.createAndLinkProgram(perPixelVertexShader, perPixelfragmentShader,
                new String[]{"a_Position",  "a_Color", "a_Normal"});
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
   /* public void drawThatWorks(float[] mvpMatrix) {
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
    }*/

   /* public void draw1(float[] mvpMatrix) {
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
    }*/

    private final int mStrideBytes = 7 * 4;
    private final int mPositionOffset = 0;
    private final int mPositionDataSize = 3;
    private final int mColorOffset = 3;
    private final int mColorDataSize = 4;

    /*public void draw2(float[] mvpMatrix) {
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
    }*/

    public void draw3(float[] mvpMatrix, float cubeScale) {

        GLES20.glUseProgram(mProgram2);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram2, "uMVPMatrix");
        mPositionHandle = GLES20.glGetAttribLocation(mProgram2, "vPosition");
        mColorHandle = GLES20.glGetAttribLocation(mProgram2, "aColor");
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
        //float[] scale = {0.8f,1.6f,0.6f};
        float[] scale = {cubeScale,cubeScale,cubeScale};
        Matrix.setIdentityM(scale_matrix, 0);
        Matrix.scaleM(scale_matrix, 0, scale[0], scale[1], scale[2]);
        Matrix.multiplyMM(mvpMatrix, 0, mvpMatrix , 0, scale_matrix, 0);

        // Pass in the combined matrix.
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        // Draw the cube.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);
    }

    private final int mNormalDataSize = 3;

    public void draw4(float[] modelMatrix, float cubeScale) {

        GLES20.glUseProgram(mProgram3);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        final float[] mMVPMatrix = new float[16];

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram3, "u_MVPMatrix");
        mMVMatrixHandle = GLES20.glGetUniformLocation(mProgram3, "u_MVMatrix");
        mLightPosHandle = GLES20.glGetUniformLocation(mProgram3, "u_LightPos");
        mPositionHandle = GLES20.glGetAttribLocation(mProgram3, "a_Position");
        mColorHandle = GLES20.glGetAttribLocation(mProgram3, "a_Color");
        mNormalHandle = GLES20.glGetAttribLocation(mProgram3, "a_Normal");

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
        mCubeNormals.position(0);
        GLES20.glVertexAttribPointer(mNormalHandle, mNormalDataSize, GLES20.GL_FLOAT, false,
                0, mCubeNormals);
        GLES20.glEnableVertexAttribArray(mNormalHandle);

        float[] scale_matrix = new float[16];
        float[] scale = {cubeScale,cubeScale,cubeScale};
        Matrix.setIdentityM(scale_matrix, 0);
        Matrix.scaleM(scale_matrix, 0, scale[0], scale[1], scale[2]);
        Matrix.multiplyMM(modelMatrix, 0, modelMatrix , 0, scale_matrix, 0);

       /* float[] scale_matrix = new float[16];
        float[] scale = {cubeScale,cubeScale,cubeScale};
        Matrix.setIdentityM(scale_matrix, 0);
        Matrix.scaleM(scale_matrix, 0, scale[0], scale[1], scale[2]);
        Matrix.multiplyMM(mMVPMatrix, 0, mMVPMatrix , 0, scale_matrix, 0);*/

        //view X model is the model view matrix
        Matrix.multiplyMM(mMVPMatrix, 0, MyGLRenderer.mViewMatrix, 0,modelMatrix , 0);
        // Pass in the modelview matrix.
        GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mMVPMatrix, 0);


        /*float[] scale_matrix = new float[16];
        float[] scale = {cubeScale,cubeScale,cubeScale};
        Matrix.setIdentityM(scale_matrix, 0);
        Matrix.scaleM(scale_matrix, 0, scale[0], scale[1], scale[2]);
        Matrix.multiplyMM(mMVPMatrix, 0, mMVPMatrix , 0, scale_matrix, 0);*/

        Matrix.multiplyMM(mMVPMatrix, 0, MyGLRenderer.mProjectionMatrix, 0, mMVPMatrix, 0);
        // Pass in the combined matrix.
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        GLES20.glUniform3f(mLightPosHandle, MyGLRenderer.light1.mLightPosInEyeSpace[0], MyGLRenderer.light1.mLightPosInEyeSpace[1], MyGLRenderer.light1.mLightPosInEyeSpace[2]);

        // Draw the cube.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);
    }

    public void draw5(float[] modelMatrix, float cubeScale) {
        GLES20.glUseProgram(mProgramPerPixel);
        final float[] mMVPMatrix = new float[16];
        final float[] mMVMatrix = new float[16];
        Matrix.setIdentityM(mMVPMatrix, 0);
        Matrix.setIdentityM(mMVMatrix, 0);

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramPerPixel, "u_MVPMatrix");
        mMVMatrixHandle = GLES20.glGetUniformLocation(mProgramPerPixel, "u_MVMatrix");
        mLightPosHandle = GLES20.glGetUniformLocation(mProgramPerPixel, "u_LightPos");
        mPositionHandle = GLES20.glGetAttribLocation(mProgramPerPixel, "a_Position");
        mColorHandle = GLES20.glGetAttribLocation(mProgramPerPixel, "a_Color");
        mNormalHandle = GLES20.glGetAttribLocation(mProgramPerPixel, "a_Normal");

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
        mCubeNormals.position(0);
        GLES20.glVertexAttribPointer(mNormalHandle, mNormalDataSize, GLES20.GL_FLOAT, false,
                0, mCubeNormals);
        GLES20.glEnableVertexAttribArray(mNormalHandle);

        float[] scale_matrix = new float[16];
        float[] scale = {cubeScale,cubeScale,cubeScale};
        Matrix.setIdentityM(scale_matrix, 0);
        Matrix.scaleM(scale_matrix, 0, scale[0], scale[1], scale[2]);
        Matrix.multiplyMM(modelMatrix, 0, modelMatrix , 0, scale_matrix, 0);

       /* float[] scale_matrix = new float[16];
        float[] scale = {cubeScale,cubeScale,cubeScale};
        Matrix.setIdentityM(scale_matrix, 0);
        Matrix.scaleM(scale_matrix, 0, scale[0], scale[1], scale[2]);
        Matrix.multiplyMM(mMVPMatrix, 0, mMVPMatrix , 0, scale_matrix, 0);*/

        //view X model is the model view matrix
       // Matrix.multiplyMM(mMVMatrix, 0, modelMatrix , 0, MyGLRenderer.mViewMatrix, 0);
        //why does this matter the order??????????
        Matrix.multiplyMM(mMVMatrix, 0, MyGLRenderer.mViewMatrix  , 0, modelMatrix, 0);
        // Pass in the modelview matrix.
        GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mMVMatrix, 0);

//why does it only shine after modelview matrix is sent in and then scaling happens
        /*float[] scale_matrix = new float[16];
        float[] scale = {cubeScale,cubeScale,cubeScale};
        Matrix.setIdentityM(scale_matrix, 0);
        Matrix.scaleM(scale_matrix, 0, scale[0], scale[1], scale[2]);
        Matrix.multiplyMM(mMVMatrix, 0, mMVMatrix , 0, scale_matrix, 0);*/

        Matrix.multiplyMM(mMVPMatrix, 0, MyGLRenderer.mProjectionMatrix, 0, mMVMatrix, 0);
        // Pass in the combined matrix.
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        GLES20.glUniform3f(mLightPosHandle, MyGLRenderer.light1.mLightPosInEyeSpace[0], MyGLRenderer.light1.mLightPosInEyeSpace[1], MyGLRenderer.light1.mLightPosInEyeSpace[2]);

        // Draw the cube.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);
    }

}