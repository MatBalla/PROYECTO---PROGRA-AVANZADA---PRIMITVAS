package com.cilindro;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {

    private Cylinder cilindro; //Intancia de la clase Cylinder

    private final float[] mVPMatrix = new float[16]; //matriz que combina la posición de la cámara y la perspectiva
    private final float[] mProjectionMatrix = new float[16]; //Matriz de Proyección - Define la perspectiva
    private final float[] mViewMatrix = new float[16]; //Matriz de Vista - Define la posición y orientación de la cámara

    //Parámetros de cámara
    public float camaraX = 0.0f; //Coordenada horizonal
    public float camaraY = 3.0f; //Coordenada vertical
    public float camaraZ = 2.9f; //Coordenada de profundidad

    @Override
    public void onDrawFrame(GL10 gl) { //Se llama continuamente (muchas veces por segundo) para redibujar el contenido

        //Borra la capa de color y borra el buffer de profundidad para el nuevo fotograma
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        //Calcula dónde está la cámara y hacia dónde está apuntando
        Matrix.setLookAtM( //Construye matriz de vista - define un punto de vista
                mViewMatrix, //guardar el resultado en la matriz mViewMatrix
                0, //empieza en posicion cero
                camaraX, camaraY, camaraZ,  // posición de cámara en X, Y y Z
                0f, 3f, 2.9f,                 // hacia dónde mira (centro) - punto de interés: hacia dónde está mirando la cámara.
                0f, 1f, 0f                  // vector "arriba" - cuál es la dirección "hacia el cielo" para que la escena no se dibuje de cabeza.
        );

        //Dónde está la cámara (mViewMatrix) y cómo es la perspectiva
        Matrix.multiplyMM( //Multiplica dos matrices de 4x4
                mVPMatrix, 0, //guarda el resultado final de la multiplicación
                mProjectionMatrix, 0, //Matriz onSurfaceChanged 
                mViewMatrix, 0 //Matriz mViewMatrix
        );

        // ⛔ IMPORTANTE: pasa matriz de transformación(mVPMatrix) combinada a nuestro objeto 3D y dibuja
        cilindro.draw(mVPMatrix);
    }

    //Ajustar el área de dibujo
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

        GLES20.glViewport(0, 0, width, height); //Definir el Área de Dibujo

        float ratio = (float) width / height; //Calcula la proporción

        Matrix.frustumM( //Configurar el Lente de Perspectiva
                mProjectionMatrix, 0, //crea una matriz de perspectiva
                -ratio, ratio, //definen los límites izquierdo y derecho del área visible, usando la proporción
                -1, 1, //límites inferior y superior
                1, 10 // limite visibilidad cercana y lejana
        );
    }

    //crea la superficie de dibujo
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); //color fondo

        GLES20.glEnable(GLES20.GL_DEPTH_TEST); //Habilitar la Profundidad

        // ‼️ ESTE ES EL CILINDRO NUEVO Y SIMPLE
        cilindro = new Cylinder(0.5f, 1.0f, 32);//Crea el cilindro: radio (0.5), altura (1.0) y segmentos o numero de lados planos(32)
    }

    // Dejas igual tu cargador de shaders
    public static int loadShader(int type, String shaderCode) {

        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }
}

