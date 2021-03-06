package io.vtown.WeiTangApp.ui.title;

import java.util.HashMap;

import com.android.volley.Request.Method;

import de.greenrobot.event.EventBus;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import io.vtown.WeiTangApp.R;
import io.vtown.WeiTangApp.bean.bcomment.BComment;
import io.vtown.WeiTangApp.bean.bcomment.BUser;
import io.vtown.WeiTangApp.bean.bcomment.news.BMessage;
import io.vtown.WeiTangApp.comment.contant.Constants;
import io.vtown.WeiTangApp.comment.contant.PromptManager;
import io.vtown.WeiTangApp.comment.contant.Spuit;
import io.vtown.WeiTangApp.comment.util.StrUtils;
import io.vtown.WeiTangApp.ui.ATitleBase;

/**
 * @author 作者 易惠华 yihuihua@v-town.cc
 * @version 创建时间：2016-5-31 下午2:18:38 申请代理页面
 */
public class AApplyProxy extends ATitleBase {

    /**
     * 申请代理规则
     */
    private TextView tv_apply_proxy_rule;
    /**
     * 自我介绍输入框
     */
    private EditText et_introduce_youself;
    /**
     * 提交按钮
     */
    private Button btn_apply_proxy_confirm;
    /**
     * 品牌店铺店铺的id
     */
    private String apply_id;// seller_id
    private int CurrentPostion;

    @Override
    protected void InItBaseView() {
        setContentView(R.layout.activity_apply_proxy);
        IBunddl();
        SetTitleHttpDataLisenter(this);
        IView();
    }

    private void IBunddl() {
        if (null == getIntent().getExtras()
                || !getIntent().getExtras().containsKey("brandid"))
            BaseActivity.finish();
        CurrentPostion = getIntent().getIntExtra("postion", -1);
        apply_id = getIntent().getStringExtra("brandid");

        //
    }

    private void IView() {
        tv_apply_proxy_rule = (TextView) findViewById(R.id.tv_apply_proxy_rule);
        et_introduce_youself = (EditText) findViewById(R.id.et_introduce_youself);
        btn_apply_proxy_confirm = (Button) findViewById(R.id.btn_apply_proxy_confirm);

        btn_apply_proxy_confirm.setOnClickListener(this);
    }

    @Override
    protected void InitTile() {
        SetTitleTxt(getResources().getString(R.string.apply_proxy));
    }

    @Override
    protected void DataResult(int Code, String Msg, BComment Data) {
        PromptManager.ShowCustomToast(BaseContext, "申请已提交，请耐心等候系统审核");
        EventBus.getDefault().post(
                new BMessage(BMessage.Tage_Brand_Apply_Statue));

        BMessage MyBmensage=new BMessage(BMessage.Tage_Updata_Brand_list);
        MyBmensage.setBrandApplyPostion(CurrentPostion);
        EventBus.getDefault().post(MyBmensage);
        BaseActivity.finish();
    }

    @Override
    protected void DataError(String error, int LoadType) {
        PromptManager.ShowCustomToast(BaseContext, error);
        BaseActivity.finish();
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
            case R.id.btn_apply_proxy_confirm:
                if (StrUtils.EditTextIsEmPty(et_introduce_youself)) {
                    PromptManager.ShowCustomToast(BaseContext, "请输入申请理由");
                    return;
                }
                if (CheckNet(BaseContext)) return;
                BrandApplay(apply_id, et_introduce_youself.getText().toString()
                        .trim());
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

    private void BrandApplay(String apply_id, String ntro) {
        PromptManager.showLoading(BaseContext);
        BUser mBUser = Spuit.User_Get(BaseActivity);
        HashMap<String, String> SelectMap = new HashMap<String, String>();
        SelectMap.put("seller_id", mBUser.getSeller_id());
        SelectMap.put("apply_id", apply_id);
        SelectMap.put("intro", ntro);
        FBGetHttpData(SelectMap, Constants.BrandApplay, Method.POST, 1,
                LOAD_INITIALIZE);

    }

}
