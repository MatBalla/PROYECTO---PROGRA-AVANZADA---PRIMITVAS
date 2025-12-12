package com.grafico3d;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Torus {

    private FloatBuffer vertexBuffer;
    private ShortBuffer indexBuffer;

    private int mProgram;
    private int positionHandle, colorHandle, mvpHandle;

    static final int COORDS_POR_VERTEX = 3;

    float[] color = {0.8f, 0.8f, 0.8f, 1.0f};

    private final int vertexCount;
    private final int vertexStride = COORDS_POR_VERTEX * 4;

    private float[] torusCoords;
    private short[] torusIndex;


    /*
    private float[] matrizVista = new float[16];
    private float[] matrizProy = new float[16];
    private float[] matrizModelo = new float[16];
    private float[] matrizMVP = new float[16];
    */

    public Torus() {

        torusCoords = createCoordsTorus(0.3f, 0.1f, 32, 16);
        torusIndex = createIndexTorus(32, 16);

        vertexCount = torusCoords.length / COORDS_POR_VERTEX;

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(torusCoords.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        vertexBuffer = byteBuffer.asFloatBuffer();
        vertexBuffer.put(torusCoords);
        vertexBuffer.position(0);

        ByteBuffer ib = ByteBuffer.allocateDirect(torusIndex.length * 2);
        ib.order(ByteOrder.nativeOrder());
        indexBuffer = ib.asShortBuffer();
        indexBuffer.put(torusIndex).position(0);

        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);

        // setCamara();
    }


    /*
    private void setCamara() {
        Matrix.setLookAtM(
                matrizVista, 0,
                1.5f, 1.5f, 1.5f,
                0f, 0f, 0f,
                0f, 1f, 0f
        );
    }

    public void setProjection(int width, int height) {
        float ratio = (float) width / height;
        Matrix.frustumM(matrizProy, 0, -ratio, ratio, -1, 1, 1.5f, 10);
    }
    */


    public void draw(float[] mvpMatrix) {

        GLES20.glUseProgram(mProgram);

        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);

        mvpHandle = GLES20.glGetUniformLocation(mProgram, "uMVP");

        // ENVIAMOS LA MATRIZ DEL RENDER (no usamos matrices internas)
        GLES20.glUniformMatrix4fv(mvpHandle, 1, false, mvpMatrix, 0);

        GLES20.glVertexAttribPointer(
                positionHandle,
                COORDS_POR_VERTEX,
                GLES20.GL_FLOAT,
                false,
                vertexStride,
                vertexBuffer
        );

        colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        int half = torusIndex.length / 2;

        GLES20.glUniform4f(colorHandle, 0.45f, 0.25f, 0.15f, 1.0f);
        indexBuffer.position(0);

        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES,
                half,
                GLES20.GL_UNSIGNED_SHORT,
                indexBuffer
        );

        GLES20.glUniform4f(colorHandle, 0.55f, 0.35f, 0.20f, 1.0f);
        indexBuffer.position(half);

        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES,
                torusIndex.length - half,
                GLES20.GL_UNSIGNED_SHORT,
                indexBuffer
        );

        GLES20.glDisableVertexAttribArray(positionHandle);
    }

    private float[] createCoordsTorus(float R, float r, int mayor, int menor) {
        float[] v = new float[mayor * menor * 3];
        int p = 0;

        for (int i = 0; i < mayor; i++) {
            float t = (float) (2 * Math.PI * i / mayor);
            float cosT = (float) Math.cos(t);
            float sinT = (float) Math.sin(t);

            for (int j = 0; j < menor; j++) {
                float pAng = (float) (2 * Math.PI * j / menor);
                float cosP = (float) Math.cos(pAng);
                float sinP = (float) Math.sin(pAng);

                v[p++] = (R + r * cosP) * cosT;
                v[p++] = (R + r * cosP) * sinT;
                v[p++] = r * sinP;
            }
        }
        return v;
    }

    private short[] createIndexTorus(int mayor, int menor) {
        short[] ind = new short[mayor * menor * 6];
        int k = 0;

        for (int i = 0; i < mayor; i++) {
            for (int j = 0; j < menor; j++) {

                int a = i * menor + j;
                int b = i * menor + (j + 1) % menor;
                int c = ((i + 1) % mayor) * menor + (j + 1) % menor;
                int d = ((i + 1) % mayor) * menor + j;

                ind[k++] = (short) a;
                ind[k++] = (short) b;
                ind[k++] = (short) c;

                ind[k++] = (short) a;
                ind[k++] = (short) c;
                ind[k++] = (short) d;
            }
        }
        return ind;
    }

    private final String vertexShaderCode =
            "uniform mat4 uMVP;" +
                    "attribute vec4 vPosition;" +
                    "void main(){ " +
                    "   gl_Position = uMVP * (vPosition + vec4(0.6, -0.75, -0.6, 0.0));" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main(){" +
                    "   gl_FragColor = vColor;" +
                    "}";
}
