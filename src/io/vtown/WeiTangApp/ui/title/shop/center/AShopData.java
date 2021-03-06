package io.vtown.WeiTangApp.ui.title.shop.center;

import io.vtown.WeiTangApp.R;
import io.vtown.WeiTangApp.bean.bcache.BShop;
import io.vtown.WeiTangApp.bean.bcomment.BComment;
import io.vtown.WeiTangApp.bean.bcomment.BLComment;
import io.vtown.WeiTangApp.bean.bcomment.BUser;
import io.vtown.WeiTangApp.bean.bcomment.new_three.BNewHome;
import io.vtown.WeiTangApp.bean.bcomment.news.BMessage;
import io.vtown.WeiTangApp.bean.bcomment.news.BNew;
import io.vtown.WeiTangApp.comment.contant.CacheUtil;
import io.vtown.WeiTangApp.comment.contant.Constants;
import io.vtown.WeiTangApp.comment.contant.LogUtils;
import io.vtown.WeiTangApp.comment.contant.PromptManager;
import io.vtown.WeiTangApp.comment.contant.Spuit;
import io.vtown.WeiTangApp.comment.net.NHttpBaseStr;
import io.vtown.WeiTangApp.comment.net.qiniu.NUPLoadUtil;
import io.vtown.WeiTangApp.comment.net.qiniu.NUpLoadUtils;
import io.vtown.WeiTangApp.comment.net.qiniu.NUpLoadUtils.UpResult;
import io.vtown.WeiTangApp.comment.util.QRCodeUtil;
import io.vtown.WeiTangApp.comment.util.SdCardUtils;
import io.vtown.WeiTangApp.comment.util.StrUtils;
import io.vtown.WeiTangApp.comment.util.image.ImageLoaderUtil;
import io.vtown.WeiTangApp.comment.view.CircleImageView;
import io.vtown.WeiTangApp.comment.view.dialog.ImagViewDialog;
import io.vtown.WeiTangApp.event.interf.IDialogResult;
import io.vtown.WeiTangApp.event.interf.IHttpResult;
import io.vtown.WeiTangApp.ui.ATitleBase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.Request.Method;

import de.greenrobot.event.EventBus;
import io.vtown.WeiTangApp.ui.comment.AWeb;
import io.vtown.WeiTangApp.ui.title.center.set.AAccountSafe;
import io.vtown.WeiTangApp.ui.title.center.set.ARealIdauthSucceed;
import io.vtown.WeiTangApp.ui.title.loginregist.ARealIdauth;

/**
 * @author 作者 易惠华 yihuihua@v-town.cc
 * @version 创建时间：2016-5-25 上午10:07:24 店铺资料页面
 */
public class AShopData extends ATitleBase implements OnLongClickListener {

    /**
     * 头像
     */
    private CircleImageView tab_shop_iv;
    /**
     * 店铺ID
     */
    private TextView tv_shop_id;

    /**
     * 店铺ID
     */
    private LinearLayout ll_shop_id;
    /**
     * 店铺封面
     */
    private View shop_cover;
    /**
     * 店铺昵称
     */
    private View shop_another_name;
    /**
     * 店铺规则
     */
    private View shop_guize;
    /**
     * 店铺介绍
     */
    private View shop_introduce;
    private TextView tv_conment1;
    private TextView tv_conment2;

   // private TextView shop_data_name;

    /**
     * 实名认证
     */
    private View authentication;
    /**
     * 分享
     */

    /**
     * 电话号码
     */
    private TextView tv_phone_numb;

    /**
     * 用户信息
     */
    private BUser user_Get;

    /**
     * 账户安全
     */
    private View account_safe;
//    private BShop uBShop;

    /**
     * 存放图片和视频的集合
     */
//	private List<BLComment> ResourcesList = new ArrayList<BLComment>();

    // 文件路径
    private String path = "";

    private static final int RESULT_CODE = 111;

    private int show_type = 0;

    private ClipData myClip;

    /**
     * 文本操作管理
     */
    private ClipboardManager myClipboard;

    // 二维码是否点击
    private boolean QrClick = true;
    private Bitmap qrBitmap;
    private float QrscaleWidth;
    private float QrscaleHeight;

    /**
     * 认证状态
     */
    private TextView comment_txtarrow_content;

    /**
     * 是否认证
     */
    private boolean isLogin_RenZheng_Set;
    private View myView;
    private BNewHome MBNewHome;
    private boolean IsAvater=false;


    @Override
    protected void InItBaseView() {

        setContentView(R.layout.activity_shop_center_shop_data);
        myView = LayoutInflater.from(BaseContext).inflate(R.layout.activity_shop_center_shop_data, null);
        myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        user_Get = Spuit.User_Get(getApplicationContext());
//        uBShop = Spuit.Shop_Get(getApplicationContext());
        MBNewHome = JSON.parseObject(CacheUtil.NewHome_Get(BaseContext), BNewHome.class);
        IView();
        IData();
        ShowView(MBNewHome );
    }

    private void IData() {
        BUser user_Get = Spuit.User_Get(getApplicationContext());
        String phone = user_Get.getPhone();
        if (!StrUtils.isEmpty(phone)) {
            StrUtils.SetTxt(tv_phone_numb, phone.substring(0, 3) + "****"
                    + phone.substring(7));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        isLogin_RenZheng_Set = Spuit.IsLogin_RenZheng_Set(BaseContext);
        if (isLogin_RenZheng_Set) {
            StrUtils.SetTxt(comment_txtarrow_content, "已认证");

        } else {
            StrUtils.SetTxt(comment_txtarrow_content, "未认证");
        }
    }

    private void ShowView(BNewHome shop_Get) {

        //StrUtils.SetTxt(shop_data_name, MBNewHome.getSellerinfo().getSeller_name());
        ImageLoaderUtil.Load2(shop_Get.getSellerinfo().getAvatar(), tab_shop_iv,
                R.drawable.testiv);
        StrUtils.SetTxt(tv_shop_id, MBNewHome.getSellerinfo().getSeller_no());
        SetItemContent(shop_another_name, R.string.shop_another_name,
                shop_Get.getSellerinfo().getSeller_name(), 1);
        SetItemContent(
                shop_introduce,
                R.string.shop_introduce,
                StrUtils.isEmpty(shop_Get.getSellerinfo().getIntro()) ? "您还未填写个性签名" : shop_Get.getSellerinfo()
                        .getIntro(), 2);
    }

    private void IView() {
        //shop_data_name = (TextView) findViewById(R.id.shop_data_name);

        tab_shop_iv = (CircleImageView) findViewById(R.id.tab_shop_iv);


        tv_phone_numb = (TextView) findViewById(R.id.tv_phone_numb);
        authentication = findViewById(R.id.authentication);
        comment_txtarrow_content = (TextView) authentication
                .findViewById(R.id.comment_txtarrow_content);
        account_safe = findViewById(R.id.account_safe);

        tv_shop_id = (TextView) findViewById(R.id.tv_shop_id);
        shop_cover = findViewById(R.id.shop_cover);
        shop_another_name = findViewById(R.id.shop_another_name);
        shop_introduce = findViewById(R.id.shop_introduce);
        ll_set_icon = (LinearLayout) findViewById(R.id.ll_set_icon);
        ll_shop_id = (LinearLayout) findViewById(R.id.ll_shop_id);
        ll_set_icon.setOnClickListener(this);
        ll_shop_id.setOnLongClickListener(this);

        shop_guize = findViewById(R.id.shop_guize);
        SetItemContent(shop_cover, R.string.shop_cover, "", 0);
        SetItemContent(shop_guize, R.string.contact_dengjiguize, "", -1);
        SetItemContent(authentication, R.string.authentication, "未认证",1);
        SetItemContent(account_safe, R.string.account_safe, "",0);




    }



    private void SetItemContent(View VV, int ResourceTitle,
                                String ResourceRight, int type) {
        ((TextView) VV.findViewById(R.id.comment_txtarrow_title))
                .setText(getResources().getString(ResourceTitle));
        if (!StrUtils.isEmpty(ResourceRight)) {
            switch (type) {
                case 1:
                    tv_conment1 = ((TextView) VV
                            .findViewById(R.id.comment_txtarrow_content));
                    tv_conment1.setText(ResourceRight);
                    break;

                case 2:
                    tv_conment2 = ((TextView) VV
                            .findViewById(R.id.comment_txtarrow_content));
                    StrUtils.SetTxt(tv_conment2, ResourceRight);
                    break;

                default:
                    break;
            }

        }
        VV.setOnClickListener(this);
    }

    @Override
    protected void InitTile() {
        SetTitleTxt(getString(R.string.shop_data));
    }

    @Override
    protected void DataResult(int Code, String Msg, BComment Data) {

    }

    @Override
    protected void DataError(String error, int LoadTyp) {
    }

    @Override
    protected void NetConnect() {
        NetError.setVisibility(View.GONE);

    }

    @Override
    protected void NetDisConnect() {
        NetError.setVisibility(View.VISIBLE);

    }

    @Override
    protected void SetNetView() {
        SetNetStatuse(NetError);

    }

    @Override
    protected void MyClick(View V) {

        switch (V.getId()) {
            case R.id.shop_guize://等级规则
                PromptManager.SkipActivity(BaseActivity, new Intent(
                        BaseActivity, AWeb.class).putExtra(
                        AWeb.Key_Bean,
                        new BComment(Constants.Home_Level, getResources().getString(R.string.dengjiguize))));
                break;
            case R.id.ll_set_icon:
                show_type = 1;
                DialogTest(1);
                break;

            case R.id.shop_cover:
                show_type = 2;
                DialogTest(2);
                break;

            case R.id.shop_another_name:

                Intent intent = new Intent(BaseActivity, AShopDataEdit.class);
                String content = tv_conment1.getText().toString().trim();
                if (!StrUtils.isEmpty(content)) {
                    intent.putExtra("content", content);
                } else {
                    intent.putExtra("content", "");
                }
                intent.putExtra("type", 1);
                intent.putExtra("seller_id", user_Get.getSeller_id());// user_Get.getSeller_no()
                PromptManager.SkipResultActivity(BaseActivity, intent, 100);
                break;

            case R.id.shop_introduce:
                Intent intent1 = new Intent(BaseActivity, AShopDataEdit.class);
                String content1 = tv_conment2.getText().toString().trim();
                // if (!StrUtils.isEmpty(content1)) {
                // intent1.putExtra("content", content1);
                // }
                intent1.putExtra("content", content1);

                intent1.putExtra("type", 2);

                intent1.putExtra("seller_id", user_Get.getSeller_id());// );user_Get.getSeller_no()

                PromptManager.SkipResultActivity(BaseActivity, intent1, 101);
                break;


            case R.id.authentication:
                if (isLogin_RenZheng_Set) {
                    PromptManager.SkipActivity(BaseActivity, new Intent(
                            BaseActivity, ARealIdauthSucceed.class));
                } else {
                    PromptManager.SkipActivity(BaseActivity, new Intent(
                            BaseActivity, ARealIdauth.class).putExtra(
                            ARealIdauth.FROM_WHERE_KEY, 2));
                }

                break;

            case R.id.account_safe://账户安全
                PromptManager.SkipActivity(BaseActivity, new Intent(BaseActivity,
                        AAccountSafe.class));
                break;

        }
    }

    /**
     * 分享店铺URL
     */
    private void ShareShopUrl() {
        BNew myBNwe = new BNew();
        myBNwe.setShare_log(Spuit.Shop_Get(getApplicationContext()).getAvatar());
        myBNwe.setShare_title(Spuit.Shop_Get(getApplicationContext()).getSeller_name());
        myBNwe.setShare_content(Spuit.Shop_Get(getApplicationContext()).getIntro());
        myBNwe.setShare_url(MBNewHome.getSellerinfo().getSeller_url());
        ShowP(myView, myBNwe);
    }


    @Override
    protected void InItBundle(Bundle bundle) {
        //
    }

    @Override
    protected void SaveBundle(Bundle bundle) {
    }

    // ****************start********获取头像的相关代码**********start*********************

    /**
     * 对话框测试
     *
     * @param
     */
    private void DialogTest(int type) {
        ShowCustomDialog(type == 1 ? "修改头像" : "修改背景图", "图库", "相机",
                new IDialogResult() {
                    @Override
                    public void RightResult() {
                        camera();
                    }

                    @Override
                    public void LeftResult() {
                        gallery();
                    }
                });
    }

    private static final int PHOTO_REQUEST_CAMERA = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果

    /* 头像名称 */
    private static final String PHOTO_FILE_COVER = "cover.jpg";

    /* 封面 */
    private static final String PHOTO_FILE_AVATAR = "avatar.jpg";

    /**
     * 所有上传图片拍照图片必须要放在本文件夹里面
     */
    public static String PicHost = Environment.getExternalStorageDirectory()
            + "/" + "WtAndroid";
    // private static final String PHOTO_FILE_PATH = getPath(Environment
    // .getExternalStorageDirectory() + "/" + "CutPictureDemo");
    private static final String PHOTO_FILE_PATH = getPath(PicHost);
    private File tempFile;
    private LinearLayout ll_set_icon;
    private Bitmap bitmap;

    /**
     * 从相册获取
     *
     * @param
     */
    public void gallery() {
        switch (show_type) {
            case 1:
                tempFile = getFile(PHOTO_FILE_PATH + "/" + System.currentTimeMillis()
                        + PHOTO_FILE_COVER);
                break;

            case 2:
                tempFile = getFile(PHOTO_FILE_PATH + "/" + System.currentTimeMillis()
                        + PHOTO_FILE_AVATAR);
                break;
        }

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);

    }

    /**
     * 从相机获取
     *
     * @param
     */
    public void camera() {
        // 判断存储卡是否可以用，可用进行存储

        switch (show_type) {
            case 1:

                tempFile = getFile(PHOTO_FILE_PATH + "/" + System.currentTimeMillis()
                        + PHOTO_FILE_COVER);
                break;

            case 2:
                tempFile = getFile(PHOTO_FILE_PATH + "/" + System.currentTimeMillis()
                        + PHOTO_FILE_AVATAR);
                break;
        }

        if (hasSdcard()) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 调用照相机
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
            startActivityForResult(intent, PHOTO_REQUEST_CAMERA);

        }
    }

    /**
     * 返回结果
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode == RESULT_CODE) {
            String result = data.getStringExtra("content");
            StrUtils.SetTxt(tv_conment1, result);
            //StrUtils.SetTxt(shop_data_name, result);
        }

        if (requestCode == 101 && resultCode == RESULT_CODE) {
            String result = data.getStringExtra("content");
            StrUtils.SetTxt(tv_conment2, result);
        }

        // 相册选取
        if (requestCode == PHOTO_REQUEST_GALLERY && data != null) {
            crop(data.getData(), Uri.fromFile(tempFile));
        }
        // 照相机选取
        else if (requestCode == PHOTO_REQUEST_CAMERA
                && resultCode == Activity.RESULT_OK) {
            // 这里拍摄图片和截取后的图片文件都写入了同一个文件，photo.jpg
            crop(Uri.fromFile(tempFile), Uri.fromFile(tempFile));
        }
        // 返回截取的图片
        else if (requestCode == PHOTO_REQUEST_CUT) {
            if (data != null) {
                // bitmap = decodeUriAsBitmap(Uri.fromFile(tempFile));
                bitmap = StrUtils.GetBitMapFromPath(tempFile.toString());
                if (null == bitmap) {
                    return;
                }
                if(CheckNet(BaseContext))return;
                String picType = "";
                switch (show_type) {
                    case 1:// 头像
                        picType = "avatar";
                        UpdaIvTest(tempFile, Bitmap2Bytes(bitmap),
                                StrUtils.UploadQNName(picType), tab_shop_iv,
                                bitmap, 1);
                        break;

                    case 2:// 封面
                        picType = "photo";
                        UpdaIvTest(tempFile, Bitmap2Bytes(bitmap),
                                StrUtils.UploadQNName(picType), tab_shop_iv,
                                bitmap, 2);
                        break;

                }

            } else {
                Toast.makeText(getApplicationContext(), "截取图片失败",
                        Toast.LENGTH_SHORT).show();
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CacheUtil.BitMapRecycle(qrBitmap);
        CacheUtil.BitMapRecycle(bitmap);

    }

    /**
     * 裁切图片 uri为选中图片返回的Uri cutImgUri为把截取图片写入sd卡的Uri
     */
    private void crop(Uri uri, Uri cutImgUri) {
        boolean  IsAvater=(show_type==1);
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", IsAvater?8:16);
        intent.putExtra("aspectY", 8);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", IsAvater?500:800);
        intent.putExtra("outputY", 500);
        // 图片格式
        intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", false);// true:不返回uri，false：返回uri
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cutImgUri);// 写入截取的图片
        startActivityForResult(intent, PHOTO_REQUEST_CUT);


    }

    /**
     * uri转换为bitmap
     *
     * @param uri
     * @return
     */
    public Bitmap decodeUriAsBitmap(Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(this.getContentResolver()
                    .openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    // ****************end********获取头像的相关代码**********end*********************

    public void UpdaIvTest(final File aa, byte[] bytes, String picname,
                           final ImageView Iv, final Bitmap bitmap, final int type) {


        NUPLoadUtil dLoadUtils = new NUPLoadUtil(BaseContext, aa, picname);


        dLoadUtils.SetUpResult1(new NUPLoadUtil.UpResult1() {
            @Override
            public void Progress(String arg0, double arg1) {

            }

            @Override
            public void Onerror() {
                PromptManager.closeLoading();
            }

            @Override
            public void Complete(String HostUrl, String Url) {

                LogUtils.i(Url);

                LoadIv(Iv, bitmap, HostUrl, type);
            }
        });
        PromptManager.showLoading(BaseContext);
        dLoadUtils.UpLoad();
    }

    /**
     * 上传图片url
     */
    private void LoadIv(final ImageView Iv, final Bitmap bitmap,
                        final String PicUrl, final int Type) {
        // SetTitleHttpDataLisenter(this);
        // HashMap<String, String> map = new HashMap<String, String>();
        // map.put("seller_id", seller_id);

        String myUrl = "";

        NHttpBaseStr mBaseStr = new NHttpBaseStr(BaseContext);
        mBaseStr.setPostResult(new IHttpResult<String>() {

            @Override
            public void onError(String error, int LoadType) {
                PromptManager.ShowCustomToast(BaseContext, "头像上传失败");
            }

            @Override
            public void getResult(int Code, String Msg, String Data) {
                if (Code == 200) {
                    EventBus.getDefault().post(
                            new BMessage(
                                    BMessage.Fragment_Home_Bind));

                    if (1 == Type) {
                        Iv.setImageBitmap(bitmap);
                        Spuit.Save_Shop_Head(getApplicationContext(), PicUrl);
                        EventBus.getDefault().post(
                                new BMessage(
                                        BMessage.Tage_Shop_data_cover_change));


                    }
                    if (2 == Type) {
                        Spuit.Save_Shop_Background(getApplicationContext(), PicUrl);
                        // 发送事件
                        EventBus.getDefault()
                                .post(new BMessage(
                                        BMessage.Tage_Shop_data_background_change));
                    }

                    PromptManager.ShowCustomToast(BaseContext,
                            1 == Type ? "头像上传成功" : "封面上传成功");
                } else {
                    onError("解析错误", 1);
                }

            }
        });

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("seller_id", user_Get.getSeller_id());
        PromptManager.showLoading(BaseContext);
        if (Type == 1) {// 头像
            myUrl = Constants.MODIFI_SHOP_ICON;
            map.put("avatar", PicUrl);
            // PromptManager.ShowMyToast(BaseContext, "我成功了0");
        } else if (Type == 2) {// 封面
            myUrl = Constants.MODIFI_SHOP_COVER;
            map.put("cover", PicUrl);

        }

        mBaseStr.getData(myUrl, map, Method.PUT);
    }





    @Override
    public boolean onLongClick(View v) {

        copyShopID();

        return true;
    }

    /**
     * 复制
     */
    @SuppressLint("NewApi")
    private void copyShopID() {
        String text = tv_shop_id.getText().toString().trim();
        myClip = ClipData.newPlainText("text", text);
        myClipboard.setPrimaryClip(myClip);
        PromptManager.ShowMyToast(BaseContext, "店铺号复制成功");
    }

}
