package com.transformaciones;

import static com.transformaciones.MyGLRenderer.loadShader;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Piramide {
    private final FloatBuffer vertexBuffer;

    private final ShortBuffer indextBuffer;
    private final int mProgram;
    static final int COORDS_POR_VERTEX = 3;

    static float [] vertices = {
      0.0f, 1.0f, 0.0f, //vertice punta - 0
      -1.0f, -1.0f, 1.0f, //vertice izquierdo de la base - 1
       1.0f, -1.0f, 1.0f, //vertice derecho - 2
       0.0f, -1.0f, -1.0f //vertice de atrás - 3
    };

    private final short [] indices = {
      0,1,2, //cara frontal
      0,2,3, //cara derecha
      0,3,1, //cara izquierda
      1,3,2 //base - cara inferior
    };


    public Piramide(){
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        //que la ejecución utilice el orden propuesto
        byteBuffer.order(ByteOrder.nativeOrder());

        vertexBuffer = byteBuffer.asFloatBuffer();
        //copiar las coordenadas hacia el buffer que va al gpu
        vertexBuffer.put(vertices);
        //situar el cursor en el centro
        vertexBuffer.position(0);


        ByteBuffer sb = ByteBuffer.allocateDirect(indices.length*4);
        sb.order(ByteOrder.nativeOrder());
        indextBuffer = sb.asShortBuffer();
        indextBuffer.put(indices);
        indextBuffer.position(0);

        //Se encarga del primer shader
        //Se repite en todas las primitivas
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);


        //Crear el programa vacio en la GPU
        //Es un contenedor que une los dos shaders, fragment y vertex
        mProgram = GLES20.glCreateProgram();
        //Agrega los dos shaders al programa
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        //Revisar que los shaders sean compatibles
        GLES20.glLinkProgram(mProgram);
    }


    private final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;"+
                    "void main(){" +
                    "gl_Position =  uMVPMatrix * vPosition ;"+
                    "}"; //A cada coordenada de pantalla se asocia con una real

    //La presicion da informacion precisa del grafico
    private final String fragmentShaderCode =
            "precision mediump float;"+
                    "uniform vec4 vColor;" +
                    "void main(){"+
                    "gl_FragColor = vec4(0.96, 0.96, 0.86, 1.0);"+
                    "}";


    public void draw(float [] mvpMatrix){
        GLES20.glUseProgram(mProgram);

        int positionHandle = GLES20.glGetAttribLocation(mProgram,"vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);

        GLES20.glVertexAttribPointer(
                positionHandle,
                3,
                GLES20.GL_FLOAT,
                false,
                0,
                vertexBuffer
        );

        //cuando se manda el color al fragment, se evita poner ese codigo aqui

        int matrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix"); //Buscar ubicación (matriz)
        GLES20.glUniformMatrix4fv(matrixHandle, 1, false, mvpMatrix, 0); //Envía la matriz de movimiento, rotación y escala

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length, GLES20.GL_UNSIGNED_SHORT, indextBuffer);
        GLES20.glDisableVertexAttribArray(positionHandle);

    }
}
