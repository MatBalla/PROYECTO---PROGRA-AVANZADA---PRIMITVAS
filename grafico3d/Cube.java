package com.grafico3d;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

public class Cube {

    private FloatBuffer bottomBuffer;   // tapa inferior - Almacena las coordenadas X, Y, Z de la tapa inferior.
    private FloatBuffer topBuffer;      // tapa superior - Almacena las coordenadas X, Y, Z de la tapa superior.
    private FloatBuffer sideBuffer;     // superficie lateral - Almacena las coordenadas X, Y, Z de la superficie lateral (cuerpo).

    private int bottomCount; // El número total de puntos (vértices) que tiene la tapa inferior
    private int topCount; // El número total de puntos (vértices) que tiene la tapa superior.
    private int sideCount; // El número total de puntos (vértices) que tiene la superficie lateral.

    private int mProgram; //Es el cerebro que contiene las instrucciones de cómo dibujar y colorear
    private int positionHandle; //Es dónde se le pasan las coordenadas X, Y, Z.
    private int colorHandle; //Es dónde se le pasa el color del objeto.
    private int matrixHandle; //Es dónde se le pasan las instrucciones para mover, rotar o escalar el cilindro en el espacio 3D.

    static final int COORDS_PER_VERTEX = 3; //Define que cada punto (vértice) se compone de 3 coordenadas: X, Y y Z
    static final int STRIDE = COORDS_PER_VERTEX * 4; //distancia en bytes entre el inicio de un punto y el inicio del siguiente. Se multiplica por 4 porque un float ocupa 4 bytes en memoria


    //Vertex Shader se encarga de la geometría y la posición. Se ejecuta una vez por cada punto (vértice) del cilindro.
    private final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main(){" +
                    "    gl_Position = uMVPMatrix * (vPosition + vec4(0.3,0.0,-0.3,0.0));" +
                    "}";



    //Fragment Shader se encarga del color final. Se ejecuta una vez por cada píxel que el cilindro ocupa en la pantalla.
    private final String fragmentShaderCode =
            "precision mediump float;" +  //Define la precisión de los números que se usan para el color.
                    "uniform vec4 vColor;" + //Es el color que queremos aplicar al cilindro
                    "void main(){ gl_FragColor = vColor; }"; //Asigna el color que nos pasaron (vColor) al píxel que se está dibujando en ese momento, guardándolo en la variable de salida gl_FragColor



    //Constructor: envia de parametro (radio, altura, segmentos)
    public Cube(float radius, float height, int segments) {

        createBottom(radius, height, segments); //llama al método - Determina las coordenadas (X, Y, Z) de la base del cilindro.
        createTop(radius, height, segments); //llama al método - Determina las coordenadas (X, Y, Z) del techo del cilindro
        createSide(radius, height, segments); //llama a, metodo - Determina las coordenadas (X, Y, Z) del cuerpo cilíndrico.

        int vs = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode); //Carga y compila el Vertex Shader
        int fs = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode); //Carga y compila el Fragment Shader.

        mProgram = GLES20.glCreateProgram(); //Crea un contenedor vacío para nuestro programa de dibujo
        GLES20.glAttachShader(mProgram, vs); //Adjunta el Vertex Shader (vs) al contenedor del programa.
        GLES20.glAttachShader(mProgram, fs); //Adjunta el Fragment Shader (fs) al contenedor del programa.
        GLES20.glLinkProgram(mProgram); //Enlaza los dos shaders adjuntos.
    }



    // Crea la base (tapa inferior) del cilindro. -recibe el radio (r), la altura (h) y el número de segmentos
    private void createBottom(float r, float h, int seg) {

        ArrayList<Float> list = new ArrayList<>(); //Crea una lista (ArrayList) temporal para guardar todas las coordenadas X, Y y

        float y = -h / 2f; //Calcula la altura de la base. - coloca la base debajo del centro y=0

        list.add(0f); list.add(y); list.add(0f); // centro - Define el punto central. - En la técnica TRIANGLE_FAN, este es el punto que se conecta a todos los demás.

        //Ejemplo: r=0.5, h=1, seg=3; ang=2*pi*1/3 = 120; X coordenada = 0.5* coseno(120) = -0.25; y=-1/2=-0.5 ; Z coordenada = 0.5*seno(120)= 0.433 y asi por cada punto
        for (int i = 0; i <= seg; i++) { // Bucle - Por cada segmento, se calcula un punto en el borde del círculo.
            double ang = 2 * Math.PI * i / seg; //Determina el ángulo en radianes para el punto actual. - calcula 360grados por i /segmentos
            list.add((float)(r * Math.cos(ang))); //Calcula la coordenada X - Usa el coseno del ángulo, lo multiplica por el radio
            list.add(y); // Añade la coordenada Y - que es la altura de la base - coordenada negativa
            list.add((float)(r * Math.sin(ang))); //Calcula la coordenada Z - Usa el seno del ángulo, lo multiplica por el radio
        }

        bottomCount = list.size() / 3; //Calcula el número de vértices. - 3 por coordenadas x,y,z
        bottomBuffer = toBuffer(list); //Convierte la lista en el formato especial FloatBuffer
    }



    // Crea la tapa (tapa superior) del cilindro. -recibe el radio (r), la altura (h) y el número de segmentos
    //Es practicamente el metodo del createBottom de la tapa inferior pero cambia en la coordenada Y
    private void createTop(float r, float h, int seg) {

        ArrayList<Float> list = new ArrayList<>(); //lista temporal para guardar las coordenadas X, Y y Z

        float y = +h / 2f;//Calcula la altura de la base. - coloca la base encima del centro y=0

        list.add(0f); list.add(y); list.add(0f); // centro - Define el punto central. - En la técnica TRIANGLE_FAN, este es el punto que se conecta a todos los demás.

        //Misma lógica for del método createBottom
        //Ejemplo: r=0.5, h=1, seg=3; ang=2*pi*1/3 = 120; X coordenada = 0.5* coseno(120) = -0.25; y=1/2=0.5 ; Z coordenada = 0.5*seno(120)= 0.433 y asi por cada punto
        for (int i = 0; i <= seg; i++) { // Bucle - Por cada segmento, se calcula un punto en el borde del círculo.
            double ang = 2 * Math.PI * i / seg; //Determina el ángulo en radianes para el punto actual. - calcula 360grados por i /segmentos
            list.add((float)(r * Math.cos(ang))); //Calcula la coordenada X - Usa el coseno del ángulo, lo multiplica por el radio
            list.add(y); // Añade la coordenada Y - que es la altura de la base - coordenada positiva
            list.add((float)(r * Math.sin(ang))); //Calcula la coordenada Z - Usa el seno del ángulo, lo multiplica por el radio
        }

        topCount = list.size() / 3; //Calcula el número de vértices. - 3 por coordenadas x,y,z
        topBuffer = toBuffer(list); //Convierte la lista en el formato especial FloatBuffer
    }



    // Crea la superficie curva del cilindro utilizando el GL_TRIANGLE_STRIP - recibe el radio (r), la altura (h) y el número de segmentos
    private void createSide(float r, float h, int seg) {

        ArrayList<Float> list = new ArrayList<>(); //lista temporal para guardar las coordenadas X, Y y Z

        float y1 = -h / 2f; //Calcula altura inferior., abajo de y=0
        float y2 = +h / 2f; //Calclula altura superior, encima de y=0


        //Misma lógica for del método createBottom y createTop, utiliza las dos alturas
        //Ejemplo: r=0.5, h=1, seg=3; ang=2*pi*1/3 = 120; X coordenada = 0.5* coseno(120) = -0.25; y1=-1/2=0.5 ;y2=1/2=0.5; Z coordenada = 0.5*seno(120)= 0.433 y asi por cada punto
        for (int i = 0; i <= seg; i++) {

            double ang = 2 * Math.PI * i / seg;//Determina el ángulo en radianes para el punto actual. - calcula 360grados por i /segmentos
            float x = (float)(r * Math.cos(ang));//Calcula la coordenada X - Usa el coseno y el radio (r) para encontrar la coordenada
            float z = (float)(r * Math.sin(ang));//Calcula la coordenada Z - Usa el seno y el radio (r) para encontrar la coordenada

            // abajo - Añade el punto inferior - Guarda las coordenadas X, Y1, Z en la lista
            list.add(x); list.add(y1); list.add(z);

            // arriba - Añade el punto superior - Guarda las coordenadas X, Y2, Z en la lista
            list.add(x); list.add(y2); list.add(z);
        }

        sideCount = list.size() / 3; //Calcula el número de vértices. - 3 por coordenadas x,y,z
        sideBuffer = toBuffer(list); //Convierte la lista en el formato especial FloatBuffer
    }



    // Recibe la matriz de transformación (mvpMatrix), que contiene las instrucciones de movimiento y perspectiva.
    public void draw(float[] mvpMatrix) {

        GLES20.glUseProgram(mProgram);

        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        matrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(matrixHandle, 1, false, mvpMatrix, 0); //Envía la matriz de movimiento, rotación y escala

        GLES20.glEnableVertexAttribArray(positionHandle);

        // ---------------- TAPAS -----------------
        GLES20.glUniform4fv(colorHandle, 1, new float[]{0.45f, 0.35f, 0.65f, 1.0f}, 0);
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, STRIDE, bottomBuffer);//lee como se formará la base

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, bottomCount); //Dibuja e interpreta

        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, STRIDE, topBuffer);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, topCount);

        // ---------------- LATERAL ----------------
        GLES20.glUniform4fv(colorHandle, 1, new float[]{0.35f, 0.25f, 0.55f, 1.0f}, 0);
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, STRIDE, sideBuffer);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, sideCount);

        GLES20.glDisableVertexAttribArray(positionHandle);
    }

    //Convierte la lista de coordenadas que creamos en el formato exacto que la (GPU) necesita leer
    private FloatBuffer toBuffer(ArrayList<Float> list) { //Recibe la lista de números float (coordenadas) y debe devolver un FloatBuffer.
        float[] a = new float[list.size()]; //Crea un array de Java tradicional (float[]) con el mismo tamaño que la lista.
        for (int i = 0; i < list.size(); i++) a[i] = list.get(i); //copia los datos de la Lista al array a

        //Crea un contenedor en la memoria nativa. El tamaño es el número de floats (a.length) multiplicado por 4 (porque un float ocupa 4 bytes).
        ByteBuffer bb = ByteBuffer.allocateDirect(a.length * 4);
        bb.order(ByteOrder.nativeOrder()); //Se asegura de que los bytes se organicen en el orden correcto
        FloatBuffer fb = bb.asFloatBuffer(); //Convierte el contenedor de bytes (ByteBuffer) en un FloatBuffer
        fb.put(a); //Copia todos los datos del array (a) dentro del FloatBuffer (fb) en la memoria nativa.
        fb.position(0); //Asegura que se vea desde el primer punto
        return fb; //Retorna el FloatBuffer
    }

}
