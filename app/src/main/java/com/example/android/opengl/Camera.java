package com.example.android.opengl;

import android.opengl.Matrix;

public class Camera {
    public float[] mViewMatrix = new float[16];
    public float[] eyeXYZ = new float[] {0.0f, 0.0f, 0.0f, 1.0f};

    static int state = 0;

    final int amountOfStates = 6;

    boolean cubeReached = false;
    boolean vectorReached = false;

    public void changeCameraState() {
        state++;
        cubeReached = false;
        vectorReached = false;
        if (MyGLRenderer.control && state > 5) state = 3;
        else if (!MyGLRenderer.control && state >= amountOfStates) state = 0;
    }

    //state 0 = top corner looking centre
    //state 1 = bottom corner tracking
    // state 2 = centre tracking
    //state 3 = follow and track 3rd person
    //state 4 = follow and track 3rd person far away
    //state 5 = firstperson

    public float[] returnViewMatrix ()
    {
        float offset;
        float offset2;
        float yOverhead = 2.0f;
        //follow dolphin
        if (MyGLRenderer.dolphinFollow) {
            switch (state) {
                //state 0 = top corner looking centre
                case 0:
                    eyeXYZ[0] = 35.0f;
                    eyeXYZ[1] = 35.0f;
                    eyeXYZ[2] = 35.0f;
                    Matrix.setLookAtM(mViewMatrix, 0, eyeXYZ[0], eyeXYZ[1], eyeXYZ[2], 0f, 0f, 0.0f, 0f, 1.0f, 0.0f);
                    break;
                //state 1 = bottom corner tracking
                case 1:
                    eyeXYZ[0] = -35.0f;
                    eyeXYZ[1] = -35.0f;
                    eyeXYZ[2] = -35.0f;
                    Matrix.setLookAtM(mViewMatrix, 0, eyeXYZ[0], eyeXYZ[1], eyeXYZ[2], MyGLRenderer.dolphin.centerOfDolphinWorldSpace[0], MyGLRenderer.dolphin.centerOfDolphinWorldSpace[1], MyGLRenderer.dolphin.centerOfDolphinWorldSpace[2], 0f, 1.0f, 0.0f);
                    break;
                // state 2 = centre tracking
                case 2:
                    eyeXYZ[0] = 1.0f;
                    eyeXYZ[1] = 1.0f;
                    eyeXYZ[2] = 1.0f;
                    Matrix.setLookAtM(mViewMatrix, 0, eyeXYZ[0], eyeXYZ[1], eyeXYZ[2], MyGLRenderer.dolphin.centerOfDolphinWorldSpace[0], MyGLRenderer.dolphin.centerOfDolphinWorldSpace[1], MyGLRenderer.dolphin.centerOfDolphinWorldSpace[2], 0f, 1.0f, 0.0f);
                    break;
                //state 3 = follow and track 3rd person
                case 3:
                    offset = 4.0f;
                    float speedOfCamera = MyGLRenderer.getDolphinSpeed() + 0.2f;

                    if (!cubeReached && !vectorReached) {
                        float vectorX = MyGLRenderer.dolphin.centerOfDolphinWorldSpace[0] - MyGLRenderer.dolphin.dolphinNormalWorldSpace[0] * 7.0f;
                        float vectorY = MyGLRenderer.dolphin.centerOfDolphinWorldSpace[1] - MyGLRenderer.dolphin.dolphinNormalWorldSpace[1] * 7.0f + yOverhead;
                        float vectorZ = MyGLRenderer.dolphin.centerOfDolphinWorldSpace[2] - MyGLRenderer.dolphin.dolphinNormalWorldSpace[2] * 7.0f;
                        if (MyGLRenderer.moveObject(eyeXYZ, vectorX, vectorY, vectorZ, speedOfCamera)) {
                            vectorReached = true;
                        }
                        Matrix.setLookAtM(mViewMatrix, 0, eyeXYZ[0], eyeXYZ[1], eyeXYZ[2], MyGLRenderer.dolphin.centerOfDolphinWorldSpace[0], MyGLRenderer.dolphin.centerOfDolphinWorldSpace[1], MyGLRenderer.dolphin.centerOfDolphinWorldSpace[2], 0f, 1.0f, 0.0f);
                    }
                    if (!cubeReached && vectorReached) {
                        float cubeDestX = MyGLRenderer.dolphin.centerOfDolphinWorldSpace[0] - MyGLRenderer.dolphin.dolphinNormalWorldSpace[0] * offset;
                        float cubeDestY = MyGLRenderer.dolphin.centerOfDolphinWorldSpace[1] - MyGLRenderer.dolphin.dolphinNormalWorldSpace[1] * offset + yOverhead;
                        float cubeDestZ = MyGLRenderer.dolphin.centerOfDolphinWorldSpace[2] - MyGLRenderer.dolphin.dolphinNormalWorldSpace[2] * offset;
                        if (MyGLRenderer.moveObject(eyeXYZ, cubeDestX, cubeDestY, cubeDestZ, speedOfCamera)) {
                            cubeReached = true;
                        }
                        Matrix.setLookAtM(mViewMatrix, 0, eyeXYZ[0], eyeXYZ[1], eyeXYZ[2], MyGLRenderer.dolphin.centerOfDolphinWorldSpace[0], MyGLRenderer.dolphin.centerOfDolphinWorldSpace[1], MyGLRenderer.dolphin.centerOfDolphinWorldSpace[2], 0f, 1.0f, 0.0f);
                    }
                    if (cubeReached) {
                        eyeXYZ[0] = MyGLRenderer.dolphin.centerOfDolphinWorldSpace[0] - MyGLRenderer.dolphin.dolphinNormalWorldSpace[0] * offset;
                        eyeXYZ[1] = MyGLRenderer.dolphin.centerOfDolphinWorldSpace[1] - MyGLRenderer.dolphin.dolphinNormalWorldSpace[1] * offset + yOverhead;
                        eyeXYZ[2] = MyGLRenderer.dolphin.centerOfDolphinWorldSpace[2] - MyGLRenderer.dolphin.dolphinNormalWorldSpace[2] * offset;
                        Matrix.setLookAtM(mViewMatrix, 0, eyeXYZ[0], eyeXYZ[1], eyeXYZ[2], MyGLRenderer.dolphin.centerOfDolphinWorldSpace[0], MyGLRenderer.dolphin.centerOfDolphinWorldSpace[1], MyGLRenderer.dolphin.centerOfDolphinWorldSpace[2], 0f, 1.0f, 0.0f);
                    }
                    break;
                //state 4 = follow and track 3rd person far away
                case 4:
                    offset = 6.0f;
                    offset2 = 10.0f;

                    eyeXYZ[0] = MyGLRenderer.dolphin.centerOfDolphinWorldSpace[0] - MyGLRenderer.dolphin.dolphinNormalWorldSpace[0] * offset;
                    eyeXYZ[1] = MyGLRenderer.dolphin.centerOfDolphinWorldSpace[1] - MyGLRenderer.dolphin.dolphinNormalWorldSpace[1] * offset + yOverhead;
                    eyeXYZ[2] = MyGLRenderer.dolphin.centerOfDolphinWorldSpace[2] - MyGLRenderer.dolphin.dolphinNormalWorldSpace[2] * offset;
                    Matrix.setLookAtM(mViewMatrix, 0, eyeXYZ[0], eyeXYZ[1], eyeXYZ[2], MyGLRenderer.dolphin.centerOfDolphinWorldSpace[0], MyGLRenderer.dolphin.centerOfDolphinWorldSpace[1], MyGLRenderer.dolphin.centerOfDolphinWorldSpace[2], 0f, 1.0f, 0.0f);
                    break;
                //state 5 == firstperson
                case 5:
                    offset = 13.0f;

                    eyeXYZ[0] = MyGLRenderer.dolphin.centerOfDolphinWorldSpace[0] + MyGLRenderer.dolphin.dolphinNormalWorldSpace[0] * 3.0f;
                    eyeXYZ[1] = MyGLRenderer.dolphin.centerOfDolphinWorldSpace[1] + MyGLRenderer.dolphin.dolphinNormalWorldSpace[1] * 3.0f;
                    eyeXYZ[2] = MyGLRenderer.dolphin.centerOfDolphinWorldSpace[2] + MyGLRenderer.dolphin.dolphinNormalWorldSpace[2] * 3.0f;
                    Matrix.setLookAtM(mViewMatrix, 0, eyeXYZ[0], eyeXYZ[1], eyeXYZ[2], MyGLRenderer.dolphin.centerOfDolphinWorldSpace[0] + MyGLRenderer.dolphin.dolphinNormalWorldSpace[0] * offset,MyGLRenderer.dolphin.centerOfDolphinWorldSpace[1] + MyGLRenderer.dolphin.dolphinNormalWorldSpace[1] * offset, MyGLRenderer.dolphin.centerOfDolphinWorldSpace[2] + MyGLRenderer.dolphin.dolphinNormalWorldSpace[2] * offset, 0f, 1.0f, 0.0f);
                    break;
                default:
                    break;
            }
        }

        //follow cube
        else {
            switch (state) {
                //state 0 = top corner looking centre
                case 0:
                    eyeXYZ[0] = 35.0f;
                    eyeXYZ[1] = 35.0f;
                    eyeXYZ[2] = 35.0f;
                    Matrix.setLookAtM(mViewMatrix, 0, eyeXYZ[0], eyeXYZ[1], eyeXYZ[2], 0f, 0f, 0.0f, 0f, 1.0f, 0.0f);
                    break;
                //state 1 = bottom corner tracking
                case 1:
                    eyeXYZ[0] = -35.0f;
                    eyeXYZ[1] = -35.0f;
                    eyeXYZ[2] = -35.0f;
                    Matrix.setLookAtM(mViewMatrix, 0, eyeXYZ[0], eyeXYZ[1], eyeXYZ[2], MyGLRenderer.centerOfCubeWorldSpace[0], MyGLRenderer.centerOfCubeWorldSpace[1], MyGLRenderer.centerOfCubeWorldSpace[2], 0f, 1.0f, 0.0f);
                    break;
                // state 2 = centre tracking
                case 2:
                    eyeXYZ[0] = 1.0f;
                    eyeXYZ[1] = 1.0f;
                    eyeXYZ[2] = 1.0f;
                    Matrix.setLookAtM(mViewMatrix, 0, eyeXYZ[0], eyeXYZ[1], eyeXYZ[2], MyGLRenderer.centerOfCubeWorldSpace[0], MyGLRenderer.centerOfCubeWorldSpace[1], MyGLRenderer.centerOfCubeWorldSpace[2], 0f, 1.0f, 0.0f);
                    break;
                //state 3 = follow and track 3rd person
                case 3:
                    offset = 4.0f;
                    float speedOfCamera = MyGLRenderer.getDolphinSpeed() + 0.2f;

                    if (!cubeReached && !vectorReached) {
                        float vectorX = MyGLRenderer.centerOfCubeWorldSpace[0] - MyGLRenderer.mCubeNormalVectorWorldSpace[0] * 7.0f;
                        float vectorY = MyGLRenderer.centerOfCubeWorldSpace[1] - MyGLRenderer.mCubeNormalVectorWorldSpace[1] * 7.0f + yOverhead;
                        float vectorZ = MyGLRenderer.centerOfCubeWorldSpace[2] - MyGLRenderer.mCubeNormalVectorWorldSpace[2] * 7.0f;
                        if (MyGLRenderer.moveObject(eyeXYZ, vectorX, vectorY, vectorZ, speedOfCamera)) {
                            vectorReached = true;
                        }
                        Matrix.setLookAtM(mViewMatrix, 0, eyeXYZ[0], eyeXYZ[1], eyeXYZ[2], MyGLRenderer.centerOfCubeWorldSpace[0], MyGLRenderer.centerOfCubeWorldSpace[1], MyGLRenderer.centerOfCubeWorldSpace[2], 0f, 1.0f, 0.0f);
                    }
                    if (!cubeReached && vectorReached) {
                        float cubeDestX = MyGLRenderer.centerOfCubeWorldSpace[0] - MyGLRenderer.mCubeNormalVectorWorldSpace[0] * offset;
                        float cubeDestY = MyGLRenderer.centerOfCubeWorldSpace[1] - MyGLRenderer.mCubeNormalVectorWorldSpace[1] * offset + yOverhead;
                        float cubeDestZ = MyGLRenderer.centerOfCubeWorldSpace[2] - MyGLRenderer.mCubeNormalVectorWorldSpace[2] * offset;
                        if (MyGLRenderer.moveObject(eyeXYZ, cubeDestX, cubeDestY, cubeDestZ, speedOfCamera)) {
                            cubeReached = true;
                        }
                        Matrix.setLookAtM(mViewMatrix, 0, eyeXYZ[0], eyeXYZ[1], eyeXYZ[2], MyGLRenderer.centerOfCubeWorldSpace[0], MyGLRenderer.centerOfCubeWorldSpace[1], MyGLRenderer.centerOfCubeWorldSpace[2], 0f, 1.0f, 0.0f);
                    }
                    if (cubeReached) {
                        eyeXYZ[0] = MyGLRenderer.centerOfCubeWorldSpace[0] - MyGLRenderer.mCubeNormalVectorWorldSpace[0] * offset;
                        eyeXYZ[1] = MyGLRenderer.centerOfCubeWorldSpace[1] - MyGLRenderer.mCubeNormalVectorWorldSpace[1] * offset + yOverhead;
                        eyeXYZ[2] = MyGLRenderer.centerOfCubeWorldSpace[2] - MyGLRenderer.mCubeNormalVectorWorldSpace[2] * offset;
                        Matrix.setLookAtM(mViewMatrix, 0, eyeXYZ[0], eyeXYZ[1], eyeXYZ[2], MyGLRenderer.centerOfCubeWorldSpace[0], MyGLRenderer.centerOfCubeWorldSpace[1], MyGLRenderer.centerOfCubeWorldSpace[2], 0f, 1.0f, 0.0f);
                    }
                    break;
                //state 4 = follow and track 3rd person far away
                case 4:
                    offset = 6.0f;
                    offset2 = 10.0f;

                    eyeXYZ[0] = MyGLRenderer.centerOfCubeWorldSpace[0] - MyGLRenderer.mCubeNormalVectorWorldSpace[0] * offset;
                    eyeXYZ[1] = MyGLRenderer.centerOfCubeWorldSpace[1] - MyGLRenderer.mCubeNormalVectorWorldSpace[1] * offset + yOverhead;
                    eyeXYZ[2] = MyGLRenderer.centerOfCubeWorldSpace[2] - MyGLRenderer.mCubeNormalVectorWorldSpace[2] * offset;
                    Matrix.setLookAtM(mViewMatrix, 0, eyeXYZ[0], eyeXYZ[1], eyeXYZ[2], MyGLRenderer.centerOfCubeWorldSpace[0] + MyGLRenderer.mCubeNormalVectorWorldSpace[0] * offset2, MyGLRenderer.centerOfCubeWorldSpace[1] + MyGLRenderer.mCubeNormalVectorWorldSpace[1] * offset2, MyGLRenderer.centerOfCubeWorldSpace[2] + MyGLRenderer.mCubeNormalVectorWorldSpace[2] * offset2, 0f, 1.0f, 0.0f);
                    break;
                //state 5 == firstperson
                case 5:
                    offset = 10.0f;

                    eyeXYZ[0] = MyGLRenderer.centerOfCubeWorldSpace[0] + MyGLRenderer.mCubeNormalVectorWorldSpace[0] * 1.2f;
                    eyeXYZ[1] = MyGLRenderer.centerOfCubeWorldSpace[1] + MyGLRenderer.mCubeNormalVectorWorldSpace[1] * 1.2f;
                    eyeXYZ[2] = MyGLRenderer.centerOfCubeWorldSpace[2] + MyGLRenderer.mCubeNormalVectorWorldSpace[2] * 1.2f;
                    Matrix.setLookAtM(mViewMatrix, 0, eyeXYZ[0], eyeXYZ[1], eyeXYZ[2], MyGLRenderer.centerOfCubeWorldSpace[0] + MyGLRenderer.mCubeNormalVectorWorldSpace[0] * offset, MyGLRenderer.centerOfCubeWorldSpace[1] + MyGLRenderer.mCubeNormalVectorWorldSpace[1] * offset, MyGLRenderer.centerOfCubeWorldSpace[2] + MyGLRenderer.mCubeNormalVectorWorldSpace[2] * offset, 0f, 1.0f, 0.0f);
                    break;
                default:
                    break;
            }
        }
        return mViewMatrix;
    }
}
