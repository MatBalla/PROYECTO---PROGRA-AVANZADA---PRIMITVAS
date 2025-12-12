package com.grafico3d;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {

    private Line ejex;
    private Line1 ejey;
    private Line2 ejez;
    private TruncatedPyramid truncatedPyramid;
    private HexagonalPyramid hexagonalPyramid;
    private Cone cone;
    private Tetraedro tetraedro;
    private Sphere sphere;
    private Torus torus;
    private Cylinder cilindro;
    private Cube cube;
    private Cube1 cube1;
    private Cube2 cube2;
    private Prisma pr;

    private final float[] mVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];

    // Parámetros de cámara
    public float camaraX = 0.0f;
    public float camaraY = 0.5f;
    public float camaraZ = 3.5f;

    // NUEVAS VARIABLES PARA ROTACIÓN
    private float anguloRotacion = 0f;        // Ángulo actual en grados
    private float radioOrbita = 3.5f;   // Radio de la órbita circular
    private float velocidadRotacion = 0.5f; // Grados por frame (ajustable)

    // Para controlar la rotación
    private boolean rotacionAutomatica = true; // Activar/desactivar rotación

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // ================== ACTUALIZAR POSICIÓN DE CÁMARA (ROTACIÓN) ==================
        if (rotacionAutomatica) {
            anguloRotacion += velocidadRotacion;
            if (anguloRotacion >= 360f) {
                anguloRotacion -= 360f;
            }

            // Calcular posición circular en el plano XZ
            // Usamos trigonometría: X = radio * cos(ángulo), Z = radio * sin(ángulo)
            float anguloRad = (float) Math.toRadians(anguloRotacion);
            camaraX = (float) Math.sin(anguloRad) * radioOrbita;
            camaraZ = (float) Math.cos(anguloRad) * radioOrbita;

            // Mantener Y fijo para órbita horizontal
            // Si quieres órbita vertical: camaraY = (float) Math.sin(anguloRad) * radioOrbita;
        }

        // ================== CONFIGURAR VISTA DE CÁMARA ==================
        Matrix.setLookAtM(
                mViewMatrix, 0,
                camaraX, camaraY, camaraZ,   // Posición de cámara (actualizada)
                0f, 0f, 0f,                  // Siempre mira al centro
                0f, 1f, 0f                   // Vector "arriba" (eje Y)
        );

        // ================== COMBINAR MATRICES ==================
        Matrix.multiplyMM(mVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        // ================== DIBUJAR OBJETOS ==================
//        ejex.draw(mVPMatrix);
//        ejey.draw(mVPMatrix);
//        ejez.draw(mVPMatrix);
        cube.draw(mVPMatrix);
        cube1.draw(mVPMatrix);
        truncatedPyramid.draw(mVPMatrix);
        torus.draw(mVPMatrix);
        pr.draw(mVPMatrix);
        sphere.draw(mVPMatrix);
        cone.draw(mVPMatrix);
        hexagonalPyramid.draw(mVPMatrix);
        cilindro.draw(mVPMatrix);
        cube2.draw(mVPMatrix);
        tetraedro.draw(mVPMatrix);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;

        Matrix.frustumM(
                mProjectionMatrix, 0,
                -ratio, ratio,
                -1, 1,
                1, 10
        );

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.88f, 0.75f, 0.65f, 1.0f); // Fondo gris oscuro
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        // Inicializar objetos
        ejex = new Line();
        ejey = new Line1();
        ejez = new Line2();
        cube = new Cube(0.7f, 1.4f, 4);
        cube1 = new Cube1(0.7f, 1.4f, 4);
        pr = new Prisma(0.75f, 0.15f, 6);
        truncatedPyramid = new TruncatedPyramid();
        torus = new Torus();
        sphere = new Sphere(0.55f, 50, 50);
        cone = new Cone(0.6f, 1.0f, 30);
        hexagonalPyramid = new HexagonalPyramid(0.15f, 0.2f);
        cilindro = new Cylinder(0.60f, 0.3f,32);
        cube2 = new Cube2(0.9f, 1.1f,4);
        tetraedro = new Tetraedro();


    }

    // ================== MÉTODOS PARA CONTROLAR LA ROTACIÓN ==================

    /**
     * Activa/desactiva la rotación automática
     */
    public void setRotacionAutomatica(boolean activar) {
        this.rotacionAutomatica = activar;
    }

    /**
     * Cambia la velocidad de rotación
     * @param velocidad Grados por frame (ej: 0.5f = lento, 2.0f = rápido)
     */
    public void setVelocidadRotacion(float velocidad) {
        this.velocidadRotacion = velocidad;
    }

    /**
     * Cambia el radio de la órbita
     * @param radio Distancia de la cámara al centro
     */
    public void setRadioOrbita(float radio) {
        this.radioOrbita = radio;
        // Recalcular posición actual
        float anguloRad = (float) Math.toRadians(anguloRotacion);
        camaraX = (float) Math.sin(anguloRad) * radioOrbita;
        camaraZ = (float) Math.cos(anguloRad) * radioOrbita;
    }

    /**
     * Reinicia la rotación a un ángulo específico
     */
    public void setAnguloRotacion(float anguloGrados) {
        this.anguloRotacion = anguloGrados;
        float anguloRad = (float) Math.toRadians(anguloRotacion);
        camaraX = (float) Math.sin(anguloRad) * radioOrbita;
        camaraZ = (float) Math.cos(anguloRad) * radioOrbita;
    }

    public static int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }
}