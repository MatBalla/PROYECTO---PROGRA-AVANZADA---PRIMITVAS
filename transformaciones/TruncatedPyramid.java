package com.transformaciones;

import android.opengl.GLES20;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer; // Importado para los índices

public class TruncatedPyramid {

    private final FloatBuffer vertexBuffer;
    private final ShortBuffer drawListBuffer;
    private final FloatBuffer colorBuffer;
    private final int mProgram;
    private int positionHandle;
    private int colorHandle;
    private int vPMatrixHandle;

    static final int COORDS_PER_VERTEX = 3;
    static final int COLOR_PER_VERTEX = 4;

    // --- NUEVAS DIMENSIONES (Dimensiones equivalentes a R=1.0, H=1.5) ---

    // Altura total: 1.5f. Para centrar en Y=0, usamos +/- 0.75f.
    private static final float HALF_HEIGHT = 0.75f;

    // Lado/2 de la tapa superior (Tomamos R_top = 0.5f)
    private static final float TOP_S = 0.5f;

    // Lado/2 de la base inferior (Tomamos R_bottom = 1.0f)
    private static final float BOTTOM_S = 1.0f;

    static final float[] UNIQUE_VERTICES = {
            // Vértices de la tapa superior (Top: Y = +0.75)
            // (x, y, z)
            -TOP_S, HALF_HEIGHT, -TOP_S,   // 0: A (Back-Left-Top)
            -TOP_S, HALF_HEIGHT,  TOP_S,   // 1: B (Front-Left-Top)
            TOP_S, HALF_HEIGHT,  TOP_S,   // 2: C (Front-Right-Top)
            TOP_S, HALF_HEIGHT, -TOP_S,   // 3: D (Back-Right-Top)

            // Vértices de la base inferior (Bottom: Y = -0.75)
            -BOTTOM_S, -HALF_HEIGHT, -BOTTOM_S, // 4: E (Back-Left-Bottom)
            -BOTTOM_S, -HALF_HEIGHT,  BOTTOM_S, // 5: F (Front-Left-Bottom)
            BOTTOM_S, -HALF_HEIGHT,  BOTTOM_S, // 6: G (Front-Right-Bottom)
            BOTTOM_S, -HALF_HEIGHT, -BOTTOM_S  // 7: H (Back-Right-Bottom)
    };

    // ÍNDICES: Define los 12 triángulos (6 caras * 2 triángulos/cara = 36 índices)
    private final short[] drawOrder = {
            // Cara Frontal (BFCG)
            1, 5, 6, 1, 6, 2,
            // Cara Derecha (CGHD)
            2, 6, 7, 2, 7, 3,
            // Cara Trasera (DHEA)
            3, 7, 4, 3, 4, 0,
            // Cara Izquierda (AEFB)
            0, 4, 5, 0, 5, 1,
            // Cara Superior (ABCD)
            0, 1, 2, 0, 2, 3,
            // Cara Inferior (EFGH)
            4, 5, 6, 4, 6, 7
    };

    // COLORES: Definimos 8 colores (uno por vértice). Mantenemos tus colores.
    private static final float[] VERTEX_COLORS = {
            // Colores Top (para vértices 0, 1, 2, 3) - Claro
            0.75f, 0.75f, 0.75f, 1.0f,
            0.75f, 0.75f, 0.75f, 1.0f,
            0.75f, 0.75f, 0.75f, 1.0f,
            0.75f, 0.75f, 0.75f, 1.0f,

            // Colores Bottom (para vértices 4, 5, 6, 7) - Oscuro
            0.35f, 0.35f, 0.35f, 1.0f,
            0.35f, 0.35f, 0.35f, 1.0f,
            0.35f, 0.35f, 0.35f, 1.0f,
            0.35f, 0.35f, 0.35f, 1.0f
    };

    private final int vertexStride = COORDS_PER_VERTEX * 4; // 12 bytes
    private final int colorStride = COLOR_PER_VERTEX * 4; // 16 bytes


    public TruncatedPyramid() {
        int vertexCount = UNIQUE_VERTICES.length / COORDS_PER_VERTEX;

        // 1. Inicializar búfer de Vértices Únicos
        ByteBuffer bb = ByteBuffer.allocateDirect(UNIQUE_VERTICES.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(UNIQUE_VERTICES);
        vertexBuffer.position(0);

        // 2. Inicializar búfer de Colores Únicos
        ByteBuffer cb = ByteBuffer.allocateDirect(VERTEX_COLORS.length * 4);
        cb.order(ByteOrder.nativeOrder());
        colorBuffer = cb.asFloatBuffer();
        colorBuffer.put(VERTEX_COLORS);
        colorBuffer.position(0);

        // 3. Inicializar búfer de Índices
        ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrder.length * 2); // short ocupa 2 bytes
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
    }

    public void draw(float[] mvpMatrix) {
        GLES20.glUseProgram(mProgram);

        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        colorHandle = GLES20.glGetAttribLocation(mProgram, "vColor");
        GLES20.glEnableVertexAttribArray(colorHandle);
        GLES20.glVertexAttribPointer(colorHandle, COLOR_PER_VERTEX, GLES20.GL_FLOAT, false,
                colorStride, colorBuffer);

        vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);

        // Dibujado usando índices
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES,
                drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT,
                drawListBuffer);

        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(colorHandle);
    }

    private final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "attribute vec4 vColor;" +
                    "varying vec4 fColor;" +
                    "void main() {" +
                    // Traslación mantenida según tu solicitud:
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "  fColor = vColor;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "varying vec4 fColor;" +
                    "void main() {" +
                    "  gl_FragColor = fColor;" +
                    "}";


}