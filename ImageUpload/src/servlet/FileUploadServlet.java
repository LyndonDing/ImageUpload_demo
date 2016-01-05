package servlet;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

public class FileUploadServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * servlet初始化
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}
  
	/**
	 * 取得上传表单参数并保存文件信息
	 * 
	 * @param request
	 * @param HttpServletResponse
	 * @throws response
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		boolean flag = false;
		String successMessage = "Upload file successed.";

		String fileName = null;
		DataInputStream in = null; 
		FileOutputStream fileOut = null;
		
		/** 取得客户端的传递类型 */
		String contentType = request.getContentType();
		byte dataBytes[] = null ; 
		try {
			/** 确认数据类型是 multipart/form-data */
			if (contentType != null
					&& contentType.indexOf("multipart/form-data") != -1) {
				request.setCharacterEncoding("utf-8");
				/** 取得上传文件流的字节长度 */
				int fileSize = request.getContentLength();
				
				Enumeration<String> names = request.getParameterNames();
				while (names.hasMoreElements()) {
					String string = (String) names.nextElement();
					System.out.println(string);
				}
				/** 可以判断文件上传上线
				if (fileSize > MAX_SIZE) {
					successMessage = "Sorry, file is too large to upload.";
					return;
				} */
				
				final Part filePart = request.getPart("imageFiles");
				
//			    final String fileName = getFileName(filePart);
				
				/** 读入上传的数据 */
				in = new DataInputStream(filePart.getInputStream());

				/** 保存上传文件的数据 */ 
				int byteRead = 0; 
				int totalBytesRead = 0;
				dataBytes = new byte[fileSize];
				
				/** 上传的数据保存在byte数组 */
				while(totalBytesRead < fileSize){ 
					byteRead = in.read(dataBytes, totalBytesRead, fileSize); 
					totalBytesRead += byteRead; 
				} 
				
				int i = dataBytes.length;
				/** 根据byte数组创建字符串 */
				String file = new String(dataBytes,"UTF-8");
				i = file.length();
				/** 取得上传的数据的文件名 */
				
				String upFileName = file.substring(file.indexOf("filename=\"") + 10);
				upFileName = upFileName.substring(0, upFileName.indexOf("\n"));
				upFileName = upFileName.substring(upFileName.lastIndexOf("\\") + 1, upFileName.indexOf("\"")); 

				/** 取得数据的分隔字符串 */
				String boundary = contentType.substring(contentType.lastIndexOf("boundary=") + 9, contentType.length()); 
				/** 创建保存路径的文件名 */
				fileName = upFileName;
				
				int pos; 
				pos = file.indexOf("filename=\""); 
				pos = file.indexOf("\n",pos) + 1; 
				pos = file.indexOf("\n",pos) + 1; 
				pos = file.indexOf("\n",pos) + 1; 
				int boundaryLocation = file.indexOf(boundary, pos)-4;
				
				/** 取得文件数据的开始的位置 */
				int startPos = file.substring(0, pos).length();
				
				/** 取得文件数据的结束的位置 */
				int endPos = file.substring(boundaryLocation).length();
				
				/** 创建文件的写出类 */
				fileOut = new FileOutputStream(this.getServletContext().getRealPath("/image")+"/"+fileName); 
				/** 保存文件的数据 */
				fileOut.write(dataBytes, startPos, (fileSize - endPos - startPos));
				
			}else{ 
				successMessage = "Data type is not multipart/form-data.";
			}
	
		} catch (Exception e) {
			successMessage = e.getMessage();

		} finally {
			try {
				//close open file
				in.close();
				if(flag){
					response.getOutputStream().write(("<a href=\"download?fileName="+fileName+"\" ><img src=\"image/"+fileName+"\"/></a>").getBytes());
				}
				response.getOutputStream().write(successMessage.getBytes());
				response.getOutputStream().close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 释放
	 */
	public void destroy() {
		/** 调用父类的方法 */
		super.destroy();
	}
	
	private String getFileName(final Part part) {
	    final String partHeader = part.getHeader("content-disposition");
	    for (String content : part.getHeader("content-disposition").split(";")) {
	        if (content.trim().startsWith("filename")) {
	            return content.substring(
	                    content.indexOf('=') + 1).trim().replace("\"", "");
	        }
	    }
	    return null;
	}
}
