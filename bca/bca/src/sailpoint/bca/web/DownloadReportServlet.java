package sailpoint.bca.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class DownloadReportServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String fileName = req.getParameter("fileName");
		File f = new File(fileName);
		OutputStream out = resp.getOutputStream();
	    resp.setHeader("Content-Disposition", "attachment; filename=\"" + f.getName()+ "\"");
	    resp.setContentType("application/octet-stream");

		@SuppressWarnings("resource")
		FileInputStream fis = new FileInputStream(f);
		byte[] buffer = new byte[1024];
		int len =0;
		while((len=fis.read(buffer))!=-1){
			out.write(buffer,0,len);
		}
		out.close();
		out.flush();
	}

}
