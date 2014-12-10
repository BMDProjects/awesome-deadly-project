package com.example.android.opengl;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

public class Room {
    Shaders shaders = new Shaders();

    private final FloatBuffer mCubePositions;
    private final FloatBuffer mCubeColors;
    private final FloatBuffer mCubeNormals;
    private final int mProgramPerPixel;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;
    private int mMVMatrixHandle;
    private int mMMatrixHandle;
    private int mLightPosHandle0;
    private int mLightPosHandle1;
    private int mLightPosHandle2;
    private int mLightPosHandle3;
    private int mLightPosHandle4;
    private int mLightPosHandle5;
    private int distanceCorrectionHandle;
    private int mNormalHandle;

    private int mTextureDataHandle;
    private int mTextureCoordinateHandle;
    private int mTextureUniformHandle;
    private final FloatBuffer mCubeTextureCoordinates;
    private final int mTextureCoordinateDataSize = 2;

    public int delayer = 20;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;

    final float[] cubePositionData =
            {
                    // Front face
                    -1.0f, 1.0f, 1.0f,
                    1.0f, 1.0f, 1.0f,
                    -1.0f, -1.0f, 1.0f,
                    -1.0f, -1.0f, 1.0f,
                    1.0f, 1.0f, 1.0f,
                    1.0f, -1.0f, 1.0f,

                    // Right face
                    1.0f, 1.0f, 1.0f,
                    1.0f, 1.0f, -1.0f,
                    1.0f, -1.0f, 1.0f,
                    1.0f, -1.0f, 1.0f,
                    1.0f, 1.0f, -1.0f,
                    1.0f, -1.0f, -1.0f,

                    // Back face
                    1.0f, 1.0f, -1.0f,
                    -1.0f, 1.0f, -1.0f,
                    1.0f, -1.0f, -1.0f,
                    1.0f, -1.0f, -1.0f,
                    -1.0f, 1.0f, -1.0f,
                    -1.0f, -1.0f, -1.0f,


                    // Left face
                    -1.0f, 1.0f, -1.0f,
                    -1.0f, 1.0f, 1.0f,
                    -1.0f, -1.0f, -1.0f,
                    -1.0f, -1.0f, -1.0f,
                    -1.0f, 1.0f, 1.0f,
                    -1.0f, -1.0f, 1.0f,

                    // Top face
                    -1.0f, 1.0f, -1.0f,
                    1.0f, 1.0f, -1.0f,
                    -1.0f, 1.0f, 1.0f,
                    -1.0f, 1.0f, 1.0f,
                    1.0f, 1.0f, -1.0f,
                    1.0f, 1.0f, 1.0f,


                    // Bottom face
                    1.0f, -1.0f, -1.0f,
                    -1.0f, -1.0f, -1.0f,
                    1.0f, -1.0f, 1.0f,
                    1.0f, -1.0f, 1.0f,
                    -1.0f, -1.0f, -1.0f,
                    -1.0f, -1.0f, 1.0f,
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

                    // Back face (brown)
                    0.8f, 0.53f, 0.24f, 1.0f,
                    0.8f, 0.53f, 0.24f, 1.0f,
                    0.8f, 0.53f, 0.24f, 1.0f,
                    0.8f, 0.53f, 0.24f, 1.0f,
                    0.8f, 0.53f, 0.24f, 1.0f,
                    0.8f, 0.53f, 0.24f, 1.0f,

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

    final float[] cubeNormalData =
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
            };

    final float[] cubeTextureCoordinateData =
            {
                    // Front face
                    0.0f, 0.0f,
                    1.0f, 0.0f,
                    0.0f, 1.0f,
                    0.0f, 1.0f,
                    1.0f, 0.0f,
                    1.0f, 1.0f,

                    // Right face
                    0.0f, 0.0f,
                    1.0f, 0.0f,
                    0.0f, 1.0f,
                    0.0f, 1.0f,
                    1.0f, 0.0f,
                    1.0f, 1.0f,

                    // Back face
                    0.0f, 0.0f,
                    1.0f, 0.0f,
                    0.0f, 1.0f,
                    0.0f, 1.0f,
                    1.0f, 0.0f,
                    1.0f, 1.0f,

                    // Left face
                    0.0f, 0.0f,
                    1.0f, 0.0f,
                    0.0f, 1.0f,
                    0.0f, 1.0f,
                    1.0f, 0.0f,
                    1.0f, 1.0f,

                    // Top face
                    0.0f, 0.0f,
                    1.0f, 0.0f,
                    0.0f, 1.0f,
                    0.0f, 1.0f,
                    1.0f, 0.0f,
                    1.0f, 1.0f,

                    // Bottom face
                    0.0f, 0.0f,
                    1.0f, 0.0f,
                    0.0f, 1.0f,
                    0.0f, 1.0f,
                    1.0f, 0.0f,
                    1.0f, 1.0f,
            };


    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    public float[] color = {0.0f, 1.0f, 1.0f, 0.0f};

    public Room(Context context) {
        mTextureDataHandle = TextureHelper.loadTexture(context, R.drawable.bumpy_bricks_public_domain);

        mCubePositions = ByteBuffer.allocateDirect(cubePositionData.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCubePositions.put(cubePositionData).position(0);

        mCubeColors = ByteBuffer.allocateDirect(cubeColorData.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCubeColors.put(cubeColorData).position(0);

        mCubeNormals = ByteBuffer.allocateDirect(cubeNormalData.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCubeNormals.put(cubeNormalData).position(0);

        mCubeTextureCoordinates = ByteBuffer.allocateDirect(cubeTextureCoordinateData.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCubeTextureCoordinates.put(cubeTextureCoordinateData).position(0);

        int perPixelTextureVertexShader = Shaders.loadShader(GLES20.GL_VERTEX_SHADER, shaders.getVertexShader(5));
        int perPixelTexturefragmentShader = Shaders.loadShader(GLES20.GL_FRAGMENT_SHADER, shaders.getFragmentShader(5));
        mProgramPerPixel = Shaders.createAndLinkProgram(perPixelTextureVertexShader, perPixelTexturefragmentShader,
                new String[]{"a_Position",  "a_Color", "a_Normal", "a_TexCoordinate"});
    }

    private final int mPositionDataSize = 3;
    private final int mColorDataSize = 4;
    private final int mNormalDataSize = 3;


    public void drawRoom(float[] modelMatrix, float cubeScale) {
        GLES20.glUseProgram(mProgramPerPixel);
        final float[] mMVPMatrix = new float[16];
        final float[] mMVMatrix = new float[16];
        Matrix.setIdentityM(mMVPMatrix, 0);
        Matrix.setIdentityM(mMVMatrix, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);
        GLES20.glUniform1i(mTextureUniformHandle, 0);
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);

        GLES20.glEnable(GLES20.GL_CULL_FACE);

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramPerPixel, "u_MVPMatrix");
        mMVMatrixHandle = GLES20.glGetUniformLocation(mProgramPerPixel, "u_MVMatrix");
        mTextureUniformHandle = GLES20.glGetUniformLocation(mProgramPerPixel, "u_Texture");
        mLightPosHandle0 = GLES20.glGetUniformLocation(mProgramPerPixel, "u_LightPos0");
        mLightPosHandle1 = GLES20.glGetUniformLocation(mProgramPerPixel, "u_LightPos1");
        mLightPosHandle2 = GLES20.glGetUniformLocation(mProgramPerPixel, "u_LightPos2");
        mLightPosHandle3 = GLES20.glGetUniformLocation(mProgramPerPixel, "u_LightPos3");
        mLightPosHandle4 = GLES20.glGetUniformLocation(mProgramPerPixel, "u_LightPos4");
        mLightPosHandle5 = GLES20.glGetUniformLocation(mProgramPerPixel, "u_LightPos5");

        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgramPerPixel, "a_TexCoordinate");
        mPositionHandle = GLES20.glGetAttribLocation(mProgramPerPixel, "a_Position");
        mColorHandle = GLES20.glGetAttribLocation(mProgramPerPixel, "a_Color");
        mNormalHandle = GLES20.glGetAttribLocation(mProgramPerPixel, "a_Normal");


        //Pass in vertex position information
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

        mCubeTextureCoordinates.position(0);
        GLES20.glVertexAttribPointer(mTextureCoordinateHandle, mTextureCoordinateDataSize, GLES20.GL_FLOAT, false,
                0, mCubeTextureCoordinates);

        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);

        float[] scale_matrix = new float[16];
        float[] scale = {cubeScale,cubeScale,cubeScale};
        Matrix.setIdentityM(scale_matrix, 0);
        Matrix.scaleM(scale_matrix, 0, scale[0], scale[1], scale[2]);
        Matrix.multiplyMM(modelMatrix, 0, modelMatrix , 0, scale_matrix, 0);

        //view times model is the model view matrix
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

        GLES20.glUniform1f(distanceCorrectionHandle, 1.0f);

        GLES20.glUniform3f(mLightPosHandle0, MyGLRenderer.light[0].mLightPosInEyeSpace[0], MyGLRenderer.light[0].mLightPosInEyeSpace[1], MyGLRenderer.light[0].mLightPosInEyeSpace[2]);
        GLES20.glUniform3f(mLightPosHandle1, MyGLRenderer.light[1].mLightPosInEyeSpace[0], MyGLRenderer.light[1].mLightPosInEyeSpace[1], MyGLRenderer.light[1].mLightPosInEyeSpace[2]);
        GLES20.glUniform3f(mLightPosHandle2, MyGLRenderer.light[2].mLightPosInEyeSpace[0], MyGLRenderer.light[2].mLightPosInEyeSpace[1], MyGLRenderer.light[2].mLightPosInEyeSpace[2]);
        GLES20.glUniform3f(mLightPosHandle3, MyGLRenderer.light[3].mLightPosInEyeSpace[0], MyGLRenderer.light[3].mLightPosInEyeSpace[1], MyGLRenderer.light[3].mLightPosInEyeSpace[2]);
        GLES20.glUniform3f(mLightPosHandle4, MyGLRenderer.light[4].mLightPosInEyeSpace[0], MyGLRenderer.light[4].mLightPosInEyeSpace[1], MyGLRenderer.light[4].mLightPosInEyeSpace[2]);
        GLES20.glUniform3f(mLightPosHandle5, MyGLRenderer.light[5].mLightPosInEyeSpace[0], MyGLRenderer.light[5].mLightPosInEyeSpace[1], MyGLRenderer.light[5].mLightPosInEyeSpace[2]);

        // Draw the cube.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);

        GLES20.glDisable(GLES20.GL_CULL_FACE);
    }

}
