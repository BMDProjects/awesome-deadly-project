package com.example.android.opengl;

/**
 * Created by Ben on 03/12/2014.
 */
public class Shaders {

    public String getVertexShader(int i) {
        String shaderString;

        switch (i) {
            case 1: shaderString = vertexShaderCode;
                break;
            case 2: shaderString = vertexShaderCode2;
                break;
            case 3: shaderString = vertexShaderCode3;
                break;
            default: shaderString = "no shader";
                break;
        }
        return shaderString;
    }

    public String getFragmentShader(int i) {
        String shaderString;

        switch (i) {
            case 1: shaderString = fragmentShaderCode;
                break;
            case 2: shaderString = fragmentShaderCode2;
                break;
            case 3: shaderString = fragmentShaderCode3;
                break;
            default: shaderString = "no shader";
                break;
        }
        return shaderString;
    }

    public String getPointVertexShader() {
        String shaderString;

        shaderString = pointVertexShader;

        return shaderString;
    }

    public String getPointFragmentShader() {
        String shaderString;

        shaderString = pointFragmentShader;
        return shaderString;
    }

    private final String vertexShaderCode =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    // the matrix must be included as a modifier of gl_Position
                    // Note that the uMVPMatrix factor *must be first* in order
                    // for the matrix multiplication product to be correct.
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    private final String vertexShaderCode2 =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "attribute vec4 aColor;       " +

                    "varying vec4 vColor;     " +
                    "void main() {" +
                    "   vColor = aColor;    " +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    private final String fragmentShaderCode2 =
            "precision mediump float;" +
                    "varying vec4 vColor;    " +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    private final String vertexShaderCode3 =
            "uniform mat4 u_MVPMatrix;      \n"		// A constant representing the combined model/view/projection matrix.
            + "uniform mat4 u_MVMatrix;       \n"		// A constant representing the combined model/view matrix.
            + "uniform vec3 u_LightPos;       \n"	    // The position of the light in eye space.

            + "attribute vec4 a_Position;     \n"		// Per-vertex position information we will pass in.
            + "attribute vec4 a_Color;        \n"		// Per-vertex color information we will pass in.
            + "attribute vec3 a_Normal;       \n"		// Per-vertex normal information we will pass in.

            + "varying vec4 v_Color;          \n"		// This will be passed into the fragment shader.

            + "void main()                    \n" 	// The entry point for our vertex shader.
            + "{                              \n"
            // Transform the vertex into eye space.
            + "   vec3 modelViewVertex = vec3(u_MVMatrix * a_Position);              \n"
            // Transform the normal's orientation into eye space.
            + "   vec3 modelViewNormal = vec3(u_MVMatrix * vec4(a_Normal, 0.0));     \n"
            // Will be used for attenuation.
           // + "   float distance = length(u_LightPos - modelViewVertex);             \n"
            // Get a lighting direction vector from the light to the vertex.
            + "   vec3 lightVector = normalize(u_LightPos - modelViewVertex);        \n"
            // Calculate the dot product of the light vector and vertex normal. If the normal and light vector are
            // pointing in the same direction then it will get max illumination.
            + "   float diffuse = max(dot(modelViewNormal, lightVector), 0.1);       \n"
            // Attenuate the light based on distance.
         //   + "   diffuse = diffuse * (1.0 / (1.0 + (0.25 * distance * distance)));  \n"
            // Multiply the color by the illumination level. It will be interpolated across the triangle.
            + "   v_Color = a_Color * diffuse;                                       \n"
              //      + "   v_Color = a_Color;                                       \n"
            // gl_Position is a special variable used to store the final position.
            // Multiply the vertex by the matrix to get the final point in normalized screen coordinates.
            + "   gl_Position = u_MVPMatrix * a_Position;                            \n"
            + "}                                                                     \n";

    private final String fragmentShaderCode3 =
            "precision mediump float;       \n"		// Set the default precision to medium. We don't need as high of a
                    // precision in the fragment shader.
                    + "varying vec4 v_Color;          \n"		// This is the color from the vertex shader interpolated across the
                    // triangle per fragment.
                    + "void main()                    \n"		// The entry point for our fragment shader.
                    + "{                              \n"
                    + "   gl_FragColor = v_Color;     \n"		// Pass the color directly through the pipeline.
                    + "}                              \n";

    private final String pointVertexShader =
            "uniform mat4 u_MVPMatrix;      \n"
                    +	"attribute vec4 a_Position;     \n"
                    + "void main()                    \n"
                    + "{                              \n"
                    + "   gl_Position = u_MVPMatrix   \n"
                    + "               * a_Position;   \n"
                    + "   gl_PointSize = 5.0;         \n"
                    + "}                              \n";

    private final String pointFragmentShader =
            "precision mediump float;       \n"
                    + "void main()                    \n"
                    + "{                              \n"
                    + "   gl_FragColor = vec4(0.0,    \n"
                    + "   1.0, 0.0, 1.0);             \n"
                    + "}                              \n";
}
