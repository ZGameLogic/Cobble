package com.zgamelogic.data.enums;

import com.zgamelogic.data.CobbleServiceException;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Getter
public enum BuildingType {
    TOWN_HALL("Town Hall", "Mayor", "th"),
    BUILDER("Builder's Hut", "Builder", "bd"),
    WHEAT_FARM("Wheat Farm", "Wheat Farmer", "wf"),
    FISHERY("Fishery", "Fisherman", "fs"),
    FORESTRY_HUT("Forestry Hut", "Forester", "fh"),
    MINE("Mine", "Miner", "mn"),
    HOME("Home", "", "hm");

    private final String friendlyName;
    private final String workerTitle;
    private final String code;

    BuildingType(String friendlyName, String workerTitle, String code) {
        this.friendlyName = friendlyName;
        this.workerTitle = workerTitle;
        this.code = code;
    }

    public static boolean validName(String name) {
        return Arrays.stream(BuildingType.values()).anyMatch(cobbleBuildingType -> cobbleBuildingType.friendlyName.equals(name));
    }

    public static BuildingType fromCode(String code){
        return Arrays.stream(BuildingType.values()).filter(t -> t.code.equals(code)).findFirst().orElse(null);
    }

    public static BuildingType fromName(String name) throws CobbleServiceException {
        return Arrays.stream(BuildingType.values())
            .filter(type -> type.getFriendlyName().equals(name))
            .findFirst()
            .orElseThrow(() -> new CobbleServiceException("Friendly name does not exist"));
    }

    public static String mapBuildings(Map<BuildingType, Integer> map){
        StringBuilder sb = new StringBuilder();
        map.forEach((key, value) -> {
            if (value == 0) return;
            sb.append(key.getCode() + value);
        });
        return sb.toString();
    }

    public static Map<BuildingType, Integer> mapBuildings(String resources, boolean includeZero){
        Map<BuildingType, Integer> map = new HashMap<>();
        if(includeZero){
            map = Arrays.stream(BuildingType.values())
                .collect(Collectors.toMap(type -> type, type -> 0));
        }
        if(resources == null) return map;

        Pattern pattern = Pattern.compile("([a-zA-Z]+)(\\d+)");
        Matcher matcher = pattern.matcher(resources);

        while (matcher.find()) {
            String code = matcher.group(1);
            int number = Integer.parseInt(matcher.group(2));
            BuildingType type = BuildingType.fromCode(code);
            if(type == null) continue;
            map.put(type, number);
        }

        return map;
    }
}
