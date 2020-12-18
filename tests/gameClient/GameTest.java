package gameClient;

import Server.Game_Server_Ex2;
import api.*;
import com.google.gson.*;
import gameClient.util.Point3D;
import org.junit.jupiter.api.Test;
import static gameClient.Arena.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

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

    public static void placeAgents(game_service game, List<CL_Pokemon> pokemons, directed_weighted_graph graph) {
        PriorityQueue<CL_Pokemon> pokemon_value_queue = new PriorityQueue<>(new Comparator<>() {
            @Override
            public int compare(CL_Pokemon poke1, CL_Pokemon poke2) {
                if (poke1.getValue() > poke2.getValue()) return -1;
                else if (poke1.getValue() < poke2.getValue()) return 1;
                else return 0;
            }
        });
        pokemon_value_queue.addAll(pokemons);
        JsonObject json_obj = JsonParser.parseString(game.toString()).getAsJsonObject();
        json_obj.getAsJsonObject("GameServer").get("agents").getAsInt();
        int agents_number = json_obj.getAsJsonObject("GameServer").get("agents").getAsInt();
        int treated_agents = agents_number;
        for(int i = 0; i < agents_number; i++) {
            if (!pokemon_value_queue.isEmpty()) {
                CL_Pokemon c_pokemon = pokemon_value_queue.poll();
                int pokemon_dest_node = c_pokemon.get_edge().getSrc();
                if(c_pokemon.getType() < 0) {
                    pokemon_dest_node = c_pokemon.get_edge().getDest();
                }
                game.addAgent(pokemon_dest_node);
                treated_agents--;
            }
        }
        while (treated_agents > 0) {
            game.addAgent(ThreadLocalRandom.current().nextInt(1, graph.nodeSize()));
            treated_agents--;
        }
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
    void agentsPlacing_updateEdge() {
        game_service game = Game_Server_Ex2.getServer(11);
        directed_weighted_graph graph = parseGraph(game.getGraph());
        ArrayList<CL_Pokemon> pokemon_list = json2Pokemons(game.getPokemons());
        System.out.println(game.getPokemons());
        System.out.println(pokemon_list.size());
        for (CL_Pokemon pokemon : pokemon_list) {
            Arena.updateEdge(pokemon, graph);
        }
        placeAgents(game,pokemon_list,graph);
        PriorityQueue<CL_Pokemon> pokemon_value_queue = new PriorityQueue<>(new Comparator<>() {
            @Override
            public int compare(CL_Pokemon poke1, CL_Pokemon poke2) {
                if (poke1.getValue() > poke2.getValue()) return -1;
                else if (poke1.getValue() < poke2.getValue()) return 1;
                else return 0;
            }
        });
        pokemon_value_queue.addAll(pokemon_list);
        System.out.println(game.getAgents());
        ArrayList<Integer> agents_sources = new ArrayList<>();
        JsonObject agents_obj = JsonParser.parseString(game.getAgents()).getAsJsonObject();
        JsonArray J_obj = agents_obj.getAsJsonArray("Agents");
        for (JsonElement gson : J_obj) {
            JsonObject json_agent = gson.getAsJsonObject();
            agents_sources.add(json_agent.getAsJsonObject("Agent").get("src").getAsInt());
        }
        CL_Pokemon tmp_pok = pokemon_value_queue.poll();
        assertEquals(13.0,tmp_pok.getValue());
        assertEquals(tmp_pok.get_edge().getSrc(),agents_sources.get(0));
        tmp_pok = pokemon_value_queue.poll();
        assertEquals(12.0,tmp_pok.getValue());
        assertEquals(9.0,pokemon_value_queue.poll().getValue());
        assertEquals(8.0,pokemon_value_queue.poll().getValue());
        assertEquals(5.0,pokemon_value_queue.poll().getValue());
        assertEquals(5.0,pokemon_value_queue.poll().getValue());
    }

    @Test
    void pokemonTargeting() {
        game_service game = Game_Server_Ex2.getServer(11);
        ArrayList<CL_Pokemon> pokemon_list = json2Pokemons(game.getPokemons());
        for (CL_Pokemon pokemon : pokemon_list) {
            assertFalse(pokemon.isTargeted());
        }
        for (CL_Pokemon pokemon : pokemon_list) {
            pokemon.targetPokemon();
            assertTrue(pokemon.isTargeted());
        }
        Ex2.resetTargeting(pokemon_list);
        for (CL_Pokemon pokemon : pokemon_list) {
            assertFalse(pokemon.isTargeted());
        }
    }

    @Test
    void agentTargeting() {
        game_service game = Game_Server_Ex2.getServer(11);
        directed_weighted_graph graph = parseGraph(game.getGraph());
        ArrayList<CL_Pokemon> pokemon_list = json2Pokemons(game.getPokemons());
        System.out.println(game.getAgents());
        for (CL_Pokemon pokemon : pokemon_list) {
            Arena.updateEdge(pokemon, graph);
        }
        placeAgents(game,pokemon_list,graph);
        System.out.println(game.getAgents());
        PriorityQueue<CL_Pokemon> pokemon_value_queue = new PriorityQueue<>(new Comparator<>() {
            @Override
            public int compare(CL_Pokemon poke1, CL_Pokemon poke2) {
                if (poke1.getValue() > poke2.getValue()) return -1;
                else if (poke1.getValue() < poke2.getValue()) return 1;
                else return 0;
            }
        });
        pokemon_value_queue.addAll(pokemon_list);
    }
}
