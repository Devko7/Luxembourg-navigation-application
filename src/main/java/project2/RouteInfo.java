package project2;

import java.util.Map;

public class RouteInfo {
    private final String operator;
    private final String shortName;
    private final String longName;
    private final String headSign;

    public RouteInfo(String operatorNameString, String shortNameString, String longNameString, String headSignString) {
        this.operator = operatorNameString;
        this.shortName = shortNameString;
        this.longName = longNameString;
        this.headSign = headSignString;
    }

    public String getOperator() {
        return this.operator;
    }

    public String getShortName() {
        return this.shortName;
    }

    public String getLongName() {
        return this.longName;
    }

    public String getHeadSign() {
        return this.headSign;
    }

    public Map<String,Object> toJson() {
        return Map.of("operator", this.operator,"shortName", this.shortName,"longName", this.longName,"headSign", this.headSign);
    }
}
