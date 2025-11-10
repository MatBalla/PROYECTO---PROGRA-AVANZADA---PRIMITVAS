package com.prograavanzada.primitivas;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {
    @Override
    public void onDrawFrame(GL10 gl) {
        //limpiar y dibujar la pantalla, dibuja a 60 framesporsegundo

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);//ver el color de forma constante


    }



    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // cambiar orientacion del dispositivo movil, tamanio de la pantalla

        //tamanio pantalla
        GLES20.glViewport(0,0,width, height);//toda la pantalla



    }




    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        //color de fondo RGBA
        //GLES20.glClearColor(1.0f, 0.0f, 0.0f, 1.0f); rojo

        // GLES20.glClearColor(0.0f, 1.0f, 0.0f, 1.0f); verde
        //GLES20.glClearColor(0.0f, 0.0f, 1.0f, 1.0f); //azul

        //COLORES, PEDIR A GEMINI QUE CAMBIE
        //GLES20.glClearColor(0.65f, 0.65f, 0.65f, 1.0f);//gris

        GLES20.glClearColor(0.7f, 0.85f, 1.0f, 1.0f);//celeste



    }
    //controlar el como se muestran las cosas, color
}
