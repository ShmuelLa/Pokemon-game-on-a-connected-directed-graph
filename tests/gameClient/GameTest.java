package gameClient;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    public static void main(String[] args) {
        List<Double> testlist = new ArrayList<>();
        testlist.add(2323.0);
        testlist.add(11.0);
        System.out.println(testlist.get(0));
        Collections.sort(testlist);
        System.out.println(testlist.get(0));
    }

    @Test
    void agentJsonTest() {
        String input = "{\"Agent\":{\"src\":39,\"pos\":\"35.20192230347054,32.10710793781513,0.0\",\"id\":2,\"dest\":15,\"value\":13,\"speed\":1}}";
        JsonObject json_obj = JsonParser.parseString(input).getAsJsonObject();
        System.out.println(json_obj.getAsJsonObject("Agent").get("src"));
    }
}