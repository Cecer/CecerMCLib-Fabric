package com.cecer1.projects.mc.cecermclib.fabric.modules.overlaytest.fbo;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.HashMap;
import java.util.Map;

public class FBORef extends PhantomReference<FBO> {

    private static final ReferenceQueue<FBO> fboRefQueue = new ReferenceQueue<>();
    private static final Map<FBOResources, FBORef> fboRefHolder = new HashMap<>();
    public static void handleForPhantoms() {
        FBORef ref;
        while ((ref = (FBORef) fboRefQueue.poll()) != null) {
            ref.dispose();
        }
    }
    static void track(FBO fbo, FBOResources resources) {
        FBORef ref = new FBORef(fbo, resources);
        FBORef.fboRefHolder.put(resources, ref);
    }
    static void manuallyDispose(FBOResources resources) {
        FBORef ref = FBORef.fboRefHolder.get(resources);
        if (ref != null) {
            ref.dispose();
        }
    }



    private FBOResources resources;
    private FBORef(FBO fbo, FBOResources resources) {
        super(fbo, FBORef.fboRefQueue);
        this.resources = resources;
    }

    private void dispose() {
        if (this.resources != null) {
            FBORef.fboRefHolder.remove(this.resources);
            this.resources.dispose();
            this.resources = null;
        }
    }
}