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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.Math;

public class MyGLRenderer implements GLSurfaceView.Renderer {
    private static final String TAG = "MyGLRenderer";
    private final Context mContext;

    private OBJParser parser;

    public static Camera camera;
    private Cube   mCube;
    private Cube   mCube2;
    private Room   bigRoom;
    static public Dolphin dolphin;

    //has to be public because cube has to access it
    public static int amountOfLights = 6;
    public static Light light[] = new Light[6];

    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    public static final float[] mProjectionMatrix = new float[16];
    public static float[] mViewMatrix = new float[16];
    private final float[] mRotationMatrix= new float[16];
    private final float[] mCurrentRotation= new float[16];
    private final float[] mAccumulatedRotation = new float[16];
    private final float[] mAccumulatedRotation2 = new float[16];
    private final float[] mAccumulatedRotation3 = new float[16];
    private final float[] mAccumulatedRotation4 = new float[16];
    private final float[] mTemporaryRotation= new float[16];

    public float[] centerOfCubeModelSpace = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
    public float[] centerOfCubeEyeSpace = new float[4];
    public static float[] centerOfCubeWorldSpace = new float[4];
    public static float[] mCubeNormalVectorModelSpace = new float[] {1.0f, 0.0f, 0.0f, 1.0f};
    public static float[] mCubeNormalVectorWorldSpace = new float[4];

    final int pointsOnJourney = 40;
    private float [] [] theJourney = new float [pointsOnJourney][3];
    private short coord = 0;
    private float [] [] dolphinJourney = new float [pointsOnJourney][3];
    private short coord2 = 0;

    public volatile static float xzCurrAngle1 = 0.0f;
    public volatile static float xyCurrAngle1 = 0.0f;
    public volatile static float xzCurrAngle2 = 0.0f;
    public volatile static float xyCurrAngle2 = 0.0f;

    float xzPrevAngle = 0.0f;
    float xzDestAngle = 0.0f;
    float xzAccu = 0.0f;
    float xzDifference = 0.0f;
    boolean reachedAngle = false;
    boolean firstTime = true;

    public volatile boolean rotatingUp = false;
    public volatile boolean rotatingDown = false;
    public volatile boolean rotatingLeft = false;
    public volatile boolean rotatingRight = false;

    static boolean control = false;
    static boolean dolphinFollow = false;

    public static float getDolphinSpeed() {
        return dolphinSpeed;
    }

    public static void setDolphinSpeed(float dolphinSpeed_p){ dolphinSpeed = dolphinSpeed_p;}
    private static float dolphinSpeed = 0.1f;

    boolean reachedDestination = false;
    boolean reachedDestination2 = false;

    MyGLRenderer(Context context) {
        super();
        mContext = context;
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        //GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        camera = new Camera();
        mCube   = new Cube();
        mCube2 = new Cube();
        bigRoom = new Room(mContext);
        parser = new OBJParser(mContext);
        dolphin = parser.parseOBJ(R.raw.fulltexturedolphin);

        centerOfCubeWorldSpace[0] = 0.0f;
        centerOfCubeWorldSpace[1] = 0.0f;
        centerOfCubeWorldSpace[2] = 0.0f;

        Matrix.setIdentityM(mAccumulatedRotation, 0);
        Matrix.setIdentityM(mAccumulatedRotation2, 0);
        Matrix.setIdentityM(mAccumulatedRotation3, 0);
        Matrix.setIdentityM(mAccumulatedRotation4, 0);

        for (int i = 0; i < amountOfLights; i++) {
            light[i] = new Light();
        }

        for(int i = 0; i < pointsOnJourney; i++){
            for(int j = 0; j < 3; j++){
                //if (j == 0) theJourney[i][j] = 0.0f;
                //else
                theJourney[i][j] = (float)(Math.random()*90) - 45.0f;
            }
        }

        for(int i = 0; i < pointsOnJourney; i++){
            for(int j = 0; j < 3; j++){
                //if (j == 0) theJourney[i][j] = 0.0f;
                //else
                dolphinJourney[i][j] = (float)(Math.random()*75) - 45.0f;
            }
        }
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        float[] sqScratch = new float[16];
        float[] sqScratch2 = new float[16];

        // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        if (!control) {
            reachedDestination = moveObject(centerOfCubeWorldSpace, theJourney[coord][0], theJourney[coord][1], theJourney[coord][2], dolphinSpeed);
            if (reachedDestination) {
                reachedDestination = false;
                reachedAngle = false;
                firstTime = true;
                xzPrevAngle = xzDestAngle;
                coord++;
                if (coord > pointsOnJourney - 1) coord = 0;
            }


            reachedDestination2 = moveObject(dolphin.centerOfDolphinWorldSpace, dolphinJourney[coord2][0], dolphinJourney[coord2][1], dolphinJourney[coord2][2], dolphinSpeed);
            if (reachedDestination2) {
                reachedDestination2 = false;
                reachedAngle = false;
                firstTime = true;
                coord2++;
                if (coord2 > pointsOnJourney - 1) coord2 = 0;
            }
        }

        if (control) {
            if (!dolphinFollow) {
                float hypotenuse = (float) Math.sqrt(mCubeNormalVectorWorldSpace[0] * mCubeNormalVectorWorldSpace[0] + mCubeNormalVectorWorldSpace[1] * mCubeNormalVectorWorldSpace[1] + mCubeNormalVectorWorldSpace[2] * mCubeNormalVectorWorldSpace[2]);
                float movementInX = mCubeNormalVectorWorldSpace[0];
                float movementInY = mCubeNormalVectorWorldSpace[1];
                float movementInZ = mCubeNormalVectorWorldSpace[2];

                centerOfCubeWorldSpace[0] += dolphinSpeed * movementInX;
                centerOfCubeWorldSpace[1] += dolphinSpeed * movementInY;
                centerOfCubeWorldSpace[2] += dolphinSpeed * movementInZ;

                if (centerOfCubeWorldSpace[0] > 49.0f || centerOfCubeWorldSpace[1] > 49.0f || centerOfCubeWorldSpace[2] > 49.0f || centerOfCubeWorldSpace[0] < -49.0f || centerOfCubeWorldSpace[1] < -49.0f || centerOfCubeWorldSpace[2] < -49.0f) {
                    centerOfCubeWorldSpace[0] = 0.0f;
                    centerOfCubeWorldSpace[1] = 0.0f;
                    centerOfCubeWorldSpace[2] = 0.0f;
                }
            }

            else {
                float movementInX = dolphin.dolphinNormalWorldSpace[0];
                float movementInY = dolphin.dolphinNormalWorldSpace[1];
                float movementInZ = dolphin.dolphinNormalWorldSpace[2];

                dolphin.centerOfDolphinWorldSpace[0] += dolphinSpeed * movementInX;
                dolphin.centerOfDolphinWorldSpace[1] += dolphinSpeed * movementInY;
                dolphin.centerOfDolphinWorldSpace[2] += dolphinSpeed * movementInZ;

                if (dolphin.centerOfDolphinWorldSpace[0] > 46.0f || dolphin.centerOfDolphinWorldSpace[1] > 46.0f || dolphin.centerOfDolphinWorldSpace[2] > 46.0f || dolphin.centerOfDolphinWorldSpace[0] < -46.0f || dolphin.centerOfDolphinWorldSpace[1] < -46.0f || dolphin.centerOfDolphinWorldSpace[2] < -46.0f) {
                    dolphin.centerOfDolphinWorldSpace[0] = 0.0f;
                    dolphin.centerOfDolphinWorldSpace[1] = 0.0f;
                    dolphin.centerOfDolphinWorldSpace[2] = 0.0f;
                }
            }
            final float angularSpeed = 0.5f;
            if (rotatingUp) {
                xyCurrAngle1 -= angularSpeed;
                xyCurrAngle2 -= angularSpeed;
            }
            if (rotatingDown) {
                xyCurrAngle1 += angularSpeed;
                xyCurrAngle2 += angularSpeed;
            }
            if (rotatingLeft) {
                xzCurrAngle1 += angularSpeed;
                xzCurrAngle2 += angularSpeed;
            }
            if (rotatingRight) {
                xzCurrAngle1 -= angularSpeed;
                xzCurrAngle2 -= angularSpeed;
            }
        }

        mViewMatrix = camera.returnViewMatrix();

        long slowTime = SystemClock.uptimeMillis() % 100000L;
        float sqAngle = (360.0f / 100000.0f) * ((int) slowTime);
        long mediumTime = SystemClock.uptimeMillis() % 10000L;
        float sqAngle2 = (360.0f / 10000.0f) * ((int) mediumTime);
        long FastTime = SystemClock.uptimeMillis() % 5000L;
        float sqAngle3 = (360.0f / 5000.0f) * ((int) FastTime);

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
        Matrix.translateM(light[1].mLightModelMatrix, 0, 0.0f, 0.0f, 0.0f);
        Matrix.rotateM(light[1].mLightModelMatrix, 0, sqAngle3, 0.5f, 1.0f, 0.0f);
        Matrix.translateM(light[1].mLightModelMatrix, 0, 0.0f, 0.0f, 40.0f);

        Matrix.multiplyMV(light[1].mLightPosInWorldSpace, 0, light[1].mLightModelMatrix, 0, light[1].mLightPosInModelSpace, 0);
        Matrix.multiplyMV(light[1].mLightPosInEyeSpace, 0, mViewMatrix, 0, light[1].mLightPosInWorldSpace, 0);

        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, light[1].mLightModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
        light[1].drawLight(mMVPMatrix);

        //light[2]
        Matrix.setIdentityM(light[2].mLightModelMatrix, 0);
        Matrix.setIdentityM(mMVPMatrix, 0);
        Matrix.translateM(light[2].mLightModelMatrix, 0, -30.0f, -30.0f, -30.0f);
        Matrix.rotateM(light[2].mLightModelMatrix, 0, sqAngle2, 0.0f, 1.0f, 0.0f);
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
        Matrix.rotateM(light[4].mLightModelMatrix, 0, sqAngle2, -0.5f, 1.0f, 0.0f);
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

        //all dolphin drawing

        //1st time only to get around translating normal coordinates. Normal always has to be normalized. 1 in length. If you translate it becomes bigger. So trick is to only rotate normal vector.
        Matrix.setIdentityM(sqScratch2, 0);
        Matrix.setIdentityM(sqScratch, 0);
        Matrix.translateM(sqScratch2, 0, 0.0f, 0.0f, 0.0f);
        if (dolphinFollow) {
            Matrix.setIdentityM(mCurrentRotation, 0);
            Matrix.rotateM(mCurrentRotation, 0, xzCurrAngle1, 0.0f, 1.0f, 0.0f);
            Matrix.rotateM(mCurrentRotation, 0, xyCurrAngle1, -1.0f, 0.0f, 0.0f);
            xzCurrAngle1 = 0.0f;
            xyCurrAngle1 = 0.0f;

            Matrix.multiplyMM(mTemporaryRotation, 0, mCurrentRotation, 0, mAccumulatedRotation3, 0);
            System.arraycopy(mTemporaryRotation, 0, mAccumulatedRotation3, 0, 16);

            Matrix.multiplyMM(mTemporaryRotation, 0, sqScratch2, 0, mAccumulatedRotation3, 0);
            System.arraycopy(mTemporaryRotation, 0, sqScratch2, 0, 16);

            Matrix.multiplyMV(dolphin.dolphinNormalWorldSpace, 0, sqScratch2, 0, dolphin.dolphinNormalModelSpace, 0);
        }
        //done again because normalized coordinate has to ONLY BE ROTATED
        Matrix.setIdentityM(dolphin.dolphinModel, 0);
        Matrix.translateM(dolphin.dolphinModel, 0, dolphin.centerOfDolphinWorldSpace[0], dolphin.centerOfDolphinWorldSpace[1], dolphin.centerOfDolphinWorldSpace[2]);
        if (dolphinFollow) {
            Matrix.setIdentityM(mCurrentRotation, 0);

            Matrix.rotateM(mCurrentRotation, 0, xzCurrAngle2, 0.0f, 1.0f, 0.0f);
            Matrix.rotateM(mCurrentRotation, 0, xyCurrAngle2, -1.0f, 0.0f, 0.0f);
            xzCurrAngle2 = 0.0f;
            xyCurrAngle2 = 0.0f;

            //Matrix.rotateM(mCurrentRotation, 0, sqAngle, 0.0f, 1.0f, 0.0f);

            Matrix.multiplyMM(mTemporaryRotation, 0, mCurrentRotation, 0, mAccumulatedRotation4, 0);
            System.arraycopy(mTemporaryRotation, 0, mAccumulatedRotation4, 0, 16);

            Matrix.multiplyMM(mTemporaryRotation, 0, dolphin.dolphinModel, 0, mAccumulatedRotation4, 0);
            System.arraycopy(mTemporaryRotation, 0, dolphin.dolphinModel, 0, 16);
        }
        Matrix.multiplyMV(dolphin.centerOfDolphinWorldSpace , 0, dolphin.dolphinModel, 0, dolphin.centerOfDolphinModelSpace , 0);

        dolphin.drawDolphin(dolphin.dolphinModel, 0.18f);

        /*if (!control) {
            if (firstTime) {
                firstTime = false;
                xzAccu = 0.0f;
                double[] angles = new double[2];
                angles = returnAngles(centerOfCubeWorldSpace[0], centerOfCubeWorldSpace[1], centerOfCubeWorldSpace[2], theJourney[coord][0], theJourney[coord][1], theJourney[coord][2]);
                xzDestAngle = ((float) angles[1]);
                xzDifference = Math.abs(xzDestAngle - xzPrevAngle);
            }
        }*/

        //1st time only to get around translating normal coordinates. Normal always has to be normalized. 1 in length. If you translate it becomes bigger. So trick is to only rotate normal vector.
        Matrix.setIdentityM(sqScratch2, 0);
        Matrix.setIdentityM(sqScratch, 0);
        Matrix.translateM(sqScratch, 0, centerOfCubeWorldSpace[0], centerOfCubeWorldSpace[1], centerOfCubeWorldSpace[2]);

        Matrix.setIdentityM(mCurrentRotation, 0);
        Matrix.rotateM(mCurrentRotation, 0, xzCurrAngle1, 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(mCurrentRotation, 0, xyCurrAngle1, -1.0f, 0.0f, 0.0f);
        xzCurrAngle1 = 0.0f;
        xyCurrAngle1 = 0.0f;

        Matrix.multiplyMM(mTemporaryRotation, 0, mCurrentRotation, 0, mAccumulatedRotation, 0);
        System.arraycopy(mTemporaryRotation, 0, mAccumulatedRotation, 0, 16);

        Matrix.multiplyMM(mTemporaryRotation, 0, sqScratch2, 0, mAccumulatedRotation, 0);
        System.arraycopy(mTemporaryRotation, 0, sqScratch2, 0, 16);

        Matrix.multiplyMV(mCubeNormalVectorWorldSpace, 0, sqScratch2, 0, mCubeNormalVectorModelSpace, 0);

        //done again because normalized coordinate has to ONLY BE ROTATED
        Matrix.setIdentityM(sqScratch, 0);
        Matrix.translateM(sqScratch, 0, centerOfCubeWorldSpace[0], centerOfCubeWorldSpace[1], centerOfCubeWorldSpace[2]);

        Matrix.setIdentityM(mCurrentRotation, 0);

        /*if(!reachedAngle && !control) {
            //float epsilon = 0.05f;
            float angularSpeed = 0.1f;
            if (!approxEqual(xzAccu, xzDifference, angularSpeed)) {
                xzCurrAngle = angularSpeed;
                xzAccu += xzCurrAngle;
            }
            /*if (approxEqual(xyAccu, xyDifference, angularSpeed) && approxEqual(xzAccu, xzDifference, angularSpeed)) {
                reachedAngle = true;
            }*/
        //}*/

        Matrix.rotateM(mCurrentRotation, 0, xzCurrAngle2, 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(mCurrentRotation, 0, xyCurrAngle2, -1.0f, 0.0f, 0.0f);
        xzCurrAngle2 = 0.0f;
        xyCurrAngle2 = 0.0f;

        //Matrix.rotateM(mCurrentRotation, 0, sqAngle, 0.0f, 1.0f, 0.0f);

        Matrix.multiplyMM(mTemporaryRotation, 0, mCurrentRotation, 0, mAccumulatedRotation2, 0);
        System.arraycopy(mTemporaryRotation, 0, mAccumulatedRotation2, 0, 16);

        Matrix.multiplyMM(mTemporaryRotation, 0, sqScratch, 0, mAccumulatedRotation2, 0);
        System.arraycopy(mTemporaryRotation, 0, sqScratch, 0, 16);

        Matrix.multiplyMV(centerOfCubeWorldSpace , 0, sqScratch, 0, centerOfCubeModelSpace , 0);
        Matrix.multiplyMV(centerOfCubeEyeSpace , 0, mViewMatrix, 0, centerOfCubeWorldSpace , 0);

        mCube.draw5(sqScratch, 1.0f);

        //Matrix.multiplyMM(sqScratch, 0, sqScratch2, 0, sqScratch, 0);

        //Matrix.setRotateM(rotationMatrix, 0, sqAngle, 0.0f, 1.0f, 0.0f);
        // Matrix.multiplyMM(sqScratch, 0, rotationMatrix , 0,sqScratch , 0);
        //Matrix.rotateM(sqScratch, 0 , sqAngle2, 0.0f, 1.0f, 0.0f);
        //Matrix.rotateM(sqScratch, 0 , sqAngle, 0.0f, 0.0f, 0.0f);

        //draw room with 50 in scale.
        Matrix.setIdentityM(sqScratch, 0);
        bigRoom.drawRoom(sqScratch, 50.0f);
    }

    public static boolean moveObject(float coordinates[], float x, float y, float z, float speed) {
        float epsilon = speed;
        double [] angles = new double [2];
        angles = returnAngles(coordinates[0], coordinates[1], coordinates[2], x, y, z);

        if(approxEqual(coordinates[0], x, epsilon) && approxEqual(coordinates[1], y, epsilon)
                && approxEqual(coordinates[2], z, epsilon)) return true;

        coordinates[0] += speed * Math.cos(angles[0]);
        coordinates[1] += speed * Math.sin(angles[0]);
        coordinates[2] += speed * Math.sin(angles[1]);

        return false;
    }

    private boolean rotateObject(float coordinates[],float[] modelMatrix, float destX, float destY, float destZ, float angularSpeed) {
        float epsilon = angularSpeed;
        //double xzDestAngle;
        //double xyDestAngle;

        double[] angles = new double[2];
        angles = returnAngles(coordinates[0], coordinates[1], coordinates[2], destX, destY, destZ);
        //setXzCurrAngle((float)angles[1]);
       // setXyCurrAngle((float)angles[0]);

        return false;
    }

    private static double[] returnAngles(float x1, float y1, float z1, float x2, float y2, float z2)
    {
        float deltax = Math.abs((x2-x1));
        float deltay = Math.abs((y2-y1));
        float deltaz = Math.abs((z2-z1));

        if (deltax == 0.0) deltax = 0.0001f;
        if (deltay == 0.0 && deltaz == 0.0) {
            deltay = 0.0001f;
            deltaz = 0.0001f;
        }

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

    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }
}