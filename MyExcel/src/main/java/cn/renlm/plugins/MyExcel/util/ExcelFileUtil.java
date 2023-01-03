package cn.renlm.plugins.MyExcel.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.poifs.filesystem.FileMagic;

import cn.hutool.core.io.IORuntimeException;

public class ExcelFileUtil {

	public static boolean isXls(InputStream in) {
		return FileMagic.OLE2 == getFileMagic(in);
	}

	public static boolean isXlsx(InputStream in) {
		return FileMagic.OOXML == getFileMagic(in);
	}

	public static boolean isXlsx(File file) {
		try {
			return FileMagic.valueOf(file) == FileMagic.OOXML;
		} catch (IOException e) {
			throw new IORuntimeException(e);
		}
	}

	private static FileMagic getFileMagic(InputStream in) {
		FileMagic magic;
		in = FileMagic.prepareToCheckMagic(in);
		try {
			magic = FileMagic.valueOf(in);
		} catch (IOException e) {
			throw new IORuntimeException(e);
		}

		return magic;
	}

}
