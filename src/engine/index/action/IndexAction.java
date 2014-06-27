package engine.index.action;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;
import common.PropertiesContainer;

import engine.index.IndexEngine;
import engine.index.dao.db.IndexDAO;
import engine.index.vo.IndexResultDataBean;
/**
 * request /indexAction.if에 따른 indexing logic 수행
 * @author KIMSEONHO
 *
 */
public class IndexAction implements Action {
	@Override
	public ActionForward execute(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		ActionForward forward = new ActionForward();
		IndexResultDataBean indexResultDataBean = null;
		IndexEngine indexEngine = null;
		
		// load properties file
		new PropertiesContainer();
		
		String realBookmarkFolder = null;
		String saveBookmarkFolder = PropertiesContainer.prop.getProperty("saveBookmarkFolder");
		// "/data/bookmark/chrome" - 북마크를 저장할 경로(상대경로)
		
		int fileSize = 5 * 1024 * 1024;
		
		realBookmarkFolder = saveBookmarkFolder;		// absolute path
		//realBookmarkFolder = request.getServletContext().getRealPath(saveBookmarkFolder);		// 상대경로
		// 절대경로로 변경
		try {
			MultipartRequest multi = null;
			
			multi = new MultipartRequest(
					request, realBookmarkFolder, fileSize, "utf-8", new DefaultFileRenamePolicy());
			// 북마크 저장
			Enumeration<?> files = multi.getFileNames();
			
			String file = (String) files.nextElement();
			String filename = multi.getFilesystemName(file);
			String donator = multi.getParameter("CONTRIBUTOR");
			
			System.out.println("IndexAction - filename " + filename);
			System.out.println("IndexAction - donator " + donator);
			
			String fileRealPath = realBookmarkFolder + "/" + filename;
			System.out.println("IndexAction - fileRealPath - " + fileRealPath);
			
			indexEngine = new IndexEngine();
			indexResultDataBean = indexEngine.start(fileRealPath, donator);
			
			IndexDAO indexDAO = new IndexDAO(); 
			indexDAO.updateURLUploadCount(donator, indexResultDataBean.getAssignedIdxNum());
			int totalAssignedIdxNum = indexDAO.getURLUploadCount(donator);
			indexDAO.close();
			
			indexResultDataBean.setTotalAssignedIdxNum(totalAssignedIdxNum);
			
			request.setAttribute("INDEX_RESULT", indexResultDataBean);
			
			System.out.println("IndexAction - Success");
			forward.setPath("/indexStatus.if");
			
			return forward;
			
		} catch (Exception ex) {
			System.err.println("IndexAction - err");
			ex.printStackTrace();
		}
		
		return null;
		
	}
}
