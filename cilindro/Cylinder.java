package com.cilindro;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

public class Cylinder {

    private FloatBuffer bottomBuffer;   // tapa inferior
    private FloatBuffer topBuffer;      // tapa superior
    private FloatBuffer sideBuffer;     // superficie lateral

    private int bottomCount;
    private int topCount;
    private int sideCount;

    private int mProgram;
    private int positionHandle;
    private int colorHandle;
    private int matrixHandle;

    static final int COORDS_PER_VERTEX = 3;
    static final int STRIDE = COORDS_PER_VERTEX * 4;

    float color[] = {1f, 0f, 0f, 1f};

    private final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main(){ gl_Position = uMVPMatrix * vPosition; }";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main(){ gl_FragColor = vColor; }";

    public Cylinder(float radius, float height, int segments) {

        createBottom(radius, height, segments);
        createTop(radius, height, segments);
        createSide(radius, height, segments);

        int vs = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fs = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vs);
        GLES20.glAttachShader(mProgram, fs);
        GLES20.glLinkProgram(mProgram);
    }

    // =====================================================
    // 1) TAPA INFERIOR (TRIANGLE FAN)
    // =====================================================
    private void createBottom(float r, float h, int seg) {

        ArrayList<Float> list = new ArrayList<>();

        float y = -h / 2f;

        list.add(0f); list.add(y); list.add(0f); // centro

        for (int i = 0; i <= seg; i++) {
            double ang = 2 * Math.PI * i / seg;
            list.add((float)(r * Math.cos(ang)));
            list.add(y);
            list.add((float)(r * Math.sin(ang)));
        }

        bottomCount = list.size() / 3;
        bottomBuffer = toBuffer(list);
    }

    // =====================================================
    // 2) TAPA SUPERIOR (TRIANGLE FAN)
    // =====================================================
    private void createTop(float r, float h, int seg) {

        ArrayList<Float> list = new ArrayList<>();

        float y = +h / 2f;

        list.add(0f); list.add(y); list.add(0f); // centro

        for (int i = 0; i <= seg; i++) {
            double ang = 2 * Math.PI * i / seg;
            list.add((float)(r * Math.cos(ang)));
            list.add(y);
            list.add((float)(r * Math.sin(ang)));
        }

        topCount = list.size() / 3;
        topBuffer = toBuffer(list);
    }

    // =====================================================
    // 3) SUPERFICIE LATERAL (TRIANGLE STRIP)
    // =====================================================
    private void createSide(float r, float h, int seg) {

        ArrayList<Float> list = new ArrayList<>();

        float y1 = -h / 2f;
        float y2 = +h / 2f;

        for (int i = 0; i <= seg; i++) {

            double ang = 2 * Math.PI * i / seg;
            float x = (float)(r * Math.cos(ang));
            float z = (float)(r * Math.sin(ang));

            // abajo
            list.add(x); list.add(y1); list.add(z);

            // arriba
            list.add(x); list.add(y2); list.add(z);
        }

        sideCount = list.size() / 3;
        sideBuffer = toBuffer(list);
    }

    // =====================================================
    // DIBUJAR EL CILINDRO
    // =====================================================
    public void draw(float[] mvpMatrix) {

        GLES20.glUseProgram(mProgram);

        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        matrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        GLES20.glUniformMatrix4fv(matrixHandle, 1, false, mvpMatrix, 0);
        GLES20.glUniform4fv(colorHandle, 1, color, 0);

        GLES20.glEnableVertexAttribArray(positionHandle);

        // ---------------- TAPAS -----------------
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, STRIDE, bottomBuffer);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, bottomCount);

        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, STRIDE, topBuffer);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, topCount);

        // ---------------- LATERAL ----------------
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, STRIDE, sideBuffer);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, sideCount);

        GLES20.glDisableVertexAttribArray(positionHandle);
    }

    // =====================================================
    // Convertir lista → FloatBuffer
    // =====================================================
    private FloatBuffer toBuffer(ArrayList<Float> list) {
        float[] a = new float[list.size()];
        for (int i = 0; i < list.size(); i++) a[i] = list.get(i);

        ByteBuffer bb = ByteBuffer.allocateDirect(a.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(a);
        fb.position(0);
        return fb;
    }

}
