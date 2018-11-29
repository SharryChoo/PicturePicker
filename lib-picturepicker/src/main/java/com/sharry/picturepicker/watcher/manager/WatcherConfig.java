package com.sharry.picturepicker.watcher.manager;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

/**
 * 图片查看器相关的配置
 *
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2018/9/22 17:56
 */
public class WatcherConfig implements Parcelable {

    public static Builder Builder() {
        return new Builder();
    }

    // 图片选择的相关配置
    public ArrayList<String> pictureUris;// 需要展示的集合
    public ArrayList<String> userPickedSet;// 选中的集合
    public int threshold;// 阈值

    // 指示器背景色
    public int indicatorTextColor = Color.WHITE;
    public int indicatorSolidColor = Color.parseColor("#ff64b6f6");     // 指示器选中的填充色
    public int indicatorBorderCheckedColor = indicatorSolidColor;                   // 指示器边框选中的颜色
    public int indicatorBorderUncheckedColor = Color.WHITE;                         // 指示器边框未被选中的颜色

    // 定位展示的位置
    public int position;

    public WatcherConfig() {
    }

    public Builder newBuilder() {
        return new Builder(this);
    }

    protected WatcherConfig(Parcel in) {
        pictureUris = in.createStringArrayList();
        userPickedSet = in.createStringArrayList();
        threshold = in.readInt();
        indicatorTextColor = in.readInt();
        indicatorSolidColor = in.readInt();
        indicatorBorderCheckedColor = in.readInt();
        indicatorBorderUncheckedColor = in.readInt();
        position = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(pictureUris);
        dest.writeStringList(userPickedSet);
        dest.writeInt(threshold);
        dest.writeInt(indicatorTextColor);
        dest.writeInt(indicatorSolidColor);
        dest.writeInt(indicatorBorderCheckedColor);
        dest.writeInt(indicatorBorderUncheckedColor);
        dest.writeInt(position);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<WatcherConfig> CREATOR = new Creator<WatcherConfig>() {
        @Override
        public WatcherConfig createFromParcel(Parcel in) {
            return new WatcherConfig(in);
        }

        @Override
        public WatcherConfig[] newArray(int size) {
            return new WatcherConfig[size];
        }
    };

    public static class Builder {

        private WatcherConfig mConfig;

        private Builder() {
            mConfig = new WatcherConfig();
        }

        private Builder(@NonNull WatcherConfig config) {
            this.mConfig = config;
        }

        /**
         * 选择的最大阈值
         */
        public Builder setThreshold(int threshold) {
            mConfig.threshold = threshold;
            return this;
        }

        /**
         * 需要展示的 URI
         */
        public Builder setPictureUri(@NonNull String uri) {
            ArrayList<String> pictureUris = new ArrayList<>();
            pictureUris.add(uri);
            setPictureUris(pictureUris, 0);
            return this;
        }

        /**
         * 需要展示的 URI 集合
         *
         * @param pictureUris 数据集合
         * @param position    展示的位置
         */
        public Builder setPictureUris(@NonNull ArrayList<String> pictureUris, int position) {
            mConfig.pictureUris = pictureUris;
            mConfig.position = position;
            return this;
        }

        /**
         * 设置用户已经选中的图片, 相册会根据 Path 比较, 在相册中打钩
         *
         * @param pickedPictures 已选中的图片
         */
        public Builder setUserPickedSet(@NonNull ArrayList<String> pickedPictures) {
            mConfig.userPickedSet = pickedPictures;
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

        public WatcherConfig build() {
            return mConfig;
        }

    }
}
