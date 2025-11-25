package com.progra.primitivas;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class Circle {
    private final FloatBuffer vertexBuffer;
    private final int mProgram;
    private int positionHandle, colorHandle;
    static final int COORDS_POR_VERTEX = 3;
    float[] color = {1.0f, 1.0f, 1.0f, 1.0f};
    private final int vertexCount;
    private final int vertexStride = COORDS_POR_VERTEX*4; //cada vertice necesita 12 bytes
    static float [] circleCoords;


public Circle(float radius, int numPoints){//parametro(radio(tamaño),puntos a utilizar)
    //reserva de memoria para que la gpu lo lea directo + rápido
    ByteBuffer byteBuffer = ByteBuffer.allocateDirect(circleCoords.length * 4);
    //que la ejecución utilice el orden propuesto
    byteBuffer.order(ByteOrder.nativeOrder());

    vertexBuffer = byteBuffer.asFloatBuffer();
    //copiar las coordenadas hacia el buffer que va al gpu
    vertexBuffer.put(circleCoords);
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


    private float [] createCircleCoords(float radius, int numPoints){
        List<Float> coords = new ArrayList<>();
         coords.add(0.0f); //centro en x
         coords.add(0.0f); //centro en y
         coords.add(0.0f); //centro en z

        //calculo del angulo
        double angle = 2.0*Math.PI/numPoints; //encuentra intervalo a utilizar - en radianes

        //encontrar puntos x y y
        for(int i=0; i <= numPoints; i++){
            double angle2 = angle*i; //1er angulo 36, 2do.... - va incrementando
            coords.add((float)(radius*Math.cos(angle2)));
            coords.add((float)(radius*Math.sin(angle2)));
            coords.add(0.0f);
        }


    }



}
