package com.grafico3d;

import static com.grafico3d.MyGLRenderer.loadShader;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class HexagonalPyramid {

    private FloatBuffer vertexBuffer;
    private final int mProgram;
    private int positionHandle, colorHandle, mvpMatrixHandle;
    private static final int COORDS_PER_VERTEX = 3;
    private final int vertexStride = COORDS_PER_VERTEX * 4;

    private float[] pyramidCoords; //XIME
    private final int vertexCount;

    // Colores distintos para cada triángulo
    private float[][] colores = {

            {0.05f, 0.05f, 0.05f, 1.0f},   // Negro absoluto
            {0.08f, 0.08f, 0.08f, 1.0f},   // Negro carbón
            {0.12f, 0.12f, 0.12f, 1.0f},   // Negro grisáceo


            {0.18f, 0.18f, 0.20f, 1.0f},   // Gris metálico oscuro
            {0.22f, 0.22f, 0.25f, 1.0f},   // Gris acero
            {0.28f, 0.28f, 0.32f, 1.0f},   // Gris medio metálico


            {0.35f, 0.35f, 0.40f, 1.0f},   // Plateado oscuro
            {0.45f, 0.45f, 0.50f, 1.0f},   // Plateado medio
            {0.55f, 0.55f, 0.60f, 1.0f},   // Plateado claro


            {0.70f, 0.70f, 0.75f, 1.0f},   // Brillo intenso
            {0.85f, 0.85f, 0.90f, 1.0f},   // Reflejo especular
            {0.95f, 0.95f, 1.00f, 1.0f}    // Punto de luz máximo
    };

    // Vertex Shader: transforma coordenadas 3D → 2D con la matriz MVP
    private final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * (vPosition + vec4(0.3,0.7,-0.65,0.0));" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    public HexagonalPyramid(float radius, float height) {
        pyramidCoords = createHexagonPyramid(radius, height, 0f, 0f);
        vertexCount = pyramidCoords.length / COORDS_PER_VERTEX; //XIME

        ByteBuffer bb = ByteBuffer.allocateDirect(pyramidCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(pyramidCoords);
        vertexBuffer.position(0);

        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
    }

    // 🔹 Genera base (6 triángulos) + caras laterales (6 triángulos)
    private float[] createHexagonPyramid(float radius, float height, float centerX, float centerZ) {
        List<Float> coords = new ArrayList<>();
        double angleStep = 2.0 * Math.PI / 6;

        // Vértices del hexágono
        float[][] hexVertex = new float[6][3];
        for (int i = 0; i < 6; i++) {
            double angle = i * angleStep;
            hexVertex[i][0] = centerX + (float)(radius * Math.cos(angle));
            hexVertex[i][1] = 0.0f;
            hexVertex[i][2] = centerZ + (float)(radius * Math.sin(angle));
        }

        float[] center = {centerX, 0.0f, centerZ};
        float[] apex = {centerX, height, centerZ};

        // Base: 6 triángulos
        for (int i = 0; i < 6; i++) {
            int next = (i + 1) % 6;
            coords.add(center[0]); coords.add(center[1]); coords.add(center[2]);
            coords.add(hexVertex[i][0]); coords.add(hexVertex[i][1]); coords.add(hexVertex[i][2]);
            coords.add(hexVertex[next][0]); coords.add(hexVertex[next][1]); coords.add(hexVertex[next][2]);
        }

        // Caras laterales: 6 triángulos
        for (int i = 0; i < 6; i++) {
            int next = (i + 1) % 6;
            coords.add(hexVertex[i][0]); coords.add(hexVertex[i][1]); coords.add(hexVertex[i][2]);
            coords.add(hexVertex[next][0]); coords.add(hexVertex[next][1]); coords.add(hexVertex[next][2]);
            coords.add(apex[0]); coords.add(apex[1]); coords.add(apex[2]);
        }

        float[] arraysV = new float[coords.size()];
        for (int i = 0; i < coords.size(); i++) arraysV[i] = coords.get(i);
        return arraysV;
    }

    public void draw(float[] mvpMatrix) {
        GLES20.glUseProgram(mProgram);

        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);

        mvpMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);

        colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Dibujar cada triángulo con un color distinto
        int triCount = vertexCount / 3;
        for (int i = 0; i < triCount; i++) {
            GLES20.glUniform4fv(colorHandle, 1, colores[i % colores.length], 0);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, i * 3, 3);
        }

        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}