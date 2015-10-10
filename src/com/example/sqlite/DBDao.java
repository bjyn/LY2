package com.example.sqlite;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.bean.FTBaseInfo;
import com.example.bean.FTFBBaseInfo;
import com.example.bean.FanBrand;
import com.example.bean.FanType;
import com.example.bean.OfflineFeedbacked;
import com.example.bean.OfflineUnfeedback;
import com.example.bean.UserInfo;
import com.example.tree_component.bean.FaultTreeNode;
import com.example.tree_component.bean.FaultTreeURLBean;
import com.example.tree_component.bean.TreeBean;
import com.example.tree_component.bean.TreeRoot;

/**
 * 对数据库的操作类
 * 
 * @author steven
 * 
 */
public class DBDao {
	public static final String TAG = "DBDao";
	MyDatabaseHelper dbOpenHelper;

	public DBDao(Context context) {
		this.dbOpenHelper = new MyDatabaseHelper(context);
	}

	// SYS_USER用户信息表===============================
	/**
	 * 得到数据库中缓存的登录名明文和MD5加密密码
	 * 
	 * @return 明文登录名及MD5加密后的密码
	 */
	public List<String> getCodeAndPwd() {
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.query("SYS_USER", new String[] { "user_code",
				"user_password" }, null, null, null, null, null);
		if (cursor.getCount() == 0) {
			// 数据库没有数据时
			cursor.close();
			return null;
		} else {
			cursor.moveToFirst();
			List<String> values = new ArrayList<>();
			values.add(cursor.getString(0));// userCode
			values.add(cursor.getString(1));// password
			cursor.close();
			return values;
		}
	}

	/**
	 * 返回当前用户信息
	 * 
	 * @return 用户信息
	 */
	public UserInfo getUserInfo() {
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.query("SYS_USER", new String[] { "user_name",
				"user_last_time", "user_current_unfeedback_count",
				"user_limit_unfeedback_count" }, null, null, null, null, null);
		if (cursor.getCount() == 0) {
			cursor.close();
			return null;
		} else {
			cursor.moveToFirst();
			cursor.close();
			// TODO 666666666666666666
			UserInfo userInfo = null;
			cursor.close();
			return userInfo;
		}
	}

	/**
	 * 更新／插入用户信息
	 * 
	 * @param userInfo
	 */
	public void updateUserInfo(UserInfo userInfo) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery("select * from SYS_USER", null);
		ContentValues contentValues = new ContentValues();
		contentValues.put("user_code", userInfo.getUserCode());
		contentValues.put("user_password", userInfo.getUserPassword());
		contentValues.put("user_name", userInfo.getUserName());
		contentValues.put("user_last_time", userInfo.getUserLastTime());
		contentValues.put("user_current_unfeedback_count",
				userInfo.getUserCurrentUnfeedbackCount());
		contentValues.put("user_limit_unfeedback_count",
				userInfo.getUserLimitUnfeedbackCount());
		if (cursor.getCount() == 0) {
			db.insert("SYS_USER", null, contentValues);
		} else {
			db.update("SYS_USER", contentValues, null, null);
			Log.i(TAG, "用户信息已更新");
		}
		cursor.close();
	}

	/**
	 * 更新用户表中的未反馈条数。
	 * 
	 * @param unfeedbackCount
	 */
	public void updateUserUnfeedbackCount(int unfeedbackCount) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("user_current_unfeedback_count", unfeedbackCount);
		db.update("SYS_USER", contentValues, null, null);
	}

	// FAN_BRAND_INFO风机品牌表===============================

	/**
	 * 得到所有风机品牌信息
	 * 
	 * @return
	 */
	public List<FanBrand> getFanBrand() {
		Cursor cursor = dbOpenHelper.getReadableDatabase().query(
				"FAN_BRAND_INFO", new String[] { "*" }, null, null, null, null,
				null);
		List<FanBrand> fanBrands = new ArrayList<>();
		while (cursor.moveToNext()) {
			fanBrands
					.add(new FanBrand(cursor.getString(0), cursor.getString(1)));
		}
		cursor.close();
		return fanBrands;

	}

	/**
	 * 插入全部风机品牌信息
	 * 
	 * @param fanBrands
	 */
	public void insertFanBrandInfo(List<FanBrand> fanBrands) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.beginTransaction();
		db.execSQL("delete from FAN_BRAND_INFO");
		for (FanBrand fanBrand : fanBrands) {
			ContentValues contentValues = new ContentValues();
			contentValues.put("fan_brand_name", fanBrand.getName());
			contentValues.put("fan_brand_code", fanBrand.getCode());
			db.insert("FAN_BRAND_INFO", null, contentValues);
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	// FAN_TYPE_INFO风机型号表===============================

	/**
	 * 得到所有风机的类型
	 * 
	 * @return
	 */
	public List<FanType> getFanType() {
		Cursor cursor = dbOpenHelper.getReadableDatabase().query(
				"FAN_TYPE_INFO", new String[] { "*" }, null, null, null, null,
				null);
		List<FanType> fanTypes = new ArrayList<>();
		while (cursor.moveToNext()) {
			fanTypes.add(new FanType(cursor.getString(0), cursor.getString(1),
					cursor.getString(2)));
		}
		cursor.close();
		return fanTypes;

	}

	/**
	 * 插入全部风机类型信息
	 * 
	 * @param fanTypes
	 */
	public void insertFanTypeInfo(List<FanType> fanTypes) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.execSQL("delete from FAN_TYPE_INFO");
		for (FanType fanType : fanTypes) {
			ContentValues contentValues = new ContentValues();
			contentValues.put("fan_type_code", fanType.getCode());
			contentValues.put("fan_type_name", fanType.getName());
			contentValues.put("parent_code", fanType.getBrandCode());
			db.insert("FAN_TYPE_INFO", null, contentValues);
		}

	}

	// FT_TO_FEEDBACK_INFO离线已反馈表===============================

	/**
	 * 删除一条离线状态已反馈纪录
	 * 
	 * @param offlineFeedbacked
	 */
	public void deleteOfflineFeedbacked(OfflineFeedbacked offlineFeedbacked) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.delete("FT_TO_FEEDBACK_INFO", "time=?",
				new String[] { offlineFeedbacked.getFeedbackTime() });
	}

	/**
	 *   获取所有离线状态下被置为已反馈的纪录
	 * 
	 * @return
	 */
	public List<OfflineFeedbacked> getAllOfflineFeedbackedsByTime() {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		List<OfflineFeedbacked> offlineFeedbackeds = new ArrayList<>();
		Cursor cursor = db.rawQuery(
				"select * from FT_TO_FEEDBACK_INFO order by time asc", null);
		while (cursor.moveToNext()) {
			// TODO 66666666666666
			OfflineFeedbacked offlineFeedbacked = null;
			offlineFeedbackeds.add(offlineFeedbacked);
		}
		cursor.close();
		return offlineFeedbackeds;
	}

	/**
	 * 插入离线下被置为已反馈的故障树
	 * 
	 * @param feedbackCode
	 * @param code
	 * @param feedbackType
	 * @param handleFaultCode
	 * @param description
	 */
	public boolean insertOfflineFeedbacked(OfflineFeedbacked offlineFeedbacked) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("feedbackcode", offlineFeedbacked.getFeedbackCode());
		contentValues.put("code", offlineFeedbacked.getCode());
		contentValues.put("feedback_type", offlineFeedbacked.getFeedbackType());
		contentValues.put("handle_fault_code",
				offlineFeedbacked.getHandleFaultCode());
		contentValues.put("description", offlineFeedbacked.getDescription());
		contentValues.put("time", offlineFeedbacked.getFeedbackTime());
		if (db.insert("FT_TO_FEEDBACK_INFO", null, contentValues) == -1) {
			Log.e(TAG, "插入离线已反馈纪录失败！time字段：" + offlineFeedbacked.getFeedbackTime());
			return false;
		} else {
			return true;
		}
	}

	// FT_SET_UNFEEDBACK_STATUS_INFO离线未反馈表===============================
	/**
	 * 删除一条离线状态未反馈纪录
	 * 
	 * @param offlineUnfeedback
	 * 
	 */
	public void deleteOfflineUnfeedback(OfflineUnfeedback offlineUnfeedback) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.delete("FT_SET_UNFEEDBACK_STATUS_INFO", "time=?",
				new String[] { offlineUnfeedback.getTime() });
	}

	/**
	 * 插入一条离线状态未反馈纪录
	 * 
	 * @param offlineUnfeedback
	 */
	public void insertOfflineUnfeedback(OfflineUnfeedback offlineUnfeedback) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("code", offlineUnfeedback.getCode());
		contentValues.put("time", offlineUnfeedback.getTime());
		db.insert("FT_SET_UNFEEDBACK_STATUS_INFO", null, contentValues);
	}

	/**
	 *  获取所有离线状态下被置为未反馈的纪录
	 */
	public List<OfflineUnfeedback> getAllOfflineUnfeedbacksByTime() {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		Cursor cursor = db
				.rawQuery(
						"select * from FT_SET_UNFEEDBACK_STATUS_INFO order by time asc",
						null);
		List<OfflineUnfeedback> unfeedbackList = new ArrayList<>();
		while (cursor.moveToNext()) {
			unfeedbackList.add(new OfflineUnfeedback(cursor.getString(0),
					cursor.getString(1)));
		}
		cursor.close();
		return unfeedbackList;
	}

	// FT_Q_BASE_INFO故障树基本信息表===============================

	/**
	 * 根据故障树编码code在FT_Q_BASE_INFO表中查询特定的一棵故障树的基本信息
	 * 
	 * @param code
	 * @return
	 */
	public FTBaseInfo getFTQBaseInfoByCode(String code) {
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.query("FT_Q_BASE_INFO", new String[] { "*" },
				"code = ?", new String[] { code }, null, null, null);
		if (cursor.getCount() == 0) {
			return null;
		} else {
			cursor.moveToNext();
			// TODO 6666666
			FTBaseInfo ftBaseInfo = null;
			cursor.close();
			return ftBaseInfo;
		}

	}

	/**
	 * 根据code查找反馈表中纪录，并更新这些纪录的详情版本.下载树的时候调用：更新树的版本。
	 * 
	 * @param code
	 *            故障树编码
	 * @param version
	 *            详情版本
	 */
	public void updateFTDetailVersion(String code, int version) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("detail_version", version);
		db.update("FT_Q_BASE_INFO", contentValues, "code=?",
				new String[] { code });
	}

	/**
	 * 更新纪录。如果不存在该纪录。插入。
	 * 
	 * @param ftBaseInfos
	 */
	public void updateFTBaseInfo(List<FTBaseInfo> ftBaseInfos) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		for (FTBaseInfo ftBaseInfo : ftBaseInfos) {
			Cursor cursor = db.rawQuery(
					"select * from FT_Q_BASE_INFO where code=?",
					new String[] { ftBaseInfo.getCode() });
			ContentValues contentValues = new ContentValues();
			contentValues.put("main_fault_code", ftBaseInfo.getMainFaultCode());
			contentValues.put("follow_fault_code",
					ftBaseInfo.getFollowFaultCode());
			contentValues.put("c_name", ftBaseInfo.getChineseName());
			contentValues.put("e_name", ftBaseInfo.getEnglishName());
			contentValues.put("fan_brand_name", ftBaseInfo.getFanBrand()
					.getName());
			contentValues.put("fan_brand_code", ftBaseInfo.getFanBrand()
					.getCode());
			contentValues.put("fan_type_name", ftBaseInfo.getFanType()
					.getName());
			contentValues.put("fan_type_code", ftBaseInfo.getFanType()
					.getCode());
			contentValues.put("trigger_condition",
					ftBaseInfo.getTriggerCondition());
			contentValues.put("fault_phe", ftBaseInfo.getFaultPhe());
			contentValues.put("base_version", ftBaseInfo.getVersion());
			contentValues.put("remark", ftBaseInfo.getRemark());
			if (cursor.getCount() == 0) {
				// 插入 ，版本号上不知道，设为0
				contentValues.put("detail_version", 0);
				db.insert("FT_Q_BASE_INFO", null, contentValues);
			} else {
				// 更新 ，不能覆盖了原来的版本号
				int updateCount = db.update("FT_Q_BASE_INFO", contentValues,
						"code=?", new String[] { ftBaseInfo.getCode() });
				Log.i(TAG, "更新数量：" + updateCount);
			}
			cursor.close();
		}
	}

	/**
	 * 离线状态下按照条件获取故障树基本信息列表
	 * 
	 * @param code
	 * @param keyword
	 * @param fanBrandCode
	 * @param fanTypeCode
	 * @return
	 */
	public List<FTBaseInfo> getFtBaseInfosByConditions(String code,
			String keyword, String fanBrandCode, String fanTypeCode) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		List<FTBaseInfo> ftBaseInfos = new ArrayList<>();

		// 1.查询基本信息表
		ArrayList<String> seletionArgs = new ArrayList<>();
		if (code != null && !code.equals("")) {
			seletionArgs.add(" (main_fault_code='" + code
					+ "' or follow_fault_code='" + code + "') ");
		}
		if (keyword != null && !keyword.equals("")) {
			// TODO 涉及基本信息表和节点表中的模糊查询
			seletionArgs
					.add(" ((main_fault_code like '%"
							+ keyword
							+ "%' or follow_fault_code like '%"
							+ keyword
							+ "%' or c_name like '%"
							+ keyword
							+ "%' or e_name like '%"
							+ keyword
							+ "%' or trigger_condition like '%"
							+ keyword
							+ "%') OR (code in (select root_code from FT_DETAIL_INFO where name like '%"
							+ keyword + "%'))) ");
		}
		if (fanBrandCode != null && !fanBrandCode.equals("")) {
			seletionArgs.add(" fan_brand_code='" + fanBrandCode + "' ");
		}
		if (fanTypeCode != null && !fanTypeCode.equals("")) {
			seletionArgs.add(" fan_type_code='" + fanTypeCode + "' ");
		}

		String whereSql = " where ";
		for (String string : seletionArgs) {
			whereSql += string + " and ";
		}
		whereSql = (String) whereSql.subSequence(0, whereSql.length() - 1
				- new String(" and ").length());

		// TODO 查询页面中的排序有待讨论
		Cursor cursor = db.rawQuery("select * from FT_Q_BASE_INFO" + whereSql,
				null);
		while (cursor.moveToNext()) {
			// TODO 6666666
			ftBaseInfos.add(null);
		}
		Log.i(TAG, "按照条件从基本信息表查出数量：" + cursor.getCount());
		cursor.close();
		return ftBaseInfos;
	}

	// FT_FB_BASE_INFO故障树反馈信息表===============================

	/**
	 * 返回全部未反馈的故障树的信息
	 * 
	 * @return
	 */
	public List<FTFBBaseInfo> getUnfeedbackBaseInfos() {
		List<FTFBBaseInfo> unfeedbacks = new ArrayList<>();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery(
				"select * from FT_FB_BASE_INFO where status='1'", null);
		while (cursor.moveToNext()) {
			// TODO 6666666
			FTFBBaseInfo ftfbBaseInfo = null;
			unfeedbacks.add(ftfbBaseInfo);
		}
		cursor.close();
		return unfeedbacks;
	}

	/**
	 * 更新或插入反馈故障树基本信息
	 * 
	 * @param baseInfos
	 */
	public void updateFaultTreeFBBaseInfo(List<FTFBBaseInfo> baseInfos) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.beginTransaction();
		for (FTFBBaseInfo baseInfo : baseInfos) {
			ContentValues contentValues = new ContentValues();
			contentValues.put("feedbackcode", baseInfo.getFeedbackCode());
			contentValues.put("code", baseInfo.getCode());
			contentValues.put("main_fault_code", baseInfo.getMainFaultCode());
			contentValues.put("follow_fault_code",
					baseInfo.getFollowFaultCode());
			contentValues.put("c_name", baseInfo.getChineseName());
			contentValues.put("e_name", baseInfo.getEnglishName());
			contentValues.put("fan_brand_name", baseInfo.getFanBrand()
					.getName());
			contentValues.put("fan_brand_code", baseInfo.getFanBrand()
					.getCode());
			contentValues.put("fan_type_name", baseInfo.getFanType().getName());
			contentValues.put("fan_type_code", baseInfo.getFanType().getCode());
			contentValues.put("status", baseInfo.getFeedbackStatus());
			contentValues.put("result", baseInfo.getCheckStatus());
			contentValues.put("trigger_condition",
					baseInfo.getTriggerCondition());
			contentValues.put("fault_phe", baseInfo.getFaultPhe());
			contentValues.put("base_version", baseInfo.getVersion());
			contentValues.put("remark", baseInfo.getRemark());
			contentValues.put("time", baseInfo.getLookTime());
			// contentValues.put("detail_version", baseInfo.getDetailVersion());
			// detail_version 是不能被更新的！

			// 先查， 后更新
			Cursor cursor = db
					.rawQuery(
							"select * from FT_FB_BASE_INFO where code=? and time=?",
							new String[] { baseInfo.getCode(),
									baseInfo.getLookTime() });
			if (cursor.getCount() == 0) {
				// 插入
				contentValues
						.put("detail_version", baseInfo.getProVersion());
				db.insert("FT_FB_BASE_INFO", null, contentValues);
			} else {
				db.update(
						"FT_FB_BASE_INFO",
						contentValues,
						"time=?,code=?",
						new String[] { baseInfo.getLookTime(),
								baseInfo.getCode() });
			}
			cursor.close();

		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	/**
	 * 更新反馈故障树基本信息到最新版本
	 * 
	 * @param latestBaseInfo
	 *            该故障树的最新版本bean
	 */
	public void updateUnfeedbackBaseInfo(FTFBBaseInfo latestBaseInfo) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("main_fault_code", latestBaseInfo.getMainFaultCode());
		contentValues.put("follow_fault_code",
				latestBaseInfo.getFollowFaultCode());
		contentValues.put("c_name", latestBaseInfo.getChineseName());
		contentValues.put("e_name", latestBaseInfo.getEnglishName());
		contentValues.put("fan_brand_name", latestBaseInfo.getFanBrand()
				.getName());
		contentValues.put("fan_brand_code", latestBaseInfo.getFanBrand()
				.getCode());
		contentValues.put("fan_type_name", latestBaseInfo.getFanType()
				.getName());
		contentValues.put("fan_type_code", latestBaseInfo.getFanType()
				.getCode());
		contentValues.put("status", latestBaseInfo.getFeedbackStatus());
		contentValues.put("result", latestBaseInfo.getCheckStatus());
		contentValues.put("trigger_condition",
				latestBaseInfo.getTriggerCondition());
		contentValues.put("fault_phe", latestBaseInfo.getFaultPhe());
		contentValues.put("base_version", latestBaseInfo.getVersion());
		contentValues.put("remark", latestBaseInfo.getRemark());
		int updateNum = db.update("FT_FB_BASE_INFO", contentValues,
				"feedbackcode=?",
				new String[] { latestBaseInfo.getFeedbackCode() });
		if (updateNum == 1) {
			Log.i(TAG, "成功更新代码为：" + latestBaseInfo.getFeedbackCode() + "的一条纪录");
		} else if (updateNum > 1) {
			Log.e(TAG, "Error:更新数量超过1");
		}
	}

	/**
	 * 根据time查找反馈表中的纪录，更新／插入feedbackCode字段
	 * 
	 * @param feedbackCode
	 * @param time
	 * @return T:正常 F：异常
	 */
	public boolean updateOfflineFBFTFeedbackCode(String feedbackCode,
			String time) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("feedbackcode", feedbackCode);
		if (db.update("FT_FB_BASE_INFO", contentValues, "time=?",
				new String[] { time }) == 1) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 根据code查找反馈表中纪录，并更新这些纪录的详情版本
	 * 
	 * @param code
	 *            故障树编码
	 * @param version
	 *            详情版本
	 */
	public void updateFBFTDetailVersion(String code, int version) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("detail_version", version);
		db.update("FT_FB_BASE_INFO", contentValues, "code=?",
				new String[] { code });
	}

	/**
	 * 根据Code查找反馈表中是否含有已反馈的纪录。
	 * 
	 * @param code
	 * @return
	 */
	public boolean isFTUnfeedback(String code) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery(
				"select * from FT_FB_BASE_INFO where code=? and status=?",
				new String[] { code, "1" });
		if (cursor.getCount() == 0) {
			cursor.close();
			return false;
		} else {
			cursor.close();
			return true;
		}
	}

	/**
	 * 根据Code获取反馈表中的反馈信息
	 * 
	 * @param code
	 * @return
	 */
	public FTFBBaseInfo getFtfbBaseInfoByCode(String code, String time) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery(
				"select * from FT_FB_BASE_INFO where code=? and time=?",
				new String[] { code, time });
		if (cursor.getCount() == 0) {
			return null;
		} else {
			cursor.moveToNext();
			// TODO 6666666
			FTFBBaseInfo ftfbBaseInfo = null;
			cursor.close();
			return ftfbBaseInfo;
		}
	}

	/**
	 * 离线状态下获取反馈表中符合条件的反馈信息
	 * 
	 * @param faultCode
	 * @param feedbackStatus
	 * @param fanBrandCode
	 * @param fanTypeCode
	 * @return
	 */
	public List<FTFBBaseInfo> getFtfbBaseInfosByConditions(String code,
			String feedbackStatus, String fanBrandCode, String fanTypeCode) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		List<FTFBBaseInfo> ftfbBaseInfos = new ArrayList<>();

		ArrayList<String> seletionArgs = new ArrayList<>();
		if (code != null && !code.equals("")) {
			seletionArgs.add(" (main_fault_code='" + code
					+ "' or follow_fault_code='" + code + "') ");
		}
		if (feedbackStatus != null && !feedbackStatus.equals("")) {
			seletionArgs.add(" status='" + feedbackStatus + "' ");
		}
		if (fanBrandCode != null && !fanBrandCode.equals("")) {
			seletionArgs.add(" fan_brand_code='" + fanBrandCode + "' ");
		}
		if (fanTypeCode != null && !fanTypeCode.equals("")) {
			seletionArgs.add(" fan_type_code='" + fanTypeCode + "' ");
		}

		String whereSql = " where ";
		for (String string : seletionArgs) {
			whereSql += string + " and ";
		}
		whereSql = (String) whereSql.subSequence(0, whereSql.length() - 1
				- new String(" and ").length());

		// TODO 查询页面中的排序有待讨论
		Cursor cursor = db.rawQuery("select * from FT_FB_BASE_INFO" + whereSql,
				null);
		// XXX Fuckyou
//		while (cursor.moveToNext()) {
//			ftfbBaseInfos.add(new FTFBBaseInfo(cursor.getString(0), cursor
//					.getString(1), cursor.getString(2), cursor.getString(3),
//					cursor.getString(4), cursor.getString(5), new FanBrand(
//							cursor.getString(6), cursor.getString(7)),
//					new FanType(cursor.getString(7), cursor.getString(8),
//							cursor.getString(9)), cursor.getString(10), cursor
//							.getString(11), cursor.getString(12), cursor
//							.getString(13), cursor.getInt(14), cursor
//							.getString(15), cursor.getString(16), cursor
//							.getInt(17)));
//		}
		Log.i(TAG, "按照条件从FTFB表查出数量：" + cursor.getCount());
		cursor.close();
		return ftfbBaseInfos;
	}

	// FT_DETAIL_INFO故障树节点表===============================

	/**
	 * 获取一棵树。
	 * 
	 * @param code
	 * @return
	 */
	public TreeBean getTreeByCode(String code) {
		// 获取每一层子节点
		List<FaultTreeNode> faultReasonNodes = getTreeNodesByCodeAndLevel(code,
				1);
		List<FaultTreeNode> reasonCheckNodes = getTreeNodesByCodeAndLevel(code,
				2);
		List<FaultTreeNode> handleFaultTreeNodes = getTreeNodesByCodeAndLevel(
				code, 3);

		// 设置子节点列表
		for (FaultTreeNode reasonCheckNode : reasonCheckNodes) {
			List<FaultTreeNode> childsNodes = new ArrayList<>();
			for (FaultTreeNode handleFaultNode : handleFaultTreeNodes) {
				if (handleFaultNode.getParentCode().equals(
						reasonCheckNode.getCode())) {
					childsNodes.add(handleFaultNode);
				}
			}
			reasonCheckNode.setChildCodes(childsNodes);
		}

		for (FaultTreeNode faultReasonNode : faultReasonNodes) {
			List<FaultTreeNode> childsNodes = new ArrayList<>();
			for (FaultTreeNode reasonCheckNode : reasonCheckNodes) {
				if (faultReasonNode.getCode().equals(
						reasonCheckNode.getParentCode())) {
					childsNodes.add(reasonCheckNode);
				}
			}
			faultReasonNode.setChildCodes(childsNodes);
		}

		for (FaultTreeNode faultTreeNode : handleFaultTreeNodes) {
			faultTreeNode.setChildCodes(null);
		}

		// 获取树根节点信息
		FTBaseInfo ftBaseInfo = getFTQBaseInfoByCode(code);
		TreeRoot treeRoot = new TreeRoot(code, ""
				+ ftBaseInfo.getProVersion(), ftBaseInfo.getMainFaultCode(),
				ftBaseInfo.getFollowFaultCode(), ftBaseInfo.getChineseName(),
				ftBaseInfo.getEnglishName(), ftBaseInfo.getTriggerCondition());

		return new TreeBean(treeRoot, faultReasonNodes, reasonCheckNodes,
				handleFaultTreeNodes);
	}

	/**
	 * 获取树中某一层的节点
	 * 
	 * @param FTCode
	 * @param level
	 * @return
	 */
	public List<FaultTreeNode> getTreeNodesByCodeAndLevel(String FTCode,
			int level) {
		List<FaultTreeNode> faultTreeNodes = new ArrayList<>();
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery(
				"select * from FT_DETAIL_INFO where root_code='" + FTCode
						+ "' and level=" + level, null);
		while (cursor.moveToNext()) {
			String code = cursor.getString(3);
			List<FaultTreeURLBean> urlBeans = new ArrayList<>();
			Cursor urlCursor = db.rawQuery(
					"select * from FT_URL_INFO where code=? and root_code=?",
					new String[] { code, FTCode });
			while (urlCursor.moveToNext()) {
				urlBeans.add(new FaultTreeURLBean(FTCode, code, urlCursor
						.getString(2), urlCursor.getString(3)));
			}
			urlCursor.close();
			FaultTreeNode faultTreeNode = new FaultTreeNode(urlBeans, FTCode,
					level, cursor.getString(2), cursor.getString(3),
					cursor.getString(4), cursor.getInt(5));
			faultTreeNodes.add(faultTreeNode);
		}
		cursor.close();
		return faultTreeNodes;
	}

	/**
	 * 在节点表和链接表中插入一棵树。
	 * 
	 * @param rootCode
	 *            故障树代码
	 * @param treeNodeMap
	 *            故障树节点图
	 * @param nodeURLMap
	 *            故障树链接图
	 */
	public void insertFT(String rootCode,
			Map<String, List<FaultTreeNode>> treeNodeMap,
			Map<String, List<FaultTreeURLBean>> nodeURLMap) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();

		for (FaultTreeNode faultTreeNode : treeNodeMap.get("faultReasonNode")) {
			ContentValues contentValues = new ContentValues();
			contentValues.put("root_code", rootCode);
			contentValues.put("level", "1");
			contentValues.put("parent_code", faultTreeNode.getParentCode());
			contentValues.put("code", faultTreeNode.getCode());
			contentValues.put("name", faultTreeNode.getName());
			contentValues.put("persentage", faultTreeNode.getPercentage());
			db.insert("FT_DETAIL_INFO", null, contentValues);
		}

		for (FaultTreeNode faultTreeNode : treeNodeMap.get("reasonCheckNode")) {
			ContentValues contentValues = new ContentValues();
			contentValues.put("root_code", rootCode);
			contentValues.put("level", "2");
			contentValues.put("parent_code", faultTreeNode.getParentCode());
			contentValues.put("code", faultTreeNode.getCode());
			contentValues.put("name", faultTreeNode.getName());
			contentValues.put("persentage", faultTreeNode.getPercentage());
			db.insert("FT_DETAIL_INFO", null, contentValues);
		}

		for (FaultTreeNode faultTreeNode : treeNodeMap.get("handleFaultNode")) {
			ContentValues contentValues = new ContentValues();
			contentValues.put("root_code", rootCode);
			contentValues.put("level", "3");
			contentValues.put("parent_code", faultTreeNode.getParentCode());
			contentValues.put("code", faultTreeNode.getCode());
			contentValues.put("name", faultTreeNode.getName());
			contentValues.put("persentage", faultTreeNode.getPercentage());
			db.insert("FT_DETAIL_INFO", null, contentValues);
		}

		for (FaultTreeURLBean faultTreeURLBean : nodeURLMap
				.get("faultReasonURL")) {
			ContentValues contentValues = new ContentValues();
			contentValues.put("root_code", rootCode);
			contentValues.put("code", faultTreeURLBean.getCode());
			contentValues.put("name", faultTreeURLBean.getName());
			contentValues.put("url", faultTreeURLBean.getUrl());
			db.insert("FT_URL_INFO", null, contentValues);
		}

		for (FaultTreeURLBean faultTreeURLBean : nodeURLMap
				.get("reasonCheckURL")) {
			ContentValues contentValues = new ContentValues();
			contentValues.put("root_code", rootCode);
			contentValues.put("code", faultTreeURLBean.getCode());
			contentValues.put("name", faultTreeURLBean.getName());
			contentValues.put("url", faultTreeURLBean.getUrl());
			db.insert("FT_URL_INFO", null, contentValues);
		}

		for (FaultTreeURLBean faultTreeURLBean : nodeURLMap
				.get("handleFaultURL")) {
			ContentValues contentValues = new ContentValues();
			contentValues.put("root_code", rootCode);
			contentValues.put("level", "3");
			contentValues.put("code", faultTreeURLBean.getCode());
			contentValues.put("name", faultTreeURLBean.getName());
			contentValues.put("url", faultTreeURLBean.getUrl());
			db.insert("FT_URL_INFO", null, contentValues);
		}
	}

	/**
	 * 根据code删除节点表和链接表中的相关树的纪录
	 * 
	 * @param code
	 *            故障树代码
	 */
	public void deleteFTByCode(String code) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.delete("FT_DETAIL_INFO", "root_code=?", new String[] { code });
		db.delete("FT_URL_INFO", "root_code", new String[] { code });
	}

	// FT_URL_INFO故障树节点链接表===============================

	// 其他===========================

	/**
	 * 得到数据库文件的大小
	 * 
	 * @return 数据库文件大小
	 */
	public float getDBSize() {
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		File file = new File(db.getPath());
		return (float) file.length();
	}
}
