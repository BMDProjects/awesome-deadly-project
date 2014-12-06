package com.example.android.opengl;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;
import android.content.Context;
import java.lang.Math;

public class MyGLRenderer implements GLSurfaceView.Renderer {
    Context mContext;

    private static final String TAG = "MyGLRenderer";
    private Cube   mCube;
    private Cube   mCube2;
    private Room   bigRoom;
    private Cube   centreCube;

    //has to be public because cube has to access it
    public static int amountOfLights = 6;
    public static Light light[] = new Light[6];

    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    public static final float[] mProjectionMatrix = new float[16];
    public static final float[] mViewMatrix = new float[16];
    private final float[] mRotationMatrix= new float[16];

    private final float[] centerOfCubeModelSpace = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
    private final float[] centerOfCubeEyeSpace = new float[4];
    private final float[] centerOfCubeWorldSpace = new float[4];
    final int pointsOnJourney = 100;

    private float [] [] theJourney = new float [pointsOnJourney][3];
    private short coord = 0;


    public void setXzCurrAngle(float xzCurrAngle) {
        this.xzCurrAngle = xzCurrAngle;
    }
    public void setXyCurrAngle(float xyCurrAngle) {
        this.xyCurrAngle = xyCurrAngle;
    }

    float xzCurrAngle = 0.0f;
    float xyCurrAngle = 0.0f;

    // double xzDestAngle = 0.0f;
    // double xyDestAngle = 0.0f;
    boolean firstTime = true;

    boolean reachedAngle = false;

    enum CameraState { CORNER, CENTRE, FIRSTPERSON, THIRDPERSON};
    private CameraState cameraState;

    boolean arrayOfStates[] = new boolean[4];
    volatile int state = 0;

    final int amountOfStates = 6;
    //state 0 = top corner looking centre
    //state 1 = bottom corner tracking
    // state 2 = centre tracking
    //state 3 = follow and track 3rd person
    //state 4 = follow and track 3rd person far away
    //state 5 == firstperson

    public void changeCameraState() {
        state++;
        cubeReached = false;
        if (state >= amountOfStates && !touched) state = 0;
    }

    public volatile boolean touched = false;

    boolean cubeReached = false;

    float theta = 0.0f;
    float speed = 0.01f;
    float radius = 3.0f;

    float eyeX = 0.0f;
    float eyeY = 0.0f;
    float eyeZ = 0.0f;

    private final float[] eyeXYZ = new float[] {0.0f, 0.0f, 0.0f, 1.0f};;

    final float lookX = 0.0f;
    final float lookY = 0.0f;
    final float lookZ = 0.0f;

    final float upX = 0.0f;
    final float upY = 1.0f;
    final float upZ = 0.0f;

    //private float[] mTempMatrix = new float[16];

    private float mAngle;
    float x = 0.0f;
    float y = 0.0f;

    boolean reachedDestination = false;

    MyGLRenderer(Context context) {
        super();
        mContext = context;
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        //GLES20.glEnable(GLES20.GL_BLEND);
        // GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);B
        //GLES20.glEnable(GLES20.GL_NORMALIZE);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
       // GLES20.glEnable(GLES20.GL_CULL_FACE);
        //unused.glEnable(GL_RESCALE_NORMAL);
        GLES20.glEnable(GLES20.GL_VERTEX_ATTRIB_ARRAY_NORMALIZED);
        // GLES20.glLightModeli(GL_LIGHT_MODEL_TWO_SIDE, GL_TRUE);
        // unused.glLightModeli(GL_LIGHT_MODEL_TWO_SIDE, GL_TRUE);

        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        mCube   = new Cube();
        mCube2 = new Cube();
        bigRoom = new Room();
        centreCube = new Cube();

        for (int i = 0; i < amountOfLights; i++) {
            light[i] = new Light();
        }

        /*theJourney[0][0] = 15f;
        theJourney[0][1] = -10f;
        theJourney[0][2] = -10f;
        theJourney[1][0] = -10f;
        theJourney[1][1] = 10f;
        theJourney[1][2] = 10f;
        theJourney[2][0] = 25f;
        theJourney[2][1] = 0f;
        theJourney[2][2] = 0;*/

        for(int i = 0; i < pointsOnJourney; i++){
            for(int j = 0; j < 3; j++){
                //theJourney[i][j] = (float)(Math.random()*50) - 25.0f;
                theJourney[i][j] = (float)(Math.random()*90) - 45.0f;
                if (theJourney[i][j] <= 20 && theJourney[i][j] >= 0) theJourney[i][j] += 21.0f;
                else if (theJourney[i][j] >= -20 && theJourney[i][j] <= 0) theJourney[i][j] -= 21.0f;
            }
        }
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        float[] sqScratch = new float[16];

        // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        theta += speed;
        if(radius > 6.0f) radius = radius - speed;
        else radius = radius + speed;

        touched = false;

        boolean reachedDestination2 = false;
       /* reachedDestination2 = (moveObject(eyeXYZ, theJourney[coord][0] + 2.0f, theJourney[coord][1] + 2.0f, theJourney[coord][2], 0.1f));
        if(reachedDestination2){
            reachedDestination2 = false;
            //coord++;
            if(coord > pointsOnJourney - 1) coord = 0;
        }*/
        //eyeX = (float)(3.0 * Math.cos(theta)); //+ circle.cx;
        //eyeZ = (float)(radius * Math.sin(theta)); //+ circle.cy;
        // eyeY = (float)(radius * Math.cos(theta));

        reachedDestination = moveObject(centerOfCubeWorldSpace, theJourney[coord][0], theJourney[coord][1], theJourney[coord][2], 0.1f);
        if(reachedDestination){
            reachedDestination = false;
            reachedAngle = false;
            firstTime = true;
            coord++;
            if(coord > pointsOnJourney - 1) coord = 0;
        }

        //state 0 = top corner looking centre
        //state 1 = bottom corner tracking
        // state 2 = centre tracking
        //state 3 = follow and track 3rd person
        //state 4 = follow and track 3rd person far away
        //state 5 == firstperson
        if (state == 0) {
            eyeZ = 35.0f;
            eyeX = 35.0f;
            eyeY = 35.0f;
            Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, 0f, 0f, 0.0f, 0f, 1.0f, 0.0f);
        }
        else if (state == 1) {
            eyeZ = -35.0f;
            eyeX = -35.0f;
            eyeY = -35.0f;
            Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, centerOfCubeWorldSpace[0], centerOfCubeWorldSpace[1], centerOfCubeWorldSpace[2], 0f, 1.0f, 0.0f);
        }
        else if (state == 2) {
            eyeZ = 0.0f;
            eyeX = 0.0f;
            eyeY = 0.0f;
            Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, centerOfCubeWorldSpace[0], centerOfCubeWorldSpace[1], centerOfCubeWorldSpace[2], 0f, 1.0f, 0.0f);
        }
        else if (state == 3) {
            float offset = 2.0f;
            float offset2 = 2.5f;

            float distanceFromObect = distance(0, 0, 0, offset, offset, offset);
            if (distance(centerOfCubeWorldSpace[0], centerOfCubeWorldSpace[1], centerOfCubeWorldSpace[2], eyeXYZ[0], eyeXYZ[1], eyeXYZ[2]) > distanceFromObect && !cubeReached) {
                moveObject(eyeXYZ, centerOfCubeWorldSpace[0] + offset, centerOfCubeWorldSpace[1] + offset, centerOfCubeWorldSpace[2] + offset, 0.25f);
                Matrix.setLookAtM(mViewMatrix, 0, eyeXYZ[0], eyeXYZ[1], eyeXYZ[2], centerOfCubeWorldSpace[0], centerOfCubeWorldSpace[1], centerOfCubeWorldSpace[2], 0f, 1.0f, 0.0f);
            }
            if (distance(centerOfCubeWorldSpace[0], centerOfCubeWorldSpace[1], centerOfCubeWorldSpace[2], eyeXYZ[0], eyeXYZ[1], eyeXYZ[2]) < distanceFromObect && !cubeReached) {
                cubeReached = true;
            }
            if (cubeReached) {
                eyeXYZ[0] = centerOfCubeWorldSpace[0] + offset2;
                eyeXYZ[1] = centerOfCubeWorldSpace[1] + offset2;
                eyeXYZ[2] = centerOfCubeWorldSpace[2] + offset2;
                Matrix.setLookAtM(mViewMatrix, 0, eyeXYZ[0], eyeXYZ[1], eyeXYZ[2], centerOfCubeWorldSpace[0], centerOfCubeWorldSpace[1], centerOfCubeWorldSpace[2], 0f, 1.0f, 0.0f);
            }
        }
        else if (state == 4) {
            float offset = 4.0f;
            float offset2 = 6.0f;
            float distanceFromObect = distance(0, 0, 0, offset, offset, offset);
            if (distance(centerOfCubeWorldSpace[0], centerOfCubeWorldSpace[1], centerOfCubeWorldSpace[2], eyeXYZ[0], eyeXYZ[1], eyeXYZ[2]) > distanceFromObect && !cubeReached) {
                moveObject(eyeXYZ, centerOfCubeWorldSpace[0] + offset, centerOfCubeWorldSpace[1] + offset, centerOfCubeWorldSpace[2] + offset, 0.25f);
                Matrix.setLookAtM(mViewMatrix, 0, eyeXYZ[0], eyeXYZ[1], eyeXYZ[2], centerOfCubeWorldSpace[0], centerOfCubeWorldSpace[1], centerOfCubeWorldSpace[2], 0f, 1.0f, 0.0f);
            }
            if (distance(centerOfCubeWorldSpace[0], centerOfCubeWorldSpace[1], centerOfCubeWorldSpace[2], eyeXYZ[0], eyeXYZ[1], eyeXYZ[2]) < distanceFromObect && !cubeReached) {
                cubeReached = true;
            }
            if (cubeReached) {
                eyeXYZ[0] = centerOfCubeWorldSpace[0] + offset2;
                eyeXYZ[1] = centerOfCubeWorldSpace[1] + offset2;
                eyeXYZ[2] = centerOfCubeWorldSpace[2] + offset2;
                Matrix.setLookAtM(mViewMatrix, 0, eyeXYZ[0], eyeXYZ[1], eyeXYZ[2], centerOfCubeWorldSpace[0], centerOfCubeWorldSpace[1], centerOfCubeWorldSpace[2], 0f, 1.0f, 0.0f);
            }
        }
        else if (state == 5) {
            float offset = 2.0f;
            float offset2 = 2.5f;
            float distanceFromObect = distance(0, 0, 0, offset, offset, offset);
            if (distance(centerOfCubeWorldSpace[0], centerOfCubeWorldSpace[1], centerOfCubeWorldSpace[2], eyeXYZ[0], eyeXYZ[1], eyeXYZ[2]) > distanceFromObect && !cubeReached) {
                moveObject(eyeXYZ, centerOfCubeWorldSpace[0] + offset, centerOfCubeWorldSpace[1] + offset, centerOfCubeWorldSpace[2] + offset, 0.25f);
                Matrix.setLookAtM(mViewMatrix, 0, eyeXYZ[0], eyeXYZ[1], eyeXYZ[2], centerOfCubeWorldSpace[0], centerOfCubeWorldSpace[1], centerOfCubeWorldSpace[2], 0f, 1.0f, 0.0f);
            }
            if (distance(centerOfCubeWorldSpace[0], centerOfCubeWorldSpace[1], centerOfCubeWorldSpace[2], eyeXYZ[0], eyeXYZ[1], eyeXYZ[2]) < distanceFromObect && !cubeReached) {
                cubeReached = true;
            }
            if (cubeReached) {
                eyeXYZ[0] = centerOfCubeWorldSpace[0];
                eyeXYZ[1] = centerOfCubeWorldSpace[1];
                eyeXYZ[2] = centerOfCubeWorldSpace[2] + 3.0f;
                Matrix.setLookAtM(mViewMatrix, 0, eyeXYZ[0], eyeXYZ[1], eyeXYZ[2], centerOfCubeWorldSpace[0], centerOfCubeWorldSpace[1], centerOfCubeWorldSpace[2], 0f, 1.0f, 0.0f);
            }
        }
        // Matrix.setIdentityM(mViewMatrix, 0);
        //  Matrix.setLookAtM(mViewMatrix, 0, eyeXYZ[0], eyeXYZ[1], eyeXYZ[2], 0.0f, 0.0f, 0.0f, 0f, 1.0f, 0.0f);
        //Matrix.setLookAtM(mViewMatrix, 0, eyeXYZ[0], eyeXYZ[1], eyeXYZ[2],  centerOfCubeWorldSpace[0], centerOfCubeWorldSpace[1], centerOfCubeWorldSpace[2], 0f, 1.0f, 0.0f);
        //eyeXYZ[0] += speed;
        // Use the following code to generate constant rotation.
        // Leave this code out when using TouchEvents.

        //float sqAngle = 30.0f;
        //USE THIS INSTEAD FOR ANGLE ------------------------------------------------------
        long time = SystemClock.uptimeMillis() % 10000L;
        float sqAngle = (360.0f / 10000.0f) * ((int) time);


        // Calculate position of the light[0]. Coordinate location.
        Matrix.setIdentityM(light[0].mLightModelMatrix, 0);
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
        light[5].drawLight(mMVPMatrix);



        Matrix.setIdentityM(sqScratch, 0);
        Matrix.setIdentityM(mMVPMatrix, 0);

        Matrix.translateM(sqScratch, 0, centerOfCubeWorldSpace[0], centerOfCubeWorldSpace[1], centerOfCubeWorldSpace[2]);

     /*   if (firstTime) {
            double[] angles = new double[2];
            angles = returnAngles( centerOfCubeWorldSpace[0], centerOfCubeWorldSpace[1], centerOfCubeWorldSpace[2], theJourney[coord][0], theJourney[coord][1], theJourney[coord][2]);
            xzDestAngle = angles[1];

            firstTime = false;
        }*/

        if (!reachedAngle) {
            //rotateObject(centerOfCubeWorldSpace, sqScratch, theJourney[coord][0], theJourney[coord][1], theJourney[coord][2], 0.3f, xzCurrAngle, xyCurrAngle);
        }
        else {
            int i = 5;
        }

        // rotateObject(centerOfCubeWorldSpace, sqScratch, -25.0f, 25.0f, -45.0f, 0.4f, xzCurrAngle, xyCurrAngle);



        //Matrix.setRotateM(rotationMatrix, 0, sqAngle, 0.0f, 1.0f, 0.0f);
        // Matrix.multiplyMM(sqScratch, 0,rotationMatrix , 0,sqScratch , 0);
        Matrix.rotateM(sqScratch, 0 , xzCurrAngle, 0.0f, 1.0f, 0.0f);

        Matrix.multiplyMV(centerOfCubeWorldSpace , 0, sqScratch, 0, centerOfCubeModelSpace , 0);
        Matrix.multiplyMV(centerOfCubeEyeSpace , 0, mViewMatrix, 0, centerOfCubeWorldSpace , 0);

        mCube.draw5(sqScratch, 1.0f);
        //mCube.draw3(sqScratch, 1.0f);
        //  Matrix.multiplyMV(mLightPosInWorldSpace, 0, sqScratch, 0, mLightPosInModelSpace, 0);
        // Matrix.multiplyMV(mLightPosInEyeSpace, 0, mViewMatrix, 0, mLightPosInWorldSpace, 0);

        //cube 2
        // float[] scale_matrix = new float[16];
        Matrix.setIdentityM(sqScratch, 0);
        /*Matrix.setIdentityM(scale_matrix, 0);
        float[] scale = {50.0f,50.0f,50.0f};
        Matrix.scaleM(scale_matrix, 0, scale[0], scale[1], scale[2]);

        Matrix.multiplyMM(sqScratch, 0, sqScratch , 0, scale_matrix, 0);*/
        // Matrix.translateM(sqScratch,  0, 0.0f, 0.0f, 0.0f);
        // Matrix.multiplyMM(mMVPMatrix, 0, sqScratch, 0, mViewMatrix, 0);
        //  Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        bigRoom.drawRoom(sqScratch, 50.0f);


        //cube 3
       /* Matrix.setIdentityM(sqScratch, 0);
        Matrix.setIdentityM(mMVPMatrix, 0);
        Matrix.translateM(sqScratch,  0, 0.0f, 0.0f, 0.0f);


        Matrix.setRotateM(mRotationMatrix, 0, sqAngle, 0.5f, 0.3f, 1.0f);
        //Matrix.multiplyMM(mMVPMatrix, 0, mMVPMatrix, 0, mRotationMatrix, 0);
        Matrix.multiplyMM(sqScratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
        mCube3.draw3(sqScratch, 0.5f);*/

        //Matrix.setIdentityM(sqScratch, 0);
        // Matrix.setIdentityM(mMVPMatrix, 0);
        //centreCube.draw5(sqScratch, 20.0f);
    }

    private static boolean moveObject(float coordinates[], float x, float y, float z, float speed) {
        float epsilon = 0.5f;
        double [] angles = new double [2];
        angles = returnAngles(coordinates[0], coordinates[1], coordinates[2], x, y, z);

        coordinates[0] += speed*Math.cos(angles[0]);
        coordinates[1] += speed*Math.sin(angles[0]);
        coordinates[2] += speed*Math.sin(angles[1]);

        if(approxEqual(coordinates[0], x, epsilon) && approxEqual(coordinates[1], y, epsilon)
                && approxEqual(coordinates[2], z, epsilon)) return true;

        return false;
    }

    private boolean rotateObject(float coordinates[],float[] modelMatrix, float destX, float destY, float destZ, float angularSpeed, float xzCurrAngle, float xyCurrAngle) {
        float epsilon = angularSpeed;
        //double xzDestAngle;
        //double xyDestAngle;

        double[] angles = new double[2];
        angles = returnAngles(coordinates[0], coordinates[1], coordinates[2], destX, destY, destZ);
        double xzDestAngle = angles[1];


        //Making a global angle gamma that which will be the amount it has to turn each time (will be the incremented value
        //angles are the angles between object and destination
        //angles2 are the angles between object and x axis, say 50.0

        // double[] angles2 = new double[2];

        //xyz 1 = end of x-axis: xyz 2 = object
        //angles2 = returnAngles(50.0f, 0.0f, 0.0f, coordinates[0], coordinates[1], coordinates[2]);

        //xzDestAngle = angles2[1] - angles[1];
        // xyDestAngle = angles2[1] - angles[1];


        //Now turn the amount between these angles (positive rotation assumed clockwise, otherwise change to angles - angles2
        //alpha is now angle to reach, delat is amount to rotate image, if they're equal return true :)
        //Assuming rotation is clockwise

        //Check quadrants
        //Alpha say xy angle
        //Change cur an
        if (approxEqual(xzCurrAngle,xzDestAngle, epsilon)) {
            return true;
        }
        else {
            //if (xzCurrAngle >= Math.) {
            //    xzCurrAngle = 0.0f;
            // }
            //if(Math.abs(xzCurrAngle - xzDestAngle) < 3.14) {
            //    angularSpeed *= -1;
            // }
            xzCurrAngle += angularSpeed;
            xzCurrAngle = xzCurrAngle % 6.28f;
            setXzCurrAngle(xzCurrAngle);
        }

        /*if ((beta >= 0 && beta <= Math.PI/2) || (Math.PI/2 >= 0 && beta <= Math.PI)) {
            if(destX > coordinates[0]) {
                alpha += angularSpeed;
                beta = angles[1];
            }
            else {
                alpha -= angularSpeed;
                beta = angles[1];
            }
        }
        else{
            if(destX > coordinates[0]) {
                alpha += angularSpeed;
                beta = angles[1];
            }
            else {
                alpha -= angularSpeed;
                beta = angles[1];
            }
        }*/

        /*else if(Math.PI/2 >= 0 && beta <= Math.PI){
            if(destX > coordinates[0]) {
                alpha += angularSpeed;
                beta = angles[1];
            }
            else {
                alpha -= angularSpeed;
                beta = angles[1];
            }
        }*/

        //float[] sqScratch = new float[16];
        //Matrix.RotateM(modelMatrix, 0 , xzCurrAngle, 0.0f, 1.0f, 0.0f);
        //Matrix.RotateM(modelMatrix, 0 , 0.0f, 0.0f, 1.0f, 0.0f);

        return false;
    }

    private static double[] returnAngles(float x1, float y1, float z1, float x2, float y2, float z2)
    {
        float deltax = Math.abs((x2-x1));
        float deltay = Math.abs((y2-y1));
        float deltaz = Math.abs((z2-z1));

        if (deltax == 0.0) deltax = 0.0001f;

        float xySlope = deltay/deltax;
        float xzSlope = deltaz/deltax;

        double [] angles = new double [2];
        angles[0] = Math.atan((double)xySlope);
        angles[1] = Math.atan((double)xzSlope);

        if(x2 >= x1 && y2 >= y1);
        else if(x2 >= x1 && y2 < y1) angles[0] *= -1;
        else if(x2 <= x1 && y2 < y1) angles[0] = Math.PI + angles[0];
        else if(x2 < x1 && y2 >= y1) angles[0] = Math.PI - angles[0];

        if(x2 >= x1 && z2 >= z1);
        else if(x2 >= x1 && z2 < z1) angles[1] *= -1;
        else if(x2 <= x1 && z2 < z1) angles[1] = Math.PI + angles[1];
        else if(x2 < x1 && z2 >= z1) angles[1] = Math.PI - angles[1];
        return angles;
    }

    public static float distance(float x1, float y1, float z1, float x2, float y2, float z2) {
        return (float)Math.sqrt(
                (x2 - x1)*(x2 - x1)
                        + (y2 - y1)*(y2 - y1)
                        + (z2 - z1)*(z2 - z1) );
    }

    public static boolean approxEqual(float x, float y, float eps){
        return Math.abs(x-y)<eps;
    }

    public static boolean approxEqual(double x, double y, double eps){
        return Math.abs(x-y)<eps;
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
        final float far = 160.0f;

        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
        // Matrix.frustumM(mProjectionMatrix, 0, left, right, -1, 1, 1, 100);
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

    public static int createAndLinkProgram(final int vertexShaderHandle, final int fragmentShaderHandle, final String[] attributes)
    {
        int programHandle = GLES20.glCreateProgram();

        if (programHandle != 0)
        {
            // Bind the vertex shader to the program.
            GLES20.glAttachShader(programHandle, vertexShaderHandle);

            // Bind the fragment shader to the program.
            GLES20.glAttachShader(programHandle, fragmentShaderHandle);

            // Bind attributes
            if (attributes != null)
            {
                final int size = attributes.length;
                for (int i = 0; i < size; i++)
                {
                    GLES20.glBindAttribLocation(programHandle, i, attributes[i]);
                }
            }

            // Link the two shaders together into a program.
            GLES20.glLinkProgram(programHandle);

            // Get the link status.
            final int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);

            // If the link failed, delete the program.
            if (linkStatus[0] == 0)
            {
                Log.e(TAG, "Error compiling program: " + GLES20.glGetProgramInfoLog(programHandle));
                GLES20.glDeleteProgram(programHandle);
                programHandle = 0;
            }
        }

        if (programHandle == 0)
        {
            throw new RuntimeException("Error creating program.");
        }

        return programHandle;
    }
}