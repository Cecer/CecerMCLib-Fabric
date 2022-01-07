package com.cecer1.projects.mc.cecermclib.fabric.modules.rendering;
//
//import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
//import net.minecraft.client.gui.hud.InGameOverlayRenderer;
//import net.minecraftforge.client.event.RenderGameOverlayEvent;
//import net.minecraftforge.common.MinecraftForge;
//import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
//
//import java.util.Iterator;
//
//public class RenderSequencer {
//
//    private RenderingModule renderManager;
//    private Iterator<RenderLayer> iter;
//    private RenderLayer resumeLayer;
//
//    RenderSequencer(RenderingModule renderManager) {
//        this.renderManager = renderManager;
//
//    }
//
//    @SubscribeEvent(receiveCanceled = true)
//    public void onPreOverlay(RenderGameOverlayEvent.Pre event) {
//        // TODO: Add events for the steps forge doesn't provide with mixins
//        switch (event.type) {
//            // 0    render
//            // 100  renderVignetteOverlay
//            // 200  renderSpyglassOverlay or renderOverlay (mutually exclusive)
//            // 250  renderOverlay(POWDER_SNOW_OUTLINE)
//            // 300  renderPortalOverlay
//            // 400  spectatorHud.render or renderHotbar (mutually exclusive)
//            // 500  renderCrosshair
//            // 600  bossBarHud.render
//            // 700  renderStatusBars (moved to an early rendering stage) // Multiple merged into one step
//            // 1000 renderMountHealth
//            // 1300 renderMountJumpBar
//            // 1400 renderExperienceBar
//            // 1500 renderHeldItemTooltip or spectatorHud.render(again?)  (mutually exclusive)
//            // 1530 {sleep stuff} // Moved from 1200
//            // 1570 renderDemoTimer
//            // 1600 debugHud.render
//            // 1800 overlayMessage // Record title and stuff
//            // 1900 title
//            // 1950 subtitles
//            // 2000 chatHud.render
//            // 2100 playerListHud.render
//            // 
//            
//            case ALL:
//                this.being();
//                this.renderUntil(0, false);
//                break;
//            // VIGNETTE         100 (non-forge) // renderVignetteOverlay
//            case HELMET:
//                this.renderUntil(200, false);
//                break;
//            case PORTAL:
//                this.renderUntil(300, false);
//                break;
//            case HOTBAR:
//                this.renderUntil(400, false);
//                break;
//            case CROSSHAIRS:
//                this.renderUntil(500, false);
//                break;
//            case BOSSHEALTH:
//                this.renderUntil(600, false);
//                break;
//            case HEALTH:
//                this.renderUntil(700, false);
//                break;
//            case ARMOR:
//                this.renderUntil(800, false);
//                break;
//            case FOOD:
//                this.renderUntil(900, false);
//                break;
//            case HEALTHMOUNT:
//                this.renderUntil(1000, false);
//                break;
//            case AIR:
//                this.renderUntil(1100, false);
//                break;
//            // SLEEP_FADE       1200 (non-forge)
//            case JUMPBAR:
//                this.renderUntil(1300, false);
//                break;
//            case EXPERIENCE:
//                this.renderUntil(1400, false);
//                break;
//            // TOOL_HIGHLIGHT   1500 (non-forge)
//            case TEXT:
//                this.renderUntil(1600, false);
//                break;
//            // RECORD           1800 (non-forge)
//            // TITLE_RENDER     190 (non-forge)
//            // SCOREBOARD       1950 (non-forge)
//            case CHAT:
//                this.renderUntil(2000, false);
//                break;
//            case PLAYER_LIST:
//                this.renderUntil(2100, false);
//                break;
//            // ContextMenuRenderLayer (1000000)
//        }
//    }
//    @SubscribeEvent(receiveCanceled = true)
//    public void onPostOverlay(RenderGameOverlayEvent.Post event) {
//        if (event.type == RenderGameOverlayEvent.ElementType.ALL) {
////            FBOTester.runTest();
//        }
//        switch (event.type) {
//            case ALL:
//                this.renderUntil(Integer.MAX_VALUE, true);
//                this.end();
//                break;
//        }
//    }
//
//    private void being() {
//        this.iter = this.renderManager.getLayerIterator();
//        this.resumeLayer = null;
//        this.renderManager.beingFrame();
//    }
//    private void end() {
//        this.iter = null;
//        this.resumeLayer = null;
//        this.renderManager.endFrame();
//    }
//
//    private void renderUntil(int untilRenderOrderValue, boolean inclusive) {
//        if (this.iter == null) {
//            throw new IllegalStateException("Attempt to call renderUpTo(int) before being() has been called!");
//        }
//
//        if (this.resumeLayer != null) {
//            if (this.resumeLayer.getRenderOrder() > untilRenderOrderValue || (inclusive && this.resumeLayer.getRenderOrder() == untilRenderOrderValue)) {
//                return;
//            }
//            this.resumeLayer.doRender(this.renderManager.getCurrentRenderContext());
//            this.resumeLayer = null;
//        }
//
//        while (this.iter.hasNext()) {
//            RenderLayer layer = this.iter.next();
//            if (layer.getRenderOrder() > untilRenderOrderValue || (inclusive && layer.getRenderOrder() == untilRenderOrderValue)) {
//                this.resumeLayer = layer;
//                return;
//            }
//            layer.doRender(this.renderManager.getCurrentRenderContext());
//        }
//    }
//}
