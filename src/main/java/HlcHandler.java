import db.DataBase;
import exceptions.PathException;
import exceptions.WrongParameterException;
import model.Jsonable;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by tookuk on 8/14/17.
 */
public class HlcHandler extends AbstractHandler {

    private final DataBase db;

    public HlcHandler(DataBase dataBase){
        this.db = dataBase;
    }

    public void handle(String s,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response)
            throws IOException, ServletException {

        String[] pathelements = s.split("/");

        if ("GET".equals(request.getMethod())) {
            processGET(baseRequest, request, response, pathelements);
        } else if ("POST".equals(request.getMethod())){
            processPOST(baseRequest, request, response, pathelements);
        }
    }

    private void processGET(Request baseRequest, HttpServletRequest request, HttpServletResponse response, String[] pathelements) throws IOException {
        Jsonable respString = null;
        try{
            if (pathelements.length > 1) {
                if ("users".equals(pathelements[1])) {
                    if (pathelements.length > 3) {
                        respString = db.getVisitsOfUser(pathelements[2]
                                , request.getParameter("fromDate")
                                , request.getParameter("toDate")
                                , request.getParameter("country")
                                , request.getParameter("toDistance")
                        );
                    } else {
                        respString = db.getUserById(Integer.parseInt(pathelements[2]));
                    }
                } else if ("visits".equals(pathelements[1])) {
                    respString = db.getVisitById(Integer.parseInt(pathelements[2]));
                } else if ("locations".equals(pathelements[1])) {
                    if (pathelements.length > 3) {
                        respString = db.getLocationAvg(pathelements[2]
                                , request.getParameter("fromDate")
                                , request.getParameter("toDate")
                                , request.getParameter("fromAge")
                                , request.getParameter("toAge")
                                , request.getParameter("gender"));
                    } else {
                        respString = db.getLocationById(Integer.parseInt(pathelements[2]));
                    }
                }
            }
        } catch (NumberFormatException e){
            do404(baseRequest, response);
            return;
        } catch (WrongParameterException e){
            do400(baseRequest, response);
            return;
        }

        if(respString == null){
            do404(baseRequest, response);
        } else {
            doOK(baseRequest, response, respString.toJson());
        }
    }

    private void processPOST(Request baseRequest, HttpServletRequest request, HttpServletResponse response, String[] pathelements) throws IOException {
        try {
            BufferedReader reader = request.getReader();
            String source = reader.readLine();
            if(StringUtils.isBlank(source)){
                throw new WrongParameterException();
            }
            if ("users".equals(pathelements[1])) {
                if ("new".equals(pathelements[2])) {
                    db.addUser(FileLoader.parseUser(new JSONObject(source)));
                } else {
                    if (!StringUtils.isNumeric(pathelements[2])) {
                        do404(baseRequest, response);
                        return;
                    }
                    db.updateUser(Integer.parseInt(pathelements[2]), new JSONObject(source));
                }
            } else if ("visits".equals(pathelements[1])) {
                if ("new".equals(pathelements[2])) {
                    db.addVisit(FileLoader.parseVisit(db, new JSONObject(source)));
                } else {
                    if (!StringUtils.isNumeric(pathelements[2])) {
                        do404(baseRequest, response);
                        return;
                    }
                    db.updateVisit(Integer.parseInt(pathelements[2]), new JSONObject(source));
                }
            } else if ("locations".equals(pathelements[1])) {
                if ("new".equals(pathelements[2])) {
                    db.addLocation(FileLoader.parseLocation(new JSONObject(source)));
                } else {
                    if (!StringUtils.isNumeric(pathelements[2])) {
                        do404(baseRequest, response);
                        return;
                    }
                    db.updateLocation(Integer.parseInt(pathelements[2]), new JSONObject(source));
                }
            }
        } catch (WrongParameterException | JSONException e){
            e.printStackTrace();
            do400(baseRequest, response);
            return;
        } catch (PathException e){
            do404(baseRequest, response);
            return;
        }
        doOkPost(baseRequest, response);
    }

    private void doOK(Request baseRequest, HttpServletResponse response, String respString) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        response.getWriter().println(respString);
    }

    private void do400(Request baseRequest, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(400);
        baseRequest.setHandled(true);
        response.getWriter().println("400");
    }

    private void do404(Request baseRequest, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(404);
        baseRequest.setHandled(true);
        response.getWriter().println("404");
    }

    private void doOkPost(Request baseRequest, HttpServletResponse response) throws IOException {
        doOK(baseRequest, response, "{}");
    }
}
