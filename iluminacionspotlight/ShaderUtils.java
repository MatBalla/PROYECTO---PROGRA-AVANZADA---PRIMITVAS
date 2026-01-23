package com.prograavanzada.iluminacionspotlighnuevo;

import android.opengl.GLES20;

public class ShaderUtils{
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
    public static int createProgram(String vertex,String fragment){
        int vertexS = loadShader(GLES20.GL_VERTEX_SHADER,vertex);
        int fragmentS = loadShader(GLES20.GL_FRAGMENT_SHADER,fragment);
        int program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program,vertexS);
        GLES20.glAttachShader(program,fragmentS);
        GLES20.glLinkProgram(program);
        return program;
    }
}
