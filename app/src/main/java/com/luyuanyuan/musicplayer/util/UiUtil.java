package com.luyuanyuan.musicplayer.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.Window;
import android.view.WindowManager;

public class UiUtil {
    private static final int FLAG_SYSTEM_BAR_BASE = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

    public static void setStatusBarColor(Window window, int color) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);
    }

    public static void setNavigationBarColor(Window window, int color) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setNavigationBarColor(color);
    }

    public static void setLightStatusBar(Window window, int flag) {
        window.getDecorView().setSystemUiVisibility(flag | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    public static void setLightNavigationBar(Window window, int flag) {
        window.getDecorView().setSystemUiVisibility(flag | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
    }

    public static void expandStatusBar(Window window, int flag) {
        window.getDecorView().setSystemUiVisibility(flag | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | FLAG_SYSTEM_BAR_BASE);
    }

    public static void expandNavigationBar(Window window, int flag) {
        window.getDecorView().setSystemUiVisibility(flag | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | FLAG_SYSTEM_BAR_BASE);
    }

    public static void roundView(View view, final float conner) {
        view.setClipToOutline(true);
        view.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), conner);
            }
        });
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static Bitmap blurBitmap(Context context, Bitmap srcBitmap, float blurLevel) {
        if (blurLevel <= 0) {
            return srcBitmap;
        }
        Bitmap outBitmap = Bitmap.createBitmap(srcBitmap.getWidth(), srcBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        // 初始化Renderscript，该类提供了RenderScript context，创建其他RS类之前必须先创建这个类，其控制RenderScript的初始化，资源管理及释放
        RenderScript renderScript = RenderScript.create(context);
        // 创建高斯模糊对象
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        // 创建Allocations，此类是将数据传递给RenderScript内核的主要方 法，并制定一个后备类型存储给定类型
        Allocation allIn = Allocation.createFromBitmap(renderScript, srcBitmap);
        Allocation allOut = Allocation.createFromBitmap(renderScript, outBitmap);
        //设定模糊度(注：Radius最大只能设置25.f)
        blurScript.setRadius(blurLevel);
        // Perform the Renderscript
        blurScript.setInput(allIn);
        blurScript.forEach(allOut);
        // Copy the final bitmap created by the out Allocation to the outBitmap
        allOut.copyTo(outBitmap);
        // recycle the original bitmap
        // bitmap.recycle();
        // After finishing everything, we destroy the Renderscript.
        renderScript.destroy();
        return outBitmap;
    }
}
