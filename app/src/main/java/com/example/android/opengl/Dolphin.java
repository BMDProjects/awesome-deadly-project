package com.example.android.opengl;

import android.content.Context;
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

    public Context context;

    public final float[] dolphinModel = new float[16];
    public final float[] centerOfDolphinModelSpace = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
    public final float[] centerOfDolphinWorldSpace = new float[4];
    public final float[] dolphinNormalWorldSpace = new float[4];
    public final float[] dolphinNormalModelSpace = new float[] {1.0f, 0.0f, 0.0f, 1.0f};;

    private FloatBuffer mDolphinVertexBuffer;
    private FloatBuffer mDolphinNormalBuffer;
    private ShortBuffer mDolphinDrawOrder;
    private final int mDolphinProgram;
    private int mPositionHandle;
    private int mColorHandle;
    private int  mNormalHandle;
    private int mMVPMatrixHandle;

    private int mTextureDataHandle;
    private int mTextureCoordinateHandle;
    private int mTextureUniformHandle;
    private FloatBuffer mDolphinTextureCoordinates;
    private final int mTextureCoordinateDataSize = 2;

    public int delayer = 20;

    Vector<Float> v;
    Vector<Float> vn;
    Vector<Float> vt;
    Vector<Short> faces;

    static final int COORDS_PER_VERTEX = 3;

    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    public float[] color = {0.0f, 1.0f, 1.0f, 0.0f};

    public Dolphin(Vector<Float> v, Vector<Float> vn, Vector<Float> vt, Vector<Short> faces, Context context_p) {
        super();
        this.v = v;
        this.vn = vn;
        this.vt = vt;
        this.faces = faces;

        context = context_p;

        //mTextureDataHandle = TextureHelper.loadTexture(context, R.drawable.bumpy_bricks_public_domain);
        mTextureDataHandle = TextureHelper.loadTexture(context, R.drawable.dolphintexture);

        buildVertexBuffer();
        buildVertexBuffer2();
        buildVertexBuffer3();
        buildVertexBuffer4();

        int dolphinVertexShader = Shaders.loadShader(GLES20.GL_VERTEX_SHADER, shaders.getVertexShader(6));
        int dolphinfragmentShader = Shaders.loadShader(GLES20.GL_FRAGMENT_SHADER, shaders.getFragmentShader(6));
        mDolphinProgram = Shaders.createAndLinkProgram(dolphinVertexShader, dolphinfragmentShader,
                new String[]{"a_Position",  "a_Normal", "a_TexCoordinate"});
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

    public void buildVertexBuffer4(){
        ByteBuffer vBuf = ByteBuffer.allocateDirect(vt.size() * 4);
        vBuf.order(ByteOrder.nativeOrder());
        mDolphinTextureCoordinates = vBuf.asFloatBuffer();
        mDolphinTextureCoordinates.put(toPrimitiveArrayF(vt));
        mDolphinTextureCoordinates.position(0);
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

    private final int mPositionDataSize = 3;
    private final int mColorDataSize = 4;
    private final int mNormalDataSize = 3;


    public void drawDolphin(float[] modelMatrix, float dolphinScale) {
        GLES20.glUseProgram(mDolphinProgram);
        final float[] mMVPMatrix = new float[16];
        final float[] mMVMatrix = new float[16];
        Matrix.setIdentityM(mMVPMatrix, 0);
        Matrix.setIdentityM(mMVMatrix, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);
        GLES20.glUniform1i(mTextureUniformHandle, 0);
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mDolphinProgram, "u_MVPMatrix");

        mNormalHandle = GLES20.glGetAttribLocation(mDolphinProgram, "a_Normal");
        mPositionHandle = GLES20.glGetAttribLocation(mDolphinProgram, "a_Position");
        mColorHandle = GLES20.glGetUniformLocation(mDolphinProgram, "vColor");

        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mDolphinProgram, "a_TexCoordinate");
        mTextureUniformHandle = GLES20.glGetUniformLocation(mDolphinProgram, "u_Texture");

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

        mDolphinTextureCoordinates.position(0);
        GLES20.glVertexAttribPointer(mTextureCoordinateHandle, mTextureCoordinateDataSize, GLES20.GL_FLOAT, false,
                0, mDolphinTextureCoordinates);

        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);


        float[] scale_matrix = new float[16];
        float[] scale = {dolphinScale,dolphinScale,dolphinScale};
        Matrix.setIdentityM(scale_matrix, 0);
        Matrix.scaleM(scale_matrix, 0, scale[0], scale[1], scale[2]);
        Matrix.multiplyMM(modelMatrix, 0, modelMatrix , 0, scale_matrix, 0);

        Matrix.multiplyMM(mMVMatrix, 0, MyGLRenderer.mViewMatrix  , 0, modelMatrix, 0);
        // Pass in the modelview matrix.
        //GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mMVMatrix, 0);

        Matrix.multiplyMM(mMVPMatrix, 0, MyGLRenderer.mProjectionMatrix, 0, mMVMatrix, 0);
        // Pass in the combined matrix.
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // Draw the cube.
        //GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, v.size());
        //GLES20.glDrawElements(GLES20.GL_TRIANGLES, v.size(), );
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, faces.size(),
                GLES20.GL_UNSIGNED_SHORT, mDolphinDrawOrder);

        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

}
