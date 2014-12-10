package com.example.android.opengl;

import android.opengl.Matrix;

public class Camera {
    public float[] mViewMatrix = new float[16];
    private final float[] eyeXYZ = new float[] {0.0f, 0.0f, 0.0f, 1.0f};

    final float lookX = 0.0f;
    final float lookY = 0.0f;
    final float lookZ = 0.0f;

    final float upX = 0.0f;
    final float upY = 1.0f;
    final float upZ = 0.0f;

    int state = 0;

    boolean control = false;

    final int amountOfStates = 6;

    boolean cubeReached = false;

    public void changeCameraState() {
        state++;
        cubeReached = false;
        if (control && state > 5) state = 3;
        else if (!control && state >= amountOfStates) state = 0;
    }

    //state 0 = top corner looking centre
    //state 1 = bottom corner tracking
    // state 2 = centre tracking
    //state 3 = follow and track 3rd person
    //state 4 = follow and track 3rd person far away
    //state 5 == firstperson

    public float[] returnViewMatrix ()
    {
        //state 0 = top corner looking centre
        if (state == 0) {
            eyeXYZ[0] = 35.0f;
            eyeXYZ[1] = 35.0f;
            eyeXYZ[2] = 35.0f;
            Matrix.setLookAtM(mViewMatrix, 0, eyeXYZ[0], eyeXYZ[1], eyeXYZ[2], 0f, 0f, 0.0f, 0f, 1.0f, 0.0f);
        }
        //state 1 = bottom corner tracking
        else if (state == 1) {
            eyeXYZ[0] = -35.0f;
            eyeXYZ[1] = -35.0f;
            eyeXYZ[2] = -35.0f;
            Matrix.setLookAtM(mViewMatrix, 0, eyeXYZ[0], eyeXYZ[1], eyeXYZ[2], MyGLRenderer.centerOfCubeWorldSpace[0], MyGLRenderer.centerOfCubeWorldSpace[1], MyGLRenderer.centerOfCubeWorldSpace[2], 0f, 1.0f, 0.0f);
        }
        // state 2 = centre tracking
        else if (state == 2) {
            eyeXYZ[0] = 0.0f;
            eyeXYZ[1] = 0.0f;
            eyeXYZ[2] = 0.0f;
            Matrix.setLookAtM(mViewMatrix, 0, eyeXYZ[0], eyeXYZ[1], eyeXYZ[2], MyGLRenderer.centerOfCubeWorldSpace[0], MyGLRenderer.centerOfCubeWorldSpace[1], MyGLRenderer.centerOfCubeWorldSpace[2], 0f, 1.0f, 0.0f);
        }
        //state 3 = follow and track 3rd person
        else if (state == 3) {
            float offset = 2.0f;
            float offset2 = 2.5f;

            float distanceFromObect = MyGLRenderer.distance(0, 0, 0, offset, offset, offset);
            if (MyGLRenderer.distance(MyGLRenderer.centerOfCubeWorldSpace[0], MyGLRenderer.centerOfCubeWorldSpace[1], MyGLRenderer.centerOfCubeWorldSpace[2], eyeXYZ[0], eyeXYZ[1], eyeXYZ[2]) > distanceFromObect && !cubeReached) {
                MyGLRenderer.moveObject(eyeXYZ, MyGLRenderer.centerOfCubeWorldSpace[0] + offset, MyGLRenderer.centerOfCubeWorldSpace[1] + offset, MyGLRenderer.centerOfCubeWorldSpace[2] + offset, 0.25f);
                Matrix.setLookAtM(mViewMatrix, 0, eyeXYZ[0], eyeXYZ[1], eyeXYZ[2], MyGLRenderer.centerOfCubeWorldSpace[0], MyGLRenderer.centerOfCubeWorldSpace[1], MyGLRenderer.centerOfCubeWorldSpace[2], 0f, 1.0f, 0.0f);
            }
            if (MyGLRenderer.distance(MyGLRenderer.centerOfCubeWorldSpace[0], MyGLRenderer.centerOfCubeWorldSpace[1], MyGLRenderer.centerOfCubeWorldSpace[2], eyeXYZ[0], eyeXYZ[1], eyeXYZ[2]) < distanceFromObect && !cubeReached) {
                cubeReached = true;
            }
            if (cubeReached) {
                eyeXYZ[0] = MyGLRenderer.centerOfCubeWorldSpace[0] - offset2 * (float)Math.cos(MyGLRenderer.xzCurrAngle);
                eyeXYZ[1] = MyGLRenderer.centerOfCubeWorldSpace[1] - offset2 * (float)Math.sin(MyGLRenderer.xyCurrAngle);
                eyeXYZ[2] = MyGLRenderer.centerOfCubeWorldSpace[2] - offset2 * (float)Math.sin(MyGLRenderer.xzCurrAngle);
                Matrix.setLookAtM(mViewMatrix, 0, eyeXYZ[0], eyeXYZ[1], eyeXYZ[2], MyGLRenderer.centerOfCubeWorldSpace[0], MyGLRenderer.centerOfCubeWorldSpace[1], MyGLRenderer.centerOfCubeWorldSpace[2], 0f, 1.0f, 0.0f);
            }
        }
        //state 4 = follow and track 3rd person far away
        else if (state == 4) {
            float offset = 4.0f;
            float offset2 = 6.0f;
            float distanceFromObect = MyGLRenderer.distance(0, 0, 0, offset, offset, offset);
            if (MyGLRenderer.distance(MyGLRenderer.centerOfCubeWorldSpace[0], MyGLRenderer.centerOfCubeWorldSpace[1], MyGLRenderer.centerOfCubeWorldSpace[2], eyeXYZ[0], eyeXYZ[1], eyeXYZ[2]) > distanceFromObect && !cubeReached) {
                MyGLRenderer.moveObject(eyeXYZ, MyGLRenderer.centerOfCubeWorldSpace[0] + offset, MyGLRenderer.centerOfCubeWorldSpace[1] + offset, MyGLRenderer.centerOfCubeWorldSpace[2] + offset, 0.25f);
                Matrix.setLookAtM(mViewMatrix, 0, eyeXYZ[0], eyeXYZ[1], eyeXYZ[2], MyGLRenderer.centerOfCubeWorldSpace[0], MyGLRenderer.centerOfCubeWorldSpace[1], MyGLRenderer.centerOfCubeWorldSpace[2], 0f, 1.0f, 0.0f);
            }
            if (MyGLRenderer.distance(MyGLRenderer.centerOfCubeWorldSpace[0], MyGLRenderer.centerOfCubeWorldSpace[1], MyGLRenderer.centerOfCubeWorldSpace[2], eyeXYZ[0], eyeXYZ[1], eyeXYZ[2]) < distanceFromObect && !cubeReached) {
                cubeReached = true;
            }
            if (cubeReached) {
                eyeXYZ[0] = MyGLRenderer.centerOfCubeWorldSpace[0] - offset2 * (float)Math.cos(MyGLRenderer.xzCurrAngle);
                eyeXYZ[1] = MyGLRenderer.centerOfCubeWorldSpace[1] - offset2 * (float)Math.sin(MyGLRenderer.xyCurrAngle);
                eyeXYZ[2] = MyGLRenderer.centerOfCubeWorldSpace[2] - offset2 * (float)Math.sin(MyGLRenderer.xzCurrAngle);
                Matrix.setLookAtM(mViewMatrix, 0, eyeXYZ[0], eyeXYZ[1], eyeXYZ[2], MyGLRenderer.centerOfCubeWorldSpace[0], MyGLRenderer.centerOfCubeWorldSpace[1], MyGLRenderer.centerOfCubeWorldSpace[2], 0f, 1.0f, 0.0f);
            }
        }
        //state 5 == firstperson
        else if (state == 5) {
            float offset = 2.0f;
            float offset2 = 2.5f;
            float distanceFromObect = MyGLRenderer.distance(0, 0, 0, offset, offset, offset);
            if (MyGLRenderer.distance(MyGLRenderer.centerOfCubeWorldSpace[0], MyGLRenderer.centerOfCubeWorldSpace[1], MyGLRenderer.centerOfCubeWorldSpace[2], eyeXYZ[0], eyeXYZ[1], eyeXYZ[2]) > distanceFromObect && !cubeReached) {
                MyGLRenderer.moveObject(eyeXYZ, MyGLRenderer.centerOfCubeWorldSpace[0] + offset, MyGLRenderer.centerOfCubeWorldSpace[1] + offset, MyGLRenderer.centerOfCubeWorldSpace[2] + offset, 0.25f);
                Matrix.setLookAtM(mViewMatrix, 0, eyeXYZ[0], eyeXYZ[1], eyeXYZ[2], MyGLRenderer.centerOfCubeWorldSpace[0], MyGLRenderer.centerOfCubeWorldSpace[1], MyGLRenderer.centerOfCubeWorldSpace[2], 0f, 1.0f, 0.0f);
            }
            if (MyGLRenderer.distance(MyGLRenderer.centerOfCubeWorldSpace[0], MyGLRenderer.centerOfCubeWorldSpace[1], MyGLRenderer.centerOfCubeWorldSpace[2], eyeXYZ[0], eyeXYZ[1], eyeXYZ[2]) < distanceFromObect && !cubeReached) {
                cubeReached = true;
            }
            if (cubeReached) {
                eyeXYZ[0] = MyGLRenderer.centerOfCubeWorldSpace[0] + offset2 * (float)Math.cos(MyGLRenderer.xzCurrAngle);
                eyeXYZ[1] = MyGLRenderer.centerOfCubeWorldSpace[1] + offset2 * (float)Math.sin(MyGLRenderer.xyCurrAngle);
                eyeXYZ[2] = MyGLRenderer.centerOfCubeWorldSpace[2] + offset2 * (float)Math.sin(MyGLRenderer.xzCurrAngle);
                Matrix.setLookAtM(mViewMatrix, 0, eyeXYZ[0], eyeXYZ[1], eyeXYZ[2], MyGLRenderer.centerOfCubeWorldSpace[0] + 6.0f * (float)Math.cos(MyGLRenderer.xzCurrAngle), MyGLRenderer.centerOfCubeWorldSpace[1] + 6.0f * (float)Math.sin(MyGLRenderer.xyCurrAngle), MyGLRenderer.centerOfCubeWorldSpace[2] + 6.0f * (float)Math.sin(MyGLRenderer.xzCurrAngle), 0f, 1.0f, 0.0f);
            }
        }
        return mViewMatrix;
    }
}
