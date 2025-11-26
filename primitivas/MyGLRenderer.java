package com.prograavanzada.practicaeri;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {

    //puntos y lineas
    private Point point;
    private Point2 point2;
    private Point3 point3;
    private Line line; //instancia clase linea
    private Line2 line2;


    //lineas referencia plano x y y
    private LineaReferencia lineaRefrencia;
    private LineaReferenciaY lineaReferenciaY;

    //triangulo
    private Triangle triangulo;

    //cuadrado
    private Square cuadrado;

    //circulo
    private Circle circulo;

    //cilindro

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        //carita feliz con baston
        //point.draw();
        //line.draw();
        //point2.draw();
        //point3.draw();
        //line2.draw();

        //lineas referencia plano x y y - comentar para ejcutar linea normales
        lineaRefrencia.draw();
        lineaReferenciaY.draw();

        //trangulo
        //triangulo.draw();


        //cuadrado
        //cuadrado.draw();


        //circulo
        //circulo.draw();


    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES20.glViewport(0,0,width,height);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.1961f, 0.2941f, 0.4667f, 1.0f) ;

        //puntos y lineas
        //point = new Point();
        //line = new Line();
        //point2 = new Point2();
       // point3 = new Point3();
        //line2 = new Line2();


        //lineas referencia plano x y y
        lineaRefrencia = new LineaReferencia();
        lineaReferenciaY = new LineaReferenciaY();

        //triangulo
        //triangulo = new Triangle();

        //cuadrado
        //cuadrado = new Square();

        //circulo
        circulo = new Circle(0.5f, 50);//radio, numero de puntos



    }



    //para cargar el shader vertex o el shader fragment (tipo de shader, código del shader em GLSL)
    public static int loadShader(int type, String shaderCode){
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        //compilar el shader
        GLES20.glCompileShader(shader);
        //retorna el id del shader
        return shader;

    }
}
