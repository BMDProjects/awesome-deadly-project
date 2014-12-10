package com.example.android.opengl;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Random;
import java.util.Vector;

public class Dolphin {
    Shaders shaders = new Shaders();

    public final float[] dolphinModel = new float[16];
    public final float[] centerOfDolphinModelSpace = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
    public final float[] centerOfDolphinEyeSpace = new float[4];
    public final float[] centerOfDolphinWorldSpace = new float[4];

    private FloatBuffer mDolphinVertexBuffer;
    private FloatBuffer mDolphinNormalBuffer;
    private ShortBuffer mDolphinDrawOrder;
   /* private final FloatBuffer mDolphinColors;
   // private final FloatBuffer mDolphinNormals;*/
    private final int mDolphinProgram;
    private int mPositionHandle;
    private int mColorHandle;
    private int  mNormalHandle;
   private int mMVPMatrixHandle;
    private int mMVMatrixHandle;
    private int mLightPosHandle0;
    private int mLightPosHandle1;
    private int mLightPosHandle2;
    private int mLightPosHandle3;
    private int mLightPosHandle4;
    private int mLightPosHandle5;
    private int distanceCorrectionHandle;

    public int delayer = 20;

    Vector<Float> v;
    Vector<Float> vn;
    Vector<Float> vt;
    Vector<Short> faces;
   // Vector<TDModelPart> parts;

    static final int COORDS_PER_VERTEX = 3;

    /*final float[] mDolphinPositions =
            {

            };

    final float[] mDolphinColors =
            {

            };

    final float[] mDolphinNormals =
            {

            };*/


    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    public float[] color = {0.0f, 1.0f, 1.0f, 0.0f};

    /*public Dolphin () {
        int perPixelVertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, shaders.getVertexShader(0));
        int perPixelfragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, shaders.getFragmentShader(0));
        mProgramPerPixel = MyGLRenderer.createAndLinkProgram(perPixelVertexShader, perPixelfragmentShader,
                new String[]{"vPosition"});
    }*/

    public Dolphin(Vector<Float> v, Vector<Float> vn, Vector<Float> vt, Vector<Short> faces) {
        super();
        this.v = v;
        this.vn = vn;
        this.vt = vt;
        this.faces = faces;

        buildVertexBuffer();
        buildVertexBuffer2();

        int dolphinVertexShader = Shaders.loadShader(GLES20.GL_VERTEX_SHADER, shaders.getVertexShader(6));
        int dolphinfragmentShader = Shaders.loadShader(GLES20.GL_FRAGMENT_SHADER, shaders.getFragmentShader(6));
        mDolphinProgram = Shaders.createAndLinkProgram(dolphinVertexShader, dolphinfragmentShader,
                new String[]{"a_Position",  "a_Normal"});

       //GLES20.glBindAttribLocation(programHandle, i, attributes[i]);
    }

    public void buildVertexBuffer(){
        ByteBuffer vBuf = ByteBuffer.allocateDirect(v.size() * 4);
        vBuf.order(ByteOrder.nativeOrder());
        mDolphinVertexBuffer = vBuf.asFloatBuffer();
        mDolphinVertexBuffer.put(toPrimitiveArrayF(v));
        mDolphinVertexBuffer.position(0);
    }

    public void buildVertexBuffer3(){
        ByteBuffer vBuf = ByteBuffer.allocateDirect(vn.size() * 4);
        vBuf.order(ByteOrder.nativeOrder());
        mDolphinNormalBuffer = vBuf.asFloatBuffer();
        mDolphinNormalBuffer.put(toPrimitiveArrayF(vn));
        mDolphinNormalBuffer.position(0);
    }

    public void buildVertexBuffer2(){
        ByteBuffer vBuf = ByteBuffer.allocateDirect(faces.size() * 2);
        vBuf.order(ByteOrder.nativeOrder());
        mDolphinDrawOrder = vBuf.asShortBuffer();
        mDolphinDrawOrder.put(toPrimitiveArrayS(faces));
        mDolphinDrawOrder.position(0);
    }

    private static float[] toPrimitiveArrayF(Vector<Float> vector){
        float[] f;
        f=new float[vector.size()];
        for (int i=0; i<vector.size(); i++){
            f[i]=vector.get(i);
        }
        return f;
    }

    private static short[] toPrimitiveArrayS(Vector<Short> vector){
        short[] s;
        s=new short[vector.size()];
        for (int i=0; i<vector.size(); i++){
            s[i]=vector.get(i);
        }
        return s;
    }

    public String toString(){
        String str=new String();
       // str+="Number of parts: "+parts.size();
        str+="\nNumber of vertexes: "+v.size();
        str+="\nNumber of vns: "+vn.size();
        str+="\nNumber of vts: "+vt.size();
        str+="\n/////////////////////////\n";
      /*  for(int i=0; i<parts.size(); i++){
            str+="Part "+i+'\n';
            str+=parts.get(i).toString();
            str+="\n/////////////////////////";
        }*/
        return str;
    }

    // number of coordinates per vertex in this array


    /*public Dolphin() {
        mCubePositions = ByteBuffer.allocateDirect(cubePositionData.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCubePositions.put(cubePositionData).position(0);

        mCubeColors = ByteBuffer.allocateDirect(cubeColorData.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCubeColors.put(cubeColorData).position(0);

        mCubeNormals = ByteBuffer.allocateDirect(cubeNormalData.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCubeNormals.put(cubeNormalData).position(0);

        int perPixelVertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, shaders.getVertexShader(4));
        int perPixelfragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, shaders.getFragmentShader(4));
        mProgramPerPixel = MyGLRenderer.createAndLinkProgram(perPixelVertexShader, perPixelfragmentShader,
                new String[]{"a_Position",  "a_Color", "a_Normal"});
    }*/

    private final int mPositionDataSize = 3;
    private final int mColorDataSize = 4;
    private final int mNormalDataSize = 3;


    public void drawDolphin(float[] modelMatrix, float dolphinScale) {
        GLES20.glUseProgram(mDolphinProgram);
        final float[] mMVPMatrix = new float[16];
        final float[] mMVMatrix = new float[16];
        Matrix.setIdentityM(mMVPMatrix, 0);
        Matrix.setIdentityM(mMVMatrix, 0);

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mDolphinProgram, "uMVPMatrix");
        mMVMatrixHandle = GLES20.glGetUniformLocation(mDolphinProgram, "u_MVMatrix");

        mLightPosHandle0 = GLES20.glGetUniformLocation(mDolphinProgram, "u_LightPos0");
        mLightPosHandle1 = GLES20.glGetUniformLocation(mDolphinProgram, "u_LightPos1");
        mLightPosHandle2 = GLES20.glGetUniformLocation(mDolphinProgram, "u_LightPos2");
        mLightPosHandle3 = GLES20.glGetUniformLocation(mDolphinProgram, "u_LightPos3");
        mLightPosHandle4 = GLES20.glGetUniformLocation(mDolphinProgram, "u_LightPos4");
        mLightPosHandle5 = GLES20.glGetUniformLocation(mDolphinProgram, "u_LightPos5");
        distanceCorrectionHandle = GLES20.glGetUniformLocation(mDolphinProgram, "distanceCorrection");

        mNormalHandle = GLES20.glGetAttribLocation(mDolphinProgram, "a_Normal");
        mPositionHandle = GLES20.glGetAttribLocation(mDolphinProgram, "a_Position");
        mColorHandle = GLES20.glGetUniformLocation(mDolphinProgram, "vColor");

        //Pass in vertex position information
        mDolphinVertexBuffer.position(0);
        GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false,
                0, mDolphinVertexBuffer);
        GLES20.glEnableVertexAttribArray(mPositionHandle);

       /* // Pass in the color information
        mCubeColors.position(0);
        GLES20.glVertexAttribPointer(mColorHandle, mColorDataSize, GLES20.GL_FLOAT, false,
                0, mCubeColors);
        GLES20.glEnableVertexAttribArray(mColorHandle);*/

        // Pass in the normal information
        mDolphinNormalBuffer.position(0);
        GLES20.glVertexAttribPointer(mNormalHandle, mNormalDataSize, GLES20.GL_FLOAT, false,
                0, mDolphinNormalBuffer);
        GLES20.glEnableVertexAttribArray(mNormalHandle);


        float[] scale_matrix = new float[16];
        float[] scale = {dolphinScale,dolphinScale,dolphinScale};
        Matrix.setIdentityM(scale_matrix, 0);
        Matrix.scaleM(scale_matrix, 0, scale[0], scale[1], scale[2]);
        Matrix.multiplyMM(modelMatrix, 0, modelMatrix , 0, scale_matrix, 0);

        Matrix.multiplyMM(mMVMatrix, 0, MyGLRenderer.mViewMatrix  , 0, modelMatrix, 0);
        // Pass in the modelview matrix.

        Matrix.multiplyMM(mMVPMatrix, 0, MyGLRenderer.mProjectionMatrix, 0, mMVMatrix, 0);
        // Pass in the combined matrix.
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        GLES20.glUniform3f(mLightPosHandle0, MyGLRenderer.light[0].mLightPosInEyeSpace[0], MyGLRenderer.light[0].mLightPosInEyeSpace[1], MyGLRenderer.light[0].mLightPosInEyeSpace[2]);
        GLES20.glUniform3f(mLightPosHandle1, MyGLRenderer.light[1].mLightPosInEyeSpace[0], MyGLRenderer.light[1].mLightPosInEyeSpace[1], MyGLRenderer.light[1].mLightPosInEyeSpace[2]);
        GLES20.glUniform3f(mLightPosHandle2, MyGLRenderer.light[2].mLightPosInEyeSpace[0], MyGLRenderer.light[2].mLightPosInEyeSpace[1], MyGLRenderer.light[2].mLightPosInEyeSpace[2]);
        GLES20.glUniform3f(mLightPosHandle3, MyGLRenderer.light[3].mLightPosInEyeSpace[0], MyGLRenderer.light[3].mLightPosInEyeSpace[1], MyGLRenderer.light[3].mLightPosInEyeSpace[2]);
        GLES20.glUniform3f(mLightPosHandle4, MyGLRenderer.light[4].mLightPosInEyeSpace[0], MyGLRenderer.light[4].mLightPosInEyeSpace[1], MyGLRenderer.light[4].mLightPosInEyeSpace[2]);
        GLES20.glUniform3f(mLightPosHandle5, MyGLRenderer.light[5].mLightPosInEyeSpace[0], MyGLRenderer.light[5].mLightPosInEyeSpace[1], MyGLRenderer.light[5].mLightPosInEyeSpace[2]);

        GLES20.glUniform1f(distanceCorrectionHandle, 0.1f);

        // Draw the cube.
        //GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, v.size());
        //GLES20.glDrawElements(GLES20.GL_TRIANGLES, v.size(), );
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, faces.size(),
                GLES20.GL_UNSIGNED_SHORT, mDolphinDrawOrder);

        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

}
