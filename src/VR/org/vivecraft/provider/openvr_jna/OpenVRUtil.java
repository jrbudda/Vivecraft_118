package org.vivecraft.provider.openvr_jna;

import jopenvr.HmdMatrix34_t;
import jopenvr.HmdMatrix44_t;
import org.vivecraft.utils.Utils;
import org.vivecraft.utils.math.Matrix4f;

public class OpenVRUtil
{
    public static Matrix4f convertSteamVRMatrix3ToMatrix4f(HmdMatrix34_t hmdMatrix, Matrix4f mat)
    {
        Utils.Matrix4fSet(mat, hmdMatrix.m[0], hmdMatrix.m[1], hmdMatrix.m[2], hmdMatrix.m[3], hmdMatrix.m[4], hmdMatrix.m[5], hmdMatrix.m[6], hmdMatrix.m[7], hmdMatrix.m[8], hmdMatrix.m[9], hmdMatrix.m[10], hmdMatrix.m[11], 0.0F, 0.0F, 0.0F, 1.0F);
        return mat;
    }

    public static Matrix4f convertSteamVRMatrix4ToMatrix4f(HmdMatrix44_t hmdMatrix, Matrix4f mat)
    {
        Utils.Matrix4fSet(mat, hmdMatrix.m[0], hmdMatrix.m[1], hmdMatrix.m[2], hmdMatrix.m[3], hmdMatrix.m[4], hmdMatrix.m[5], hmdMatrix.m[6], hmdMatrix.m[7], hmdMatrix.m[8], hmdMatrix.m[9], hmdMatrix.m[10], hmdMatrix.m[11], hmdMatrix.m[12], hmdMatrix.m[13], hmdMatrix.m[14], hmdMatrix.m[15]);
        return mat;
    }

    public static HmdMatrix34_t convertToMatrix34(com.mojang.math.Matrix4f matrix)
    {
        HmdMatrix34_t hmdmatrix34_t = new HmdMatrix34_t();
        hmdmatrix34_t.m[0] = matrix.m00;
        hmdmatrix34_t.m[1] = matrix.m10;
        hmdmatrix34_t.m[2] = matrix.m20;
        hmdmatrix34_t.m[3] = matrix.m30;
        hmdmatrix34_t.m[4] = matrix.m01;
        hmdmatrix34_t.m[5] = matrix.m11;
        hmdmatrix34_t.m[6] = matrix.m21;
        hmdmatrix34_t.m[7] = matrix.m31;
        hmdmatrix34_t.m[8] = matrix.m02;
        hmdmatrix34_t.m[9] = matrix.m12;
        hmdmatrix34_t.m[10] = matrix.m22;
        hmdmatrix34_t.m[11] = matrix.m32;
        return hmdmatrix34_t;
    }
    
    public static com.mojang.math.Matrix4f Matrix4fFromOpenVR(HmdMatrix44_t in)
    {
        com.mojang.math.Matrix4f matrix4f = new com.mojang.math.Matrix4f();
        matrix4f.m00 = in.m[0];
        matrix4f.m01 = in.m[1];
        matrix4f.m02 = in.m[2];
        matrix4f.m03 = in.m[3];
        matrix4f.m10 = in.m[4];
        matrix4f.m11 = in.m[5];
        matrix4f.m12 = in.m[6];
        matrix4f.m13 = in.m[7];
        matrix4f.m20 = in.m[8];
        matrix4f.m21 = in.m[9];
        matrix4f.m22 = in.m[10];
        matrix4f.m23 = in.m[11];
        matrix4f.m30 = in.m[12];
        matrix4f.m31 = in.m[13];
        matrix4f.m32 = in.m[14];
        matrix4f.m33 = in.m[15];
        return matrix4f;
    }
}
