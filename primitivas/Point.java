package com.prograavanzada.primitivas;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Point { //Modifica o necesario para que se dibuje un punto

    //(4)variables forman parte de cualquier primitva------------
    private final FloatBuffer vertexBuffer; //buffer de decimales, permite almacenar coordenadas de los vertices

    private final int mProgram; //rerpresenta el programa del OpenGL con los Shaders

    private int positionHandle, colorHandle; //identifica variable posiciones, variable colores



    //atributos trabajan con los vertices------------------
    static final int COORDS_POR_VERTEX = 3; // sirve para especificar el tamaño de cada vertices, cada 3 elementos son 1 vertice

    static float pointCoord[] = {0.0f, 0.0f, 0.0f}; //los vertices seran arreglos o matrices, {x,y,z}
    // arreglo de coordenadas depende de vertices, punto 3

    private final int vertexCount = pointCoord.length/COORDS_POR_VERTEX; // identificar cuantos vertices hay dentro de la figura

    private final int vertexStride = COORDS_POR_VERTEX * 4; //ayuda a definir cuantos bytes ocupa cada vertice en el buffer, cada vertice ocupa 4 bytes, cada 12 un vertice

    float color[] =  {0.7f, 0.85f, 1.0f, 1.0f} ;//darle color al elemento geometrico, colores RGBA



    //constructor-------------------
    public Point(){ // coordenadas en buffers, compila los shaders: vertex o fragment, crea el Opengl para rednerizar

        ByteBuffer buteBuffer = ByteBuffer.allocateDirect(pointCoord.length*4);//crear el buffer, reserva memoria directa para que lea gpu, hace que lea mas rapiod
        buteBuffer.order(ByteOrder.nativeOrder()); //certificade leer acuerdo al orden de los vertices
        vertexBuffer = buteBuffer.asFloatBuffer(); // buffer a buffer de float
        vertexBuffer.put(pointCoord); //copia coordenadas del vertice superior al GPU
        vertexBuffer.position(0);//situar el cursor al inicio del buffer, empieza desde el inicio

    }


}
