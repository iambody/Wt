package io.vtown.WeiTangApp.ui.title.myhome;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.volley.Request;
import com.mob.tools.utils.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.vtown.WeiTangApp.R;
import io.vtown.WeiTangApp.bean.bcomment.BComment;
import io.vtown.WeiTangApp.bean.bcomment.BUser;
import io.vtown.WeiTangApp.bean.bcomment.easy.BLLevelName;
import io.vtown.WeiTangApp.bean.bcomment.easy.wallet.BLAPropertyList;
import io.vtown.WeiTangApp.bean.bcomment.easy.zhuanqu.BLIntegralType;
import io.vtown.WeiTangApp.bean.bcomment.new_three.integral_detail.BCIntegralDetail;
import io.vtown.WeiTangApp.bean.bcomment.new_three.integral_detail.BLIntegralDetails;
import io.vtown.WeiTangApp.comment.contant.CacheUtil;
import io.vtown.WeiTangApp.comment.contant.Constants;
import io.vtown.WeiTangApp.comment.contant.PromptManager;
import io.vtown.WeiTangApp.comment.contant.Spuit;
import io.vtown.WeiTangApp.comment.util.DimensionPixelUtil;
import io.vtown.WeiTangApp.comment.util.StrUtils;
import io.vtown.WeiTangApp.comment.view.custom.CompleteListView;
import io.vtown.WeiTangApp.comment.view.listview.LListView;
import io.vtown.WeiTangApp.ui.ATitleBase;
import io.vtown.WeiTangApp.ui.title.center.myinvitecode.AMyInviteCode;
import io.vtown.WeiTangApp.ui.title.center.myorder.ACenterMyOrderDetail;
import io.vtown.WeiTangApp.ui.ui.ANewHome;

/**
 * Created by Yihuihua on 2016/10/12.
 * 积分明细页面
 */

public class AIntegralDetail extends ATitleBase implements LListView.IXListViewListener {

    private View integral_detail_nodata_lay;
    private LListView integral_detail_list;
    private BUser mUser;
    private String lastid = "";

    //类型：不传代表全部1系统赠送2每日签到3邀请下级注册4下级激活5自己购买商品6下线购买商品
    private final static String TYPE_ALL = "";
    private final static String TYPE_SYSTEM = "1";
    private final static String TYPE_PAST = "2";
    private final static String TYPE_INVITE = "3";
    private final static String TYPE_ACTIVATION = "4";
    private final static String TYPE_BUY_OWN = "5";
    private final static String TYPE_BUY_FRIEND = "6";
    private final static String TYPE_CONSUME = "7";
    private final static String TYPE_SHARE = "8";
    private String Current_Type = TYPE_ALL;
    private PopupWindow popupWindow;
    private IntegralOutsideAdapter mAdapter;
    private List<BCIntegralDetail> mDatas;
    private List<BLIntegralType> type_datas = new ArrayList<BLIntegralType>();



    @Override
    protected void InItBaseView() {
        setContentView(R.layout.activity_integral_detail);
        SetTitleHttpDataLisenter(this);
        mUser = Spuit.User_Get(BaseContext);
        IView();
        ITypeCache();
        ICache();
        IvData();
        IData(Current_Type, LOAD_INITIALIZE);
    }

    private void ITypeCache() {
        String type_lvs = CacheUtil.Integral_Type_Get(BaseContext);
        if (!StrUtils.isEmpty(type_lvs)) {

            type_datas = JSON.parseArray(type_lvs, BLIntegralType.class);
        }
    }

    private void IvData() {
        FBGetHttpData(new HashMap<String, String>(), Constants.Integral_Type, Request.Method.GET, 1, LOAD_INITIALIZE);
    }


    private void IView() {
        integral_detail_list = (LListView) findViewById(R.id.integral_detail_list);
        integral_detail_nodata_lay = findViewById(R.id.integral_detail_nodata_lay);
        integral_detail_list.setPullLoadEnable(true);
        integral_detail_list.setPullRefreshEnable(true);
        integral_detail_list.setXListViewListener(this);
        integral_detail_list.hidefoot();
        mAdapter = new IntegralOutsideAdapter(R.layout.item_integral_detail_outside);
        integral_detail_list.setAdapter(mAdapter);
        integral_detail_nodata_lay.setOnClickListener(this);
    }

    private void ICache() {
        String integral_cache = CacheUtil.Integral_Detail_Get(BaseContext);
        if (StrUtils.isEmpty(integral_cache)) {
            PromptManager.showtextLoading(BaseContext, getResources().getString(R.string.xlistview_header_hint_loading));
        } else {
            try {
                mDatas = JSON.parseArray(integral_cache, BCIntegralDetail.class);
            } catch (Exception e) {
                return;
            }

            if (TYPE_ALL.equals(Current_Type)) {
                mAdapter.FreshData(mDatas);
            }
        }
    }

    private void IData(String type, int loadtype) {
        HashMap<String, String> map = new HashMap<>();
        map.put("member_id", mUser.getMember_id());//"10014952"
        map.put("type", type);
        map.put("page_num", Constants.PageSize + "");
        map.put("last_id", lastid);
        map.put("api_version", "3.1.3");
        FBGetHttpData(map, Constants.Integral_Detail, Request.Method.GET, 0, loadtype);
    }

    @Override
    protected void InitTile() {
        SetTitleTxt(getString(R.string.integral_title_all));
        SetRightText(getResources().getString(R.string.txt_filter));
        if(type_datas.size()>0){
            right_txt.setVisibility(View.VISIBLE);
        }else{
            right_txt.setVisibility(View.GONE);
        }
        right_txt.setOnClickListener(this);
    }

    @Override
    protected void DataResult(int Code, String Msg, BComment Data) {

        switch (Data.getHttpResultTage()){
            case 0:
                switch (Data.getHttpLoadType()) {
                    case LOAD_INITIALIZE:
                        if (StrUtils.isEmpty(Data.getHttpResultStr())) {
                            integral_detail_list.setVisibility(View.GONE);
                            integral_detail_nodata_lay.setVisibility(View.VISIBLE);
                            ShowErrorIv(R.drawable.pic_jifenmingxi);
                            integral_detail_nodata_lay.setClickable(false);
                            mAdapter.FreshData(new ArrayList<BCIntegralDetail>());

                            if (TYPE_ALL.equals(Current_Type)) {
                                ShowErrorCanLoad(getResources().getString(R.string.null_integral_detail));
                                CacheUtil.Integral_Detail_Save(BaseContext, Data.getHttpResultStr());
                            }
                            if (TYPE_SYSTEM.equals(Current_Type)) {
                                ShowErrorCanLoad(getResources().getString(R.string.null_integral_system));
                            }
                            if (TYPE_PAST.equals(Current_Type)) {
                                ShowErrorCanLoad(getResources().getString(R.string.null_integral_past));
                            }
                            if (TYPE_ACTIVATION.equals(Current_Type)) {
                                ShowErrorCanLoad(getResources().getString(R.string.null_integral_activation));
                            }
                            if (TYPE_INVITE.equals(Current_Type)) {
                                ShowErrorCanLoad(getResources().getString(R.string.null_integral_invite));

                            }
                            if (TYPE_BUY_OWN.equals(Current_Type)) {
                                ShowErrorCanLoad(getResources().getString(R.string.null_integral_own_buy));
                            }
                            if (TYPE_BUY_FRIEND.equals(Current_Type)) {
                                ShowErrorCanLoad(getResources().getString(R.string.null_integral_friend_buy));
                            }

                            if (TYPE_CONSUME.equals(Current_Type)) {
                                ShowErrorCanLoad(getResources().getString(R.string.null_integral_consume));

                            }

                            if (TYPE_SHARE.equals(Current_Type)) {
                                ShowErrorCanLoad(getResources().getString(R.string.null_integral_share));

                            }
                            return;
                        }
                        integral_detail_list.setVisibility(View.VISIBLE);
                        integral_detail_nodata_lay.setVisibility(View.GONE);
                        mDatas = new ArrayList<BCIntegralDetail>();
                        mDatas = JSON.parseArray(Data.getHttpResultStr(), BCIntegralDetail.class);
                        mAdapter.FreshData(mDatas);
                        if (TYPE_ALL.equals(Current_Type)) {
                            CacheUtil.Integral_Detail_Save(BaseContext, Data.getHttpResultStr());
                        }
                        List<BLIntegralDetails> data = getAllIntegralDetailList(mDatas);
                        lastid = data.get(data.size() - 1).getId();

                        if (data.size() == Constants.PageSize) {
                            integral_detail_list.ShowFoot();
                            integral_detail_list.setPullLoadEnable(true);
                        }

                        if (data.size() < Constants.PageSize) {
                            integral_detail_list.hidefoot();
                            integral_detail_list.setPullLoadEnable(false);
                        }

                        break;

                    case LOAD_REFRESHING:
                        integral_detail_list.stopRefresh();
                        if (StrUtils.isEmpty(Data.getHttpResultStr())) {
                            PromptManager.ShowCustomToast(BaseContext, getString(R.string.no_new_integral_detail));
                            // ShowErrorCanLoad(getResources().getString(R.string.null_integral_detail));
                            return;
                        }


                        mDatas = JSON.parseArray(Data.getHttpResultStr(), BCIntegralDetail.class);
                        mAdapter.FreshData(mDatas);
                        List<BLIntegralDetails> data1 = getAllIntegralDetailList(mDatas);
                        lastid = data1.get(data1.size() - 1).getId();
                        if (data1.size() == Constants.PageSize) {
                            integral_detail_list.ShowFoot();
                            integral_detail_list.setPullLoadEnable(true);
                        }

                        if (data1.size() < Constants.PageSize) {
                            integral_detail_list.hidefoot();
                            integral_detail_list.setPullLoadEnable(false);
                        }
                        break;

                    case LOAD_LOADMOREING:
                        integral_detail_list.stopLoadMore();
                        if (StrUtils.isEmpty(Data.getHttpResultStr())) {
                            PromptManager.ShowCustomToast(BaseContext, getString(R.string.no_more_integral_detail));
                            // ShowErrorCanLoad(getResources().getString(R.string.null_integral_detail));
                            return;
                        }
                        mDatas = JSON.parseArray(Data.getHttpResultStr(), BCIntegralDetail.class);
                        if (mDatas.get(0).getMonth().equals(mAdapter.GetApData().get(mAdapter.getCount() - 1).getMonth())) {
                            mAdapter.MergeFrashData(mDatas);
                        } else {
                            mAdapter.FreshDataAll(mDatas);
                        }

                        List<BLIntegralDetails> data2 = getAllIntegralDetailList(mDatas);
                        lastid = data2.get(data2.size() - 1).getId();
                        if (data2.size() == Constants.PageSize) {
                            integral_detail_list.ShowFoot();
                            integral_detail_list.setPullLoadEnable(true);
                        }

                        if (data2.size() < Constants.PageSize) {
                            integral_detail_list.hidefoot();
                            integral_detail_list.setPullLoadEnable(false);
                        }
                        break;
                }
                break;

            case 1:

                type_datas = JSON.parseArray(Data.getHttpResultStr(),BLIntegralType.class);
                CacheUtil.Integral_Type_Save(BaseContext,Data.getHttpResultStr());
                if(type_datas.size()>0){
                    right_txt.setVisibility(View.VISIBLE);
                }
                break;
        }

    }

    private List<BLIntegralDetails> getAllIntegralDetailList(List<BCIntegralDetail> datas) {
        List<BLIntegralDetails> details = new ArrayList<BLIntegralDetails>();
        for (int i = 0; i < datas.size(); i++) {
            details.addAll(datas.get(i).getList());
        }
        return details;
    }

    @Override
    protected void DataError(String error, int LoadType) {
        switch (LoadType) {
            case LOAD_INITIALIZE:
                integral_detail_list.setVisibility(View.GONE);
                integral_detail_nodata_lay.setVisibility(View.VISIBLE);
                ShowErrorIv(R.drawable.pic_jifenmingxi);
                integral_detail_nodata_lay.setClickable(true);
                ShowErrorCanLoad(error);

                break;

            case LOAD_REFRESHING:
                integral_detail_list.stopRefresh();
                break;

            case LOAD_LOADMOREING:
                integral_detail_list.stopLoadMore();
                break;
        }
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
            case R.id.right_txt:
                if (CheckNet(BaseContext)) return;
                IPopupWindow(V);
                break;

            case R.id.tv_all_integral://全部
                IntegralSwitch(TYPE_ALL,getResources().getString(R.string.integral_title_all));
                break;

            case R.id.integral_detail_nodata_lay:
                if (CheckNet(BaseContext)) return;

                lastid = "";
                IData(Current_Type, LOAD_INITIALIZE);


                break;

        }

    }

    private void IntegralSwitch(String type,String titlename){
        if (!type.equals(Current_Type)) {
            Current_Type = type;
            SetTitleTxt(titlename);
            lastid = "";
            IData(Current_Type, LOAD_INITIALIZE);
            mAdapter.Clearn();
            integral_detail_nodata_lay.setVisibility(View.GONE);
            integral_detail_list.hidefoot();
            PromptManager.showtextLoading(BaseContext, getResources().getString(R.string.xlistview_header_hint_loading));
        }
        popupWindow.dismiss();
    }

    @Override
    protected void InItBundle(Bundle bundle) {

    }

    @Override
    protected void SaveBundle(Bundle bundle) {

    }

    @Override
    public void onRefresh() {
        lastid = "";
        IData(Current_Type, LOAD_REFRESHING);
    }

    @Override
    public void onLoadMore() {
        IData(Current_Type, LOAD_LOADMOREING);
    }

    /**
     * 控制PopupWindow
     */
    private void IPopupWindow(View V) {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            showPop(V);
        }
    }

    private void showPop(View V) {

        View view = View.inflate(BaseContext, R.layout.pop_integral_filter,
                null);
        LinearLayout ll_integral_type_layout = (LinearLayout) view.findViewById(R.id.ll_integral_type_layout);
        TextView tv_all_integral = (TextView) view.findViewById(R.id.tv_all_integral);
        tv_all_integral.setOnClickListener(this);

        LinearLayout.LayoutParams contentParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
        for(int i = 0; i < type_datas.size();i++){
            TextView textView = new TextView(BaseContext);
            View line = new View(BaseContext);
            textView.setText(type_datas.get(i).getName());
            textView.setTextColor(getResources().getColor(R.color.white));
            textView.setClickable(true);
            textView.setLayoutParams(contentParams);
             textView.setPadding(5, DimensionPixelUtil.dip2px(BaseContext, 8), 5, DimensionPixelUtil.dip2px(BaseContext, 8));
            textView.setGravity(Gravity.CENTER);
            line.setBackgroundResource(R.color.white);
            line.setLayoutParams(lineParams);
            ll_integral_type_layout.addView(textView);
            ll_integral_type_layout.addView(line);
            textView.setOnClickListener(new OnPopClickListener(i));
}

//        TextView tv_system = (TextView) view.findViewById(R.id.tv_system);
//        TextView tv_past = (TextView) view.findViewById(R.id.tv_past);
//        TextView tv_invite_register = (TextView) view.findViewById(R.id.tv_invite_register);
//        TextView tv_friend_activation = (TextView) view.findViewById(R.id.tv_friend_activation);
//        TextView tv_own_buy = (TextView) view.findViewById(R.id.tv_own_buy);
//        TextView tv_friend_buy = (TextView) view.findViewById(R.id.tv_friend_buy);
//        TextView tv_consume = (TextView) view.findViewById(R.id.tv_consume);
//        TextView tv_share = (TextView) view.findViewById(R.id.tv_share);



//        tv_system.setOnClickListener(this);
//        tv_past.setOnClickListener(this);
//        tv_invite_register.setOnClickListener(this);
//        tv_friend_activation.setOnClickListener(this);
//        tv_own_buy.setOnClickListener(this);
//        tv_friend_buy.setOnClickListener(this);
//        tv_consume.setOnClickListener(this);
//        tv_share.setOnClickListener(this);


        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        popupWindow.setBackgroundDrawable(dw);
        popupWindow.showAsDropDown(V, 0, 0);
    }


    class IntegralOutsideAdapter extends BaseAdapter {

        private int ResourseId;
        private LayoutInflater inflater;
        private List<BCIntegralDetail> datas = new ArrayList<BCIntegralDetail>();

        public IntegralOutsideAdapter(int ResourseId) {
            super();
            this.ResourseId = ResourseId;
            this.inflater = LayoutInflater.from(BaseContext);
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        public List<BCIntegralDetail> GetApData() {
            return this.datas;
        }

        @Override
        public Object getItem(int position) {
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void FreshData(List<BCIntegralDetail> data) {
            this.datas.clear();
            this.datas = data;
            this.notifyDataSetChanged();
        }

        public void FreshDataAll(List<BCIntegralDetail> more_data) {
            this.datas.addAll(more_data);
            this.notifyDataSetChanged();
        }

        public void Clearn() {
            datas = new ArrayList<BCIntegralDetail>();
            this.notifyDataSetChanged();
        }

        public void MergeFrashData(List<BCIntegralDetail> dattaa) {

            this.datas.get(getCount() - 1).getList().addAll(dattaa.get(0).getList());
            for (int i = 1; i < dattaa.size(); i++) {
                this.datas.add(dattaa.get(i));
            }
            this.notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            IntegralOutsideHoler holer = null;
            if (null == convertView) {
                holer = new IntegralOutsideHoler();
                convertView = inflater.inflate(ResourseId, null);
                holer.integral_month = (TextView) convertView.findViewById(R.id.integral_month);
                holer.lv_integral_list_outside = (CompleteListView) convertView.findViewById(R.id.lv_integral_list_outside);
                convertView.setTag(holer);
            } else {
                holer = (IntegralOutsideHoler) convertView.getTag();
            }
            StrUtils.SetTxt(holer.integral_month, datas.get(position).getMonth());
           final IntegralInsideAdapter integralInsideAdapter = new IntegralInsideAdapter(R.layout.item_integral_detail, datas.get(position).getList());
            holer.lv_integral_list_outside.setAdapter(integralInsideAdapter);

            return convertView;
        }
    }


    class IntegralInsideAdapter extends BaseAdapter {
        private int ResourseId;
        private List<BLIntegralDetails> datas = new ArrayList<BLIntegralDetails>();
        private LayoutInflater inflater;

        public IntegralInsideAdapter(int ResourseId, List<BLIntegralDetails> datas) {
            super();
            this.ResourseId = ResourseId;
            this.datas = datas;
            this.inflater = LayoutInflater.from(BaseContext);
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            IntegralInsideHolder holder = null;
            if (convertView == null) {
                holder = new IntegralInsideHolder();
                convertView = inflater.inflate(ResourseId, null);
                holder.tv_integral_day = (TextView) convertView.findViewById(R.id.tv_integral_day);
                holder.tv_integral_time = (TextView) convertView.findViewById(R.id.tv_integral_time);
                holder.tv_integral_content = (TextView) convertView.findViewById(R.id.tv_integral_content);
                holder.tv_integral_seller_order_sn = (TextView) convertView.findViewById(R.id.tv_integral_seller_order_sn);
                holder.ll_integral_point_and_status = (LinearLayout) convertView.findViewById(R.id.ll_integral_point_and_status);
                holder.tv_integral_point = (TextView) convertView.findViewById(R.id.tv_integral_point);
                holder.tv_integral_status = (TextView) convertView.findViewById(R.id.tv_integral_status);
                holder.iv_dot_integral = (ImageView)convertView.findViewById(R.id.iv_dot_integral) ;
                convertView.setTag(holder);
            } else {
                holder = (IntegralInsideHolder) convertView.getTag();
            }
            BLIntegralDetails data = datas.get(position);
            StrUtils.SetTxt(holder.tv_integral_day, data.getDateStr());
            StrUtils.SetTxt(holder.tv_integral_time, data.getDate());


            if(TYPE_ACTIVATION.equals(data.getType())){
                StrUtils.SetTxt(holder.tv_integral_content, data.getInvite_member_id()+"升级积分");
            }else if(TYPE_BUY_FRIEND.equals(data.getType())){
                StrUtils.SetTxt(holder.tv_integral_content, data.getBy_member_id()+"购物积分");
            }else if(TYPE_INVITE.equals(data.getType())){
                StrUtils.SetTxt(holder.tv_integral_content, data.getInvite_member_id()+"注册积分");
            }else if(TYPE_BUY_OWN.equals(data.getType())){
                StrUtils.SetTxt(holder.tv_integral_content, "我的"+data.getTitle());
            }else{
                StrUtils.SetTxt(holder.tv_integral_content, data.getTitle());
            }

            if(TYPE_BUY_OWN.equals(data.getType())){
                holder.tv_integral_seller_order_sn.setVisibility(View.VISIBLE);
                StrUtils.SetTxt(holder.tv_integral_seller_order_sn,"订单号："+data.getSeller_order_sn());
            }else{
                holder.tv_integral_seller_order_sn.setVisibility(View.GONE);
            }

            int point = Integer.parseInt(data.getPoint());
            if (point > 9999 && point < 100000) {
                holder.tv_integral_point.setTextSize(12);
            } else if (point > 999 && point < 10000) {
                holder.tv_integral_point.setTextSize(14);
            } else if (point > 99999) {
                holder.tv_integral_point.setTextSize(11);
            } else {
                holder.tv_integral_point.setTextSize(15);
            }
            if (TYPE_CONSUME.equals(data.getType())) {
                holder.ll_integral_point_and_status.setBackgroundResource(R.drawable.shape_integral_bg_green);
                StrUtils.SetTxt(holder.tv_integral_point, "-" + point);
                holder.iv_dot_integral.setImageDrawable(getResources().getDrawable(R.drawable.lvdian));
                holder.tv_integral_status.setVisibility(View.GONE);
            } else {
                holder.ll_integral_point_and_status.setBackgroundResource(R.drawable.shape_integral_bg_fen);
                StrUtils.SetTxt(holder.tv_integral_point, "+" + point);
                holder.iv_dot_integral.setImageDrawable(getResources().getDrawable(R.drawable.huangdian));
                holder.tv_integral_status.setVisibility(View.VISIBLE);
            }

            int status = Integer.parseInt(data.getStatus());
            String status_str = "";
            switch (status) {
                case 1:
                    status_str = "正在到账";
                    //holder.iv_dot_integral.setImageDrawable(getResources().getDrawable(R.drawable.huangdian));
                    holder.tv_integral_point.getPaint().setFlags(0);
                    break;
                case 2:
                    status_str = "到账成功";
                    holder.tv_integral_point.getPaint().setFlags(0);
                    break;
                case 3:
                    status_str = "到账失败";
                    holder.iv_dot_integral.setImageDrawable(getResources().getDrawable(R.drawable.lvdian));
                    holder.ll_integral_point_and_status.setBackgroundResource(R.drawable.shape_intergral_bg_gray);
                    StrUtils.SetTxt(holder.tv_integral_point, point+"");
                    holder.tv_integral_point.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    break;
            }
            StrUtils.SetTxt(holder.tv_integral_status, status_str);

//            holder.tv_integral_seller_order_sn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    PromptManager.SkipActivity(BaseActivity,new Intent(BaseContext, ACenterMyOrderDetail.class));
//                }
//            });

            return convertView;
        }
    }


    class IntegralInsideHolder {
        TextView tv_integral_day, tv_integral_time, tv_integral_content, tv_integral_point, tv_integral_status,tv_integral_seller_order_sn;
        LinearLayout ll_integral_point_and_status;
        ImageView iv_dot_integral;
    }

    class IntegralOutsideHoler {
        TextView integral_month;
        CompleteListView lv_integral_list_outside;
    }


    class OnPopClickListener implements View.OnClickListener {
        private int clickposition;

        public OnPopClickListener(int position) {
            clickposition = position;
        }

        @Override
        public void onClick(View v) {
            IntegralSwitch(type_datas.get(clickposition).getId()+"",type_datas.get(clickposition).getName());
        }
    }
}
