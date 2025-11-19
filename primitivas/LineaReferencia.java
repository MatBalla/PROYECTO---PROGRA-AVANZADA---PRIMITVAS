package com.prograavanzada.practicaeri;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class LineaReferencia {
    //configuracion el crear una linea

    //copia codigo del Point
    private final FloatBuffer vertexBuffer;
    private final int mProgram;//arranca el OpenGl
    private int positionHandle, colorHandle;

    private final float[] lineCoord = {
            //coordenadas de los vertices, necesita dos puntos, entre -1,1
            -0.98f, 0.0f, 0.98f, 0.0f

    };//arreglo de coordenadas de los vertices

    float[] color = {1.0f, 1.0f, 1.0f, 1.0f};

    public LineaReferencia() {
        //copia todo de clase Point
        //reserva de memoria para que la gpu lo lea directo + rápido
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(lineCoord.length * 4);
        //que la ejecución utilice el orden propuesto
        byteBuffer.order(ByteOrder.nativeOrder());

        vertexBuffer = byteBuffer.asFloatBuffer();
        //copiar las coordenadas hacia el buffer que va al gpu
        vertexBuffer.put(lineCoord);
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


    //para cargar el shader vertex o el shader fragment (tipo de shader, código del shader em GLSL)
    private int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        //compilar el shader
        GLES20.glCompileShader(shader);
        //retorna el id del shader
        return shader;

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


    public void draw() {
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
                2,
                GLES20.GL_FLOAT, //arreglo
                false, //no se normalizan los datos
                0,//inicia en 0
                vertexBuffer
        );

        //definir el color, dubjo de la primitiva, desactivación del arreglo(limpieza)
        colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        GLES20.glUniform4fv(colorHandle, 1, color, 0);

        GLES20.glLineWidth(10);

        //dibujar
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, 2);
        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}
