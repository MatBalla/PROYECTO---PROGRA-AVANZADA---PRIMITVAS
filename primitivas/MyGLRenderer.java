package com.prograavanzada.practicaeri;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {

    private Point point;
    private Point2 point2;
    private Point3 point3;
    private Line line; //instancia clase linea
    private Line2 line2;

    //lineas referencia plano x y y
    private LineaReferencia lineaRefrencia;
    private LineaReferenciaY lineaReferenciaY;

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        point.draw();

        line.draw();
        point2.draw();
        point3.draw();
        line2.draw();

        //lineas referencia plano x y y
        lineaRefrencia.draw();
        lineaReferenciaY.draw();
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES20.glViewport(0,0,width,height);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.1961f, 0.2941f, 0.4667f, 1.0f) ;
        point = new Point();

        line = new Line();
        point2 = new Point2();
        point3 = new Point3();
        line2 = new Line2();

        //lineas referencia plano x y y
        lineaRefrencia = new LineaReferencia();
        lineaReferenciaY = new LineaReferenciaY();
    }
}
