package com.example.common;

import java.util.ArrayList;
import java.util.List;

import com.example.bean.FanBrand;
import com.example.bean.FanType;
import com.example.singleton.UserSingleton;

public class FanDataTest {
	static List<FanBrand> fanBrands = new ArrayList<FanBrand>();
	List<FanType> fanTypes = new ArrayList<FanType>();

	public FanDataTest() {
		// TODO Auto-generated constructor stub
	}

	public static void fanDataTest() {
		FanBrand fanBrand;
		for (int i = 0; i < 3; i++) {
			fanBrand = new FanBrand("华创" + i, "huachuang" + i);
			List<FanType> localFanTypes = new ArrayList<FanType>();
			FanType localFanType;
			for (int j = 0; j < 4; j++) {
				localFanType = new FanType("huachuang" + i, i + "牌型号" + j, "型号"
						+ j);
				localFanTypes.add(localFanType);
			}
			fanBrand.setFanTypes((ArrayList<FanType>) localFanTypes);
			fanBrands.add(fanBrand);
		}
		UserSingleton.getInstance().setFanBrands(fanBrands);
		for (int i = 0; i < fanBrands.size(); i++) {
			UserSingleton.getInstance().getFanTypes()
					.addAll(fanBrands.get(i).getFanTypes());
		}

	}

}
