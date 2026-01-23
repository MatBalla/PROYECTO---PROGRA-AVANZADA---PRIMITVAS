package com.prograavanzada.iluminacionspotlighnuevo;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public  class MyGLRenderer implements GLSurfaceView.Renderer {

    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mMVPMatrix = new float[16];
    private final float[] mModelMatrix = new float[16];
    private Pyramid pyramid;

    private final float[] lightPos = {0f,3f,3f}; //posicion de la luz
    private final float[] lightDir = {0f,-1f,-1f}; // direccion de la luz
    private final float[] eyePos = {0f,2f,6f}; // posicion de la vista(camara)


    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        Matrix.setLookAtM(mViewMatrix, 0,
                eyePos[0], eyePos[1], eyePos[2],   // posición del ojo (x,y,z)
                0f, 0f, 0f,     // mira al centro
                0f, 1f, 0f            //1 hacia arriba
        );
        Matrix.setIdentityM(mModelMatrix,0);
        Matrix.multiplyMM(mMVPMatrix,0,mViewMatrix,0,mModelMatrix,0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0); //ver si es mViewMatrix o mvp

        pyramid.draw(mMVPMatrix,mModelMatrix,lightPos,lightDir,eyePos);



    }
    //cambiar la orientación del disposito móvil,ajustar el tamaño de ejecución
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //usar toda la pantalla
        GLES20.glViewport(0,0,width,height);
        float ratio = (float) width / height;
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 2f, 10f);


    }
    //usado para definir el fondo de la app,es la primera en inicializar
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.0f,0.0f,0.0f,1.0f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        pyramid = new Pyramid();




    }
    public static int loadShader(int type,String shaderCode){//carga los shaders,type es el tipo del shader y shaderCode el codigo del shader en gles
        //1 Crear un shader vacio
        int shader = GLES20.glCreateShader(type);
        //2 Cargar el codigo que se usara mas adelante.Not:shaderCode respeta los principios de gles
        GLES20.glShaderSource(shader,shaderCode);
        //3 compilar el shader, implementado en gles
        GLES20.glCompileShader(shader);
        //returnar el id del shader ya estructurado
        return shader;

    }



}

