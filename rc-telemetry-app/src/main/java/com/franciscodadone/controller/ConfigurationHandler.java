package com.franciscodadone.controller;

import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;
import com.franciscodadone.model.BMP280;
import com.franciscodadone.model.Horizon;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

public class ConfigurationHandler {
    private static int baud_rate;

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

        map.put("altitude_trim", (int) BMP280.altitudeTrim);

        map.put("baud_rate", baud_rate);

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
            //noinspection rawtypes
            HashMap hm = r.read(HashMap.class);

            Horizon.gyCenterRollTrim = Integer.parseInt(String.valueOf(hm.get("H_roll_center_trim")));
            Horizon.gyCenterPitchTrim = Integer.parseInt(String.valueOf(hm.get("H_pitch_center_trim")));
            Horizon.gyCenterInvertedTrim = Integer.parseInt(String.valueOf(hm.get("H_inverted_center_trim")));

            Horizon.gyRightInvertedTrim = Integer.parseInt(String.valueOf(hm.get("H_inverted_right_trim")));
            Horizon.gyLeftInvertedTrim = Integer.parseInt(String.valueOf(hm.get("H_inverted_left_trim")));
            Horizon.gyUpInvertedTrim = Integer.parseInt(String.valueOf(hm.get("H_inverted_up_trim")));
            Horizon.gyDownInvertedTrim = Integer.parseInt(String.valueOf(hm.get("H_inverted_down_trim")));

            Horizon.gyRightTrim = Integer.parseInt(String.valueOf(hm.get("H_right_trim")));
            Horizon.gyLeftTrim = Integer.parseInt(String.valueOf(hm.get("H_left_trim")));
            Horizon.gyUpTrim = Integer.parseInt(String.valueOf(hm.get("H_up_trim")));
            Horizon.gyDownTrim = Integer.parseInt(String.valueOf(hm.get("H_down_trim")));

            BMP280.altitudeTrim = Integer.parseInt(String.valueOf(hm.get("altitude_trim")));

            baud_rate = Integer.parseInt(String.valueOf(hm.get("baud_rate")));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static int getBaudRate() {
        return baud_rate;
    }

}
