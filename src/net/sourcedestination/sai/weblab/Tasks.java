package net.sourcedestination.sai.weblab;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourcedestination.funcles.tuple.Tuple;
import net.sourcedestination.funcles.tuple.Tuple2;
import net.sourcedestination.sai.reporting.Log;
import net.sourcedestination.sai.task.Task;

/**
 * instantiates resource and plugin classes and manages instances
 *
 * TODO: only basic testing performed -- unit tests and more extensive testing
 * needed
 * 
 * @author jmorwick
 */
@WebServlet(name = "Tasks", urlPatterns = { "/Tasks" })
public class Tasks extends HttpServlet {

	public static Set<Tuple2<String, Supplier<Task>>> getInitiators() {
		Set<Tuple2<String, Supplier<Task>>> acc = Sets.newHashSet();
		for (Map.Entry<String, Object> e : Resources.RESOURCES.entrySet()) {
			try {
				if (e.getValue() instanceof Supplier<?>) {
					if (e.getValue().getClass().getMethod("get")
							.getReturnType().equals(Task.class)) {

						acc.add(Tuple.makeTuple(e.getKey(),
								(Supplier<Task>) e.getValue()));
					}
				}
			} catch (NoSuchMethodException e1) {
			} catch (SecurityException e1) {
			}
		}
		return acc;
	}

	public static Supplier<Task> getInitiator(String name) {
		Set<Tuple2<String, Supplier<Task>>> acc = Sets.newHashSet();
		for (Map.Entry<String, Object> e : Resources.RESOURCES.entrySet()) {
			try {
				if (e.getValue() instanceof Supplier<?>) {
					if (e.getValue().getClass().getMethod("get")
							.getReturnType().equals(Task.class) &&
						e.getKey().equals(name)) {

						return (Supplier<Task>)e.getValue();
					}
				}
			} catch (NoSuchMethodException e1) {
			} catch (SecurityException e1) {
			}
		}
		return null;
	}

	public static final Set<Task> TASKS = Sets.newHashSet();
	public static final Map<Task,Log> FINISHED_TASKS = Maps.newHashMap();
	public static final Map<Task,Exception> CRASHED_TASKS = Maps.newHashMap();

	/**
	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
	 * methods.
	 *
	 * @param request
	 *            servlet request
	 * @param response
	 *            servlet response
	 * @throws ServletException
	 *             if a servlet-specific error occurs
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	protected void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		try (PrintWriter out = response.getWriter()) {

			//
			// validate the request
			//

			// make sure an action was set
			Map<String, String[]> params = request.getParameterMap();
			if (!params.containsKey("action")
					|| params.get("action").length != 1
					|| !Sets.newHashSet("initiate").contains(
							params.get("action")[0])) {
				response.sendError(400, "no proper action indicated");
				return;
			}

			//
			// request is valid, process it
			//
			boolean valid = true;
			String action = params.get("action")[0];
			if (action.equals("initiate")) {
				// use given name if present
				String initiator = params.get("initiator")[0];

				Task t = getInitiator(initiator).get();
				
				// TODO: use executor service instead
				try {
					Thread runner = new Thread() {
						public void run() { try {
							FINISHED_TASKS.put(t, t.call());
						} catch (Exception e) {
							CRASHED_TASKS.put(t, e);
						} }
					};
					runner.start();
				} catch(Exception e) {
					
				}
				
				TASKS.add(t); 		
				response.sendRedirect("tasks.jsp");
			} else if (action.equals("cancel")) {

				// TODO: develop this
				response.sendRedirect(".");
			} else {
				response.sendError(400, "invalid arguments");
			}
		}
	}

	// <editor-fold defaultstate="collapsed"
	// desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
	/**
	 * Handles the HTTP <code>GET</code> method.
	 *
	 * @param request
	 *            servlet request
	 * @param response
	 *            servlet response
	 * @throws ServletException
	 *             if a servlet-specific error occurs
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Handles the HTTP <code>POST</code> method.
	 *
	 * @param request
	 *            servlet request
	 * @param response
	 *            servlet response
	 * @throws ServletException
	 *             if a servlet-specific error occurs
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Returns a short description of the servlet.
	 *
	 * @return a String containing servlet description
	 */
	@Override
	public String getServletInfo() {
		return "manages SAI tasks";
	}// </editor-fold>

}