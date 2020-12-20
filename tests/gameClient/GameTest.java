package gameClient;

import Server.Game_Server_Ex2;
import api.*;
import com.google.gson.*;
import org.junit.jupiter.api.Test;
import static gameClient.Arena.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GameTest {
    private static Arena _arena;
    private static game_service _game;
    public static long _sleep_time = 30;
    public static directed_weighted_graph _graph;

    public void testGameStarter(int scenario) {
        _game = Game_Server_Ex2.getServer(scenario);
        _graph = Arena.parseGraph(_game.getGraph());
        _arena = new Arena(_game);
        placeAgents(_game,_arena.getPokemons(),_graph);
        _arena.updateArena(_game);
    }

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

    public synchronized static PriorityQueue<CL_Pokemon> checkProximityCase(CL_Agent agent, List<CL_Pokemon> pokemons) {
        PriorityQueue<CL_Pokemon> poke_queue = new PriorityQueue<>(new Comparator<>() {
            @Override
            public int compare(CL_Pokemon poke1, CL_Pokemon poke2) {
                if (poke1.getValue() > poke2.getValue()) return -1;
                else if (poke1.getValue() < poke2.getValue()) return 1;
                else return 0;
            }
        });
        for (edge_data edge : _graph.getE(agent.getSrcNode())) {
            for (CL_Pokemon pokemon : pokemons) {
                if(Arena.isOnEdge(edge, pokemon, _graph)) {
                    if (pokemon.getType() > 0 && (pokemon.get_edge().getSrc() < pokemon.get_edge().getDest())) {
                        poke_queue.add(pokemon);
                    }
                    else if (pokemon.getType() < 0 && (pokemon.get_edge().getSrc() > pokemon.get_edge().getDest())) {
                        poke_queue.add(pokemon);
                    }
                }
            }
        }
        if (!poke_queue.isEmpty()) return poke_queue;
        else return null;
    }

    public static ArrayList<CL_Agent> initAgentsFromJson(String json) {
        ArrayList<CL_Agent> result = new ArrayList<>();
        JsonObject agents_obj = JsonParser.parseString(json).getAsJsonObject();
        JsonArray J_obj = agents_obj.getAsJsonArray("Agents");
        for (JsonElement gson : J_obj) {
            JsonObject json_agent = gson.getAsJsonObject();
            result.add(new CL_Agent(json_agent,_graph));
        }
        return result;
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
                if (c_pokemon.getType() > 0) {
                    Math.min(c_pokemon.get_edge().getSrc(),c_pokemon.get_edge().getDest());
                    game.addAgent(Math.min(c_pokemon.get_edge().getSrc(),c_pokemon.get_edge().getDest()));
                }
                else if (c_pokemon.getType() < 0) {
                    Math.max(c_pokemon.get_edge().getSrc(),c_pokemon.get_edge().getDest());
                    game.addAgent(Math.max(c_pokemon.get_edge().getSrc(),c_pokemon.get_edge().getDest()));
                }
                //game.addAgent(c_pokemon.get_edge().getSrc());
                treated_agents--;
            }
        }
        while (treated_agents > 0) {
            game.addAgent(ThreadLocalRandom.current().nextInt(1, graph.nodeSize()));
            treated_agents--;
        }
        System.out.println(game.getAgents());
        List<CL_Agent> result_agents_arr = initAgentsFromJson(game.getAgents());
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
        System.out.println(game.getPokemons());
        ArrayList<CL_Pokemon> pokemon_list = initPokemonsFromJson(pokemon_json);
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
        ArrayList<CL_Pokemon> pokemon_list = initPokemonsFromJson(game.getPokemons());
        System.out.println(game.getPokemons());
        System.out.println(pokemon_list.size());
        for (CL_Pokemon pokemon : pokemon_list) {
            Arena.updateEdge(pokemon, graph);
        }
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
        JsonObject agents_obj = JsonParser.parseString(_game.getAgents()).getAsJsonObject();
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
        ArrayList<CL_Pokemon> pokemon_list = initPokemonsFromJson(game.getPokemons());
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
        ArrayList<CL_Pokemon> pokemon_list = initPokemonsFromJson(game.getPokemons());
        for (CL_Pokemon pokemon : pokemon_list) {
            Arena.updateEdge(pokemon, graph);
        }
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

    @Test
    void stuckScenarioTest() {
        testGameStarter(11);
        dw_graph_algorithms algo = new DWGraph_Algo();
        algo.init(_graph);
        for (node_data n : algo.shortestPath(2,26)) {
            System.out.print(n.getKey() + "  ");
        }
        System.out.println(algo.shortestPath(2,26).get(1).getKey());
        System.out.println(algo.shortestPath(2,26).size());
/*        CL_Pokemon poke1 = new CL_Pokemon()
        returnClosestPokemon(_arena.getPokemons(),);*/
    }

    @Test
    void closestPokemonTest() {
        testGameStarter(11);
        for (CL_Agent agent : _arena.getAgents()) {
            if (agent.getID() == 0) {
                for (CL_Pokemon pokemon : _arena.getPokemons()) {
                    if (pokemon.getValue() == 13) {
                        System.out.println(Ex2.returnClosestPokemon(_arena.getPokemons(),agent).get_edge().toString());
                    }
                }
            }
        }
    }

    @Test
    void proximityCase() {
        testGameStarter(11);
        CL_Agent agent = new CL_Agent(2, 1, 7,_graph);
        PriorityQueue<CL_Pokemon> pq_pokemon = checkProximityCase(agent,_arena.getPokemons());
        assertEquals(pq_pokemon.poll().get_edge().getDest(),6);
    }

    @Test
    void distanceDivisionTesting() {
        testGameStarter(11);
        Ex2 ex2 = new Ex2(11);
        double maxdis = Ex2.getGraphMaxDistance();
        for (node_data node : Ex2._game_graph.getV()) {
            for (node_data node2 : Ex2._game_graph.getV()) {
                if (node.getLocation().distance(node2.getLocation()) > maxdis) fail();
            }
        }
    }
}

