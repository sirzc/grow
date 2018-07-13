package com.sirzc.util.core;

import java.util.Stack;

/**
 * 
 * @Title: ShortIdUtil.java
 * @Package com.stylefeng.guns
 * @Description: 将自增id转成短链接,同时短连接可逆回自增id,相邻自增id无规律，防破解。基于Feistel的特性转为Base62。
 * @author 作者：Administrator
 * @date 创建时间：2018年5月23日 下午4:46:09
 * @version V1.0
 */
public class ShortIdUtil {

	/**
	 * 调整字符顺序可增加混淆
	 */
	private static char[] charSet = "0WqPQRI7yCDE31VONvSXnxY2bcdJK8zoLMZa9AmBjklpTUu45FGrst6Hwefghi".toCharArray();

	/**
	 * Feistel密码结构：如果permutedId(a)=b，那么必然会有permutedId(b)=a
	 *
	 * @param id
	 * @return
	 */
	public static Long permutedId(Long id) {
		Long l1 = (id >> 16) & 65535;
		Long r1 = id & 65535;

		for (int i = 0; i < 2; i++){
			Long l2 = r1;
			Long r2 = l1 ^ (int) (roundFunction(r1) * 65535);
			l1 = l2;
			r1 = r2;
		}
		return ((r1 << 16) + l1);
	}

	public static Double roundFunction(Long val) {
		return ((131239 * val + 15534) % 714025) / 714025.0;
	}

	/**
	 * 将10进制转化为62进制
	 *
	 * @param number
	 * @param length
	 *            转化成的62进制长度，不足length长度的话高位补0，否则不改变什么
	 * @return
	 */
	public static String convertDecimalToBase62(long number, int length) {
		Long rest = number;
		Stack<Character> stack = new Stack<Character>();
		StringBuilder result = new StringBuilder(0);
		while (rest != 0){
			stack.add(charSet[new Long((rest - (rest / 62) * 62)).intValue()]);
			rest = rest / 62;
		}
		for (; !stack.isEmpty();){
			result.append(stack.pop());
		}
		int resultLength = result.length();
		StringBuilder temp0 = new StringBuilder();
		for (int i = 0; i < length - resultLength; i++){
			temp0.append('0');
		}
		return temp0.toString() + result.toString();
	}

	/**
	 * 将62进制转换成10进制数
	 *
	 * @param str
	 * @return
	 */
	public static String convertBase62ToDecimal(String str) {
		int multiple = 1;
		long result = 0;
		Character c;
		for (int i = 0; i < str.length(); i++){
			c = str.charAt(str.length() - i - 1);
			result += getCharValue(c) * multiple;
			multiple = multiple * 62;
		}
		return result + "";
	}

	/**
	 * 获取字符下标
	 * 
	 * @param c
	 * @return
	 */
	private static int getCharValue(Character c) {
		for (int i = 0; i < charSet.length; i++){
			if (c == charSet[i]){
				return i;
			}
		}
		return -1;
	}

	/**
	 * 
	 * 自增id加密. <br/>
	 *
	 * @param data
	 * @return 加密后的id
	 * @author 作者：Administrator
	 */
	public static String encryptHex(String data) {
		
		if (ToolUtil.isNotEmpty(data)){
			Long newId = permutedId(Long.valueOf(data));
			data = convertDecimalToBase62(newId, 8);
		}
		return data;
	}

	/**
	 * 
	 * 自增id加密. <br/>
	 *
	 * @param data
	 * @return 加密后的id
	 * @author 作者：Administrator
	 */
	public static String encryptHex(Integer data) {
		if (ToolUtil.isEmpty(data))
			return null;
		return encryptHex(String.valueOf(data));
	}

	/**
	 * 
	 * 自增id解密. <br/>
	 *
	 * @param data
	 * @return 解密后的id
	 * @author 作者：Administrator
	 */
	public static String decryptStr(String data) {
		String preCode = convertBase62ToDecimal(data);
		return permutedId(Long.parseLong(preCode)).toString();
	}

	public static void main(String[] args) {
		//相邻转码无关性测试
		for (int i = 1; i < 2; i++){
			Long newId = permutedId(Long.valueOf(i));
			Long de = permutedId(newId);
			String code = ShortIdUtil.convertDecimalToBase62(newId, 8);
			String preCode = convertBase62ToDecimal(code);
			System.out.println(String.format("原ID=%d, 一次permuted的ID=%d, 二次permuted后的ID=%d, Base62加密后=%s,解密=%d", i,
					newId, de, code, permutedId(Long.parseLong(preCode))));
			System.out.println(code);
		}
		System.out.println("888" + String.valueOf(null));
	}
}