package com.example.fragment;

import java.util.List;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.lyfaultdiagnosissystem.R;
import com.example.tree_component.bean.ImageResource;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ImageGridFragment extends Fragment {
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options; // ��ʾͼƬ������
	private GridView gridView;
	private List<ImageResource> imageResources;
	
	

	public ImageGridFragment(List<ImageResource> imageResources) {
		super();
		this.imageResources = imageResources;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_part_image, container,
				false);
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.ic_stub)
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error).cacheInMemory(true)
				.cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565) // ����ͼƬ�Ľ�������
				.build();
		gridView = (GridView) view.findViewById(R.id.gridview);
		gridView.setAdapter(new ImageAdapter());
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				startImagePagerActivity(position);
			}
		});
		return view;
	}

	private void startImagePagerActivity(int position) {

	}

	public class ImageAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return imageResources.size();
		}

		@Override
		public Object getItem(int position) {
			return imageResources.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ImageView imageView;
			if (convertView == null) {
				imageView = (ImageView) getActivity().getLayoutInflater()
						.inflate(R.layout.item_grid_image, parent, false);
			} else {
				imageView = (ImageView) convertView;
			}

			// ��ͼƬ��ʾ�������ӵ�ִ�гأ�ͼƬ������ʾ��ImageView���ֵ���ImageView
			imageLoader.displayImage(imageResources.get(position).getUrl(), imageView,
					options);

			return imageView;
		}
	}
}
