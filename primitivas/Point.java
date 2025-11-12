package com.prograavanzada.primitivas;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Point { //Modifica o necesario para que se dibuje un punto

    //(4)variables forman parte de cualquier primitva------------
    private final FloatBuffer vertexBuffer; //buffer de decimales, permite almacenar coordenadas de los vertices -Almacena coordenadas de manera que OpenGL los interprete

    private final int mProgram; //rerpresenta el programa del OpenGL con los Shaders - Identificador de OpenGL y los shaders - rerpresenta el programa del OpenGL con los Shaders

    private int positionHandle, colorHandle; //identifica variable posiciones, variable colores - Almacena los identificadores de las posiciones y colores



    //atributos trabajan con los vertices------------------
    static final int COORDS_POR_VERTEX = 3; // sirve para especificar el tamaño de cada vertices, cada 3 elementos son 1 vertice - Especificar el tamaño de coordenadas de cada  vertice

    static float pointCoord[] = {0.0f, 0.0f, 0.0f}; //los vertices seran arreglos o matrices, {x,y,z} - 
    // arreglo de coordenadas depende de vertices, punto 3
    //Los vertices siempre seran arreglos
    //    El numero de coordenadas esta dado por el numero de vertices
    //    {x, y, z}

    

    private final int vertexCount = pointCoord.length/COORDS_POR_VERTEX; // identificar cuantos vertices hay dentro de la figura - Contar el numero de vertices que se generan de acuerdo al arreglo

    private final int vertexStride = COORDS_POR_VERTEX * 4; //ayuda a definir cuantos bytes ocupa cada vertice en el buffer, cada vertice ocupa 4 bytes, cada 12 un vertice
    //Definir cuantos bytes ocupara un vertice en el buffer
    //    Lo normal es que ocupe 4 bytes, dado que tiene 3 vertices, ocupara 12 bytes

    

    float color[] =  {0.7f, 0.85f, 1.0f, 1.0f} ;//darle color al elemento geometrico, colores RGBA - Representa el color, de tamaño 4 por RGBa



    //constructor-------------------

    //    Crea el buffer para enviar al GPU
    //    Complilar los vertex y los sheders
    //    Crea el programa para el renderizado
    public Point(){ // coordenadas en buffers, compila los shaders: vertex o fragment, crea el Opengl para rednerizar

        ByteBuffer buteBuffer = ByteBuffer.allocateDirect(pointCoord.length*4);//crear el buffer, Reserva de memoria directa para que OpenGl pueda leerlo mas rapido
        buteBuffer.order(ByteOrder.nativeOrder()); //certificade leer acuerdo al orden de los vertices,  Para que la ejecucion use el orden de bytes del dispositivo
        vertexBuffer = buteBuffer.asFloatBuffer(); // buffer a buffer de float, Convirtiendo a un buffer a float, por el arreglo que es de floats
        vertexBuffer.put(pointCoord); //copia coordenadas del vertice superior al GPU, Copiando las coordenas declaradas y que seran eviadas al GPU
        vertexBuffer.position(0);//situar el cursor al inicio del buffer, empieza desde el inicio, Situar el cursor en el inicio para que OpenGL lo lea desde el principio

    }


}

