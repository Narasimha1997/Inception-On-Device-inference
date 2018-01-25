package com.example.narasimha.ondevice;

import android.widget.Toast;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by narasimha on 6/12/17.
 */

public class ClassMapper {

    public Map<Integer, String> reduceMaps(Map<Integer, String> nodes, Map<String, String> labels){
        Map<Integer, String> reducedLabels = new HashMap<Integer, String>();
        //Get keys
        Set NODES = reducedLabels.keySet();
        for(Map.Entry<Integer, String> tuple : nodes.entrySet()){
            Integer id = tuple.getKey();
            if(nodes.containsKey(id)){
                String uid = nodes.get(id);
                String label = labels.get(uid);
                reducedLabels.put(id, label);
            }
        }
        return reducedLabels;
    }

    public String reducePredictions(int max_arg, Map<Integer, String> nodes, Map<String, String> labels){
        if(nodes.containsKey(max_arg))
            return labels.get(nodes.get(max_arg));
        else return null;
    }
}
