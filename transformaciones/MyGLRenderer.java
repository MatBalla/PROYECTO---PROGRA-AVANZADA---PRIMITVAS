package com.transformaciones;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {
    private Ejes ejes;
    private Piramide piramide;
    private Cone cono;
    private Cylinder cilindro;
    private Sphere esfera;
    private Torus dona;
    private TruncatedPyramid piramide_Truncada;



    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mVPMatrix = new float[16];
    private final float[] mModelMatrix = new float[16];

    private float angle = 1.0f; //En radianes


    // Parámetros de cámara
    //SI QUIERES VER TIPO PLANO DIBUJO: X=2, Y=2, Z=2
    public float camaraX = 2.0f;
    public float camaraY = 2.0f;
    public float camaraZ = 2.0f;


    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        Matrix.setLookAtM(
                mViewMatrix, 0,
                camaraX, camaraY, camaraZ,   // Posición de cámara
                0f, 0f, 0f,                  // Siempre mira al centro
                0f, 1f, 0f                   // Vector "arriba" (eje Y)
        );

        //EJES -----------------------------------------------------------------------------------
        Matrix.setIdentityM(mModelMatrix, 0); // La matriz modelo es la identidad para los ejes
        float [] mvMatrix_Ejes = new float[16];
        Matrix.multiplyMM(mvMatrix_Ejes, 0, mViewMatrix, 0, mModelMatrix, 0);
        float [] mVPMatrix_Ejes = new float[16];
        Matrix.multiplyMM(mVPMatrix_Ejes, 0, mProjectionMatrix, 0, mvMatrix_Ejes, 0);
        ejes.draw(mVPMatrix_Ejes); // Dibuja los ejes X, Y, Z

        //ejes.draw(mVPMatrix); //ejes ultima figura dibujada
        //----------------------------------------------------------------------------------------




        //PIRAMIDE---------------------------------------------------------------
        Matrix.setIdentityM(mModelMatrix, 0); // Reiniciar la matriz modelo
        //1-Scale (Escalado) - tamanio
        Matrix.scaleM(
                mModelMatrix, //Matriz modelo
                0,//indice
                0.25f,//x
                0.25f,//y
                0.25f//z
        );

        //2-Rotate(Rotar) - giro alrededor de una línea imaginaria
        Matrix.rotateM(
                mModelMatrix,//Matriz modelo
                0, //indice
                angle,//angulo de rotacion
                0,//x - (0,1)
                1,//y - (0,1)
                0//z - (0,1)
        );

        //3-Translate (Trasladar) - a donde se mueve en x,y,z como un punto
        Matrix.translateM(
                mModelMatrix, //Matriz modelo
                0,//indice
                -2f,//x
                0f,//y
                0f//z
        );

        float [] mvMatrix_piramide = new float[16];
        Matrix.multiplyMM(mvMatrix_piramide, 0, mViewMatrix, 0, mModelMatrix, 0);//matriz nueva = matriz vista * matriz modelo
        Matrix.multiplyMM(mVPMatrix, 0, mProjectionMatrix, 0, mvMatrix_piramide, 0);

        piramide.draw(mVPMatrix);
        ejes.draw(mVPMatrix); //ejes piramide
        //-----------------------------------------------------------------------------------------



        //CONO---------------------------------------------------------------
        Matrix.setIdentityM(mModelMatrix, 0);// Reiniciar la matriz modelo
        //1-Scale (Escalado) - tamanio
        Matrix.scaleM(
                mModelMatrix, //Matriz modelo
                0,//indice
                0.25f,//x
                0.25f,//y
                0.25f//z
        );

        //2-Rotate(Rotar) - giro alrededor de una línea imaginaria
        Matrix.rotateM(
                mModelMatrix,//Matriz modelo
                0, //indice
                angle,//angulo de rotacion
                0,//x - (0,1)
                1,//y - (0,1)
                0//z - (0,1)
        );

        //3-Translate (Trasladar) - a donde se mueve en x,y,z como un punto
        Matrix.translateM(
                mModelMatrix, //Matriz modelo
                0,//indice
                2f,//x
                0f,//y
                0f//z
        );

        float [] mvMatrix_cono = new float[16];
        Matrix.multiplyMM(mvMatrix_cono, 0, mViewMatrix, 0, mModelMatrix, 0);//matriz nueva = matriz vista * matriz modelo
        Matrix.multiplyMM(mVPMatrix, 0, mProjectionMatrix, 0, mvMatrix_cono, 0);
        cono.draw(mVPMatrix);
        ejes.draw(mVPMatrix);//ejes cono
        //-----------------------------------------------------------------------------------------



        //CILINDRO---------------------------------------------------------------
        Matrix.setIdentityM(mModelMatrix, 0);// Reiniciar la matriz modelo
        //1-Scale (Escalado) - tamanio
        Matrix.scaleM(
                mModelMatrix, //Matriz modelo
                0,//indice
                0.25f,//x
                0.25f,//y
                0.25f//z
        );

        //2-Rotate(Rotar) - giro alrededor de una línea imaginaria
        Matrix.rotateM(
                mModelMatrix,//Matriz modelo
                0, //indice
                angle,//angulo de rotacion
                0,//x - (0,1)
                1,//y - (0,1)
                0//z - (0,1)
        );

        //3-Translate (Trasladar) - a donde se mueve en x,y,z como un punto
        Matrix.translateM(
                mModelMatrix, //Matriz modelo
                0,//indice
                0f,//x
                2f,//y
                0f//z
        );

        float [] mvMatrix_cilindro = new float[16];
        Matrix.multiplyMM(mvMatrix_cilindro, 0, mViewMatrix, 0, mModelMatrix, 0);//matriz nueva = matriz vista * matriz modelo
        Matrix.multiplyMM(mVPMatrix, 0, mProjectionMatrix, 0, mvMatrix_cilindro, 0);
        cilindro.draw(mVPMatrix);
        ejes.draw(mVPMatrix); //ejes cilindro
        //-----------------------------------------------------------------------------------------



        //ESFERA---------------------------------------------------------------
        Matrix.setIdentityM(mModelMatrix, 0);// Reiniciar la matriz modelo
        //1-Scale (Escalado) - tamanio
        Matrix.scaleM(
                mModelMatrix, //Matriz modelo
                0,//indice
                0.25f,//x
                0.25f,//y
                0.25f//z
        );

        //2-Rotate(Rotar) - giro alrededor de una línea imaginaria
        Matrix.rotateM(
                mModelMatrix,//Matriz modelo
                0, //indice
                angle,//angulo de rotacion
                0,//x - (0,1)
                1,//y - (0,1)
                0//z - (0,1)
        );

        //3-Translate (Trasladar) - a donde se mueve en x,y,z como un punto
        Matrix.translateM(
                mModelMatrix, //Matriz modelo
                0,//indice
                0f,//x
                -3f,//y
                0f//z
        );

        float [] mvMatrix_esfera = new float[16];
        Matrix.multiplyMM(mvMatrix_esfera, 0, mViewMatrix, 0, mModelMatrix, 0);//matriz nueva = matriz vista * matriz modelo
        Matrix.multiplyMM(mVPMatrix, 0, mProjectionMatrix, 0, mvMatrix_esfera, 0);
        esfera.draw(mVPMatrix);
        ejes.draw(mVPMatrix);//ejes esfera
        //-----------------------------------------------------------------------------------------



        //TORUS---------------------------------------------------------------
        Matrix.setIdentityM(mModelMatrix, 0);// Reiniciar la matriz modelo
        //1-Scale (Escalado) - tamanio
        Matrix.scaleM(
                mModelMatrix, //Matriz modelo
                0,//indice
                0.25f,//x
                0.25f,//y
                0.25f//z
        );

        //2-Rotate(Rotar) - giro alrededor de una línea imaginaria
        Matrix.rotateM(
                mModelMatrix,//Matriz modelo
                0, //indice
                angle,//angulo de rotacion
                0,//x - (0,1)
                1,//y - (0,1)
                0//z - (0,1)
        );

        //3-Translate (Trasladar) - a donde se mueve en x,y,z como un punto
        Matrix.translateM(
                mModelMatrix, //Matriz modelo
                0,//indice
                0f,//x
                -6f,//y
                0f//z
        );

        float [] mvMatrix_torus = new float[16];
        Matrix.multiplyMM(mvMatrix_torus, 0, mViewMatrix, 0, mModelMatrix, 0);//matriz nueva = matriz vista * matriz modelo
        Matrix.multiplyMM(mVPMatrix, 0, mProjectionMatrix, 0, mvMatrix_torus, 0);
        dona.draw(mVPMatrix);
        ejes.draw(mVPMatrix);//ejes dona
        //-----------------------------------------------------------------------------------------



        //PIRAMIDE TRUNCADA---------------------------------------------------------------
        Matrix.setIdentityM(mModelMatrix, 0);// Reiniciar la matriz modelo
        //1-Scale (Escalado) - tamanio
        Matrix.scaleM(
                mModelMatrix, //Matriz modelo
                0,//indice
                0.15f,//x
                0.15f,//y
                0.15f//z
        );

        //2-Rotate(Rotar) - giro alrededor de una línea imaginaria
        Matrix.rotateM(
                mModelMatrix,//Matriz modelo
                0, //indice
                angle,//angulo de rotacion
                0,//x - (0,1)
                1,//y - (0,1)
                0//z - (0,1)
        );

        //3-Translate (Trasladar) - a donde se mueve en x,y,z como un punto
        Matrix.translateM(
                mModelMatrix, //Matriz modelo
                0,//indice
                0f,//x
                7f,//y
                0f//z
        );

        float [] mvMatrix_piramide_truncada = new float[16];
        Matrix.multiplyMM(mvMatrix_piramide_truncada, 0, mViewMatrix, 0, mModelMatrix, 0);//matriz nueva = matriz vista * matriz modelo
        Matrix.multiplyMM(mVPMatrix, 0, mProjectionMatrix, 0, mvMatrix_piramide_truncada, 0);
        piramide_Truncada.draw(mVPMatrix);
        ejes.draw(mVPMatrix);//ejes piramide truncada
        //-----------------------------------------------------------------------------------------




        //Forma que rote constantemente
        //con -= gira al otro lado
        angle+= 1.0f; //velocidad se cambia este 1.0 a algun valor mayor



    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.1961f, 0.2941f, 0.4667f, 1.0f) ;
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        //Ejes
        ejes = new Ejes();
        //Piramide
        piramide = new Piramide();
        //Cono
        cono = new Cone(1f, 2, 30);
        //Cilindro
        cilindro = new Cylinder(1f, 1.5f, 30);
        //Esfera
        esfera = new Sphere(1.5f, 30, 30);
        //Torus
        dona = new Torus();
        //Piramide Truncada
        piramide_Truncada = new TruncatedPyramid();

    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES20.glViewport(0,0,width,height);
        float ratio = (float) width / height;

        Matrix.frustumM(
                mProjectionMatrix, 0,
                -ratio, ratio,
                -1, 1,
                2f, 10f
        );
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

