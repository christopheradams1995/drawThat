
package drawthat;

import java.awt.Color;
import java.awt.Point;

public class DrawLine 
{
    private Point start;
    private Point[] mid;
    private Point end;
    private Color color;
    private int size;
    
    DrawLine(Point start, Point[] mid, Point end, Color color, int size)
    {
        this.start = start;
        this.mid = mid;
        this.end = end;
        this.color = color;
        this.size = size;
    }
    
    DrawLine(Point start, Color color, int size)
    {
        this.start = start;
        this.color = color;
        this.size = size;
    }
    
    public Point[] getPoints()
    {
        return mid;
    }
    
    public Color getColor()
    {
        return color;
    }
    
    public int getSize()
    {
        return size;
    }
    
    public void setEnd(Point p)
    {
        this.end = p;
    }
    
    //arrayindex out of bounds , having trouble drawing , every line is leading to an inital point
    public void setMid(Point[] p)
    {
        mid = new Point[p.length+2];
        
        int midI = 1;
        for(int i=0;i<p.length;i++)
        {
                
            mid[midI++] = p[i];
            //System.out.println(p[i]);
        }
        mid[0] = start;
        mid[mid.length-1] = end;
        //System.out.println("start send =" + start);
                
        
    }
}
