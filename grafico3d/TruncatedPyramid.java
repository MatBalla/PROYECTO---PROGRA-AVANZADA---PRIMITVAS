package com.grafico3d;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class TruncatedPyramid {

    private final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
            "attribute vec4 vPosition;" +
            "attribute vec4 vColor;" +
            "varying vec4 fColor;" +
            "void main() {" +
            "  gl_Position = uMVPMatrix * (vPosition + vec4(-0.3,0.73,0.65,0.0));" +
            "  fColor = vColor;" +
            "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
            "varying vec4 fColor;" +
            "void main() {" +
            "  gl_FragColor = fColor;" +
            "}";

    private final FloatBuffer vertexBuffer;
    private final FloatBuffer colorBuffer;
    private final int mProgram;
    private int positionHandle;
    private int colorHandle;
    private int vPMatrixHandle;

    static final int COORDS_PER_VERTEX = 3;
    static final int COLOR_PER_VERTEX = 4;

    // Coordenadas de la piramide
    // Cara de abajo(y = -0.5), Cara de arriba (y = 0.5)
    static float pyramidCoords[] = {

            // Cara Frontal (Frente)
            -0.070f,  0.035f,  0.070f,
            -0.140f, -0.035f,  0.140f,
            0.140f,  -0.035f,  0.140f,
            -0.070f,  0.035f,  0.070f,
            0.140f,  -0.035f,  0.140f,
            0.070f,   0.035f,  0.070f,

            // Cara Derecha
            0.070f,  0.035f,  0.070f,
            0.140f, -0.035f,  0.140f,
            0.140f, -0.035f, -0.140f,
            0.070f,  0.035f,  0.070f,
            0.140f, -0.035f, -0.140f,
            0.070f,  0.035f, -0.070f,

            // Cara Trasera
            0.070f,  0.035f, -0.070f,
            0.140f, -0.035f, -0.140f,
            -0.140f, -0.035f, -0.140f,
            0.070f,  0.035f, -0.070f,
            -0.140f, -0.035f, -0.140f,
            -0.070f,  0.035f, -0.070f,

            // Cara Izquierda
            -0.070f,  0.035f, -0.070f,
            -0.140f, -0.035f, -0.140f,
            -0.140f, -0.035f,  0.140f,
            -0.070f,  0.035f, -0.070f,
            -0.140f, -0.035f,  0.140f,
            -0.070f,  0.035f,  0.070f,

            // Cara Superior
            -0.070f,  0.035f, -0.070f,
            -0.070f,  0.035f,  0.070f,
            0.070f,   0.035f,  0.070f,
            -0.070f,  0.035f, -0.070f,
            0.070f,   0.035f,  0.070f,
            0.070f,   0.035f, -0.070f,

            // Cara Inferior
            -0.140f, -0.035f,  0.140f,
            -0.140f, -0.035f, -0.140f,
            0.140f,  -0.035f, -0.140f,
            -0.140f, -0.035f,  0.140f,
            0.140f,  -0.035f, -0.140f,
            0.140f,  -0.035f,  0.140f
    };






    float colors[] = {

            // --- Front (gris medio) ---
            0.55f, 0.55f, 0.55f, 1.0f,
            0.55f, 0.55f, 0.55f, 1.0f,
            0.55f, 0.55f, 0.55f, 1.0f,
            0.55f, 0.55f, 0.55f, 1.0f,
            0.55f, 0.55f, 0.55f, 1.0f,
            0.55f, 0.55f, 0.55f, 1.0f,

            // --- Right (gris medio) ---
            0.55f, 0.55f, 0.55f, 1.0f,
            0.55f, 0.55f, 0.55f, 1.0f,
            0.55f, 0.55f, 0.55f, 1.0f,
            0.55f, 0.55f, 0.55f, 1.0f,
            0.55f, 0.55f, 0.55f, 1.0f,
            0.55f, 0.55f, 0.55f, 1.0f,

            // --- Back (gris medio) ---
            0.55f, 0.55f, 0.55f, 1.0f,
            0.55f, 0.55f, 0.55f, 1.0f,
            0.55f, 0.55f, 0.55f, 1.0f,
            0.55f, 0.55f, 0.55f, 1.0f,
            0.55f, 0.55f, 0.55f, 1.0f,
            0.55f, 0.55f, 0.55f, 1.0f,

            // --- Left (gris medio) ---
            0.55f, 0.55f, 0.55f, 1.0f,
            0.55f, 0.55f, 0.55f, 1.0f,
            0.55f, 0.55f, 0.55f, 1.0f,
            0.55f, 0.55f, 0.55f, 1.0f,
            0.55f, 0.55f, 0.55f, 1.0f,
            0.55f, 0.55f, 0.55f, 1.0f,

            // --- Top (más claro, brillo) ---
            0.75f, 0.75f, 0.75f, 1.0f,
            0.75f, 0.75f, 0.75f, 1.0f,
            0.75f, 0.75f, 0.75f, 1.0f,
            0.75f, 0.75f, 0.75f, 1.0f,
            0.75f, 0.75f, 0.75f, 1.0f,
            0.75f, 0.75f, 0.75f, 1.0f,

            // --- Bottom (sombra) ---
            0.35f, 0.35f, 0.35f, 1.0f,
            0.35f, 0.35f, 0.35f, 1.0f,
            0.35f, 0.35f, 0.35f, 1.0f,
            0.35f, 0.35f, 0.35f, 1.0f,
            0.35f, 0.35f, 0.35f, 1.0f,
            0.35f, 0.35f, 0.35f, 1.0f
    };



    public TruncatedPyramid() {
        ByteBuffer bb = ByteBuffer.allocateDirect(pyramidCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(pyramidCoords);
        vertexBuffer.position(0);

        ByteBuffer cb = ByteBuffer.allocateDirect(colors.length * 4);
        cb.order(ByteOrder.nativeOrder());
        colorBuffer = cb.asFloatBuffer();
        colorBuffer.put(colors);
        colorBuffer.position(0);

        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
    }

    public void draw(float[] mvpMatrix) {
        GLES20.glUseProgram(mProgram);

        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false,
                COORDS_PER_VERTEX * 4, vertexBuffer);

        colorHandle = GLES20.glGetAttribLocation(mProgram, "vColor");
        GLES20.glEnableVertexAttribArray(colorHandle);
        GLES20.glVertexAttribPointer(colorHandle, COLOR_PER_VERTEX, GLES20.GL_FLOAT, false,
                COLOR_PER_VERTEX * 4, colorBuffer);

        vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, pyramidCoords.length / COORDS_PER_VERTEX);

        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(colorHandle);
    }
}
