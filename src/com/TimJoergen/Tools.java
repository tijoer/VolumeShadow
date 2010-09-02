package com.TimJoergen;

import javax.media.opengl.GL;

/**
 * This is a helper Class, that contains methods, that are nearly alywase 
 * needed.
 * 
 * TODO: make singleton
 * @author Tim
 */
public class Tools {
    
    public static int fps;
    private long startTime;
    private long timePassed;
    private long currentTime;
    public static int currentFrame = 0;
    public static long movementFactor;
    private static Tools instance = null;

    Tools(GL gl) {
        startTime = System.currentTimeMillis();
        lastSecond = System.currentTimeMillis();
        fps = 0;
    }

    public static Tools getInstance() {
        return instance;
    }

    static double degreeToRadian(float angle) {
        return angle * Math.PI / 180.0f;
    }

    private int fpsPassed = 0;
    private long lastSecond = 0;
    public void calculateFramerate() {
        currentTime = System.currentTimeMillis();
        timePassed = currentTime - startTime;
        movementFactor = (long) (timePassed / 15);
        currentFrame++;
        
        if(currentTime - lastSecond >= 1000) {
            fps = fpsPassed;
            lastSecond = currentTime;
            fpsPassed = 0;
        }
        
        fpsPassed++;
    }
}