package com.sharry.picturepicker.picker.manager;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

import com.sharry.picturepicker.crop.manager.CropConfig;
import com.sharry.picturepicker.camera.manager.CameraConfig;

import java.util.ArrayList;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 图片选择器的配置属性类
 *
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.1
 * @since 2018/11/29 17:07
 */
public class PickerConfig implements Parcelable {

    public static final int INVALIDATE_VALUE = -1;

    public static Builder Builder() {
        return new Builder();
    }

    public static final Creator<PickerConfig> CREATOR = new Creator<PickerConfig>() {
        @Override
        public PickerConfig createFromParcel(Parcel in) {
            return new PickerConfig(in);
        }

        @Override
        public PickerConfig[] newArray(int size) {
            return new PickerConfig[size];
        }
    };

    // 图片选择的相关配置
    public ArrayList<String> userPickedSet = new ArrayList<>();
    public int threshold = 9;
    public int spanCount = 3;
    // Toolbar 背景色
    public int toolbarBkgColor = Color.parseColor("#ff64b6f6");                 // 背景色
    public int toolbarBkgDrawableResId = INVALIDATE_VALUE;                                  // 背景的Drawable
    // 整体背景色
    public int pickerBackgroundColor = INVALIDATE_VALUE;
    // Item 背景色
    public int pickerItemBackgroundColor = Color.WHITE;
    // 指示器背景色
    public int indicatorTextColor = Color.WHITE;
    public int indicatorSolidColor = Color.parseColor("#ff64b6f6");            // 指示器选中的填充色
    public int indicatorBorderCheckedColor = indicatorSolidColor;                          // 指示器边框选中的颜色
    public int indicatorBorderUncheckedColor = Color.WHITE;                                // 指示器边框未被选中的颜色
    // 是否展示滚动动画
    public boolean isToolbarBehavior = false;
    public boolean isFabBehavior = false;
    public CameraConfig cameraConfig;                                                       // 拍照的配置
    public CropConfig cropConfig;                                                           // 裁剪的配置

    private PickerConfig() {
    }

    public Builder newBuilder() {
        return new Builder(this);
    }

    /**
     * 是否支持相机
     */
    public boolean isCameraSupport() {
        return cameraConfig != null;
    }

    /**
     * 是否支持裁剪
     */
    public boolean isCropSupport() {
        return cropConfig != null;
    }

    protected PickerConfig(Parcel in) {
        userPickedSet = in.createStringArrayList();
        threshold = in.readInt();
        spanCount = in.readInt();
        toolbarBkgColor = in.readInt();
        toolbarBkgDrawableResId = in.readInt();
        pickerBackgroundColor = in.readInt();
        pickerItemBackgroundColor = in.readInt();
        indicatorTextColor = in.readInt();
        indicatorSolidColor = in.readInt();
        indicatorBorderCheckedColor = in.readInt();
        indicatorBorderUncheckedColor = in.readInt();
        isToolbarBehavior = in.readByte() != 0;
        isFabBehavior = in.readByte() != 0;
        cameraConfig = in.readParcelable(CameraConfig.class.getClassLoader());
        cropConfig = in.readParcelable(CropConfig.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(userPickedSet);
        dest.writeInt(threshold);
        dest.writeInt(spanCount);
        dest.writeInt(toolbarBkgColor);
        dest.writeInt(toolbarBkgDrawableResId);
        dest.writeInt(pickerBackgroundColor);
        dest.writeInt(pickerItemBackgroundColor);
        dest.writeInt(indicatorTextColor);
        dest.writeInt(indicatorSolidColor);
        dest.writeInt(indicatorBorderCheckedColor);
        dest.writeInt(indicatorBorderUncheckedColor);
        dest.writeByte((byte) (isToolbarBehavior ? 1 : 0));
        dest.writeByte((byte) (isFabBehavior ? 1 : 0));
        dest.writeParcelable(cameraConfig, flags);
        dest.writeParcelable(cropConfig, flags);
    }

    public static class Builder {

        private PickerConfig mConfig;

        private Builder() {
            mConfig = new PickerConfig();
        }

        private Builder(@NonNull PickerConfig config) {
            this.mConfig = config;
        }

        /**
         * 设置相册可选的最大数量
         *
         * @param threshold 阈值
         */
        public Builder setThreshold(int threshold) {
            mConfig.threshold = threshold;
            return this;
        }

        /**
         * 设置用户已经选中的图片, 相册会根据 Path 比较, 在相册中打钩
         *
         * @param pickedPictures 已选中的图片
         */
        public Builder setPickedPictures(@NonNull ArrayList<String> pickedPictures) {
            mConfig.userPickedSet.addAll(pickedPictures);
            return this;
        }

        public Builder setSpanCount(int count) {
            mConfig.spanCount = count;
            return this;
        }

        /**
         * 设置 Toolbar 的背景色
         */
        public Builder setToolbarBackgroundColor(@ColorInt int color) {
            mConfig.toolbarBkgColor = color;
            return this;
        }

        /**
         * 设置 Toolbar 的背景图片
         *
         * @param drawableRes drawable 资源 ID
         */
        public Builder setToolbarBackgroundDrawableRes(@DrawableRes int drawableRes) {
            mConfig.toolbarBkgDrawableResId = drawableRes;
            return this;
        }

        /**
         * 设置图片选择器的背景色
         */
        public Builder setPickerBackgroundColor(@ColorInt int color) {
            mConfig.pickerBackgroundColor = color;
            return this;
        }

        /**
         * 设置图片选择器的背景色
         */
        public Builder setPickerItemBackgroundColor(@ColorInt int color) {
            mConfig.pickerItemBackgroundColor = color;
            return this;
        }

        /**
         * 设置选择索引的边框颜色
         *
         * @param textColor 边框的颜色
         */
        public Builder setIndicatorTextColor(@ColorInt int textColor) {
            mConfig.indicatorTextColor = textColor;
            return this;
        }

        /**
         * 设置选择索引的边框颜色
         *
         * @param solidColor 边框的颜色
         */
        public Builder setIndicatorSolidColor(@ColorInt int solidColor) {
            mConfig.indicatorSolidColor = solidColor;
            return this;
        }

        /**
         * 设置选择索引的边框颜色
         *
         * @param checkedColor   选中的边框颜色的 Res Id
         * @param uncheckedColor 未选中的边框颜色的Res Id
         */
        public Builder setIndicatorBorderColor(@ColorInt int checkedColor, @ColorInt int uncheckedColor) {
            mConfig.indicatorBorderCheckedColor = checkedColor;
            mConfig.indicatorBorderUncheckedColor = uncheckedColor;
            return this;
        }

        /**
         * 是否设置 Toolbar Behavior 动画
         */
        public Builder isToolbarScrollable(boolean isToolbarScrollable) {
            mConfig.isToolbarBehavior = isToolbarScrollable;
            return this;
        }

        /**
         * 是否设置 Fab Behavior 滚动动画
         */
        public Builder isFabScrollable(boolean isFabScrollable) {
            mConfig.isFabBehavior = isFabScrollable;
            return this;
        }

        public Builder setCropConfig(@Nullable CropConfig cropConfig) {
            mConfig.cropConfig = cropConfig;
            return this;
        }

        public Builder setCameraConfig(@Nullable CameraConfig cameraConfig) {
            mConfig.cameraConfig = cameraConfig;
            return this;
        }

        public PickerConfig build() {
            return mConfig;
        }

    }
}
