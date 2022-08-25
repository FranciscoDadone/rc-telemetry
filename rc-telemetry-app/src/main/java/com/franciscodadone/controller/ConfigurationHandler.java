package com.franciscodadone.controller;

import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;
import com.franciscodadone.model.Horizon;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

public class ConfigurationHandler {

    public static void update() {
        Map<String, Integer> map = new HashMap<>();

        map.put("H_roll_center_trim", Horizon.gyCenterRollTrim);
        map.put("H_pitch_center_trim", Horizon.gyCenterPitchTrim);
        map.put("H_inverted_center_trim", Horizon.gyCenterInvertedTrim);

        map.put("H_inverted_right_trim", Horizon.gyRightInvertedTrim);
        map.put("H_inverted_left_trim", Horizon.gyLeftInvertedTrim);
        map.put("H_inverted_down_trim", Horizon.gyDownInvertedTrim);
        map.put("H_inverted_up_trim", Horizon.gyUpInvertedTrim);

        map.put("H_right_trim", Horizon.gyRightTrim);
        map.put("H_left_trim", Horizon.gyLeftTrim);
        map.put("H_up_trim", Horizon.gyUpTrim);
        map.put("H_down_trim", Horizon.gyDownTrim);

        try {
            YamlWriter writer = new YamlWriter(new FileWriter("configuration.yml"));
            writer.write(map);
            writer.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void read() {
        try {
            YamlReader r = new YamlReader(new FileReader("configuration.yml"));
            HashMap hm = r.read(HashMap.class);

            Horizon.gyCenterRollTrim = Integer.valueOf(String.valueOf(hm.get("H_roll_center_trim")));
            Horizon.gyCenterPitchTrim = Integer.valueOf(String.valueOf(hm.get("H_pitch_center_trim")));
            Horizon.gyCenterInvertedTrim = Integer.valueOf(String.valueOf(hm.get("H_inverted_center_trim")));

            Horizon.gyRightInvertedTrim = Integer.valueOf(String.valueOf(hm.get("H_inverted_right_trim")));
            Horizon.gyLeftInvertedTrim = Integer.valueOf(String.valueOf(hm.get("H_inverted_left_trim")));
            Horizon.gyUpInvertedTrim = Integer.valueOf(String.valueOf(hm.get("H_inverted_up_trim")));
            Horizon.gyDownInvertedTrim = Integer.valueOf(String.valueOf(hm.get("H_inverted_down_trim")));

            Horizon.gyRightTrim = Integer.valueOf(String.valueOf(hm.get("H_right_trim")));
            Horizon.gyLeftTrim = Integer.valueOf(String.valueOf(hm.get("H_left_trim")));
            Horizon.gyUpTrim = Integer.valueOf(String.valueOf(hm.get("H_up_trim")));
            Horizon.gyDownTrim = Integer.valueOf(String.valueOf(hm.get("H_down_trim")));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
