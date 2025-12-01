package com.cilindro;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {

    private Cylinder cilindro;

    private final float[] mVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];

    // ✅ PARÁMETROS DE CÁMARA (fáciles de modificar)
    public float camaraX = 0.0f;
    public float camaraY = 3.0f;
    public float camaraZ = 2.9f;

    @Override
    public void onDrawFrame(GL10 gl) {

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // ✅ Cámara sencilla y clara
        Matrix.setLookAtM(
                mViewMatrix, 0,
                camaraX, camaraY, camaraZ,  // posición de cámara
                0f, 3f, 2.9f,                 // hacia dónde mira (centro)
                0f, 1f, 0f                  // vector "arriba"
        );

        Matrix.multiplyMM(
                mVPMatrix, 0,
                mProjectionMatrix, 0,
                mViewMatrix, 0
        );

        // ⛔ IMPORTANTE: pasa la matriz al cilindro
        cilindro.draw(mVPMatrix);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        Matrix.frustumM(
                mProjectionMatrix,
                0,
                -ratio, ratio,
                -1, 1,
                1, 10
        );
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        // ‼️ ESTE ES EL CILINDRO NUEVO Y SIMPLE
        cilindro = new Cylinder(0.5f, 1.0f, 32);
    }

    // Dejas igual tu cargador de shaders
    public static int loadShader(int type, String shaderCode) {

        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }
}
