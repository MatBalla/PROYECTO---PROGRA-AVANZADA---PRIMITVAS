package com.transformaciones;

import android.opengl.GLES20;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

//CLASE PARA EXCLUSIVAMENTE EJES
public class Ejes {
    private final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "attribute vec4 vColor;" +
                    "varying vec4 _vColor;" +
                    "void main() {" +
                    "  _vColor = vColor;" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "varying vec4 _vColor;" +
                    "void main() {" +
                    "  gl_FragColor = _vColor;" +
                    "}";

    private final FloatBuffer vertexBuffer;
    private final FloatBuffer colorBuffer;
    private final int mProgram;

    // Coordenadas de los ejes: (0,0,0) a (L,0,0), (0,L,0), (0,0,L)
    // Coordenadas de los ejes: de -L a +L
    static final float L = 4.0f;
    static final float[] EjesCoords = {
            -L, 0.0f, 0.0f,  // Inicio del Eje X (-X)
            L, 0.0f, 0.0f,  // Fin del Eje X (+X)

            0.0f, -L, 0.0f,  // Inicio del Eje Y (-Y)
            0.0f,  L, 0.0f,  // Fin del Eje Y (+Y)

            0.0f, 0.0f, -L,  // Inicio del Eje Z (-Z)
            0.0f, 0.0f,  L   // Fin del Eje Z (+Z)
    };

    // (XRojo, YVerde, ZAzul).

    static final float[] ColoresEjes = {
            1.0f, 0.0f, 0.0f, 1.0f, // Rojo (Eje X, parte negativa)
            1.0f, 0.0f, 0.0f, 1.0f, // Rojo (Eje X, parte positiva)
            0.0f, 1.0f, 0.0f, 1.0f, // Verde (Eje Y, parte negativa)
            0.0f, 1.0f, 0.0f, 1.0f, // Verde (Eje Y, parte positiva)
            0.0f, 0.0f, 1.0f, 1.0f, // Azul (Eje Z, parte negativa)
            0.0f, 0.0f, 1.0f, 1.0f  // Azul (Eje Z, parte positiva)
    };

    // Constructor
    public Ejes() {
        // Inicializar búfer de vértices
        ByteBuffer bb = ByteBuffer.allocateDirect(EjesCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(EjesCoords);
        vertexBuffer.position(0);

        // Inicializar búfer de colores
        ByteBuffer cb = ByteBuffer.allocateDirect(ColoresEjes.length * 4);
        cb.order(ByteOrder.nativeOrder());
        colorBuffer = cb.asFloatBuffer();
        colorBuffer.put(ColoresEjes);
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

        // Obtener manejadores (handles)
        int positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        int colorHandle = GLES20.glGetAttribLocation(mProgram, "vColor");
        int mvpMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        // Habilitar vértices
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer);

        // Habilitar colores
        GLES20.glEnableVertexAttribArray(colorHandle);
        GLES20.glVertexAttribPointer(colorHandle, 4, GLES20.GL_FLOAT, false, 4 * 4, colorBuffer);

        // Pasar la matriz de transformación
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);

        // Dibujar las líneas (6 puntos = 3 pares de líneas)
        GLES20.glLineWidth(5.0f); // Opcional: para que las líneas sean más gruesas
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, 6);

        // Deshabilitar
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(colorHandle);
    }
}
