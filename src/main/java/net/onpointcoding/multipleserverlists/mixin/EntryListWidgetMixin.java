package net.onpointcoding.multipleserverlists.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.onpointcoding.multipleserverlists.duck.EntryListWidgetDuckProvider;
import net.onpointcoding.multipleserverlists.duck.MultiplayerScreenDuckProvider;
import net.onpointcoding.multipleserverlists.duck.ServerEntryDuckProvider;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Objects;

@Mixin(EntryListWidget.class)
public abstract class EntryListWidgetMixin<E extends EntryListWidget.Entry<E>> implements EntryListWidgetDuckProvider {
    private Runnable refreshCallback;

    @Shadow
    public abstract int getRowLeft();

    @Shadow
    public abstract int getRowWidth();

    @Shadow
    @Final
    protected int itemHeight;

    @Shadow
    protected int bottom;

    @Shadow
    protected int top;

    @Shadow
    protected abstract int getRowBottom(int index);

    @Shadow
    protected abstract int getRowTop(int index);

    @Shadow
    protected abstract int getEntryCount();

    @Shadow
    protected abstract E getEntry(int index);

    @Shadow
    public abstract int getMaxScroll();

    @Shadow
    protected int headerHeight;

    @Shadow
    public abstract double getScrollAmount();

    @Shadow
    public abstract List<E> children();

    @Shadow
    @Final
    protected MinecraftClient client;

    @Shadow
    private double scrollAmount;

    @Inject(method = "renderList", at = @At("TAIL"))
    private void injectedRenderList(MatrixStack matrices, int x, int y, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        //noinspection ConstantConditions
        if (((Object) this) instanceof MultiplayerServerListWidget) {
            E pEntry = this.getEntryAtOffsetY(mouseY);
            boolean isScrollable = this.getMaxScroll() > 0;
            int i = this.getEntryCount();
            for (int j = 0; j < i; ++j) {
                int k = this.getRowTop(j);
                int l = this.getRowBottom(j);
                if (l >= this.top && k <= this.bottom) {
                    int n = this.itemHeight - 4;
                    E entry = this.getEntry(j);
                    int o = this.getRowWidth();
                    int r = this.getRowLeft();
                    if (entry instanceof ServerEntryDuckProvider serverEntryDuckProvider)
                        serverEntryDuckProvider.extendedRender(matrices, j, k, r, o, n, mouseX, mouseY, Objects.equals(pEntry, entry), delta, isScrollable);
                }
            }
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void injectedMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        //noinspection ConstantConditions
        if (((Object) this) instanceof MultiplayerServerListWidget) {
            int pIdx = this.getEntryIndex(mouseY);
            E pEntry = this.getEntryAtOffsetY(mouseY);
            if (pEntry != null) {
                boolean isScrollable = this.getMaxScroll() > 0;
                int t = this.getRowWidth();
                int u = isScrollable ? t + 6 : t;
                double v = mouseX - this.getRowLeft();
                if (this.client.currentScreen instanceof MultiplayerScreenDuckProvider duckProvider) {
                    int q = 0;
                    if (v < 0 && v > -16) q = -1;
                    else if (v > u && v < u + 16) q = 1;

                    if (q != 0) {
                        ServerList firstServerList = duckProvider.getServerListForTab(duckProvider.getCurrentTab());
                        ServerList secondServerList = duckProvider.getServerListForTab(duckProvider.getCurrentTab() + q);
                        if (firstServerList != null && secondServerList != null) {
                            // Load
                            firstServerList.loadFile();
                            secondServerList.loadFile();

                            // Grab and move
                            ServerInfo serverInfo = firstServerList.get(pIdx);
                            firstServerList.remove(serverInfo);
                            secondServerList.add(serverInfo);

                            // Save
                            firstServerList.saveFile();
                            secondServerList.saveFile();
                        }

                        resetScrollPosition();
                        if (refreshCallback != null) refreshCallback.run();

                        cir.setReturnValue(true);
                        cir.cancel();
                    }
                }
            }
        }
    }

    private int getEntryIndex(double y) {
        int m = MathHelper.floor(y - (double) this.top) - this.headerHeight + (int) this.getScrollAmount() - 4;
        return m / this.itemHeight;
    }

    @Nullable
    private E getEntryAtOffsetY(double y) {
        int m = MathHelper.floor(y - (double) this.top) - this.headerHeight + (int) this.getScrollAmount() - 4;
        int n = m / this.itemHeight;
        return n >= 0 && m >= 0 && n < this.getEntryCount() ? this.children().get(n) : null;
    }

    @Override
    public void resetScrollPosition() {
        scrollAmount = 0;
    }

    @Override
    public void setRefreshCallback(Runnable callback) {
        refreshCallback = callback;
    }
}
