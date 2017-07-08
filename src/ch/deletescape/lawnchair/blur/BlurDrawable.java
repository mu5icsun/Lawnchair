package ch.deletescape.lawnchair.blur;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class BlurDrawable extends Drawable implements BlurWallpaperProvider.Listener {

    private final Paint mPaint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG);
    private final Paint mClipPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF mClipRect = new RectF();

    private final BlurWallpaperProvider mProvider;
    private final float mRadius;
    private float mTranslation;
    private float mOffset;
    private Bitmap mWallpaper;
    private boolean mShouldDraw = true;
    private float mOverscroll;

    public BlurDrawable(BlurWallpaperProvider provider, float radius) {
        mProvider = provider;
        mRadius = radius;

        if (radius > 0) {
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        }

        onWallpaperChanged();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (!mShouldDraw || mWallpaper == null) return;

        if (mRadius > 0) {
            mClipRect.set(0, 0, canvas.getWidth(), canvas.getHeight());
            canvas.drawRoundRect(mClipRect, mRadius, mRadius, mClipPaint);
        }

        canvas.drawBitmap(mWallpaper, - mOffset - mOverscroll, -mTranslation, mPaint);
    }

    @Override
    public void setAlpha(int alpha) {
        mShouldDraw = alpha == 255;
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public void startListening() {
        mProvider.addListener(this);
    }

    public void stopListening() {
        mProvider.removeListener(this);
    }

    @Override
    public void onWallpaperChanged() {
        mWallpaper = mProvider.getWallpaper();
        invalidateSelf();
    }

    @Override
    public void onOffsetChanged(float offset) {
        mOffset = offset;
        invalidateSelf();
    }

    public void setTranslation(float translation) {
        mTranslation = translation;
        invalidateSelf();
    }

    public void setOverscroll(float progress) {
        mOverscroll = progress;
        invalidateSelf();
    }
}
