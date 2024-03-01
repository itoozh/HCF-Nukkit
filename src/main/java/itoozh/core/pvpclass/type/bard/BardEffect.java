package itoozh.core.pvpclass.type.bard;

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
public class BardEffect {
    private Effect effect;
    private int duration;
    private int effectDuration;
    private int energyRequired;
    private boolean effectFriendlies;
    private boolean effectEnemies;
    private boolean clickable;
    private int bardDistance;
    private boolean effectSelf;

    public BardEffect(Map<String, Object> map, boolean clickeable) {
        this.effect = ItemUtil.getEffect((String) map.get("EFFECT")).clone();
        this.duration = effect.getDuration();
        this.bardDistance = Main.getInstance().getConfig().getInt("BARD_CLASS.BARD_DISTANCE");
        this.effectFriendlies = (boolean) map.get("EFFECT_FRIENDLIES");
        this.effectSelf = (boolean) map.get("EFFECT_SELF");
        this.effectEnemies = (boolean) map.get("EFFECT_ENEMIES");
        this.clickable = clickeable;
        if (clickeable) {
            this.energyRequired = (int) map.get("ENERGY_REQUIRED");
        }
    }

    public BardEffect(boolean clickeable, Effect effect) {
        this.clickable = clickeable;
        this.effect = effect;
        this.bardDistance = Main.getInstance().getConfig().getInt("BARD_CLASS.BARD_DISTANCE");
        this.energyRequired = 0;
        this.effectFriendlies = true;
        this.effectSelf = true;
        this.effectEnemies = false;
    }

    public void applyEffect(Player player) {
        Effect effect = this.effect.clone();
        effect.setDuration(this.duration);
        Session session = Main.getInstance().getSessionManager().getSession(player);
        Team team = session.getTeam();
        if (this.clickable) {
            if (this.energyRequired == 0) {
                player.sendMessage(TextFormat.colorize(LanguageUtils.getString("PVP_CLASSES.BARD_CLASS.USED_EFFECT_NO_ENERGY").replaceAll("%effect%", effect.getName())));
            } else {
                player.sendMessage(TextFormat.colorize(LanguageUtils.getString("PVP_CLASSES.BARD_CLASS.USED_EFFECT").replaceAll("%effect%", effect.getName()).replaceAll("%energy%", String.valueOf(this.energyRequired))));
            }
        }
        if (this.effectSelf) {
            Main.getInstance().getPvPClassManager().addEffect(player, effect);
        }

        for (Player entity : player.getLevel().getPlayers().values()) {
            if (entity != player && player.distance(entity) <= this.bardDistance) {

                Claim targetTeam = Main.getInstance().getTeamManager().getClaimManager().findClaim(entity.getLocation());
                if (targetTeam != null) {
                    if (targetTeam.getType() == ClaimType.SPAWN) {
                        continue;
                    }
                }
                if (this.effectFriendlies && team != null && team.getPlayers().contains(entity.getUniqueId())) {
                    Main.getInstance().getPvPClassManager().addEffect(entity, effect);
                    if (this.clickable) {
                        entity.sendMessage(TextFormat.colorize(LanguageUtils.getString("PVP_CLASSES.BARD_CLASS.TEAM_EFFECT").replaceAll("%player%", player.getName()).replaceAll("%effect%", effect.getName())));
                    }
                }
                if (!this.effectEnemies) {
                    continue;
                }
                if (team != null) {

                    if (team.getPlayers().contains(entity.getUniqueId())) {
                        continue;
                    }
                }
                Main.getInstance().getPvPClassManager().addEffect(entity, effect);
            }
        }
    }
}