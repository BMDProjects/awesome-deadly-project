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
    private Cube   mCube3;

    //has to be public because cube has to access it
    public static Light light1;

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
        //unused.glEnable(GL_RESCALE_NORMAL);
        GLES20.glEnable(GLES20.GL_VERTEX_ATTRIB_ARRAY_NORMALIZED);
       // GLES20.glLightModeli(GL_LIGHT_MODEL_TWO_SIDE, GL_TRUE);
       // unused.glLightModeli(GL_LIGHT_MODEL_TWO_SIDE, GL_TRUE);

        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        mCube   = new Cube();
        mCube2 = new Cube();
        mCube3 = new Cube();

        light1 = new Light();

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
                theJourney[i][j] = (float)(Math.random()*50) - 25.0f;
            }
        }
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        float[] scratch = new float[16];
        float[] sqScratch = new float[16];

        // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        theta += speed;
        if(radius > 6.0f) radius = radius - speed;
        else radius = radius + speed;

        boolean reachedDestination2 = false;
        reachedDestination2 = (moveObject(eyeXYZ, theJourney[coord][0] + 2.0f, theJourney[coord][1] + 3.5f, theJourney[coord][2], 0.1f));
        if(reachedDestination2){
            reachedDestination2 = false;
            coord++;
            if(coord > pointsOnJourney - 1) coord = 0;
        }
        //eyeX = (float)(3.0 * Math.cos(theta)); //+ circle.cx;
        //eyeZ = (float)(radius * Math.sin(theta)); //+ circle.cy;
        // eyeY = (float)(radius * Math.cos(theta));
      //  eyeZ = 6.0f;
      //  eyeX = 0.0f;
     //   eyeY = 6.0f;
        // Set the camera position (View matrix)
        // Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, centerOfCubeEyeSpace[0], centerOfCubeEyeSpace[1], centerOfCubeEyeSpace[2], 0f, 1.0f, 0.0f);
        //Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, centerOfCubeEyeSpace[0], centerOfCubeEyeSpace[1], centerOfCubeEyeSpace[2], 0f, 1.0f, 0.0f);
       // Matrix.setLookAtM(mViewMatrix, 0, eyeXYZ[0], eyeXYZ[1], eyeXYZ[2], centerOfCubeWorldSpace[0], centerOfCubeWorldSpace[1], centerOfCubeWorldSpace[2], 0f, 1.0f, 0.0f);
        Matrix.setIdentityM(mViewMatrix, 0);
      //  Matrix.setLookAtM(mViewMatrix, 0, eyeXYZ[0], eyeXYZ[1], eyeXYZ[2], 0.0f, 0.0f, 0.0f, 0f, 1.0f, 0.0f);
        Matrix.setLookAtM(mViewMatrix, 0, eyeXYZ[0], eyeXYZ[1], eyeXYZ[2],  centerOfCubeWorldSpace[0], centerOfCubeWorldSpace[1], centerOfCubeWorldSpace[2], 0f, 1.0f, 0.0f);
        //eyeXYZ[0] += speed;
        // Use the following code to generate constant rotation.
        // Leave this code out when using TouchEvents.

        //float sqAngle = 30.0f;
        //USE THIS INSTEAD FOR ANGLE ------------------------------------------------------
        long time = SystemClock.uptimeMillis() % 10000L;
        float sqAngle = (360.0f / 10000.0f) * ((int) time);


        // Calculate position of the light. Rotate and then push into the distance.
        Matrix.setIdentityM(light1.mLightModelMatrix, 0);
        Matrix.translateM(light1.mLightModelMatrix, 0, 0.0f, 0.0f, -2.0f);
        Matrix.rotateM(light1.mLightModelMatrix, 0, sqAngle, 0.0f, 1.0f, 0.0f);
        Matrix.translateM(light1.mLightModelMatrix, 0, 0.0f, 0.0f, 2.0f);

        Matrix.multiplyMV(light1.mLightPosInWorldSpace, 0, light1.mLightModelMatrix, 0, light1.mLightPosInModelSpace, 0);
        Matrix.multiplyMV(light1.mLightPosInEyeSpace, 0, mViewMatrix, 0, light1.mLightPosInWorldSpace, 0);

        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, light1.mLightModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
        light1.drawLight(mMVPMatrix);

        //Initiialize and Translate
        Matrix.setIdentityM(sqScratch, 0);
        Matrix.setIdentityM(mMVPMatrix, 0);
        Matrix.translateM(sqScratch, 0, centerOfCubeWorldSpace[0], centerOfCubeWorldSpace[1], centerOfCubeWorldSpace[2]);
        Matrix.multiplyMV(centerOfCubeWorldSpace , 0, sqScratch, 0, centerOfCubeModelSpace , 0);
        Matrix.multiplyMV(centerOfCubeEyeSpace , 0, mViewMatrix, 0, centerOfCubeWorldSpace , 0);
        /*reachedDestination = moveObject(centerOfCubeWorldSpace, theJourney[coord + 1][0], theJourney[coord + 1][1], theJourney[coord + 1][2], 0.1f);
        if(reachedDestination){
            reachedDestination = false;
            coord++;
            if(coord > pointsOnJourney - 1) coord = 0;
        }*/

      // if( moveObject(centerOfCubeWorldSpace, theJourney[coord][0], theJourney[coord][1], theJourney[coord][2], 0.1f);)
        //if( moveObject(eyeXYZ, 10.0f, 10.0f, 10.0f, 0.2f))
        //Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, sqScratch, 0);
       // Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);



        mCube.draw5(sqScratch, 1.0f);
        //mCube.draw3(sqScratch, 1.0f);
        //  Matrix.multiplyMV(mLightPosInWorldSpace, 0, sqScratch, 0, mLightPosInModelSpace, 0);
        // Matrix.multiplyMV(mLightPosInEyeSpace, 0, mViewMatrix, 0, mLightPosInWorldSpace, 0);

        //cube 2
        float[] scale_matrix = new float[16];
        Matrix.setIdentityM(sqScratch, 0);
        /*Matrix.setIdentityM(scale_matrix, 0);
        float[] scale = {50.0f,50.0f,50.0f};
        Matrix.scaleM(scale_matrix, 0, scale[0], scale[1], scale[2]);

        Matrix.multiplyMM(sqScratch, 0, sqScratch , 0, scale_matrix, 0);*/
       // Matrix.translateM(sqScratch,  0, 0.0f, 0.0f, 0.0f);
       // Matrix.multiplyMM(mMVPMatrix, 0, sqScratch, 0, mViewMatrix, 0);
      //  Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        mCube2.draw5(sqScratch, 50.0f);


        //cube 3
        Matrix.setIdentityM(sqScratch, 0);
        Matrix.setIdentityM(mMVPMatrix, 0);
        Matrix.translateM(sqScratch,  0, 0.0f, 0.0f, 0.0f);
        Matrix.multiplyMM(mMVPMatrix, 0, sqScratch, 0, mViewMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        Matrix.setRotateM(mRotationMatrix, 0, sqAngle, 0.5f, 0.3f, 1.0f);
        //Matrix.multiplyMM(mMVPMatrix, 0, mMVPMatrix, 0, mRotationMatrix, 0);
        Matrix.multiplyMM(sqScratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
        mCube3.draw3(sqScratch, 0.5f);
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

    public static boolean approxEqual(float x, float y, float eps){
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