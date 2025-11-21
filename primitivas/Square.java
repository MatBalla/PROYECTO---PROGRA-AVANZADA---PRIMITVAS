package com.prograavanzada.practicaeri;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Square {
    private FloatBuffer vertexBuffer;
    private final int mProgram;
    private int positionHandle, colorHandle;

    static final int COORDS_POR_VERTEX = 3;

    private final int vertexCount = squareCoords.length/COORDS_POR_VERTEX;// 9/3 = 3
    private final int vertexStride = COORDS_POR_VERTEX*4; //cada vertice necesita 12 bytes

    float[] color = {1.0f, 1.0f, 1.0f, 1.0f};


    static float squareCoords[] = {
             0.25f, 0.75f, 0.0f,//vertice superior izquierdo - 0 - siempre comienza desde aqui
             0.25f, 0.0f, 0.0f, //vertice inferior izquierdo - 1
             1.0f, 0.0f, 0.0f, // vertice inferior derecho - 2
             1.0f, 0.75f, 0.0f // vertice superior derecho - 3
    };

    private final short drawOrder[] = {
      0,1,2, //primer triangulo
      0,2,3  //segundo triangulo
    };

    private final ShortBuffer shortBuffer;

    public Square(){
        //copia todo de clase Point
        //reserva de memoria para que la gpu lo lea directo + rápido
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(squareCoords.length * 4);
        //que la ejecución utilice el orden propuesto
        byteBuffer.order(ByteOrder.nativeOrder());

        vertexBuffer = byteBuffer.asFloatBuffer();
        //copiar las coordenadas hacia el buffer que va al gpu
        vertexBuffer.put(squareCoords);
        //situar el cursor en el centro
        vertexBuffer.position(0);


        ByteBuffer sb = ByteBuffer.allocateDirect(drawOrder.length*4);
        sb.order(ByteOrder.nativeOrder());
        shortBuffer = sb.asShortBuffer();
        shortBuffer.put(drawOrder);
        shortBuffer.position(0);


        //metodo loadShader en clase MyGLRenderer para solo llamar
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
                GLES20.GL_FLOAT,//tipo de dato
                false,//nromalizado?
                COORDS_POR_VERTEX*4, //12 bytes por vertice
                vertexBuffer
        );

        colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        GLES20.glUniform4fv(colorHandle, 1, color, 0);

        //

        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES,
                drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT,
                shortBuffer
                );
        GLES20.glDisableVertexAttribArray(positionHandle);



    }



}
