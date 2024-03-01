package itoozh.core.session.procces.type;

import cn.nukkit.Player;
import cn.nukkit.math.Vector3;
import cn.nukkit.scheduler.Task;
import cn.nukkit.scheduler.TaskHandler;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.session.Session;
import itoozh.core.session.procces.Process;
import itoozh.core.session.procces.ProcessType;
import itoozh.core.team.claim.ClaimType;
import itoozh.core.team.listener.ClaimListener;

public class ClaimProcess extends Process {


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String name;
    public Vector3 firstPos = null;
    public Vector3 secondPos = null;
    public ClaimType claimType;

    public int price = 0;

    public State state = State.claiming;

    public TaskHandler taskHandler;

    public ClaimProcess(long expireAt, Player player, ClaimType claimType, String name) {
        super(expireAt, player, ProcessType.CLAIM_PROCESS);
        this.name = name;
        this.claimType = claimType;
        taskHandler = Main.getInstance().getServer().getScheduler().scheduleDelayedTask(Main.getInstance(), () -> {
            Session session = Main.getInstance().getSessionManager().getSession(player);
            session.setProcess(null);
            if (player.isOnline()) {
                if (player.getInventory().contains(Main.getInstance().getTeamManager().getClaimManager().item)) {
                    player.getInventory().remove(Main.getInstance().getTeamManager().getClaimManager().item);
                }
                player.sendMessage(TextFormat.YELLOW + "Claiming process just expired!");
            }

        }, Math.toIntExact(20 * expireAt));
    }

    public void stop() {
        taskHandler.cancel();
    }

    @Override
    public void resetExpireAt() {
        super.resetExpireAt();
        taskHandler.cancel();
        taskHandler = Main.getInstance().getServer().getScheduler().scheduleDelayedTask(Main.getInstance(), () -> {
            Session session = Main.getInstance().getSessionManager().getSession(player);
            session.setProcess(null);
            if (player.isOnline()) {
                if (player.getInventory().contains(Main.getInstance().getTeamManager().getClaimManager().item)) {
                    player.getInventory().remove(Main.getInstance().getTeamManager().getClaimManager().item);
                }
                new ClaimListener().clearAllPillars(player, this);
                player.sendMessage(TextFormat.YELLOW + "Claiming process just expired!");
            }

        }, Math.toIntExact(20 * fullExpireTime));
    }

    public enum State {
        claiming,
        waiting
    }
}
