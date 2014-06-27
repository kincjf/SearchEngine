package common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
/**
 * 파일 저장 관련 method
 * @author KIMSEONHO
 *
 */
public class FileContainer {
	/**
	 * parameter에 대한 데이터를 파일로 저장
	 * @param html			parsing한 html tag data(<body>)
	 * @param content		html Tag, white space를 제거한 데이터
	 * @param idxNum		file로 저장할 이름(url 고유 index 번호)
	 */
	public static void SaveFile(String html, String content, int idxNum) { // html, content 파일 저장
		// String filename = GetSTRFilter(title);
		System.out.println("filename : " + idxNum);
		
		String htmlFileFolder = PropertiesContainer.prop.getProperty("saveHtmlFile");
		String contentFileFolder = PropertiesContainer.prop.getProperty("saveContentFile");
		
		File htmlFile = new File(htmlFileFolder + idxNum + ".html");
		File contentFile = new File(contentFileFolder + idxNum + ".txt");

		try {
			if (htmlFile.createNewFile() == true) { // HTML파일 저장
				BufferedWriter output = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(
								htmlFile.getPath()), "UTF8"));

				output.write(html);
				output.close();
			}

			if (contentFile.createNewFile() == true) { // CONTENT파일 저장
				BufferedWriter output = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(
								contentFile.getPath()), "UTF8"));

				output.write(content);
				output.close();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("BasicCrawler - SaveFile : " + idxNum);
			e.printStackTrace();
		}

	}
	
	/**
	 * not used
	 * @param str			string data
	 * @return				fildering data
	 */
	public static String GetSTRFilter(String str) {
		/*String[] filter_word = { " ", "\\.", "\\?", "\\/", "\\>", "\\!", "\\@",
				"\\#", "\\$", "\\%", "\\^", "\\&", "\\*", "\\(", "\\)", "\\_",
				"\\+", "\\=", "\\|", "\\\\", "\\}", "\\]", "\\{", "\\[",
				"\\\"", "\\'", "\\:", "\\;", "\\<", "\\,", "\\>", "\\.", "\\?",
				"\\/" };*/
		String[] filter_word = { " ", "\\?", "\\/", "\\>", "\\&", "\\*", "\\(", "\\)", "\\_",
				"\\|", "\\\\", "\\\"", "\\'", "\\:", "\\<", "\\,", "\\>", "\\?", "\\/" };
		
		for (int i = 0; i < filter_word.length && str.length() > 0; i++) {
			str = str.replaceAll(filter_word[i], " ");
		}

		return str;

	}
	
	/**
	 * 파일의 확장자 제거
	 * @param str			file name
	 * @return				str which deleted extention
	 */
	public static String deleteExtension(String str) {
		int fileIdx = str.lastIndexOf(".");
		return str.substring(0, fileIdx);
	}
	
	/**
	 * url의 AnchorText 제거(hash tag등)
	 * @param str			url data
	 * @return				anchor text가 제거된 url
	 */
	public static String deleteAnchor(String str) {
		int fileIdx = str.lastIndexOf("#");
		String strTmp = str;
		
		if(fileIdx == -1) {
			return strTmp;
		} else {
			return str.substring(0, fileIdx);
		}
	}
}
