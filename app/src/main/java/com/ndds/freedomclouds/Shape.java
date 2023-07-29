package com.ndds.freedomclouds;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Shape {
    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    protected FloatBuffer vertexBuffer;
    protected int mProgram = 0;
    protected FloatBuffer buildBuffer(float[] positions) {
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(positions.length * 4);
        // (# of coordinate values * 4 bytes per float)
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(positions);
        fb.position(0);
        return fb;
    }
    float[] color = { 1, 1, 1, 1 };

    public void draw(float brightnessFactor) {
        // Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        int positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(positionHandle);

        // Prepare the triangle coordinate data
        // 4 bytes per vertex
        int vertexStride = COORDS_PER_VERTEX * 4;
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        int isTextureHandle  = GLES20.glGetUniformLocation(mProgram,"textured");
        GLES20.glUniform1f(isTextureHandle,0.0f);
        int colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        float[] blendedColor = new float[] {
                color[0] * brightnessFactor,
                color[1] * brightnessFactor,
                color[2], color[3]
        };
        GLES20.glUniform4fv(colorHandle, 1, blendedColor, 0);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, (360 * 6));

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}
