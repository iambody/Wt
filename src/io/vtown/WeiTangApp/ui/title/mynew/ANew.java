package io.vtown.WeiTangApp.ui.title.mynew;

import io.vtown.WeiTangApp.R;
import io.vtown.WeiTangApp.adapter.ChatHistoryAdapter;
import io.vtown.WeiTangApp.bean.bcomment.BComment;
import io.vtown.WeiTangApp.bean.bcomment.BLComment;
import io.vtown.WeiTangApp.bean.bcomment.BUser;
import io.vtown.WeiTangApp.bean.bcomment.news.BMessage;
import io.vtown.WeiTangApp.comment.contant.CacheUtil;
import io.vtown.WeiTangApp.comment.contant.Constants;
import io.vtown.WeiTangApp.comment.contant.LogUtils;
import io.vtown.WeiTangApp.comment.contant.PromptManager;
import io.vtown.WeiTangApp.comment.contant.Spuit;
import io.vtown.WeiTangApp.comment.util.DateUtils;
import io.vtown.WeiTangApp.comment.util.StrUtils;
import io.vtown.WeiTangApp.comment.util.ViewHolder;
import io.vtown.WeiTangApp.comment.util.image.ImageLoaderUtil;
import io.vtown.WeiTangApp.comment.view.custom.CompleteListView;
import io.vtown.WeiTangApp.comment.view.listview.LListView;
import io.vtown.WeiTangApp.comment.view.listview.LListView.IXListViewListener;
import io.vtown.WeiTangApp.event.interf.IDialogResult;
import io.vtown.WeiTangApp.ui.ATitleBase;
import io.vtown.WeiTangApp.ui.comment.im.AChatInf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.volley.Request.Method;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.exceptions.EaseMobException;

import de.greenrobot.event.EventBus;

/**
 * @author 作者 大兔兔 wangyongkui@v-town.cc
 * @version 创建时间：2016-6-12 下午9:20:46
 */
public class ANew extends ATitleBase {

    /**
     * 消息列表
     */
    private CompleteListView mynew_newlist;
    /**
     * 对应的Ap
     */
    private MyMew_Ap myMew_Ap;
    /**
     * 用户信息
     */
    private BUser user_Get;
    // IM会话记录列表
    private ChatHistoryAdapter ImHistoryAdapter;
    // ls
    private CompleteListView mynew_imlist;
    // 所有的会话列表
    private List<EMConversation> conversationList = new ArrayList<EMConversation>();
    // 微糖 小助手的一个EMConversation
    private EMConversation HeplerConversation = null;
    private LinearLayout new_zhushou_lay;
    private TextView item_my_new_content;
    private TextView new_zhushou_time;

    @Override
    protected void InItBaseView() {
        setContentView(R.layout.activity_mynew);
        EventBus.getDefault().register(this, "NewReciver", BMessage.class);
        user_Get = Spuit.User_Get(BaseContext);
        BaseV();
        SetTitleHttpDataLisenter(this);
        ICache();
        IData(LOAD_INITIALIZE);
    }

    /**
     * 检查是否包含缓存
     */
    private void ICache() {
        if (!StrUtils.isEmpty(CacheUtil.New_Get(BaseContext))) {// 存在缓存

            List<BLComment> comments = new ArrayList<BLComment>();
            try {
                comments = JSON.parseArray(CacheUtil.New_Get(BaseContext),
                        BLComment.class);
                myMew_Ap.Refrsh(comments);
                mynew_imlist.setVisibility(View.VISIBLE);
                new_zhushou_lay.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                mynew_imlist.setVisibility(View.VISIBLE);
                new_zhushou_lay.setVisibility(View.VISIBLE);
                PromptManager.showtextLoading(BaseContext, getResources()
                        .getString(R.string.loading));
                return;
            }

        } else {// 没有缓存
            PromptManager.showtextLoading(BaseContext, getResources()
                    .getString(R.string.loading));
        }
    }

    /* 获取接口的数据 */
    private void IData(int LoadType) {
        if (LoadType == LOAD_LOADMOREING) {
            PromptManager.showtextLoading(BaseContext, getResources()
                    .getString(R.string.loading));
        }
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("member_id", user_Get.getId());
        FBGetHttpData(map, Constants.My_New_ls, Method.GET, 0, LoadType);
    }

    /**
     * 清空消息
     */
    private void Delet() {
        PromptManager.showtextLoading(BaseContext, "努力清理中..");
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("member_id", user_Get.getId());

        FBGetHttpData(map, Constants.My_Item_New_Delet, Method.DELETE, 40,
                LOAD_LOADMOREING);
    }

    /**
     * 单个删除消息
     */
    private void DeletByType(String SourceType) {
        // NewDeletByTypes
        SetTitleHttpDataLisenter(this);
        PromptManager.showtextLoading(BaseContext, "删除中....");
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("member_id", user_Get.getMember_id());
        map.put("source_type", SourceType);
        FBGetHttpData(map, Constants.NewDeletByType, Method.DELETE, 31,
                LOAD_REFRESHING);

    }

    //
    private void BaseV() {
        new_zhushou_lay = (LinearLayout) findViewById(R.id.new_zhushou_lay);
        item_my_new_content = (TextView) findViewById(R.id.item_my_new_content);
        new_zhushou_time = (TextView) findViewById(R.id.new_zhushou_time);
        new_zhushou_lay.setOnClickListener(this);

        conversationList.addAll(loadConversationWithRecentChat());

        // IM的view
        mynew_imlist = (CompleteListView) findViewById(R.id.mynew_imlist);
        ImHistoryAdapter = new ChatHistoryAdapter(BaseContext, conversationList);
        mynew_imlist.setAdapter(ImHistoryAdapter);
        mynew_imlist.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                EMConversation conversation = (EMConversation) ImHistoryAdapter
                        .getItem(position);
                EMMessage latmessage = conversation.getLastMessage();
                EventBus.getDefault().post(new BMessage(BMessage.IM_MSG_READ));
                Intent intent = new Intent(BaseActivity, AChatInf.class);
                try {
                    if (latmessage.direct == EMMessage.Direct.SEND) {// EMMessageDirectionSend
                        // holder.name.setText(ReciverName);
                        intent.putExtra("tagname", conversation.getUserName());
                        String ReciverName = latmessage
                                .getStringAttribute("extReceiveNickname");
                        intent.putExtra("title", latmessage
                                .getStringAttribute("extReceiveNickname"));
                        intent.putExtra("iv", latmessage
                                .getStringAttribute("extReceiveHeadUrl"));

                        startActivity(intent);
                        conversation.resetUnreadMsgCount();
                    }
                    if (latmessage.direct == EMMessage.Direct.RECEIVE) {
                        try {
                            String ReciverName = conversation.getLastMessage()
                                    .getStringAttribute("extSendNickname");

                            String ReciverUrl = conversation.getLastMessage()
                                    .getStringAttribute("extSendHeadUrl");
                            intent.putExtra("tagname",
                                    conversation.getUserName());

                            intent.putExtra("title", ReciverName);
                            intent.putExtra("iv", ReciverUrl);

                            startActivity(intent);
                            conversation.resetUnreadMsgCount();
                        } catch (Exception e) {

                        }
                    }
                    for (int i = conversation.getAllMessages().size() - 1; i > 0; i--) {
                        EMMessage mymewEmMessagesss = conversation
                                .getMessage(i);
                        if (mymewEmMessagesss.direct == EMMessage.Direct.SEND) {
                            intent.putExtra("tagname",
                                    conversation.getUserName());
                            try {
                                intent.putExtra(
                                        "title",
                                        mymewEmMessagesss
                                                .getStringAttribute("extReceiveNickname"));
                            } catch (Exception e) {
                                intent.putExtra("title", "小糖果");
                            }
                            try {
                                intent.putExtra(
                                        "iv",
                                        mymewEmMessagesss
                                                .getStringAttribute("extReceiveHeadUrl"));

                            } catch (Exception e) {
                            }
                            startActivity(intent);
                            conversation.resetUnreadMsgCount();
                            return;

                        }

                    }
                    intent.putExtra("tagname", conversation.getUserName());
                    intent.putExtra("title", "小糖果");
                    intent.putExtra("iv", "");
                    startActivity(intent);
                    conversation.resetUnreadMsgCount();

                } catch (Exception e) {

                }

            }

        });
        mynew_imlist.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {
                final EMConversation conversation = (EMConversation) ImHistoryAdapter
                        .getItem(arg2);

                ShowCustomDialog("是否删除该条对话?", "取消", "删除", new IDialogResult() {

                    @Override
                    public void RightResult() {
                        EMChatManager.getInstance().deleteConversation(
                                conversation.getUserName());
                        refresh();

                    }

                    @Override
                    public void LeftResult() {
                    }
                });

                return true;
            }
        });
        // 消息的view
        mynew_newlist = (CompleteListView) findViewById(R.id.mynew_newlist);

        myMew_Ap = new MyMew_Ap(R.layout.item_my_new);
        mynew_newlist.setAdapter(myMew_Ap);

        mynew_newlist.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {

                BLComment itemdata = (BLComment) arg0.getItemAtPosition(arg2);
                if (itemdata.getMessage_info() == null
                        || StrUtils.isEmpty(itemdata.getMessage_info().getId())) {
                    PromptManager.ShowCustomToast(BaseContext, "暂无消息");
                } else {
                    PromptManager.SkipActivity(BaseActivity, new Intent(
                            BaseActivity, AItemNew.class).putExtra("newtype",
                            itemdata.getMessage_info().getSource_type()));

                }

            }
        });

        mynew_newlist.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {
                final BLComment itemdata = (BLComment) arg0
                        .getItemAtPosition(arg2);
                ShowCustomDialog("是否删除该类型消息?", "取消", "确定", new IDialogResult() {

                    @Override
                    public void RightResult() {
                        DeletByType(itemdata.getSource_type());
                    }

                    @Override
                    public void LeftResult() {

                    }
                });
                return true;
            }
        });
    }

    @Override
    protected void InitTile() {
        SetTitleTxt("我的消息");
        SetRightRightIv(R.drawable.lajixiang_iv);
        right_right_iv.setOnClickListener(this);
        right_right_iv.setVisibility(View.GONE);
    }

    @Override
    protected void DataResult(int Code, String Msg, BComment Data) {

        switch (Data.getHttpResultTage()) {
            case 0:// 初始化进来获取数据
                mynew_imlist.setVisibility(View.VISIBLE);
                // new_zhushou_lay.setVisibility(View.VISIBLE);
                if (StrUtils.isEmpty(Data.getHttpResultStr())) {
                    // 数据加载完毕的操作
                    DataError("无更多数据", Data.getHttpLoadType());
                    return;
                }

                List<BLComment> comments = new ArrayList<BLComment>();
                try {
                    comments = JSON.parseArray(Data.getHttpResultStr(),
                            BLComment.class);
                } catch (Exception e) {
                    DataError("数据格式错误", Data.getHttpLoadType());
                    return;
                }
                CacheUtil.New_Save(BaseContext, Data.getHttpResultStr());
                // right_right_iv.setVisibility(View.VISIBLE);
                switch (Data.getHttpLoadType()) {
                    case LOAD_INITIALIZE:

                        myMew_Ap.Refrsh(comments);
                        break;
                    case LOAD_REFRESHING:
                        myMew_Ap.Refrsh(comments);

                        break;
                    case LOAD_LOADMOREING:

                        myMew_Ap.AddRefrsh(comments);
                        break;
                    default:
                        break;
                }

                break;
            case 40:// 清空消息成功
                myMew_Ap.Refrsh(new ArrayList<BLComment>());
                break;
            case 31:// 删除单个消息
                IData(LOAD_INITIALIZE);
                break;
            default:
                break;
        }

    }

    private List<BLComment> FiltrateData(List<BLComment> dda) {
        List<BLComment> dataa = new ArrayList<BLComment>();
        for (int i = 0; i < dda.size(); i++) {
            if (dda.get(i).getMessage_info() != null
                    && !StrUtils.isEmpty(dda.get(i).getMessage_info()
                    .getCreate_time())) {
                dataa.add(dda.get(i));
            }
        }
        return dataa;

    }

    @Override
    protected void DataError(String error, int LoadType) {
        // PromptManager.ShowCustomToast(BaseContext, error);
        mynew_imlist.setVisibility(View.VISIBLE);
        // new_zhushou_lay.setVisibility(View.VISIBLE);
    }

    public void NewReciver(BMessage message) {
        switch (message.getMessageType()) {
            case BMessage.Tage_New_Kill:
                BaseActivity.finish();

                break;

            default:
                break;
        }
    }

    /**
     * 刷新小助手数据
     */
    private void Frashherpler() {
        if (HeplerConversation != null) {// 微糖小助手的会话实体存在

            StrUtils.SetTxt(item_my_new_content,
                    getMessage(HeplerConversation.getLastMessage()));

            StrUtils.SetTxt(new_zhushou_time, DateUtils
                    .timeStampToStr1(HeplerConversation.getLastMessage()
                            .getMsgTime()));
            //
        } else {// 不存在

        }

    }

    /**
     * 获取所有会话
     *
     * @return
     */
    private Collection<? extends EMConversation> loadConversationWithRecentChat() {
        Hashtable<String, EMConversation> conversations = EMChatManager
                .getInstance().getAllConversations();
        List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
        synchronized (conversations) {
            for (EMConversation conversation : conversations.values()) {
                if (conversation.getAllMessages().size() != 0) {
                    if (!conversation.getUserName().equals(Constants.WtHelper)) {
                        sortList.add(new Pair<Long, EMConversation>(
                                conversation.getLastMessage().getMsgTime(),
                                conversation));
                        String ssss = conversation.getUserName();

                    } else {
                        HeplerConversation = conversation;
                        Frashherpler();
                    }
                }
            }
        }

        try {
            sortConversationByLastChatTime(sortList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<EMConversation> list = new ArrayList<EMConversation>();
        for (Pair<Long, EMConversation> sortItem : sortList) {
            list.add(sortItem.second);
        }
        return list;
    }

    /**
     * 根据最后一条消息的时间排序
     *
     * @param sortList
     */
    private void sortConversationByLastChatTime(
            List<Pair<Long, EMConversation>> sortList) {
        Collections.sort(sortList,
                new Comparator<Pair<Long, EMConversation>>() {

                    @Override
                    public int compare(Pair<Long, EMConversation> con1,
                                       Pair<Long, EMConversation> con2) {
                        if (con1.first == con2.first) {
                            return 0;
                        } else if (con2.first > con1.first) {
                            return 1;
                        } else {
                            return -1;
                        }
                    }
                });
    }

    public void refresh() {
        conversationList.clear();
        conversationList.addAll(loadConversationWithRecentChat());
        if (ImHistoryAdapter != null) {
            ImHistoryAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
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

    private class MyMew_Ap extends BaseAdapter {//
        private LayoutInflater inflater;
        private int ResourceId;
        private List<BLComment> datas = new ArrayList<BLComment>();

        public MyMew_Ap(int resourceId) {
            super();

            this.inflater = LayoutInflater.from(BaseContext);
            this.ResourceId = resourceId;

        }

        /**
         * 刷新
         */
        public void Refrsh(List<BLComment> da) {
            this.datas = FiltrateData(da);
            notifyDataSetChanged();
        }

        /**
         * 添加刷新
         */
        public void AddRefrsh(List<BLComment> da) {
            this.datas.addAll(da);
            notifyDataSetChanged();
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
            My_New_Item myItem = null;
            if (convertView == null) {
                myItem = new My_New_Item();
                convertView = inflater.inflate(ResourceId, null);
                myItem.item_my_new_title = (TextView) convertView
                        .findViewById(R.id.item_my_new_title);
                myItem.item_my_new_content = (TextView) convertView
                        .findViewById(R.id.item_my_new_content);

                myItem.item_my_new_iv = ViewHolder.get(convertView,
                        R.id.item_myin_new_iv);
                myItem.item_my_new_time = ViewHolder.get(convertView,
                        R.id.item_my_new_time);
                convertView.setTag(myItem);
            } else {
                myItem = (My_New_Item) convertView.getTag();
            }

            BLComment data = datas.get(position);
            if (data.getMessage_info() == null
                    || StrUtils.isEmpty(data.getMessage_info().getId())) {
                StrUtils.SetTxt(myItem.item_my_new_content, "暂无消息");
                myItem.item_my_new_title.setVisibility(View.INVISIBLE);
            } else {
                myItem.item_my_new_title.setVisibility(View.VISIBLE);
                myItem.item_my_new_content.setVisibility(View.VISIBLE);
                StrUtils.SetTxt(myItem.item_my_new_title, data
                        .getMessage_info().getTitle());
                StrUtils.SetTxt(myItem.item_my_new_content, data
                        .getMessage_info().getContent());
                StrUtils.SetTxt(myItem.item_my_new_time, DateUtils
                        .timeStampToStr(StrUtils.toLong(data.getMessage_info()
                                .getCreate_time())));

                int SourType = StrUtils.toInt(data.getSource_type());
                switch (SourType) {
                    case 1:// 消息
                        myItem.item_my_new_iv.setImageResource(R.drawable.new_new);
                        break;
                    case 2:// 支付
                        myItem.item_my_new_iv
                                .setImageResource(R.drawable.new_fukuan);
                        break;
                    case 3:// 订单
                        myItem.item_my_new_iv.setImageResource(R.drawable.new_oder);
                        break;
                    default:
                        break;
                }

            }

            return convertView;
        }

        class My_New_Item {
            ImageView item_my_new_iv;
            TextView item_my_new_title;
            TextView item_my_new_content;
            TextView item_my_new_time;//
        }
    }

    @Override
    protected void MyClick(View V) {
        switch (V.getId()) {
            case R.id.right_right_iv:// 跳转
                ShowCustomDialog("确定清理消息?", "取消", "清理", new IDialogResult() {

                    @Override
                    public void RightResult() {
                        Delet();
                    }

                    @Override
                    public void LeftResult() {
                    }
                });

                // PromptManager.SkipActivity(BaseActivity, new Intent(BaseActivity,
                // AAllNewSet.class));
                break;
            case R.id.new_zhushou_lay:// 微糖小助手 v-town000111222
                Intent intent = new Intent(BaseActivity, AChatInf.class);
                intent.putExtra("tagname", Constants.WtHelper);
                intent.putExtra("title",
                        getResources().getString(R.string.wt_helper));
                intent.putExtra("iv", "");
                intent.putExtra("ishepler", true);
                startActivity(intent);
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
