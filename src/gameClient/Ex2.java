package gameClient;
import Server.Game_Server_Ex2;
import api.DWGraph_DS;

public class Ex2 {
    public static void main(String[] args) {
        int level = 0;
        api.game_service game = Game_Server_Ex2.getServer(level);
        System.out.println(game.getGraph());
        api.directed_weighted_graph gr = new DWGraph_DS();
    }
}
