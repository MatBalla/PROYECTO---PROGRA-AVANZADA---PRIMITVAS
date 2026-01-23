package com.prograavanzada.iluminacionspotlighnuevo;
import android.content.Context;
import android.opengl.GLSurfaceView;

public class MyGLSurfaceView extends GLSurfaceView {
    private final MyGLRenderer renderer;

    public MyGLSurfaceView(Context context) {
        super(context);
        //usar la versión 2 como estándar
        setEGLContextClientVersion(2);
        renderer = new MyGLRenderer();
        setRenderer(renderer);

    }
}
