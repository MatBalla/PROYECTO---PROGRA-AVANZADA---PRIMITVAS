package com.prograavanzada.practicaeri;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Triangle {
    private FloatBuffer vertexBuffer;
    private final int mProgram;
    private int positionHandle, colorHandle;
    static final int COORDS_POR_VERTEX = 3;
    static float triangleCoords[] = {
        -0.5f, 0.75f, 0.0f, //vertice superior
        -0.75f, 0.25f, 0.0f, // vertice inferior izquierdo
        -0.25f, 0.25f, 0.0f // vertice inferior derecho
    };
    private final int vertexCount = triangleCoords.length/COORDS_POR_VERTEX;// 9/3 = 3
    private final int vertexStride = COORDS_POR_VERTEX*4; //cada vertice necesita 12 bytes

    float[] color = {1.0f, 1.0f, 1.0f, 1.0f};


    public Triangle(){
        //copia todo de clase Point
        //reserva de memoria para que la gpu lo lea directo + rápido
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(triangleCoords.length * 4);
        //que la ejecución utilice el orden propuesto
        byteBuffer.order(ByteOrder.nativeOrder());

        vertexBuffer = byteBuffer.asFloatBuffer();
        //copiar las coordenadas hacia el buffer que va al gpu
        vertexBuffer.put(triangleCoords);
        //situar el cursor en el centro
        vertexBuffer.position(0);


        //metodo loadShader en MyGLRenderer para solo llamar
        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);

    }

    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "void main(){" +
                    "gl_Position = vPosition;" +
                    "}";

    //especifica precisión para que el gráfico salga exacto
    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main(){" +
                    "gl_FragColor = vColor;" +
                    "}";
    public void draw(){
        GLES20.glUseProgram(mProgram);

        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        GLES20.glEnableVertexAttribArray(positionHandle);

        GLES20.glVertexAttribPointer(
                positionHandle,
                COORDS_POR_VERTEX, //3 vertices
                GLES20.GL_FLOAT,
                false,
                vertexStride, //tamanio vertices
                vertexBuffer
        );

        colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        GLES20.glUniform4fv(colorHandle, 1, color, 0);

        //GL_TRIANGLES - une los vertices   <I
        //GL_TRINAGLE_STRIP - comparten lado de trignulos   <I>
        //GL_TRIANGLE_FAN - comparten vertice de trignulos   I><I
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
        GLES20.glDisableVertexAttribArray(positionHandle);

    }


}
