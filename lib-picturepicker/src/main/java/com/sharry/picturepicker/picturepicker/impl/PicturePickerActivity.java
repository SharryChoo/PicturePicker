package com.sharry.picturepicker.picturepicker.impl;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.sharry.picturepicker.R;
import com.sharry.picturepicker.picturepicker.manager.PickerConfig;
import com.sharry.picturepicker.widget.PicturePickerFabBehavior;
import com.sharry.picturepicker.widget.toolbar.AppBarHelper;
import com.sharry.picturepicker.widget.toolbar.CommonToolbar;
import com.sharry.picturepicker.widget.toolbar.Style;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
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
       Inner constants associated with toolbar.
     */
    private final int TAG_TOOLBAR_BACK = 0x00000001;
    private final int TAG_TOOLBAR_CHECKED_DETAIL = 0x00000002;
    private final int TAG_TOOLBAR_ENSURE = 0x00000003;

    /*
       Presenter associated with this Activity.
     */
    private PicturePickerContract.IPresenter mPresenter = new PicturePickerPresenter(this);

    /*
       Views
     */
    // Toolbar
    private CommonToolbar mToolbar;
    private TextView mTvToolbarFolderName;
    private TextView mTvToolbarEnsure;
    // Content pictures
    private RecyclerView mRvPictures;
    // bottom menu
    private TextView mTvSelectedFolderName;
    private TextView mTvPreview;
    private RecyclerView mRvFolders;
    // Floating action bar
    private FloatingActionButton mFab;

    /*
      Bottom drawer behavior.
     */
    private BottomSheetBehavior mBottomMenuBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.libpicturepicker_activity_picture_picker);
        initTitle();
        initViews();
        initData();
    }

    protected void initTitle() {
        // 设置沉浸式状态栏
        AppBarHelper.with(this).setStatusBarStyle(Style.TRANSPARENT).apply();
        // 初始化视图
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setAdjustToTransparentStatusBar(true);
        // 添加返回按钮
        mToolbar.addLeftIcon(TAG_TOOLBAR_BACK, R.drawable.libpicturepicker_common_arrow_right_white,
                this);
        // 添加选中详情的文本
        mToolbar.addLeftText(TAG_TOOLBAR_CHECKED_DETAIL, getString(
                R.string.libpicturepicker_picturepicker_all_picture), 20, null);
        mTvToolbarFolderName = mToolbar.getViewByTag(TAG_TOOLBAR_CHECKED_DETAIL);
        // 添加图片确认按钮
        mToolbar.addRightText(TAG_TOOLBAR_ENSURE, getString(
                R.string.libpicturepicker_picturepicker_ensure), 15, this);
        mTvToolbarEnsure = mToolbar.getViewByTag(TAG_TOOLBAR_ENSURE);
    }

    protected void initViews() {
        // RecyclerView
        mRvPictures = findViewById(R.id.rv_pictures);

        // 底部菜单控制区域
        mTvSelectedFolderName = findViewById(R.id.tv_folder_name);
        mTvSelectedFolderName.setOnClickListener(this);
        mTvPreview = findViewById(R.id.tv_preview);
        mTvPreview.setOnClickListener(this);
        mRvFolders = findViewById(R.id.rv_folders);
        mRvFolders.setLayoutManager(new LinearLayoutManager(this));
        mRvFolders.setHasFixedSize(true);
        mBottomMenuBehavior = BottomSheetBehavior.from(findViewById(R.id.ll_bottom_menu));

        // 悬浮按钮
        mFab = findViewById(R.id.fab);
        mFab.setVisibility(View.GONE);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) mFab.getLayoutParams();
        params.setBehavior(new PicturePickerFabBehavior());
        mFab.setLayoutParams(params);
        mFab.setOnClickListener(this);
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
    public void setBackgroundColor(int color) {
        mRvPictures.setBackgroundColor(color);
    }

    @Override
    public void setSpanCount(int spanCount) {
        mRvPictures.setLayoutManager(new GridLayoutManager(this, spanCount));
    }

    @Override
    public void setPicturesAdapter(PickerConfig config, ArrayList<String> displayPaths, ArrayList<String> userPickedPaths) {
        mRvPictures.setAdapter(new PictureAdapter(this, config,
                displayPaths, userPickedPaths));
    }

    @Override
    public void setFolderAdapter(ArrayList<PictureFolder> allFolders) {
        mRvFolders.setAdapter(new FolderAdapter(this, allFolders));
    }

    @Override
    public void setFabColor(int color) {
        mFab.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    @Override
    public void switchFabVisibility(boolean isVisible) {
        mFab.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setPictureFolderText(String folderName) {
        // 更新文件夹名称
        mTvSelectedFolderName.setText(folderName);
        mTvToolbarFolderName.setText(folderName);
        mRvPictures.getAdapter().notifyDataSetChanged();
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
        mRvPictures.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void notifyDisplayPathsChanged() {
        mRvPictures.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void notifyDisplayPathsInsertToFirst() {
        mRvPictures.getAdapter().notifyItemInserted(1);
    }

    @Override
    public void notifyFolderDataSetChanged() {
        mRvFolders.getAdapter().notifyDataSetChanged();
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
        } else if (v == mToolbar.getViewByTag(TAG_TOOLBAR_BACK)) {// 返回按钮
            onBackPressed();
        } else if (v == mToolbar.getViewByTag(TAG_TOOLBAR_ENSURE) || v.getId() == R.id.fab) {// 确认按钮
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

}
