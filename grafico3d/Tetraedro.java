package com.grafico3d;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Tetraedro {
    private FloatBuffer vertexBuffer;
    private final int mProgram;
    private int positionHandle, mMVPMatrixHandle;

    // Coordenadas organizadas por CARA (no por vértice)
    private float[] tetrahedronCoordsByFace = {
            // Cara 1: (frontal derecha)
            0.0f, 0.075f, 0.0f,     // Vert0 (0.0f * 0.05f, 1.5f * 0.05f, 0.0f * 0.05f)
            0.0f, 0.0f, 0.075f,     // Vert1 (0.0f * 0.05f, 0.0f * 0.05f, 1.5f * 0.05f)
            0.065f, 0.0f, -0.04f,   // Vert2 (1.3f * 0.05f, 0.0f * 0.05f, -0.8f * 0.05f)

            // Cara 2: (trasera derecha)
            0.0f, 0.075f, 0.0f,     // Vert0
            0.065f, 0.0f, -0.04f,   // Vert2
            -0.065f, 0.0f, -0.04f,  // Vert3 (-1.3f * 0.05f, 0.0f * 0.05f, -0.8f * 0.05f)

            // Cara 3: (frontal izquierda)
            0.0f, 0.075f, 0.0f,     // Vert0
            -0.065f, 0.0f, -0.04f,  // Vert3
            0.0f, 0.0f, 0.075f,     // Vert1

            // Cara 4: (base)
            0.0f, 0.0f, 0.075f,     // Vert1
            -0.065f, 0.0f, -0.04f,  // Vert3
            0.065f, 0.0f, -0.04f    // Vert2
    };

    private float[][] faceColorsArray = {
            // Rojo brillante con gradiente para efecto 3D
            {0.8f, 0.1f, 0.1f, 1.0f},   // Cara superior (más brillante)
            {0.6f, 0.05f, 0.05f, 1.0f},  // Cara frontal (medio)
            {0.4f, 0.0f, 0.0f, 1.0f},    // Cara lateral (oscura)
            {0.3f, 0.0f, 0.0f, 1.0f}     // Cara base (más oscura)
    };

    public Tetraedro() {
        // Buffer de vértices
        ByteBuffer bb = ByteBuffer.allocateDirect(tetrahedronCoordsByFace.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(tetrahedronCoordsByFace);
        vertexBuffer.position(0);

        // Compilar shaders
        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
    }

    private final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * (vPosition + vec4(-0.2, -0.67, 0.95, 0.0));" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 uColor;" +
                    "void main() {" +
                    "  gl_FragColor = uColor;" +
                    "}";

    public void draw(float[] mvpMatrix) {
        GLES20.glUseProgram(mProgram);

        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, 3,
                GLES20.GL_FLOAT, false, 12, vertexBuffer);

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        int colorHandle = GLES20.glGetUniformLocation(mProgram, "uColor");

        // Dibujar cada cara con su propio color
        for (int i = 0; i < 4; i++) {
            // Pasar color para esta cara
            GLES20.glUniform4fv(colorHandle, 1, faceColorsArray[i], 0);

            // Dibujar 3 vértices de esta cara
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, i * 3, 3);
        }

        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}