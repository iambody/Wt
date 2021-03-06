package io.vtown.WeiTangApp.ui.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.volley.Request.Method;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;
import io.vtown.WeiTangApp.BaseApplication;
import io.vtown.WeiTangApp.R;
import io.vtown.WeiTangApp.bean.bcomment.BComment;
import io.vtown.WeiTangApp.bean.bcomment.BLComment;
import io.vtown.WeiTangApp.bean.bcomment.BLDComment;
import io.vtown.WeiTangApp.bean.bcomment.BUser;
import io.vtown.WeiTangApp.bean.bcomment.easy.BShop;
import io.vtown.WeiTangApp.bean.bcomment.easy.shop.BShopBase;
import io.vtown.WeiTangApp.bean.bcomment.easy.shop.BShopCatory;
import io.vtown.WeiTangApp.bean.bcomment.easy.shop.BShopGoods;
import io.vtown.WeiTangApp.bean.bcomment.news.BMessage;
import io.vtown.WeiTangApp.comment.contant.Constants;
import io.vtown.WeiTangApp.comment.contant.PromptManager;
import io.vtown.WeiTangApp.comment.contant.Spuit;
import io.vtown.WeiTangApp.comment.util.StrUtils;
import io.vtown.WeiTangApp.comment.util.ViewHolder;
import io.vtown.WeiTangApp.comment.util.image.ImageLoaderUtil;
import io.vtown.WeiTangApp.comment.view.CircleImageView;
import io.vtown.WeiTangApp.comment.view.custom.CompleteGridView;
import io.vtown.WeiTangApp.comment.view.custom.CompleteListView;
import io.vtown.WeiTangApp.comment.view.custom.PullView;
import io.vtown.WeiTangApp.comment.view.listview.HorizontalListView;
import io.vtown.WeiTangApp.ui.ATitileNoBase;
import io.vtown.WeiTangApp.ui.comment.AShopGoodSou;
import io.vtown.WeiTangApp.ui.comment.AphotoPager;
import io.vtown.WeiTangApp.ui.comment.im.AChatLoad;
import io.vtown.WeiTangApp.ui.title.AGoodDetail;
import io.vtown.WeiTangApp.ui.title.center.myshow.AOtherShow;
import io.vtown.WeiTangApp.ui.title.center.myshow.ARecyclerOtherShow;

/**
 * @author 作者 大兔兔 wangyongkui@v-town.cc
 * @author 自营商品的店铺
 * @version 创建时间：2016-5-17 下午6:26:43
 */
public class AShopDetail extends ATitileNoBase implements PullView.OnFooterRefreshListener {
    /**
     * 外层的view
     */
    private PullView activivty_shopdetail_outlay;
    private ScrollView shop_out_scrollview;
    // shop的cover
    private ImageView shopdetail_imagview_cover;
    // 横向ls的查看资质所在的布局
    private LinearLayout shopdetail_shop_bran_horizon_lay;
    /**
     * 外层的
     */
    private View shopdetail_nodata_lay;

    /**
     * 头像
     */
    private CircleImageView shopdetail_imagview;
    /**
     * 店铺名称
     */
    private TextView shopdetail_shop_name;
    /**
     * 店铺描述
     */
    private TextView shopdetail_shop_tag;
    /**
     * 已关注人数
     */
    private TextView shopdetail_shop_guanzhu_number;
    /**
     * 今日访问人数
     */
    private TextView shopdetail_shop_visitor_number;

    /**
     * 资质列表
     */
    private HorizontalListView shopdetail_zizhi_horizon_ls;
    /**
     * 资质Ls对应的ap
     */
    private ShopBrandsLsAp shopBrandsLsAp;

    /**
     * 查看资质按钮
     */
    private TextView shopdetail_shop_zizhi;
    // 查看show

    private TextView shopdetail_shop_look_show;
    /**
     * 品牌的按钮
     */
    private TextView shop_detail_brand;
    /**
     * 自营的按钮
     */
    private TextView shop_detail_ziying;
    /**
     * 分类的横向滑动
     */
    private HorizontalListView shop_detail_horizontal_ls;
    /**
     * 分类的横向Ap
     */
    private ShopSortLsAp shopSortLsAp;
    /**
     * 商品的listview
     */
    private CompleteListView shop_detail_downgoods_ls;
    /**
     * 商品的
     */
    private DownGoodsAp downGoodsAp;

    /**
     * 店铺信息的标识
     */
    public static final int Tag_Inf = 321;
    /**
     * 店铺里面的单个品牌的数据标识
     */
    public static final int Tag_Ls = 322;
    // 关注店铺
    public static final int Tag_GuanZhu = 329;
    // 取消关注店铺
    public static final int Tag_CancleGuanZhu = 359;

    /**
     * 品牌是否被点击
     */
//    public boolean IsBrand = true;
    /**
     * 分页加载时候的当前页
     */
    private int CurrentPage = 1;
    //当前的类别
    private String CurrentCategory_Id = "-1";

    /**
     * 店铺的数据源
     */
    private BShop MyData;// BDComment
    /**
     * 用户信息
     */
    private BUser user_Get;
    /**
     * 商品总数
     */
    private TextView shopdetail_shop_total_goods;
    /**
     * show 数量
     */
    private TextView shopdetail_shop_show_count;

    /**
     * show
     */
    private LinearLayout ll_shopdetail_shop_look_show;

    private ImageView shopdetail_sou_iv;//搜索

    /**
     * Te联系卖家
     */
    private RelativeLayout rl_shop_detail_contact_log;

    private RelativeLayout rl_shop_detail_shoucang_log;

    private ImageView shop_detail_shoucang_log;
    //分类的线性布局
    private LinearLayout shop_detail_catory_lay;
    //缓存自营商品的列表
    private List<BShopGoods> CacheLsDatas_ZiYing = new ArrayList<BShopGoods>();
    private List<BShopGoods> CacheLsDatas_Brand = new ArrayList<BShopGoods>();
    /**
     * 控制gradview和ls切换的imagview
     */
    private CompleteGridView shop_detail_downgoods_gr;
    private ImageView shop_detail_goods_chang_iv;
    private boolean IsShowLs = true;


    // 是否关注本店铺
    private boolean IsCollect;
    // 是否品牌按钮被点击
    private boolean IsBrandStatue = false;

//    private ImageView iv_shop_detail_search;
    // private EditText et_shop_detail_search;

    private boolean mFlag = true;


    @Override
    protected void InItBaseView() {
        setContentView(R.layout.activity_shopdetail);
        user_Get = Spuit.User_Get(BaseContext);
        SetTitleHttpDataLisenter(this);
        IBase();
        IData();
    }

    // 获取商品详情的通道
    private void IData() {
        PromptManager.showtextLoading(BaseContext, getResources().getString(R.string.loading));

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("seller_id", baseBcBComment.getId());// 自营店铺
        map.put("_member_id", user_Get.getId());
        FBGetHttpData(map, Constants.Shop_Inf, Method.GET, Tag_Inf,
                LOAD_INITIALIZE);

    }

    /**
     * 获取筛选列表信息 Sell_Id是店铺商的ID// category_id 是分类的ID
     */
    private void GetList(int Page, String category_id, int LoadType) {
        if (LoadType == LOAD_REFRESHING)
            PromptManager.showLoading(BaseContext);
        if (LoadType == LOAD_LOADMOREING)
            activivty_shopdetail_outlay.ShowFoot();
        // 获取全部信息
        HashMap<String, String> SelectMap = new HashMap<String, String>();
        SelectMap.put("category_id", category_id);// 空代表全部，
        SelectMap.put("seller_id", baseBcBComment.getId());// 商家的id
        SelectMap.put("is_agent", IsBrandStatue ? "1" : "0");// 是否是品牌
        SelectMap.put("sale_status", "100");// 售卖状态=>100表示售卖中
        SelectMap.put("is_delete", "0");// 0-正常 1-已删除
        // sssss
        SelectMap.put("page", Page + "");
        SelectMap.put("pagesize", Constants.PageSize + "");

        FBGetHttpData(SelectMap, Constants.Select_Ls, Method.GET, Tag_Ls,
                LoadType);
    }

    /**
     * 关注店铺
     */
    private void GuanZhuSho(String ShopId, boolean IsTOGuanzhu) {// CancleGuanZhuShop
//        PromptManager.showLoading(BaseContext);
        // Tag_GuanZhu
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("seller_id", ShopId);// 所要关注的店铺的的店铺id
        hashMap.put("member_id", user_Get.getId());

        FBGetHttpData(hashMap, IsTOGuanzhu ? Constants.GuanZhuShop
                        : Constants.CancleGuanZhuShop, IsTOGuanzhu ? Method.POST
                        : Method.DELETE, IsTOGuanzhu ? Tag_GuanZhu : Tag_CancleGuanZhu,
                LOAD_LOADMOREING);
    }

    private void IBase() {
        shop_detail_catory_lay = (LinearLayout) findViewById(R.id.shop_detail_catory_lay);

        shopdetail_sou_iv = (ImageView) findViewById(R.id.shopdetail_sou_iv);

        shop_out_scrollview = (ScrollView) findViewById(R.id.shop_out_scrollview);
        shop_detail_downgoods_gr = (CompleteGridView) findViewById(R.id.shop_detail_downgoods_gr);

//        iv_shop_detail_search = (ImageView) findViewById(R.id.iv_shop_detail_search);
        //et_shop_detail_search = (EditText) findViewById(R.id.et_shop_detail_search);

        shop_detail_goods_chang_iv = (ImageView) findViewById(R.id.shop_detail_goods_chang_iv);
        shop_detail_goods_chang_iv.setOnClickListener(this);

        shopdetail_shop_look_show = (TextView) findViewById(R.id.shopdetail_shop_look_show);


        shopdetail_imagview_cover = (ImageView) findViewById(R.id.shopdetail_imagview_cover);

        shopdetail_shop_bran_horizon_lay = (LinearLayout) findViewById(R.id.shopdetail_shop_bran_horizon_lay);

        activivty_shopdetail_outlay = (PullView) findViewById(R.id.activivty_shopdetail_outlay);
        activivty_shopdetail_outlay.setOnFooterRefreshListener(this);
        activivty_shopdetail_outlay.setLastUpdated(new Date().toLocaleString());
        activivty_shopdetail_outlay.HindFoot();

        shopdetail_nodata_lay = findViewById(R.id.shopdetail_nodata_lay);
        shopdetail_nodata_lay.setOnClickListener(this);
        IDataView(activivty_shopdetail_outlay, shopdetail_nodata_lay,
                NOVIEW_INITIALIZE);

        shopdetail_imagview = (CircleImageView) findViewById(R.id.shopdetail_imagview);

        shopdetail_imagview.setBorderWidth(5);
        shopdetail_imagview.setBorderColor(getResources().getColor(R.color.TextColorWhite));


        ll_shopdetail_shop_look_show = (LinearLayout) findViewById(R.id.ll_shopdetail_shop_look_show);


        shopdetail_shop_name = (TextView) findViewById(R.id.shopdetail_shop_name);
        shopdetail_shop_tag = (TextView) findViewById(R.id.shopdetail_shop_tag);
        //shopdetail_shop_lianix = (TextView) findViewById(R.id.shopdetail_shop_lianix);
        rl_shop_detail_contact_log = (RelativeLayout) findViewById(R.id.rl_shop_detail_contact_log);
        //shopdetail_shop_guanzhu_bt = (TextView) findViewById(R.id.shopdetail_shop_guanzhu_bt);
        rl_shop_detail_shoucang_log = (RelativeLayout) findViewById(R.id.rl_shop_detail_shoucang_log);
        shop_detail_shoucang_log = (ImageView) findViewById(R.id.shop_detail_shoucang_log);
        shopdetail_shop_guanzhu_number = (TextView) findViewById(R.id.shopdetail_shop_guanzhu_number);

        shopdetail_shop_visitor_number = (TextView) findViewById(R.id.shopdetail_shop_visitor_number);
        shopdetail_shop_show_count = (TextView) findViewById(R.id.shopdetail_shop_show_count);


        shopdetail_shop_total_goods = (TextView) findViewById(R.id.shopdetail_shop_total_goods);
        shopdetail_zizhi_horizon_ls = (HorizontalListView) findViewById(R.id.shopdetail_zizhi_horizon_ls);
        shop_detail_horizontal_ls = (HorizontalListView) findViewById(R.id.shop_detail_horizontal_ls);

        shopdetail_shop_zizhi = (TextView) findViewById(R.id.shopdetail_shop_zizhi);

        shop_detail_brand = (TextView) findViewById(R.id.shop_detail_brand);
        shop_detail_ziying = (TextView) findViewById(R.id.shop_detail_ziying);
        shop_detail_downgoods_ls = (CompleteListView) findViewById(R.id.shop_detail_downgoods_ls);
        downGoodsAp = new DownGoodsAp(R.layout.item_shopdetail_ziying_ls);

        shopdetail_shop_zizhi.setOnClickListener(this);

        rl_shop_detail_contact_log.setOnClickListener(this);
        rl_shop_detail_shoucang_log.setOnClickListener(this);
        //shopdetail_shop_guanzhu_bt.setOnClickListener(this);
        ll_shopdetail_shop_look_show.setOnClickListener(this);

        shop_detail_brand.setOnClickListener(this);
        shop_detail_ziying.setOnClickListener(this);
        shopdetail_sou_iv.setOnClickListener(this);

        shopBrandsLsAp = new ShopBrandsLsAp(BaseContext,
                R.layout.item_shop_detail_brands);
        shopdetail_zizhi_horizon_ls.setAdapter(shopBrandsLsAp);

        shopSortLsAp = new ShopSortLsAp(BaseContext,
                R.layout.item_fragment_shop_good_manger_brand);
        shop_detail_horizontal_ls.setAdapter(shopSortLsAp);

        shop_detail_downgoods_ls.setAdapter(downGoodsAp);
        shop_detail_downgoods_gr.setAdapter(downGoodsAp);


        shop_detail_downgoods_gr.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int arg2, long arg3) {
                BShopGoods da = (BShopGoods) arg0.getItemAtPosition(arg2);
                PromptManager.SkipActivity(BaseActivity, new Intent(
                        BaseContext, AGoodDetail.class).putExtra(
                        "goodid", da.getId()));

            }
        });
        shop_detail_downgoods_ls
                .setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int arg2, long arg3) {
                        BShopGoods da = (BShopGoods) arg0.getItemAtPosition(arg2);
                        PromptManager.SkipActivity(BaseActivity, new Intent(
                                BaseContext, AGoodDetail.class).putExtra(
                                "goodid", da.getId()));

                    }
                });
        shop_detail_horizontal_ls
                .setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int arg2, long arg3) {
                        BShopCatory da = (BShopCatory) arg0.getItemAtPosition(arg2);
                        CurrentPage = 1;
                        CurrentCategory_Id = da.getId();
                        GetList(CurrentPage, da.getId(), LOAD_REFRESHING);

                    }
                });

    }

    @Override
    protected void InitTile() {

    }

    @Override
    protected void DataResult(int Code, String Msg, BComment Data) {

        switch (Data.getHttpResultTage()) {
            case Tag_Inf:

                if (StrUtils.isEmpty(Data.getHttpResultStr())) {
                    DataError(Msg, Data.getHttpLoadType());
                    return;
                }
                IDataView(activivty_shopdetail_outlay, shopdetail_nodata_lay,
                        NOVIEW_RIGHT);
                MyData = new BShop();
                shop_out_scrollview.smoothScrollTo(0, 20);
                // 解析**************************************************************************************************
                JSONObject mJsonObject = null;
                try {
                    mJsonObject = new JSONObject(Data.getHttpResultStr());

                } catch (JSONException e1) {
                    e1.printStackTrace();
                    DataError(Msg, Data.getHttpLoadType());
                    return;
                }
                if (StrUtils.JsonContainKey(mJsonObject, "agents")) {

                    try {
                        if (!StrUtils.isEmpty(mJsonObject.getString("agents")))
                            MyData.setAgents(JSON.parseArray(
                                    mJsonObject.getString("agents"),
                                    BShopGoods.class));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (StrUtils.JsonContainKey(mJsonObject, "categorys")) {

                    try {
                        if (!StrUtils.isEmpty(mJsonObject.getString("categorys")))//{//存在分类
                            MyData.setCategorys(JSON.parseArray(
                                    mJsonObject.getString("categorys"),
                                    BShopCatory.class));
//                        } else {//没有分类
//
//
//
//                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (StrUtils.JsonContainKey(mJsonObject, "base")) {
                    try {
                        if (!StrUtils.isEmpty(mJsonObject.getString("base")))
                            MyData.setBase(JSON.parseObject(
                                    mJsonObject.getString("base"), BShopBase.class));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (StrUtils.JsonContainKey(mJsonObject, "diy")) {
                    try {
                        if (!StrUtils.isEmpty(mJsonObject.getString("diy")))
                            MyData.setDiy(JSON.parseArray(
                                    mJsonObject.getString("diy"), BShopGoods.class));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                // 解析**************************************************************************************************************


                BaseViewFradsash(MyData.getBase());
                shopSortLsAp.Refrsh(MyData.getCategorys());
                shopBrandsLsAp.FrashView(MyData.getAgents());
                downGoodsAp.FrashView(MyData.getDiy());

                break;
            case Tag_Ls:
                List<BShopGoods> blComments = new ArrayList<BShopGoods>();
                if (StrUtils.isEmpty(Data.getHttpResultStr()) && Data.getHttpLoadType() == LOAD_LOADMOREING) {
                    PromptManager.ShowCustomToast(BaseContext, "没有更多商品");
                    activivty_shopdetail_outlay.onFooterRefreshComplete();
                    activivty_shopdetail_outlay.HindFoot();
                    return;
                }

                if (StrUtils.isEmpty(Data.getHttpResultStr()) && Data.getHttpLoadType() == LOAD_REFRESHING) {
                    downGoodsAp.FrashView(new ArrayList<BShopGoods>());
                    return;
                }
                try {
                    blComments = JSON.parseArray(Data.getHttpResultStr(),
                            BShopGoods.class);
                } catch (Exception e) {
                    DataError(Msg, Data.getHttpLoadType());

                    return;
                }
                if (Data.getHttpLoadType() == LOAD_REFRESHING) {
                    downGoodsAp.FrashView(blComments);
                    if (!IsBrandStatue) {
                        CacheLsDatas_ZiYing = new ArrayList<BShopGoods>();
                        CacheLsDatas_ZiYing = blComments;
                    } else {
                        CacheLsDatas_Brand = new ArrayList<BShopGoods>();
                        CacheLsDatas_Brand = blComments;
                    }

                }
                if (Data.getHttpLoadType() == LOAD_LOADMOREING) {
                    downGoodsAp.AddFrashView(blComments);
                    activivty_shopdetail_outlay.onFooterRefreshComplete();
                    if (blComments.size() == 10) {
                        activivty_shopdetail_outlay.ShowFoot();
                    } else {
                        activivty_shopdetail_outlay.HindFoot();
                    }
                    if (!IsBrandStatue) {
                        CacheLsDatas_ZiYing.addAll(blComments);
                    } else {
                        CacheLsDatas_Brand.addAll(blComments);
                    }
                }

                break;
            case Tag_GuanZhu:// 关注店铺
                IsCollect = true;
                IsCollectBtControl(IsCollect);
                // 关注店铺成功后需要刷新****

                GuanZhuaFrash(true);
                EventBus.getDefault()
                        .post(new BMessage(BMessage.Tage_ShopSouFrash));
                break;
            case Tag_CancleGuanZhu:// 取消关注
                IsCollect = false;
                IsCollectBtControl(IsCollect);
                // 取消关注刷新关注人数
                GuanZhuaFrash(false);
                EventBus.getDefault()
                        .post(new BMessage(BMessage.Tage_ShopSouFrash));
                break;
            default:
                break;
        }
    }

    /**
     * 刷新收藏人数
     */

    private void GuanZhuaFrash(boolean IsCollect) {
        // StrUtils.SetTxt(shopdetail_shop_guanzhu_number, base.getAttention());
        int Numbe = 0;
        try {
            Numbe = StrUtils.toInt(shopdetail_shop_guanzhu_number.getText()
                    .toString().trim());
        } catch (Exception e) {
            return;
        }

        if (IsCollect) {// 收藏就加一
            Numbe = Numbe + 1;
            StrUtils.SetTxt(shopdetail_shop_guanzhu_number, Numbe + "");
        } else {// 取消收藏就减一
            Numbe = Numbe - 1;
            StrUtils.SetTxt(shopdetail_shop_guanzhu_number, Numbe + "");

        }

    }

    /**
     * 刷新基础UI
     *
     * @param base
     */
    private void BaseViewFradsash(BShopBase base) {
        if (base.getMember_id() != null
                && base.getMember_id().equals(user_Get.getMember_id())) {// 是我自己的店铺
            rl_shop_detail_shoucang_log.setVisibility(View.GONE);
            rl_shop_detail_contact_log.setVisibility(View.GONE);
        }
        IsCollect = base.getIs_collect().equals("1");// 1标识已经收藏过

        ImageLoaderUtil.Load2(base.getAvatar(), shopdetail_imagview,
                R.drawable.testiv);
        ImageLoaderUtil.Load2(base.getCover(),
                shopdetail_imagview_cover, R.drawable.item_shangji_iv);
        StrUtils.SetTxt(shopdetail_shop_name, base.getSeller_name());


        StrUtils.SetTxt(shopdetail_shop_tag, StrUtils.isEmpty(base.getIntro()) ? "暂无介绍" : base.getIntro());
        StrUtils.SetTxt(shopdetail_shop_guanzhu_number, base.getAttention());
        StrUtils.SetTxt(shopdetail_shop_visitor_number, base.getTodayVisitor());
        StrUtils.SetTxt(shopdetail_shop_total_goods, base.getGoods_count());
        StrUtils.SetTxt(shopdetail_shop_show_count, base.getShow_count());
        IsCollectBtControl(IsCollect);

        if (base.getGoods_count().equals("0")) {
            shopdetail_sou_iv.setVisibility(View.GONE);
            ShowErrorCanLoad(getResources().getString(R.string.shop_no_good));
            shop_detail_catory_lay.setVisibility(View.GONE);
            shopdetail_nodata_lay.setVisibility(View.VISIBLE);
            shopdetail_sou_iv.setClickable(false);
        }
        if(StrUtils.isEmpty(base.getShow_count())||"0".equals(base.getShow_count())){
            ll_shopdetail_shop_look_show.setClickable(false);
        }

    }

    private void IsCollectBtControl(boolean isCollect) {
        if (isCollect) {// 收藏过
            shop_detail_shoucang_log.setImageDrawable(getResources().getDrawable(R.drawable.ic_shoucang_press));
        } else {// 未收藏过

            shop_detail_shoucang_log.setImageDrawable(getResources().getDrawable(R.drawable.ic_shoucang_nor));

        }

    }

    @Override
    protected void DataError(String error, int LoadTyp) {
        if (LoadTyp == LOAD_INITIALIZE) {
            ShowErrorCanLoad(getResources().getString(R.string.error_null_noda));
            IDataView(activivty_shopdetail_outlay, shopdetail_nodata_lay,
                    NOVIEW_ERROR);
        } else if (LoadTyp == LOAD_LOADMOREING) {
            activivty_shopdetail_outlay.onFooterRefreshComplete();
        } else {
            PromptManager.ShowCustomToast(BaseContext, error);
        }

    }


    @Override
    public void onFooterRefresh(PullView view) {
        if (CurrentCategory_Id.equals("-1")) {
            activivty_shopdetail_outlay.onFooterRefreshComplete();
            return;
        }

        CurrentPage = CurrentPage + 1;
        GetList(CurrentPage, CurrentCategory_Id, LOAD_LOADMOREING);
    }

    /**
     * 只有品牌商品中使用到的横向滑动的listview
     */

    private class ShopSortLsAp extends BaseAdapter {
        private Context mycContext;
        private LayoutInflater inflater;
        private int ResourceId;
        private List<BShopCatory> datas = new ArrayList<BShopCatory>();

        public ShopSortLsAp(Context mycContext, int resourceId) {
            super();
            this.mycContext = mycContext;
            this.inflater = LayoutInflater.from(mycContext);
            this.ResourceId = resourceId;
        }

        /**
         * 刷新需要在头部添加一个全部的item
         */
        public void Refrsh(List<BShopCatory> da) {
            if (da == null || da.size() == 0)
                return;
            BShopCatory dad = new BShopCatory();
            dad.setId("");
            dad.setCate_name("全部");
            dad.setBrandDetaiLsSelect(false);
            this.datas = new ArrayList<BShopCatory>();
            this.datas.add(dad);
            this.datas.addAll(da);

            notifyDataSetChanged();

        }

        public void Notifi(List<BShopCatory> daSS) {
            this.datas = daSS;
            this.notifyDataSetChanged();
        }

        public List<BShopCatory> getdaBlComments() {
            return datas;
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int arg0) {
            return datas.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(int arg0, View convertView, ViewGroup arg2) {
            MyShopItem myItem = null;
            if (convertView == null) {
                myItem = new MyShopItem();
                convertView = inflater.inflate(ResourceId, null);
                myItem.item_fragment_shop_good_manger_brand_name = (TextView) convertView
                        .findViewById(R.id.item_fragment_shop_good_manger_brand_name);
                // Views.add(myItem);
                convertView.setTag(myItem);

            } else {
                myItem = (MyShopItem) convertView.getTag();
            }
            myItem.item_fragment_shop_good_manger_brand_name
                    .setBackgroundResource(datas.get(arg0)
                            .isBrandDetaiLsSelect() ? R.drawable.shape_comment_oval_pre
                            : R.drawable.shape_comment_oval);
            myItem.item_fragment_shop_good_manger_brand_name
                    .setTextColor(getResources()
                            .getColor(
                                    datas.get(arg0).isBrandDetaiLsSelect() ? R.color.app_fen
                                            : R.color.grey));

            StrUtils.SetTxt(myItem.item_fragment_shop_good_manger_brand_name,
                    datas.get(arg0).getCate_name());
            myItem.item_fragment_shop_good_manger_brand_name
                    .setOnClickListener(new HorizontalItemClikListener(arg0,
                            datas, myItem, this));// TODO后期需要传入正确的BLComment数据bean
            // 目前为null
            return convertView;
        }

        class MyShopItem {
            TextView item_fragment_shop_good_manger_brand_name;
        }

        class HorizontalItemClikListener implements OnClickListener {

            private int Postion;// 记录点击的位置
            private List<BShopCatory> blCommentss;// 记录品牌的数据beanList<BShopCatory>
            private MyShopItem myBrandItem;
            ShopSortLsAp ap;

            public HorizontalItemClikListener(int postion,
                                              List<BShopCatory> data, MyShopItem item,
                                              ShopSortLsAp aBrandLsAp) {
                super();
                myBrandItem = item;
                ap = aBrandLsAp;
                Postion = postion;
                blCommentss = data;
            }

            @Override
            public void onClick(View arg0) {
                if (blCommentss.get(Postion).isBrandDetaiLsSelect())
                    return;

                for (int i = 0; i < blCommentss.size(); i++) {

                    blCommentss.get(i).setBrandDetaiLsSelect(i == Postion);

                    myBrandItem.item_fragment_shop_good_manger_brand_name
                            .setBackgroundResource(blCommentss.get(Postion)
                                    .isBrandDetaiLsSelect() ? R.drawable.shape_comment_oval_pre
                                    : R.drawable.shape_comment_oval);
                    myBrandItem.item_fragment_shop_good_manger_brand_name
                            .setTextColor(getResources()
                                    .getColor(
                                            blCommentss.get(Postion)
                                                    .isBrandDetaiLsSelect() ? R.color.app_fen
                                                    : R.color.grey));
                }
                ap.notifyDataSetChanged();
            }
        }
    }

    public void FristSelect() {
        List<BShopCatory> blCommentss = new ArrayList<BShopCatory>();

        blCommentss = shopSortLsAp.getdaBlComments();

        for (int i = 0; i < blCommentss.size(); i++) {
            if (i == 0) {
                blCommentss.get(i).setBrandDetaiLsSelect(true);
            } else
                blCommentss.get(i).setBrandDetaiLsSelect(false);

        }
        shopSortLsAp.Notifi(blCommentss);
    }

    /**
     * 代理店铺 店铺详情
     *
     * @author datutu
     */
    class ShopBrandsLsAp extends BaseAdapter {
        private Context mycContext;
        private LayoutInflater inflater;
        private int ResourceId;
        private List<BShopGoods> datas = new ArrayList<BShopGoods>();

        public ShopBrandsLsAp(Context mycContext, int resourceId) {
            super();
            this.mycContext = mycContext;
            this.inflater = LayoutInflater.from(mycContext);
            this.ResourceId = resourceId;
        }

        public void FrashView(List<BShopGoods> datasa) {
            if (datasa == null || datasa.size() == 0) {
                shopdetail_shop_bran_horizon_lay.setVisibility(View.GONE);
                return;
            }

            shopdetail_shop_bran_horizon_lay.setVisibility(View.VISIBLE);
            this.datas = datasa;
            this.notifyDataSetChanged();
        }

        public List<BShopGoods> GetRsource() {
            return datas;

        }

        @Override
        public int getCount() {
            return datas.size();
        }

        public List<String> getmyLs() {
            List<String> mList = new ArrayList<String>();

            for (int i = 0; i < datas.size(); i++) {
                mList.add(datas.get(i).getAvatar());
            }
            return mList;
        }

        @Override
        public Object getItem(int arg0) {
            return datas.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(int arg0, View arg1, ViewGroup arg2) {
            ShopBrandsItems mBrandsItems = null;
            if (null == arg1) {
                mBrandsItems = new ShopBrandsItems();
                arg1 = inflater.inflate(R.layout.item_shop_detail_brands, null);
                mBrandsItems.item_shop_detail_brands_brandiv = ViewHolder.get(
                        arg1, R.id.item_shop_detail_brands_brandiv);
                arg1.setTag(mBrandsItems);

            } else {
                mBrandsItems = (ShopBrandsItems) arg1.getTag();
            }
            ImageLoaderUtil.Load2(datas.get(arg0).getAvatar(),
                    mBrandsItems.item_shop_detail_brands_brandiv,
                    R.drawable.error_iv2);

            final int Postiooon = arg0;
            mBrandsItems.item_shop_detail_brands_brandiv
                    .setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            List<String> daa = getmyLs();
                            if (daa.size() == 0)
                                return;
                            Intent mIntent = new Intent(BaseContext,
                                    AphotoPager.class);
                            mIntent.putExtra("position", Postiooon);
                            mIntent.putExtra("urls", StrUtils.LsToArray(daa));
                            PromptManager.SkipActivity(BaseActivity, mIntent);
                        }
                    });
            return arg1;
        }

        class ShopBrandsItems {
            ImageView item_shop_detail_brands_brandiv;
        }
    }

    /**
     * 下边商品的适配器
     */
    private class DownGoodsAp extends BaseAdapter {

        private LayoutInflater inflater;
        private int ResourceId;
        private List<BShopGoods> datas = new ArrayList<BShopGoods>();
        private List<String> GoodsIdS = new ArrayList<String>();

        public DownGoodsAp(int resourceId) {
            super();
            this.inflater = LayoutInflater.from(BaseContext);
            this.ResourceId = resourceId;
        }

        /**
         * 刷新ls
         */
        public void FrashView(List<BShopGoods> datasa) {
            if (null == datasa)
                return;
            this.datas = datasa;
            this.notifyDataSetChanged();
            GoodsIdS = new ArrayList<String>();
            for (int i = 0; i < datas.size(); i++) {
                GoodsIdS.add(datas.get(i).getId());
            }
        }

        /**
         * Add刷新
         *
         * @param
         */
        public void AddFrashView(List<BShopGoods> datasasss) {

            if (null == datasasss || datasasss.size() == 0)
                return;
            List<BShopGoods> ddddd = new ArrayList<BShopGoods>();
            ddddd.addAll(datas);
            //(datasasss);


            for (int i = 0; i < datasasss.size(); i++) {
                if (!GoodsIdS.contains(datasasss.get(i).getId())) {
                    GoodsIdS.add(datasasss.get(i).getId());
                    ddddd.add(datasasss.get(i));
                }
            }
            this.datas = null;
            this.datas = ddddd;
            this.notifyDataSetChanged();
        }

        /**
         * 清除ls
         */
        public void Clearn() {
            this.datas = new ArrayList<BShopGoods>();
            this.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int arg0) {
            return datas.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(int arg0, View arg1, ViewGroup arg2) {
            ShopDetailGoodsItem detailGoodsItem = null;
            if (null == arg1) {
                arg1 = inflater.inflate(ResourceId, null);
                detailGoodsItem = new ShopDetailGoodsItem();
                detailGoodsItem.item_shopdetail_good_iv = ViewHolder.get(arg1,
                        R.id.item_shopdetail_good_iv);
                detailGoodsItem.item_shopdetail_good_name = ViewHolder.get(
                        arg1, R.id.item_shopdetail_good_name);
                detailGoodsItem.item_shopdetail_good_price = ViewHolder.get(
                        arg1, R.id.item_shopdetail_good_price);

                arg1.setTag(detailGoodsItem);
            } else {
                detailGoodsItem = (ShopDetailGoodsItem) arg1.getTag();
            }
            ImageLoaderUtil.Load2(datas.get(arg0).getCover(),
                    detailGoodsItem.item_shopdetail_good_iv,
                    R.drawable.error_iv2);
            StrUtils.SetTxt(detailGoodsItem.item_shopdetail_good_name, datas
                    .get(arg0).getTitle());
            StrUtils.SetTxt(detailGoodsItem.item_shopdetail_good_price,
                    StrUtils.SetTextForMony(datas.get(arg0).getSell_price()) + "元");
            return arg1;
        }

        class ShopDetailGoodsItem {
            ImageView item_shopdetail_good_iv;
            TextView item_shopdetail_good_name;
            TextView item_shopdetail_good_price;

        }
    }

    @Override
    protected void NetConnect() {
        NetError.setVisibility(View.GONE);

        // 无网络==》有网络
        if (MyData == null)
            IData();

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
            case R.id.shopdetail_sou_iv://搜索
                PromptManager.SkipActivity(BaseActivity, new Intent(BaseActivity, AShopGoodSou.class).putExtra("Sellid", MyData.getBase().getId()).putExtra("Sellname", MyData.getBase().getSeller_name()));
                BaseApplication.GetInstance().setShopSouRecommend(MyData.getDiy());
                break;
            case R.id.shop_detail_goods_chang_iv:
                IsShowLs = !IsShowLs;
                shop_detail_goods_chang_iv.setImageResource(IsShowLs ? R.drawable.shop_good_iv_ls : R.drawable.shop_good_iv_gr);
                shop_detail_downgoods_ls.setVisibility(IsShowLs ? View.VISIBLE : View.GONE);
                shop_detail_downgoods_gr.setVisibility(!IsShowLs ? View.VISIBLE : View.GONE);

//              activivty_shopdetail_outlay.smoothScrollTo(0, 20);
                break;
            case R.id.shop_detail_brand:// 品牌列表
                shop_detail_brand.setTextColor(getResources().getColor(
                        R.color.app_fen));
                shop_detail_ziying.setTextColor(getResources().getColor(
                        R.color.app_black));
                IsBrandStatue = true;

                FristSelect();

                if (CacheLsDatas_Brand == null || CacheLsDatas_Brand.size() == 0) {
                    CurrentPage = 1;
                    CurrentCategory_Id = "0";
                    GetList(CurrentPage, CurrentCategory_Id, LOAD_REFRESHING);
                } else {

                    CurrentPage = StrUtils.ShopDetailPage(CacheLsDatas_Brand.size());//(int)((CacheLsDatas_Brand.size()+10)/10) ;
                    CurrentCategory_Id = "0";

                    downGoodsAp.FrashView(CacheLsDatas_Brand);
                }
                shop_out_scrollview.smoothScrollTo(0, 20);
                break;
            case R.id.shop_detail_ziying:// 自营的按钮
                shop_detail_ziying.setTextColor(getResources().getColor(
                        R.color.app_fen));
                shop_detail_brand.setTextColor(getResources().getColor(
                        R.color.app_black));
                IsBrandStatue = false;
                FristSelect();

                if (CacheLsDatas_ZiYing == null || CacheLsDatas_ZiYing.size() == 0) {
                    CurrentPage = 1;
                    CurrentCategory_Id = "0";
                    GetList(CurrentPage, CurrentCategory_Id, LOAD_REFRESHING);
                } else {
                    CurrentPage = StrUtils.ShopDetailPage(CacheLsDatas_ZiYing.size());//(int)((CacheLsDatas_ZiYing.size()+10)/10) ;
                    CurrentCategory_Id = "0";
                    downGoodsAp.FrashView(CacheLsDatas_ZiYing);
                }
                shop_out_scrollview.smoothScrollTo(0, 20);
                break;

            case R.id.shopdetail_shop_zizhi:
                List<BShopGoods> DATA = shopBrandsLsAp.GetRsource();
                BaseApplication.GetInstance().setZiYingShop_To_Ls(DATA);
//                PromptManager.SkipActivity(BaseActivity, new Intent(BaseActivity,
//                        ALookAptitude.class));
                break;

            //case R.id.shopdetail_shop_guanzhu_bt:// 关注按钮
            case R.id.rl_shop_detail_shoucang_log:
                GuanZhuSho(baseBcBComment.getId(), !IsCollect);
                break;
            case R.id.rl_shop_detail_contact_log:// 联系店主
                if (!StrUtils.isEmpty(MyData.getBase().getId()))
                    PromptManager.SkipActivity(
                            BaseActivity,
                            new Intent(BaseActivity, AChatLoad.class)
                                    .putExtra(AChatLoad.Tage_TageId,
                                            MyData.getBase().getId())
                                    .putExtra(AChatLoad.Tage_Name,
                                            MyData.getBase().getSeller_name())
                                    .putExtra(AChatLoad.Tage_Iv,
                                            MyData.getBase().getAvatar()));

                break;

            case R.id.shopdetail_nodata_lay:
                IData();
                break;
            case R.id.ll_shopdetail_shop_look_show:// 查看show

                if (null != MyData && !StrUtils.isEmpty(MyData.getBase().getId())) {
                    Intent intent = new Intent(BaseActivity, ARecyclerOtherShow.class);
                    intent.putExtra(BaseKey_Bean, new BComment(MyData.getBase()
                            .getId(), MyData.getBase().getCover(), MyData.getBase()
                            .getAvatar(), MyData.getBase().getSeller_name(), MyData
                            .getBase().getIs_brand()));
                    PromptManager.SkipActivity(BaseActivity, intent);
                }
                break;


            default:
                break;
        }
    }




    @Override
    protected void InItBundle(Bundle bundle) {
    }

    @Override
    protected void SaveBundle(Bundle bundle) {
    }

}
