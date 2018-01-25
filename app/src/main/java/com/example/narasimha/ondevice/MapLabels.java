package com.example.narasimha.ondevice;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import  java.util.*;
/**
 * Created by narasimha on 6/12/17.
 */

public class MapLabels {

    //Create a map object containing <class_name, node_id>
    //Create a reduced representation of <node_id, class_label>
    public Map<String, String> loadLabels(InputStream labelFile)throws Exception{
        BufferedReader reader = new BufferedReader(new InputStreamReader(labelFile));
        String line = "";
        Map<String, String> labelsMap = new HashMap<String, String>();
        while((line = reader.readLine())!=null){
            String[] splits = line.split("\t");
            labelsMap.put(splits[0], splits[1]);
        }
        labelsMap.size();
        return labelsMap;
    }

    public Map<Integer, String> loadNodes(InputStream protocolBuffer)throws Exception{
        Map<Integer, String> UIDS = new HashMap<Integer, String>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(protocolBuffer));
        String line;
        Integer node_id=0; String label_id="";
        while((line = reader.readLine())!=null){
            if(line.contains("target_class:"))
                node_id = Integer.parseInt(line.split(": ")[1]);
            if(line.contains("target_class_string:")) {
                label_id = line.split(": ")[1].replace("\"", "").trim();
                UIDS.put(node_id, label_id);
            }
        }
        return UIDS;
    }
}
