package com.prograavanzada.primitivas;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Point { //Modifica o necesario para que se dibuje un punto

    //(4)variables forman parte de cualquier primitva------------
    private final FloatBuffer vertexBuffer; //buffer de decimales, permite almacenar coordenadas de los vertices -Almacena coordenadas de manera que OpenGL los interprete

    private final int mProgram; //rerpresenta el programa del OpenGL con los Shaders - Identificador de OpenGL y los shaders - rerpresenta el programa del OpenGL con los Shaders - - identificador del programa

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


         //cargar shaders - iniciar primitvas
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode); //recibe el tipo de shader y el codigo del shader
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);//carga el fragment

        //crea el programa OpenGL en la gpu
        mProgram = GLES20.glCreateProgram(); // programa dentro del OPENGL es un contenedor y une el vertex con el fragment
        GLES20.glAttachShader(mProgram, vertexShader); // añade los shaders al programa
        GLES20.glAttachShader(mProgram, fragmentShader);//carga los shaders

        //permite ensamblar el programa, revisa que los shaders sean compatibles
        GLES20.glLinkProgram(mProgram);
        
    }

    //permite dibujar los vertices usando el programa de shaders - es la parte final del ciclo de renderizado
    public void draw(){

        //pasos necesarios dibujar primitiva
        GLES20.glUseProgram(mProgram);//1-activar el programa
        positionHnadle = GLES20.glGetAttribLocation(mProgram, "vPosition");//2-obtener la posicion - vienen desde el vertex shader
        GLES20.glEnableVertexAttribArray(positionHnadle);//3-activar el arreglo/array de vertices
        GLES20.glVertexAttribPointer(//4-especficiar como lee los datos del buffer
                    positionHnadle,
                COORDS_POR_VERTEX,
                GLES20.GL_FLOAT, //tipo de datos
                false, //se normalizan los datos?
                vertexStride, //numero de bytes por vertice
                vertexBuffer//buffer por donde vienen los datos
        );
        colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");//5-poner el color
        GLES20.glUniform4fv(colorHandle, 1, color, 0);//ientificar el color desde el fragment shader

        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);//6-dibujar - dibuja un punto, vertices punto=1, vertices linea=2
        GLES20.glDisableVertexAttribArray(positionHnadle);//7-finaliza desactivando el arreglo - limpieza

    }


    ///cargar los shaders - vertex y fragment - se envia como cadena de texto
    /// //siempre cargar los shaders
    private int loadShader(int type, String shaderCode){
        int shader = GLES20.glCreateShader(type); // carga el vertex y el fragment - crea un shader vacio vy luego lo implementa
        GLES20.glShaderSource(shader, shaderCode);//carga el codigo en este
        GLES20.glCompileShader(shader);//compilar el shader
        return shader;//retornar el id del shader
    }


    private final String vertexShaderCode = "attribute vec4 vPosition;" +
            "void main(){" +
            "gl_Position = vPosition;" +
            "gl_PointSize = 100.0;" +
            "}";//posiciones - punto es vector de 4 componentes : cada vertices tiene 4 componentes
    private final String fragmentShaderCode = "precision mediump float;" +
            "uniform vec4 vColor;" +
            "void main(){" +
            "gl_FragColor = vColor;" +
            "}";//precision datos del shader, es necesario para que no salga el grafico diferente



}


