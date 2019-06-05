package com.example.find_kick

import java.io.File
import kotlin.math.roundToInt

fun main(args: Array<String>) {

var gotit = getKickLocations()

    for(i in 0..gotit.size-1){
        System.out.println(gotit[i])
    }

}

fun getKickLocations(): IntArray {
    //System.out.println("Reading Wav")
    var wavFile = WavFile.openWavFile(File("app/src/main/res/raw/dotamono.wav"))
    //wavFile.display()

    var wav = DoubleArray(wavFile.numFrames.toInt())
    wavFile.readFrames(wav, wavFile.numFrames.toInt())

    // Filtering Section
    // Section 1
    var k1 = 0.210437273851690309633966080582467839122
    var num1 = doubleArrayOf(1 * k1, -1.999715279155000358102256541315000504255 * k1, 1 * k1)
    var den1 = doubleArrayOf(1.0, -1.99955689404341763193428960221353918314, 0.999763861879083393091605103109031915665)
    // Difference equation
    var y1d = DoubleArray(wavFile.numFrames.toInt())
    for (n in 2..wav.size - 1) {
        y1d[n] = 0.0
    }
    for (n in 2..wav.size - 1) {
        y1d[n] =
            (num1[0] * wav[n] + num1[1] * wav[n - 1] + num1[2] * wav[n - 2] - den1[1] * y1d[n - 1] - den1[2] * y1d[n - 2]) / den1[0]
    }

    // Section 2
    var k2 = 0.210437273851690309633966080582467839122
    var num2 = doubleArrayOf(1 * k2, -1.999898937775592244747713266406208276749 * k2, 1 * k2)
    var den2 = doubleArrayOf(1.0, -1.999667477171261698032367348787374794483, 0.999806477630654999444459463120438158512)
    // Difference equation
    var y2d = DoubleArray(wavFile.numFrames.toInt())
    for (n in 2..wav.size - 1) {
        y2d[n] = 0.0
    }
    for (n in 2..wav.size - 1) {
        y2d[n] =
            (num2[0] * y1d[n] + num2[1] * y1d[n - 1] + num2[2] * y1d[n - 2] - den2[1] * y2d[n - 1] - den2[2] * y2d[n - 2]) / den2[0]
    }

    // Section 3
    var k3 = 0.163929096939188917447793869541783351451
    var num3 = doubleArrayOf(1 * k3, -1.999620533527335819456993704079650342464 * k3, 1 * k3)
    var den3 = doubleArrayOf(1.0, -1.999158249204060489034873171476647257805, 0.99935082980535538954569574343622662127)
    // Difference equation
    var y3d = DoubleArray(wavFile.numFrames.toInt())
    for (n in 2..wav.size - 1) {
        y3d[n] = 0.0
    }
    for (n in 2..wav.size - 1) {
        y3d[n] =
            (num3[0] * y2d[n] + num3[1] * y2d[n - 1] + num3[2] * y2d[n - 2] - den3[1] * y3d[n - 1] - den3[2] * y3d[n - 2]) / den3[0]
    }

    // Section 4
    var k4 = 0.163929096939188917447793869541783351451
    var num4 = doubleArrayOf(1.0 * k4, -1.999924172419381918075487192254513502121 * k4, 1.0 * k4)
    var den4 = doubleArrayOf(1.0, -1.999279023516156161832668658462353050709, 0.999428349424154371938300300826085731387)
    // Difference equation
    var y4d = DoubleArray(wavFile.numFrames.toInt())
    for (n in 2..wav.size - 1) {
        y4d[n] = 0.0
    }
    for (n in 2..wav.size - 1) {
        y4d[n] =
            (num4[0] * y3d[n] + num4[1] * y3d[n - 1] + num4[2] * y3d[n - 2] - den4[1] * y4d[n - 1] - den4[2] * y4d[n - 2]) / den4[0]
    }

    // Section 5
    var k5 = 0.001308477824504156736620807954807332862
    var num5 = doubleArrayOf(1.0 * k5, 0.0, -1 * k5)
    var den5 = doubleArrayOf(1.0, -1.999036145472885994678335919161327183247, 0.999205709629116700654094529454596340656)
    // Difference equation
    var y5d = DoubleArray(wavFile.numFrames.toInt())
    for (n in 2..wav.size - 1) {
        y5d[n] = 0.0
    }
    for (n in 2..wav.size - 1) {
        y5d[n] =
            (num5[0] * y4d[n] + num5[1] * y4d[n - 1] + num5[2] * y4d[n - 2] - den5[1] * y5d[n - 1] - den5[2] * y5d[n - 2]) / den5[0]
    }

    // Finding the kick
    var th = .21 // Threshold
    var hits = DoubleArray(wavFile.numFrames.toInt())
    for (n in 2..wav.size - 1) {
        hits[n] = 0.0
    }

    var grace_samp = 4000
    var count = -1
    var hit_location = DoubleArray(1000)
    var i = 1
    var shift = 5000.toDouble() // Number of samples to shift
    for (n in 0..y5d.size-1) {
        if ((y5d[n] > th) && (count < 0)){
            hits[n] = th
            count = grace_samp
            hit_location[i] = n - shift
            i = i + 1
        } else {
            count = count - 1
        }
    }

    // Taking out the zeros
    var sizeofnozeros = 0
    for(n in 0..hit_location.size-1){
        if(hit_location[n] != 0.0){
            sizeofnozeros = sizeofnozeros + 1
        }
    }

    var nozeros = DoubleArray(sizeofnozeros)
    var index = 0
    for (n in 0..hit_location.size-1) {
        if(hit_location[n] != 0.0){
            nozeros[index] = hit_location[n]
            index++
        }
    }

    // System.out.println(nozeros.size)
    var Fs = 44100
    var nozerosmilli = IntArray(sizeofnozeros)
    for(i in 0..nozeros.size-1){
        nozeros[i] = (nozeros[i]*1000/Fs)
        nozerosmilli[i] = nozeros[i].roundToInt()

        //System.out.println(nozerosmilli[i])
    }
    return nozerosmilli
}