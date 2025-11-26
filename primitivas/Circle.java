package com.prograavanzada.practicaeri;

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

        circleCoords = createCircleCoords(radius, numPoints);//utiliza parametros del constructor
        vertexCount = circleCoords.length/COORDS_POR_VERTEX; //vertices totales


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


    private float[] createCircleCoords(float radius, int numPoints) {
        List<Float> coords = new ArrayList<>();

        // Centro
        coords.add(0.0f);
        coords.add(0.0f);
        coords.add(0.0f);

        double angle = 2.0 * Math.PI / numPoints;

        // Puntos del círculo
        for (int i = 0; i <= numPoints; i++) {
            double angle2 = angle * i;
            coords.add((float) (radius * Math.cos(angle2)));
            coords.add((float) (radius * Math.sin(angle2)));
            coords.add(0.0f);
        }

        float[] arrayV = new float[coords.size()];
        for (int i = 0; i < coords.size(); i++) {
            arrayV[i] = coords.get(i);
        }

        return arrayV;
    }


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

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vertexCount ); //GL_TRIANGLE_FAN por el punto en común
        GLES20.glDisableVertexAttribArray(positionHandle);

    }






}