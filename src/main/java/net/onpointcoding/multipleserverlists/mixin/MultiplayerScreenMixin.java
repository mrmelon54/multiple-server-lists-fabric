package net.onpointcoding.multipleserverlists.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.ServerList;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.onpointcoding.multipleserverlists.duck.MultiplayerScreenDuckProvider;
import net.onpointcoding.multipleserverlists.util.CustomFileServerList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerScreen.class)
public class MultiplayerScreenMixin extends Screen implements MultiplayerScreenDuckProvider {
    private int currentTab = 0;

    @Shadow
    protected MultiplayerServerListWidget serverListWidget;
    @Shadow
    private ServerList serverList;

    @Shadow
    @Final
    private Screen parent;

    protected MultiplayerScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void injectedInit(CallbackInfo ci) {
        this.addDrawableChild(new ButtonWidget(this.width - 88, 4, 20, 20, new LiteralText("<"), (button) -> {
            currentTab--;
            reloadServerList();
        }));
        this.addDrawableChild(new ButtonWidget(this.width - 24, 4, 20, 20, new LiteralText(">"), (button) -> {
            currentTab++;
            reloadServerList();
        }));
        reloadServerList();
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void injectedRender(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (this.client != null)
            this.client.textRenderer.draw(matrices, new LiteralText(currentTab == 0 ? "Main" : String.valueOf(currentTab)), this.width - 64, 10, 0xffffff);
    }

    private void reloadServerList() {
        // Stop underflow
        if (currentTab < 0) currentTab = 0;

        if (currentTab == 0) serverList = new ServerList(MinecraftClient.getInstance());
        else serverList = new CustomFileServerList(MinecraftClient.getInstance(), currentTab);
        serverList.loadFile();
        this.serverListWidget.setServers(serverList);
    }

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "refresh", at = @At("HEAD"), cancellable = true)
    private void refresh(CallbackInfo ci) {
        MultiplayerScreen multiplayerScreen = new MultiplayerScreen(this.parent);
        if (multiplayerScreen instanceof MultiplayerScreenDuckProvider duckProvider)
            duckProvider.setCurrentTab(currentTab);
        MinecraftClient.getInstance().openScreen(multiplayerScreen);
        ci.cancel();
    }

    @Override
    public void setCurrentTab(int currentTab) {
        this.currentTab = currentTab;
    }
}
