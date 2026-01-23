package com.prograavanzada.iluminacionspotlighnuevo;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Pyramid {
    private final FloatBuffer vertexBuffer;
    private final int program;
    private final float[] vertices = {
            -1,0,1,    0,-1,0,
            1,0,-1,     0,-1,0,
            1,0,1,     0,-1,0,
            -1,0,1,    0,-1,0,
            //caras
            -1,0,-1,     -0.5f,0.5f,-0.5f,
            1,0,-1,      0.5f,0.5f,0.5f,
            0,1,0,        0,0.5f,0,

            1,0,-1,      0.5f,0.5f,-0.5f,
            1,0,1,       0.5f,0.5f,0.5f,
            0,1,0,       0,0.5f,0,

            1,0,1,       0.5f,0.5f,0.58f,
            -1,0,1,      -0.5f,0.5f,0.5f,
            0,1,0,       0,0.5f,0,

            -1,0,1,     -0.5f,0.5f,0.5f,
            -1,0,-1      -0.5f,0.5f,-0.5f,
            0,1,0,         0,0.5f,0

    };
    public Pyramid(){
        //transfrma los vertices para que la gpu interprete y genere el grafico
        ByteBuffer vb = ByteBuffer.allocateDirect(vertices.length * 4);
        vb.order(ByteOrder.nativeOrder());
        vertexBuffer = vb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        program = ShaderUtils.createProgram(vertexShaderCode,fragmentShaderCode);

    }
    public void draw(float[] mvpMatrix,float[] modelMatrix,
                     float[] lightPosition,float[] lightDirection,float[] eyePosition){
        GLES20.glUseProgram(program);
        int posHandle = GLES20.glGetAttribLocation(program,"aPosition");
        vertexBuffer.position(0); //solo si ya estan los vertices y las normales en un mismo arreglo
        //un vertice compuesto de 6 elementos
        GLES20.glVertexAttribPointer(posHandle,3,GLES20.GL_FLOAT,false,6*4,vertexBuffer);
        GLES20.glEnableVertexAttribArray(posHandle);
        //handel normal
        int normHandle = GLES20.glGetAttribLocation(program,"aNormal");
        vertexBuffer.position(3); //solo si ya estan los vertices y las normales en un mismo arreglo
        //un vertice compuesto de 6 elementos
        GLES20.glVertexAttribPointer(normHandle,3,GLES20.GL_FLOAT,false,6*4,vertexBuffer);
        GLES20.glEnableVertexAttribArray(normHandle);
        //handle de matrices
        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(program,"uMVPMatrix"),1,false,mvpMatrix,0);
        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(program,"uMVMatrix"),1,false,modelMatrix,0);
        //componentes de la luz
        GLES20.glUniform3fv(GLES20.glGetUniformLocation(program,"uLightPos"),1,lightPosition,0);
        GLES20.glUniform3fv(GLES20.glGetUniformLocation(program,"uLightDir"),1,lightDirection,0);
        GLES20.glUniform3fv(GLES20.glGetUniformLocation(program,"uEyePos"),1,eyePosition,0);
        //coseno angulo abertura,recomendable entre 10 y 60 grados
        GLES20.glUniform1f(GLES20.glGetUniformLocation(program,"uCutOff"),(float)Math.cos(Math.toRadians(12.5)));

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN,0,4); //para la base
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES,4,12); //caras

        GLES20.glDisableVertexAttribArray(posHandle);
        GLES20.glDisableVertexAttribArray(normHandle);

    }
    private final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
                    "uniform mat4 uMVMatrix;" +
                    "attribute vec3 aPosition;"+
                    "attribute vec3 aNormal;"+
                    "varying vec3 vNormal;"+
                    "varying vec3 vPosition;"+
                    "void main(){"+
                    "vPosition = vec3(uMVMatrix * vec4(aPosition,1.0));"+
                    "vNormal = mat3(uMVMatrix) * aNormal;"+
                    "gl_Position = uMVPMatrix * vec4(aPosition,1.0);"+

            "}";
    private final String fragmentShaderCode =
            "precision mediump float;"+
                    "uniform vec3 uLightPos;"+
                    "uniform vec3 uLightDir;"+
                    "uniform vec3 uEyePos;"+
                    //coseno del angulo limite
                    "uniform float uCutOff;"+
                    "varying vec3 vNormal;"+
                    "varying vec3 vPosition;"+
                    "void main(){"+
                    "vec3 normalN = normalize(vNormal);"+
                    "vec3 lightDir = normalize(uLightPos - vPosition);"+
                    //esta linea genera el efecto spothlight, si no se halla un valor optimo, no se forma el cono de luz y no ilumina
                    "float theta = dot(lightDir,normalize(-uLightDir));"+
                    "vec3 ambient = vec3(0.1);"+
                    "vec3 diffuse = vec3(0.0);"+
                    "vec3 specular = vec3(0.0);"+
                    "if(theta > uCutOff){"+
                    "float diff = max(dot(normalN,lightDir),0.0);"+
                    "diffuse = diff * vec3(1.0,0.7,0.3);"+
                    "vec3 viewDir = normalize(uEyePos -vPosition);"+
                    "vec3 reflectDir = reflect(-lightDir,normalN);"+
                    "float spec = pow(max(dot(viewDir,reflectDir),0.0),16.0);"+
                    "specular = spec * vec3(1.0);"+

                    "}"+
                    "vec3 result = ambient + diffuse + specular;"+
                    "gl_FragColor = vec4(result,1.0);"+

            "}";

}

