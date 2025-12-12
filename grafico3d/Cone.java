package com.grafico3d;

import android.opengl.GLES20;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class Cone {
    private final FloatBuffer vertexBuffer;
    private int mProgram;
    private int positionHandle;
    private int colorHandle;
    private int mvpMatrixHandle; //matriz de transformación

    static final int COORDS_POR_VERTEX = 3;
    private final int vertexCount;
    private final int vertexStride = COORDS_POR_VERTEX * 4;

    float colorLados[] = {0.0f, 0.0f, 0.0f, 1.0f}; // Color de triángulos laterales
    float colorBase[]  = {0.15f, 0.15f, 0.15f, 1.0f}; // Color de la base

    private int baseStartIndex;   // Donde comienzan los vértices del círculo
    private int baseVertexCount;

    public Cone(float radius, float height, int numPoints) {

        float[] coneCoords = createConeCoords(radius, height, numPoints);

        vertexCount = coneCoords.length / COORDS_POR_VERTEX;

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(coneCoords.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        vertexBuffer = byteBuffer.asFloatBuffer();
        vertexBuffer.put(coneCoords);
        vertexBuffer.position(0);

        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
    }

    private float[] createConeCoords(float radius, float height, int numPoints) {
        List<Float> coords = new ArrayList<>();

        // --- Punta del cono ---
        coords.add(0f);
        coords.add(height);
        coords.add(0f);

        double angleStep = 2.0 * Math.PI / numPoints;


        baseStartIndex = 1;
        baseVertexCount = numPoints + 2;

        //Vértices del borde de la base
        for (int i = 0; i <= numPoints; i++) {
            double ang = angleStep * i;
            coords.add((float)(radius * Math.cos(ang)));//x
            coords.add(0f);                             //y
            coords.add((float)(radius * Math.sin(ang)));//z
        }

        //centro de la base
        coords.add(0f); coords.add(0f); coords.add(0f);

        float[] arr = new float[coords.size()];
        for (int i = 0; i < coords.size(); i++){
            arr[i] = coords.get(i);
        }

        return arr;
    }

    private final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * (vPosition + vec4(0.0, -0.9, 0.0, 0.0));" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    public void draw(float[] mvpMatrix) {
        GLES20.glUseProgram(mProgram);

        mvpMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);

        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, COORDS_POR_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);

        colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // ----------- DIBUJAR LADOS DEL CONO -----------
        GLES20.glUniform4fv(colorHandle, 1, colorLados, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, baseStartIndex + baseVertexCount - 1);

        // ----------- DIBUJAR BASE -----------
        GLES20.glUniform4fv(colorHandle, 1, colorBase, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, baseStartIndex, baseVertexCount);

        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}