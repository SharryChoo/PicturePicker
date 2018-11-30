package com.sharry.picturepicker.picker.manager;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;

import com.sharry.picturepicker.picker.impl.PicturePickerActivity;
import com.sharry.picturepicker.support.loader.IPictureLoader;
import com.sharry.picturepicker.support.loader.PictureLoader;
import com.sharry.picturepicker.support.permission.PermissionsCallback;
import com.sharry.picturepicker.support.permission.PermissionsManager;

import androidx.annotation.NonNull;

/**
 * Created by Sharry on 2018/6/13.
 * Email: SharryChooCHN@Gmail.com
 * Version: 1.1
 * Description: 图片选择器的管理类
 */
public class PicturePickerManager {

    public static final String TAG = PicturePickerManager.class.getSimpleName();

    public static PicturePickerManager with(@NonNull Context context) {
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            return new PicturePickerManager(activity);
        } else {
            throw new IllegalArgumentException("PicturePickerManager.with -> Context can not cast to Activity");
        }
    }

    private String[] mPermissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private Activity mActivity;
    private PicturePickerFragment mPickerFragment;
    private PickerConfig mConfig;

    private PicturePickerManager(@NonNull Activity activity) {
        this.mActivity = activity;
        this.mPickerFragment = getCallbackFragment(mActivity);
    }

    /**
     * 设置图片加载方案
     */
    public PicturePickerManager setPictureLoader(@NonNull IPictureLoader loader) {
        PictureLoader.setPictureLoader(loader);
        return this;
    }

    /**
     * 设置图片选择的配置
     */
    public PicturePickerManager setPickerConfig(PickerConfig config) {
        this.mConfig = config;
        return this;
    }

    /**
     * 发起请求
     *
     * @param pickerCallback 图片选中的回调
     */
    public void start(@NonNull final PickerCallback pickerCallback) {
        PermissionsManager.getManager(mActivity)
                .request(mPermissions)
                .execute(new PermissionsCallback() {
                    @Override
                    public void onResult(boolean granted) {
                        if (granted) {
                            startActual(pickerCallback);
                        }
                    }
                });
    }

    /**
     * 处理 PicturePickerActivity 的启动
     */
    private void startActual(@NonNull PickerCallback pickerCallback) {
        verify();

        mPickerFragment.setPickerCallback(pickerCallback);
        PicturePickerActivity.startActivityForResult(mActivity, mPickerFragment, mConfig);
    }

    private void verify() {
        // 1. 验证是否实现了图片加载器
        if (PictureLoader.getPictureLoader() == null) {
            throw new UnsupportedOperationException("PictureLoader.load -> please invoke setPictureLoader first");
        }
        // 2. 若开启了裁剪, 则只能选中一张图片
        if (mConfig.isCropSupport()) {
            mConfig.rebuild()
                    .setThreshold(1)
                    .setPickedPictures(null)
                    .build();
        }
    }

    /**
     * 获取用于回调的 Fragment
     */
    private PicturePickerFragment getCallbackFragment(Activity activity) {
        PicturePickerFragment pickerFragment = findCallbackFragment(activity);
        if (pickerFragment == null) {
            pickerFragment = PicturePickerFragment.newInstance();
            FragmentManager fragmentManager = activity.getFragmentManager();
            fragmentManager.beginTransaction().add(pickerFragment, TAG).commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
        }
        return pickerFragment;
    }

    /**
     * 在 Activity 中通过 TAG 去寻找我们添加的 Fragment
     */
    private PicturePickerFragment findCallbackFragment(Activity activity) {
        return (PicturePickerFragment) activity.getFragmentManager().findFragmentByTag(TAG);
    }

}
