/**
 * DrawLine
 * 
 * v1.0
 * 
 * 15/05/2015
 * 
 * This file is owned by Christopher Adams
 */

package drawthat;

import java.awt.Color;
import java.awt.Point;

//Holds the information for a line for drawing
public class DrawLine {
    private final Point start;
    private Point[] mid;
    private Point end;
    private final Color color;
    private final int size;
    
    DrawLine(Point start, Point[] mid, Point end, Color color, int size) {
        this.start = start;
        this.mid = mid;
        this.end = end;
        this.color = color;
        this.size = size;
    }
    
    DrawLine(Point start, Color color, int size) {
        this.start = start;
        this.color = color;
        this.size = size;
    }
    
    public Point[] getPoints() {
        return mid;
    }
    
    public Color getColor() {
        return color;
    }
    
    public int getSize() {
        return size;
    }
    
    public void setEnd(Point p) {
        this.end = p;
    }
    
    
    public void setMid(Point[] p) {
        mid = new Point[p.length+2];
        
        int midI = 1;
        for (Point p1 : p) {
            mid[midI++] = p1;
        }
        mid[0] = start;
        mid[mid.length-1] = end;   
    }
}
