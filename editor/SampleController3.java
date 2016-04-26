package pantheon.sample.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Clob;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import pantheon.sample.dao.SampleDAO;

@Controller
public class SampleController3 {

	public static String clobToString(Clob clob) throws SQLException,
			IOException {

		if (clob == null) {
			return "";
		}

		StringBuffer strOut = new StringBuffer();

		String str = "";

		BufferedReader br = new BufferedReader(clob.getCharacterStream());

		while ((str = br.readLine()) != null) {
			strOut.append(str);
		}
		return strOut.toString();
	}

	@Resource(name = "SampleDAO")
	private SampleDAO sampleDAO;

	@RequestMapping(value = "/editor/insert.do")
	public void insert(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Map<String, String> param = new HashMap<String, String>();
		param.put("TITLE", request.getParameter("title"));
		param.put("CONTENT", request.getParameter("content"));
		sampleDAO.insertSample5(param);
	}

	@RequestMapping(value = "/editor/select.do")
	public String select(HttpServletRequest request,
			HttpServletResponse response, ModelMap map) throws Exception {
		Map<String, String> param = new HashMap<String, String>();
		map.addAttribute("SELECT", sampleDAO.selectSample2(param));
		return "pantheon/editor";
	}

	@RequestMapping("/file_uploader.do")
	public String file_uploader(HttpServletRequest request,
			HttpServletResponse response, Editor editor) {
		String return1 = request.getParameter("callback");
		String return2 = "?callback_func="
				+ request.getParameter("callback_func");
		String return3 = "";
		String name = "";
		try {
			if (editor.getFiledata() != null
					&& editor.getFiledata().getOriginalFilename() != null
					&& !editor.getFiledata().getOriginalFilename().equals("")) {
				// 기존 상단 코드를 막고 하단코드를 이용
				name = editor
						.getFiledata()
						.getOriginalFilename()
						.substring(
								editor.getFiledata().getOriginalFilename()
										.lastIndexOf(File.separator) + 1);
				String filename_ext = name.substring(name.lastIndexOf(".") + 1);
				filename_ext = filename_ext.toLowerCase();
				String[] allow_file = { "jpg", "png", "bmp", "gif" };
				int cnt = 0;
				for (int i = 0; i < allow_file.length; i++) {
					if (filename_ext.equals(allow_file[i])) {
						cnt++;
					}
				}
				if (cnt == 0) {
					return3 = "&errstr=" + name;
				} else {
					// 파일 기본경로
					String dftFilePath = request.getSession()
							.getServletContext().getRealPath("/");
					// 파일 기본경로 _ 상세경로
					String filePath = dftFilePath + "resource"
							+ File.separator + "editor" + File.separator
							+ "upload" + File.separator;
					File file = new File(filePath);
					if (!file.exists()) {
						file.mkdirs();
					}
					String realFileNm = "";
					SimpleDateFormat formatter = new SimpleDateFormat(
							"yyyyMMddHHmmss");
					String today = formatter.format(new java.util.Date());
					realFileNm = today + UUID.randomUUID().toString()
							+ name.substring(name.lastIndexOf("."));
					String rlFileNm = filePath + realFileNm;
					// /////////////// 서버에 파일쓰기 /////////////////
					editor.getFiledata().transferTo(new File(rlFileNm));
					// /////////////// 서버에 파일쓰기 /////////////////
					return3 += "&bNewLine=true";
					return3 += "&sFileName=" + name;
					return3 += "&sFileURL=/resource/editor/upload/"
							+ realFileNm;
				}
			} else {
				return3 += "&errstr=error";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:" + return1 + return2 + return3;
	}

	@RequestMapping("/file_uploader_html5.do")
	public void file_uploader_html5(HttpServletRequest request,HttpServletResponse response) {
		try {
			// 파일정보
			String sFileInfo = "";
			// 파일명을 받는다 - 일반 원본파일명
			String filename = request.getHeader("file-name");
			// 파일 확장자
			String filename_ext = filename
					.substring(filename.lastIndexOf(".") + 1);
			// 확장자를소문자로 변경
			filename_ext = filename_ext.toLowerCase();
			
			// 이미지 검증 배열변수
			String[] allow_file = { "jpg", "png", "bmp", "gif" };

			// 돌리면서 확장자가 이미지인지
			int cnt = 0;
			for (int i = 0; i < allow_file.length; i++) {
				if (filename_ext.equals(allow_file[i])) {
					cnt++;
				}
			}

			// 이미지가 아님
			if (cnt == 0) {
				PrintWriter print = response.getWriter();
				print.print("NOTALLOW_" + filename);
				print.flush();
				print.close();
			} else {
				// 이미지이므로 신규 파일로 디렉토리 설정 및 업로드
				// 파일 기본경로
				String dftFilePath = request.getSession().getServletContext()
						.getRealPath("/");
				// 파일 기본경로 _ 상세경로
				String filePath = dftFilePath + "resource" + File.separator
						+ "editor" + File.separator + "multiupload"
						+ File.separator;
				File file = new File(filePath);
				if (!file.exists()) {
					file.mkdirs();
				}
				String realFileNm = "";
				SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
				String today = formatter.format(new java.util.Date());
				realFileNm = today + UUID.randomUUID().toString()
						+ filename.substring(filename.lastIndexOf("."));
				String rlFileNm = filePath + realFileNm;
				// /////////////// 서버에 파일쓰기 /////////////////
				
//				MultipartFile Filedata = request
//				final MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
//				final Map<String, MultipartFile> files = multiRequest.getFileMap();
//				Iterator<Entry<String, MultipartFile>> itr = files.entrySet().iterator();
//				MultipartFile file1;
//				while (itr.hasNext()) {
//					Entry<String, MultipartFile> entry = itr.next();
//					System.out.println("[" + entry.getKey() + "]");
//					file1 = entry.getValue();
//					if (!"".equals(file1.getOriginalFilename())) {
//						file1.transferTo(new File(rlFileNm));
//					}
//				}
				
//				InputStream is = request.getInputStream();
				InputStream is = request.getInputStream();
				
				OutputStream os = new FileOutputStream(rlFileNm);
				int numRead;
				byte b[] = new byte[Integer.parseInt(request.getHeader("file-size"))];
				System.out.println(b.length);
				while ((numRead = is.read(b, 0, b.length)) != -1) {
					os.write(b, 0, numRead);
				}
				if (is != null) {
					is.close();
				}
				os.flush();
				os.close();
				// /////////////// 서버에 파일쓰기 /////////////////

				// 정보 출력
				sFileInfo += "&bNewLine=true";
				// img 태그의 title 속성을 원본파일명으로 적용시켜주기 위함
				sFileInfo += "&sFileName=" + filename;
				;
				sFileInfo += "&sFileURL=" + "/resource/editor/multiupload/"
						+ realFileNm;
				PrintWriter print = response.getWriter();
				print.print(sFileInfo);
				print.flush();
				print.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
