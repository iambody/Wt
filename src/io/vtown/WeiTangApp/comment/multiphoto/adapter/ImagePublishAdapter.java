package io.vtown.WeiTangApp.comment.multiphoto.adapter;

import io.vtown.WeiTangApp.R;
import io.vtown.WeiTangApp.comment.multiphoto.model.ImageItem1;
import io.vtown.WeiTangApp.comment.multiphoto.util.CustomConstants;
import io.vtown.WeiTangApp.comment.multiphoto.util.ImageDisplayer;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class ImagePublishAdapter extends BaseAdapter {
	private List<ImageItem1> mDataList = new ArrayList<ImageItem1>();
	private Context mContext;

	public ImagePublishAdapter(Context context, List<ImageItem1> dataList) {
		this.mContext = context;
		this.mDataList = dataList;
	}

	public int getCount() {
		// 多返回一个用于展示添加图标
		if (mDataList == null) {
			return 1;
		} else if (mDataList.size() == CustomConstants.MAX_IMAGE_SIZE) {
			return CustomConstants.MAX_IMAGE_SIZE;
		} else {
			return mDataList.size() + 1;
		}
	}

	public Object getItem(int position) {
		if (mDataList != null
				&& mDataList.size() == CustomConstants.MAX_IMAGE_SIZE) {
			return mDataList.get(position);
		}

		else if (mDataList == null || position - 1 < 0
				|| position > mDataList.size()) {
			return null;
		} else {
			return mDataList.get(position - 1);
		}
	}

	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("ViewHolder")
	public View getView(int position, View convertView, ViewGroup parent) {
		// 所有Item展示不满一页，就不进行ViewHolder重用了，避免了一个拍照以后添加图片按钮被覆盖的奇怪问题
		convertView = View.inflate(mContext, R.layout.multiphoto_item_publish,
				null);
		ImageView imageIv = (ImageView) convertView
				.findViewById(R.id.item_grid_image);
		ProgressBar item_grid_image_progress = (ProgressBar) convertView
				.findViewById(R.id.item_grid_image_progress);
		item_grid_image_progress.setVisibility(View.GONE);
		if (isShowAddItem(position)) {
			imageIv.setImageResource(R.drawable.multiphoto_btn_add_pic);
			imageIv.setBackgroundResource(R.color.bg_gray);
		} else {
			final ImageItem1 item = mDataList.get(position);
			ImageDisplayer.getInstance(mContext).displayBmp(imageIv,
					item.thumbnailPath, item.sourcePath);
		}

		return convertView;
	}

	private boolean isShowAddItem(int position) {
		int size = mDataList == null ? 0 : mDataList.size();
		return position == size;
	}

}
