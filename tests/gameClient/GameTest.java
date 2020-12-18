package gameClient;

import Server.Game_Server_Ex2;
import api.*;
import com.google.gson.*;
import gameClient.util.Point3D;
import org.junit.jupiter.api.Test;
import static gameClient.Arena.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    public static directed_weighted_graph loadGraphJson(String Json, String filename) {
        directed_weighted_graph game_graph = new DWGraph_DS();
        dw_graph_algorithms game_graph_algo = new DWGraph_Algo();
        game_graph_algo.init(game_graph);
        try {
            FileWriter output = new FileWriter("tests/IO/"+filename);
            output.write(Json);
            output.flush();
            game_graph_algo.load("tests/IO/"+filename);
            return game_graph_algo.getGraph();
        } catch (IOException e) {
            System.out.println("Wrong Input");
            e.printStackTrace();
        }
        return game_graph_algo.getGraph();
    }

    @Test
    void agentJsonTest() {
        String input = "{\"Agent\":{\"src\":39,\"pos\":\"35.20192230347054,32.10710793781513,0.0\",\"id\":2,\"dest\":15,\"value\":13,\"speed\":1}}";
        JsonObject json_obj = JsonParser.parseString(input).getAsJsonObject();
        System.out.println(json_obj.getAsJsonObject("Agent").get("src"));
    }

    @Test
    void gameJsonTests() {
        game_service game = Game_Server_Ex2.getServer(11);
        System.out.println(game.getAgents());
        //game.addAgent(1);
        System.out.println(game.toString());
        JsonObject json_obj = JsonParser.parseString(game.toString()).getAsJsonObject();
        System.out.println(json_obj.getAsJsonObject("GameServer").get("agents").getAsInt());
        game.addAgent(2);
        game.addAgent(4);
        System.out.println(game.getAgents());
        JsonObject jjjobjj = JsonParser.parseString(game.getAgents()).getAsJsonObject();
        JsonArray J_obj = jjjobjj.getAsJsonArray("Agents");
        for (JsonElement gson : J_obj) {
            JsonObject json_agent = gson.getAsJsonObject();
            System.out.println(json_agent.getAsJsonObject("Agent").get("id").getAsInt());
        }
    }

    @Test
    void graphParser() {
        game_service game = Game_Server_Ex2.getServer(11);
        directed_weighted_graph loaded_from_parser = parseGraph(game.getGraph());
        directed_weighted_graph loaded_from_file = loadGraphJson(game.getGraph(),"1");
        assertEquals(loaded_from_file,loaded_from_parser);
    }

    @Test
    void pokemonParser() {
        game_service game = Game_Server_Ex2.getServer(11);
        String pokemon_json = game.getPokemons();
        ArrayList<CL_Pokemon> pokemon_list = json2Pokemons(pokemon_json);
        assertEquals(6,pokemon_list.size());
        assertEquals(5.0,pokemon_list.get(0).getValue());
        assertEquals(8.0,pokemon_list.get(1).getValue());
        assertEquals(13.0,pokemon_list.get(2).getValue());
        assertEquals(12.0,pokemon_list.get(5).getValue());
    }

    @Test
    void updateEdge() {
        game_service game = Game_Server_Ex2.getServer(11);
        String pokemon_json = game.getPokemons();
        ArrayList<CL_Pokemon> pokemon_list = json2Pokemons(pokemon_json);
    }
}
