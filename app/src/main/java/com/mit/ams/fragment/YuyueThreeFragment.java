package com.mit.ams.fragment;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.mit.ams.R;
import com.mit.ams.common.Constants;
import com.mit.ams.utils.IpTimeStamp;
import com.mit.ams.utils.PictureUtil;
import com.mit.ams.utils.UploadUtil;

import org.xutils.common.Callback;
import org.xutils.common.util.KeyValue;
import org.xutils.http.RequestParams;
import org.xutils.http.body.MultipartBody;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * description: 定损第二步-上传照片
 * autour: 刘鹏飞
 * date: 17.7.20 14:56
 * update: 17.7.20
 * version:
 */
public class YuyueThreeFragment extends Fragment {

    private String TAG = YuyueThreeFragment.class.getSimpleName();

    private AppCompatActivity activity;
    private View view;
    private ActionBar actionBar;
    private TextView actionbarTitle;

    private ProgressDialog progressDialog;

    private Button submitButton, takePhoto, pickPhoto;
    private String damageId;
    private ImageView img1, img2, img3, img4, img5, img6;

    protected static final int TO_UPLOAD_FILE = 1;
    protected static final int UPLOAD_FILE_DONE = 2;
    private static final int UPLOAD_INIT_PROCESS = 4;
    private static final int UPLOAD_IN_PROCESS = 5;
    private String pathImage, factoryId;
    private HashMap<Integer, String> filePathMap = new HashMap<>();
    private int selectPosition;

    /**
     * 获取到的图片路径
     */
    private Uri photoUri;
    public static final int SELECT_PIC_BY_TACK_PHOTO = 1;
    public static final int SELECT_PIC_BY_PICK_PHOTO = 2;
    private PopupWindow popupWindow;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_auth_two, container, false);
        activity = (AppCompatActivity) this.getActivity();
        damageId = getArguments().getString("damageId");
        factoryId = getArguments().getString("FACTORY_ID");
        initView();
        return view;
    }

    private void initView() {
        //把标题栏改为登陆
        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setCustomView(R.layout.text_titlebar);
        actionbarTitle = (TextView) actionBar.getCustomView().findViewById(R.id.actionbar_title);
        actionbarTitle.setText("上传照片");
        actionBar.setDisplayHomeAsUpEnabled(true);// 显示返回按钮
        setHasOptionsMenu(true);//这个需要，不然onOptionsItemSelected方法不会被调用

        //页面空间初始化
        img1 = (ImageView) view.findViewById(R.id.img1);
        img2 = (ImageView) view.findViewById(R.id.img2);
        img3 = (ImageView) view.findViewById(R.id.img3);
        img4 = (ImageView) view.findViewById(R.id.img4);
        img5 = (ImageView) view.findViewById(R.id.img5);
        img6 = (ImageView) view.findViewById(R.id.img6);
        submitButton = (Button) view.findViewById(R.id.submit_btn);

        img1.setOnClickListener(clickListener);
        img2.setOnClickListener(clickListener);
        img3.setOnClickListener(clickListener);
        img4.setOnClickListener(clickListener);
        img5.setOnClickListener(clickListener);
        img6.setOnClickListener(clickListener);
        submitButton.setOnClickListener(clickListener);
    }

    /**
     * 监听相机点击事件
     */
    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_take_photo:
                    takePhoto();
                    break;
                case R.id.btn_pick_photo:
                    pickPhoto();
                    break;
                case R.id.img1:
                    onItemClick(1);
                    break;
                case R.id.img2:
                    onItemClick(2);
                    break;
                case R.id.img3:
                    onItemClick(3);
                    break;
                case R.id.img4:
                    onItemClick(4);
                    break;
                case R.id.img5:
                    onItemClick(5);
                    break;
                case R.id.img6:
                    onItemClick(6);
                    break;
                case R.id.submit_btn:
                    if(null == fileList || fileList.size() ==0){
                        showToast("请至少上传一张您的车辆故障图片");
                        break;
                    }
                    progressDialog = ProgressDialog.show(activity, "提示", "正在上传...");
                    handler.sendEmptyMessage(TO_UPLOAD_FILE);
                    break;

                default:
                    break;
            }
        }
    };

    /**
     * 拍照获取图片
     */
    private void takePhoto() {
        //执行拍照前，应该先判断SD卡是否存在
        String SDState = Environment.getExternalStorageState();
        if (SDState.equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//"android.media.action.IMAGE_CAPTURE"
            /***
             * 需要说明一下，以下操作使用照相机拍照，拍照后的图片会存放在相册中的
             * 这里使用的这种方式有一个好处就是获取的图片是拍照后的原图
             * 如果不实用ContentValues存放照片路径的话，拍照后获取的图片为缩略图不清晰
             */
            ContentValues values = new ContentValues();
            photoUri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            /**-----------------*/
            startActivityForResult(intent, SELECT_PIC_BY_TACK_PHOTO);
        } else {
            showToast("内存卡不存在");
        }
    }

    /***
     * 从相册中取图片
     */
    private void pickPhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(intent, SELECT_PIC_BY_PICK_PHOTO);
    }

    /**
     * 选择图片后，获取图片的路径
     *
     * @param requestCode
     * @param data
     */
    private void doPhoto(int requestCode, Intent data) {
        if (requestCode == SELECT_PIC_BY_PICK_PHOTO)  //从相册取图片，有些手机有异常情况，请注意
        {
            if (data == null) {
                showToast("选择图片文件出错");
                return;
            }
            photoUri = data.getData();
            if (photoUri == null) {
                showToast("选择图片文件出错");
                return;
            }
        }
        String[] pojo = {MediaStore.Images.Media.DATA};
        Cursor cursor = activity.getContentResolver().query(photoUri, pojo, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(pojo[0]);
            cursor.moveToFirst();
            pathImage = cursor.getString(columnIndex);
            cursor.close();
        }
        Log.i(TAG, "imagePath = " + pathImage);
    }

    /**
     * 点击图片调用图库
     */
    public void onItemClick(int position) {
        selectPosition = position;
        // 一个自定义的布局，显示拍照或者是从相册选择
        View contentView = LayoutInflater.from(activity).inflate(R.layout.select_pic, null);
        popupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        //实例化一个ColorDrawable颜色为半透明，已达到变暗的效果
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
        popupWindow.setBackgroundDrawable(dw);
        // 设置好参数之后再show
        popupWindow.showAtLocation(view, Gravity.BOTTOM, 10, 10);

        takePhoto = (Button) contentView.findViewById(R.id.btn_take_photo);
        pickPhoto = (Button) contentView.findViewById(R.id.btn_pick_photo);
        takePhoto.setOnClickListener(clickListener);
        pickPhoto.setOnClickListener(clickListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null != popupWindow) {
            popupWindow.dismiss();
        }
        try {
            if (resultCode == activity.RESULT_OK) {
                doPhoto(requestCode, data);
                Log.i(TAG, "最终选择的图片=" + pathImage);
                Bitmap smallBitmap = PictureUtil.getSmallBitmap(pathImage, 268, 172);//压缩图片
                Bitmap tempBitmap = PictureUtil.getMyImage(pathImage);//把原图压缩为640*960
                IpTimeStamp its = new IpTimeStamp("172.168.3.222");
                File dir = new File(Environment.getExternalStorageDirectory() + "/xiucheba/");
                if (!dir.exists() || !dir.isDirectory()) {
                    dir.mkdirs();
                }
                String path = Environment.getExternalStorageDirectory() + "/xiucheba/" + its.getIpTimeRand() + ".JPG";
                Log.d(TAG, "存储图片到" + path);
                PictureUtil.storeImage(tempBitmap, path);
                fileList.add(new File(path));
                widthHeightList.append(tempBitmap.getWidth() + "_" + tempBitmap.getHeight()).append(",");
                filePathMap.put(selectPosition, pathImage);
                for (int i = 1; i < 7; i++) {
                    if (selectPosition == 1) {
                        img1.setImageBitmap(smallBitmap);
                    }
                    if (selectPosition == 2) {
                        img2.setImageBitmap(smallBitmap);
                    }
                    if (selectPosition == 3) {
                        img3.setImageBitmap(smallBitmap);
                    }
                    if (selectPosition == 4) {
                        img4.setImageBitmap(smallBitmap);
                    }
                    if (selectPosition == 5) {
                        img5.setImageBitmap(smallBitmap);
                    }
                    if (selectPosition == 6) {
                        img6.setImageBitmap(smallBitmap);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "选取图片出错");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    List<File> fileList = new ArrayList<>();
    StringBuffer widthHeightList = new StringBuffer();

    private void toUploadFile() {
        RequestParams params = new RequestParams(Constants.IMG_URL);

        List<KeyValue> list = new ArrayList<>();
        for (File file : fileList) {
            list.add(new KeyValue(String.valueOf(selectPosition), file));
        }

        MultipartBody body = new MultipartBody(list, "UTF-8");
        params.setRequestBody(body);
        params.setMultipart(true);
        params.addParameter("damageId", damageId);
        params.addParameter("widthHeight", widthHeightList.toString());
        x.http().post(params, new Callback.CacheCallback<String>() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onSuccess(String s) {
                try {
                    Log.d(TAG, s);
                    JSONObject obj = JSONObject.parseObject(s);
                    boolean result = obj.getBoolean("success");
                    if(result){
                        progressDialog.dismiss();
                        showToast("预约成功");
                        //跳转到用户中心
                        FragmentManager manager = activity.getSupportFragmentManager();
                        FragmentTransaction transaction = manager.beginTransaction();
                        transaction.replace(R.id.content_fragment, new RepairFragment());
                        transaction.commit();
                    } else {
                        progressDialog.dismiss();
                        showToast("上传失败，请重试");
                    }
                } catch (Exception e) {
                    progressDialog.dismiss();
                    showToast("上传失败，请重试");
                    Log.e(TAG, e.toString());
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                Log.e(TAG, throwable.toString());
                Toast.makeText(activity, "错误:" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }

            @Override
            public boolean onCache(String s) {
                return false;
            }
        });
    }

//    private void toUploadFile() {
//        progressDialog = ProgressDialog.show(activity, "提示", "正在上传...");
//        final String fileKey = "upload";
//        final UploadUtil uploadUtil = UploadUtil.getInstance();
//        uploadUtil.setOnUploadProcessListener(new UploadUtil.OnUploadProcessListener() {
//
//            @Override
//            public void onUploadProcess(int uploadSize) {
//                Message msg = Message.obtain();
//                msg.what = UPLOAD_IN_PROCESS;
//                msg.arg1 = uploadSize;
//                handler.sendMessage(msg);
//            }
//
//            @Override
//            public void onUploadDone(int responseCode, String message) {
//
//                progressDialog.dismiss();
//                Message msg = Message.obtain();
//                msg.what = UPLOAD_FILE_DONE;
//                msg.arg1 = responseCode;
//                msg.obj = message;
//                handler.sendMessage(msg);
//            }
//
//            @Override
//            public void initUpload(int fileSize) {
//                Message msg = Message.obtain();
//                msg.what = UPLOAD_INIT_PROCESS;
//                msg.arg1 = fileSize;
//                handler.sendMessage(msg);
//            }
//        });  //设置监听器监听上传状态
//        final Map<String, String> params = new HashMap<>();
//        params.put("dpnumber", "13800001111");
//        System.out.println(filePathMap.size());
//        new Thread(new Runnable() {
//            int i = 0;
//
//            @Override
//            public void run() {
//                final boolean uploadFile = uploadUtil.uploadFile(filePathMap, fileKey, Constants.IMG_URL, params);
//                activity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (uploadFile) {
//                            showToast("上传成功");
//                            progressDialog.dismiss();
//                        } else {
//                            showToast("上传失败");
//                            progressDialog.dismiss();
//                        }
//                    }
//                });
//            }
//        }).start();
//    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TO_UPLOAD_FILE:
                    toUploadFile();
                    break;
                case UPLOAD_INIT_PROCESS:
                    break;
                case UPLOAD_IN_PROCESS:
                    break;
                case UPLOAD_FILE_DONE:
                    String result = "响应码：" + msg.arg1 + "\n响应信息：" + msg.obj + "\n耗时：" + UploadUtil.getRequestTime() + "秒";

                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:   //返回键的id
                FragmentManager manager = activity.getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.content_fragment, new DamageFragment());
                transaction.commit();
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showToast(String msg) {
        Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
    }

}
