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
        final int pointVertexShaderHandle = Shaders.loadShader(GLES20.GL_VERTEX_SHADER, shaders.getPointVertexShader());
        final int pointFragmentShaderHandle = Shaders.loadShader(GLES20.GL_FRAGMENT_SHADER, shaders.getPointFragmentShader());
        mPointProgramHandle = Shaders.createAndLinkProgram(pointVertexShaderHandle, pointFragmentShaderHandle,
                new String[]{"a_Position"});
    }

    //make private
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

    public static void drawAllLight() {
        // Calculate position of the light[0]. Coordinate location.
       /* Matrix.setIdentityM(light[0].mLightModelMatrix, 0);
        Matrix.setIdentityM(mMVPMatrix, 0);
        moveObject(light[0].mLightPosInWorldSpace,theJourney[coord][0] + 2.0f, theJourney[coord][1] + 2.0f, theJourney[coord][2], 0.3f );
        Matrix.translateM(light[0].mLightModelMatrix, 0, light[0].mLightPosInWorldSpace[0], light[0].mLightPosInWorldSpace[1], light[0].mLightPosInWorldSpace[2]);

        Matrix.multiplyMV(light[0].mLightPosInWorldSpace, 0, light[0].mLightModelMatrix, 0, light[0].mLightPosInModelSpace, 0);
        Matrix.multiplyMV(light[0].mLightPosInEyeSpace, 0, mViewMatrix, 0, light[0].mLightPosInWorldSpace, 0);

        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, light[0].mLightModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
        light[0].drawLight(mMVPMatrix);

        //light[1]. Rotate and then push into the distance.
        Matrix.setIdentityM(light[1].mLightModelMatrix, 0);
        Matrix.setIdentityM(mMVPMatrix, 0);
        Matrix.translateM(light[1].mLightModelMatrix, 0, 30.0f, 30.0f, 30.0f);
        Matrix.rotateM(light[1].mLightModelMatrix, 0, sqAngle, 0.0f, 1.0f, 0.0f);
        Matrix.translateM(light[1].mLightModelMatrix, 0, 0.0f, 0.0f, 2.0f);

        Matrix.multiplyMV(light[1].mLightPosInWorldSpace, 0, light[1].mLightModelMatrix, 0, light[1].mLightPosInModelSpace, 0);
        Matrix.multiplyMV(light[1].mLightPosInEyeSpace, 0, mViewMatrix, 0, light[1].mLightPosInWorldSpace, 0);

        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, light[1].mLightModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
        light[1].drawLight(mMVPMatrix);

        //light[2]
        Matrix.setIdentityM(light[2].mLightModelMatrix, 0);
        Matrix.setIdentityM(mMVPMatrix, 0);
        Matrix.translateM(light[2].mLightModelMatrix, 0, -30.0f, -30.0f, -30.0f);
        Matrix.rotateM(light[2].mLightModelMatrix, 0, sqAngle, 0.0f, 1.0f, 0.0f);
        Matrix.translateM(light[2].mLightModelMatrix, 0, 0.0f, 0.0f, 2.0f);

        Matrix.multiplyMV(light[2].mLightPosInWorldSpace, 0, light[2].mLightModelMatrix, 0, light[2].mLightPosInModelSpace, 0);
        Matrix.multiplyMV(light[2].mLightPosInEyeSpace, 0, mViewMatrix, 0, light[2].mLightPosInWorldSpace, 0);

        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, light[2].mLightModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
        light[2].drawLight(mMVPMatrix);

        //light[3]
        Matrix.setIdentityM(light[3].mLightModelMatrix, 0);
        Matrix.setIdentityM(mMVPMatrix, 0);
        Matrix.translateM(light[3].mLightModelMatrix, 0, 30.0f, -30.0f, -30.0f);
        Matrix.rotateM(light[3].mLightModelMatrix, 0, sqAngle, 0.0f, 1.0f, 0.0f);
        Matrix.translateM(light[3].mLightModelMatrix, 0, 0.0f, 0.0f, 2.0f);

        Matrix.multiplyMV(light[3].mLightPosInWorldSpace, 0, light[3].mLightModelMatrix, 0, light[3].mLightPosInModelSpace, 0);
        Matrix.multiplyMV(light[3].mLightPosInEyeSpace, 0, mViewMatrix, 0, light[3].mLightPosInWorldSpace, 0);

        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, light[3].mLightModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
        light[3].drawLight(mMVPMatrix);

        //light[4]
        Matrix.setIdentityM(light[4].mLightModelMatrix, 0);
        Matrix.setIdentityM(mMVPMatrix, 0);
        Matrix.translateM(light[4].mLightModelMatrix, 0, -30.0f, 30.0f, -30.0f);
        Matrix.rotateM(light[4].mLightModelMatrix, 0, sqAngle, 0.0f, 1.0f, 0.0f);
        Matrix.translateM(light[4].mLightModelMatrix, 0, 0.0f, 0.0f, 2.0f);

        Matrix.multiplyMV(light[4].mLightPosInWorldSpace, 0, light[4].mLightModelMatrix, 0, light[4].mLightPosInModelSpace, 0);
        Matrix.multiplyMV(light[4].mLightPosInEyeSpace, 0, mViewMatrix, 0, light[4].mLightPosInWorldSpace, 0);

        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, light[4].mLightModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
        light[4].drawLight(mMVPMatrix);

        //light[5]
        Matrix.setIdentityM(light[5].mLightModelMatrix, 0);
        Matrix.setIdentityM(mMVPMatrix, 0);
        Matrix.translateM(light[5].mLightModelMatrix, 0, -30.0f, -30.0f, 30.0f);
        Matrix.rotateM(light[5].mLightModelMatrix, 0, sqAngle, 0.0f, 1.0f, 0.0f);
        Matrix.translateM(light[5].mLightModelMatrix, 0, 0.0f, 0.0f, 2.0f);

        Matrix.multiplyMV(light[5].mLightPosInWorldSpace, 0, light[5].mLightModelMatrix, 0, light[5].mLightPosInModelSpace, 0);
        Matrix.multiplyMV(light[5].mLightPosInEyeSpace, 0, mViewMatrix, 0, light[5].mLightPosInWorldSpace, 0);

        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, light[5].mLightModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
        light[5].drawLight(mMVPMatrix);*/
    }
}
