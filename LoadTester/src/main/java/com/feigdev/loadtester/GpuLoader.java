package com.feigdev.loadtester;

/**
 * Created by ejf3 on 11/6/13.
 */
public class GpuLoader { //extends RSSurfaceView {
//    private RenderScriptGL mRSGL;
//    private SnowRS mRender;
//
//    public GpuLoader(Context context) {
//        super(context);
//    }
//
//    @Override
//    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
//        super.surfaceChanged(holder, format, w, h);
//        if (mRSGL == null) {
//            RenderScriptGL.SurfaceConfig sc = new RenderScriptGL.SurfaceConfig();
//            mRSGL = createRenderScriptGL(sc);
//            mRSGL.setSurface(holder, w, h);
//            mRender = new SnowRS(w, h);
//            mRender.init(mRSGL, getResources(), false);
//            mRender.start();
//        }
//    }
//    @Override
//    protected void onDetachedFromWindow() {
//        if (mRSGL != null) {
//            mRSGL = null;
//            destroyRenderScriptGL();
//        }
//    }
}
