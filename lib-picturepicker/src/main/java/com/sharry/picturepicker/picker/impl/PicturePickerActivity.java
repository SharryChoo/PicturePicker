package com.sharry.picturepicker.picker.impl;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.sharry.picturepicker.R;
import com.sharry.picturepicker.picker.manager.PickerConfig;
import com.sharry.picturepicker.support.utils.ColorUtil;
import com.sharry.picturepicker.support.utils.VersionUtil;
import com.sharry.picturepicker.widget.PicturePickerFabBehavior;
import com.sharry.picturepicker.widget.toolbar.SToolbar;
import com.sharry.picturepicker.widget.toolbar.TextViewOptions;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 图片选择器的 Activity
 *
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.3
 * @since 2018/9/1 10:17
 */
public class PicturePickerActivity extends AppCompatActivity implements PicturePickerContract.IView,
        PictureAdapter.AdapterInteraction,
        FolderAdapter.AdapterInteraction,
        View.OnClickListener {

    /*
       Outer constants.
     */
    public static final String START_EXTRA_CONFIG = "start_intent_extra_config";// 用户配置的属性
    public static final String RESULT_EXTRA_PICKED_PICTURES = "result_intent_extra_picked_pictures";// 返回的图片

    /*
       Presenter associated with this Activity.
     */
    private PicturePickerContract.IPresenter mPresenter = new PicturePickerPresenter(this);

    /*
       Views
     */
    // Toolbar
    private SToolbar mToolbar;
    private TextView mTvToolbarFolderName;
    private TextView mTvToolbarEnsure;
    // Content pictures
    private RecyclerView mRecyclePictures;
    // bottom navigation menu
    private ViewGroup mMenuNaviContainer;
    private TextView mTvFolderName;
    private TextView mTvPreview;
    private RecyclerView mRecycleFolders;
    // Floating action bar
    private FloatingActionButton mFab;

    /*
      CoordinatorLayout behaviors.
     */
    private BottomSheetBehavior mBottomMenuBehavior;
    private PicturePickerFabBehavior mFabBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.libpicturepicker_activity_picture_picker);
        initTitle();
        initViews();
        initData();
    }

    protected void initTitle() {
        // 初始化视图
        mToolbar = findViewById(R.id.toolbar);
        // 设置标题文本
        mToolbar.setTitleText(getString(R.string.libpicturepicker_picker_all_picture));
        mTvToolbarFolderName = mToolbar.getTitleText();
        // 添加图片确认按钮
        mToolbar.addRightMenuText(
                TextViewOptions.Builder()
                        .setText(getString(R.string.libpicturepicker_picker_ensure))
                        .setTextSize(15)
                        .setListener(this)
                        .build()
        );
        mTvToolbarEnsure = mToolbar.getRightMenuView(0);
    }

    protected void initViews() {
        // Pictures recycler view.
        mRecyclePictures = findViewById(R.id.recycle_pictures);

        // Bottom navigation menu.
        mMenuNaviContainer = findViewById(R.id.rv_menu_navi_container);
        mTvFolderName = findViewById(R.id.tv_folder_name);
        mTvPreview = findViewById(R.id.tv_preview);
        mRecycleFolders = findViewById(R.id.recycle_folders);
        mTvFolderName.setOnClickListener(this);
        mTvPreview.setOnClickListener(this);
        mRecycleFolders.setLayoutManager(new LinearLayoutManager(this));
        mRecycleFolders.setHasFixedSize(true);
        mBottomMenuBehavior = BottomSheetBehavior.from(findViewById(R.id.ll_bottom_menu));
        mBottomMenuBehavior.setBottomSheetCallback(new BottomMenuNavigationCallback());

        // Floating action bar.
        mFab = findViewById(R.id.fab);
        mFab.setOnClickListener(this);
        mFabBehavior = PicturePickerFabBehavior.from(mFab);
    }

    protected void initData() {
        mPresenter.start(this, (PickerConfig)
                getIntent().getParcelableExtra(START_EXTRA_CONFIG));
    }

    @Override
    public void setToolbarBackgroundColor(int color) {
        mToolbar.setBackgroundColor(color);
    }

    @Override
    public void setToolbarBackgroundDrawable(int drawableId) {
        mToolbar.setBackgroundDrawableRes(drawableId);
    }

    @Override
    public void setToolbarScrollable(boolean isScrollable) {
        if (isScrollable) {
            AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) mToolbar.getLayoutParams();
            params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                    | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
                    | AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP);
            mToolbar.setLayoutParams(params);
        }
    }

    @Override
    public void setPicturesBackgroundColor(int color) {
        mRecyclePictures.setBackgroundColor(color);
    }

    @Override
    public void setPicturesSpanCount(int spanCount) {
        mRecyclePictures.setLayoutManager(new GridLayoutManager(this, spanCount));
    }

    @Override
    public void setPicturesAdapter(PickerConfig config, ArrayList<String> displayPaths, ArrayList<String> userPickedPaths) {
        mRecyclePictures.setAdapter(new PictureAdapter(this, config,
                displayPaths, userPickedPaths));
    }

    @Override
    public void setFolderAdapter(ArrayList<PictureFolder> allFolders) {
        mRecycleFolders.setAdapter(new FolderAdapter(this, allFolders));
    }

    @Override
    public void setFabColor(int color) {
        mFab.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    @Override
    public void switchFabVisibility(boolean isVisible) {
        if (isVisible) {
            mFab.show();
        } else {
            mFab.hide();
        }
    }

    @Override
    public void setPictureFolderText(String folderName) {
        // 更新文件夹名称
        mTvFolderName.setText(folderName);
        mTvToolbarFolderName.setText(folderName);
        mRecyclePictures.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void setToolbarEnsureText(CharSequence content) {
        mTvToolbarEnsure.setText(content);
    }

    @Override
    public void setPreviewText(CharSequence content) {
        mTvPreview.setText(content);
    }

    @Override
    public void notifyPickedPathsChanged() {
        mRecyclePictures.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void notifyDisplayPathsChanged() {
        mRecyclePictures.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void notifyDisplayPathsInsertToFirst() {
        mRecyclePictures.getAdapter().notifyItemInserted(1);
    }

    @Override
    public void notifyFolderDataSetChanged() {
        mRecycleFolders.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void showMsg(String msg) {
        Snackbar.make(mFab, msg, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public boolean onPictureChecked(String uri) {
        return mPresenter.handlePictureChecked(uri);
    }

    @Override
    public void onPictureRemoved(String uri) {
        mPresenter.handlePictureRemoved(uri);
    }

    @Override
    public void onPictureClicked(ImageView imageView, String uri, int position) {
        mPresenter.handlePictureClicked(position, imageView);
    }

    @Override
    public void onCameraClicked() {
        mPresenter.handleCameraClicked();
    }

    @Override
    public void onFolderChecked(int position) {
        mPresenter.handleFolderChecked(position);
        mBottomMenuBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_folder_name) {// 底部菜单按钮
            mBottomMenuBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else if (v.getId() == R.id.tv_preview) {// 预览按钮
            mPresenter.handlePreviewClicked();
        } else if (v == mTvToolbarEnsure || v.getId() == R.id.fab) {// 确认按钮
            mPresenter.handleEnsureClicked();
        }
    }

    @Override
    public void onBackPressed() {
        if (BottomSheetBehavior.STATE_COLLAPSED != mBottomMenuBehavior.getState()) {
            mBottomMenuBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Callback associated with bottom menu navigation bar.
     * Method will be invoked when menu scrolled.
     */
    private class BottomMenuNavigationCallback extends BottomSheetBehavior.BottomSheetCallback {

        private final Drawable folderDrawable;
        private final int bgCollapsedColor;
        private final int bgExpandColor;
        private final int textCollapsedColor;
        private final int textExpandColor;
        private int bgColor;
        private int textColor;

        BottomMenuNavigationCallback() {
            folderDrawable = mTvFolderName.getCompoundDrawables()[0];
            bgCollapsedColor = ContextCompat.getColor(PicturePickerActivity.this,
                    R.color.libpricturepicker_picker_bottom_menu_navi_bg_collapsed_color);
            bgExpandColor = ContextCompat.getColor(PicturePickerActivity.this,
                    R.color.libpricturepicker_picker_bottom_menu_navi_bg_expand_color);
            textCollapsedColor = ContextCompat.getColor(PicturePickerActivity.this,
                    R.color.libpricturepicker_picker_bottom_menu_navi_text_collapsed_color);
            textExpandColor = ContextCompat.getColor(PicturePickerActivity.this,
                    R.color.libpricturepicker_picker_bottom_menu_navi_text_expand_color);
        }

        @Override
        public void onStateChanged(@NonNull View view, int state) {
            mFabBehavior.setBehaviorValid(BottomSheetBehavior.STATE_COLLAPSED == state);
        }

        @Override
        public void onSlide(@NonNull View view, float fraction) {
            // Get background color associate with the bottom menu navigation bar.
            bgColor = ColorUtil.gradualChanged(fraction,
                    bgCollapsedColor, bgExpandColor);
            mMenuNaviContainer.setBackgroundColor(bgColor);
            // Get text color associate with the bottom menu  navigation bar.
            textColor = ColorUtil.gradualChanged(fraction,
                    textCollapsedColor, textExpandColor);
            // Set text drawable color before set text color with the purpose of decrease view draw.
            if (VersionUtil.isLollipop()) {
                folderDrawable.setTint(textColor);
            }
            // Set texts colors associate with the bottom menu.
            mTvFolderName.setTextColor(textColor);
            mTvPreview.setTextColor(textColor);
        }
    }

}
