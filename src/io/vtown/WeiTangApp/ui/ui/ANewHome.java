package io.vtown.WeiTangApp.ui.ui;

import io.vtown.WeiTangApp.R;
import io.vtown.WeiTangApp.adapter.BasAdapter;
import io.vtown.WeiTangApp.bean.bcache.BHome;
import io.vtown.WeiTangApp.bean.bcomment.BComment;
import io.vtown.WeiTangApp.bean.bcomment.BUser;
import io.vtown.WeiTangApp.bean.bcomment.news.BMessage;
import io.vtown.WeiTangApp.comment.contant.CacheUtil;
import io.vtown.WeiTangApp.comment.contant.Constants;
import io.vtown.WeiTangApp.comment.contant.PromptManager;
import io.vtown.WeiTangApp.comment.contant.Spuit;
import io.vtown.WeiTangApp.comment.util.StrUtils;
import io.vtown.WeiTangApp.comment.util.image.ImageLoaderUtil;
import io.vtown.WeiTangApp.comment.view.MyBadgeView;
import io.vtown.WeiTangApp.comment.view.listview.LListView;
import io.vtown.WeiTangApp.comment.view.listview.LListView.IXListViewListener;
import io.vtown.WeiTangApp.comment.view.pop.PHomeSelect;
import io.vtown.WeiTangApp.comment.view.pop.PHomeSelect.SeleckClickListener;
import io.vtown.WeiTangApp.ui.ATitileNoBase;
import io.vtown.WeiTangApp.ui.comment.ACommentList;
import io.vtown.WeiTangApp.ui.comment.AWeb;
import io.vtown.WeiTangApp.ui.title.ABrandDetail;
import io.vtown.WeiTangApp.ui.title.AGoodDetail;
import io.vtown.WeiTangApp.ui.title.mynew.ANew;
import io.vtown.WeiTangApp.ui.title.zhuanqu.AZhuanQu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.jauker.widget.BadgeView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import de.greenrobot.event.EventBus;

/**
 * @author 作者 大兔兔 wangyongkui@v-town.cc
 * @version 创建时间：2016-7-14 下午1:35:30
 * 
 */
public class ANewHome extends ATitileNoBase implements IXListViewListener {

	/**
	 * 积累的view
	 */
	private View basView;

	/**
	 * 类别imagview
	 */
	private ImageView newhome_select_iv;
	/**
	 * 点击搜索
	 */
	private RelativeLayout newhome_sou_lay;
	/**
	 * 扫描
	 */
	private ImageView newhome_sao_iv;
	/**
	 * 消息
	 */
	private ImageView newhome_new_iv;

	/**
	 * 列表
	 */
	private LListView newhome_ls;
	/**
	 * adapter
	 */
	private NewHoemAp newHoemAp;
	private BUser mBUser;
	// 保存全部的View数据
	private BComment mBComment;
	/**
	 * 当前是第几页
	 */
	private int CurrNumber = 1;

	private BadgeView badgeView;

	/**
	 * 消息的角标
	 */

	@Override
	protected void InItBaseView() {

		setContentView(R.layout.activity_newhome);
		basView = LayoutInflater.from(BaseContext).inflate(
				R.layout.activity_newhome, null);
		mBUser = Spuit.User_Get(BaseContext);
		EventBus.getDefault().register(this, "getEventBusMsg", BMessage.class);
		IBasView();
		SetTitleHttpDataLisenter(this);
		GetHomeData(CurrNumber, LOAD_INITIALIZE); 

	}

	private void IBasView() {
		IUpView();
//		ICacheData();
	}

	/**
	 * 开启缓存模式
	 */
	private void ICacheData() {
		if (StrUtils.isEmpty(CacheUtil.Home_Get(BaseContext))) {
			PromptManager.showtextLoading(BaseContext, getResources()
					.getString(R.string.loading));
			return;
		}
		// 开始解析数据
		try {
			mBComment = JSON.parseObject(CacheUtil.Home_Get(BaseContext),
					BComment.class);
			newHoemAp.FrashData(mBComment.getAdvert());
			if (mBComment.getAdvert() == null
					|| mBComment.getAdvert().size() < 10) {
				newhome_ls.hidefoot();
			} else {// 没有更多
				newhome_ls.ShowFoot();
			}
		} catch (Exception e) {
			PromptManager.showtextLoading(BaseContext, getResources()
					.getString(R.string.loading));

			return;
		}

	}

	/**
	 * 获取数据
	 */
	private void GetHomeData(int Page, int LoadType) {

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("page", Page + "");
		map.put("pageNum", Constants.PageSize + "");
		map.put("member_id", mBUser.getMember_id());
		FBGetHttpData(map, Constants.HomeUrl, Method.GET, 0, LoadType);
	}

	private void IUpView() {
		badgeView = new BadgeView(BaseContext);
		newhome_select_iv = (ImageView) findViewById(R.id.newhome_select_iv);
		newhome_sou_lay = (RelativeLayout) findViewById(R.id.newhome_sou_lay);
		newhome_sao_iv = (ImageView) findViewById(R.id.newhome_sao_iv);
		newhome_new_iv = (ImageView) findViewById(R.id.newhome_new_iv);
		newhome_ls = (LListView) findViewById(R.id.newhome_ls);
		newHoemAp = new NewHoemAp();

		boolean imMessage = Spuit.IMMessage_Get(BaseContext);
		if (imMessage) {
			newhome_new_iv.setImageDrawable(getResources().getDrawable(
					R.drawable.ic_xiaoxitixing_nor));
		} else {

			newhome_new_iv.setImageDrawable(getResources().getDrawable(
					R.drawable.new1));
		}

		newhome_ls.setPullLoadEnable(true);
		newhome_ls.setPullRefreshEnable(true);
		newhome_ls.setXListViewListener(this);
		newhome_ls.hidefoot();

		newhome_ls.setAdapter(newHoemAp);
//		 newhome_ls.setOnScrollListener(new PauseOnScrollListener(ImageLoader
//		 .getInstance(), false, true));
		newhome_select_iv.setOnClickListener(this);
		newhome_sou_lay.setOnClickListener(this);
		newhome_sao_iv.setOnClickListener(this);
		newhome_new_iv.setOnClickListener(this);
		newhome_ls.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				BHome data = (BHome) arg0.getItemAtPosition(arg2);

				if (CheckNet(BaseContext))
					return;
				// advert_type 类型 1H5首页，2商品详情页，3店铺详情页,4活动详情页
				if (StrUtils.isEmpty(data.getAdvert_type()))
					return;

				int Type = StrUtils.toInt(data.getAdvert_type());

				switch (Type) {
				case 1:// HT跳转

					PromptManager.SkipActivity(BaseActivity, new Intent(
							BaseActivity, AWeb.class).putExtra(
							AWeb.Key_Bean,
							new BComment(data.getUrl(), StrUtils.NullToStr(data
									.getAdvert_type_str()))));
					break;
				case 2:// 商品详情页
					PromptManager.SkipActivity(BaseActivity, new Intent(
							BaseContext, AGoodDetail.class).putExtra("goodid",
							data.getSource_id()));
					// PromptManager.SkipActivity(BaseActivity, new
					// Intent(BaseContext, APlayer.class));
					break;
				case 3:// 店铺详情页!!!!!!!!!!!!!!!!!!!!!!!!需要修改
					BComment mBComment = new BComment(data.getSource_id(), data
							.getTitle());
					if (data.getIs_brand().equals("1")) {// 品牌店铺
						PromptManager.SkipActivity(BaseActivity, new Intent(
								BaseActivity, ABrandDetail.class).putExtra(
								BaseKey_Bean, mBComment));
					} else {// 自营店铺
						PromptManager.SkipActivity(BaseActivity, new Intent(
								BaseActivity, AShopDetail.class).putExtra(
								BaseKey_Bean, mBComment));
					}
					break;
				case 4:// 活动详情页
					BComment mBCommentss = new BComment(data.getSource_id(),
							data.getTitle());
					PromptManager.SkipActivity(BaseActivity, new Intent(
							BaseContext, AZhuanQu.class).putExtra(BaseKey_Bean,
							mBCommentss));
					break;
				default:
					// default时候直接展示大图
					break;
				}

			}
		});
	}

	@Override
	protected void InitTile() {
	}

	@Override
	protected void DataResult(int Code, String Msg, BComment Data) {

		switch (Data.getHttpLoadType()) {
		case LOAD_INITIALIZE:
			CacheUtil.Home_Save(BaseContext, Data.getHttpResultStr());
			if (StrUtils.isEmpty(Data.getHttpResultStr())) {// 数据不存在
				return;
			}

			mBComment = JSON.parseObject(Data.getHttpResultStr(),
					BComment.class);
			Send(mBComment.getCart_num());
			newHoemAp.FrashData(mBComment.getAdvert());

			if (mBComment.getAdvert() == null
					|| mBComment.getAdvert().size() < 10) {
				newhome_ls.hidefoot();

			} else {// 没有更多
				newhome_ls.ShowFoot();
			}
			break;
		case LOAD_REFRESHING:
			newhome_ls.stopRefresh();

			if (StrUtils.isEmpty(Data.getHttpResultStr())) {// 数据不存在
				return;
			}
			mBComment = JSON.parseObject(Data.getHttpResultStr(),
					BComment.class);
			if (mBComment.getAdvert() == null
					|| mBComment.getAdvert().size() < 10) {
				newhome_ls.hidefoot();

			} else {// 没有更多
				newhome_ls.ShowFoot();
			}

			newHoemAp.FrashData(mBComment.getAdvert());
			break;
		case LOAD_LOADMOREING:
			newhome_ls.stopLoadMore();
			JSONObject mJsonObject = null;
			try {
				mJsonObject = new JSONObject(Data.getHttpResultStr());
				if (StrUtils.isEmpty(mJsonObject.getString("advert")))
					return;
			} catch (JSONException e) {
				e.printStackTrace();
			}

			if (StrUtils.isEmpty(Data.getHttpResultStr())) {// 数据不存在
				return;
			}
			mBComment = JSON.parseObject(Data.getHttpResultStr(),
					BComment.class);
			if (mBComment.getAdvert() == null
					|| mBComment.getAdvert().size() < 10) {
				newhome_ls.hidefoot();

			} else {// 没有更多
				newhome_ls.ShowFoot();
			}
			newHoemAp.AddFrash(mBComment.getAdvert());
			break;
		default:
			break;
		}

	}

	@Override
	protected void DataError(String error, int LoadType) {
		PromptManager.ShowCustomToast(BaseContext, error);
		switch (LoadType) {
		case LOAD_INITIALIZE:
			GetHomeData(CurrNumber, LOAD_INITIALIZE);
			break;
		case LOAD_REFRESHING:
			newhome_ls.stopRefresh();
			break;
		case LOAD_LOADMOREING:
			newhome_ls.stopLoadMore();
			break;
		default:
			break;
		}
	}

	// private void ShowPop(View V) {
	// if (null == mBComment)
	// return;
	// final PHomeSelect pSelect = new PHomeSelect(BaseContext,
	// mBComment.getCate());
	// // 加了下面这行，onItemClick才好用
	// pSelect.GetSelectReult(new SeleckClickListener() {
	// @Override
	// public void GetResult(BHome data) {
	// BComment datas = new BComment(data.getId(), data.getCate_name());
	//
	// if (CheckNet(BaseContext))
	// return;
	//
	// if (data.getId().equals("0")) {
	// PromptManager
	// .SkipActivity(
	// BaseActivity,
	// new Intent(BaseContext, ACommentList.class)
	// .putExtra(
	// ACommentList.Tage_ResultKey,
	// ACommentList.Tage_HomePopBrand)
	// .putExtra(
	// ACommentList.Tage_BeanKey,
	// datas));
	// } else {
	// PromptManager.SkipActivity(BaseActivity, new Intent(
	// BaseContext, AGoodSort.class).putExtra(
	// AGoodSort.BaseKey_Bean, datas));
	// }
	// pSelect.dismiss();
	// }
	// });
	// pSelect.showAsDropDown(V, -20, 20);
	// // pSelect.showAsDropDown(V);
	//
	// }

	@Override
	protected void NetConnect() {
		NetError.setVisibility(View.GONE);

		// 无网络==》有网络
		if (newHoemAp.getCount() == 0)
			GetHomeData(CurrNumber, LOAD_INITIALIZE);

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
		// case R.id.newhome_select_iv:// 分类
		// ShowPop(V);
		// // ShowBottomPop(BaseContext, basView );
		//
		// break;
		case R.id.newhome_sou_lay:// 搜索
			PromptManager.SkipActivity(BaseActivity, new Intent(BaseContext,
					ASouSouGood.class));
			break;
		case R.id.newhome_sao_iv:
			PromptManager.SkipActivity(BaseActivity, new Intent(BaseContext,
					CaptureActivity.class));
			/*
			 * PromptManager.SkipActivity(BaseActivity, new Intent(BaseContext,
			 * ATestView.class));
			 */
			break;
		case R.id.newhome_new_iv:
			PromptManager.SkipActivity(BaseActivity, new Intent(BaseActivity,
					ANew.class));

			break;

		default:
			break;
		}
	}

	/**
	 * 列表的Adapater
	 */
	private class NewHoemAp extends BasAdapter {
		private List<BHome> datas = new ArrayList<BHome>();

		public NewHoemAp() {
			super();

		}

		/**
		 * 刷新
		 */
		public void FrashData(List<BHome> da) {
			this.datas = da;
			notifyDataSetChanged();
		}

		/**
		 * 添加刷新
		 */
		public void AddFrash(List<BHome> daas) {
			if (null == daas)
				return;
			this.datas.addAll(daas);
			notifyDataSetChanged();
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
			Myitem myItem = null;
			if (arg1 == null) {
				myItem = new Myitem();
				arg1 = LayoutInflater.from(BaseContext).inflate(
						R.layout.item_home_brand, null);
				myItem.item_home_brand_iv = (ImageView) arg1
						.findViewById(R.id.item_home_brand_iv);

				ViewGroup.LayoutParams lp = myItem.item_home_brand_iv
						.getLayoutParams();
				lp.width = screenWidth;
				lp.height = screenWidth / 2;
				myItem.item_home_brand_iv.setScaleType(ScaleType.FIT_XY);
				myItem.item_home_brand_iv.setLayoutParams(lp);

				arg1.setTag(myItem);
			} else {
				myItem = (Myitem) arg1.getTag();
			}

			final Myitem myitem2 = myItem;
			final int Postion = arg0;
			final BHome data = datas.get(arg0);
			 ImageLoaderUtil.Load22(datas.get(arg0).getPic_path(),
			 myItem.item_home_brand_iv, R.drawable.error_iv1);

//			DisplayImageOptions options = new DisplayImageOptions.Builder()
//					.showImageOnLoading(R.drawable.error_iv1)
//					.showImageForEmptyUri(R.drawable.error_iv1)
//					.showImageOnFail(R.drawable.error_iv1)
//					.cacheOnDisc(true)
//					.cacheInMemory(true)
//					.cacheOnDisk(true)
//					// .considerExifParams(true)
//					// .displayer(new FadeInBitmapDisplayer(100))
//					// .imageScaleType(ImageScaleType.NONE)
//					.displayer(new SimpleBitmapDisplayer())
//					.bitmapConfig(Bitmap.Config.RGB_565).build();
//
//			String tag = (String) myitem2.item_home_brand_iv.getTag();
//			if (tag == null || !tag.equals(datas.get(Postion).getPic_path())) {
//				ImageLoader.getInstance().displayImage(
//						datas.get(Postion).getPic_path(),
//						new ImageViewAware(myitem2.item_home_brand_iv, false), options,
//						new ImageLoadingListener() {
//							@Override
//							public void onLoadingStarted(String s, View view) {
//
//							}
//
//							@Override
//							public void onLoadingFailed(String s, View view,
//									FailReason failReason) {
//
//							}
//
//							@Override
//							public void onLoadingComplete(String s, View view,
//									Bitmap bitmap) {
//								myitem2.item_home_brand_iv.setTag(datas.get(
//										Postion).getPic_path());// 确保下载完成再打tag.
//							}
//
//							@Override
//							public void onLoadingCancelled(String s, View view) {
//
//							}
//
//						});
//			}

			// myItem.item_home_brand_iv.setOnClickListener(new
			// OnClickListener() {
			// @Override
			// public void onClick(View v) {
			//
			// if (CheckNet(BaseContext))
			// return;
			// // advert_type 类型 1H5首页，2商品详情页，3店铺详情页,4活动详情页
			// if (StrUtils.isEmpty(data.getAdvert_type()))
			// return;
			//
			// int Type = StrUtils.toInt(data.getAdvert_type());
			//
			// switch (Type) {
			// case 1:// HT跳转
			//
			// PromptManager.SkipActivity(BaseActivity, new Intent(
			// BaseActivity, AWeb.class).putExtra(
			// AWeb.Key_Bean,
			// new BComment(data.getUrl(), StrUtils
			// .NullToStr(data.getAdvert_type_str()))));
			// break;
			// case 2:// 商品详情页
			// PromptManager.SkipActivity(BaseActivity, new Intent(
			// BaseContext, AGoodDetail.class).putExtra(
			// "goodid", data.getSource_id()));
			// // PromptManager.SkipActivity(BaseActivity, new
			// // Intent(BaseContext, APlayer.class));
			// break;
			// case 3:// 店铺详情页!!!!!!!!!!!!!!!!!!!!!!!!需要修改
			// BComment mBComment = new BComment(data.getSource_id(),
			// data.getTitle());
			// if (data.getIs_brand().equals("1")) {// 品牌店铺
			// PromptManager
			// .SkipActivity(BaseActivity, new Intent(
			// BaseActivity, ABrandDetail.class)
			// .putExtra(BaseKey_Bean, mBComment));
			// } else {// 自营店铺
			// PromptManager.SkipActivity(BaseActivity,
			// new Intent(BaseActivity, AShopDetail.class)
			// .putExtra(BaseKey_Bean, mBComment));
			// }
			// break;
			// case 4:// 活动详情页
			// BComment mBCommentss = new BComment(
			// data.getSource_id(), data.getTitle());
			// PromptManager.SkipActivity(BaseActivity, new Intent(
			// BaseContext, AZhuanQu.class).putExtra(
			// BaseKey_Bean, mBCommentss));
			// break;
			// default:
			// // default时候直接展示大图
			// break;
			// }
			//
			// }
			// });

			return arg1;
		}

		class Myitem {
			ImageView item_home_brand_iv;
		}

		List<String> GetLs(List<BHome> daass) {
			List<String> mStrings = new ArrayList<String>();

			for (int i = 0; i < daass.size(); i++) {
				mStrings.add(daass.get(i).getPic_path());
			}
			return mStrings;

		}

	}

	private int number = 20;// 每次获取多少条数据

	private final class ScrollListener implements OnScrollListener {

		// scrollState=0,1,2三种状态
		public void onScrollStateChanged(AbsListView view, int scrollState) {
		}

		// totalItemCount 表示ListView中已装载的数据
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {

			final int loadtotal = totalItemCount;
			int lastItemid = newhome_ls.getLastVisiblePosition();// 获取当前屏幕最后Item的ID
			if ((lastItemid + 1) == totalItemCount) {// 达到数据的最后一条记录
				if (totalItemCount > 0) {
					// // 当前页
					int currentpage = totalItemCount % number == 0 ? totalItemCount
							/ number
							: totalItemCount / number + 1;
					int nextpage = currentpage + 1;// 下一页

					new Thread(new Runnable() {
						public void run() {
							try {
								Thread.sleep(3000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}

							// handler.sendMessage(handler.obtainMessage(100,
							// result));
						}
					}).start();
					// }
				}

			}
		}
	}

	// Handler handler = new Handler() {
	// public void handleMessage(Message msg) {
	// data.addAll((List<String>) msg.obj);
	// adapter.notifyDataSetChanged();// 告诉ListView数据已经发生改变，要求ListView更新界面显示
	// // ListView.getFooterViewsCount(),返回0表示没有footerView 大于0表示有
	// if (listView.getFooterViewsCount() > 0)
	// listView.removeFooterView(footer);
	// loadfinish = true;
	// }
	// };

	@Override
	protected void InItBundle(Bundle bundle) {
	}

	@Override
	protected void SaveBundle(Bundle bundle) {
	}

	@Override
	public void onRefresh() {
		CurrNumber = 1;
		GetHomeData(CurrNumber, LOAD_REFRESHING);
	}

	@Override
	public void onLoadMore() {
		CurrNumber = CurrNumber + 1;
		GetHomeData(CurrNumber, LOAD_LOADMOREING);
	}

	private void InitBadgeView() {

		badgeView.setTargetView(newhome_new_iv);
		// badgeView.setBackgroundColor(getResources().getColor(R.color.white));
		// badgeView.setBackground(2, getResources().getColor(R.color.white));
		badgeView.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.shape_white_point));
		FrameLayout.LayoutParams mLayoutParams = new FrameLayout.LayoutParams(
				20, 20);
		badgeView.setLayoutParams(mLayoutParams);
		badgeView.setPadding(30, 0, 0, 0);
		badgeView.setBadgeCount(1);

		badgeView.setBadgeGravity(Gravity.TOP | Gravity.RIGHT);
		badgeView.setTextSize(5);

	}

	public void getEventBusMsg(BMessage event) {
		int messageType = event.getMessageType();

		switch (messageType) {
		case BMessage.IM_Have_MSG:
			// InitBadgeView();
			newhome_new_iv.setImageDrawable(getResources().getDrawable(
					R.drawable.ic_xiaoxitixing_nor));
			break;

		case BMessage.IM_MSG_READ:
			// badgeView.setBadgeCount(0);

			newhome_new_iv.setImageDrawable(getResources().getDrawable(
					R.drawable.new1));
			Spuit.IMMessage_Set(BaseContext, false);
			break;

		default:
			break;
		}

	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
		try {
			EventBus.getDefault().unregister(this);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
