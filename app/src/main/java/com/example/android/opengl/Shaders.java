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
            case 4: shaderString = perPixelVertexShader;
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
            case 4: shaderString = perPixelFragmentShader;
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
            + "   float diffuse = max(dot(modelViewNormal, lightVector), 0.2);       \n"
            // Attenuate the light based on distance.
         //   + "   diffuse = diffuse * (1.0 / (1.0 + (0.25 * distance * distance)));  \n"
            // Multiply the color by the illumination level. It will be interpolated across the triangle.
           + "   v_Color = a_Color * diffuse;                                       \n"
          //          + "   v_Color = a_Color;                                       \n"
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

    private final String perPixelVertexShader =
            "uniform mat4 u_MVPMatrix;      \n"		// A constant representing the combined model/view/projection matrix.
            + "uniform mat4 u_MVMatrix;       \n"		// A constant representing the combined model/view matrix.

            + "attribute vec4 a_Position;     \n"		// Per-vertex position information we will pass in.
            + "attribute vec4 a_Color;        \n"		// Per-vertex color information we will pass in.
            + "attribute vec3 a_Normal;       \n"		// Per-vertex normal information we will pass in.

            + "varying vec3 v_Position;       \n"		// This will be passed into the fragment shader.
            + "varying vec4 v_Color;          \n"		// This will be passed into the fragment shader.
            + "varying vec3 v_Normal;         \n"		// This will be passed into the fragment shader.

            // The entry point for our vertex shader.
            + "void main()                                                \n"
            + "{                                                          \n"
            // Transform the vertex into eye space.
            + "   v_Position = vec3(u_MVMatrix * a_Position);             \n"
            // Pass through the color.
            + "   v_Color = a_Color;                                      \n"
            // Transform the normal's orientation into eye space.
            + "   v_Normal = (vec3(u_MVMatrix * vec4(a_Normal, 0.0)));      \n"
            // gl_Position is a special variable used to store the final position.
            // Multiply the vertex by the matrix to get the final point in normalized screen coordinates.
            + "   gl_Position = u_MVPMatrix * a_Position;                 \n"
            + "}                                                          \n";

    private final String perPixelFragmentShader =
            "precision mediump float;       \n"		// Set the default precision to medium. We don't need as high of a precision in the fragment shader.
            // The position of the light in eye space.
            + "uniform vec3 u_LightPos0;       \n"
            + "uniform vec3 u_LightPos1;       \n"
            + "uniform vec3 u_LightPos2;       \n"
            + "uniform vec3 u_LightPos3;       \n"
            + "uniform vec3 u_LightPos4;       \n"
            + "uniform vec3 u_LightPos5;       \n"
            + "uniform float distanceCorrection;"
      //      + "uniform vec3 u_LightPos6;       \n"
            + "varying vec3 v_Position;		\n"		// Interpolated position for this fragment.
            + "varying vec4 v_Color;          \n"		// This is the color from the vertex shader interpolated across the triangle per fragment.
            + "varying vec3 v_Normal;         \n"		// Interpolated normal for this fragment.
            // The entry point for our fragment shader.
            + "void main()                    \n"
            + "{                              \n"

              //  + " float distanceCorrection = 1.0;"
             + "   float distance0 = length(u_LightPos0 - v_Position) * distanceCorrection;   "
            + "   float distance1 = length(u_LightPos1 - v_Position) * distanceCorrection;   "
            + "   float distance2 = length(u_LightPos2 - v_Position) * distanceCorrection;   "
            + "   float distance3 = length(u_LightPos3 - v_Position) * distanceCorrection;   "
            + "   float distance4 = length(u_LightPos4 - v_Position) * distanceCorrection;   "
            + "   float distance5 = length(u_LightPos5 - v_Position) * distanceCorrection;   "
            // Get a lighting direction vector from the light to the vertex.
            + "   vec3 lightVector0 = normalize(u_LightPos0 - v_Position);             \n"
            + "   vec3 lightVector1 = normalize(u_LightPos1 - v_Position);             \n"
            + "   vec3 lightVector2 = normalize(u_LightPos2 - v_Position);             \n"
            + "   vec3 lightVector3 = normalize(u_LightPos3 - v_Position);             \n"
            + "   vec3 lightVector4 = normalize(u_LightPos4 - v_Position);             \n"
            + "   vec3 lightVector5 = normalize(u_LightPos5 - v_Position);             \n"

            //+ "if (dot(v_Normal, lightVector) < 0.0) { v_Normal = -v_Normal; }"
                  //  + "if (dot(v_Normal, lightVector) < 0.0) {  float diffuse = -dot(v_Normal, lightVector); " +
                //    "                    gl_FragColor = v_Color * diffuse + 0.2; }"
               //    + " else {  float diffuse = dot(v_Normal, lightVector); " +
               //     "gl_FragColor = v_Color * diffuse + 0.2; } "



           + "float lowerClamp = 0.0f;"
            + "   float diffuse0 = max(dot(v_Normal, lightVector0), lowerClamp);              \n"
            + "   diffuse0 = diffuse0 * (1.0 / (1.0 + (0.25 * distance0 * distance0))); " +
             "     float diffuse1 = max(dot(v_Normal, lightVector1), lowerClamp);              \n" +
             "    diffuse1 = diffuse1 * (1.0 / (1.0 + (0.25 * distance1 * distance1)));"
            + "   float diffuse2 = max(dot(v_Normal, lightVector2), lowerClamp);              \n"
            + "   diffuse2 = diffuse2 * (1.0 / (1.0 + (0.25 * distance2 * distance2))); " +
            "     float diffuse3 = max(dot(v_Normal, lightVector3), lowerClamp);              \n" +
            "    diffuse3 = diffuse3 * (1.0 / (1.0 + (0.25 * distance3 * distance3)));"
            + "   float diffuse4 = max(dot(v_Normal, lightVector4), lowerClamp);              \n"
            + "   diffuse4 = diffuse4 * (1.0 / (1.0 + (0.25 * distance4 * distance4))); "
            + "   float diffuse5 = max(dot(v_Normal, lightVector5), lowerClamp);              \n"
            + "   diffuse5 = diffuse5 * (1.0 / (1.0 + (0.25 * distance5 * distance5))); "

            +"   float totalDiffuse = diffuse0 + diffuse1 + diffuse2 + diffuse3 + diffuse4 + diffuse5 ;"

            // Multiply the color by the diffuse illumination level to get final output color.
                    // Calculate the dot product of the light vector and vertex normal. If the normal and light vector are
                    // pointing in the same direction then it will get max illumination.

            + "   gl_FragColor = v_Color * totalDiffuse + 0.1;                                  \n"
            + "}                                                                     \n";





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
