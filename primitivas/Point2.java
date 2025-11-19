package com.prograavanzada.practicaeri;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Point2 {
    //ATRIBUTOS PRINCIPALES
    private final FloatBuffer vertexBuffer;
    // almacena coordenadas de manera que OPENGL las pueda interpretar
    private final int mProgram;
    // identificador del programa que une los shaders
    private int positionHandle, colorHandle;
// ¡! Almacenan los identificadores para acceder/identificar a las variables de los shaders


    //ATRIBUTOS PARA VÉRTICES

    //para especificar el tamaño de coordenadas de cada vértice
    //cada vértice tiene 3 coordenadas (3D)
    static final int COORDS_POR_VERTEX = 3;

    //las coordenadas de los vértices siempre serán arreglos
    //el arreglo de coordenadas depende del número de vértices
    //coordenadas de cada uno de los vértices
    static float pointCoord[] = {0.5f, 0.5f, 0.0f};

    ///
    //contar el número de vértices
    private final int vertexCount = pointCoord.length/COORDS_POR_VERTEX;
    //define cuantos bytes ocupa cada vértice en el buffer
    //(cada coordenada de flotantes representa 4 bytes de memoria)
    private final int vertexStride = COORDS_POR_VERTEX*4;
    //color a la figura
    float[] color = {1.0f, 1.0f, 0.0f, 1.0f};

    //CONSTRUCTOR: crea el buffer para enviar las coordenadas a la gpu
    //compila los shader
    //crea el programa opengl para render

    public Point2(){
        //reserva de memoria para que la gpu lo lea directo + rápido
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(pointCoord.length*4);
        //que la ejecución utilice el orden propuesto
        byteBuffer.order(ByteOrder.nativeOrder());

        vertexBuffer = byteBuffer.asFloatBuffer();
        //copiar las coordenadas hacia el buffer que va al gpu
        vertexBuffer.put(pointCoord);
        //situar el cursor en el centro
        vertexBuffer.position(0);

        //para todas las primitivas
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        //un "contenedor" que une los shaders
        mProgram = GLES20.glCreateProgram();
        //asociacón de shaders
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
    }

    private final String vertexShaderCode =
            "attribute vec4 vPosition;"+
                    "void main(){" +
                    "gl_Position = vPosition;" +
                    "gl_PointSize = 100.0;" +
                    "}";

    //especifica precisión para que el gráfico salga exacto
    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main(){" +
                    "gl_FragColor = vColor;"+
                    "}";


    //dibuja los vértices (cómo se construye la primitiva)
    public void draw(){
        //usa el programa del shader definido en el constructor
        GLES20.glUseProgram(mProgram);
        //obtener la posición
        //las posiciones vienen del vertex shader en un atributo del GLSL
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        //activar el arreglo de vertices
        GLES20.glEnableVertexAttribArray(positionHandle);
        //especificar como lee los datos el buffer
        GLES20.glVertexAttribPointer(
                positionHandle,
                COORDS_POR_VERTEX,
                GLES20.GL_FLOAT, //tipo de dato
                false, //si se normalizan los datos
                vertexStride,
                vertexBuffer
        );

        //definir el color, dubjo de la primitiva, desactivación del arreglo(limpieza)
        colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        GLES20.glUniform4fv(colorHandle, 1, color, 0);
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, vertexCount);
        GLES20.glDisableVertexAttribArray(positionHandle);

    }



    //para cargar el shader vertex o el shader fragment (tipo de shader, código del shader em GLSL)
    private int loadShader(int type, String shaderCode){
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        //compilar el shader
        GLES20.glCompileShader(shader);
        //retorna el id del shader
        return shader;

    }





}



