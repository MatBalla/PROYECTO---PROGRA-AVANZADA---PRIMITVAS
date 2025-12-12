package com.grafico3d;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

public class Sphere {

    private FloatBuffer vertexBuffer;
    private final int mProgram;

    private int positionHandle, colorHandle, mvpMatrixHandle;
    private int vertexCount;
    private int indexCount;

    private final int vertexStride = 3 * 4;
    private float[] color= new float[]{0.60f, 0.70f, 0.80f, 0.40f};
    private ShortBuffer indexBuffer;
    private FloatBuffer wireVertexBuffer;
    private ShortBuffer wireIndexBuffer;
    public boolean showWireframe = true;
    private int wireIndexCount;
    private float[] modelMatrix = new float[16];

    public Sphere(float radius, int stacks, int slices) {
        float[] vertices = createSphereVertices(radius, stacks, slices);
        short[] indices = createSphereIndices(stacks, slices);

        vertexCount = vertices.length / 3;
        indexCount = indices.length;

        vertexBuffer = createFloatBuffer(vertices);
        indexBuffer = createShortBuffer(indices);

        //Malla
        float[] wireVertices = createSphereVertices(radius, 50, 50);
        short[] wireIndices = createWireIndices(50, 50);
        wireVertexBuffer = createFloatBuffer(wireVertices);
        wireIndexBuffer = createShortBuffer(wireIndices);
        wireIndexCount = wireIndices.length;

        Matrix.setIdentityM(modelMatrix, 0);


        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
    }

    private FloatBuffer createFloatBuffer(float[] data) {
        ByteBuffer bb = ByteBuffer.allocateDirect(data.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer buffer = bb.asFloatBuffer();
        buffer.put(data);
        buffer.position(0);
        return buffer;
    }

    private ShortBuffer createShortBuffer(short[] data) {
        ByteBuffer bb = ByteBuffer.allocateDirect(data.length * 2);
        bb.order(ByteOrder.nativeOrder());
        ShortBuffer buffer = bb.asShortBuffer();
        buffer.put(data);
        buffer.position(0);
        return buffer;
    }

    private float[] createSphereVertices(float radius, int stacks, int slices) {
        List<Float> coords = new ArrayList<>();

        for (int i = 0; i <= stacks; i++) {
            double phi = Math.PI * i / stacks;
            double sinPhi = Math.sin(phi);
            double cosPhi = Math.cos(phi);

            for (int j = 0; j <= slices; j++) {
                double theta = 2.0 * Math.PI * j / slices;
                double sinTheta = Math.sin(theta);
                double cosTheta = Math.cos(theta);

                float x = (float) (radius * sinPhi * cosTheta);
                float y = (float) (radius * cosPhi);
                float z = (float) (radius * sinPhi * sinTheta);

                // Agrega las coordenadas x, y, z a la lista
                coords.add(x);
                coords.add(y);
                coords.add(z);
            }
        }

        // Convierte la lista de Float a array de float[]
        float[] vertexArray = new float[coords.size()];
        for (int i = 0; i < coords.size(); i++) {
            vertexArray[i] = coords.get(i);
        }

        return vertexArray;
    }
    private short[] createSphereIndices(int stacks, int slices) {
        List<Short> indexList = new ArrayList<>();

        for (int i = 0; i < stacks; i++) {
            for (int j = 0; j < slices; j++) {

                int first = (i * (slices + 1)) + j;
                int second = first + slices + 1;

                indexList.add((short) first);
                indexList.add((short) second);
                indexList.add((short) (first + 1));

                indexList.add((short) (first + 1));
                indexList.add((short) second);
                indexList.add((short) (second + 1));
            }
        }

        short[] indices = new short[indexList.size()];
        for (int i = 0; i < indexList.size(); i++) {
            indices[i] = indexList.get(i);
        }

        return indices;
    }

    private short[] createWireIndices(int stacks, int slices) {
        List<Short> indexList = new ArrayList<>();

        for (int i = 0; i <= stacks; i++) {
            for (int j = 0; j < slices; j++) {
                int first = i * (slices + 1) + j;
                int second = first + 1;
                indexList.add((short) first);
                indexList.add((short) second);
            }
        }

        for (int j = 0; j <= slices; j++) {
            for (int i = 0; i < stacks; i++) {
                int first = i * (slices + 1) + j;
                int second = first + slices + 1;
                indexList.add((short) first);
                indexList.add((short) second);
            }
        }

        short[] indices = new short[indexList.size()];
        for (int i = 0; i < indexList.size(); i++) {
            indices[i] = indexList.get(i);
        }

        return indices;
    }
    public void draw(float[] vpMatrix) {
        float[] mvpMatrix = new float[16];
        Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, modelMatrix, 0);

        GLES20.glUseProgram(mProgram);

        int positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);

        int mvpMatrixHandle = GLES20.glGetUniformLocation(mProgram, "vMVPMatrix");
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);

        int colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        GLES20.glUniform4fv(colorHandle, 1, color, 0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexCount, GLES20.GL_UNSIGNED_SHORT, indexBuffer);
        GLES20.glDisableVertexAttribArray(positionHandle);

        if (showWireframe) {
            GLES20.glEnableVertexAttribArray(positionHandle);
            GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, vertexStride, wireVertexBuffer);
            GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);
            GLES20.glUniform4fv(colorHandle, 1, new float[]{0.02f, 0.02f, 0.05f, 0.95f}, 0);
            GLES20.glLineWidth(2.5f);
            GLES20.glDrawElements(GLES20.GL_LINES, wireIndexCount, GLES20.GL_UNSIGNED_SHORT, wireIndexBuffer);
            GLES20.glDisableVertexAttribArray(positionHandle);
        }
    }

    private final String vertexShaderCode =
            "uniform mat4 vMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = vMVPMatrix *(vPosition + vec4(0.3, 0.0, 0.3, 0.0));" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

}