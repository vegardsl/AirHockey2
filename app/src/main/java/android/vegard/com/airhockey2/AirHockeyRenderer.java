package android.vegard.com.airhockey2;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.vegard.com.airhockey2.util.LoggerConfig;
import android.vegard.com.airhockey2.util.ShaderHelper;
import android.vegard.com.airhockey2.util.TextResourceReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Vegard on 28.04.2016.
 */
public class AirHockeyRenderer implements GLSurfaceView.Renderer{

    private static final String U_COLOR = "u_Color";
    private static final String A_POSITION = "a_Position";
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int BYTES_PER_FLOAT = 4;
    private final FloatBuffer vertexData;
    private final Context context;
    private int program;
    private int uColorLocation;
    private int aPositionLocation;

    public AirHockeyRenderer(Context context) {
        this.context = context;
        float[] tableVerticesWithTriangles = {
                // Border - Triangle 1
                -0.6f, -0.6f,
                0.6f,  0.6f,
                -0.6f,  0.6f,

                // Border - Triangle 2
                -0.6f, -0.6f,
                0.6f, -0.6f,
                0.6f,  0.6f,

                // Triangle Fan
                0, 0,
                -0.5f, -0.5f,
                0.5f, -0.5f,
                0.5f, 0.5f,
                -0.5f, 0.5f,
                -0.5f, -0.5f,

                // Line 1
                -0.5f, 0f,
                0.5f, 0f,

                // Mallets
                0f, -0.25f,
                0f,  0.25f
        };
        vertexData = ByteBuffer
                .allocateDirect(tableVerticesWithTriangles.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexData.put(tableVerticesWithTriangles);
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config){
        GLES20.glClearColor(0.0f, 0.2f, 0.0f, 0.0f);

        String vertexShaderSource = TextResourceReader
                .readTextFileFromResource(context, R.raw.simple_vertex_shader);
        String fragmentShaderSource = TextResourceReader
                .readTextFileFromResource(context, R.raw.simple_fragment_shader);

        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);
        program = ShaderHelper.linkProgram(vertexShader, fragmentShader);

        if (LoggerConfig.ON) {
            ShaderHelper.validateProgram(program);
        }
        GLES20.glUseProgram(program);

        uColorLocation = GLES20.glGetUniformLocation(program, U_COLOR);

        aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION);

        vertexData.position(0); // Where to read in buffer.
        GLES20.glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GLES20.GL_FLOAT,
                false, 0, vertexData);
        GLES20.glEnableVertexAttribArray(aPositionLocation);
    }



    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        // Set the OpenGL viewport to fill the entire surface.
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        // Clear the rendering surface.
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        //Draw border
        GLES20.glUniform4f(uColorLocation, 0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);

        //Draw table
        GLES20.glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 6, 6);

        //Draw line
        GLES20.glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glDrawArrays(GLES20.GL_LINES, 12, 2);

        // Draw the first mallet blue.
        GLES20.glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);
        GLES20.glDrawArrays(GLES20.GL_POINTS, 14, 1);
        // Draw the second mallet red.
        GLES20.glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glDrawArrays(GLES20.GL_POINTS, 15, 1);
    }
}
