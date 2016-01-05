package servlet;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FileDownloadServlet extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4918224801592814390L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String fileTrueName = req.getParameter("fileName");
		resp.setContentType("application/x-msdownload; charset=utf-8");
		resp.setHeader("Content-disposition", "attachment;filename=\""+fileTrueName+"\"");
		
		byte[] buffered = new byte[1024];
		
		BufferedInputStream input = new BufferedInputStream(new FileInputStream(this.getServletContext().getRealPath("/image")+"/"+fileTrueName));
		DataOutputStream output = new DataOutputStream(resp.getOutputStream());
		
		while (input.read(buffered,0,buffered.length) != -1) {
			output.write(buffered,0,buffered.length);
		}
		
		input.close();
		output.close();
	}
}
