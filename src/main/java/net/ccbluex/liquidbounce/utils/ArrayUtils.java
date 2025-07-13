package net.ccbluex.liquidbounce.utils;

import java.util.ArrayList;
import java.util.Collections;

public class ArrayUtils {
	@SafeVarargs
	public static <T> ArrayList<T> getAsArray(T...objects){
		ArrayList<T> array = new ArrayList<>();
		Collections.addAll(array, objects);
		return array;
	}
}
