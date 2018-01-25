package com.example.narasimha.ondevice; /**
 * Created by narasimha on 10/12/17.
 */
import java.lang.reflect.Array;
import java.util.*;
public class DomainsRepresentation {

    public static ArrayList<Double> times = new ArrayList<Double>();
    public static Queue<Integer> tests;
    public static double currentTime = 0;

    static ArrayList<Double> getTimes(){
        return times;
    }

    static void addTimeOnY(Double time){
        times.add(time);
    }

    static Queue<Integer> getTests(){
        return tests;
    }

    static void addTimeOnX(Integer count){
        tests.add(count);
    }
}
