package ikeyler.mlmod;

import ikeyler.mlmod.messages.Messages;
import ikeyler.mlmod.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TitleListener {
    private String lastTitle;
    private String lastSubtitle;
    private final Minecraft mc = Minecraft.getMinecraft();
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onRenderOverlay(RenderGameOverlayEvent event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.SUBTITLES) {
            String title = getTitle(false);
            String subtitle = getTitle(true);
            if ((title != null && !title.equals(lastTitle)) || (subtitle != null && !subtitle.equals(lastSubtitle))) {
                onTitleChanged(title, subtitle);
                lastTitle = title;
                lastSubtitle = subtitle;
            }
        }
    }
    private String getTitle(boolean subtitle) {
        String[] fields = subtitle
                ? new String[]{"displayedSubTitle", "field_175200_y"}
                : new String[]{"displayedTitle", "field_175201_x"};
        for (String field : fields) {
            try {
                String value = ObfuscationReflectionHelper.getPrivateValue(GuiIngame.class, mc.ingameGUI, field);
                if (value != null) return value;
            }
            catch (Exception ignore) {}
        }
        return null;
    }
    private void onTitleChanged(String title, String subtitle) {
        String unfTitle = TextUtil.removeColors(title);
        String unfSubtitle = TextUtil.removeColors(subtitle);
        if (Messages.contains("TITLE", unfTitle) || Messages.contains("SUBTITLE", unfSubtitle)) {
            clearTitle();
        }
    }
    private void clearTitle() {
        mc.ingameGUI.displayTitle(null, null, -1, -1, -1);
    }
}
