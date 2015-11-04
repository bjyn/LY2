package com.example.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.example.fragment.BaseInfoFragment;
import com.example.fragment.ImageGridFragment;
import com.example.lyfaultdiagnosissystem.R;
import com.example.singleton.UserSingleton;
import com.example.tree_component.bean.ImageResource;
import com.example.tree_component.bean.PartInfo;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class PartActivity extends FragmentActivity implements OnClickListener {

	// widgets
	private ViewPager viewPager;
	private Button infoButton;
	private Button paperButton;
	private Button imageButton;
	private ImageView infoFlag;
	private ImageView paperFlag;
	private ImageView imageFlag;

	// temporaries
	private List<Fragment> fragments;
	private PartInfo partInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_part);
		initImageLoader(PartActivity.this);
		// initiate widgets
		viewPager = (ViewPager) this.findViewById(R.id.id_viewpager);
		infoButton = (Button) this.findViewById(R.id.tree_group_btn);
		paperButton = (Button) this.findViewById(R.id.paper_button);
		imageButton = (Button) this.findViewById(R.id.tree_comp_btn);
		infoFlag = (ImageView) this.findViewById(R.id.part_flag_one);
		paperFlag = (ImageView) this.findViewById(R.id.tree_group_flag);
		imageFlag = (ImageView) this.findViewById(R.id.tree_comp_flag);

		infoButton.setOnClickListener(this);
		paperButton.setOnClickListener(this);
		imageButton.setOnClickListener(this);
		infoFlag.setVisibility(View.VISIBLE);
		paperFlag.setVisibility(View.INVISIBLE);
		imageFlag.setVisibility(View.INVISIBLE);

		partInfo = UserSingleton.getInstance().getPartInfo();
		List<ImageResource> papers = partInfo.getPapers();
		List<ImageResource> images = partInfo.getImages();

		fragments = new ArrayList<>();
		fragments.add(new BaseInfoFragment());
		fragments.add(new ImageGridFragment(papers));
		fragments.add(new ImageGridFragment(images));
		viewPager.setAdapter(new FragmentPagerAdapter(
				getSupportFragmentManager()) {

			@Override
			public int getCount() {
				return fragments.size();
			}

			@Override
			public Fragment getItem(int arg0) {
				return fragments.get(arg0);
			}

		});

		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int target) {
				switch (target) {
				case 0:
					infoFlag.setVisibility(View.VISIBLE);
					paperFlag.setVisibility(View.INVISIBLE);
					imageFlag.setVisibility(View.INVISIBLE);
					break;

				case 1:
					infoFlag.setVisibility(View.INVISIBLE);
					paperFlag.setVisibility(View.VISIBLE);
					imageFlag.setVisibility(View.INVISIBLE);
					break;
				case 2:
					infoFlag.setVisibility(View.INVISIBLE);
					paperFlag.setVisibility(View.INVISIBLE);
					imageFlag.setVisibility(View.VISIBLE);
					break;

				default:
					break;
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tree_group_btn:
			infoFlag.setVisibility(View.VISIBLE);
			paperFlag.setVisibility(View.INVISIBLE);
			imageFlag.setVisibility(View.INVISIBLE);
			viewPager.setCurrentItem(0, true);
			break;
		case R.id.paper_button:
			infoFlag.setVisibility(View.INVISIBLE);
			paperFlag.setVisibility(View.VISIBLE);
			imageFlag.setVisibility(View.INVISIBLE);
			viewPager.setCurrentItem(1, true);
			break;

		case R.id.tree_comp_btn:
			infoFlag.setVisibility(View.INVISIBLE);
			paperFlag.setVisibility(View.INVISIBLE);
			imageFlag.setVisibility(View.VISIBLE);
			viewPager.setCurrentItem(2, true);
			break;

		default:
			break;
		}
	}

}
