/*
*
*    Copyright © 2015-2016 Tübitak ULAKBIM
*
*    This file is part of Lider Ahenk.
*
*    Lider Ahenk is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    Lider Ahenk is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with Lider Ahenk.  If not, see <http://www.gnu.org/licenses/>.
*/
package tr.org.liderahenk.lider.core.api.utils;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.text.StrBuilder;

/**
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
@SuppressWarnings("rawtypes")
public class LiderCoreUtils {

	public static final String EMPTY = "";

	private static SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy H:m");

	public static boolean isInteger(String s) {
		if (s.isEmpty())
			return false;
		for (int i = 0; i < s.length(); i++) {
			if (i == 0 && s.charAt(i) == '-') {
				if (s.length() == 1)
					return false;
				else
					continue;
			}
			if (Character.digit(s.charAt(i), 10) < 0)
				return false;
		}
		return true;
	}

	public static String join(Collection collection, String separator) {
		if (collection == null) {
			return null;
		}
		return join(collection.iterator(), separator);
	}

	public static String join(Collection collection, String separator, StringJoinCursor cursor) {
		if (collection == null) {
			return null;
		}
		return join(collection.iterator(), separator, cursor);
	}

	public static String join(Iterator iterator, String separator, StringJoinCursor cursor) {
		// handle null, zero and one elements before building a buffer
		if (iterator == null) {
			return null;
		}
		if (!iterator.hasNext()) {
			return EMPTY;
		}
		Object first = iterator.next();
		if (!iterator.hasNext()) {
			return ObjectUtils.toString(cursor.getValue(first));
		}

		// two or more elements
		StrBuilder buf = new StrBuilder(256); // Java default is 16, probably
		// too small
		if (first != null) {
			buf.append(cursor.getValue(first));
		}

		while (iterator.hasNext()) {
			if (separator != null) {
				buf.append(separator);
			}
			Object obj = iterator.next();
			if (obj != null) {
				buf.append(cursor.getValue(obj));
			}
		}
		return buf.toString();
	}

	public static String join(Iterator iterator, String separator) {
		return join(iterator, separator, null);
	}

	public static Object getFieldValueIfExists(Object obj, String fieldName) {
		try {
			Field field = obj.getClass().getDeclaredField(fieldName);
			if (field != null) {
				field.setAccessible(true);
				Object val = field.get(obj);
				if (val instanceof Date) {
					return format.format((Date) val);
				}
				return val;
			}
		} catch (Exception e) {
		}
		return null;
	}

}
