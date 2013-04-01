package com.common.method;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeansException;

/*
 import org.apache.poi.hssf.usermodel.HSSFCell;
 import org.apache.poi.hssf.usermodel.HSSFRow;
 import org.apache.poi.hssf.usermodel.HSSFSheet;
 import org.apache.poi.hssf.usermodel.HSSFWorkbook;
 */

public class PubMethod {
	
	private static boolean DEBUG = false;
	
	public static void toPrintln(Object content) {
		if(DEBUG)
		{
			System.out.println("Print at "+(new Date()) + ": <<<"+(content==null?"null":content.toString())+">>>");
		}
	}
	public static void toPrintln() {
		if(DEBUG)
		{
			System.out.println();
		}
	}
	
	/**
	 * 将iSource转为长度为iArrayLen的byte数组，字节数组的低位是整型的低字节位
	 * @param iSource
	 * @param iArrayLen
	 * @return
	 */
	public static byte[] toByteArray(int iSource, int iArrayLen)
	{
		byte[] bLocalArr = new byte[iArrayLen];
		for (int i = 0; (i < 4) && (i < iArrayLen); i++)
		{
			bLocalArr[i] = (byte) (iSource >> 8 * i & 0xFF);
		}
		return bLocalArr;
	}
	
	/**
	 * 将iSource转为长度为iArrayLen的byte数组，字节数组的低位是long的低字节位
	 * @param iSource
	 * @param iArrayLen
	 * @return
	 */
	public static byte[] toByteArray(long iSource, int iArrayLen)
	{
		byte[] bLocalArr = new byte[iArrayLen];
		for (int i = 0; (i < 8) && (i < iArrayLen); i++)
		{
			bLocalArr[i] = (byte) (iSource >> 8 * i & 0xFF);
		}
		return bLocalArr;
	}

	/**
	 * 将byte数组bRefArr转为一个整数,字节数组的低位是整型的低字节位
	 * @param bRefArr
	 * @return
	 */
	public static int toInt(byte[] bRefArr)
	{
		int iOutcome = 0;
		byte bLoop;
		for (int i = 0; i < 4; i++)
		{
			bLoop = bRefArr[i];
			iOutcome += (bLoop & 0xFF) << (8 * i);
		}
		return iOutcome;
	}
	
	/**
	 * 将byte数组bRefArr转为一个长整数,字节数组的低位是整型的低字节位
	 * @param bRefArr
	 * @return
	 */
	public static long toLong(byte[] bRefArr)
	{
		int iOutcome = 0;
		byte bLoop;
		for (int i = 0; i < 8; i++)
		{
			bLoop = bRefArr[i];
			iOutcome += (bLoop & 0xFF) << (8 * i);
		}
		return iOutcome;
	}



	public static String formatDouble(DecimalFormat formater, Double val) {
		if (isEmpty(formater))
			formater = new DecimalFormat("#.00");
		if (val == null)
			return "0.00";
		return formater.format(val.doubleValue());
	}

	public static double convertDouble2double(Double val) {
		if (isEmpty(val)) {
			return 0.0;
		}
		return val.doubleValue();
	}

	// 异常数值回写
	public void ReWrtOnError(String actionname, String methodname,
			HttpServletRequest request) {
		String url = actionname + "?method=" + methodname;
		if (!url.startsWith("/")) {
			url = "/" + actionname + "?method=" + methodname;
		}
		Map paramMap = request.getParameterMap();
		if (!PubMethod.isEmpty(paramMap)) {
			Iterator keysetiterator = paramMap.keySet().iterator();
			for (; keysetiterator.hasNext();) {
				String key = "" + keysetiterator.next();
				if ("bizdivCondition".equals(key))
					continue;
				String val = null;
				try {
					val = request.getParameter(key);
				} catch (Exception e) {
				}
				if ((val != null) && !val.equals("")) {
					url += "&" + key + "=" + val;
				}
			}
		}
		request.setAttribute("retpath", url);// 抛异常

	}

	/*
	 * 把数值转换为List author:
	 * 
	 */
	public static List Array2List(Object[] objarr) {
		if (isEmpty(objarr))
			return null;
		else {
			List list = new ArrayList();
			int len = objarr.length;
			for (int index = 0; index < len; index++) {
				list.add(objarr[index]);
			}
			return list;
		}
	}

	/*
	 * 初始化延迟加载对象。 @author:
	 * 
	 */
	public static void initialize(Object obj) {

		try {
			org.hibernate.Hibernate.initialize(obj);
		} catch (org.hibernate.ObjectNotFoundException e) {
			// e.printStackTrace();
			obj = null;
		} catch (Exception e) {
			// e.printStackTrace();
			obj = null;
		}
	}
	
	/**
	 * 生成效果统计的按时间段的sql
	 * @param columnName
	 * @param date
	 * @param timeSpan 时间间隔 10 30 60  （单位：分钟）
	 * @return
	 */
	public static String getEffcSql(String columnName, Date date, int timeSpan)
	{
		StringBuffer rtnStr = new StringBuffer("case ");
		String dateStr = PubMethod.dateToString(date, "yyyy-MM-dd");//"yyyy-MM-dd HH:mm:ss"
		dateStr += " 00:00:00";
		for(int i = 0;i < 24*60/timeSpan - 1;i++)
		{
			String nextDate = PubMethod.getSecStringGap(dateStr, timeSpan*60, "yyyy-MM-dd HH:mm:ss");
			rtnStr.append("when ").append(columnName).append(" >= '").append(dateStr).append("' and ")
			      .append(columnName).append(" < '").append(nextDate).append("' then '").append(dateStr.substring(11, 16))
			      .append("-").append(nextDate.substring(11, 16)).append("' ");
			//toPrintln(dateStr + " " + nextDate);
			//toPrintln(rtnStr.toString());
			dateStr = nextDate;
		}
		//toPrintln(dateStr);
		rtnStr.append(" else '").append(dateStr.substring(11, 16)).append("-00:00' end");
		return rtnStr.toString();
	}
	
	/**
	 * 生成效果统计的按时间段的sql
	 * @param columnName
	 * @param dateStr
	 * @param timeSpan 时间间隔 10 30 60  （单位：分钟）
	 * @return
	 */
	public static String getEffcSql(String columnName, String dateStr, int timeSpan)
	{
		Date date = PubMethod.stringToDate(dateStr);
		return PubMethod.getEffcSql(columnName, date, timeSpan);
	}

	/*
	 * 判断结果集是否唯一 @author:
	 */
	public static boolean isUnique(List list) {
		if (!isEmpty(list)) {
			if (list.size() == 1) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/*
	 * 判断结果集是否唯一 @author:
	 */
	public static boolean isUnique(Collection collection) {
		if (!isEmpty(collection)) {
			if (collection.size() == 1) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/*
	 * 判断结果集是否唯一 @author:
	 */
	public static boolean isUnique(Set set) {
		if (!isEmpty(set)) {
			if (set.size() == 1) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/*
	 * 判断结果集是否唯一 @author:
	 */
	public static boolean isUnique(Map map) {
		if (!isEmpty(map)) {
			if (map.size() == 1) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/*
	 * 获取当前时间(时间戳类型) timestamp. @author:
	 */
	public static Timestamp getCurrentTimestamp() {
		return new java.sql.Timestamp(System.currentTimeMillis());
	}

	/*
	 * 返回担保人中文名称 @author:
	 */
	public static String getWarrantorTypeName(String warrantorType) {
		if (warrantorType == null)
			return "";
		if (warrantorType.trim().equals("0")) {
			return "无";
		} else if (warrantorType.trim().equals("1")) {
			return "法人";
		} else if (warrantorType.trim().equals("2")) {
			return "自然人";
		} else {
			return "";
		}
	}

	/*
	 * 获取字符串 @author:
	 */
	public static String getString(Object obj) {
		if (obj == null || obj.equals("null"))
			return "";
		else
			return obj.toString();
	}

	/*
	 * 把List里头的对象转换为以特定分割符号分割的字符串。
	 * 
	 * 主要用于生成 sql/hql 语句中的In 子句。
	 * 
	 * author:chen-huiming.
	 */
	public static String getList2StringBySplitter(List listObj, String Splitter) {
		if (Splitter == null)
			Splitter = ",";
		if (Splitter.equals(""))
			Splitter = ",";
		String tmp = " ";
		if (listObj == null || listObj.size() == 0)
			return "";
		int objSize = listObj.size();

		for (int index = 0; index < objSize; index++) {
			if (index != objSize - 1) {
				if (listObj.get(index) instanceof String)
					tmp += "'" + listObj.get(index) + "'" + Splitter;
				else
					tmp += listObj.get(index) + Splitter;
			} else {
				if (listObj.get(index) instanceof String)
					tmp += "'" + listObj.get(index) + "'";
				else
					tmp += listObj.get(index);
			}

		}
		return tmp;
	}

	/*
	 * 获取 给定格式 时间字符串。
	 * 
	 */
	public static String getOraFormatDateString(String dateStr, String format) {
		String retStr;
		if (dateStr == null || dateStr.equals(""))
			dateStr = getCurSysDate();
		retStr = " to_date('" + dateStr + "','" + format + "')";
		return retStr;
	}

	/*
	 * 获取 时间比较字符串。
	 */
	public static String getOraDateCompString(String dateMin, String dateMax,
			String fieldName) {
		String retStr = "";
		if (fieldName == null || fieldName.trim().equals(""))
			return null;
		retStr += " and " + fieldName + ">="
				+ getOraFormatDateString(dateMin, "YYYY-MM-DD");
		retStr += " and " + fieldName + "<="
				+ getOraFormatDateString(dateMax, "YYYY-MM-DD");
		return retStr;
	}

	/*
	 * 获取对象的所有信息 @author:chenhm
	 */
	public static Map getObjFieldVals(Object obj) {
		if (isEmpty(obj))
			return null;
		Map result = null;
		Field[] Fieldsarray = obj.getClass().getDeclaredFields();

		int fildsLength = 0;
		org.springframework.beans.BeanWrapper beanWrapper = new org.springframework.beans.BeanWrapperImpl(
				obj);
		if (!PubMethod.isEmpty(Fieldsarray)) {
			fildsLength = Fieldsarray.length;
			result = new HashMap(fildsLength);
			for (int index = 0; index < fildsLength; index++) {
				String fieldname = Fieldsarray[index].getName();
				Object objval = new Object();
				if (!fieldname.equals("serialVersionUID")
						&& !fieldname.toUpperCase().equals("CGLIB$BOUND")) {
					try {
						objval = beanWrapper.getPropertyValue(fieldname);
					} catch (BeansException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (!PubMethod.isEmpty(objval)) {
					result.put(fieldname, objval);
				}
			}
		}
		return result;
	}

	public static Map getObjFieldVals(Object obj, Class cls) {
		if (isEmpty(obj))
			return null;
		Map result = null;
		Field[] Fieldsarray = cls.getDeclaredFields();

		int fildsLength = 0;
		org.springframework.beans.BeanWrapper beanWrapper = new org.springframework.beans.BeanWrapperImpl(
				obj);
		if (!PubMethod.isEmpty(Fieldsarray)) {
			fildsLength = Fieldsarray.length;
			result = new HashMap(fildsLength);
			for (int index = 0; index < fildsLength; index++) {
				String fieldname = Fieldsarray[index].getName();
				Object objval = new Object();
				String sobjval = "";
				if (!fieldname.equals("serialVersionUID")) {
					if (!isEmpty(beanWrapper.getPropertyValue(fieldname))) {
						objval = beanWrapper.getPropertyValue(fieldname);
						sobjval = objval.toString();
					}
				}
				if (!PubMethod.isEmpty(sobjval)) {
					result.put(fieldname, sobjval);
				} else {
					result.put(fieldname, "");
				}
			}
		}
		return result;
	}

	/*
	 * 获取包装后的对象. @author:
	 */
	public static BeanWrapper getWrapperedObj(Object obj) {
		BeanWrapper beanWrapper = new org.springframework.beans.BeanWrapperImpl(
				obj);
		return beanWrapper;
	}

	public static void initObject(Object srcObj) {

		if (srcObj == null)
			return;
		Method[] method = srcObj.getClass().getDeclaredMethods();
		for (int index = 0; index < method.length; index++) {
			String methodName = method[index].getName();
			methodName = (methodName == null) ? "" : methodName.trim();
			if (methodName.startsWith("get")) {
				String aa = method[index].getReturnType().getName();
				if (method[index].getReturnType().getName().equals(
						"java.lang.String"))
					;
				else
					continue;
				String fieldName = methodName.substring(3);// cut 'get'
				Method desMethod = getMethodByName(srcObj, "set" + fieldName);
				Object val = null;
				try {

					val = method[index].invoke(srcObj, null);
					if (val == null || val.toString().equals("null"))
						desMethod.invoke(srcObj, new Object[] { "" });
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/*
	 * 获取包装后的对象 @author:
	 */
	public static List getWrapperedObjList(List list) {
		if (isEmpty(list))
			return null;
		List result = new ArrayList();
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			result.add(getWrapperedObj(iterator.next()));
		}
		return result;
	}

	public static Object getFieldValue(final Object obj, String fieldName) {
		Object val = null;
		try {
			Method[] method = obj.getClass().getDeclaredMethods();
			for (int index = 0; index < method.length; index++) {
				PubMethod.toPrintln("method[index].getName():"
						+ method[index].getName());
				if (method[index].getName().equalsIgnoreCase("get" + fieldName)) {
					PubMethod.toPrintln(fieldName);
					val = method[index].invoke(obj, null);
					PubMethod.toPrintln(fieldName + ":" + val);
				}
			}
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return val;
	}

	/**
	 * 检查类中是否有指定的方法名
	 * 
	 * @author wangjianhua
	 * @param obj
	 * @param methodName
	 * @return
	 */
	public static boolean hasMethodByName(Object obj, String methodName) {
		boolean hasMethod = false;
		methodName = (methodName == null) ? "" : methodName.trim();
		if (isEmpty(obj) || isEmpty(methodName)) {
			return hasMethod;
		}

		Method[] method = obj.getClass().getDeclaredMethods();
		for (int index = 0; index < method.length; index++) {
			// PubMethod.toPrint("method[index].getName():"+method[index].getName());
			String tmpMethodName = method[index].getName();
			tmpMethodName = (tmpMethodName == null) ? "" : tmpMethodName.trim();
			if (tmpMethodName.equals(methodName)) {
				hasMethod = true;
				break;
			}
		}
		return hasMethod;
	}

	/**
	 * 通过指定的方法名找到Method
	 * 
	 * @author wangjianhua
	 * @param obj
	 * @param methodName
	 * @return
	 */
	public static Method getMethodByName(Object obj, String methodName) {
		Method resMethod = null;
		methodName = (methodName == null) ? "" : methodName.trim();
		if (isEmpty(obj) || isEmpty(methodName)) {
			return resMethod;
		}

		Method[] method = obj.getClass().getDeclaredMethods();
		for (int index = 0; index < method.length; index++) {
			// PubMethod.toPrint("method[index].getName():"+method[index].getName());
			String tmpMethodName = method[index].getName();
			tmpMethodName = (tmpMethodName == null) ? "" : tmpMethodName.trim();
			if (tmpMethodName.equals(methodName)) {
				resMethod = method[index];
				break;
			}
		}
		return resMethod;
	}

	/**
	 * po对象的拷贝，用于业务po对象给该业务历史po对象赋值
	 * （注：相同名字的属性其类型须一致；且属性的类型最好不是原始类型，类似int.long，最好使用Integer.Long ...）
	 * 
	 * @author wangjianhua
	 * @param srcObj
	 * @param desObj
	 * @return
	 */
	public static void copyPersistantObject(Object srcObj, Object desObj) {
		if (isEmpty(srcObj) || isEmpty(desObj)) {
			System.err
					.println("NullPointerException at PubMethod.copyPersistantObject\n...........");
			// throw new NullPointerException();
		}
		Method[] method = srcObj.getClass().getDeclaredMethods();
		for (int index = 0; index < method.length; index++) {
			// PubMethod.toPrint("method[index].getName():"+method[index].getName());
			String methodName = method[index].getName();
			methodName = (methodName == null) ? "" : methodName.trim();
			if (methodName.startsWith("get")
					&& hasMethodByName(desObj, methodName)) {
				String fieldName = methodName.substring(3);// cut 'get'
				Method desMethod = getMethodByName(desObj, "set" + fieldName);
				Object val = null;
				try {
					val = method[index].invoke(srcObj, null);
					if (val == null || "".equals(val.toString().trim())) {
						continue;
					}
					desMethod.invoke(desObj, new Object[] { val });
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 将PO对象的属性和值拼为sql 主要用在：后台界面中获得的数据来自biz，在界面上显示列表后需要后续处理时使用（如：代码申请中的代码发布）
	 * 
	 * @param object
	 * @return
	 */
	public static String getObjectParam(Object object) {
		StringBuffer sb = new StringBuffer("");
		String flag = "<-|->";
		Method[] method = object.getClass().getDeclaredMethods();
		boolean hasParam = false;
		for (int index = 0; index < method.length; index++) {
			// PubMethod.toPrint("method[index].getName():"+method[index].getName());
			String methodName = method[index].getName();
			methodName = (methodName == null) ? "" : methodName.trim();
			if (methodName.startsWith("get")) {
				Object val = null;
				try {
					val = method[index].invoke(object, null);
					val = (val == null) ? new String("") : val;
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
				String fieldName = smallFistLetter(methodName.substring(3));
				sb.append(fieldName + "=" + val.toString()).append(flag);
				hasParam = true;
			}
		}
		String result = sb.toString();
		if (hasParam == true) {
			result = result.substring(0, result.length() - flag.length());
		}
		// PubMethod.toPrint(object.getClass().getName() + " - " + result);
		// com.sinosure.sol.persistence.po.VbCodeapply -
		// remark=<|>clientno=070001<|>bnsid=1145<|>bnsstate=109203<|>bnsstage=2<|>applicantno=008<|>chnname=kit
		// 猫<|>codeapplyid=4741<|>bizclientno=BIZ<|>bizacceptno=<|>applytype=1<|>bizbackreason=<|>deleteflag=1<|>applicant=操作员1<|>applydate=2007-04-27
		// 09:14:57.0<|>engname=hello kit<|>countrycode=<|>builddate=2007-04-24
		// 16:05:09.0<|>objectid=3607<|>approveopinion=<|>chnaddr=<|>engaddr=<|>approvecode=<|>hobjectid=5054<|>objecttype=3<|>bnsname=银行代码申请<|>clientchnname=买方名称<|>clientengname=英文名称
		// <|>acceptdate=<|>accepter=<|>accepterno=<|>builder=操作员1<|>builderno=008<|>clientsigner=<|>clientsignerno=<|>clientsigntime=<|>repealdate=<|>repealer=<|>repealerno=<|>repealreason=<|>solsigner=<|>solsignerno=<|>solsigntime=<|>bnscode=010902<|>
		return result;
	}

	/**
	 * 将通过方法 getObjectParam 生成的字符串组装成 PO对象，getObjectParam方法的反操作
	 * 只适合属性类型都是String类型的PO对象
	 * 
	 * @param object
	 * @param params
	 */
	public static Object getObjectFromParams(Object object, String params) {
		params = (params == null) ? "" : params.trim();
		String flag = "<-|->";
		String[] paramStrArr = params.split(flag);
		for (int index = 0; index < paramStrArr.length; index++) {
			String param = paramStrArr[index];
			String fieldName = "";
			String value = "";
			int pos = param.indexOf("=");
			if (pos != -1) {
				fieldName = param.substring(0, pos);
				value = param.substring(pos + 1);
				try {
					Method method = object.getClass().getDeclaredMethod(
							"set" + capitalFistLetter(fieldName),
							new Class[] { Class.forName("java.lang.String") });
					method.invoke(object, new Object[] { value });
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return object;
	}

	/**
	 * 字符串的首字母小写
	 * 
	 * @param val
	 * @return
	 */
	public static String smallFistLetter(String val) {
		val = (val == null) ? "" : val.trim();
		if ("".equals(val)) {
			return "";
		}
		val = val.substring(0, 1).toLowerCase() + val.substring(1);
		return val;
	}

	/**
	 * 字符串的首字母大写
	 * 
	 * @param val
	 * @return
	 */
	public static String capitalFistLetter(String val) {
		val = (val == null) ? "" : val.trim();
		if ("".equals(val)) {
			return "";
		}
		val = val.substring(0, 1).toUpperCase() + val.substring(1);
		return val;
	}
	
	/**
	 * List 深拷贝:序列化|反序列化方法
	 * 注：记着放到集合中的元素要能够序列化，所以必须实现Serializable接口。
	 * @param src
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
    public static List copyListBySerialize(List src) throws IOException, ClassNotFoundException{
    	if(PubMethod.isEmpty(src)) return null;
    	
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(src);
    
        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in =new ObjectInputStream(byteIn);
        List dest = (List)in.readObject();
        return dest;
    }
    
    /**
     * List 浅拷贝
     * @param src
     * @param dest
     */
    public static void copyCollectionByAdd(Collection src, Collection dest){
    	if(PubMethod.isEmpty(src) || PubMethod.isEmpty(dest)) return;
    	
    	//for (int i = 0 ; i< src.size() ;i++) {//jdk 1.4
    	for (Object obj : src) {//jdk 1.5 以上版本
            //Object obj = src.get(i);
            dest.add(obj);
        }
    }    


	/*
	 * 对象间值的相互拷贝.
	 */
	public static void copyPropeties(Object srcObj, Object destObj) {
		try {
			org.springframework.beans.BeanUtils.copyProperties(srcObj, destObj);
		} catch (BeansException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 判断是否存在 单引号。
	public static boolean isContainSingleQuotes(String str) {
		if (str.indexOf("'") >= 0)
			return true;
		else
			return false;
	}

	// 判断是否为空。
	public static boolean isEmpty(String Value) {
		return (Value == null || Value.trim().equals(""));
	}

	/*
	 * @function:判空 @author:
	 */
	public static boolean isEmpty(List list) {
		if (list == null || list.size() == 0)
			return true;
		else
			return false;
	}

	/*
	 * @function:判空 @author:
	 */
	public static boolean isEmpty(Set set) {
		if (set == null || set.size() == 0)
			return true;
		else
			return false;
	}

	/*
	 * @function:判空 @author:
	 */
	public static boolean isEmpty(Map map) {
		if (map == null || map.size() == 0)
			return true;
		else
			return false;
	}

	// 判断是否为空。
	public static boolean isEmpty(Object Value) {
		if (Value == null)
			return true;
		else
			return false;
	}

	public static boolean isEmpty(Double value) {
		if (value == null || value.doubleValue() == 0.0)
			return true;
		else
			return false;
	}

	// 判断是否为空。
	public static boolean isEmpty(Long obj) {
		if (obj == null || obj.longValue() == 0)
			return true;
		else
			return false;
	}

	// 判断是否为空。
	public static boolean isEmpty(Object[] Value) {
		if (Value == null || Value.length == 0)
			return true;
		else
			return false;
	}

	// 返回有效状态值
	public static int validState() {
		return 1;
	}

	// 返回无效状态值
	public static int invalidState() {
		return 0;
	}

	// 判断状态是否有效。0无效、1有效、9删除。
	public static boolean isValid(int state) {
		if (state == 1)
			return true;
		else
			return false;
	}

	// Set集合到List的转换
	public static List getList(Set set) {
		List list = new ArrayList();
		Iterator iterator = set.iterator();
		while (iterator.hasNext()) {
			list.add(iterator.next());
		}
		return list;
	}

	/**
	 * 把List转换为Set
	 * 
	 * @param set
	 * @return
	 */
	public static Set convertList2Set(List list) {
		if (list == null || list.size() == 0)
			return null;
		Set set = new HashSet();
		Iterator iterator = list.iterator();
		while (iterator.hasNext()) {
			set.add(iterator.next());
		}
		return set;
	}

	// 返回删除状态值。
	public static int getDeletedState() {
		return 9;
	}

	// debug sql
	public static void debugHQL(org.apache.log4j.Logger logger,
			org.hibernate.Query queryObject, String[] params, Object[] values) {
		if (logger.isDebugEnabled()) {
			String str = "HQL:\n" + queryObject.getQueryString();
			if(params.length>0)
			{
				str += "\n params: ";
				for (int index = 0; index < params.length; index++) {
					str += "{"+params[index] + " = " + values[index]+"}";
				}
			}
			logger.debug(str);
		}
	}

	public static void debugHQL(org.apache.log4j.Logger logger,
			org.hibernate.Query queryObject, Map paramsMap) {
		if (!logger.isDebugEnabled())
			return;
		if(queryObject.getQueryString().contains("select max(id) from BasSysmessage where")) return;

		String str = "\n*HQL: " + queryObject.getQueryString();
		if (paramsMap != null && paramsMap.size() > 0)
		{
			str += "\n   >: ";
			Iterator iterator = paramsMap.keySet().iterator();
			while (iterator.hasNext()) {
				String param = "" + iterator.next();
				Object value = paramsMap.get(param);
				logger.debug(param + " = " + value );
				str += "{"+param + " = " + value+"}";
			}
		}
		logger.debug(str);
	}

	public static void debugHQL(org.apache.log4j.Logger logger, Object obj) {

	}

	// 写日志文件。(头信息，内容信息，绝对路径）
	public void wrtFile(String[] heads, List content, String AbsolutPath) {
		// 建立文件对象。
		File file = new File(AbsolutPath);
		FileWriter fileWriter = null;
		// 判断文件是否存在
		if (file.exists()) {
			try {
				// 存在则删除之。
				file.delete();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// 重新新建日志文件。
		try {
			file.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 建立FileWriter对象（在file对象的基础上）。
		try {
			fileWriter = new FileWriter(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 写文件头信息。
		for (int headCount = 0; headCount < heads.length; headCount++) {
			try {
				fileWriter.write(heads[headCount]);
				fileWriter.write("\t\t");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// 换行
		try {
			fileWriter.write("\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 写内容
		for (int contentCount = 0; contentCount < content.size(); ++contentCount) {
			// 每行内容
			String[] tempRowContent = (String[]) content.get(contentCount);
			// 写入一行信息。
			for (int cnt = 0; cnt < tempRowContent.length; cnt++) {
				try {
					// 滤去无效字符。（filter out unqulified characters).
					if (("" + tempRowContent[cnt]).equals("null")) {
						tempRowContent[cnt] = "";
					}
					fileWriter.write(tempRowContent[cnt]);
					fileWriter.write("\t\t");
					fileWriter.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				fileWriter.write("\n");
				if (contentCount % 2000 == 0)
					fileWriter.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try {
			file = null;
			fileWriter.close();
			fileWriter = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * 按要求分割字符串.
	 * 
	 */
	public static Object[] splitString(String srcStr, String splitter) {
		if(srcStr == null) return new String[]{""};
		String[] tmpArr = srcStr.split(splitter);
		if (tmpArr == null || tmpArr.length == 0) {
			return new String[]{""};
		} else {
			for (int index = 0; index < tmpArr.length; index++) {
				tmpArr[index] = tmpArr[index].trim();
			}
			return tmpArr;
		}
	}

	/**
	 * 分隔字符串
	 * 
	 * @param src
	 * @param token
	 * @author wangjh
	 * @return
	 */
	public static String[] splits(String src, String token) {
		String[] res = null;
		if (src == null || "".equals(src.trim()) || token == null) {
			res = new String[0];
			return res;
		}
		token = token.trim();
		StringBuffer str = new StringBuffer();
		if (!"".equals(token)) {
			for (int i = src.indexOf(token); i != -1; i = src.indexOf(token)) {
				str.append(" " + src.substring(0, i + 1) + " ");
				src = src.substring(i + 1);
			}
		} else {
			token = " ";
			str.append(src);
		}

		StringTokenizer st = new StringTokenizer(str.toString(), token);
		res = new String[st.countTokens()];
		int j = 0;
		while (st.hasMoreElements()) {
			res[j] = ((String) st.nextElement()).trim();
			// PubMethod.toPrint("Token: <" + res[j] + ">");
			j++;
		}
		st = null;
		return res;
	}

	// 使用常用分割符号分割字符串.(,，.。空格|)
	public static List splitStringWithUsualTokens(String srcStr) {
		List result = new ArrayList();
		if (PubMethod.isEmpty(srcStr))
			return result;

		// srcStr =srcStr.replaceAll(".", ",");
		srcStr = srcStr.replaceAll("，", ",");
		srcStr = srcStr.replaceAll("。", ",");
		srcStr = srcStr.replaceAll("、", ",");
		srcStr = srcStr.replaceAll("@", ",");
		srcStr = srcStr.replaceAll("、", ",");
		srcStr = srcStr.replaceAll("/", ",");
		// srcStr =srcStr.replaceAll("|", ",");
		srcStr = srcStr.replaceAll(" ", ",");
		srcStr = srcStr.replaceAll("\t", ",");

		Object[] temp = splitString(srcStr, ",");
		if (!PubMethod.isEmpty(temp)) {
			for (int index = 0; index < temp.length; index++) {
				if ("".equals(("" + temp[index]).trim())) {
					continue;
				}
				if (",".equals(("" + temp[index]).trim())) {
					continue;
				}
				result.add(temp[index]);
			}
		} else {
			result.add(srcStr);
		}

		return result;
	}

	public static String formatDateTime(Date date, String format) {
		SimpleDateFormat outFormat = new SimpleDateFormat(format);
		return outFormat.format(date);
	}

	
	public static String formatDateTime(Timestamp ts, String format) {
		SimpleDateFormat outFormat = new SimpleDateFormat(format);
		return outFormat.format(ts);
	}

	public static String formatDateTime(Date date, String format,
			String timeZone) {
		SimpleDateFormat outFormat = new SimpleDateFormat(format);

		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(date.getTime());
		c.set(Calendar.MILLISECOND, 0);
		c.setTimeZone(TimeZone.getTimeZone(timeZone));
		return outFormat.format(c.getTime());
	}
	public static String formatDate(Date date){
		SimpleDateFormat outFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");		
		return outFormat.format(date);
	}
	/**
	 * 验证时间格式是否为yyyy-MM-dd HH:mm:ss，注意2011-1-1 23:59:59和2011-01-01 23:59:59认为都是合法的
	 * @param date
	 * @return
	 */
	public static boolean checkDateFormat(String date){
		
		Pattern p=Pattern.compile("^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s((([0-1][0-9])|(2?[0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");
		Matcher matcher =p.matcher(date);
		if(matcher.find()){
			//System.out.println("right");
			return true;
		}else{
			//System.out.println("wrong");
			return false;
		}
	}
	public static boolean isYesterdayTime(Date atime, String timeZone) {
		Calendar cld = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
		cld.set(Calendar.MILLISECOND, 0);
		cld.add(Calendar.DAY_OF_MONTH, -1);
		int yyear = cld.get(Calendar.YEAR);
		int ymonth = cld.get(Calendar.MONTH);
		int yday = cld.get(Calendar.DAY_OF_MONTH);

		Calendar c = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
		c.setTime(atime);
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		int second = c.get(Calendar.SECOND);

		if (year == yyear && month == ymonth && day == yday && second > 0) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isTodayTime(Date atime, String timeZone) {
		Calendar cld = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
		cld.set(Calendar.MILLISECOND, 0);
		int nowyear = cld.get(Calendar.YEAR);
		int nowmonth = cld.get(Calendar.MONTH);
		int nowday = cld.get(Calendar.DAY_OF_MONTH);

		Calendar c = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
		c.setTime(atime);
		c.set(Calendar.MILLISECOND, 0);
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		int second = c.get(Calendar.SECOND);

		if (year == nowyear && month == nowmonth && day == nowday && second > 0) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isTodayTime(long atime, String timeZone) {
		return isTodayTime(new Date(atime), timeZone);
	}

	public static boolean isYesterdayTime(long atime, String timeZone) {
		return isYesterdayTime(new Date(atime), timeZone);
	}

	public static boolean isTodayTime(long atime) {
		Calendar cld = Calendar.getInstance();
		// cld.setTime(new Date());
		int year = cld.get(Calendar.YEAR);
		int month = cld.get(Calendar.MONTH);
		int day = cld.get(Calendar.DAY_OF_MONTH);
		Calendar todaycld = Calendar.getInstance();
		todaycld.set(year, month, day, 0, 0, 0);
		if (atime >= todaycld.getTime().getTime()) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isLastdayTime(long atime) {
		Calendar cld = Calendar.getInstance();
		// cld.setTime(new Date());
		cld.add(Calendar.DAY_OF_MONTH, -1);
		int year = cld.get(Calendar.YEAR);
		int month = cld.get(Calendar.MONTH);
		int day = cld.get(Calendar.DAY_OF_MONTH);
		Calendar lastdaycld = Calendar.getInstance();
		lastdaycld.set(year, month, day, 0, 0, 0);
		if (atime >= lastdaycld.getTime().getTime()) {
			return true;
		} else {
			return false;
		}
	}

	public static String getFileExt(String fileName) {
		if (fileName != null) {
			String fileExt = "";
			fileName = fileName.toLowerCase();
			int index = fileName.lastIndexOf(".");
			fileExt = fileName.substring(index, fileName.length());
			return fileExt;
		} else {
			return "";
		}
	}

	public static List stringValues2List(String[] values) {
		List l = new ArrayList();
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				l.add(values[i]);
			}
		} else {
			l.add("0");
		}
		return l;
	}

	public static String getWebRealPath(HttpServletRequest request) {
		StringBuffer sb = new StringBuffer();
		sb.append("http://");
		sb.append(request.getServerName());
		if (request.getServerPort() != 80) {
			sb.append(":");
			sb.append(request.getServerPort());
		}

		// sb.append(request.getContextPath());
		// sb.append("/");
		return sb.toString();
	}

	// 获取测试值
	public static String getTestVal() {
		return "" + (System.currentTimeMillis() / 1000000);
	}

	// 获取测试值
	public static long getTestValL() {
		return System.currentTimeMillis() / 100000000;
	}
	//得到当前年
	public static String getCurYear(){
		String date = PubMethod.getCurSysDate();
		return date.substring(0,4);
	}
	//得到上周日日期
	public static String getLastWeekend(){
		Calendar date = Calendar.getInstance();		
		//int inte = date.get(Calendar.DAY_OF_WEEK);
		//date.add(Calendar.DATE, 0-inte);
		date.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		return formatter.format(date.getTime());		
	}
	
	//	得到前两周日期
	public static String getLastDate(){
		Calendar date = Calendar.getInstance();		
		date.add(Calendar.DATE, -15);    //得到前十五天
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		return formatter.format(date.getTime());			
	}
	
	//	得到当前时间的前x或后x小时的时间
	public static String getLastHours(int x){
		Calendar date = Calendar.getInstance();		
		date.add(Calendar.HOUR, x);    //得到x小时之后或之前的时间
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return formatter.format(date.getTime());			
	}
	
	
	
	public static String getLastDateIndex(int index){
		Calendar date = Calendar.getInstance();		
		date.add(Calendar.DATE, index);    
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		return formatter.format(date.getTime());			
	}
	
	//	得到当前日期的后两周日期
	public static String getAfterDate(){
		Calendar date = Calendar.getInstance();		
		date.add(Calendar.DATE, 15);    //得到后十五天
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		return formatter.format(date.getTime());			
	}
	
//得到当前日期指定的日期
	public static String getAfterDate(int num){
		
		Calendar date = Calendar.getInstance();		
		date.add(Calendar.DATE, num);    
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		return formatter.format(date.getTime());			
	}
	
	
	public static String getAfterDateYear(){
		Calendar date = Calendar.getInstance();		
		date.add(Calendar.DATE,3650);    //十年后的日期
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		return formatter.format(date.getTime());			
	}
	
	
	//	得到上周一日期
	public static String getLastMondy(){
		Calendar date = Calendar.getInstance();		
		int inte = date.get(Calendar.DAY_OF_WEEK);
		date.add(Calendar.DATE, 0-inte);
		date.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		return formatter.format(date.getTime());			
	}
	
	//得到当前是一年中的第几周
	public static int getWeekOfYear(){
		Calendar date = Calendar.getInstance();		
		return date.get(Calendar.WEEK_OF_YEAR);
	}
	
	public static String getCurSysDate() {
		Calendar date = Calendar.getInstance();
		Date sysDate = date.getTime();
		// PubMethod.toPrint("" + sysDate);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String curDate = formatter.format(sysDate);
		// PubMethod.toPrint("" + curDate);
		return curDate;
	}
	/** 
	* 获得指定日期的前一天 
	* @param specifiedDay 
	* @return 
	* @throws Exception 
	*/ 
	public static String getSpecifiedDayBefore(String specifiedDay){ 
	
	Calendar c = Calendar.getInstance(); 
	Date date=null; 
	try { 
	date = new SimpleDateFormat("yy-MM-dd").parse(specifiedDay); 
	} catch (ParseException e) { 
	e.printStackTrace(); 
	} 
	c.setTime(date); 
	int day=c.get(Calendar.DATE); 
	c.set(Calendar.DATE,day-1); 

	String dayBefore=new SimpleDateFormat("yyyy-MM-dd").format(c.getTime()); 
	return dayBefore; 
	} 

	//获得指定日期的后一天
	public static String getSpecifiedDayAfter(String specifiedDay){ 
		Calendar c = Calendar.getInstance(); 
		Date date=null; 
		try { 
		date = new SimpleDateFormat("yy-MM-dd").parse(specifiedDay); 
		} catch (ParseException e) { 
		e.printStackTrace(); 
		} 
		c.setTime(date); 
		int day=c.get(Calendar.DATE); 
		c.set(Calendar.DATE,day+1); 

		String dayAfter=new SimpleDateFormat("yyyy-MM-dd").format(c.getTime()); 
		return dayAfter; 
	} 


	public static String getCurSysDateH() {
		Calendar date = Calendar.getInstance();
		Date sysDate = date.getTime();
		PubMethod.toPrintln("" + sysDate);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
		String curDate = formatter.format(sysDate);
		PubMethod.toPrintln("" + curDate);
		return curDate;
	}

	/**
	 * 获取系统操作时间
	 * 
	 * @param
	 * @return String
	 */
	public static String getSysOptDate() {
		Calendar date = Calendar.getInstance();
		Date sysDate = date.getTime();
		String optDate = PubMethod.dateToString(sysDate, "yyyy-MM-dd HH:mm:ss");
		return optDate;
	}
	
	/**
	 * 获取系统操作时间
	 * 
	 * @param
	 * @return String
	 */
	public static String getSysOptDate_Y() {
		Calendar date = Calendar.getInstance();
		Date sysDate = date.getTime();
		String optDate = PubMethod.dateToString(sysDate, "yyyy-MM-dd");
		return optDate;
	}
	
	/**
	 * 换算两个日期之间的小时数
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static String getHour(String time1, String time2){
		  long quot = 0;
		  long day = 0;
		  long hour = 0;
		  String dayHour = "";
		  SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			  try {
				   Date date1 = formatter.parse( time1 );
				   Date date2 = formatter.parse( time2 );
				   quot = date1.getTime() - date2.getTime();
				   quot = quot / 1000 / 60 / 60 ;
				   
				   day = quot/24;
				   hour = quot%24;
			   
			  } catch (Exception e) {
				  e.printStackTrace();
			  }
		if(day == 0){
			dayHour = hour+"小时";
		}else{
			dayHour = day+"天"+"  "+hour+"小时"  ;
		}
		return dayHour;
	}
		
	
	
	/**
	 * 换算两个日期之间的小时数
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static long getHourDay(String time1, String time2){
		  long quot = 0;
		  //long day = 0;
		  long hour = 0;
		  SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			  try {
				   Date date1 = formatter.parse( time1 );
				   Date date2 = formatter.parse( time2 );
				   quot = date1.getTime() - date2.getTime();
				   hour = quot / 1000 / 60 / 60;
				   
			  } catch (Exception e) {
				  e.printStackTrace();
			  }
		
		return hour;
	}

	public static String getSysOptDateH() {
		Calendar date = Calendar.getInstance();
		Date sysDate = date.getTime();
		String optDate = PubMethod.dateToString(sysDate, "yyyy/MM/dd HH:mm:ss");
		return optDate;
	}
	public static String getSysOptDateH_() {
		Calendar date = Calendar.getInstance();
		Date sysDate = date.getTime();
		String optDate = PubMethod.dateToString(sysDate, "yyyy-MM-dd HH:mm:ss");
		return optDate;
	}
	
	/**
	 * 获取系统操作时间
	 * 
	 * @param
	 * @return String
	 */
	public static String getSysOptTime() {
		Calendar date = Calendar.getInstance();
		Date sysDate = date.getTime();
		String optDate = PubMethod.dateToString(sysDate, "HH:mm:ss");
		return optDate;
	}
	
	/**
	 * 获取系统操作当前小时
	 * 
	 * @param
	 * @return String
	 */
	public static String getSysOptHour() {
		Calendar date = Calendar.getInstance();
		Date sysDate = date.getTime();
		String optDate = PubMethod.dateToString(sysDate, "HH");
		return optDate;
	}
	/**
	 * 获取系统操作时间的前一天日期
	 * 
	 * @param
	 * @return String
	 */
	public static String getLastDay(){
		Calendar date = Calendar.getInstance();		
		date.add(Calendar.DATE, -1);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		return formatter.format(date.getTime());			
	}
	
	/**
	 * 获取系统操作时间的后一天日期
	 * 
	 * @param
	 * @return String
	 */
	public static String getAfterDay(){
		Calendar date = Calendar.getInstance();		
		date.add(Calendar.DATE, +1);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		return formatter.format(date.getTime());			
	}

	/**
	 * 字符串转换为Date类型
	 * 
	 * @param strValue
	 *            String 日期串
	 * @return Date
	 */
	 public static String dateToString(Date dteValue, String strFormat) {
		  if(PubMethod.isEmpty(dteValue)){
			  return null;
		  }
			SimpleDateFormat clsFormat = new SimpleDateFormat(strFormat);
			return clsFormat.format(dteValue);
		}

	/**
	 * 将字符串转换为时间
	 * 
	 * @param strValue
	 *            String 字符串日期
	 * @return Date
	 */
	public static Date stringToDate(String strValue) {
		if (PubMethod.isEmpty(strValue)) {
			return null;
		}
		SimpleDateFormat clsFormat = null;
		if (strValue.length() > 19)
			strValue = strValue.substring(0, 19);
		if (strValue.length() == 19)
			clsFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		else if (strValue.length() == 10)
			clsFormat = new SimpleDateFormat("yyyy-MM-dd");
		else if (strValue.length() == 14)
			clsFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		else if (strValue.length() == 8)
			clsFormat = new SimpleDateFormat("yyyyMMdd");

		ParsePosition pos = new ParsePosition(0);
		return clsFormat.parse(strValue, pos);
	}

	public static Timestamp stringToTimestamp(String timeStr) {
		if (PubMethod.isEmpty(timeStr)) {
			return null;
		}
		try {
			return new Timestamp(PubMethod.stringToDate(timeStr).getTime());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Date chnstringToDate(String strValue) {
		if (PubMethod.isEmpty(strValue)) {
			return null;
		}
		SimpleDateFormat clsFormat = null;
		PubMethod.toPrintln("####################Pubmethod.chnstringToDate:"
				+ strValue.length());
		if (strValue.length() == 11) {
			clsFormat = new SimpleDateFormat("yyyy年MM月dd日");
		} else if (strValue.length() == 9) {
			clsFormat = new SimpleDateFormat("yyyy年M月d日");
		} else if (strValue.length() == 10) {
			int posY = strValue.indexOf("年");
			int posM = strValue.indexOf("月");
			int posD = strValue.indexOf("日");
			// PubMethod.toPrint(posY + " " + posM + " " + posD);
			if ((posM - posY) == 2) {
				clsFormat = new SimpleDateFormat("yyyy年M月dd日");
			} else if ((posD - posM) == 2) {
				clsFormat = new SimpleDateFormat("yyyy年MM月d日");
			}
		}

		ParsePosition pos = new ParsePosition(0);
		return clsFormat.parse(strValue, pos);
	}

	public static Timestamp chnstringToTimestamp(String timeStr) {
		if (PubMethod.isEmpty(timeStr)) {
			return null;
		}
		try {
			return new Timestamp(PubMethod.chnstringToDate(timeStr).getTime());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Date stringToDate(String strValue, String dateformat) {
		if (PubMethod.isEmpty(strValue)) {
			return null;
		}
		SimpleDateFormat clsFormat = new SimpleDateFormat(dateformat);

		ParsePosition pos = new ParsePosition(0);
		return clsFormat.parse(strValue, pos);
	}

	public static Timestamp stringToTimestamp(String timeStr, String dateformat) {
		if (PubMethod.isEmpty(timeStr)) {
			return null;
		}
		return new Timestamp(PubMethod.stringToDate(timeStr, dateformat)
				.getTime());
	}

	public static Date stringToDateH(String strValue) {
		if (PubMethod.isEmpty(strValue)) {
			return null;
		}
		SimpleDateFormat clsFormat = null;
		if (strValue.length() == 19)
			clsFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		else if (strValue.length() == 10)
			clsFormat = new SimpleDateFormat("yyyy/MM/dd");
		else if (strValue.length() == 14)
			clsFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		else if (strValue.length() == 8)
			clsFormat = new SimpleDateFormat("yyyyMMdd");

		ParsePosition pos = new ParsePosition(0);
		return clsFormat.parse(strValue, pos);
	}
	
	
	public static String returnYmdhms(){
		String str1 =getSysOptDate();
		str1 = str1.trim();
		str1 = str1.replaceAll(" ", "");
		str1 = str1.replaceAll("-", "");
		str1 = str1.replaceAll(":", "");
		return str1;
	}

	/**
	 * 判断日期字符串是否合法(格式用format指定)
	 * 
	 * @param str
	 * @param format
	 * @return
	 */
	public static boolean isValidDate(String str, String format) {
		boolean ret = false;
		try {
			SimpleDateFormat sf = new SimpleDateFormat(format);
			sf.parse(str);
			ret = true;
		} catch (Throwable ex) {

		}
		return ret;
	}

	/**
	 * 全角字符串转换为半角字符串
	 * 
	 * @param QJstr
	 * @return
	 */
	public static String SBCchange(String QJstr) {
		String outStr = "";
		String Tstr = "";
		byte[] b = null;

		for (int i = 0; i < QJstr.length(); i++) {
			try {
				Tstr = QJstr.substring(i, i + 1);
				b = Tstr.getBytes("unicode");
			} catch (java.io.UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			if (b[3] == -1) {
				b[2] = (byte) (b[2] + 32);
				b[3] = 0;

				try {
					outStr = outStr + new String(b, "unicode");
				} catch (java.io.UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			} else
				outStr = outStr + Tstr;
		}

		return outStr;
	}

	/**
	 * 字符串左补零
	 * 
	 * @param s
	 * @param len
	 * @return
	 */
	public static String lPad(String s, int len) {
		if (s.length() > len)
			return s;
		char[] cs = new char[len];
		for (int i = 0; i < len; i++) {
			if (len - s.length() > i)
				cs[i] = '0';
			else
				cs[i] = s.charAt(i - (len - s.length()));
		}
		return String.copyValueOf(cs);
	}

	/**
	 * 格式化成金额字符串 xxxx.xx
	 * 
	 * @param str
	 * @return
	 */
	public static String format2Money(String str) {
		int len = str.length();
		String ret;
		if (len <= 2) {
			ret = "0." + lPad(str, 2);
		} else {
			ret = str.substring(0, len - 2) + "." + str.substring(len - 2);
		}
		return ret;
	}

	/**
	 * 格式化成金额字符串 xxx,xxx.xx
	 * 
	 * @param str
	 * @return
	 */
	public static String format2Money2(Object obj) {
		DecimalFormat format = new DecimalFormat("###,###.00");
		return format.format(obj);
	}

	/**
	 * 把传入的浮点数,截取指定的小数位
	 * 
	 * @param f
	 * @param n
	 * @return
	 */
	public static String truncFloatNumber(double f, int n) {
		String str = String.valueOf(f);
		int pos = str.indexOf(".");
		if (n <= 0)
			return str.substring(0, pos);
		else {
			pos += n + 1;
			if (pos > str.length())
				pos = str.length();
			return str.substring(0, pos);
		}
	}

	/**
	 * 把传入的数字,转换为方便阅读的格式.(K,M, G等..) 如1024=1k, 1024k=1M,1024M=1G
	 * 
	 * @param num
	 * @return
	 */
	public static String numberToReadbleString(double num) {
		if (num < 0)
			return "";
		if (num < 1024 * 1024) {
			return truncFloatNumber(num / 1024, 2) + "K";
		} else if ((num >= 1024 * 1024) && num < 1024 * 1024 * 1024) {
			return truncFloatNumber(num / (1024 * 1024), 2) + "M";
		} else {
			return truncFloatNumber(num / (1024 * 1024 * 1024), 2) + "G";
		}
	}

	/**
	 * 把YYYYMMDDHH24MISS格式的字符串转换为timestamp
	 * 
	 * @param time
	 * @return
	 */
	public static Timestamp string2Timestamp(String time) {
		Timestamp st = new Timestamp(stringToDate(time).getTime());
		return st;
	}

	/**
	 * 把date 转换为 timestamp
	 * 
	 * @param time
	 * @return
	 */
	public static Timestamp dateToTimestamp(Date date) {
		if (PubMethod.isEmpty(date)) {
			return null;
		}
		return new Timestamp(date.getTime());
	}

	/**
	 * 把timestamp转换为YYYYMMDDHH24MISS格式的字符串
	 * 
	 * @param time
	 * @return
	 */
	public static String timestamp2String(Timestamp time) {
		Date date = new Date();
		date.setTime(time.getTime());
		return dateToString(date, "yyyyMMddHHmmss");
	}

	/**
	 * timestamp 转换为 date
	 * 
	 * @param time
	 * @return
	 */
	public static Date timestampToDate(Timestamp time) {
		if (time == null) {
			return null;
		}
		Date date = new Date();
		date.setTime(time.getTime());
		return date;
	}

	/**
	 * 把timestamp转换为指定格式的字符串
	 * 
	 * @param time
	 * @param format
	 *            "yyyy-MM-dd HH:mm:ss" "yyyy-MM-dd"
	 * @return
	 */
	public static String timestamp2String(Timestamp time, String format) {
		if (time == null)
			return "";
		if (format == null || "".equals(format.trim())) {
			format = "yyyy-MM-dd HH:mm:ss";
		}
		Date date = new Date();
		date.setTime(time.getTime());
		return dateToString(date, format);
	}

	/**
	 * 把当前时间转换为指定格式的字符串
	 * 
	 * @param format
	 *            "yyyy-MM-dd HH:mm:ss" "yyyy-MM-dd"
	 * @return
	 */
	public static String currentTime2String(String format) {
		if (format == null || "".equals(format.trim())) {
			format = "yyyy-MM-dd HH:mm:ss";
		}
		Date date = new Date();
		return dateToString(date, format);
	}

	/**
	 * 计算两个日期相差多少秒
	 * 
	 * @param startDateTime
	 * @param endDateTime
	 * @return
	 */
	public static long getDateGap(String startDateTime, String endDateTime) {
		Date startDate = stringToDate(startDateTime);
		Date endDate = stringToDate(endDateTime);
		return (endDate.getTime() - startDate.getTime()) / 1000;
	}
	
	
	
	
	/**
	 * 计算两个日期相差多少小时
	 * 
	 * @param startDateTime
	 * @param endDateTime
	 * @return
	 */
	public static long getDateGapHours(String startDateTime, String endDateTime) {
		Date startDate = stringToDate(startDateTime);
		Date endDate = stringToDate(endDateTime);
		return (endDate.getTime() - startDate.getTime()) / 1000;
	}
	
	
	
	
	/**
	 * 获得与现在时间相差秒的日期
	 * 
	 * @param i 秒数  如果是取日期之前的加负号
	 */
	public static Date getSecondGap(Date date, long i)
	{
		if(date == null)
		{
			date = new Date();
		}
		long handleDate = date.getTime();
		
		return new Date(handleDate+i*1000);
	}
	
	/**
	 * 获得与现在时间相差秒的日期
	 * 
	 * @param i 秒数  如果是取日期之前的加负号
	 */
	public static String getSecStringGap(String dateStr, int i, String format)
	{
		Date date = stringToDate(dateStr);
		if(date == null)
		{
			date = new Date();
		}
		long handleDate = date.getTime();
		Date rtnDate = new Date(handleDate+i*1000);
		
		return dateToString(rtnDate, format);
	}

	/**
	 * 获得与现在时间相差几天的日期
	 * 
	 * @param i
	 *            1－明天 n－后n天 -1－昨天 -n－前n天
	 */
	public static Date getDateGap(int i) {
		/*
		 * SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd
		 * HH:mm:ss"); Date currDate = new Date(); long currTimeLen =
		 * currDate.getTime(); long resTimeLen = currTimeLen + i*(60*60*24);
		 * //Date resDate = new Date(resTimeLen); currDate.setTime(resTimeLen);
		 * PubMethod.toPrint(formatter.format(currDate) + "=======");
		 */

		Calendar now = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// PubMethod.toPrint("It is now " + formatter.format(now.getTime()));
		now.add(Calendar.DAY_OF_YEAR, i);
		// PubMethod.toPrint("Two years ago was " +
		// formatter.format(now.getTime()));

		return now.getTime();
	}
	
	/**
	 * 获得与指定日期相差几天的日期
	 * @param date 指定日期
	 * @param i 1－明天 n－后n天 -1－昨天 -n－前n天
	 * @return
	 */
	public static Date getDateGap(Date date, int i)
	{
		Calendar now = Calendar.getInstance();
		now.setTime(date);
		//PubMethod.toPrint(now.getTime()+"ddd");
		now.add(Calendar.DAY_OF_YEAR, i);
		return now.getTime();
	}
	
	//计算两个日期相隔天数
	public static int getDays(String startDate,String endDate){
		startDate = startDate.replace('-','/');
		endDate = endDate.replace('-','/');
		long quot = 0; 
		int intDays = 0;
		SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd"); 
		try { 
		Date date1 = ft.parse( startDate ); 
		Date date2 = ft.parse( endDate ); 
		quot = date2.getTime() - date1.getTime(); 
		quot = quot / 1000 / 60 / 60 / 24; 
		} catch (ParseException e) { 
		e.printStackTrace(); 
		} 
		Long longDays = Long.valueOf(quot);
		
		intDays = longDays.intValue()+1;
		
		return intDays;
		} 
	
	/**
	 * 获得与指定日期相差指定月份的日期
	 * @param date 指定日期
	 * @param i 月份
	 * @return
	 */
	public static Date getDateMonthGap(Date date, int i)
	{
		Calendar now = Calendar.getInstance();
		now.setTime(date);
		//PubMethod.toPrint(now.getTime()+"ddd");
		now.add(Calendar.MONTH, i);
		return now.getTime();
	}

	/**
	 * 取得与当前日期指定相差天数的日期
	 * 
	 * @param i
	 *            相差天数
	 * @param format
	 *            "yyyy-MM-dd HH:mm:ss" "yyyy-MM-dd"
	 * @return
	 */
	public static String getDateStringGap(int i, String format) {
		Date date = getDateGap(i);
		return dateToString(date, format);
	}
	
	/**
	 * 取得与指定日期相差天数的日期
	 * @param date "yyyy-MM-dd"
	 * @param i
	 * @param format
	 * @return
	 */
	public static String getDateStringGap(String dateStr, int i, String format) {
		Date date = stringToDate(dateStr);
		Date rtnDate = getDateGap(date, i);
		return dateToString(rtnDate, format);
	}

	/**
	 * 返回本月是本年的第几个月 *
	 * 
	 * @return
	 */
	public static int monthOfYear() {
		GregorianCalendar vTodayCal = new GregorianCalendar();
		return vTodayCal.get(GregorianCalendar.MONTH) + 1;
	}

	/**
	 * 处理sql字符串中的单引号
	 * 
	 * @param s1
	 * @return
	 */
	public static String fixSQLString(String s1) {
		String s2 = new String("");
		for (int i = 0; i < s1.length(); i++) {
			s2 += s1.charAt(i);
			if ('\'' == s1.charAt(i))
				s2 += '\'';
		}
		return s2;
	}

	/**
	 * 目的和fixSQLString一样，区别在首尾多加单引号 比如help'help --> 'help''help'
	 * 
	 * @param s1
	 *            被修正的字符串
	 * @return 修正过的字符串
	 */
	public static String fixSQLStringPlusSingleQuote(String s1) {
		return "'" + fixSQLString(s1) + "'";
	}

	/**
	 * 判断字符串s是否包含再数组中
	 * 
	 * @param s
	 * @param array
	 * @return
	 * @throws Exception
	 */
	public static boolean isInArray(String s, String[] array) throws Exception {
		boolean b = false;
		if (s == null)
			return b;
		try {
			for (int i = 0; array != null && i < array.length; i++) {
				if (s.equals(array[i]))
					return true;
			}
		} catch (Exception e) {
			throw e;
		}
		return b;
	}

	/**
	 * 把数组内容组合成1个大字符串,每个数组元素添加单引号,并且用','分隔
	 * 
	 * @param array
	 * @return
	 */
	public static String getStringFromArray(String[] array) {

		String str = "";
		if (array == null || array.length <= 0)
			return "";

		try {
			for (int i = 0; i < array.length; i++) {
				str += "'" + array[i] + "',";
			}
			if (str.length() > 0)
				str = str.substring(0, str.length() - 1);
		} catch (Exception e) {
		}

		return str;
	}

	public static Long checkLongNull(long o) {
		if (o != 0)
			return o == 0 ? new Long(0) : new Long(o);
		return new Long(o);
	}

	public static Double checkDoubleNull(double o) {
		if (o != 0)
			return o == 0 ? new Double(0) : new Double(o);
		return new Double(o);
	}

	public static String checkNull(String o) {
		if (o == null || o.equals(""))
			return "0";
		else
			return o;
	}

	public static Timestamp checkTimeNull(String src) {
		Timestamp timstamp = null;
		if (src == null || src.equals("")) {
			src = PubMethod.getSysOptDate();
		}
		try {
			timstamp = Timestamp.valueOf(src);
		} catch (Exception e) {
			src = PubMethod.getSysOptDate();
			timstamp = Timestamp.valueOf(src);
		}

		return timstamp;
	}

	/**
	 * 日期片断常量-年
	 */
	public static final String YEAR = "year";

	/**
	 * 日期片断常量-月
	 */
	public static final String MONTH = "month";

	/**
	 * 日期片断常量-日
	 */
	public static final String DAY = "day";

	/**
	 * 日期片断常量-时
	 */
	public static final String HOUR = "hour";

	/**
	 * 日期片断常量-分
	 */
	public static final String MINUTE = "minute";

	/**
	 * 日期片断常量-秒
	 */
	public static final String SECOND = "second";

	/**
	 * 日期格式常量 "yyyy-MM-dd HH:mm:ss"
	 */
	public static final String LONG = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 日期格式常量 "yyyy-MM-dd"
	 */
	public static final String SHORT = "yyyy-MM-dd";

	/**
	 * 日期格式常量 "yyyyMMddHHmmss"
	 */
	public static final String LONGNUMBER = "yyyyMMddHHmmss";

	/**
	 * 日期格式常量 "yyyyMMdd"
	 */
	public static final String SHORTNUMBER = "yyyyMMdd";

	/**
	 * 日期格式常量 "HH:mm:ss"
	 */
	public static final String LONGTIME = "HH:mm:ss";

	/**
	 * 日期格式常量 "HH:mm"
	 */
	public static final String SHORTTIME = "HH:mm";

	/**
	 * public static void main(String[] arg) {
	 * 
	 * List a = PubMethod.stringToList("123456;", ";");
	 * PubMethod.toPrint("sadf"+a.size()); PubMethod pb = new PubMethod();
	 * String[] heads = { "head1", "head2", "head3", "head4", "head5", "head6",
	 * "head7" }; List list = new ArrayList(); for (int i = 0; i < 50; i++) {
	 * String[] cnt = new String[7];//
	 * {"head1","head2","head3","head4","head5","head6","head7"}; for (int j =
	 * 0; j < 7; j++) { cnt[j] = j + "-" + System.currentTimeMillis(); }
	 * list.add(cnt); } String AbsolutPath = "d:\\test.log"; //
	 * pb.wrtFile(heads, list, AbsolutPath); try { // pb.wrtExcel(heads, list,
	 * AbsolutPath); } catch (Exception e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); }
	 * 
	 * //生成In子句函数的测试区。 List inList = new ArrayList();
	 * 
	 * inList.add(new Long(9)); inList.add(new Long(3)); inList.add(new
	 * Long(3)); inList.add(new Long(9));
	 * 
	 * inList.add("t999"); inList.add("t559");
	 * 
	 * inList.add(new Double(66));
	 * 
	 * //PubMethod.toPrint(pb.getList2StringBySplitter(inList, ""));
	 * 
	 * //时间比较串获取。 //PubMethod.toPrint(pb.getOraDateCompString("2003-11-01",
	 * "2007-09-09", "elapsedate")); com.sinosure.sol.persistence.po.VbShipment
	 * loginInfo=new com.sinosure.sol.persistence.po.VbShipment();
	 * loginInfo.setInsuresum(new Double(9)); Map map=
	 * getObjFieldVals(loginInfo); PubMethod.toPrint(map); }
	 */

	/**
	 * 保留double的小数点后的位数
	 * 
	 * @author wangjianhua
	 * @param val
	 * @param precision
	 *            小数点后保留的位数
	 * @return
	 */
	public static String roundDouble(double val) {
		java.text.DecimalFormat df = new java.text.DecimalFormat("#.##");

		return df.format(val);
		/*
		 * double factor = Math.pow(10, precision); return Math.floor(val *
		 * factor + 0.5) / factor;
		 */
	}

	/**
	 * toString
	 * 
	 * @param object
	 * @return
	 */
	public static String objectToString(Object object) {
		return object == null ? "" : object.toString().trim();
	}

	/**
	 * 判断字符串是否在列表中存在
	 * 
	 * @author wangjianhua
	 * @param list
	 * @param nodeNo
	 * @return
	 */
	public static boolean isInList(List list, String nodeNo) {
		if (PubMethod.isEmpty(list) || PubMethod.isEmpty(nodeNo)) {
			return false;
		}
		boolean result = false;
		for (Iterator it = list.iterator(); it.hasNext();) {
			String string = (String) it.next();
			if (nodeNo.equals(string)) {
				result = true;
				break;
			}
		}
		return result;
	}

	/**
	 * BigDecimal 转换为 Double
	 * 
	 * @author wangjianhua
	 * @param value
	 * @return
	 */
	public static Double bigDecimal2Double(BigDecimal value) {
		if (PubMethod.isEmpty(value)) {
			return new Double(0.0);
		}
		return new Double(value.doubleValue());
	}

	public static Integer bigInteger2Integer(BigInteger value) {
		if (PubMethod.isEmpty(value)) {
			return new Integer(0);
		}
		return new Integer(value.intValue());
	}

	public static Long bigInteger2Long(BigInteger value) {
		if (PubMethod.isEmpty(value)) {
			return new Long(0l);
		}
		return new Long(value.intValue());
	}

	/**
	 * BigDecimal 转换为 Integer
	 * 
	 * @author wangjianhua
	 * @param value
	 * @return
	 */
	public static Integer bigDecimal2Integer(BigDecimal value) {
		if (PubMethod.isEmpty(value)) {
			return new Integer(0);
		}
		return new Integer(value.intValue());
	}

	/**
	 * BigDecimal 转换为 Long
	 * 
	 * @author wangjianhua
	 * @param value
	 * @return
	 */
	public static Long bigDecimal2Long(BigDecimal value) {
		if (PubMethod.isEmpty(value)) {
			return new Long(0l);
		}
		return new Long(value.longValue());
	}

	/**
	 * BigDecimal 转换为 String
	 * 
	 * @author wangjianhua
	 * @param value
	 * @return
	 */
	public static String bigDecimal2String(BigDecimal value) {
		if (PubMethod.isEmpty(value)) {
			return "";
		}
		return value.toString();
	}

	/**
	 * BigDecimal 转换为 String
	 * 
	 * @author wangjianhua
	 * @param value
	 * @return
	 */

	/**
	 * 将数组中的元素用指定的分隔符分隔
	 * 
	 * @author wangjianhua
	 * @param array
	 * @param token
	 * @return
	 */
	public static String arrayToString(Object[] array, String token) {
		if (PubMethod.isEmpty(array) || array.length == 0) {
			return "";
		}
		String result = "";
		for (int i = 0; i < array.length; i++) {
			String item = array[i].toString();
			/*
			 * if(i != 0) { result += token; }
			 */
			result += item;
			result += token;
		}
		return result;
	}
   
	/**
	 *将数组中的元素用指定的分隔符分隔,结尾不尾随token分隔符
	 * 创建时间:  2012-2-18 下午05:19:46  
	 * @param array
	 * @param token
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public static String arrayToStringNoEndToken(Object[] array, String token) {
		if (PubMethod.isEmpty(array) || array.length == 0) {
			return "";
		}
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < array.length; i++) {
			String item = array[i].toString();
			if(result.length()<=0){
				result.append(item);
			}else{
				result.append(token).append(item);
			}
			
		}
		return result.toString();
	}
	/**
	 * 将字符串指定的分隔符分开后放入列表中
	 * 
	 * @param data
	 * @param token
	 * @author wangjiahua
	 * @return
	 */
	public static List stringToList(String data, String token) {
		List res = new ArrayList();
		if (data == null || "".equals(data.trim()) || token == null) {
			return res;
		}
		token = token.trim();
		StringBuffer str = new StringBuffer();
		if (!"".equals(token)) {
			for (int i = data.indexOf(token); i != -1; i = data.indexOf(token)) {
				str.append(" " + data.substring(0, i + 1) + " ");
				data = data.substring(i + 1);
			}
		} else {
			token = " ";
			str.append(data);
		}

		StringTokenizer st = new StringTokenizer(str.toString(), token);
		while (st.hasMoreElements()) {
			String value = ((String) st.nextElement()).trim();
			if (!"".equals(value)) {
				res.add(value);
			}
		}
		st = null;
		if (res != null && res.size() == 0) {
			res.add(data);
		}
		return res;
	}

	// 把字符串转换为List容器对象。
	public static List convertString2List(String srcString, String spiliter) {
		if (isEmpty(srcString) || isEmpty(spiliter))
			return null;

		Object[] objsArray = srcString.split(spiliter);
		List list = new ArrayList();
		if (!isEmpty(objsArray)) {
			for (int index = 0; index < objsArray.length; index++) {
				list.add(objsArray[index]);
			}
		}

		return list;
	}

	/**
	 * 日期的加减
	 * 
	 * @param Timestamp
	 * @param type
	 * @param amount
	 * @author wangjianhua
	 * @return
	 */
	public static Timestamp modifyDate(Timestamp t, String type, int amount) {
		Date date = new Date();
		date.setTime(t.getTime());
		Date newDate = modifyDate(date, type, amount);
		return new Timestamp(newDate.getTime());
	}

	/**
	 * 日期的加减
	 * 
	 * @param Date
	 * @param type
	 *            Y-年 M-月 D-天
	 * @param amount
	 *            加减的数量
	 * @author wangjianhua
	 * @return
	 */
	public static Date modifyDate(Date date, String type, int amount) {
		if (date == null) {
			date = new Date();
		}
		if (isEmpty(type)) {
			type = "";
		}
		Calendar now = Calendar.getInstance();
		now.setTime(date);
		// PubMethod.toPrint(now.get(Calendar.YEAR) + " " +
		// (now.get(Calendar.MONTH)+1) + " " + now.get(Calendar.DAY_OF_YEAR) + "
		// " + now.get(Calendar.DAY_OF_WEEK));
		if ("Y".equalsIgnoreCase(type)) {
			now.add(Calendar.YEAR, amount);
		} else if ("M".equalsIgnoreCase(type)) {
			now.add(Calendar.MONTH, amount);
		} else if ("D".equalsIgnoreCase(type)) {
			now.add(Calendar.DAY_OF_YEAR, amount);
		}
		return now.getTime();
	}

	public static String digitalFormat(Object value, String formatter) {
		if (PubMethod.isEmpty(value)) {
			return "";
		}
		if (PubMethod.isEmpty(formatter)) {
			formatter = "###,###.00";
		}
		DecimalFormat format = new DecimalFormat(formatter);
		String res = null;
		try {
			if (value instanceof Double) {
				res = format.format(((Double) value).doubleValue());
			}
			if (value instanceof Long) {
				res = format.format(((Long) value).longValue());
			}
		} catch (Exception e) {
			return "";
		}
		return res;
	}

	public static Double digitalFormatDouble(Double value, String formatter) {
		if (PubMethod.isEmpty(value)) {
			return new Double(0.0);
		}
		if (PubMethod.isEmpty(formatter)) {
			formatter = "###.00";
		}
		DecimalFormat format = new DecimalFormat(formatter);
		Double res = null;
		try {
			res = new Double(format.format(value.doubleValue()));
		} catch (Exception e) {
			return new Double(0.0);
		}
		return res;
	}
	
	
	

	public static String digitalFormat(double value, String formatter) {
		if (PubMethod.isEmpty(formatter)) {
			formatter = "###,###.00";
		}
		DecimalFormat format = new DecimalFormat(formatter);
		return format.format(value);
	}

	public static String digitalFormat(float value, String formatter) {
		if (PubMethod.isEmpty(formatter)) {
			formatter = "###,###.00";
		}
		DecimalFormat format = new DecimalFormat(formatter);
		return format.format(value);
	}
	
	
	public static Double digitalFormatDoublebit(Double value, String bitnum) {
		String formatter= "";
		if (PubMethod.isEmpty(value)) {
			return new Double(0.0);
		}
		
		if (value < 0)
			value = value - 0.0000001;
		// DecimalFormat有BUDGE，当末位为5时无法正确进行四舍五入
		else if (value > 0)
			value = value + 0.0000001;
		// DecimalFormat有BUDGE，当末位为5时无法正确进行四舍五入
		
		if (PubMethod.isEmpty(bitnum) || "2".equals(bitnum)) {
			formatter = "###.00";
		}else if("3".equals(bitnum)){
			formatter = "###.000";
		}else if("4".equals(bitnum)){
			formatter = "###.000";
		}if("0".equals(bitnum)){
			formatter = "###";
		}
		DecimalFormat format = new DecimalFormat(formatter);
		Double res = null;
		try {
			res = new Double(format.format(value.doubleValue()));
		} catch (Exception e) {
			return new Double(0.0);
		}
		return res;
	}
	

	/**
	 * 从jdbc取数据库连接
	 * 
	 * @param driver
	 * @param url
	 * @param user
	 * @param pass
	 * @return
	 * @throws Exception
	 */
	public static Connection getConnectionByJdbc(String driver, String url,
			String user, String pass) throws Exception {
		Class.forName(driver);
		return DriverManager.getConnection(url, user, pass);
	}

	/**
	 * 关闭Statement
	 * 
	 * @param stm
	 */
	public static void closeStatement(Statement stm) throws Exception {
		try {
			if (stm != null) {
				stm.close();
				stm = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 关闭ResultSet
	 * 
	 * @param rs
	 */
	public static void closeResultSet(ResultSet rs) throws Exception {
		try {
			if (rs != null) {
				rs.close();
				rs = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 关闭数据库连接
	 * 
	 * @param conn
	 */
	public static void closeConnection(Connection conn) throws Exception {
		try {
			if (conn != null) {
				conn.close();
				conn = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 判断业务状态是否出运中信保已受理的状态
	 * 
	 * @param bnsstate
	 * @return
	 */
	public static boolean isSolAccepted(String bnsstate) {
		if (bnsstate == null || bnsstate.equals(""))
			return false;
		if (bnsstate.equals("2") // 撤单
				|| bnsstate.equals("11") // 制单中
				|| bnsstate.equals("12") // 退回制单人
				|| bnsstate.equals("13") // 复核中
				|| bnsstate.equals("14") // 退回复核人
				|| bnsstate.equals("15") // 中信保退回
		) {
			return false;
		}
		return true;
	}

	public static int getDayLasted(Timestamp tm) {
		long timediff = (System.currentTimeMillis() - tm.getTime()) / 1000 / 3600; // 得到相差的小时数
		return (((int) timediff) / 24) + 1;
	}

	public static int[] getRowCol(String str) throws Exception {

		int[] rowcol = new int[2];
		try {
			String strRow = "";
			String strCol = "";
			int tmp = 0;
			for (int i = 0; i < str.length(); i++) {
				if (str.charAt(i) >= 'A' && str.charAt(i) <= 'Z')
					strRow += String.valueOf(str.charAt(i));
				else if (str.charAt(i) >= '0' && str.charAt(i) <= '9')
					strCol += String.valueOf(str.charAt(i));
			}

			strRow = PubMethod.charToNumber(strRow);
			strCol = String.valueOf(Integer.parseInt(strCol) - 1);

			if (Integer.parseInt(strRow) < 0)
				throw new Exception();
			if (Integer.parseInt(strCol) < 0)
				throw new Exception();

			rowcol[0] = Integer.parseInt(strRow);
			rowcol[1] = Integer.parseInt(strCol);

		} catch (Exception e) {
			throw e;
		}

		return rowcol;
	}

	public static String charToNumber(String str) {
		String retVuale = "";
		long vuale = 0;
		for (int i = str.length(); i > 0; i--) {
			int bytes = str.length() - i;
			char ch = str.charAt(i - 1);
			int iTmp = ch - 'A' + 1;

			vuale += iTmp * getNumber(26, bytes);
		}
		vuale = vuale - 1;

		if (vuale >= 0)
			retVuale = String.valueOf(vuale);
		return retVuale;
	}

	public static String numberToChar(String retVuale, String number, int level) {

		long vuale = new Long(number).longValue();
		long multiple;
		int residual;

		char temp;
		if (level > 0) {
			multiple = vuale / 27;
			residual = (int) vuale % 27;
			temp = (char) (residual - 1 + 'A');
		} else {
			multiple = vuale / 26;
			residual = (int) vuale % 26;
			temp = (char) (residual + 'A');
		}

		if (multiple == 0) {
			retVuale += temp;
		} else {
			retVuale = numberToChar(retVuale, String.valueOf(multiple),
					level + 1)
					+ temp;
		}

		return retVuale;

	}

	public static long getNumber(int number1, int number2) {
		long l = 1;
		if (number2 < 0)
			return 0;
		else if (number2 == 0)
			return 1;
		for (int i = 1; i <= number2; i++) {
			l = l * number1;
		}
		return l;
	}

	/**
	 * 给对象的属性赋初始值. 字符串赋值为 "" 数字复制为0
	 * 
	 * @param obj
	 */
//	public static void initilizeProperty(Object obj) throws Exception {
//		BeanInfo bi = Introspector.getBeanInfo(obj.getClass(), Object.class);
//		PropertyDescriptor[] props = bi.getPropertyDescriptors();
//
//		for (int i = 0; i < props.length; i++) {
//			Object o = props[i].getReadMethod().invoke(obj, null);
//			if (o == null) {
//				Class type = PropertyUtils.getPropertyType(obj, props[i]
//						.getName());
//				if (type.getName().equals("java.lang.String"))
//					props[i].getWriteMethod().invoke(obj, new Object[] { "" });
//				if (type.getName().equals("java.lang.Long"))
//					props[i].getWriteMethod().invoke(obj,
//							new Object[] { new Long(0) });
//				if (type.getName().equals("java.lang.Double"))
//					props[i].getWriteMethod().invoke(obj,
//							new Object[] { new Double(0) });
//				if (type.getName().equals("java.sql.Timestamp"))
//					props[i].getWriteMethod().invoke(obj,
//							new Object[] { new Timestamp(0) });
//			}
//		}
//	}

	public static String moneyFormat(String s) {
		try {
			String s1 = "";
			String s2 = "";
			String s3 = "";
			if (s.indexOf(",") != -1)
				return s;
			if (s.equals(""))
				return "";
			if (s.equals("0"))
				return "0.00";
			if (s.substring(0, 1).equals("-")) {
				s3 = "-";
				s = s.substring(1, s.length());
			}
			if (s.substring(0, 1).equals("."))
				s = "0" + s;
			int i = s.indexOf("E");
			if (i != -1) {
				int j = Integer.valueOf(s.substring(i + 1, s.length()))
						.intValue();
				String s4 = s.substring(0, i);
				if (j > 0) {
					if (s4.length() - 2 <= j) {
						int l = (j - (s4.length() - 2)) + 1;
						for (int i1 = 0; i1 < l; i1++)
							s4 = s4 + "0";

					}
					String s7 = s4.substring(0, j + 2) + "."
							+ s4.substring(j + 2, s4.length());
					s4 = s7.substring(0, 1) + s7.substring(2, s7.length());
				} else {
					j = Math.abs(j);
					String s8 = s4.substring(0, 1)
							+ s4.substring(2, s4.length());
					s4 = "0.";
					for (int j1 = 0; j1 < j - 1; j1++)
						s4 = s4 + "0";

					s4 = s4 + s8;
				}
				s = s4;
			}
			int k = s.indexOf(".");
			if (k != -1) {
				s1 = s.substring(0, k);
				s2 = s.substring(k + 1, s.length());
			} else {
				s1 = s;
				s2 = "00";
			}
			if (s2.length() > 2) {
				String s5 = s2.substring(0, 2);
				String s9 = s2.substring(2, 3);
				int k1 = Integer.valueOf(s9).intValue();
				boolean flag = false;
				if (k1 >= 5) {
					int l1 = Integer.valueOf(s5).intValue();
					if (l1 <= 8) {
						l1++;
						s2 = "0" + String.valueOf(l1);
					} else if (++l1 >= 100) {
						String s11 = String.valueOf(l1);
						s2 = s11.substring(1, 3);
						long l2 = Long.valueOf(s1).longValue();
						l2++;
						s1 = String.valueOf(l2);
					} else {
						s2 = String.valueOf(l1);
					}
				} else {
					s2 = s5;
				}
			}
			if (s1.length() > 3) {
				String s6 = s1;
				String s10 = "";
				for (; s6.length() > 3; s6 = s6.substring(0, s6.length() - 3))
					s10 = "," + s6.substring(s6.length() - 3, s6.length())
							+ s10;

				s10 = s6 + s10;
				s1 = s10;
			}
			if (s2.length() == 1)
				s2 = s2 + "0";
			return s3 + s1 + "." + s2;
		} catch (Exception e) {
			e.printStackTrace();
			return s;
		}
	}

	// 采用四舍五入的方法格式化数据
	// 入口：dblInput （double） :需要格式化的数据
	// strFormat（String） :格式如：##，###.00
	// 出口：String 经过格式化得来的数值
	public static String toFormatNum(double dblInput, String strFormat) {
		try {

			if (dblInput < 0)
				dblInput = dblInput - 0.0000001;
			// DecimalFormat有BUDGE，当末位为5时无法正确进行四舍五入
			else if (dblInput > 0)
				dblInput = dblInput + 0.0000001;
			// DecimalFormat有BUDGE，当末位为5时无法正确进行四舍五入
			else
				dblInput = 0;
			if (strFormat == null || strFormat.equals(""))
				strFormat = "###,###,###.00";
			DecimalFormat fmt = new DecimalFormat(strFormat); // "#,###,##0.00"
			strFormat = fmt.format(dblInput);
			// PubMethod.toPrint("old======"+dblInput+" new======"+strFormat) ;
		} catch (Exception e) {
			PubMethod.toPrintln("Error in ChangeNumberic.toFormatNum1 :"
					+ e.toString());
		}
		return strFormat;
	}

	public static String toFormatNum2(double dblInput, String strFormat) {
		try {

			if (dblInput < 0)
				dblInput = dblInput - 0.0000001;
			// DecimalFormat有BUDGE，当末位为5时无法正确进行四舍五入
			else if (dblInput > 0)
				dblInput = dblInput + 0.0000001;
			// DecimalFormat有BUDGE，当末位为5时无法正确进行四舍五入
			else
				dblInput = 0;
			if (strFormat == null || strFormat.equals(""))
				strFormat = "0.00";
			DecimalFormat fmt = new DecimalFormat(strFormat); // "#,###,##0.00"
			strFormat = fmt.format(dblInput);
			// PubMethod.toPrint("old======"+dblInput+" new======"+strFormat) ;
		} catch (Exception e) {
			PubMethod.toPrintln("Error in ChangeNumberic.toFormatNum1 :"
					+ e.toString());
		}
		return strFormat;
	}

	// 采用四舍五入的方法格式化数据
	// 入口：strInput （String） :需要格式化的数据
	// strFormat（String） :格式如：##，###.00
	// 出口：String 经过格式化得来的数值
	public static String toFormatNum(String strInput, String strFormat) {
		try {
			double dblInput = 0;
			// 数据初始化
			if (strInput == null)
				return "";
			strInput = strInput.trim();
			if (strInput.equals("") || strInput.equalsIgnoreCase("null"))
				return "";

			// 赋值
			dblInput = Double.parseDouble(strInput);

			if (dblInput < 0)
				dblInput = dblInput - 0.0000001;
			// DecimalFormat有BUDGE，当末位为5时无法正确进行四舍五入
			else if (dblInput > 0)
				dblInput = dblInput + 0.0000001;
			// DecimalFormat有BUDGE，当末位为5时无法正确进行四舍五入
			else
				dblInput = 0;
			if (strFormat == null || strFormat.equals(""))
				strFormat = "###,###,###.00";
			DecimalFormat fmt = new DecimalFormat(strFormat); // "#,###,##0.00"
			strFormat = fmt.format(dblInput);
			PubMethod.toPrintln("old======" + strInput + "      new======"
					+ strFormat);
		} catch (Exception e) {
			PubMethod.toPrintln("Error in ChangeNumberic.toFormatNum2 :"
					+ e.toString());
		}
		return strFormat;

	}

	public static String toFormatNum2(String strInput, String strFormat) {
		try {
			double dblInput = 0;
			// 数据初始化
			if (strInput == null)
				return "";
			strInput = strInput.trim();
			if (strInput.equals("") || strInput.equalsIgnoreCase("null"))
				return "";

			// 赋值
			dblInput = Double.parseDouble(strInput);

			if (dblInput < 0)
				dblInput = dblInput - 0.0000001;
			// DecimalFormat有BUDGE，当末位为5时无法正确进行四舍五入
			else if (dblInput > 0)
				dblInput = dblInput + 0.0000001;
			// DecimalFormat有BUDGE，当末位为5时无法正确进行四舍五入
			else
				dblInput = 0;
			if (strFormat == null || strFormat.equals(""))
				strFormat = "###.00";
			DecimalFormat fmt = new DecimalFormat(strFormat); // "#,###,##0.00"
			strFormat = fmt.format(dblInput);
			PubMethod.toPrintln("old======" + strInput + "      new======"
					+ strFormat);
		} catch (Exception e) {
			PubMethod.toPrintln("Error in ChangeNumberic.toFormatNum2 :"
					+ e.toString());
		}
		return strFormat;

	}

	// 将用字符串表示的数值转化为字符串

	public static String toNumberic(String strInput) {
		String strReturn = strInput;
		try {
			// 做简单的字符判断处理
			if (strInput == null)
				return "";
			strInput = strInput.trim();
			if (strInput.equals("") || strInput.equalsIgnoreCase("null"))
				return "";

			int intDelete = 0; // 去掉“,”“$”,"￥","＄","￡","％","%", " "
			String strDelete = ","; // 1
			intDelete = strReturn.indexOf(strDelete);
			while (intDelete != -1) {
				strReturn = strReturn.substring(0, intDelete)
						+ strReturn.substring(intDelete + 1);
				intDelete = strReturn.indexOf(strDelete);
			}
			strDelete = "$"; // 2
			intDelete = strReturn.indexOf(strDelete);
			while (intDelete != -1) {
				strReturn = strReturn.substring(0, intDelete)
						+ strReturn.substring(intDelete + 1);
				intDelete = strReturn.indexOf(strDelete);
			}
			strDelete = "￥"; // 3
			intDelete = strReturn.indexOf(strDelete);
			while (intDelete != -1) {
				strReturn = strReturn.substring(0, intDelete)
						+ strReturn.substring(intDelete + 1);
				intDelete = strReturn.indexOf(strDelete);
			}
			strDelete = "＄"; // 4
			intDelete = strReturn.indexOf(strDelete);
			while (intDelete != -1) {
				strReturn = strReturn.substring(0, intDelete)
						+ strReturn.substring(intDelete + 1);
				intDelete = strReturn.indexOf(strDelete);
			}
			strDelete = "￡"; // 5
			intDelete = strReturn.indexOf(strDelete);
			while (intDelete != -1) {
				strReturn = strReturn.substring(0, intDelete)
						+ strReturn.substring(intDelete + 1);
				intDelete = strReturn.indexOf(strDelete);
			}
			strDelete = "％"; // 6
			intDelete = strReturn.indexOf(strDelete);
			while (intDelete != -1) {
				strReturn = strReturn.substring(0, intDelete)
						+ strReturn.substring(intDelete + 1);
				intDelete = strReturn.indexOf(strDelete);
			}
			strDelete = "%"; // 7
			intDelete = strReturn.indexOf(strDelete);
			while (intDelete != -1) {
				strReturn = strReturn.substring(0, intDelete)
						+ strReturn.substring(intDelete + 1);
				intDelete = strReturn.indexOf(strDelete);
			}
			strDelete = " "; // 8
			intDelete = strReturn.indexOf(strDelete);
			while (intDelete != -1) {
				strReturn = strReturn.substring(0, intDelete)
						+ strReturn.substring(intDelete + 1);
				intDelete = strReturn.indexOf(strDelete);
			}
		} catch (Exception e) {
			PubMethod.toPrintln("Error in ChangeNumberic.toNumberic :"
					+ e.toString());
		}
		return strReturn;
	}

	/**
	 * 将List中的元素加入另一个List
	 * 
	 * @param subList
	 * @param list
	 */
	public static void addListElementToList(List subList, List list) {
		if (PubMethod.isEmpty(subList)) {
			return;
		}
		for (Iterator itSubList = subList.iterator(); itSubList.hasNext();) {
			list.add(itSubList.next());
		}
	}
	
	/**
	 * 返回数组的第一个元素，且造型为String 类型
	 * @param objArr
	 * @return
	 */
	public static String getFirstEleFromArr(Object obj)
	{
		String[] strArr = (String[]) obj;
		if(strArr == null || strArr.length == 0)
		{
			return "";
		}
		return (String) strArr[0];
	}

	/**
	 * 字符串长度不够指定长度，则在左边补0
	 * 
	 * @param val
	 * @param numbers
	 * @return
	 */
	public static String getFormatString(String val, int numbers) {
		if (PubMethod.isEmpty(val)) {
			return "";
		}
		val = val.trim();
		while (val.length() < numbers) {
			val = "0" + val;
		}
		return val;
	}

	/**
	 * 判断是否为action字符串
	 * 
	 * @param urlAction
	 * @return
	 */
	public static boolean isUrlString(String urlAction) {
		if (PubMethod.isEmpty(urlAction)) {
			return false;
		}
		if (urlAction.endsWith(".jsp") || urlAction.endsWith(".jsf")
				|| urlAction.endsWith(".html") || urlAction.endsWith(".htm")) {
			return true;
		} else {
			return false;
		}
	}
	/**
	 * 判断是否为数字
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str){  		
		boolean returnValue=false;		
		String patternValue="^[1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*$";
	    Pattern pattern = Pattern.compile(patternValue);
	    if(pattern.matcher(str).matches()){//浮点数
	    	returnValue=true;
	    }
	    
	    pattern = Pattern.compile("^[1-9]\\d*$");
	    if(pattern.matcher(str).matches()){//正整数
	    	returnValue=true;
	    }	    	
	    return returnValue;     
	 } 
	
	/**
	 * 单个判断是否为数字
	 * @param str
	 * @return
	 */
	
	public static boolean isNum(String str) { 
		for (int i = str.length(); --i >= 0;) { 
		if (!Character.isDigit(str.charAt(i))) { 
		return false; 
		} 
		} 
		return true; 
		}
	/**
	 * 获取EXCEL单元值
	 * @param cell
	 * @return
	 */
//	public static Object getCellValue(HSSFCell cell){	
//		/*这样最后就可以根据返回值直接toString()就可以获得单元格的String值而无视单元格的类型*/
//		if(cell==null)
//			return "";
//		int type=cell.getCellType();
//		if(type==HSSFCell.CELL_TYPE_NUMERIC){//数值
//			double temp=cell.getNumericCellValue();
//			if(temp==(int)temp){
//				return new Integer((int)temp);
//			}else{
//				return new Double(temp);
//			}
//		}else if(type==HSSFCell.CELL_TYPE_BLANK){//空
//			return "";
//		}else if(type==HSSFCell.CELL_TYPE_BOOLEAN){//布尔
//			boolean temp=cell.getBooleanCellValue();
//			return new Boolean(temp);
//		}else{//字符串
//			String temp=cell.getRichStringCellValue().getString();
//			return temp;
//		}
//	}
	
	
	/**
	 * 获取EXCEL单元值
	 * @param cell
	 * @return
	 */
//	public static Object getCellValueTrunString(HSSFCell cell){	
//		/*这样最后就可以根据返回值直接toString()就可以获得单元格的String值而无视单元格的类型*/
//		if(cell != null){
//			int type=cell.getCellType();
//			if(type==HSSFCell.CELL_TYPE_NUMERIC){//数值
//				double temp=cell.getNumericCellValue();
//				if(temp==(int)temp){
//					return new Integer((int)temp).toString();
//				}else{
//					return new Double(temp).toString();
//				}
//			}else if(type==HSSFCell.CELL_TYPE_BLANK){//空
//				return "";
//			}else{//字符串
//				String temp=cell.getRichStringCellValue().getString();
//				return temp;
//			}
//		}else{
//			return "";
//		}
//	}
	
	/**
	 * 获取EXCEL单元值
	 * @param cell
	 * @return
	 */
//	public static Integer getCellValueTrunInteger(HSSFCell cell){	
//		/*这样最后就可以根据返回值直接toString()就可以获得单元格的String值而无视单元格的类型*/
//		int type=cell.getCellType();
//		if(type==HSSFCell.CELL_TYPE_NUMERIC){//数值
//			double temp=cell.getNumericCellValue();
//			if(temp==(int)temp){
//				return new Integer((int)temp);
//			}else{
//				
//				String str = cell.getRichStringCellValue().getString();
//				int index = str.indexOf(".");
//				str = str.substring(0,index);
//				
//				return Integer.valueOf(str);
//				
//			}
//		}else{//字符串
//			String str=cell.getRichStringCellValue().getString();
//			return Integer.valueOf(str);
//		}
//	}
	
//	获取年月日时分秒
	public static String ymdHmsDate(Date date) {
		if (date == null)
			return "";

		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return formater.format(date);
	}
	
//	获取年月日时分秒
	public static String loggerTime() {
		Calendar date = Calendar.getInstance();
		Date sysDate = date.getTime();
		String optDate = PubMethod.dateToString(sysDate, "yyyy-MM-dd HH:mm:ss:SSS");
		return optDate;
	}
	
	
	/**
	 * 构造字符串
	 * @param str
	 * @param newId
	 * @return
	 */
	public static String bulidString(String str,Long newId){
		if(null != str  && !"".equals(str) && !PubMethod.isEmpty(newId)){
			str = str+","+newId.toString();
		}else if(!PubMethod.isEmpty(newId)){
			str = newId.toString();
		}
		return str;
	}
	
	public static String bulidStringTypeStr(String str,String newStr){
		if(!PubMethod.isEmpty(str)){
			if(!PubMethod.isEmpty(newStr)){
				str = str+","+newStr;
			}
			
		}else{
			str = newStr;
		}
		return str;
	}
	
	
	/**
	 * 返回字符串 
	 * @param senderIdStr
	 * @return 格试 '1','2','3'
	 */
	public static String returnSenderOrderIds(String str){
		String newSendIdStr = "";
		if(!PubMethod.isEmpty(str)){
			str = str.trim();
			str = str.replaceAll("\r\n", ",");
			str = str.replaceAll("'", "");
			str = str.replaceAll(" ", ",");
			str = str.replaceAll("、", ",");
			str = str.replaceAll("，", ",");
			
			str = str.replaceAll("。", ",");
			str = str.replaceAll(";", ",");
			str = str.replaceAll("；", ",");
			str = str.replaceAll(":", ",");
			str = str.replaceAll("：", ",");
			
			
			String[] strs = str.split(",");
			
			for(int i=0;i<strs.length;i++){
				String string = strs[i];
				if(!PubMethod.isEmpty(string)){
					if(PubMethod.isEmpty(newSendIdStr)){
						newSendIdStr = "'"+string+"'";
					}else{
						newSendIdStr = newSendIdStr+","+"'"+string+"'";
					}
				}
				
			}
			
			
		}
		return newSendIdStr;
	}
	
	/**
	 * 返回字符串 
	 * @param senderIdStr
	 * @return 格试 1,2,3
	 */
	public static String returnSenderOrderIdsNotHaveInvertedComma(String str){
		String newSendIdStr = "";
		if(!PubMethod.isEmpty(str)){
			str = str.trim();
			str = str.replaceAll("\r\n", ",");
			str = str.replaceAll(" ", ",");
			str = str.replaceAll("'", "");
			String[] strs = str.split(",");
			for(int i=0;i<strs.length;i++){
				String string = strs[i];
				if(!PubMethod.isEmpty(string)){
					if(PubMethod.isEmpty(newSendIdStr)){
						newSendIdStr = string;
					}else{
						newSendIdStr = newSendIdStr+","+string;
					}
				}
				
			}
			
			
		}
		return newSendIdStr;
	}
	
	
	
	public static String returnSenderOrderIdsNotHaveInvertedCommaTs(String str){
		String newSendIdStr = "";
		if(!PubMethod.isEmpty(str)){
			str = str.trim();
			str = str.replaceAll("\r\n", ",");
			str = str.replaceAll(" ", ",");
			str = str.replaceAll("、", ",");
			str = str.replaceAll("，", ",");
			str = str.replaceAll("'", "");
			str = str.replaceAll("。", ",");
			str = str.replaceAll(";", ",");
			str = str.replaceAll("；", ",");
			str = str.replaceAll(":", ",");
			str = str.replaceAll("：", ",");
			str = str.replaceAll("-", ",");
			String[] strs = str.split(",");
			
			for(int i=0;i<strs.length;i++){
				String string = strs[i];
				if(!PubMethod.isEmpty(string)){
					if(PubMethod.isEmpty(newSendIdStr)){
						newSendIdStr = string;
					}else{
						newSendIdStr = newSendIdStr+","+string;
					}
				}
				
			}
			
			
		}
		return newSendIdStr;
	}
	
	
	
	
	/**
	 * 返回日期之差的秒数
	 * @param dates
	 * @param datee
	 * @return
	 */
	public static long differThird(String dates, String datee) 
	{ 
		
		
		Date date1 = strToDateLong(dates);
	    Date date2 = strToDateLong(datee);
	    return (date2.getTime() - date1.getTime())/1000;  //用立即数，减少乘法计算的开销
	    
	    
	}
	
	
	/**
	    * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
	    * 
	    * @param strDate
	    * @return
	    */
	public static Date strToDateLong(String strDate) {
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    ParsePosition pos = new ParsePosition(0);
	    Date strtodate = formatter.parse(strDate, pos);
	    return strtodate;
	}
	
//	public static String toPinYin(String str){
//		HanyuPinyinOutputFormat outputFormat = new HanyuPinyinOutputFormat();
//		        
//        outputFormat.restoreDefault();
//        outputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
//        outputFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
//        
//       // outputFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
//      //  outputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
//      //  outputFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
//
//        return PinyinHelper.toHanyuPinyinString(str, outputFormat, " ");
//        
//	}
//	
//	public static String toPinYinNew(String str){
//		HanyuPinyinOutputFormat outputFormat = new HanyuPinyinOutputFormat();
//		        
//     //   outputFormat.restoreDefault();
//      //  outputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
//     //   outputFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
//        
//        outputFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
//        outputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
//        outputFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
//
//        return PinyinHelper.toHanyuPinyinStringNew(str, outputFormat, " ");
//        
//	}
//	//汉字首字母大写转换
//	public static String getPinYinHeadChar(String str) { 
//		HanyuPinyinOutputFormat outputFormat = new HanyuPinyinOutputFormat();
//        
//        outputFormat.restoreDefault();
//        outputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
//        outputFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
//		String convert = "";   
//		for (int j = 0; j < str.length(); j++) {   
//		//提取每一个汉字 ！Char能存储汉字 这个是Java基础哦。   
//		char word = str.charAt(j);   
//		// 提取汉字的首字母   
//		String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word,outputFormat);   
//		
//		if (pinyinArray != null) {   
//		//如果是汉字能提取当前首字母   
//		convert += pinyinArray[0].charAt(0);   
//		} else {   
//		//如果不是汉字 非汉字类型 英语类型 不用转换   
//		convert += word;   
//		}   
//		}   
//		 return convert;   
//		}  

	/**
	 * 数值合并
	 * @author baihui
	 * @param a
	 * @param b
	 * @return
	 */
	public static Object[] SeveralCombines(Object[] a ,Object[] b){
		 
		Object[] s = new  Object[a.length+b.length] ;
	 
		int i  ;
		for(i = 0 ; i < a.length  ;i++){
			s[i]=a[i] ;
		}
		
		for(int j = 0 ; j < b.length ;j++){
			s[i+j]=b[j] ;
		}
		
		return s  ;
	}
	
	public static String[] chineseDigits = new String[] { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"}; 

	/** 
	* 把金额转换为汉字表示的数量，小数点后四舍五入保留两位 
	* @param amount 
	* @return 
	*/ 
	public static String amountToChinese(double amount) { 

	if(amount > 99999999999999.99 || amount < -99999999999999.99) 
	throw new IllegalArgumentException("参数值超出允许范围 (-99999999999999.99 ～ 99999999999999.99)！"); 

	boolean negative = false; 
	if(amount < 0) { 
	negative = true; 
	amount = amount * (-1); 
	} 

	long temp = Math.round(amount * 100); 
	int numFen = (int)(temp % 10); // 分 
	temp = temp / 10; 
	int numJiao = (int)(temp % 10); //角 
	temp = temp / 10; 
//	temp 目前是金额的整数部分 

	int[] parts = new int[20]; // 其中的元素是把原来金额整数部分分割为值在 0~9999 之间的数的各个部分 
	int numParts = 0; // 记录把原来金额整数部分分割为了几个部分（每部分都在 0~9999 之间） 
	for(int i=0; ; i++) { 
	if(temp ==0) 
	break; 
	int part = (int)(temp % 10000); 
	parts[i] = part; 
	numParts ++; 
	temp = temp / 10000; 
	} 

	boolean beforeWanIsZero = true; // 标志“万”下面一级是不是 0 

	String chineseStr = ""; 
	for(int i=0; i<numParts; i++) { 

	String partChinese = partTranslate(parts[i]); 
	if(i % 2 == 0) { 
	if("".equals(partChinese)) 
	beforeWanIsZero = true; 
	else 
	beforeWanIsZero = false; 
	} 

	if(i != 0) { 
	if(i % 2 == 0) 
	chineseStr = "亿" + chineseStr; 
	else { 
	if("".equals(partChinese) && !beforeWanIsZero) // 如果“万”对应的 part 为 0，而“万”下面一级不为 0，则不加“万”，而加“零” 
	chineseStr = "零" + chineseStr; 
	else { 
	if(parts[i-1] < 1000 && parts[i-1] > 0) // 如果"万"的部分不为 0, 而"万"前面的部分小于 1000 大于 0， 则万后面应该跟“零” 
	chineseStr = "零" + chineseStr; 
	chineseStr = "万" + chineseStr; 
	} 
	} 
	} 
	chineseStr = partChinese + chineseStr; 
	} 

	if("".equals(chineseStr)) // 整数部分为 0, 则表达为"零元" 
	chineseStr = chineseDigits[0]; 
	else if(negative) // 整数部分不为 0, 并且原金额为负数 
	chineseStr = "负" + chineseStr; 

	chineseStr = chineseStr + "元"; 

	if(numFen == 0 && numJiao == 0) { 
	chineseStr = chineseStr + "整"; 
	} 
	else if(numFen == 0) { // 0 分，角数不为 0 
	chineseStr = chineseStr + chineseDigits[numJiao] + "角"; 
	} 
	else { // “分”数不为 0 
	if(numJiao == 0) 
	chineseStr = chineseStr + "零" + chineseDigits[numFen] + "分"; 
	else 
	chineseStr = chineseStr + chineseDigits[numJiao] + "角" + chineseDigits[numFen] + "分"; 
	} 

	return chineseStr; 

	} 


	/** 
	* 把一个 0~9999 之间的整数转换为汉字的字符串，如果是 0 则返回 "" 
	* @param amountPart 
	* @return 
	*/ 
	private static String partTranslate(int amountPart) { 

	if(amountPart < 0 || amountPart > 10000) { 
	throw new IllegalArgumentException("参数必须是大于等于 0，小于 10000 的整数！"); 
	} 


	String[] units = new String[] {"", "拾", "佰", "仟"}; 

	int temp = amountPart; 

	String amountStr = new Integer(amountPart).toString(); 
	int amountStrLength = amountStr.length(); 
	boolean lastIsZero = true; //在从低位往高位循环时，记录上一位数字是不是 0 
	String chineseStr = ""; 

	for(int i=0; i<amountStrLength; i++) { 
	if(temp == 0) // 高位已无数据 
	break; 
	int digit = temp % 10; 
	if(digit == 0) { // 取到的数字为 0 
	if(!lastIsZero) //前一个数字不是 0，则在当前汉字串前加“零”字; 
	chineseStr = "零" + chineseStr; 
	lastIsZero = true; 
	} 
	else { // 取到的数字不是 0 
	chineseStr = chineseDigits[digit] + units[i] + chineseStr; 
	lastIsZero = false; 
	} 
	temp = temp / 10; 
	} 
	return chineseStr; 
	} 
	
	public static String getStringFromArray(Long[] array) {
		String str = "";
		if (array == null || array.length <= 0)
			return "";

		for (int i = 0; i < array.length; i++) {
			str += array[i] + ",";
		}
		if (str.length() > 0)
			str = str.substring(0, str.length() - 1);

		return str;
		
	}
	
	
	public static String getNotFormatToday() {
		return getToday().replaceAll("-", "");
	}
	
	public static String getToday() {
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
		return formater.format(new Date());
	}

	public static String getTimeStamp() {
		StringBuilder str = new StringBuilder();
		Date ca = Calendar.getInstance().getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");	
		str.append(sdf.format(ca)) ;		
		return str.toString() ;
	}
	/**
	 * 判断是否为数字
	 * @param str
	 * @return
	 */
	public static boolean isNumericOrPrice(String str){  		
		boolean returnValue=false;		
		String patternValue="^(0|[1-9]\\d*)(\\.\\d+)?$";
	    Pattern pattern = Pattern.compile(patternValue);
	    if(pattern.matcher(str).matches()){//浮点数
	    	returnValue=true;
	    }
	    
	  	    	
	    return returnValue;     
	 } 
	
	
	
	public static void main(String[] str){
		/*Map map = new HashMap();
		map.put("a", null);
		System.out.print(map.get("a"));
		String detailAddress = "123456789";
		detailAddress.contains("456");
		System.out.print(detailAddress.contains("127"));
		
		if(str.length == 0) { 
			System.out.println("-------------------------"); 
			System.out.println("25000000000005.999: " + amountToChinese(25000000000005.999)); 
			System.out.println("45689263.626: " + amountToChinese(45689263.626)); 
			System.out.println("0.69457: " + amountToChinese(0.69457)); 
			System.out.println("253.0: " + amountToChinese(253.0)); 
			System.out.println("0: " + amountToChinese(0)); 
			System.out.println("-------------------------"); 

			} 
			else { 
			System.out.println("转换结果："); 
			System.out.println(str[0] + ": " + amountToChinese(Double.parseDouble(str[0]))); 
		} 
             */
		//Date a = stringToDate("2009-02-28");
		//Date b = getDateMonthGap(a,1);
		//System.out.println(dateToString(b,"yyyy-MM-dd")); 
		/*System.out.println(isNumericOrPrice("1.35")); 
		System.out.println(isNumericOrPrice("0.54")); 
		System.out.println(isNumericOrPrice("0.00")); 
		System.out.println(isNumericOrPrice("1444.3599")); 
		
		System.out.println(toPinYinNew("田亚莲"));
		System.out.println(getLastHours(6));*/
		
		System.out.println(getSysOptHour());
	}
	
	private static boolean isReturn(String statusDesc, Map result) {
		
		
		if(statusDesc.indexOf("到达")<0&&statusDesc.indexOf("离开")<0&&statusDesc.indexOf("安排投递")<0&&statusDesc.indexOf("投递并签收")<0)
			return false;
		
		Set<String> sets = result.keySet();
		//按时间排序
		List<String> times = new ArrayList<String>(sets);
		Collections.sort(times);
		if(statusDesc.indexOf("投递并签收")>=0){
			String firstDesc=(String)result.get(times.get(0));
			String firstAdd = firstDesc.substring(0,firstDesc.lastIndexOf("收寄"));
			String lastAdd = statusDesc.substring(0,statusDesc.indexOf("投递并签收"));
			if(firstAdd.equals(lastAdd))
				return true;
			else
				return false;
		}
		
		if(statusDesc.indexOf("安排投递")>=0){
			String firstDesc=(String)result.get(times.get(0));
			String firstAdd = firstDesc.substring(0,firstDesc.lastIndexOf("收寄"));
			String lastAdd = statusDesc.substring(0,statusDesc.indexOf("安排投递"));
			if(firstAdd.equals(lastAdd))
				return true;
			else
				return false;
		}
		boolean r = false;
		for(String key:times){		
			String temDesc=(String)result.get(key);
			if(temDesc.equals(statusDesc))
				return r;
			if(temDesc.indexOf("未妥投")>=0){
				r=true;
				break;
			}				
		}
		return r;
	}
	
	/**
	 * 换算两个日期之间的秒数数
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static long getHourSecond(String time1, String time2){
		  long quot = 0;
		  //long day = 0;
		  long hour = 0;
		  SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			  try {
				   Date date1 = formatter.parse( time1 );
				   Date date2 = formatter.parse( time2 );
				   quot = date1.getTime() - date2.getTime();
				   hour = quot / 1000 ;
				   
			  } catch (Exception e) {
				  e.printStackTrace();
			  }
		
		return hour;
	}
	
	/**
	 * 判断两个对象的类型是否一致
	 * 创 建 人:  文超
	 * 创建时间:  2011-9-11 下午03:02:43  
	 * @param obj1
	 * @param obj2
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public static boolean isSameClassType(Object obj1,Object obj2){
		if(PubMethod.isEmpty(obj1)||PubMethod.isEmpty(obj2)){
			return false;
		}
		boolean flag=obj1.getClass().getName().equals(obj2.getClass().getName());
		return flag;
	}
	
	/**
	 * 过滤属性字段
	 * 创 建 人:  文超
	 * 创建时间:  2011-9-11 下午03:04:13  
	 * @param fieldsArray  JavaBean 所以的属性项集合
	 * @param includeFieds 需要对比的属性值项
	 * @param excludeFields  将这些属性排除在外，不进行对拼
	 * @param isMatch  是否需要完全匹配，true 完全匹配，false 非完全匹配。非完全匹配值如果某一个字段在属性项集合中不存在则正常进行
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public static List<String> filterFieldsName(Field[] fieldsArray,String includeFieds,String excludeFields,boolean isMatch){
		List<String> result = new ArrayList<String>();
		
		if (PubMethod.isEmpty(fieldsArray)) {
			return null;
		}
		
		int fieldsLength = fieldsArray.length;
		List<String> fieldsName = new ArrayList<String>();
		for(int i=0;i<fieldsLength;i++){
			fieldsName.add(fieldsArray[i].getName());
		}
		
		/**如果此两个参数值都为空，则全部进行对比*/
		if(PubMethod.isEmpty(includeFieds) && PubMethod.isEmpty(excludeFields)){
			 result.addAll(fieldsName);
			 return result;
		}
		/**只匹配知道的属性*/
		if(!PubMethod.isEmpty(includeFieds)){
			String[] array = includeFieds.split(",");
			for(int i=0;i<array.length;i++){
				String inName =array[i];
				if(!fieldsName.contains(inName)){
				   if(isMatch){//如果是完全匹配，则直接返回
					   return null;
				   }
				}else{
					result.add(inName);
				}
			}
			return result;
		}
		/**排除的属性字段*/
		if(!PubMethod.isEmpty(excludeFields)){
			String[] array = excludeFields.split(",");
			result.addAll(fieldsName);
			for(int i=0;i<array.length;i++){
				String exName =array[i];
				if(!fieldsName.contains(exName)){
				   if(isMatch){
					   return null;
				   }
				}else{
					result.remove(exName);
				}
			}
			return result;
		}
		
		return null;
	}
	
	/**
	 * 比较两个实例对象属性值是否相同
	 * 创 建 人:  文超
	 * 创建时间:  2011-9-11 下午03:07:09  
	 * @param obj1  对象1
	 * @param obj2  对象2
	 * @param includeFieds  指定对比的属性
	 * @param excludeFields  将这些属性排除在外，不进行对拼
	 * @param isMatch  是否需要完全匹配，true 完全匹配，false 非完全匹配
	 * @return  对象对比的属性值相同 返回 true，不相同 返回false
	 * @see [类、类#方法、类#成员]
	 */
	public static boolean compareObjFieldVals(Object obj1,Object obj2,String includeFieds,String excludeFields,boolean isMatch){
		boolean result=true;
		boolean isSame = PubMethod.isSameClassType(obj1, obj2);
		if(!isSame){//如果两对象不是同一类型，则直接返回false
			return false;
		}
		
		Field[] obj1Fieldsarray = obj1.getClass().getDeclaredFields();
		List<String> fieldsName = PubMethod.filterFieldsName(obj1Fieldsarray, includeFieds, excludeFields, isMatch);
		if(!PubMethod.isEmpty(fieldsName)){
			org.springframework.beans.BeanWrapper beanWrapper1 = new org.springframework.beans.BeanWrapperImpl(obj1);
			org.springframework.beans.BeanWrapper beanWrapper2 = new org.springframework.beans.BeanWrapperImpl(obj2);
			for (String fieldname:fieldsName) {//对象属性值 进行比较
				Object objval1 = new Object();
				Object objval2 = new Object();
				if (!fieldname.equals("serialVersionUID")&& !fieldname.toUpperCase().equals("CGLIB$BOUND")) {
					try {
						objval1 = beanWrapper1.getPropertyValue(fieldname);
						objval2 = beanWrapper2.getPropertyValue(fieldname);
						if(PubMethod.isEmpty(objval1)){
							if(!PubMethod.isEmpty(objval2)){
								return false;
							}
						}else{
							if(!objval1.equals(objval2)){
								return false;
							}
						}
					} catch (BeansException e) {
						e.printStackTrace();
					}
				}
			}
		}else{
			return false;
		}
		return result;
	}
	/**
	 * 换算两个日期之间的天-时-分
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static String getHourMinute(String time1, String time2){
		long quot = 0;
		  long day = 0;
		  long hour = 0;
		  long minute = 0;
		  String dayHour = "";
		  SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			  try {
				   Date date1 = formatter.parse( time1 );
				   Date date2 = formatter.parse( time2 );

				   quot = date1.getTime() - date2.getTime();
				   minute = (quot/1000-quot/1000/3600*3600)/60;
				   quot = quot / 1000 / 60 / 60 ;
				   
				   
				   day = quot/24;
				   hour = quot%24;
				   
			   
			  } catch (Exception e) {
				  e.printStackTrace();
			  }
		if(day == 0 && hour == 0){
			dayHour = minute+"分";
		}else if(day == 0 && hour!=0){
			dayHour = hour+"小时"  + minute + "分";
		}else
		{
			if(day>1)
			{
				dayHour = day+"天" +hour+"小时"  + minute + "分"+"-true";	
			}else
			{
				dayHour = day+"天" +hour+"小时"  + minute + "分"+"-false";
			}
				
			
		}
		return dayHour;
	}

	/**
	 * <功能详细描述>
	 * 创建时间:  2012-2-23 上午11:17:45  
	 * @param time1
	 * @param time2
	 * @param subDay  相差的天数，如果天数超过此值，后面跟true，否则跟false
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public static String getHourMinute(String time1, String time2,int subDay){
		long quot = 0,day = 0,hour = 0,minute = 0;
		String dayHour = "";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
		    Date date1 = formatter.parse( time1 );
		    Date date2 = formatter.parse( time2 );
		    quot = date1.getTime() - date2.getTime();
			minute = (quot/1000-quot/1000/3600*3600)/60;
		    quot = quot / 1000 / 60 / 60 ;
		    day = quot/24;
		    hour = quot%24;
				   
			   
			  } catch (Exception e) {
				  e.printStackTrace();
			  }
		if(day == 0 && hour == 0){
			dayHour = minute+"分";
		}else if(day == 0 && hour!=0){
			dayHour = hour+"小时"  + minute + "分";
		}else
		{
			if(day>subDay)
			{
				dayHour = day+"天" +hour+"小时"  + minute + "分"+"-true";	
			}else
			{
				dayHour = day+"天" +hour+"小时"  + minute + "分"+"-false";
			}
				
			
		}
		return dayHour;
	}


	
	
	/**
	 * 淘宝签名算法
	 * @param parameter
	 * @param secret
	 * @param encode
	 * @return
	 */
//	public static String sign(String parameter, String secret, String encode) {
//	      // 对参数+密钥做MD5运算
//	      MessageDigest md = null;
//	      try {
//	         md = MessageDigest.getInstance("MD5");
//	      } catch (NoSuchAlgorithmException e) {
//	      }
//	      if(encode == null){//没指定编码
//	         byte[] digest = md.digest((parameter + secret).getBytes());
//	         return new String(Base64.encodeBase64(digest));
//	      }else{//指定了编码
//	         try{
//	            byte[] digest = md.digest((parameter + secret).getBytes(encode));   
//	            return new String(Base64.encodeBase64(digest),encode);
//	         } catch(UnsupportedEncodingException e) {}
//	      }
//	      return null;
//	   }

	/**
	 * java特殊字符转换
	 * 创 建 人:  文超
	 * 创建时间:  2011-10-29 下午05:24:32  
	 * @param input
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public static String  filterString(String input){
		Map<String,String> reg = new HashMap<String,String>();
		reg.put("&lt;", "<");
		reg.put("&gt;", ">");
		reg.put("&quot;", "\"");
		reg.put("&amp;", "&");
		reg.put("&apos;", "'");
		Iterator it = reg.keySet().iterator();
		while(it.hasNext()){
			String key = (String)it.next();
			String value = reg.get(key);
			input = input.replaceAll(key, value);
		}
		return input;
	}
	
	/**
	 * 获取时间list对象
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static List<Map<String,String>> getListTime(String startTime,String endTime){
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		Date endD = PubMethod.strToDateLong(endTime);
		Calendar c1 = Calendar.getInstance();
		c1.setTime(PubMethod.strToDateLong(startTime));
		Calendar c2 = Calendar.getInstance();
		c2.setTime(PubMethod.strToDateLong(endTime));
//		Map<String,String > map = new HashMap<String, String>();
		//当天
		if(isOneDay(c1,c2)){
			Map<String,String > map1 = new HashMap<String, String>();
			map1.put("start",startTime);
			map1.put("end", endTime);
			list.add(map1);
			return list;
		}
		//大于1天
		while(!isOneDay(c1,c2)){
			Map<String,String > map = new HashMap<String, String>();
			Date date = c1.getTime();
			map.put("start",PubMethod.formatDateTime(date, "yyyy-MM-dd HH:mm:ss"));
			map.put("end", PubMethod.formatDateTime(date, "yyyy-MM-dd")+" 23:59:59");
			list.add(map);
			String newdate = PubMethod.formatDateTime(date, "yyyy-MM-dd")+" 00:00:00";
			c1.setTime(PubMethod.strToDateLong(newdate));
			c1.add(c1.DAY_OF_MONTH, 1);
		}
		//最后一天
		Map<String,String > map= new HashMap<String, String>();
		map.put("start",PubMethod.formatDateTime(c1.getTime(), "yyyy-MM-dd HH:mm:ss"));
		map.put("end", endTime);
		list.add(map);
		return list;
	}
	public static boolean isOneDay(Calendar c1,Calendar c2){
		if((c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR))
				&& (c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH))   
				&& (c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH)))
			return true;
		else 
			return false;
	}
	
	
	
	/****
	 * 字符串转成数组
	 * dulin
	 * */
	public static String[] StrList(String ValStr)
	{
	    int i = 0;
	    String TempStr = ValStr;
	    String[] returnStr = new String[ValStr.length() + 1 - TempStr.replace(",", "").length()];
	    ValStr = ValStr + ",";
	    
	    
	    while (ValStr.indexOf(',') > 0)
	    {
	        returnStr[i] = ValStr.substring(0, ValStr.indexOf(","));
	        ValStr = ValStr.substring(ValStr.indexOf(",") + 1, ValStr.length());
	        i++;
	    }
	    return returnStr;
	}
	
	/**
	 * 字符串的连接
	 * 创 建 人:  文超
	 * 创建时间:  2012-1-5 下午07:29:54  
	 * @param srcStr
	 * @param addStr
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public static StringBuffer concat(StringBuffer srcStr,String addStr){
		if(!PubMethod.isEmpty(srcStr) && srcStr.length()>0){
			srcStr.append(",").append(addStr);
		}else{
			srcStr.append(addStr);
		}
		return srcStr;
	}
	

	/**
	 * 网页中文数据转码 ,如&#26080;&#26597;&#35810;&#35760;&#24405; 转换为汉字
	 * @param dataStr
	 * @return
	 */
   public static String decodeUnicode(final String dataStr) {
	        int start = 0;
	        int end = 0;
	        final StringBuffer buffer = new StringBuffer();
	        while (start > -1) {
	            int system = 10;//进制
	            if(start==0){
	                int t = dataStr.indexOf("&#");
	                if(start!=t)start = t;
	            }
	            end = dataStr.indexOf(";", start + 2);
	            String charStr = "";
	            if (end != -1) {
	                charStr = dataStr.substring(start + 2, end);

	                //判断进制
	                char s = charStr.charAt(0);
	                if(s=='x' || s=='X'){
	                    system = 16;
	                    charStr = charStr.substring(1);
	                }
	            }
	            //转换
	            try{
	            	 
	            	if(!PubMethod.isEmpty(charStr)){
	            		char letter = (char) Integer.parseInt(charStr,system);
	                    buffer.append(new Character(letter).toString());
	            	}
	               
	            }catch(NumberFormatException e){
	                e.printStackTrace();
	            }

	            //处理当前unicode字符到下一个unicode字符之间的非unicode字符
	            start = dataStr.indexOf("&#",end);
	            if(start-end>1){
	                buffer.append(dataStr.substring(end+1, start));
	            }

	            //处理最后面的非unicode字符
	            if(start==-1){
	                int length = dataStr.length();
	                if(end+1!=length){
	                    buffer.append(dataStr.substring(end+1,length));
	                }
	            }
	        }
	        return buffer.toString();
	    }	

   /**
    * 字符串拼接
    * 创 建 人:  文超
    * 创建时间:  2012-4-10 上午10:19:29  
    * @param array
    * @param type 0 返回数字串，如1,2,3。非0：返回字符串，如 '1','2','3'
    * @return
    * @see [类、类#方法、类#成员]
    */
   public static String joinToSql(String[] array,int type){
	   StringBuffer result = new StringBuffer();
	   if(PubMethod.isEmpty(array)||array.length<=0){
		   return null;
	   }
	   for(int i=0;i<array.length;i++){
		   String value = type==0?array[i]:"'"+array[i]+"'";
		   PubMethod.concat(result, value);
	   }
	   return result.toString();
   }
   
   /**
    * 数组转化为Map
    * 创 建 人:  文超
    * 创建时间:  2012-4-10 下午04:15:44  
    * @param array
    * @return
    * @see [类、类#方法、类#成员]
    */
   public static Map arrayToMap(String[] array){
	   Map map = new HashMap();
	   if(array.length<=0)
		   return map;
	   for(int i=0;i<array.length;i++){
			map.put(array[i], array[i]);
		}
	   return map;
   }
   
   /**
    * 格式化当前时间
    * 创 建 人:  文超
    * 创建时间:  2013-10-13 上午10:00:45  
    * @param format
    * @return
    * @see [类、类#方法、类#成员]
    */
   public static String getCurSysDate(String format) {
		Calendar date = Calendar.getInstance();
		Date sysDate = date.getTime();
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		String curDate = formatter.format(sysDate);
		return curDate;
	}
   
}
