package itoozh.core.gkit.profile.listener;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerAsyncPreLoginEvent;
import itoozh.core.Main;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProfileListener implements Listener {

    private final Main plugin;

    @EventHandler
    public void onJoin(PlayerAsyncPreLoginEvent event) {
        plugin.getProfileManager().getProfile(event.getUuid());
    }
}
