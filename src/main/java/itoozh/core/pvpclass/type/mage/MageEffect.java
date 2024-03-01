package itoozh.core.pvpclass.type.mage;

import cn.nukkit.Player;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.session.Session;
import itoozh.core.team.Team;
import itoozh.core.team.claim.Claim;
import itoozh.core.team.claim.ClaimType;
import itoozh.core.util.ItemUtil;
import itoozh.core.util.LanguageUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class MageEffect {
    private int energyRequired;
    private int mageDistance;
    private Effect effect;

    public MageEffect(Map<String, Object> map) {
        this.effect = ItemUtil.getEffect((String) map.get("EFFECT"));
        this.energyRequired = (int) map.get("ENERGY_REQUIRED");
        this.mageDistance = Main.getInstance().getConfig().getInt("MAGE_CLASS.MAGE_DISTANCE");
    }

    public void applyEffect(Player player) {
        Session session = Main.getInstance().getSessionManager().getSession(player);
        Team team = session.getTeam();
        int distance = this.mageDistance / 2;
        player.sendMessage(TextFormat.colorize(LanguageUtils.getString("PVP_CLASSES.MAGE_CLASS.USED_EFFECT").replaceAll("%effect%", this.effect.getName()).replaceAll("%energy%", String.valueOf(this.energyRequired))));

        for (Player entity : player.getLevel().getPlayers().values()) {
            if (entity != player && player.distance(entity) <= this.mageDistance) {
                Claim targetTeam = Main.getInstance().getTeamManager().getClaimManager().findClaim(entity.getLocation());
                if (targetTeam != null && targetTeam.getType() == ClaimType.SPAWN) {
                    continue;
                }
                if (team != null) {
                    if (team.getPlayers().contains(player.getUniqueId())) {
                        continue;
                    }
                }
                Main.getInstance().getPvPClassManager().addEffect(entity, this.effect.clone());
            }
        }
    }
}