package com.example.android.opengl;

import android.opengl.GLES20;
import android.opengl.Matrix;

public class Light {
    Shaders shaders = new Shaders();

    public float[] mLightModelMatrix = new float[16];

    public final float[] mLightPosInModelSpace = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
    public final float[] mLightPosInWorldSpace = new float[4];
    public final float[] mLightPosInEyeSpace = new float[4];
    private int mPointProgramHandle;

    public Light() {
        final int pointVertexShaderHandle = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, shaders.getPointVertexShader());
        final int pointFragmentShaderHandle = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, shaders.getPointFragmentShader());
        mPointProgramHandle = MyGLRenderer.createAndLinkProgram(pointVertexShaderHandle, pointFragmentShaderHandle,
                new String[]{"a_Position"});
    }

    public void drawLight(float[] mvpMatrix) {
        GLES20.glUseProgram(mPointProgramHandle);
        final int pointMVPMatrixHandle = GLES20.glGetUniformLocation(mPointProgramHandle, "u_MVPMatrix");
        final int pointPositionHandle = GLES20.glGetAttribLocation(mPointProgramHandle, "a_Position");

        // Pass in the position.
        GLES20.glVertexAttrib3f(pointPositionHandle, mLightPosInModelSpace[0], mLightPosInModelSpace[1], mLightPosInModelSpace[2]);

        // Since we are not using a buffer object, disable vertex arrays for this attribute.
        GLES20.glDisableVertexAttribArray(pointPositionHandle);

        // Pass in the transformation matrix.
        //Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mLightModelMatrix, 0);
       // Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(pointMVPMatrixHandle, 1, false, mvpMatrix, 0);

        // Draw the point.
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);
    }
}
