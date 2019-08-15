package com.wyq.base.sign;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wyq.base.BaseActivity;
import com.wyq.base.BuildConfig;
import com.wyq.base.R;
import com.wyq.base.sign.config.PenConfig;
import com.wyq.base.sign.util.BitmapUtil;
import com.wyq.base.sign.util.DisplayUtil;
import com.wyq.base.sign.util.StatusBarCompat;
import com.wyq.base.sign.util.SystemUtil;
import com.wyq.base.sign.view.PaintSettingWindow;
import com.wyq.base.sign.view.PaintView;
import com.wyq.base.util.ToastUtil;
import com.wyq.base.view.BaseDialog;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * 空白手写画板
 */
@RuntimePermissions
public class SignActivity extends BaseActivity implements View.OnClickListener, PaintView.StepCallback {

    public static final int REQUEST_SIGN = 1001;
    public static final int REQUEST_SIGN_UNIT = 1002;
    public static final int REQUEST_SIGN_ENFORCE = 1003;
    public static final int REQUEST_SIGN_WITNESS = 1004;

    public static final int CANVAS_MAX_WIDTH = 3000; // 画布最大宽度
    public static final int CANVAS_MAX_HEIGHT = 3000; // 画布最大高度

    private static final int saveImageWidth = 200;

    private View mContainerView;
    private CardView mClearView;
    private ImageView mClearIv;

    private PaintView mPaintView;

    private ProgressDialog mSaveProgressDlg;
    private static final int MSG_SAVE_SUCCESS = 1;
    private static final int MSG_SAVE_FAILED = 2;

    private String mSavePath;
    private boolean hasSize = false;

    private float mWidth;
    private float mHeight;
    private float widthRate = 1.0f;
    private float heightRate = 1.0f;
    private int bgColor;
    private boolean isCrop;
    private String format;

    private PaintSettingWindow settingWindow;
    private String mInitPath;

    public static void startActivityForResult(Activity activity) {
        startActivityForResult(activity, REQUEST_SIGN);
    }

    public static void startActivityForResult(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, SignActivity.class);
        intent.putExtra("format", PenConfig.FORMAT_PNG);
        intent.putExtra("crop", false);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void initData(@NotNull Intent intent) {
        isCrop = intent.getBooleanExtra("crop", false);
        format = intent.getStringExtra("format");
        bgColor = intent.getIntExtra("background", Color.WHITE);
        mInitPath = intent.getStringExtra("image");
        float bitmapWidth = intent.getFloatExtra("width", 1.0f);
        float bitmapHeight = intent.getFloatExtra("height", 1.0f);

        if (bitmapWidth > 0 && bitmapWidth <= 1.0f) {
            widthRate = bitmapWidth;
            mWidth = getResizeWidth();
        } else {
            hasSize = true;
            mWidth = bitmapWidth;
        }
        if (bitmapHeight > 0 && bitmapHeight <= 1.0f) {
            heightRate = bitmapHeight;
            mHeight = getResizeHeight();
        } else {
            hasSize = true;
            mHeight = bitmapHeight;
        }
        if (mWidth > CANVAS_MAX_WIDTH) {
            ToastUtil.shortToast(this, "画板宽度已超过" + CANVAS_MAX_WIDTH);
            finish();
        }
        if (mHeight > CANVAS_MAX_WIDTH) {
            ToastUtil.shortToast(this, "画板高度已超过" + CANVAS_MAX_WIDTH);
            finish();
        }
    }

    @Override
    protected void initViews() {
        TextView titleTv = getTitleTv();
        if (titleTv != null) {
            titleTv.setText(getString(R.string.sign_title));
        }
        TextView rightTv = getRightTv();
        if (rightTv != null) {
            rightTv.setVisibility(View.VISIBLE);
            rightTv.setText(getString(R.string.button_complete));
            rightTv.setOnClickListener(this);
        }

        mContainerView = findViewById(R.id.container);
        mPaintView = findViewById(R.id.paintView);
        mClearView = findViewById(R.id.clearBtn);
        mClearIv = findViewById(R.id.clearIv);

        mClearView.setEnabled(!mPaintView.isEmpty());
        mClearView.setOnClickListener(this);

        mPaintView.setBackgroundColor(Color.WHITE);
        mPaintView.setStepCallback(this);

        PenConfig.PAINT_SIZE_LEVEL = PenConfig.getPaintTextLevel(this);
        PenConfig.PAINT_COLOR = PenConfig.getPaintColor(this);

        Drawable drawable = DrawableCompat.wrap(Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.base_ic_delete_black_24dp)));
        if (!mPaintView.isEmpty()) {
            DrawableCompat.setTint(drawable, ContextCompat.getColor(this, R.color.btn_common_normal));
        } else {
            DrawableCompat.setTint(drawable, ContextCompat.getColor(this, R.color.btn_common_disable));
        }
        mClearIv.setImageDrawable(drawable);

        //初始画板设置
        if (!hasSize && !TextUtils.isEmpty(mInitPath)) {
            Bitmap bitmap = BitmapFactory.decodeFile(mInitPath);
            mWidth = bitmap.getWidth();
            mHeight = bitmap.getHeight();
            hasSize = true;
            if (mWidth > CANVAS_MAX_WIDTH || mHeight > CANVAS_MAX_HEIGHT) {
                bitmap = BitmapUtil.zoomImg(bitmap, CANVAS_MAX_WIDTH, CANVAS_MAX_WIDTH);
                mWidth = bitmap.getWidth();
                mHeight = bitmap.getHeight();
            }
        }
        mPaintView.init((int) mWidth, (int) mHeight, mInitPath);
        if (bgColor != Color.TRANSPARENT) {
            mPaintView.setBackgroundColor(bgColor);
        }
        //显示隐藏拖拽按钮
        hideShowDrag();
    }

    /**
     * 获取画布默认宽度
     *
     * @return
     */
    private int getResizeWidth() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && dm.widthPixels < dm.heightPixels) {
            return (int) (dm.heightPixels * widthRate);
        }
        return (int) (dm.widthPixels * widthRate);
    }

    /**
     * 获取画布默认高度
     *
     * @return
     */
    private int getResizeHeight() {
        int actionbarHeight = getResources().getDimensionPixelSize(R.dimen.dp_45);
        int statusBarHeight = StatusBarCompat.getStatusBarHeight(this);
        int otherHeight = actionbarHeight + statusBarHeight;
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && dm.widthPixels < dm.heightPixels) {
            return (int) ((dm.widthPixels - otherHeight) * heightRate);
        }
        return (int) ((dm.heightPixels - otherHeight) * heightRate);
    }

    /**
     * 横竖屏切换
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (settingWindow != null) {
            settingWindow.dismiss();
        }

        int resizeWidth = getResizeWidth();
        int resizeHeight = getResizeHeight();
        if (mPaintView != null && !hasSize) {
            mPaintView.resize(mPaintView.getLastBitmap(), resizeWidth, resizeHeight);
        }
        hideShowDrag();
    }

    /**
     * 控制防误触按钮是否显示
     */
    private void hideShowDrag() {
        ViewTreeObserver vto2 = mContainerView.getViewTreeObserver();
        vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mContainerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                //显示操作指引
                if (PenConfig.getFirst(SignActivity.this)) {
                    showGuideView();
                    PenConfig.setFirst(SignActivity.this, false);
                }

                int limitWidth = mContainerView.getWidth() - DisplayUtil.dip2px(SignActivity.this, 20);
                int limitHeight = mContainerView.getHeight() - DisplayUtil.dip2px(SignActivity.this, 20);
                if (mPaintView.getBitmap().getWidth() > limitWidth || mPaintView.getBitmap().getHeight() > limitHeight) {
//					mHandView.setVisibility(View.VISIBLE);
                } else {
                    //根据是否平板来显示防误触按钮
//					mHandView.setVisibility(SystemUtil.isTablet(SignActivity.this) ? View.VISIBLE : View.GONE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.clearBtn) {
            mPaintView.reset();
        } else if (i == R.id.rightTv) {
            if (!mPaintView.isEmpty()) {
                SignActivityPermissionsDispatcher.saveWithPermissionCheck(this);
            } else {
                setResult(RESULT_CANCELED);
                finish();
            }
        } else if (i == R.id.backBtn) {
            if (!mPaintView.isEmpty()) {
                showQuitTip();
            } else {
                setResult(RESULT_CANCELED);
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mPaintView != null) {
            mPaintView.release();
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }


    /**
     * 弹出画笔设置
     */
    private void showPaintSettingWindow() {
        settingWindow = new PaintSettingWindow(this);
        settingWindow.setSettingListener(new PaintSettingWindow.OnSettingListener() {
            @Override
            public void onColorSetting(int color) {
                mPaintView.setPaintColor(color);
//				mSettingView.setPaintColor(color);
            }

            @Override
            public void onSizeSetting(int index) {
//				mSettingView.setRadiusLevel(index);
                mPaintView.setPaintWidth(PaintSettingWindow.PEN_SIZES[index]);
            }
        });

        View contentView = settingWindow.getContentView();
        //需要先测量，PopupWindow还未弹出时，宽高为0
        contentView.measure(SystemUtil.makeDropDownMeasureSpec(settingWindow.getWidth()),
                SystemUtil.makeDropDownMeasureSpec(settingWindow.getHeight()));

        int padding = DisplayUtil.dip2px(this, 10);
        settingWindow.popAtBottomRight();
//		settingWindow.showAsDropDown(mSettingView, mSettingView.getWidth() - settingWindow.getContentView().getMeasuredWidth() + 2 * padding, 10);
    }


    private void initSaveProgressDlg() {
        mSaveProgressDlg = new ProgressDialog(this);
        mSaveProgressDlg.setMessage("正在保存,请稍候...");
        mSaveProgressDlg.setCancelable(false);
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SAVE_FAILED:
                    mSaveProgressDlg.dismiss();
                    Toast.makeText(getApplicationContext(), "保存失败", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_SAVE_SUCCESS:
                    mSaveProgressDlg.dismiss();
                    Intent intent = new Intent();
                    intent.putExtra(PenConfig.SAVE_PATH, mSavePath);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void save() {
        if (mPaintView.isEmpty()) {
            Toast.makeText(getApplicationContext(), "没有写入任何文字", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mSaveProgressDlg == null) {
            initSaveProgressDlg();
        }
        mSaveProgressDlg.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap result = mPaintView.buildAreaBitmap(isCrop);
                    if (PenConfig.FORMAT_JPG.equals(format) && bgColor == Color.TRANSPARENT) {
                        bgColor = Color.WHITE;
                    }
                    if (bgColor != Color.TRANSPARENT) {
                        result = BitmapUtil.drawBgToBitmap(result, bgColor);
                    }
                    if (result == null) {
                        mHandler.obtainMessage(MSG_SAVE_FAILED).sendToTarget();
                        return;
                    }
                    mSavePath = BitmapUtil.saveImage(SignActivity.this, BitmapUtil.zoomImg(result, saveImageWidth), 100, format);
                    if (mSavePath != null) {
                        mHandler.obtainMessage(MSG_SAVE_SUCCESS).sendToTarget();
                    } else {
                        mHandler.obtainMessage(MSG_SAVE_FAILED).sendToTarget();
                    }
                } catch (Exception e) {}
            }
        }).start();
    }

    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showRationaleForScan(final PermissionRequest request) {
        new BaseDialog.Builder(this).setMessage(R.string.base_permission_rationale)
                .setCancelable(false)
                .setNegativeButton(R.string.button_deny, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.cancel();
                    }
                })
                .setPositiveButton(R.string.button_allow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.proceed();
                    }
                }).create().show();
    }

    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showDeniedForScan() {
        ToastUtil.shortToast(this, R.string.base_permission_denied);
    }

    @OnNeverAskAgain(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showNeverAskForScan() {
        new BaseDialog.Builder(this)
                .setMessage(R.string.base_permission_setting)
                .setCancelable(false)
                .setNegativeButton(R.string.button_cancel, null)
                .setPositiveButton(R.string.button_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                }).create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        SignActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    /**
     * 画布有操作
     */
    @Override
    public void onOperateStatusChanged() {
//		mUndoView.setEnabled(mPaintView.canUndo());
//		mRedoView.setEnabled(mPaintView.canRedo());
        mClearView.setEnabled(!mPaintView.isEmpty());

//		mRedoView.setImage(R.drawable.sign_ic_redo, mPaintView.canRedo() ? PenConfig.THEME_COLOR : Color.LTGRAY);
//		mUndoView.setImage(R.drawable.sign_ic_undo, mPaintView.canUndo() ? PenConfig.THEME_COLOR : Color.LTGRAY);
        Drawable drawable = DrawableCompat.wrap(Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.base_ic_delete_black_24dp)));
        if (!mPaintView.isEmpty()) {
            DrawableCompat.setTint(drawable, ContextCompat.getColor(this, R.color.btn_common_normal));
        } else {
            DrawableCompat.setTint(drawable, ContextCompat.getColor(this, R.color.btn_common_disable));
        }
        mClearIv.setImageDrawable(drawable);
//		mClearView.setImage(R.drawable.sign_ic_clear, !mPaintView.isNull() ? PenConfig.THEME_COLOR : Color.LTGRAY);
    }

    /**
     * 显示操作指引
     */
    private void showGuideView() {
        View viewParent = getWindow().getDecorView();
        if (viewParent == null)
            return;
//		GuideView guideView = new GuideView(this, viewParent, mHandView, mPenView);
//		guideView.show();
    }

    @Override
    public void onBackPressed() {
        if (!mPaintView.isEmpty()) {
            showQuitTip();
        } else {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    /**
     * 弹出退出提示
     */
    private void showQuitTip() {
        new BaseDialog.Builder(this)
                .setTitle("提示")
                .setMessage("当前文字未保存，是否退出？")
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                }).create().show();
    }

    @Override
    protected int initLayout() {
        return R.layout.base_act_sign;
    }

    @Override
    protected boolean overStatusBar() {
        return false;
    }
}
