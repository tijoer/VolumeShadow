package exampleImplementation.math;

import java.util.ArrayList;

public class Vector3f {

    public float x;
    public float y;
    public float z;
    private int index;
    //private boolean indexSet=false;
    public ArrayList<Integer> usedBy;
    public static final float PIOVER180 = 0.0174532925f;

    public Vector3f(Vector3f v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.index = v.index;
        usedBy = new ArrayList<Integer>();
    }

    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        usedBy = new ArrayList<Integer>();
    }

    public Vector3f() {
        usedBy = new ArrayList<Integer>();
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    //if(indexSet==true)
    //    System.out.println("overwritten");
    //indexSet=true;
    }

    public Vector3f add(Vector3f v1) {
        this.x += v1.x;
        this.y += v1.y;
        this.z += v1.z;

        return this;
    }

    public Vector3f add(float s) {
        this.x += s;
        this.y += s;
        this.z += s;

        return this;
    }

    public Vector3f sub(Vector3f v0) {
        this.x -= v0.x;
        this.y -= v0.y;
        this.z -= v0.z;

        return this;
    }

    public static Vector3f sub(Vector3f v0, Vector3f v1) {
        return new Vector3f(v0.x - v1.x, v0.y - v1.y, v0.z - v1.z);
    }

    public Vector3f scale(float scalar) {
        this.x = this.x * scalar;
        this.y = this.y * scalar;
        this.z = this.z * scalar;

        return this;
    }

    public static Vector3f scale(Vector3f v0, float scalar) {
        return new Vector3f(v0.x * scalar, v0.y * scalar, v0.z * scalar);
    }

    public float dotProduct(Vector3f v1) {
        return (this.x * v1.x + this.y * v1.y + this.z * v1.z);
    }

    public Vector3f vectorProduct(Vector3f v1) {
        Vector3f ret = new Vector3f();

        ret.x = v1.y * this.z - v1.z * this.y;
        ret.y = v1.z * this.x - v1.x * this.z;
        ret.z = v1.x * this.y - v1.y * this.x;

        return ret;
    }

    public static Vector3f normalize(Vector3f v1) {
        float length = (float) Math.sqrt((v1.x * v1.x) +
                (v1.y * v1.y) +
                (v1.z * v1.z));

        //Prevent division by zero
        if (length <= 0.0001f && length >= 0.0001f) {
            length = 1.0f;
        }
        v1.x /= length;
        v1.y /= length;
        v1.z /= length;

        return v1;
    }

    public Vector3f normalize() {
        float length = (float) Math.sqrt((this.x * this.x) +
                (this.y * this.y) +
                (this.z * this.z));

        //Prevent division by zero
        if (length <= 0.0001f && length >= 0.0001f) {
            length = 1.0f;
        }
        this.x /= length;
        this.y /= length;
        this.z /= length;

        return this;
    }

    public float length() {
        return (float) Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public float angle(Vector3f v1) {
        return (float) Math.acos((this.x * v1.x + this.y * v1.y + this.z * v1.z) / (this.length() * v1.length()));
    }

    /**
     * Checks if this Vector equals another one.
     * 
     * @param v1 The vector to compare to
     * @return true if equal, false either
     */
    public boolean equals(Vector3f v1) {
        return (this.x == v1.x && this.y == v1.y && this.z == v1.z);
    }

    public static Vector3f returnScaledVector(Vector3f v, float factor) {
        return new Vector3f(v.x * factor, v.y * factor, v.z * factor);
    }
    static Vector3f v2v1 = new Vector3f(),  v3v1 = new Vector3f();

    public static void FindInvTBN(Vector3f v0, Vector3f v1, Vector3f v2,
            float v0TexCoordx, float v0TexCoordy,
            float v1TexCoordx, float v1TexCoordy,
            float v2TexCoordx, float v2TexCoordy,
            Vector3f InvNormal, Vector3f InvBinormal, Vector3f InvTangent) {
        //Calculate the vectors from the current vertex
        //to the two other vertices in the triangle
        v2v1.x = v0.x - v2.x;
        v2v1.y = v0.y - v2.y;
        v2v1.z = v0.z - v2.z;

        v3v1.x = v1.x - v2.x;
        v3v1.y = v1.y - v2.y;
        v3v1.z = v1.z - v2.z;

        //Calculate the direction of the triangle based on texture coordinates.
        // Calculate c2c1_T and c2c1_B
        float c2c1_T = v0TexCoordx - v2TexCoordx;
        float c2c1_B = v0TexCoordy - v2TexCoordy;

        // Calculate c3c1_T and c3c1_B
        float c3c1_T = v1TexCoordx - v2TexCoordx;
        float c3c1_B = v1TexCoordy - v2TexCoordy;

        //Look at the references for more explanation for this one.
        float fDenominator = c2c1_T * c3c1_B - c3c1_T * c2c1_B;
        /*ROUNDOFF here is a macro that sets a value to 0.0f if the value is a very small
        value, such as > -0.001f and < 0.001. */

        if (fDenominator < 0.0001f && fDenominator > -0.0001f) {
            //e won't risk a divide by zero, so set the tangent matrix to the
            //identity matrix
            InvTangent = new Vector3f(1.0f, 0.0f, 0.0f);
            InvBinormal = new Vector3f(0.0f, 1.0f, 0.0f);
            InvNormal = new Vector3f(0.0f, 0.0f, 1.0f);
        } else {
            // Calculate the reciprocal value once and for all (to achieve speed)
            float fScale1 = 1.0f / fDenominator;

            /* Time to calculate the tangent, binormal, and normal.
            Look at S�ren�s article for more information. */
            Vector3f T, B, N;
            T = new Vector3f((c3c1_B * v2v1.x - c2c1_B * v3v1.x) * fScale1,
                    (c3c1_B * v2v1.y - c2c1_B * v3v1.y) * fScale1,
                    (c3c1_B * v2v1.z - c2c1_B * v3v1.z) * fScale1);

            B = new Vector3f((-c3c1_T * v2v1.x + c2c1_T * v3v1.x) * fScale1,
                    (-c3c1_T * v2v1.y + c2c1_T * v3v1.y) * fScale1,
                    (-c3c1_T * v2v1.z + c2c1_T * v3v1.z) * fScale1);

            N = T.vectorProduct(B); //T%B; //Cross product!
            //This is where programmers should break up the function to smooth the tangent, binormal and
            //normal values.

            //Look at "Derivation of the Tangent Space Matrix" for more information.
            float fScale2 = 1.0f / ((T.x * B.y * N.z - T.z * B.y * N.x) +
                    (B.x * N.y * T.z - B.z * N.y * T.x) +
                    (N.x * T.y * B.z - N.z * T.y * B.x));
            InvTangent.x = B.vectorProduct(N).x * fScale2;
            InvTangent.y = returnScaledVector(N, -1.0f).vectorProduct(T).x * fScale2;
            InvTangent.z = T.vectorProduct(B).x * fScale2;
            InvTangent.normalize();

            InvBinormal.x = returnScaledVector(B, -1.0f).vectorProduct(N).y * fScale2;
            InvBinormal.y = N.vectorProduct(T).y * fScale2;
            InvBinormal.z = returnScaledVector(T, -1.0f).vectorProduct(B).y * fScale2;
            InvBinormal.normalize();

            InvNormal.x = B.vectorProduct(N).z * fScale2;
            InvNormal.y = returnScaledVector(N, -1.0f).vectorProduct(T).z * fScale2;
            InvNormal.z = T.vectorProduct(B).z * fScale2;
            InvNormal.normalize();
        }
    }

    public float[] asFloatArray() {
        float[] array = {this.x, this.y, this.z};
        return array;
    }
}
