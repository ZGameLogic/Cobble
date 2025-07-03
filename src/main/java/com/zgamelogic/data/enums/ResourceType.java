package com.zgamelogic.data.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Getter
public enum ResourceType {
    PRODUCTION("pd", "Production", false),
    WOOD("wd", "Wood", true),
    STONE("st", "Stone", true),
    METAL("ml", "Metal", true),
    MAGIC("mc", "Magic", true),
    RATIONS("ra", "Rations", true),
    POPULATION("po", "Population", false);

    public final String code;
    public final String friendlyName;
    public final boolean stockpile;

    ResourceType(String code, String friendlyName, boolean stockpile) {
        this.code = code;
        this.friendlyName = friendlyName;
        this.stockpile = stockpile;
    }

    public String getEmojiName(){
        return friendlyName.toLowerCase();
    }

    public static ResourceType fromCode(String code){
        return Arrays.stream(ResourceType.values()).filter(t -> t.code.equals(code)).findFirst().orElse(null);
    }

    public static String mapResources(Map<ResourceType, Integer> map){
        StringBuilder sb = new StringBuilder();
        map.forEach((key, value) -> {
            if (value == 0 || key.isStockpile()) return;
            sb.append(key.getCode()).append(value);
        });
        return sb.toString();
    }

    public static Map<ResourceType, Integer> mapResources(String resources, boolean includeZero){
        Map<ResourceType, Integer> map = new HashMap<>();
        if(includeZero){
            map = Arrays.stream(ResourceType.values())
                .collect(Collectors.toMap(type -> type, type -> 0));
        }
        if(resources == null) return map;

        Pattern pattern = Pattern.compile("([a-zA-Z]+)(\\d+)");
        Matcher matcher = pattern.matcher(resources);

        while (matcher.find()) {
            String code = matcher.group(1);
            int number = Integer.parseInt(matcher.group(2));
            ResourceType type = ResourceType.fromCode(code);
            if(type == null) continue;
            map.put(type, number);
        }

        return map;
    }
}
